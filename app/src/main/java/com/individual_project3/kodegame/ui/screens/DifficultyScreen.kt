package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo

@Composable
fun DifficultyScreen(navController: NavController) {

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))

    val gradient = Brush.verticalGradient(
        listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    val context = LocalContext.current

    val audio = KodeGameApp.audio

    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Difficulty",
                fontSize = 26.sp,
                fontFamily = bubbleFont,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            CloudButtonTwo(
                text = "Easy Mode",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 12.dp)
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.navigate("game_instructions_screen/easy")
            }

            CloudButtonTwo(
                text = "Hard Mode",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 12.dp)
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.navigate("game_instructions_screen/hard")
            }

            Spacer(modifier = Modifier.height(30.dp))

            CloudButtonTwo(
                text = "Back",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.popBackStack()
            }
        }
    }
}
