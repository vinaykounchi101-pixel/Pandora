package com.pandora.app.core.database

import com.pandora.app.core.database.dao.CollectionDao
import com.pandora.app.core.database.dao.FolderDao
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.TagDao
import com.pandora.app.core.database.entity.CollectionEntity
import com.pandora.app.core.database.entity.CollectionItemCrossRef
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemFolderCrossRef
import com.pandora.app.core.database.entity.ItemTagCrossRef
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.database.entity.TagEntity
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
        // If folders already exist, skip seeding
        val existing = folderDao.getAllFolders()
        // We do a one-time check
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 3600 * 1000
        val twoHoursAgo = now - 7200 * 1000
        val yesterday = now - 86400 * 1000
        val lastWeek = now - 86400 * 1000 * 5

        // 1. Seed Folders
        val fDesign = folderDao.insertFolder(FolderEntity(name = "Design Systems & UI", colorToken = "primary"))
        val fMobile = folderDao.insertFolder(FolderEntity(name = "Mobile Guidelines", parentFolderId = fDesign, colorToken = "primary"))
        val fTokens = folderDao.insertFolder(FolderEntity(name = "Tokens & Specs", parentFolderId = fDesign, colorToken = "primary"))

        val fTech = folderDao.insertFolder(FolderEntity(name = "Technical Architecture", colorToken = "primary"))
        val fDistributed = folderDao.insertFolder(FolderEntity(name = "Distributed Systems", parentFolderId = fTech, colorToken = "tertiary"))

        val fBooks = folderDao.insertFolder(FolderEntity(name = "Book Notes & Summaries", colorToken = "secondary"))
        val fInspiration = folderDao.insertFolder(FolderEntity(name = "Daily Inspiration", colorToken = "secondary"))

        // 2. Seed Collections
        val cAi = collectionDao.insertCollection(
            CollectionEntity(name = "AI & HCI Research", description = "Affordances, agent UX, mental models", iconName = "psychology", colorToken = "primary")
        )
        val cHardware = collectionDao.insertCollection(
            CollectionEntity(name = "Hardware Prototypes", description = "E-ink readers, mechanical switches", iconName = "devices", colorToken = "secondary")
        )
        val cStrategy = collectionDao.insertCollection(
            CollectionEntity(name = "Product Strategy 2025", description = "Roadmaps, positioning, quarterly OKRs", iconName = "insights", colorToken = "tertiary")
        )

        // 3. Seed Tags
        val tSystems = tagDao.insertTag(TagEntity(name = "systems", colorToken = "primary"))
        val tArch = tagDao.insertTag(TagEntity(name = "architecture", colorToken = "tertiary"))
        val tCloud = tagDao.insertTag(TagEntity(name = "cloud", colorToken = "secondary"))
        val tDesignOps = tagDao.insertTag(TagEntity(name = "designops", colorToken = "primary"))
        val tHeuristics = tagDao.insertTag(TagEntity(name = "heuristics", colorToken = "primary"))
        val tMl = tagDao.insertTag(TagEntity(name = "machine-learning", colorToken = "primary", isAiGenerated = true))

        // 4. Seed Artifact Items
        // Artifact 1: Diagram (Hero)
        val itemDiagram = ItemEntity(
            itemType = ItemType.IMAGE,
            title = "Event-Driven Topology Blueprint",
            excerpt = "Microservices, queues, and event stream pipelines across multi-region clusters.",
            capturedFromApp = "Chrome Capture",
            createdAt = twoHoursAgo
        )
        val idDiagram = itemDao.insertItem(itemDiagram)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idDiagram, folderId = fTech))
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = idDiagram, tagId = tCloud))
        collectionDao.insertCollectionItemCrossRef(CollectionItemCrossRef(collectionId = cAi, itemId = idDiagram))

        // Artifact 2: Web Article (Principles of Resilient System Design)
        val itemArticle = ItemEntity(
            itemType = ItemType.ARTICLE,
            title = "Principles of Resilient System Design",
            excerpt = "Examining failure-domain isolation, graceful degradation pipelines, and autonomous circuit-breaker strategies across multi-region clusters.",
            sourceUrl = "https://distributedsystems.io/resilience",
            domain = "distributedsystems.io",
            readingTimeMinutes = 6,
            isAutoSummarized = true,
            createdAt = oneHourAgo
        )
        val idArticle = itemDao.insertItem(itemArticle)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idArticle, folderId = fDistributed))
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = idArticle, tagId = tSystems))
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = idArticle, tagId = tArch))
        collectionDao.insertCollectionItemCrossRef(CollectionItemCrossRef(collectionId = cAi, itemId = idArticle))

        // Artifact 3: Personal Note Tile
        val itemNote = ItemEntity(
            itemType = ItemType.NOTE,
            title = "Quiet Architecture",
            fullContent = "“Good architecture feels quiet. You barely notice it until under extraordinary strain.”",
            createdAt = oneHourAgo - 1800 * 1000
        )
        val idNote = itemDao.insertItem(itemNote)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idNote, folderId = fDesign))
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = idNote, tagId = tDesignOps))

        // Artifact 4: Thought / Quote Reflection (Yesterday)
        val itemThought = ItemEntity(
            itemType = ItemType.NOTE,
            title = "Cognitive Load in UI Design",
            excerpt = "“Simplicity isn't the lack of clutter, that's just a consequence of simplicity. Simplicity is somehow essentially describing the purpose and place of an object and everything that it is.”",
            fullContent = "“Simplicity isn't the lack of clutter, that's just a consequence of simplicity. Simplicity is somehow essentially describing the purpose and place of an object and everything that it is.”",
            createdAt = yesterday
        )
        val idThought = itemDao.insertItem(itemThought)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idThought, folderId = fDesign))
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idThought, folderId = fBooks))
        itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId = idThought, tagId = tDesignOps))

        // Artifact 5: PDF Tile (Yesterday)
        val itemPdf = ItemEntity(
            itemType = ItemType.DOCUMENT,
            title = "Financial Summary 2024.pdf",
            mimeType = "application/pdf",
            fileSizeBytes = 2400000L,
            createdAt = yesterday - 3600 * 1000
        )
        val idPdf = itemDao.insertItem(itemPdf)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idPdf, folderId = fTech))

        // Artifact 6: Voice Memo Tile (Yesterday)
        val itemVoice = ItemEntity(
            itemType = ItemType.VOICE,
            title = "Microservices Latency Voice Memo",
            excerpt = "Audio transcript ready: Discussed p99 latency guarantees and connection pools.",
            durationSeconds = 102,
            createdAt = yesterday - 7200 * 1000
        )
        val idVoice = itemDao.insertItem(itemVoice)
        itemDao.insertItemFolderCrossRef(ItemFolderCrossRef(itemId = idVoice, folderId = fDistributed))
    }
}
