package com.pandora.app.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandora.app.core.export.BackupStats
import com.pandora.app.core.export.PandoraBackupManager
import com.pandora.app.core.security.KeystoreSecretManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val hasApiKey: Boolean = false,
    val apiKeyInput: String = "",
    val isBiometricEnabled: Boolean = false,
    val autoLockMinutes: Int = 5,
    val storageStats: BackupStats = BackupStats(0, 0, 0, 0L),
    val exportStatusMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val keystoreSecretManager: KeystoreSecretManager,
    private val backupManager: PandoraBackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val key = keystoreSecretManager.getGeminiApiKey()
            val biometric = keystoreSecretManager.isBiometricEnabled()
            val timeout = keystoreSecretManager.getAutoLockTimeoutMinutes()
            val stats = backupManager.getStorageStats()

            _uiState.value = _uiState.value.copy(
                hasApiKey = !key.isNullOrBlank(),
                apiKeyInput = key ?: "",
                isBiometricEnabled = biometric,
                autoLockMinutes = timeout,
                storageStats = stats
            )
        }
    }

    fun onApiKeyInputChanged(newInput: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = newInput)
    }

    fun saveApiKey() {
        val key = _uiState.value.apiKeyInput.trim()
        if (key.isNotBlank()) {
            keystoreSecretManager.saveGeminiApiKey(key)
            _uiState.value = _uiState.value.copy(
                hasApiKey = true,
                exportStatusMessage = "Gemini API Key securely saved in Keystore"
            )
        }
    }

    fun clearApiKey() {
        keystoreSecretManager.clearGeminiApiKey()
        _uiState.value = _uiState.value.copy(
            hasApiKey = false,
            apiKeyInput = "",
            exportStatusMessage = "Gemini API Key removed"
        )
    }

    fun toggleBiometric(enabled: Boolean) {
        keystoreSecretManager.setBiometricEnabled(enabled)
        _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
    }

    fun setAutoLockTimeout(minutes: Int) {
        keystoreSecretManager.setAutoLockTimeoutMinutes(minutes)
        _uiState.value = _uiState.value.copy(autoLockMinutes = minutes)
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = backupManager.exportToUri(uri)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                exportStatusMessage = if (result.isSuccess) {
                    "Export complete: ${result.getOrNull()?.itemCount} items packaged into .pandora archive"
                } else {
                    "Export failed: ${result.exceptionOrNull()?.localizedMessage}"
                }
            )
            loadSettings()
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(exportStatusMessage = null)
    }
}
