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
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemFolderCrossRef
import com.pandora.app.core.database.entity.ItemTagCrossRef
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow

data class ItemWithRelations(
    @Embedded val item: ItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ItemFolderCrossRef::class,
            parentColumn = "itemId",
            entityColumn = "folderId"
        )
    )
    val folders: List<FolderEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ItemTagCrossRef::class,
            parentColumn = "itemId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity> = emptyList()
)

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)

    @Transaction
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItemsWithRelations(): Flow<List<ItemWithRelations>>

    @Transaction
    @Query("SELECT * FROM items WHERE itemType = :type ORDER BY createdAt DESC")
    fun getItemsByType(type: ItemType): Flow<List<ItemWithRelations>>

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemWithRelationsById(id: Long): Flow<ItemWithRelations?>

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemWithRelationsByIdDirect(id: Long): ItemWithRelations?

    @Query("SELECT COUNT(*) FROM items")
    fun getTotalItemCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemFolderCrossRef(crossRef: ItemFolderCrossRef)

    @Query("DELETE FROM item_folder_cross_ref WHERE itemId = :itemId AND folderId = :folderId")
    suspend fun removeItemFromFolder(itemId: Long, folderId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemTagCrossRef(crossRef: ItemTagCrossRef)

    @Query("DELETE FROM item_tag_cross_ref WHERE itemId = :itemId AND tagId = :tagId")
    suspend fun removeItemTag(itemId: Long, tagId: Long)

    @Transaction
    @Query("""
        SELECT * FROM items 
        WHERE title LIKE '%' || :query || '%' 
           OR excerpt LIKE '%' || :query || '%' 
           OR fullContent LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchItems(query: String): Flow<List<ItemWithRelations>>

    @Transaction
    @Query("""
        SELECT items.* FROM items
        JOIN items_fts ON items.id = items_fts.rowid
        WHERE items_fts MATCH :query
        ORDER BY items.createdAt DESC
    """)
    fun searchItemsFts(query: String): Flow<List<ItemWithRelations>>
}
