package com.pandora.app.feature.organize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.database.dao.CollectionWithItems
import com.pandora.app.core.database.dao.FolderWithSubfoldersAndItems
import com.pandora.app.core.database.dao.TagWithItemCount
import com.pandora.app.data.repository.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrganizeTab(val label: String) {
    FOLDERS("Folders"),
    COLLECTIONS("Collections"),
    TAGS("Tags")
}

data class OrganizeUiState(
    val activeTab: OrganizeTab = OrganizeTab.FOLDERS,
    val folders: List<FolderWithSubfoldersAndItems> = emptyList(),
    val collections: List<CollectionWithItems> = emptyList(),
    val tags: List<TagWithItemCount> = emptyList(),
    val expandedFolderIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class OrganizeViewModel @Inject constructor(
    private val organizationRepository: OrganizationRepository
) : ViewModel() {

    private val _activeTab = MutableStateFlow(OrganizeTab.FOLDERS)
    private val _expandedFolderIds = MutableStateFlow(emptySet<Long>())

    val uiState: StateFlow<OrganizeUiState> = combine(
        _activeTab,
        organizationRepository.getRootFolders(),
        organizationRepository.getCollections(),
        organizationRepository.getTagsWithCounts(),
        _expandedFolderIds
    ) { tab, folders, collections, tags, expandedIds ->
        OrganizeUiState(
            activeTab = tab,
            folders = folders,
            collections = collections,
            tags = tags,
            expandedFolderIds = expandedIds,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrganizeUiState(isLoading = true)
    )

    fun setActiveTab(tab: OrganizeTab) {
        _activeTab.value = tab
    }

    fun toggleFolderExpanded(folderId: Long) {
        val current = _expandedFolderIds.value.toMutableSet()
        if (current.contains(folderId)) {
            current.remove(folderId)
        } else {
            current.add(folderId)
        }
        _expandedFolderIds.value = current
    }

    fun createFolder(name: String, parentFolderId: Long? = null) {
        viewModelScope.launch {
            organizationRepository.createFolder(name = name, parentFolderId = parentFolderId)
        }
    }

    fun createCollection(name: String, description: String) {
        viewModelScope.launch {
            organizationRepository.createCollection(name = name, description = description)
        }
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            organizationRepository.createTag(name = name)
        }
    }
}
