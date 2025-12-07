package com.individual_project3.kodegame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.Font
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.ui.splash.JumpSequence
import com.individual_project3.kodegame.ui.splash.RunningCharacter
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun UserTypeScreen(
    navController: NavController,
    character: @Composable () -> Unit
) {
    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )
    val context = LocalContext.current
    val audio = KodeGameApp.audio


    // Load SFX only once for this screen
    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }


    var userPicked by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // Fade out when navigating away
    var fadeOut by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        animationSpec = tween(450),
        label = ""
    )

    // Always running animation on this screen
    var isRunning by remember { mutableStateOf(true) }

    // If jumping triggered by spike
    var isJumping by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // When jump triggered, character bounces up
    val baseY = 40.dp
    val jumpHeight = 50.dp
    val animY = remember { Animatable(0f) }

    LaunchedEffect(isJumping) {
        if (isJumping) {
            val base = with(density) { baseY.toPx() }
            val jump = with(density) { jumpHeight.toPx() }

            animY.animateTo(
                base - jump,
                tween(250, easing = FastOutLinearInEasing)
            )
            animY.animateTo(
                base,
                tween(260, easing = LinearOutSlowInEasing)
            )
            isJumping = false
        }
    }

    // Show buttons after short delay
    LaunchedEffect(Unit) {
        delay(300)
        showButtons = true
    }

    // UI LAYOUT
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {

        //Spike looping animation
        if (!userPicked) {
            JumpSequence(
                isVisible = true,
                spikeYdp = -340.dp,
                onSpikeNearCenter = {
                    audio.play(R.raw.sfx_jump)
                    isJumping = true

                }
            )
        }

        //character on top center
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .offset { IntOffset(0, animY.value.roundToInt()) }
        ) {
            if (isRunning) {
                RunningCharacter(isRunning = true, modifier = Modifier.size(74.dp))
            }else{
                character()
            }
        }

        if (!userPicked) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Who are you?",
                    fontSize = 24.sp,
                    fontFamily = bubbleFont,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                //child button
                AnimatedVisibility(
                    visible = showButtons
                ) {
                    CloudButtonTwo("I am a Child") {
                        audio.play(R.raw.sfx_button_click)
                        userPicked = true
                        fadeOut = true

                        scope.launch {
                            delay(350)
                            navController.navigate("child_login_screen") {
                                popUpTo("pick_user_screen") { inclusive = true }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //parent button
                AnimatedVisibility(
                    visible = showButtons
                ) {
                    CloudButtonTwo("I am a Parent") {
                        audio.play(R.raw.sfx_button_click)
                        userPicked = true
                        fadeOut = true

                        scope.launch {
                            delay(350)
                            navController.navigate("parent_login_screen") {
                                popUpTo("pick_user_screen") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}
