package com.individual_project3.kodegame.ui.splash


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.individual_project3.kodegame.R
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    var showWelcome by remember { mutableStateOf(true) }
    var showPrompt by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        animationSpec = tween(600),
        label = "fade"
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(3000)
        showWelcome = false
        showPrompt = true
    }

    fun handleTap() {
        if (!showPrompt) return
        fadeOut = true

        scope.launch {
            delay(700)
            navController.navigate("pick_user_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .alpha(alpha)
            .clickable { handleTap() },
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            IdleCharacter(
                modifier = Modifier.size(90.dp),
                frameDelayMs = 150L
            )

            Spacer(Modifier.height(32.dp))

            when {
                showWelcome -> Text(
                    "Welcome to Pink Man's Escape",
                    fontSize = 26.sp,
                    fontFamily = bubbleFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                showPrompt -> Text(
                    "Touch the Screen",
                    fontSize = 24.sp,
                    fontFamily = bubbleFont,
                    color = Color.White
                )
            }
        }
    }
}
