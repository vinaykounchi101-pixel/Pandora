package com.pandora.app.core.export

import android.content.Context
import android.net.Uri
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.TagDao
import com.pandora.app.core.storage.VaultStorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class BackupStats(
    val itemCount: Int,
    val folderCount: Int,
    val tagCount: Int,
    val totalSizeBytes: Long
)

@Singleton
class PandoraBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val itemDao: ItemDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao,
    private val vaultStorageManager: VaultStorageManager
) {
    suspend fun exportToUri(destinationUri: Uri): Result<BackupStats> = withContext(Dispatchers.IO) {
        try {
            val items = itemDao.getAllItemsWithRelations().first()
            val folders = folderDao.getAllFolders().first()
            val tags = tagDao.getAllTags().first()

            // Build manifest metadata JSON
            val manifestJson = JSONObject().apply {
                put("version", "1.0")
                put("app", "Pandora")
                put("exportTimestamp", System.currentTimeMillis())
                put("itemCount", items.size)
                put("folderCount", folders.size)
                put("tagCount", tags.size)

                val foldersArray = JSONArray()
                folders.forEach { f ->
                    foldersArray.put(JSONObject().apply {
                        put("id", f.id)
                        put("name", f.name)
                        put("parentFolderId", f.parentFolderId ?: JSONObject.NULL)
                        put("colorToken", f.colorToken)
                    })
                }
                put("folders", foldersArray)

                val tagsArray = JSONArray()
                tags.forEach { t ->
                    tagsArray.put(JSONObject().apply {
                        put("id", t.id)
                        put("name", t.name)
                        put("colorToken", t.colorToken)
                    })
                }
                put("tags", tagsArray)

                val itemsArray = JSONArray()
                items.forEach { rel ->
                    val it = rel.item
                    itemsArray.put(JSONObject().apply {
                        put("id", it.id)
                        put("title", it.title)
                        put("itemType", it.itemType.name)
                        put("sourceUrl", it.sourceUrl ?: JSONObject.NULL)
                        put("excerpt", it.excerpt)
                        put("fullContent", it.fullContent)
                        put("localFilePath", it.localFilePath ?: JSONObject.NULL)
                        put("readingTimeMinutes", it.readingTimeMinutes)
                        put("isFavorite", it.isFavorite)
                        put("createdAt", it.createdAt)
                        put("folders", JSONArray(rel.folders.map { it.id }))
                        put("tags", JSONArray(rel.tags.map { it.name }))
                    })
                }
                put("items", itemsArray)
            }

            // Write into ZipOutputStream via ContentResolver
            val outputStream = context.contentResolver.openOutputStream(destinationUri)
                ?: return@withContext Result.failure(Exception("Cannot open destination stream"))

            var totalBytes = 0L
            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // 1. Write manifest.json
                val manifestBytes = manifestJson.toString(2).toByteArray(Charsets.UTF_8)
                val manifestEntry = ZipEntry("manifest.json")
                zipOut.putNextEntry(manifestEntry)
                zipOut.write(manifestBytes)
                zipOut.closeEntry()
                totalBytes += manifestBytes.size

                // 2. Write vault files
                val vaultDir: File = vaultStorageManager.vaultDirectory
                if (vaultDir.exists() && vaultDir.isDirectory) {
                    val files = vaultDir.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (file.isFile) {
                                val entry = ZipEntry("vault/${file.name}")
                                zipOut.putNextEntry(entry)
                                FileInputStream(file).use { input ->
                                    val buffer = ByteArray(8192)
                                    var read = input.read(buffer)
                                    while (read != -1) {
                                        zipOut.write(buffer, 0, read)
                                        totalBytes += read
                                        read = input.read(buffer)
                                    }
                                }
                                zipOut.closeEntry()
                            }
                        }
                    }
                }
            }

            Result.success(
                BackupStats(
                    itemCount = items.size,
                    folderCount = folders.size,
                    tagCount = tags.size,
                    totalSizeBytes = totalBytes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStorageStats(): BackupStats = withContext(Dispatchers.IO) {
        val items = itemDao.getAllItemsWithRelations().first()
        val folders = folderDao.getAllFolders().first()
        val tags = tagDao.getAllTags().first()

        val vaultDir: File = vaultStorageManager.vaultDirectory
        val vaultSize = if (vaultDir.exists()) {
            vaultDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } else 0L

        BackupStats(
            itemCount = items.size,
            folderCount = folders.size,
            tagCount = tags.size,
            totalSizeBytes = vaultSize
        )
    }

    suspend fun restoreFromUri(sourceUri: Uri): Result<BackupStats> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
                ?: return@withContext Result.failure(Exception("Cannot open source backup stream"))

            var manifestJson: JSONObject? = null
            val tempVaultFiles = mutableListOf<Pair<String, ByteArray>>()

            java.util.zip.ZipInputStream(java.io.BufferedInputStream(inputStream)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "manifest.json") {
                        val content = zipIn.bufferedReader(Charsets.UTF_8).readText()
                        manifestJson = JSONObject(content)
                    } else if (entry.name.startsWith("vault/")) {
                        val fileName = entry.name.removePrefix("vault/")
                        val bytes = zipIn.readBytes()
                        tempVaultFiles.add(fileName to bytes)
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (manifestJson == null) {
                return@withContext Result.failure(Exception("Invalid backup archive: missing manifest.json"))
            }

            val manifest = manifestJson!!

            // 1. Restore Folders (FK independent)
            val foldersArray = manifest.optJSONArray("folders") ?: JSONArray()
            for (i in 0 until foldersArray.length()) {
                val obj = foldersArray.getJSONObject(i)
                val id = obj.getLong("id")
                val name = obj.getString("name")
                val parentId = if (obj.isNull("parentFolderId")) null else obj.getLong("parentFolderId")
                val color = obj.optString("colorToken", "default")
                folderDao.insertFolder(com.pandora.app.core.database.entity.FolderEntity(
                    id = id,
                    name = name,
                    parentFolderId = parentId,
                    colorToken = color
                ))
            }

            // 2. Restore Tags (FK independent)
            val tagsArray = manifest.optJSONArray("tags") ?: JSONArray()
            val tagIdMap = mutableMapOf<String, Long>()
            for (i in 0 until tagsArray.length()) {
                val obj = tagsArray.getJSONObject(i)
                val name = obj.getString("name")
                val color = obj.optString("colorToken", "slate")
                val tagId = tagDao.insertTag(com.pandora.app.core.database.entity.TagEntity(
                    name = name,
                    colorToken = color
                ))
                tagIdMap[name] = tagId
            }

            // 3. Restore Items & Cross-references
            val itemsArray = manifest.optJSONArray("items") ?: JSONArray()
            for (i in 0 until itemsArray.length()) {
                val obj = itemsArray.getJSONObject(i)
                val id = obj.getLong("id")
                val title = obj.getString("title")
                val itemTypeStr = obj.optString("itemType", "NOTE")
                val itemType = try {
                    com.pandora.app.core.database.entity.ItemType.valueOf(itemTypeStr)
                } catch (_: Exception) {
                    com.pandora.app.core.database.entity.ItemType.NOTE
                }
                val sourceUrl = if (obj.isNull("sourceUrl")) null else obj.getString("sourceUrl")
                val excerpt = obj.optString("excerpt", "")
                val fullContent = obj.optString("fullContent", "")
                val localFilePath = if (obj.isNull("localFilePath")) null else obj.getString("localFilePath")
                val readingTime = obj.optInt("readingTimeMinutes", 1)
                val isFavorite = obj.optBoolean("isFavorite", false)
                val createdAt = obj.optLong("createdAt", System.currentTimeMillis())

                val savedItemId = itemDao.insertItem(com.pandora.app.core.database.entity.ItemEntity(
                    id = id,
                    itemType = itemType,
                    title = title,
                    sourceUrl = sourceUrl,
                    excerpt = excerpt,
                    fullContent = fullContent,
                    localFilePath = localFilePath,
                    readingTimeMinutes = readingTime,
                    isFavorite = isFavorite,
                    createdAt = createdAt
                ))

                // Restore Folder cross refs
                val itemFolders = obj.optJSONArray("folders") ?: JSONArray()
                for (f in 0 until itemFolders.length()) {
                    val folderId = itemFolders.getLong(f)
                    itemDao.insertItemFolderCrossRef(com.pandora.app.core.database.entity.ItemFolderCrossRef(
                        itemId = savedItemId,
                        folderId = folderId
                    ))
                }

                // Restore Tag cross refs
                val itemTags = obj.optJSONArray("tags") ?: JSONArray()
                for (t in 0 until itemTags.length()) {
                    val tagName = itemTags.getString(t)
                    val tagId = tagIdMap[tagName] ?: tagDao.getTagIdByName(tagName)
                    if (tagId != null && tagId > 0) {
                        itemDao.insertItemTagCrossRef(com.pandora.app.core.database.entity.ItemTagCrossRef(
                            itemId = savedItemId,
                            tagId = tagId
                        ))
                    }
                }
            }

            // 4. Restore Vault files to disk
            val vaultDir = vaultStorageManager.vaultDirectory
            if (!vaultDir.exists()) vaultDir.mkdirs()
            tempVaultFiles.forEach { (name, bytes) ->
                val destFile = File(vaultDir, name)
                destFile.writeBytes(bytes)
            }

            Result.success(
                BackupStats(
                    itemCount = itemsArray.length(),
                    folderCount = foldersArray.length(),
                    tagCount = tagsArray.length(),
                    totalSizeBytes = tempVaultFiles.sumOf { it.second.size.toLong() }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
