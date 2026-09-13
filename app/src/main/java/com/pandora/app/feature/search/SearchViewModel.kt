package com.pandora.app.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.database.dao.ItemDao
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedTypeFilter: ItemType? = null,
    val isFavoriteOnly: Boolean = false,
    val searchResults: List<ItemWithRelations> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val itemDao: ItemDao
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<ItemType?>(null)
    val selectedTypeFilter: StateFlow<ItemType?> = _selectedTypeFilter.asStateFlow()

    private val _isFavoriteOnly = MutableStateFlow(false)
    val isFavoriteOnly: StateFlow<Boolean> = _isFavoriteOnly.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        _selectedTypeFilter,
        _isFavoriteOnly
    ) { queryText, typeFilter, favoriteOnly ->
        Triple(queryText, typeFilter, favoriteOnly)
    }.flatMapLatest { (queryText, typeFilter, favoriteOnly) ->
        val flow = if (queryText.isBlank()) {
            itemDao.getAllItemsWithRelations()
        } else {
            itemDao.searchItems(queryText)
        }
        flow.combine(MutableStateFlow(Triple(queryText, typeFilter, favoriteOnly))) { items, (q, type, fav) ->
            val filtered = items.filter { rel ->
                val typeMatch = type == null || rel.item.itemType == type
                val favMatch = !fav || rel.item.isFavorite
                typeMatch && favMatch
            }
            SearchUiState(
                query = q,
                selectedTypeFilter = type,
                isFavoriteOnly = fav,
                searchResults = filtered,
                isSearching = q.isNotBlank()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onTypeFilterSelected(type: ItemType?) {
        _selectedTypeFilter.value = if (_selectedTypeFilter.value == type) null else type
    }

    fun onFavoriteToggle() {
        _isFavoriteOnly.value = !_isFavoriteOnly.value
    }

    fun clearSearch() {
        _query.value = ""
        _selectedTypeFilter.value = null
        _isFavoriteOnly.value = false
    }
}
