package com.pandora.app.di

import android.content.Context
import androidx.room.Room
import com.pandora.app.core.database.PandoraDatabase
import com.pandora.app.core.database.dao.AiConversationDao
import com.pandora.app.core.database.dao.CollectionDao
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePandoraDatabase(
        @ApplicationContext context: Context
    ): PandoraDatabase {
        return Room.databaseBuilder(
            context,
            PandoraDatabase::class.java,
            "pandora_vault.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideItemDao(db: PandoraDatabase): ItemDao = db.itemDao()

    @Provides
    fun provideFolderDao(db: PandoraDatabase): FolderDao = db.folderDao()

    @Provides
    fun provideTagDao(db: PandoraDatabase): TagDao = db.tagDao()

    @Provides
    fun provideCollectionDao(db: PandoraDatabase): CollectionDao = db.collectionDao()

    @Provides
    fun provideAiConversationDao(db: PandoraDatabase): AiConversationDao = db.aiConversationDao()
}
