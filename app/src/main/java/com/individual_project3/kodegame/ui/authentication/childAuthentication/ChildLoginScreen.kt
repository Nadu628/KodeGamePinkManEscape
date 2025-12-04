package com.individual_project3.kodegame.ui.authentication.childAuthentication

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import com.individual_project3.kodegame.ui.theme.CloudTextField

@Composable
fun ChildLoginScreen(navController: NavController,
                     viewModel: AuthViewModel){
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    val allFilled = username.isNotBlank() && password.isNotBlank()

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    //Error states
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember{mutableStateOf<String?>(null)}

    fun validateALl(): Boolean{
        var ok = true
        usernameError = when{
            username.isBlank() -> "Enter username"
            else -> null
        }
        if(usernameError != null) ok = false

        passwordError = when{
            password.isBlank() -> "Enter password"
            else -> null
        }
        if(passwordError != null) ok = false
        return ok
    }
    Box(
        modifier = Modifier.fillMaxSize().background(gradient),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Child Login",
                fontSize = 24.sp,
                fontFamily = bubbleFont,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            //username field
            CloudTextField(
                value = username,
                onValueChange = {username = it; usernameError = null},
                labelText = "Username",
                isError = usernameError != null,
                errorText = usernameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            //password field with toggle
            CloudTextField(
                value = password,
                onValueChange = {password = it; passwordError = null},
                labelText = "Password",
                isError = passwordError != null,
                errorText = passwordError,
                singleLine = true,
                visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if(passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = {passwordVisible = !passwordVisible }){
                        Icon(imageVector = icon, contentDescription = if(passwordVisible) "Hide password" else "Show password")

                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = allFilled,
                enter = slideInHorizontally(initialOffsetX =  {it  }),
                exit = fadeOut()
            ) {
                CloudButtonTwo("Login") {
                    viewModel.loginChild(username, password){id ->
                        if(id != null){
                            navController.navigate("game_instructions_screen")
                        }else{
                            Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))
            CloudButtonTwo("Register") {
                navController.navigate("child_registration_screen")
            }

            Spacer(modifier = Modifier.height(12.dp))
            CloudButtonTwo("Back") {
                navController.popBackStack()
            }


        }
    }

}