package com.pandora.app.data.repository

import android.net.Uri
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemFolderCrossRef
import com.pandora.app.core.database.entity.ItemTagCrossRef
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.storage.VaultStorageManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ItemRepository {
    fun getAllItems(): Flow<List<ItemWithRelations>>
    fun getItemsByType(type: ItemType): Flow<List<ItemWithRelations>>
    fun getItemById(id: Long): Flow<ItemWithRelations?>
    fun getTotalItemCount(): Flow<Int>
    fun searchItems(query: String): Flow<List<ItemWithRelations>>
    suspend fun saveItem(item: ItemEntity, folderIds: List<Long> = emptyList(), tagIds: List<Long> = emptyList()): Long
    suspend fun updateItem(item: ItemEntity)
    suspend fun deleteItem(item: ItemEntity)
    suspend fun importMedia(uri: Uri, title: String, itemType: ItemType): Long
    suspend fun assignFolder(itemId: Long, folderId: Long)
    suspend fun removeFolder(itemId: Long, folderId: Long)
    suspend fun assignTag(itemId: Long, tagId: Long)
    suspend fun removeTag(itemId: Long, tagId: Long)
    suspend fun findItemByUrl(url: String): ItemEntity?
    suspend fun findItemByExactTitle(title: String): ItemEntity?
}

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val vaultStorageManager: VaultStorageManager
) : ItemRepository {

    override fun getAllItems(): Flow<List<ItemWithRelations>> =
        itemDao.getAllItemsWithRelations()

    override fun getItemsByType(type: ItemType): Flow<List<ItemWithRelations>> =
        itemDao.getItemsByType(type)

    override fun getItemById(id: Long): Flow<ItemWithRelations?> =
        itemDao.getItemWithRelationsById(id)

    override fun getTotalItemCount(): Flow<Int> =
        itemDao.getTotalItemCount()

    override fun searchItems(query: String): Flow<List<ItemWithRelations>> =
        itemDao.searchItems(query)

    override suspend fun saveItem(
        item: ItemEntity,
        folderIds: List<Long>,
        tagIds: List<Long>
    ): Long {
        val itemId = itemDao.insertItem(item)
        folderIds.forEach { folderId ->
            itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = itemId, folderId = folderId))
        }
        tagIds.forEach { tagId ->
            itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = itemId, tagId = tagId))
        }
        return itemId
    }

    override suspend fun updateItem(item: ItemEntity) {
        itemDao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteItem(item: ItemEntity) {
        item.localFilePath?.let { path ->
            vaultStorageManager.deleteVaultFile(path)
        }
        itemDao.deleteItem(item)
    }

    override suspend fun importMedia(uri: Uri, title: String, itemType: ItemType): Long {
        val storageResult = vaultStorageManager.copyUriToVault(uri)
        val localPath = storageResult?.first
        val fileSize = storageResult?.second ?: 0L

        val item = ItemEntity(
            itemType = itemType,
            title = title,
            localFilePath = localPath,
            fileSizeBytes = fileSize,
            createdAt = System.currentTimeMillis()
        )
        return itemDao.insertItem(item)
    }

    override suspend fun assignFolder(itemId: Long, folderId: Long) {
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = itemId, folderId = folderId))
    }

    override suspend fun removeFolder(itemId: Long, folderId: Long) {
        itemDao.removeItemFromFolder(itemId, folderId)
    }

    override suspend fun assignTag(itemId: Long, tagId: Long) {
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = itemId, tagId = tagId))
    }

    override suspend fun removeTag(itemId: Long, tagId: Long) {
        itemDao.removeItemTag(itemId, tagId)
    }

    override suspend fun findItemByUrl(url: String): ItemEntity? {
        if (url.isBlank()) return null
        return itemDao.findItemByUrl(url.trim())
    }

    override suspend fun findItemByExactTitle(title: String): ItemEntity? {
        if (title.isBlank()) return null
        return itemDao.findItemByExactTitle(title.trim())
    }
}
