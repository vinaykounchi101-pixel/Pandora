package com.pandora.app.core.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val vaultDirectory: File by lazy {
        File(context.filesDir, "vault").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    suspend fun saveStreamToVault(
        inputStream: InputStream,
        extension: String
    ): Pair<String, Long> = withContext(Dispatchers.IO) {
        val fileName = "${UUID.randomUUID()}.$extension"
        val destinationFile = File(vaultDirectory, fileName)
        var totalBytes = 0L

        FileOutputStream(destinationFile).use { outputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytes += bytesRead
            }
            outputStream.flush()
        }

        Pair(destinationFile.absolutePath, totalBytes)
    }

    suspend fun copyUriToVault(
        uri: Uri,
        extensionFallback: String = "bin"
    ): Pair<String, Long>? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = when {
                mimeType?.contains("pdf") == true -> "pdf"
                mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> "jpg"
                mimeType?.contains("png") == true -> "png"
                mimeType?.contains("webp") == true -> "webp"
                mimeType?.contains("audio") == true -> "m4a"
                else -> extensionFallback
            }

            contentResolver.openInputStream(uri)?.use { stream ->
                saveStreamToVault(stream, extension)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteVaultFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (file.exists() && file.canonicalPath.startsWith(vaultDirectory.canonicalPath)) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getFreeSpaceBytes(): Long {
        return context.filesDir.freeSpace
    }
}
