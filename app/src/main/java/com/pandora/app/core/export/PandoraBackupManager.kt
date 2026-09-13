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
}
