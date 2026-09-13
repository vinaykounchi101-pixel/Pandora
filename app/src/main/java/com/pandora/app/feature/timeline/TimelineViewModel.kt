package com.pandora.app.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.database.DatabaseSeeder
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TimelineFilter(val label: String, val type: ItemType?) {
    ALL("All Items", null),
    SCREENSHOTS("Screenshots", ItemType.IMAGE),
    ARTICLES("Articles", ItemType.ARTICLE),
    NOTES("Notes", ItemType.NOTE),
    DOCUMENTS("Documents", ItemType.DOCUMENT)
}

enum class TimeHorizon {
    DAY,
    MONTH
}

data class TimelineUiState(
    val items: List<ItemWithRelations> = emptyList(),
    val activeFilter: TimelineFilter = TimelineFilter.ALL,
    val timeHorizon: TimeHorizon = TimeHorizon.DAY,
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    private val _activeFilter = MutableStateFlow(TimelineFilter.ALL)
    private val _timeHorizon = MutableStateFlow(TimeHorizon.DAY)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TimelineUiState> = combine(
        itemRepository.getAllItems(),
        _activeFilter,
        _timeHorizon,
        _searchQuery,
        itemRepository.getTotalItemCount()
    ) { items, filter, horizon, query, count ->
        val filtered = items.filter { itemWithRelations ->
            val matchesFilter = when (filter) {
                TimelineFilter.ALL -> true
                TimelineFilter.SCREENSHOTS -> itemWithRelations.item.itemType == ItemType.IMAGE
                TimelineFilter.ARTICLES -> itemWithRelations.item.itemType == ItemType.ARTICLE
                TimelineFilter.NOTES -> itemWithRelations.item.itemType == ItemType.NOTE || itemWithRelations.item.itemType == ItemType.VOICE
                TimelineFilter.DOCUMENTS -> itemWithRelations.item.itemType == ItemType.DOCUMENT
            }
            val matchesQuery = if (query.isBlank()) true else {
                itemWithRelations.item.title.contains(query, ignoreCase = true) ||
                        itemWithRelations.item.excerpt.contains(query, ignoreCase = true) ||
                        itemWithRelations.tags.any { it.name.contains(query, ignoreCase = true) }
            }
            matchesFilter && matchesQuery
        }

        TimelineUiState(
            items = filtered,
            activeFilter = filter,
            timeHorizon = horizon,
            searchQuery = query,
            totalCount = count,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimelineUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            databaseSeeder.seedInitialDataIfNeeded()
        }
    }

    fun setFilter(filter: TimelineFilter) {
        _activeFilter.value = filter
    }

    fun setTimeHorizon(horizon: TimeHorizon) {
        _timeHorizon.value = horizon
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(item: ItemWithRelations) {
        viewModelScope.launch {
            itemRepository.updateItem(item.item.copy(isFavorite = !item.item.isFavorite))
        }
    }

    fun deleteItem(item: ItemWithRelations) {
        viewModelScope.launch {
            itemRepository.deleteItem(item.item)
        }
    }
}
