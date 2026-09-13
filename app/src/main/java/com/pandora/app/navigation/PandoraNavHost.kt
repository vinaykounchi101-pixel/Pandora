package com.pandora.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pandora.app.core.designsystem.component.FloatingCaptureCapsule
import com.pandora.app.core.designsystem.component.NavDestination
import com.pandora.app.core.designsystem.component.PandoraBottomNav
import com.pandora.app.core.designsystem.component.PandoraTopAppBar
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.feature.capture.LinkPasteBottomSheet
import com.pandora.app.feature.capture.QuickNoteBottomSheet
import com.pandora.app.feature.organize.OrganizeScreen
import com.pandora.app.feature.organize.OrganizeViewModel
import com.pandora.app.feature.proposal.AiProposalBottomSheet
import com.pandora.app.feature.reader.ItemDetailScreen
import com.pandora.app.feature.reader.ItemDetailViewModel
import com.pandora.app.feature.timeline.TimelineScreen
import com.pandora.app.feature.timeline.TimelineViewModel
import kotlinx.coroutines.launch

import com.pandora.app.feature.search.SearchScreen
import com.pandora.app.feature.settings.SettingsScreen

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

    val isTopLevelDestination = currentRoute in listOf(
        Screen.Timeline.route,
        Screen.Organize.route,
        Screen.Search.route,
        Screen.Explore.route
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingCaptureCapsule(
                        onCameraClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Camera capture opened")
                            }
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
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Voice memo recording started")
                            }
                        },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    PandoraBottomNav(
                        currentRoute = currentRoute,
                        onNavigate = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(Screen.Timeline.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Timeline.route,
            modifier = Modifier.padding(innerPadding)
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
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Folder #$folderId selected")
                        }
                    },
                    onCollectionClick = { collectionId ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Collection #$collectionId opened")
                        }
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = {
                        navController.navigate(Screen.Timeline.route) {
                            popUpTo(Screen.Timeline.route) { inclusive = true }
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
                    onFolderClick = {},
                    onCollectionClick = {}
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
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Note archived in vault")
                }
            }
        )
    }

    if (showLinkPasteSheet) {
        LinkPasteBottomSheet(
            onDismiss = { showLinkPasteSheet = false },
            onSaveLink = { url, title ->
                showLinkPasteSheet = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Link archived in vault")
                }
            }
        )
    }
}
