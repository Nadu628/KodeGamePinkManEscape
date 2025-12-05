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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.individual_project3.kodegame.assets.commands.UiCommand
import com.individual_project3.kodegame.game.MazeRendererWithSprites


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
    val vm: MazeViewModel = viewModel(factory = MazeViewModel.Factory(context))

    val spriteManager = remember { SpriteManager(context) }
    val scope = rememberCoroutineScope()

    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    var playerFrameIndex by remember { mutableStateOf(0) }

    var firstPositionSynced by remember { mutableStateOf(false) }

    // ⭐ NEW: shared drag flag for palette/track highlighting
    val isDraggingFromPalette = remember { mutableStateOf(false) }

    // ⭐ Player state shortcut for movement animation
    val playerState = vm.playerState.value

    // --- Load difficulty level once per difficulty ---
    LaunchedEffect(difficulty) {
        firstPositionSynced = false
        vm.generateNextLevel(difficulty)
    }

    // --- Load sprites + music once ---
    LaunchedEffect(Unit) {
        spriteManager.preloadAllAsync(scope)
        vm.startBackgroundMusic(R.raw.sfx_game_music)
    }

    // --- Animate movement based on player position ---
    LaunchedEffect(playerState?.pos) {
        val pos = playerState?.pos ?: return@LaunchedEffect

        if (!firstPositionSynced) {
            animX.snapTo(pos.x.toFloat())
            animY.snapTo(pos.y.toFloat())
            firstPositionSynced = true
            vm.playerAnimState.value = PlayerAnimState.Run
        } else {
            animX.animateTo(
                targetValue = pos.x.toFloat(),
                animationSpec = tween(durationMillis = 200, easing = LinearEasing)
            )
            animY.animateTo(
                targetValue = pos.y.toFloat(),
                animationSpec = tween(durationMillis = 200, easing = LinearEasing)
            )
        }
    }

    // --- Continuous sprite animation loop (only one effect) ---
    LaunchedEffect(Unit) {
        while (true) {
            val state = vm.playerAnimState.value

            val frames = when (state) {
                PlayerAnimState.Jump -> spriteManager.playerJumpFrames
                PlayerAnimState.Drop -> spriteManager.playerDropFrames
                PlayerAnimState.Run  -> spriteManager.playerRunFrames
                PlayerAnimState.Hit  -> spriteManager.playerHitFrames
                else                 -> spriteManager.playerIdleFrames
            }

            val frameDuration = when (state) {
                PlayerAnimState.Hit -> 140L
                PlayerAnimState.Jump, PlayerAnimState.Drop -> 120L
                PlayerAnimState.Run -> 80L
                else -> 140L
            }

            if (frames.isNotEmpty()) {
                playerFrameIndex = (playerFrameIndex + 1) % frames.size
            }

            delay(frameDuration)
        }
    }

    // ---------------- UI ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // === TOP BAR =========================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Row 1: Back | Mode | Exit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CloudButtonTwo(
                    text = "Back",
                    modifier = Modifier.width(90.dp),
                    onClick = {
                        if (onBack != null) onBack()
                        else navController.popBackStack()
                    }
                )

                Text(
                    text = "Mode: ${difficulty.name}",
                    fontFamily = bubbleFont,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                CloudButtonTwo(
                    text = "Exit",
                    modifier = Modifier.width(90.dp),
                    onClick = {
                        navController.navigate("difficulty_screen") {
                            popUpTo("game_screen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Row 2: Next | Reset | Play (single row, no duplicates)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CloudButtonTwo(
                    text = "Next",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(90.dp),
                    onClick = {
                        firstPositionSynced = false
                        vm.generateNextLevel(difficulty)
                    }
                )

                CloudButtonTwo(
                    text = "Reset",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(90.dp),
                    onClick = {
                        vm.cancelProgram()
                        vm.resetPlayerToStart()
                        firstPositionSynced = false
                    }
                )

                CloudButtonTwo(
                    text = "Play",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(90.dp),
                    onClick = {
                        if (!vm.isProgramRunning.value) {
                            vm.runUiProgram(stepDelayMs = 300L)
                        }
                    }
                )


@Composable
fun CommandPaletteBar(
    onAddCommand: (UiCommand) -> Unit,
    isDraggingFromPalette: MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DraggableCommandIcon("↑", UiCommand.MoveUp, onAddCommand, isDraggingFromPalette)
        DraggableCommandIcon("↓", UiCommand.MoveDown, onAddCommand, isDraggingFromPalette)
        DraggableCommandIcon("←", UiCommand.MoveLeft, onAddCommand, isDraggingFromPalette)
        DraggableCommandIcon("→", UiCommand.MoveRight, onAddCommand, isDraggingFromPalette)
    }
}

@Composable
fun DraggableCommandIcon(
    label: String,
    command: UiCommand,
    onAddCommand: (UiCommand) -> Unit,
    isDraggingFromPalette: MutableState<Boolean>
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .size(56.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        isDraggingFromPalette.value = true
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        dragOffset += dragAmount
                        change.consume()
                    },
                    onDragEnd = {
                        isDragging = false
                        isDraggingFromPalette.value = false
                        dragOffset = Offset.Zero
                        onAddCommand(command)
                    },
                    onDragCancel = {
                        isDragging = false
                        isDraggingFromPalette.value = false
                        dragOffset = Offset.Zero
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 28.sp, color = Color.Black)
        }
    }
}



@Composable
fun ProgramTrackBar(
    program: List<UiCommand>,
    onRemoveAt: (Int) -> Unit,
    dropActive: Boolean
) {
    val bgColor = if (dropActive)
        Color(0xFFB3E5FC).copy(alpha = 0.9f)
    else
        Color.White.copy(alpha = 0.8f)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(program) { index, cmd ->
            val label = when (cmd) {
                UiCommand.MoveUp -> "↑"
                UiCommand.MoveDown -> "↓"
                UiCommand.MoveLeft -> "←"
                UiCommand.MoveRight -> "→"
            }

            DraggableTrackBlock(label) {
                onRemoveAt(index)  // tap or drag out to remove
            }
        }
    }
}

@Composable
fun DraggableTrackBlock(
    label: String,
    onRemove: () -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color(0xFFE1F5FE), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDrag = { change, _ -> change.consume() },
                    onDragEnd = {
                        isDragging = false
                        onRemove()
                    },
                    onDragCancel = { isDragging = false }
                )
            }
            .clickable(enabled = !isDragging) { onRemove() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 24.sp)
    }
}
