package com.pandora.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pandora.app.core.designsystem.component.FloatingCaptureCapsule
import com.pandora.app.core.designsystem.component.PandoraBottomNav
import com.pandora.app.core.designsystem.component.PandoraTopAppBar
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.feature.capture.LinkPasteBottomSheet
import com.pandora.app.feature.capture.QuickNoteBottomSheet
import com.pandora.app.feature.organize.OrganizeScreen
import com.pandora.app.feature.organize.OrganizeTab
import com.pandora.app.feature.organize.OrganizeViewModel
import com.pandora.app.feature.proposal.AiProposalBottomSheet
import com.pandora.app.feature.reader.ItemDetailScreen
import com.pandora.app.feature.reader.ItemDetailViewModel
import com.pandora.app.feature.search.SearchScreen
import com.pandora.app.feature.settings.SettingsScreen
import com.pandora.app.feature.timeline.TimelineScreen
import com.pandora.app.feature.timeline.TimelineViewModel
import kotlinx.coroutines.launch

@Composable
fun PandoraNavHost(
    navController: NavHostController = rememberNavController(),
    timelineViewModel: TimelineViewModel = hiltViewModel(),
    organizeViewModel: OrganizeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Timeline.route
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showAiProposalSheet by remember { mutableStateOf(false) }
    var showQuickNoteSheet by remember { mutableStateOf(false) }
    var showLinkPasteSheet by remember { mutableStateOf(false) }

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            timelineViewModel.quickSaveMedia(uri, "Captured Image")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Media securely saved to Offline Vault")
            }
        }
    }

    // Observe inbound Android Share Sheet intents
    androidx.compose.runtime.LaunchedEffect(Unit) {
        timelineViewModel.incomingShareManager.incomingShare.collect { payload ->
            when (payload) {
                is com.pandora.app.core.util.IncomingSharePayload.SharedText -> {
                    if (payload.text.startsWith("http://") || payload.text.startsWith("https://")) {
                        showLinkPasteSheet = true
                    } else {
                        showQuickNoteSheet = true
                    }
                }
                is com.pandora.app.core.util.IncomingSharePayload.SharedMedia -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Media received from Share Sheet: ${payload.mimeType}")
                    }
                }
            }
        }
    }

    val isTopLevelDestination = currentRoute in listOf(
        Screen.Timeline.route,
        Screen.Organize.route,
        Screen.Search.route,
        Screen.Explore.route
    )

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PorcelainCanvas,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (isTopLevelDestination) {
                    val screenTitle = when (currentRoute) {
                        Screen.Organize.route -> "Organize"
                        Screen.Search.route -> "Search"
                        Screen.Explore.route -> "Explore"
                        else -> "Timeline"
                    }
                    PandoraTopAppBar(
                        title = screenTitle,
                        onVaultClick = {
                            navController.navigate(Screen.Settings.route)
                        },
                        onProfileClick = {
                            navController.navigate(Screen.Settings.route)
                        },
                        onNotificationsClick = {
                            showAiProposalSheet = true
                        }
                    )
                }
            },
            bottomBar = {
                if (isTopLevelDestination) {
                    PandoraBottomNav(
                        currentRoute = currentRoute,
                        onNavigate = { destination ->
                            if (destination.route != currentRoute) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Timeline.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Timeline.route) {
                    TimelineScreen(
                        viewModel = timelineViewModel,
                        onItemClick = { itemId ->
                            navController.navigate(Screen.ItemDetail.createRoute(itemId))
                        }
                    )
                }

                composable(Screen.Organize.route) {
                    OrganizeScreen(
                        viewModel = organizeViewModel,
                        onFolderClick = { folderId ->
                            navController.navigate(Screen.Search.route)
                        },
                        onCollectionClick = { collectionId ->
                            navController.navigate(Screen.Search.route)
                        },
                        onTagClick = { tag ->
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        onNavigateBack = {
                            navController.navigate(Screen.Timeline.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToItemDetail = { itemId ->
                            navController.navigate(Screen.ItemDetail.createRoute(itemId))
                        }
                    )
                }

                composable(Screen.Explore.route) {
                    OrganizeScreen(
                        viewModel = organizeViewModel,
                        onFolderClick = { folderId ->
                            navController.navigate(Screen.Search.route)
                        },
                        onCollectionClick = { collectionId ->
                            navController.navigate(Screen.Search.route)
                        },
                        onTagClick = { tag ->
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.ItemDetail.route,
                    arguments = listOf(
                        navArgument("itemId") { type = NavType.LongType }
                    )
                ) {
                    val detailViewModel: ItemDetailViewModel = hiltViewModel()
                    ItemDetailScreen(
                        viewModel = detailViewModel,
                        onBackClick = { navController.popBackStack() },
                        onRelatedItemClick = { relatedId ->
                            navController.navigate(Screen.ItemDetail.createRoute(relatedId))
                        }
                    )
                }
            }
        }

        // Floating Capture Capsule floating cleanly above the bottom navigation bar
        AnimatedVisibility(
            visible = isTopLevelDestination,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 66.dp)
        ) {
            FloatingCaptureCapsule(
                onCameraClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onNoteClick = {
                    showQuickNoteSheet = true
                },
                onQuickAddClick = {
                    showAiProposalSheet = true
                },
                onLinkClick = {
                    showLinkPasteSheet = true
                },
                onVoiceClick = {
                    showQuickNoteSheet = true
                }
            )
        }
    }

    // Modal Bottom Sheets
    if (showAiProposalSheet) {
        AiProposalBottomSheet(
            onDismiss = { showAiProposalSheet = false },
            onApprove = { folders, tags ->
                showAiProposalSheet = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Saved to ${folders.joinToString()} and tagged")
                }
            },
            onDecline = {
                showAiProposalSheet = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("AI suggestion dismissed. Manual catalog opened.")
                }
            }
        )
    }

    if (showQuickNoteSheet) {
        QuickNoteBottomSheet(
            onDismiss = { showQuickNoteSheet = false },
            onSaveNote = { title, content ->
                showQuickNoteSheet = false
                timelineViewModel.quickSaveNote(title, content)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Note archived in vault")
                }
            },
            voiceHelper = timelineViewModel.voiceHelper,
            duplicateGuardHelper = timelineViewModel.duplicateGuardHelper
        )
    }

    if (showLinkPasteSheet) {
        LinkPasteBottomSheet(
            onDismiss = { showLinkPasteSheet = false },
            onSaveLink = { url, title ->
                showLinkPasteSheet = false
                timelineViewModel.quickSaveLink(url, title)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Link archived in vault")
                }
            },
            duplicateGuardHelper = timelineViewModel.duplicateGuardHelper
        )
    }
}
