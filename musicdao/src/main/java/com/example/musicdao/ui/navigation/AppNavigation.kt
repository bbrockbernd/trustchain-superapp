package com.example.musicdao.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.musicdao.ui.components.player.FullPlayerScreen
import com.example.musicdao.ui.components.player.PlayerViewModel
import com.example.musicdao.ui.screens.artists.DiscoverArtistsScreen
import com.example.musicdao.ui.screens.debug.Debug
import com.example.musicdao.ui.screens.donate.DonateScreen
import com.example.musicdao.ui.screens.home.HomeScreen
import com.example.musicdao.ui.screens.home.HomeScreenViewModel
import com.example.musicdao.ui.screens.profile.*
import com.example.musicdao.ui.screens.release.ReleaseScreen
import com.example.musicdao.ui.screens.search.DebugScreenViewModel
import com.example.musicdao.ui.screens.search.SearchScreen
import com.example.musicdao.ui.screens.search.SearchScreenViewModel
import com.example.musicdao.ui.screens.settings.SettingsScreen
import com.example.musicdao.ui.screens.settings.SettingsScreenViewModel
import com.example.musicdao.ui.screens.wallet.BitcoinWalletScreen
import com.example.musicdao.ui.screens.wallet.BitcoinWalletViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    playerViewModel: PlayerViewModel,
    ownProfileViewScreenModel: MyProfileScreenViewModel
) {
    val bitcoinWalletViewModel: BitcoinWalletViewModel = hiltViewModel()

    AnimatedNavHost(
        modifier = Modifier.fillMaxSize(),
        enterTransition = { _, _ -> EnterTransition.None },
        exitTransition = { _, _ -> ExitTransition.None },
        navController = navController,
        startDestination = Screen.Home.route,
        builder = {
            composable(Screen.Home.route) {
                val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
                HomeScreen(
                    navController = navController,
                    homeScreenViewModel = homeScreenViewModel
                )
            }
            composable(Screen.Search.route) {
                val searchScreenScreenViewModel: SearchScreenViewModel = hiltViewModel()
                SearchScreen(navController, searchScreenScreenViewModel)
            }
            composable(Screen.Debug.route) {
                val debugScreenViewModel: DebugScreenViewModel = hiltViewModel()

                Debug(debugScreenViewModel)
            }
            composable(Screen.MyProfile.route) {
                MyProfileScreen(navController = navController, ownProfileViewScreenModel)
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(navController = navController)
            }
            composable(Screen.CreatorMenu.route) {
                ProfileMenuScreen(navController = navController)
            }
            composable(Screen.BitcoinWallet.route) {
                BitcoinWalletScreen(bitcoinWalletViewModel = bitcoinWalletViewModel)
            }
            composable(
                Screen.Donate.route,
                arguments = listOf(
                    navArgument("publicKey") {
                        type = NavType.StringType
                    }
                )
            ) { navBackStackEntry ->
                DonateScreen(
                    bitcoinWalletViewModel = bitcoinWalletViewModel,
                    navBackStackEntry.arguments?.getString(
                        "publicKey"
                    )!!,
                    navController = navController
                )
            }
            composable(Screen.DiscoverArtists.route) {
                DiscoverArtistsScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                val settingsScreenViewModel: SettingsScreenViewModel = hiltViewModel()
                SettingsScreen(settingsScreenViewModel)
            }
            composable(
                Screen.Profile.route,
                arguments = listOf(
                    navArgument("publicKey") {
                        type = NavType.StringType
                    }
                )
            ) { navBackStackEntry ->
                ProfileScreen(
                    navBackStackEntry.arguments?.getString(
                        "publicKey"
                    )!!,
                    navController = navController
                )
            }
            composable(
                Screen.Release.route,
                arguments = listOf(
                    navArgument("releaseId") {
                        type = NavType.StringType
                    }
                )
            ) { navBackStackEntry ->
                ReleaseScreen(
                    navBackStackEntry.arguments?.getString(
                        "releaseId"
                    )!!,
                    playerViewModel = playerViewModel,
                    navController = navController
                )
            }
            composable(
                Screen.FullPlayerScreen.route,
                enterTransition = { _, _ ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(200)
                    )
                },
                exitTransition = { initial, _ ->
                    slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Down,
                        animationSpec = tween(200)
                    )
                },
            ) {
                FullPlayerScreen(playerViewModel)
            }
        }
    )
}
