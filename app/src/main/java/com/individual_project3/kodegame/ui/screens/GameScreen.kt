package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.navigation.NavController
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.sprites.SpriteManager
import com.individual_project3.kodegame.game.DifficultyMode
import com.individual_project3.kodegame.game.MazeViewModel
import com.individual_project3.kodegame.game.PlayerAnimState
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing


@Composable
fun GameScreen(
    difficulty: DifficultyMode,
    navController: NavController,
    onBack: (() -> Unit)? = null
) {
    val bubbleFont = remember { FontFamily(Font(R.font.poppins_bold)) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xffb3e5fc), Color(0xffb2ff59))
    )

    val context = LocalContext.current

    // --- ViewModel (ONLY declare once) ---
    val vm: MazeViewModel = viewModel(factory = MazeViewModel.Factory(context))

    val spriteManager = remember { SpriteManager(context) }
    val scope = rememberCoroutineScope()

    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    var playerFrameIndex by remember { mutableStateOf(0) }

    // Load difficulty level once
    LaunchedEffect(difficulty) {
        vm.generateNextLevel(difficulty)
    }

    // Load sprites + music once
    LaunchedEffect(Unit) {
        spriteManager.preloadAllAsync(scope)
        vm.startBackgroundMusic(R.raw.sfx_game_music)
    }

    // Animate movement
    val playerState = vm.playerState.value
    LaunchedEffect(playerState?.pos) {
        val pos = playerState?.pos ?: return@LaunchedEffect

        animX.animateTo(
            targetValue = pos.x.toFloat(),
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        )

        animY.animateTo(
            targetValue = pos.y.toFloat(),
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        )

    }

    // Animate sprite frames
    LaunchedEffect(vm.playerAnimState.value) {
        val state = vm.playerAnimState.value
        playerFrameIndex = 0

        val frames = when (state) {
            PlayerAnimState.Jump -> spriteManager.playerJumpFrames
            PlayerAnimState.Drop -> spriteManager.playerDropFrames
            PlayerAnimState.Run -> spriteManager.playerRunFrames
            PlayerAnimState.Hit -> spriteManager.playerHitFrames
            else -> spriteManager.playerIdleFrames
        }

        val frameDuration = when (state) {
            PlayerAnimState.Hit -> 140L
            PlayerAnimState.Jump, PlayerAnimState.Drop -> 120L
            PlayerAnimState.Run -> 80L
            else -> 140L
        }

        if (frames.isNotEmpty()) {
            while (vm.playerAnimState.value == state) {
                delay(frameDuration)
                playerFrameIndex = (playerFrameIndex + 1) % frames.size
            }
        }
    }

    //UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {

        // --- Top bar with Back + Mode ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                CloudButtonTwo(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.widthIn(min = 96.dp)
                )
            } else {
                Spacer(Modifier.width(96.dp))
            }

            Text(
                text = "Mode: ${difficulty.name}",
                fontFamily = bubbleFont,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(Modifier.width(96.dp))
        }

        Spacer(Modifier.height(12.dp))

        // --- Next Level + Play ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CloudButtonTwo(
                text = "Next Level",
                onClick = { vm.generateNextLevel(difficulty) }
            )

        }
    }
}

