package com.pandora.app.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.pandora.app.core.database.entity.AiConversationEntity
import com.pandora.app.core.database.entity.AiMessageEntity
import com.pandora.app.core.database.entity.CollectionEntity
import com.pandora.app.core.database.entity.CollectionItemCrossRef
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemFolderCrossRef
import com.pandora.app.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow

data class FolderWithSubfoldersAndItems(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentFolderId"
    )
    val subfolders: List<FolderEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ItemFolderCrossRef::class,
            parentColumn = "folderId",
            entityColumn = "itemId"
        )
    )
    val items: List<ItemEntity> = emptyList()
)

data class CollectionWithItems(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CollectionItemCrossRef::class,
            parentColumn = "collectionId",
            entityColumn = "itemId"
        )
    )
    val items: List<ItemEntity> = emptyList()
)

data class TagWithItemCount(
    @Embedded val tag: TagEntity,
    val itemCount: Int
)

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Transaction
    @Query("SELECT * FROM folders WHERE parentFolderId IS NULL ORDER BY sortOrder ASC, name ASC")
    fun getRootFoldersWithSubfoldersAndItems(): Flow<List<FolderWithSubfoldersAndItems>>

    @Transaction
    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Transaction
    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderWithItemsById(folderId: Long): Flow<FolderWithSubfoldersAndItems?>
}

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("""
        SELECT tags.*, COUNT(item_tag_cross_ref.itemId) as itemCount 
        FROM tags 
        LEFT JOIN item_tag_cross_ref ON tags.id = item_tag_cross_ref.tagId 
        GROUP BY tags.id 
        ORDER BY itemCount DESC, tags.name ASC
    """)
    fun getTagsWithItemCounts(): Flow<List<TagWithItemCount>>

    @Delete
    suspend fun deleteTag(tag: TagEntity)
}

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Transaction
    @Query("SELECT * FROM collections ORDER BY sortOrder ASC, updatedAt DESC")
    fun getAllCollectionsWithItems(): Flow<List<CollectionWithItems>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollectionItemCrossRef(crossRef: CollectionItemCrossRef)

    @Query("DELETE FROM collection_item_cross_ref WHERE collectionId = :collectionId AND itemId = :itemId")
    suspend fun removeItemFromCollection(collectionId: Long, itemId: Long)
}

@Dao
interface AiConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: AiConversationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiMessageEntity): Long

    @Query("SELECT * FROM ai_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<AiMessageEntity>>

    @Query("SELECT * FROM ai_conversations WHERE targetItemId = :itemId ORDER BY updatedAt DESC")
    fun getConversationsForItem(itemId: Long): Flow<List<AiConversationEntity>>

    @Query("SELECT * FROM ai_conversations WHERE targetCollectionId = :collectionId ORDER BY updatedAt DESC")
    fun getConversationsForCollection(collectionId: Long): Flow<List<AiConversationEntity>>
}
