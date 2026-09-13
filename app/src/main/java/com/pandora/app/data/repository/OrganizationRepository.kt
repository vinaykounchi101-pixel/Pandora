package com.pandora.app.data.repository

import com.pandora.app.core.database.dao.CollectionDao
import com.pandora.app.core.database.dao.CollectionWithItems
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.FolderWithSubfoldersAndItems
import com.pandora.app.core.database.dao.TagDao
import com.pandora.app.core.database.dao.TagWithItemCount
import com.pandora.app.core.database.entity.CollectionEntity
import com.pandora.app.core.database.entity.CollectionItemCrossRef
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OrganizationRepository {
    fun getRootFolders(): Flow<List<FolderWithSubfoldersAndItems>>
    fun getAllFolders(): Flow<List<FolderEntity>>
    fun getFolderWithItems(id: Long): Flow<FolderWithSubfoldersAndItems?>
    suspend fun createFolder(name: String, parentFolderId: Long? = null, colorToken: String = "primary"): Long
    suspend fun deleteFolder(folder: FolderEntity)

    fun getTagsWithCounts(): Flow<List<TagWithItemCount>>
    fun getAllTags(): Flow<List<TagEntity>>
    suspend fun createTag(name: String, colorToken: String = "primary", isAi: Boolean = false): Long
    suspend fun deleteTag(tag: TagEntity)

    fun getCollections(): Flow<List<CollectionWithItems>>
    suspend fun createCollection(name: String, description: String, iconName: String = "psychology", colorToken: String = "primary"): Long
    suspend fun addItemToCollection(collectionId: Long, itemId: Long)
    suspend fun removeItemFromCollection(collectionId: Long, itemId: Long)
    suspend fun deleteCollection(collection: CollectionEntity)
}

@Singleton
class OrganizationRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao,
    private val tagDao: TagDao,
    private val collectionDao: CollectionDao
) : OrganizationRepository {

    override fun getRootFolders(): Flow<List<FolderWithSubfoldersAndItems>> =
        folderDao.getRootFoldersWithSubfoldersAndItems()

    override fun getAllFolders(): Flow<List<FolderEntity>> =
        folderDao.getAllFolders()

    override fun getFolderWithItems(id: Long): Flow<FolderWithSubfoldersAndItems?> =
        folderDao.getFolderWithItemsById(id)

    override suspend fun createFolder(name: String, parentFolderId: Long?, colorToken: String): Long =
        folderDao.insertFolder(FolderEntity(name = name, parentFolderId = parentFolderId, colorToken = colorToken))

    override suspend fun deleteFolder(folder: FolderEntity) =
        folderDao.deleteFolder(folder)

    override fun getTagsWithCounts(): Flow<List<TagWithItemCount>> =
        tagDao.getTagsWithItemCounts()

    override fun getAllTags(): Flow<List<TagEntity>> =
        tagDao.getAllTags()

    override suspend fun createTag(name: String, colorToken: String, isAi: Boolean): Long =
        tagDao.insertTag(TagEntity(name = name, colorToken = colorToken, isAiGenerated = isAi))

    override suspend fun deleteTag(tag: TagEntity) =
        tagDao.deleteTag(tag)

    override fun getCollections(): Flow<List<CollectionWithItems>> =
        collectionDao.getAllCollectionsWithItems()

    override suspend fun createCollection(
        name: String,
        description: String,
        iconName: String,
        colorToken: String
    ): Long =
        collectionDao.insertCollection(
            CollectionEntity(name = name, description = description, iconName = iconName, colorToken = colorToken)
        )

    override suspend fun addItemToCollection(collectionId: Long, itemId: Long) =
        collectionDao.insertCollectionItemCrossRef(CollectionItemCrossRef(collectionId, itemId))

    override suspend fun removeItemFromCollection(collectionId: Long, itemId: Long) =
        collectionDao.removeItemFromCollection(collectionId, itemId)

    override suspend fun deleteCollection(collection: CollectionEntity) =
        collectionDao.deleteCollection(collection)
}
