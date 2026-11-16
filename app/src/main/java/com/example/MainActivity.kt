package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feedback.TasbeehFeedbackManager
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.TasbeehScreen
import com.example.ui.theme.DeepSanctuary
import com.example.ui.theme.ElevatedSage
import com.example.ui.theme.MidnightPine
import com.example.ui.theme.SageMist
import com.example.ui.theme.SoftGoldLight
import com.example.ui.theme.SukoonTheme
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.TasbeehViewModel

object SukoonRoutes {
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val PROGRESS = "progress"
    const val ADMIN = "admin"
    const val TASBEEH = "tasbeeh/{zikrId}"

    fun tasbeeh(zikrId: Long) = "tasbeeh/$zikrId"
}

private data class TopDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topDestinations = listOf(
    TopDestination(SukoonRoutes.HOME, "Sanctuary", Icons.Rounded.Spa, Icons.Outlined.Spa, "nav_home"),
    TopDestination(SukoonRoutes.EXPLORE, "Explore", Icons.Rounded.Explore, Icons.Outlined.Explore, "nav_explore"),
    TopDestination(SukoonRoutes.PROGRESS, "Progress", Icons.Rounded.History, Icons.Outlined.History, "nav_progress"),
    TopDestination(SukoonRoutes.ADMIN, "Admin", Icons.Rounded.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_admin")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            SukoonTheme(darkTheme = true) {
                SukoonApp()
            }
        }
    }
}

@Composable
fun SukoonApp() {
    val context = LocalContext.current
    val app = context.applicationContext as SukoonApplication
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(app.repository)
    )
    val feedbackManager = remember { TasbeehFeedbackManager(context) }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTasbeehScreen = currentRoute?.startsWith("tasbeeh") == true

    val dailyZikr by mainViewModel.dailyZikr.collectAsStateWithLifecycle()
    val publishedAzkar by mainViewModel.publishedAzkar.collectAsStateWithLifecycle()
    val allAzkar by mainViewModel.allAzkar.collectAsStateWithLifecycle()
    val completedSessions by mainViewModel.completedSessions.collectAsStateWithLifecycle()
    val completedTargetCount by mainViewModel.completedTargetCount.collectAsStateWithLifecycle()
    val preferences by mainViewModel.userPreferences.collectAsStateWithLifecycle()
    val isAdminUnlocked by mainViewModel.isAdminUnlocked.collectAsStateWithLifecycle()
    val adminAuthError by mainViewModel.adminAuthError.collectAsStateWithLifecycle()

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSanctuary)
    ) {
        val useNavRail = maxWidth >= 700.dp && !isTasbeehScreen

        Scaffold(
            containerColor = DeepSanctuary,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!isTasbeehScreen && !useNavRail) {
                    NavigationBar(
                        containerColor = MidnightPine,
                        contentColor = SoftGoldLight,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DeepSanctuary,
                                    selectedTextColor = SoftGoldLight,
                                    indicatorColor = WarmGold,
                                    unselectedIconColor = SageMist,
                                    unselectedTextColor = SageMist
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MidnightPine,
                        contentColor = SoftGoldLight,
                        modifier = Modifier
                            .fillMaxHeight()
                            .statusBarsPadding()
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = DeepSanctuary,
                                    selectedTextColor = SoftGoldLight,
                                    indicatorColor = WarmGold,
                                    unselectedIconColor = SageMist,
                                    unselectedTextColor = SageMist
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = SukoonRoutes.HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(SukoonRoutes.HOME) {
                        HomeScreen(
                            dailyZikr = dailyZikr,
                            publishedAzkarCount = publishedAzkar.size,
                            recentSessions = completedSessions,
                            completedTargetCount = completedTargetCount,
                            preferences = preferences,
                            onStartZikr = { id -> navController.navigate(SukoonRoutes.tasbeeh(id)) },
                            onExploreAll = { navigateTopLevel(SukoonRoutes.EXPLORE) },
                            onViewProgress = { navigateTopLevel(SukoonRoutes.PROGRESS) },
                            onUpdateReminder = { enabled, hour, min ->
                                mainViewModel.updateReminder(enabled, hour, min)
                            },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    composable(SukoonRoutes.EXPLORE) {
                        ExploreScreen(
                            azkarList = publishedAzkar,
                            onStartZikr = { id -> navController.navigate(SukoonRoutes.tasbeeh(id)) },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    composable(SukoonRoutes.PROGRESS) {
                        ProgressScreen(
                            totalLifetimeBeads = preferences.totalLifetimeBeads,
                            completedTargetCount = completedTargetCount,
                            sessions = completedSessions,
                            onOpenZikr = { id -> navController.navigate(SukoonRoutes.tasbeeh(id)) },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    composable(SukoonRoutes.ADMIN) {
                        AdminScreen(
                            allAzkar = allAzkar,
                            hasAdminPasscode = preferences.hasAdminPasscode,
                            isUnlocked = isAdminUnlocked,
                            authError = adminAuthError,
                            onSetupPasscode = { pin -> mainViewModel.setupAdminPasscode(pin) },
                            onVerifyPasscode = { pin -> mainViewModel.verifyAdminPasscode(pin) },
                            onLockAdmin = { mainViewModel.lockAdmin() },
                            onClearError = { mainViewModel.clearAdminError() },
                            onSaveZikr = { entity -> mainViewModel.saveZikr(entity) },
                            onSetDailyZikr = { id -> mainViewModel.setDailyZikr(id) },
                            onTogglePublished = { entity -> mainViewModel.togglePublished(entity) },
                            onDeleteZikr = { id -> mainViewModel.deleteZikr(id) },
                            onImportImage = { uri -> mainViewModel.importSceneImage(uri) },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    composable(
                        route = SukoonRoutes.TASBEEH,
                        arguments = listOf(navArgument("zikrId") { type = NavType.LongType })
                    ) { entry ->
                        val zikrId = entry.arguments?.getLong("zikrId") ?: 1L
                        val tasbeehViewModel: TasbeehViewModel = viewModel(
                            key = "tasbeeh_$zikrId",
                            factory = TasbeehViewModel.Factory(zikrId, app.repository, feedbackManager)
                        )
                        TasbeehScreen(
                            viewModel = tasbeehViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
