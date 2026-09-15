package com.pandora.app.feature.ai

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.ai.GeminiClient
import com.pandora.app.core.database.dao.CollectionWithItems
import com.pandora.app.core.database.dao.FolderWithSubfoldersAndItems
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.storage.VaultStorageManager
import com.pandora.app.data.repository.ItemRepository
import com.pandora.app.data.repository.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AiScopeTab {
    ARTIFACTS,
    FOLDERS,
    COLLECTIONS
}

sealed class AiTarget {
    data class ItemTarget(val itemWithRelations: ItemWithRelations) : AiTarget()
    data class FolderTarget(val folderWithItems: FolderWithSubfoldersAndItems) : AiTarget()
    data class CollectionTarget(val collectionWithItems: CollectionWithItems) : AiTarget()

    val title: String
        get() = when (this) {
            is ItemTarget -> itemWithRelations.item.title
            is FolderTarget -> "Folder: ${folderWithItems.folder.name}"
            is CollectionTarget -> "Collection: ${collectionWithItems.collection.name}"
        }

    val typeLabel: String
        get() = when (this) {
            is ItemTarget -> when (itemWithRelations.item.itemType) {
                ItemType.NOTE -> "NOTE"
                ItemType.IMAGE -> "PHOTO"
                ItemType.DOCUMENT -> "PDF"
                ItemType.ARTICLE -> "LINK"
                ItemType.VOICE -> "VOICE"
            }
            is FolderTarget -> "FOLDER"
            is CollectionTarget -> "COLLECTION"
        }

