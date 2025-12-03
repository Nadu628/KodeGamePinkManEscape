package com.individual_project3.kodegame.ui.splash


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt
import com.individual_project3.kodegame.R
import androidx.navigation.NavController
import com.individual_project3.kodegame.ui.screens.CloudButton



@Composable
fun SplashScreen(navController: NavController) {
    val bubbleFont = FontFamily(Font(R.font.poppins_bold))
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59)))

    // UI state
    var showWelcome by remember { mutableStateOf(true) }
    var showPrompt by remember { mutableStateOf(false) }
    var showJumpSequence by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }
    var isJumping by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var userHasPicked by remember { mutableStateOf(false) }
    var startSequence by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    //character position
    val topCenterOffsetDp = (50).dp
    val startOffsetDp = 300.dp
    val characterY = remember { Animatable(with(density){startOffsetDp.toPx()}) }

    // Show prompt after welcome
    LaunchedEffect(Unit) {
        delay(3000)
        showWelcome = false
        showPrompt = true
    }

    // When user taps: slide character up, start sequence when user taps
    LaunchedEffect(startSequence) {
        if (startSequence) {
            characterY.animateTo(
                targetValue = with(density) { topCenterOffsetDp.toPx() },
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
            showJumpSequence = true
            delay(300)
            isRunning = true
            delay(400)
            showButtons = true
        }
    }
    // React to isJumping state to perform the visual jump (so we don't call Animatables from JumpSequence coroutine)
    LaunchedEffect(isJumping) {
        if (isJumping) {
            characterY.animateTo(
                targetValue = with(density) { (-120).dp.toPx() },
                animationSpec = tween(durationMillis = 280, easing = FastOutLinearInEasing)
            )
            characterY.animateTo(
                targetValue = with(density) { topCenterOffsetDp.toPx() },
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
            )
            isJumping = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
            .clickable {
                if (showPrompt && !startSequence) startSequence = true
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.offset{IntOffset(0,characterY.value.roundToInt())}){
                when{
                    isRunning -> RunningCharacter(isRunning = true, modifier = Modifier.size(64.dp))
                    else -> IdleCharacter(modifier = Modifier.size(64.dp))
                }
            }
        }
        if(showJumpSequence && !userHasPicked){
            JumpSequence(
                isVisible = true,
                modifier = Modifier.fillMaxSize(),
                spikeYdp = 160.dp,
                onSpikeNearCenter = {isJumping=true}
            )
        }
        if(!startSequence && !userHasPicked){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(showWelcome){
                    Text(
                        text = "Welcome to Pink Man's Escape",
                        fontSize = 26.sp,
                        fontFamily = bubbleFont,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }else if(showPrompt){
                    Text(
                        text = "Touch the Screen",
                        fontSize = 24.sp,
                        fontFamily = bubbleFont,
                        color = Color.White
                    )
                }
            }
        }
        if(showButtons && !userHasPicked){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                AnimatedVisibility(
                    visible = showButtons,
                    enter = slideInHorizontally(initialOffsetX = {-600}, animationSpec = tween(600))
                ) {
                    CloudButton(
                        text = "I am a Child",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(84.dp)
                            .padding(bottom = 14.dp),
                        onClick = {
                            userHasPicked = true
                            navController.navigate("child_login_screen")
                        }
                    )
                }

                Spacer(modifier=Modifier.height(20.dp))
                AnimatedVisibility(
                    visible = showButtons,
                    enter = slideInHorizontally(initialOffsetX = {600}, animationSpec = tween(600))
                ) {
                    CloudButton(
                        text = "I am a Parent",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(84.dp),
                        onClick = {
                            userHasPicked = true
                            navController.navigate("parent_login_screen")
                        }
                    )
                }
            }
        }
    }

}


