package com.pandora.app.feature.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.database.dao.ItemWithRelations
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
        itemRepository.getAllItems()
    ) { currentItem, allItems ->
        val related = allItems.filter { it.item.id != itemId }.take(2)
        ItemDetailUiState(
            itemWithRelations = currentItem,
            relatedItems = related,
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
}
