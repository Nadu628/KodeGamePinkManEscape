package com.individual_project3.kodegame.ui.authentication.parentAuthentication

import android.R.attr.enabled
import android.graphics.Paint
import android.util.Patterns
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.individual_project3.kodegame.ui.authentication.AuthViewModel
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.theme.CloudTextField
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun ParentLoginScreen(navController: NavController,
                      viewModel: AuthViewModel = viewModel()){
    val context = LocalContext.current

    //UI state
    var email by remember { mutableStateOf("") }
    var password by remember{mutableStateOf("")}
    var passwordVisible by remember { mutableStateOf(false) }

    //Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember{mutableStateOf<String?>(null)}

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    fun validateALl(): Boolean{
        var ok = true
        emailError = when{
            email.isBlank() -> "Enter email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
            else -> null
        }
        if(emailError != null) ok = false

        passwordError = when{
            password.isBlank() -> "Enter password"
            else -> null
        }
        if(passwordError != null) ok = false
        return ok
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Parent Login",
                fontSize = 20.sp,
                fontFamily = bubbleFont,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            //email field
            CloudTextField(
                value = email,
                onValueChange = {email = it; emailError = null},
                labelText = "Email",
                isError = emailError != null,
                errorText = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(18.dp))

            val allFilled = remember(email, password){
                email.isNotBlank() && password.isNotBlank()
            }

            Button(
                onClick = {
                    if(validateALl()){
                        viewModel.loginParent(email.trim(), password.trim()){parent ->
                            if(parent != null){
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                navController.navigate("progress_screen"){
                                    popUpTo("parent_login"){inclusive = true}
                                }
                            }else{
                                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = allFilled,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Login", fontFamily = bubbleFont)
            }
        }
    }



}