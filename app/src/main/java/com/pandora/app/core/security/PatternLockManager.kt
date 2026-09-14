package com.pandora.app.core.security

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

private val Context.patternDataStore by preferencesDataStore(name = "pandora_pattern_security")

@Singleton
class PatternLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyPatternHash = stringPreferencesKey("pattern_hash")
    private val keyPatternEnabled = booleanPreferencesKey("pattern_enabled")

    val isPatternEnabled: Flow<Boolean> = context.patternDataStore.data.map { prefs ->
        prefs[keyPatternEnabled] ?: false
    }

    suspend fun setPattern(nodes: List<Int>) {
        val serialized = nodes.joinToString("-")
        val hash = hashPattern(serialized)
        context.patternDataStore.edit { prefs ->
            prefs[keyPatternHash] = hash
            prefs[keyPatternEnabled] = true
        }
    }

    suspend fun verifyPattern(nodes: List<Int>): Boolean {
        val serialized = nodes.joinToString("-")
        val inputHash = hashPattern(serialized)
        val storedHash = context.patternDataStore.data.map { prefs ->
            prefs[keyPatternHash]
        }.first()

        return storedHash != null && storedHash == inputHash
    }

    suspend fun disablePattern() {
        context.patternDataStore.edit { prefs ->
            prefs[keyPatternEnabled] = false
            prefs.remove(keyPatternHash)
        }
    }

    private fun hashPattern(pattern: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pattern.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
