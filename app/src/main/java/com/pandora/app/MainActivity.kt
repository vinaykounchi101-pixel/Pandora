package com.pandora.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.pandora.app.core.designsystem.theme.PandoraTheme
import com.pandora.app.core.util.IncomingShareManager
import com.pandora.app.navigation.PandoraNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var incomingShareManager: IncomingShareManager

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

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            when {
                type.startsWith("text/") -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)
                    if (!sharedText.isNullOrBlank()) {
                        incomingShareManager.emitSharedText(sharedText, subject)
                    }
                }
                type.startsWith("image/") || type == "application/pdf" -> {
                    @Suppress("DEPRECATION")
                    val mediaUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    if (mediaUri != null) {
                        incomingShareManager.emitSharedMedia(mediaUri, type)
                    }
                }
            }
        }
    }
}
