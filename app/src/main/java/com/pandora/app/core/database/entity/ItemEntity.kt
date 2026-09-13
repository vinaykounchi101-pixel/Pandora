package com.pandora.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ItemType {
    ARTICLE,
    IMAGE,
    NOTE,
    DOCUMENT,
    VOICE
}

@Entity(
    tableName = "items",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["itemType"]),
        Index(value = ["isFavorite"])
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemType: ItemType,
    val title: String,
    val excerpt: String = "",
    val fullContent: String = "",
    val sourceUrl: String? = null,
    val domain: String? = null,
    val localFilePath: String? = null,
    val mimeType: String? = null,
    val readingTimeMinutes: Int = 0,
    val durationSeconds: Int = 0,
    val fileSizeBytes: Long = 0,
    val isFavorite: Boolean = false,
    val isAutoSummarized: Boolean = false,
    val capturedFromApp: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
