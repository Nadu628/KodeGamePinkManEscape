package com.individual_project3.kodegame.ui.screens

import android.graphics.Color.alpha
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.individual_project3.kodegame.assets.commands.UiCommand
import com.individual_project3.kodegame.assets.commands.toEngineCommands
import com.individual_project3.kodegame.game.MazeRendererWithSprites
import com.individual_project3.kodegame.ui.theme.bubbleFont
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


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
    val firstPositionSynced = vm.firstPositionSyncedFlag


    val isDraggingFromPalette = remember { mutableStateOf(false) }

    // Load maze on difficulty change
    LaunchedEffect(difficulty) {
        firstPositionSynced.value = false
        vm.generateNextLevel(difficulty)
    }

    // Load sprites + music once
    LaunchedEffect(Unit) {
        spriteManager.preloadAllAsync(scope)
        vm.startBackgroundMusic(R.raw.sfx_game_music)
    }

    // Animate movement
    LaunchedEffect(vm.playerState.value?.pos) {
        val pos = vm.playerState.value?.pos ?: return@LaunchedEffect

        if (!firstPositionSynced.value) {
            animX.snapTo(pos.x.toFloat())
            animY.snapTo(pos.y.toFloat())
            firstPositionSynced.value = true
        } else {
            animX.animateTo(
                targetValue = pos.x.toFloat(),
                animationSpec = tween(200, easing = LinearEasing)
            )
            animY.animateTo(
                targetValue = pos.y.toFloat(),
                animationSpec = tween(200, easing = LinearEasing)
            )
        }
    }

    // Continuous animation loop
    LaunchedEffect(Unit) {
        while (true) {
            val frames = when (vm.playerAnimState.value) {
                PlayerAnimState.Jump -> spriteManager.playerJumpFrames
                PlayerAnimState.Drop -> spriteManager.playerDropFrames
                PlayerAnimState.Run  -> spriteManager.playerRunFrames
                PlayerAnimState.Hit  -> spriteManager.playerHitFrames
                else                 -> spriteManager.playerIdleFrames
            }

            val duration = when (vm.playerAnimState.value) {
                PlayerAnimState.Run -> 80L
                PlayerAnimState.Jump, PlayerAnimState.Drop -> 120L
                PlayerAnimState.Hit -> 140L
                else -> 140L
            }

            if (frames.isNotEmpty()) {
                playerFrameIndex = (playerFrameIndex + 1) % frames.size
            }

            delay(duration)
        }
    }

    // ------------------------------------------------------------------
    // UI LAYOUT
    // ------------------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // -------------------- TOP BAR -------------------------
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(4.dp))
            // Row 1: Back | Mode | Exit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CloudButtonTwo(
                    text = "Back",
                    modifier = Modifier.width(110.dp),
                    onClick = {
                        onBack?.invoke() ?: navController.popBackStack()
                    }
                )

                Text(
                    text = "Mode: ${difficulty.name}",
                    fontFamily = bubbleFont,
                    fontSize = 16.sp,
                    color = Color.White
                )

                CloudButtonTwo(
                    text = "Exit",
                    modifier = Modifier.width(110.dp),
                    onClick = {
                        navController.navigate("difficulty_screen") {
                            popUpTo("game_screen") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(Modifier.height(6.dp))

            // Row 2: Next | Reset | Play
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CloudButtonTwo(
                    text = "Next",
                    modifier = Modifier.width(110.dp),
                    onClick = {
                        firstPositionSynced.value = false
                        vm.generateNextLevel(difficulty)
                    }
                )

                CloudButtonTwo(
                    text = "Reset",
                    modifier = Modifier.width(110.dp),
                    onClick = {
                        vm.cancelProgram()
                        vm.resetPlayerToStart()
                        firstPositionSynced.value = false
                    }
                )

                CloudButtonTwo(
                    text = "Play",
                    modifier = Modifier.width(110.dp),
                    onClick = {
                        if (!vm.isProgramRunning.value) {

                            val frozenUiProgram = vm.uiProgram.toList()
                            val engineProgram = frozenUiProgram.flatMap { it.toEngineCommands() }
                            vm.setLastProgram(engineProgram)

                            vm.runLastProgram(stepDelayMs = 300L)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ---------------------- MAZE -------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f),
            contentAlignment = Alignment.Center
        ) {
            val maze = vm.currentMaze.value

            if (maze == null) {
                Text("Loading...", color = Color.White)
            } else {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val tileSizeDp = maxWidth / maze.width

                    val frame = when (vm.playerAnimState.value) {
                        PlayerAnimState.Jump -> spriteManager.playerJumpFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Drop -> spriteManager.playerDropFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Run  -> spriteManager.playerRunFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Hit  -> spriteManager.playerHitFrames.getOrNull(playerFrameIndex)
                        else -> spriteManager.playerIdleFrames.getOrNull(playerFrameIndex)
                    }

                    MazeRendererWithSprites(
                        maze = maze,
                        playerImage = frame,
                        playerAnimX = animX.value,
                        playerAnimY = animY.value,
                        spriteManager = spriteManager,
                        tileSizeDp = tileSizeDp
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // ---------------- PROGRAM TRACK (DROP AREA) -----------------
        ProgramTrackBar(
            program = vm.uiProgram,
            onRemoveAt = { vm.removeUiCommandAt(it) },
            dropActive = isDraggingFromPalette.value
        )

        Spacer(Modifier.height(6.dp))

        // -------------- DRAGGABLE COMMAND PALETTE -------------------
        CommandPaletteBar(
            difficulty = difficulty,
            onAddCommand = { vm.addUiCommand(it) },
            isDraggingFromPalette = isDraggingFromPalette
        )
    }
}

@Composable
fun CommandPaletteBar(
    difficulty: DifficultyMode,
    onAddCommand: (UiCommand) -> Unit,
    isDraggingFromPalette: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Commands",
            color = Color.White,
            fontFamily = bubbleFont,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val blockSize = 72.dp

            DraggableCommandIcon("↑", UiCommand.MoveUp, onAddCommand, isDraggingFromPalette, blockSize)
            DraggableCommandIcon("↓", UiCommand.MoveDown, onAddCommand, isDraggingFromPalette, blockSize)
            DraggableCommandIcon("←", UiCommand.MoveLeft, onAddCommand, isDraggingFromPalette, blockSize)
            DraggableCommandIcon("→", UiCommand.MoveRight, onAddCommand, isDraggingFromPalette, blockSize)
        }

        Spacer(Modifier.height(6.dp))

        if (difficulty == DifficultyMode.EASY) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DraggableCommandIcon("Repeat 3×", UiCommand.Repeat(3), onAddCommand, isDraggingFromPalette, 90.dp)
                DraggableCommandIcon("If 🍓", UiCommand.IfHasStrawberry, onAddCommand, isDraggingFromPalette, 90.dp)
            }
        }

        if (difficulty == DifficultyMode.HARD) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DraggableCommandIcon("Repeat 5×", UiCommand.Repeat(5), onAddCommand, isDraggingFromPalette, 90.dp)
                DraggableCommandIcon("If 🍓", UiCommand.IfHasStrawberry, onAddCommand, isDraggingFromPalette, 90.dp)
                DraggableCommandIcon("Until 🏁", UiCommand.RepeatUntilGoal, onAddCommand, isDraggingFromPalette, 90.dp)
                DraggableCommandIcon("Func⟦⟧", UiCommand.FunctionStart, onAddCommand, isDraggingFromPalette, 90.dp)
            }
        }
    }
}


