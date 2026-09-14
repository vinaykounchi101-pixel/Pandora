package com.pandora.app.core.util

import com.pandora.app.core.database.entity.ItemEntity
import com.pandora.app.data.repository.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

sealed interface DuplicateCheckResult {
    data object None : DuplicateCheckResult
    data class DuplicateFound(
        val existingItem: ItemEntity,
        val reason: DuplicateReason
    ) : DuplicateCheckResult
}

enum class DuplicateReason {
    EXACT_URL,
    EXACT_TITLE
}

@Singleton
class DuplicateGuardHelper @Inject constructor(
    private val itemRepository: ItemRepository
) {
    suspend fun checkDuplicate(url: String?, title: String?): DuplicateCheckResult {
        if (!url.isNullOrBlank()) {
            val existingUrlItem = itemRepository.findItemByUrl(url)
            if (existingUrlItem != null) {
                return DuplicateCheckResult.DuplicateFound(existingUrlItem, DuplicateReason.EXACT_URL)
            }
        }

        if (!title.isNullOrBlank()) {
            val existingTitleItem = itemRepository.findItemByExactTitle(title)
            if (existingTitleItem != null) {
                return DuplicateCheckResult.DuplicateFound(existingTitleItem, DuplicateReason.EXACT_TITLE)
            }
        }

        return DuplicateCheckResult.None
    }
}
