package com.pandora.app.feature.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.TagEntity
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

data class ItemDetailUiState(
    val itemWithRelations: ItemWithRelations? = null,
    val relatedItems: List<ItemWithRelations> = emptyList(),
    val availableFolders: List<FolderEntity> = emptyList(),
    val availableTags: List<TagEntity> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val organizationRepository: OrganizationRepository
) : ViewModel() {

    private val itemId: Long = savedStateHandle.get<Long>("itemId") ?: 1L

    val uiState: StateFlow<ItemDetailUiState> = combine(
        itemRepository.getItemById(itemId),
        itemRepository.getAllItems(),
        organizationRepository.getAllFolders(),
        organizationRepository.getAllTags()
    ) { currentItem, allItems, folders, tags ->
        val related = allItems.filter { it.item.id != itemId }.take(4)
        ItemDetailUiState(
            itemWithRelations = currentItem,
            relatedItems = related,
            availableFolders = folders,
            availableTags = tags,
            isFavorite = currentItem?.item?.isFavorite ?: false,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ItemDetailUiState(isLoading = true)
    )

    fun toggleFavorite() {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.updateItem(current.item.copy(isFavorite = !current.item.isFavorite))
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.deleteItem(current.item)
            onDeleted()
        }
    }

    fun updateTitleAndContent(newTitle: String, newContent: String) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.updateItem(
                current.item.copy(
                    title = newTitle.trim(),
                    fullContent = newContent.trim(),
                    excerpt = newContent.trim().take(160)
                )
            )
        }
    }

    fun assignFolder(folderId: Long) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.assignFolder(current.item.id, folderId)
        }
    }

    fun removeFolder(folderId: Long) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.removeFolder(current.item.id, folderId)
        }
    }

    fun createAndAssignFolder(name: String) {
        val current = uiState.value.itemWithRelations ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            val folderId = organizationRepository.createFolder(name = name.trim())
            if (folderId > 0) {
                itemRepository.assignFolder(current.item.id, folderId)
            }
        }
    }

    fun assignTag(tagId: Long) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.assignTag(current.item.id, tagId)
        }
    }

    fun removeTag(tagId: Long) {
        val current = uiState.value.itemWithRelations ?: return
        viewModelScope.launch {
            itemRepository.removeTag(current.item.id, tagId)
        }
    }

    fun createAndAssignTag(tagName: String) {
        val current = uiState.value.itemWithRelations ?: return
        if (tagName.isBlank()) return
        val cleanTag = tagName.trim().removePrefix("#").lowercase()
        viewModelScope.launch {
            val tagId = organizationRepository.createTag(name = cleanTag)
            if (tagId > 0) {
                itemRepository.assignTag(current.item.id, tagId)
            }
        }
    }
}