@Composable
fun DraggableCommandIcon(
    label: String,
    command: UiCommand,
    onAddCommand: (UiCommand) -> Unit,
    isDraggingFromPalette: MutableState<Boolean>,
    size: Dp = 64.dp
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

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
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        if (program.isEmpty()) {
            Text(
                "Tap or drag arrows to build a path!",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(program) { index, cmd ->

                    val label = when (cmd) {

                        UiCommand.MoveUp -> "↑"
                        UiCommand.MoveDown -> "↓"
                        UiCommand.MoveLeft -> "←"
                        UiCommand.MoveRight -> "→"

                        is UiCommand.Repeat -> "Repeat ${cmd.times}×"
                        UiCommand.IfHasStrawberry -> "If 🍓"

                        UiCommand.RepeatUntilGoal -> "Until 🏁"
                        UiCommand.RepeatWhileHasStrawberry -> "While 🍓"

                        UiCommand.FunctionStart -> "Func⟦⟧"
                        UiCommand.EndFunction -> "End Func"
                        UiCommand.FunctionCall -> "Call Func"
                    }



                    DraggableTrackBlock(label) {
                        onRemoveAt(index)
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableTrackBlock(
    label: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color(0xFFE1F5FE), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {},
                    onDrag = { change, _ -> change.consume() },
                    onDragEnd = { onRemove() },
                    onDragCancel = {}
                )
            }
            .clickable { onRemove() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 24.sp)
    }
}


