package com.individual_project3.kodegame.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.individual_project3.kodegame.ui.authentication.UserTypeScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildLoginScreen
import com.individual_project3.kodegame.ui.authentication.childAuthentication.ChildRegistrationScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentLoginScreen
import com.individual_project3.kodegame.ui.authentication.parentAuthentication.ParentRegistrationScreen
import com.individual_project3.kodegame.ui.splash.IdleCharacter
import com.individual_project3.kodegame.ui.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(){
    val navController = rememberNavController()
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
            ChildLoginScreen(navController)
        }

        composable("parent_login_screen") {
            ParentLoginScreen(navController)
        }

        composable("parent_registration_screen"){
            ParentRegistrationScreen(navController)
        }


    }
}
