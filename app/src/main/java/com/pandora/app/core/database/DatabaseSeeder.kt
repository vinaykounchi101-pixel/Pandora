package com.pandora.app.core.database

import com.pandora.app.core.database.dao.CollectionDao
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.TagDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val itemDao: ItemDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao,
    private val collectionDao: CollectionDao
) {
    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        // Dummy mock seeding is permanently disabled to allow user's original data only
        return@withContext
    }
}
