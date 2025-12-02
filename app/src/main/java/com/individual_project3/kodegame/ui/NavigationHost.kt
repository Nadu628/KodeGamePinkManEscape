package com.individual_project3.kodegame.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.individual_project3.kodegame.data.DataStore
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.data.repository.AuthRepository
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import com.individual_project3.kodegame.ui.authentication.UserTypeScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildLoginScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildRegistrationScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentLoginScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentRegistrationScreen
import com.individual_project3.kodegame.ui.splash.IdleCharacter
import com.individual_project3.kodegame.ui.splash.SplashScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val repo = AuthRepository(db.authDao())
    val sessionManager = DataStore(context)
    val authViewModel = remember { AuthViewModel(repo, sessionManager) }
    NavHost(navController = navController, startDestination = "splash_screen"){
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

    }
}
