package com.pandora.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pandora.app.core.database.dao.AiConversationDao
import com.pandora.app.core.database.dao.CollectionDao
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.TagDao
import com.pandora.app.core.database.entity.AiConversationEntity
import com.pandora.app.core.database.entity.AiMessageEntity
import com.pandora.app.core.database.entity.CollectionEntity
import com.pandora.app.core.database.entity.CollectionItemCrossRef
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemFolderCrossRef
import com.pandora.app.core.database.entity.ItemTagCrossRef
import com.pandora.app.core.database.entity.TagEntity

import com.pandora.app.core.database.entity.ItemFtsEntity

@Database(
    entities = [
        ItemEntity::class,
        ItemFtsEntity::class,
        FolderEntity::class,
        TagEntity::class,
        CollectionEntity::class,
        ItemFolderCrossRef::class,
        ItemTagCrossRef::class,
        CollectionItemCrossRef::class,
        AiConversationEntity::class,
        AiMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PandoraDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun folderDao(): FolderDao
    abstract fun tagDao(): TagDao
    abstract fun collectionDao(): CollectionDao
    abstract fun aiConversationDao(): AiConversationDao
}
