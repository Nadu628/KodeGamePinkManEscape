package com.individual_project3.kodegame.ui.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.Font
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.splash.JumpSequence
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

@Composable
fun UserTypeScreen(navController: NavController,
                   character: @Composable () -> Unit){
    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    var userPicked by remember{mutableStateOf(false)}
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showButtons = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        //background animation (spikes and jump)
        if(!userPicked){
            JumpSequence(isVisible = true)
        }
        //make character stay at the top
        Box(modifier=Modifier
            .align(Alignment.TopCenter)
            .padding(top = 40.dp)){
            character()
        }
        if(!userPicked){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 80.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Who are you?",
                    fontSize = 24.sp,
                    fontFamily = bubbleFont,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                AnimatedVisibility(
                    visible = showButtons,
                    enter = slideInHorizontally(initialOffsetX = {-300})
                ) {
                    CloudButton("I am a Child"){
                        navController.navigate("child_login_screen")
                    }
                }

                AnimatedVisibility(
                    visible = showButtons,
                    enter = slideInHorizontally(initialOffsetX={300})
                ) {
                    CloudButton("I am a Parent"){
                        navController.navigate("parent_login_screen")
                    }
                }
            }

        }
    }
}


@Composable
fun CloudButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit){
    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .height(64.dp)
    ){
        Text(text, fontSize = 18.sp, fontFamily = bubbleFont)
    }
}