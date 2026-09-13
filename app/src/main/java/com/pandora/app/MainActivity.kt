package com.pandora.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pandora.app.core.designsystem.theme.PandoraTheme
import com.pandora.app.navigation.PandoraNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            PandoraTheme {
                PandoraNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            when {
                type.startsWith("text/") -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    // Handled gracefully in capture pipeline
                }
                type.startsWith("image/") -> {
                    val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    // Handled gracefully in capture pipeline
                }
                type == "application/pdf" -> {
                    val pdfUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    // Handled gracefully in capture pipeline
                }
            }
        }
    }
}