    val contextContent: String
        get() = when (this) {
            is ItemTarget -> {
                val item = itemWithRelations.item
                item.fullContent.ifBlank { item.excerpt.ifBlank { item.title } }
            }
            is FolderTarget -> {
                val folder = folderWithItems.folder
                val items = folderWithItems.items
                buildString {
                    appendLine("Folder: ${folder.name}")
                    appendLine("Total Items in Folder: ${items.size}")
                    if (items.isEmpty()) {
                        appendLine("This folder is currently empty.")
                    } else {
                        items.forEachIndexed { index, item ->
                            appendLine("\n--- Item ${index + 1}: ${item.title} (${item.itemType}) ---")
                            val content = item.fullContent.ifBlank { item.excerpt }
                            if (content.isNotBlank()) appendLine(content)
                        }
                    }
                }
            }
            is CollectionTarget -> {
                val collection = collectionWithItems.collection
                val items = collectionWithItems.items
                buildString {
                    appendLine("Collection: ${collection.name}")
                    if (collection.description.isNotBlank()) {
                        appendLine("Description: ${collection.description}")
                    }
                    appendLine("Total Items in Collection: ${items.size}")
                    if (items.isEmpty()) {
                        appendLine("This collection is currently empty.")
                    } else {
                        items.forEachIndexed { index, item ->
                            appendLine("\n--- Item ${index + 1}: ${item.title} (${item.itemType}) ---")
                            val content = item.fullContent.ifBlank { item.excerpt }
                            if (content.isNotBlank()) appendLine(content)
                        }
                    }
                }
            }
        }
}

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PandoraAiUiState(
    val activeScopeTab: AiScopeTab = AiScopeTab.ARTIFACTS,
    val items: List<ItemWithRelations> = emptyList(),
    val folders: List<FolderWithSubfoldersAndItems> = emptyList(),
    val collections: List<CollectionWithItems> = emptyList(),
    val selectedTarget: AiTarget? = null,
    val activeTypeFilter: ItemType? = null,
    val summary: String? = null,
    val isGeneratingSummary: Boolean = false,
    val chatMessages: List<AiChatMessage> = emptyList(),
    val isSendingMessage: Boolean = false,
    val hasApiKey: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class PandoraAiViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val organizationRepository: OrganizationRepository,
    private val geminiClient: GeminiClient,
    private val vaultStorageManager: VaultStorageManager
) : ViewModel() {

    private data class AiInternalState(
        val activeScopeTab: AiScopeTab = AiScopeTab.ARTIFACTS,
        val selectedTarget: AiTarget? = null,
        val activeTypeFilter: ItemType? = null,
        val summary: String? = null,
        val isGeneratingSummary: Boolean = false,
        val chatMessages: List<AiChatMessage> = emptyList(),
        val isSendingMessage: Boolean = false,
        val errorMessage: String? = null
    )

    private val _aiState = MutableStateFlow(AiInternalState())

    val uiState: StateFlow<PandoraAiUiState> = combine(
        itemRepository.getAllItems(),
        organizationRepository.getRootFolders(),
        organizationRepository.getCollections(),
        _aiState
    ) { items, folders, collections, ai ->
        val filteredItems = if (ai.activeTypeFilter == null) {
            items
        } else {
            items.filter { it.item.itemType == ai.activeTypeFilter }
        }

        PandoraAiUiState(
            activeScopeTab = ai.activeScopeTab,
            items = filteredItems,
            folders = folders,
            collections = collections,
            selectedTarget = ai.selectedTarget,
            activeTypeFilter = ai.activeTypeFilter,
            summary = ai.summary,
            isGeneratingSummary = ai.isGeneratingSummary,
            chatMessages = ai.chatMessages,
            isSendingMessage = ai.isSendingMessage,
            hasApiKey = geminiClient.hasApiKey(),
            errorMessage = ai.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PandoraAiUiState()
    )

    fun setScopeTab(tab: AiScopeTab) {
        _aiState.value = _aiState.value.copy(activeScopeTab = tab)
    }

    fun setTypeFilter(filter: ItemType?) {
        _aiState.value = _aiState.value.copy(activeTypeFilter = filter)
    }

    fun selectTarget(target: AiTarget) {
        _aiState.value = _aiState.value.copy(
            selectedTarget = target,
            summary = null,
            chatMessages = emptyList()
        )
        generateSummaryForTarget(target)
    }

    fun selectItem(item: ItemWithRelations) {
        selectTarget(AiTarget.ItemTarget(item))
    }

    fun selectFolder(folder: FolderWithSubfoldersAndItems) {
        selectTarget(AiTarget.FolderTarget(folder))
    }

    fun selectCollection(collection: CollectionWithItems) {
        selectTarget(AiTarget.CollectionTarget(collection))
    }

    fun clearSelection() {
        _aiState.value = _aiState.value.copy(
            selectedTarget = null,
            summary = null,
            chatMessages = emptyList()
        )
    }

    private fun generateSummaryForTarget(target: AiTarget) {
        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isGeneratingSummary = true)
            val typeStr = target.typeLabel
            val contentToSummarize = target.contextContent
            val result = geminiClient.generateItemSummary(target.title, contentToSummarize, typeStr)

            result.onSuccess { summaryText ->
                _aiState.value = _aiState.value.copy(
                    summary = summaryText,
                    isGeneratingSummary = false
                )
            }.onFailure { _ ->
                _aiState.value = _aiState.value.copy(
                    summary = "📌 **${target.title}**\n\n${contentToSummarize.take(350)}",
                    isGeneratingSummary = false
                )
            }
        }
    }

    fun sendMessage(question: String) {
        val selected = _aiState.value.selectedTarget ?: return
        if (question.isBlank()) return

        val userMsg = AiChatMessage(isUser = true, text = question)
        _aiState.value = _aiState.value.copy(
            chatMessages = _aiState.value.chatMessages + userMsg
        )

        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isSendingMessage = true)
            val history = _aiState.value.chatMessages.dropLast(1).map {
                (if (it.isUser) "user" else "model") to it.text
            }
            val content = selected.contextContent
            val result = geminiClient.sendScopedChatMessage(selected.title, content, history, question)

            result.onSuccess { replyText ->
                val modelMsg = AiChatMessage(isUser = false, text = replyText)
                _aiState.value = _aiState.value.copy(
                    chatMessages = _aiState.value.chatMessages + modelMsg,
                    isSendingMessage = false
                )
            }.onFailure { err ->
                val errorMsg = AiChatMessage(
                    isUser = false,
                    text = "I encountered an issue generating a response: ${err.localizedMessage ?: "Unknown error"}. Make sure your Gemini API key is configured in Settings."
                )
                _aiState.value = _aiState.value.copy(
                    chatMessages = _aiState.value.chatMessages + errorMsg,
                    isSendingMessage = false
                )
            }
        }
    }

    fun importAndAnalyzeLink(url: String, title: String = "") {
        if (url.isBlank()) return
        viewModelScope.launch {
            val finalTitle = title.ifBlank {
                url.removePrefix("https://").removePrefix("http://").take(40)
            }
            val item = ItemEntity(
                itemType = ItemType.ARTICLE,
                title = finalTitle,
                sourceUrl = url,
                excerpt = "Web Link: $url",
                fullContent = "URL: $url\n\nArticle title: $finalTitle\nCaptured link saved in Pandora vault for AI analysis and research.",
                createdAt = System.currentTimeMillis()
            )
            val id = itemRepository.saveItem(item)
            val savedItem = ItemWithRelations(item = item.copy(id = id))
            selectItem(savedItem)
        }
    }

    fun importAndAnalyzeMedia(uri: Uri) {
        viewModelScope.launch {
            val saved = vaultStorageManager.copyUriToVault(uri)
            val item = ItemEntity(
                itemType = ItemType.IMAGE,
                title = "Imported Photo",
                localFilePath = saved?.first,
                fileSizeBytes = saved?.second ?: 0L,
                excerpt = "Captured photo / screenshot analyzed with Pandora AI",
                fullContent = "Media asset securely stored in offline vault.",
                createdAt = System.currentTimeMillis()
            )
            val id = itemRepository.saveItem(item)
            val savedItem = ItemWithRelations(item = item.copy(id = id))
            selectItem(savedItem)
        }
    }

    fun importAndAnalyzeDocument(uri: Uri, fileName: String) {
        viewModelScope.launch {
            val saved = vaultStorageManager.copyUriToVault(uri)
            val item = ItemEntity(
                itemType = ItemType.DOCUMENT,
                title = fileName.ifBlank { "Imported Document" },
                localFilePath = saved?.first,
                fileSizeBytes = saved?.second ?: 0L,
                excerpt = "Document analyzed with Pandora AI",
                fullContent = "Document: $fileName stored in offline vault.",
                createdAt = System.currentTimeMillis()
            )
            val id = itemRepository.saveItem(item)
            val savedItem = ItemWithRelations(item = item.copy(id = id))
            selectItem(savedItem)
        }
    }
}

