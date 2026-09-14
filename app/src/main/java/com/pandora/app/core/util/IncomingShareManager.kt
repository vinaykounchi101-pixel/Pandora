package com.pandora.app.core.util

import android.net.Uri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface IncomingSharePayload {
    data class SharedText(val text: String, val title: String? = null) : IncomingSharePayload
    data class SharedMedia(val uri: Uri, val mimeType: String) : IncomingSharePayload
}

@Singleton
class IncomingShareManager @Inject constructor() {
    private val _incomingShare = MutableSharedFlow<IncomingSharePayload>(extraBufferCapacity = 1)
    val incomingShare: SharedFlow<IncomingSharePayload> = _incomingShare.asSharedFlow()

    fun emitSharedText(text: String, title: String? = null) {
        _incomingShare.tryEmit(IncomingSharePayload.SharedText(text, title))
    }

    fun emitSharedMedia(uri: Uri, mimeType: String) {
        _incomingShare.tryEmit(IncomingSharePayload.SharedMedia(uri, mimeType))
    }
}
