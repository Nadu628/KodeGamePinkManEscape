package com.individual_project3.kodegame.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo

@Composable
fun GameInstructionsScreen(
    navController: NavController,
    difficulty: String
) {

    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    val gradient = Brush.verticalGradient(
        listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    val instructions = when (difficulty) {
        "easy" -> """
            • Move Pink Man through the cloud maze  
            • Drag and drop arrow blocks (Up, Down, Left, Right)  
            • Use simple loops and conditions  
            • Collect strawberries  
            • Avoid spikes  
            • Reach the goal to win!
        """.trimIndent()

        else -> """
            • Everything from Easy Mode  
            • PLUS: functions, nested loops, and trickier hazards  
            • Think carefully before you run your program  
            • Plan ahead and test your logic  
        """.trimIndent()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "How to Play",
                fontSize = 28.sp,
                fontFamily = bubbleFont,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = instructions,
                fontSize = 18.sp,
                fontFamily = bubbleFont,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            CloudButtonTwo(
                text = "Start Game",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                navController.navigate("game_screen/$difficulty")
            }

            Spacer(modifier = Modifier.height(20.dp))

            CloudButtonTwo(
                text = "Back",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                navController.popBackStack()
            }
        }
    }
}