package com.individual_project3.kodegame.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.individual_project3.kodegame.data.db.DataStore
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.ui.screens.ChildProgressScreen
import com.individual_project3.kodegame.data.repository.AuthRepository
import com.individual_project3.kodegame.game.DifficultyMode
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import com.individual_project3.kodegame.ui.screens.UserTypeScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildLoginScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildRegistrationScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentLoginScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentRegistrationScreen
import com.individual_project3.kodegame.ui.screens.DifficultyScreen
import com.individual_project3.kodegame.ui.screens.GameInstructionsScreen
import com.individual_project3.kodegame.ui.screens.GameScreen
import com.individual_project3.kodegame.ui.screens.ParentDashboardScreen
import com.individual_project3.kodegame.ui.splash.IdleCharacter
import com.individual_project3.kodegame.ui.splash.SplashScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val repo = AuthRepository(db.authDao())
    val sessionManager = DataStore(context)
    val authViewModel = remember { AuthViewModel(repo, sessionManager) }

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash_screen",
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(380),
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(tween(380))
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(320),
                targetOffsetX = { fullWidth -> -fullWidth / 4 }
            ) + fadeOut(tween(320))
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(380),
                initialOffsetX = { fullWidth -> -fullWidth / 3 }
            ) + fadeIn(tween(380))
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(320),
                targetOffsetX = { fullWidth -> fullWidth }
            ) + fadeOut(tween(320))
        }
    ){
        composable("splash_screen"){
            SplashScreen(navController)
        }


        composable("pick_user_screen") {
            UserTypeScreen(navController){
                IdleCharacter()
            }
        }

        composable("child_login_screen") {
            ChildLoginScreen(navController, authViewModel)
        }

        composable("parent_login_screen") {
            ParentLoginScreen(navController, authViewModel)
        }

        composable("parent_registration_screen"){
            ParentRegistrationScreen(navController, authViewModel)
        }

        composable("child_registration_screen") {
            ChildRegistrationScreen(navController, authViewModel)
        }

        composable("difficulty_screen") {
            DifficultyScreen(navController)
        }

        composable("game_instructions_screen/{difficulty}") { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
            GameInstructionsScreen(navController, difficulty)
        }

        composable("game_screen/{difficulty}") { backStackEntry ->
            val diffString = backStackEntry.arguments?.getString("difficulty") ?: "easy"
            val diffMode = when (diffString.lowercase()) {
                "easy" -> DifficultyMode.EASY
                "hard" -> DifficultyMode.HARD
                else -> DifficultyMode.EASY
            }

            GameScreen(difficulty = diffMode, navController = navController)
        }

        composable("parent_dashboard/{parentId}") { backStack ->
            val parentId = backStack.arguments?.getString("parentId")!!.toInt()
            ParentDashboardScreen(parentId, navController)
        }

        composable("child_progress/{childId}") { backStack ->
            val childId = backStack.arguments?.getString("childId")!!.toInt()
            ChildProgressScreen(childId, navController)
        }


    }
}
