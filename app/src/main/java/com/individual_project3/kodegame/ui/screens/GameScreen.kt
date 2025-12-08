package com.individual_project3.kodegame.ui.screens

import android.annotation.SuppressLint
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
import com.individual_project3.kodegame.ui.viewModel.MazeViewModel
import com.individual_project3.kodegame.ui.viewModel.PlayerAnimState
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.LocalizedString
import com.individual_project3.kodegame.assets.commands.NestedProgramParser
import com.individual_project3.kodegame.assets.commands.UiCommand
import com.individual_project3.kodegame.game.MazeRendererWithSprites
import com.individual_project3.kodegame.ui.viewModel.buildStructuredProgram
import kotlinx.coroutines.launch


@SuppressLint("StringFormatInvalid")
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
    val audio = KodeGameApp.audio
    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }


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

    // Load sprites
    LaunchedEffect(Unit) {
        spriteManager.preloadAllAsync(scope)
    }


    // Continuous animation loop for sprite frames
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
                PlayerAnimState.Jump,
                PlayerAnimState.Drop -> 120L
                PlayerAnimState.Hit -> 140L
                else -> 140L
            }

            if (frames.isNotEmpty()) {
                playerFrameIndex = (playerFrameIndex + 1) % frames.size
            }

            delay(duration)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp))  // increased

        // -------------------- TOP BAR -------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Row 1: Back | Mode | Exit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CloudButtonTwo(
                    text = LocalizedString(R.string.back),
                    modifier = Modifier.width(100.dp),
                    onClick = {
                        audio.play(R.raw.sfx_button_click)
                        onBack?.invoke() ?: navController.popBackStack()
                    }
                )

                Text(
                    text = context.getString(R.string.mode, difficulty.name)
                    ,
                    fontFamily = bubbleFont,
                    fontSize = 18.sp,
                    color = Color.White
                )

                CloudButtonTwo(
                    text = LocalizedString(R.string.exit),
                    modifier = Modifier.width(100.dp),
                    onClick = {
                        audio.play(R.raw.sfx_button_click)
                        navController.navigate("difficulty_screen") {
                            popUpTo("game_screen") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            // Row 2: Next | Reset | Play
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (vm.levelCompleted.value) {
                    CloudButtonTwo(
                        text = LocalizedString(R.string.next),
                        modifier = Modifier.width(100.dp),
                        onClick = {
                            audio.play(R.raw.sfx_button_click)
                            vm.levelCompleted.value = false
                            firstPositionSynced.value = false
                            vm.generateNextLevel(difficulty)
                        }
                    )
                }


                Spacer(Modifier.width(10.dp))

                CloudButtonTwo(
                    text = LocalizedString(R.string.reset),
                    modifier = Modifier.width(100.dp),
                    onClick = {
                        audio.play(R.raw.sfx_button_click)
                        vm.cancelProgram()
                        vm.resetPlayerToStart()
                        firstPositionSynced.value = false
                    }
                )

                Spacer(Modifier.width(10.dp))

                CloudButtonTwo(
                    text = LocalizedString(R.string.play),
                    modifier = Modifier.width(100.dp),
                    onClick = {
                        audio.play(R.raw.sfx_button_click)
                        vm.levelCompleted.value = false
                        if (!vm.isProgramRunning.value) {
                            val frozenUiProgram = vm.uiProgram.toList()
                            val structured = buildStructuredProgram(frozenUiProgram)
                            val engineProgram = NestedProgramParser.toEngineCommands(structured)

                            vm.setLastProgram(engineProgram)

                            vm.resetPlayerToStart()
                            firstPositionSynced.value = false

                            vm.playerState.value?.pos?.let { pos ->
                                scope.launch {
                                    animX.snapTo(pos.x.toFloat())
                                    animY.snapTo(pos.y.toFloat())
                                }
                            }
                            vm.runLastProgram(stepDelayMs = 300L)
                        }
                    }
                )
            }
        }

        // ---------------------- MAZE -------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val maze = vm.currentMaze.value

            if (maze == null) {
                Text(
                    LocalizedString(R.string.loading),
                    color = Color.White,
                    fontFamily = bubbleFont,
                    fontSize = 20.sp
                )
            } else {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val tileSizeDp = maxWidth / maze.width
                    val tileSizePx = with(LocalDensity.current) { tileSizeDp.toPx() }

                    // Animate movement
                    LaunchedEffect(vm.playerState.value?.pos) {
                        val pos = vm.playerState.value?.pos ?: return@LaunchedEffect

                        val firstSync = firstPositionSynced.value

                        if (!firstSync) {
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
                    val frame = when (vm.playerAnimState.value) {
                        PlayerAnimState.Jump ->
                            spriteManager.playerJumpFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Drop ->
                            spriteManager.playerDropFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Run  ->
                            spriteManager.playerRunFrames.getOrNull(playerFrameIndex)
                        PlayerAnimState.Hit  ->
                            spriteManager.playerHitFrames.getOrNull(playerFrameIndex)
                        else ->
                            spriteManager.playerIdleFrames.getOrNull(playerFrameIndex)
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

        Spacer(Modifier.height(10.dp))

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
    val paletteCommands = buildList<Pair<String, UiCommand>> {

        // MOVEMENT
        add("↑" to UiCommand.MoveUp)
        add("↓" to UiCommand.MoveDown)
        add("←" to UiCommand.MoveLeft)
        add("→" to UiCommand.MoveRight)

        // EASY + HARD
        if (difficulty == DifficultyMode.EASY || difficulty == DifficultyMode.HARD) {
            add(LocalizedString(R.string.repeat_three) to UiCommand.Repeat(3))
            add(LocalizedString(R.string.if_) to UiCommand.IfHasStrawberry())
        }

        // HARD only
        if (difficulty == DifficultyMode.HARD) {
            add(LocalizedString(R.string.until) to UiCommand.RepeatUntilGoal())
            add(LocalizedString(R.string.while_) to UiCommand.RepeatWhileHasStrawberry())
            add(LocalizedString(R.string.func_start) to UiCommand.FunctionStart)
            add(LocalizedString(R.string.func_end) to UiCommand.EndFunction)
            add(LocalizedString(R.string.call_func) to UiCommand.FunctionCall)
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = paletteCommands,
            key = { (label, command) -> label.hashCode() xor command::class.hashCode() }
        ) { (label, command) ->
            DraggableCommandIcon(
                label = label,
                command = command,
                onAddCommand = onAddCommand,
                isDraggingFromPalette = isDraggingFromPalette
            )
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
            .size(size)
            .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
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
            Text(
                text = label,
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
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
        Color.White.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        if (program.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    LocalizedString(R.string.drag_commands),
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(
                    items = program,
                    key = { index, cmd -> index * 31 + cmd::class.hashCode() }
                ) { index, cmd ->
                    val context = LocalContext.current
                    val label = when (cmd) {
                        UiCommand.MoveUp -> "↑"
                        UiCommand.MoveDown -> "↓"
                        UiCommand.MoveLeft -> "←"
                        UiCommand.MoveRight -> "→"

                        is UiCommand.Repeat -> LocalizedString(R.string.repeat_times, cmd.times)

                        is UiCommand.IfHasStrawberry -> LocalizedString(R.string.if_)

                        UiCommand.FunctionStart -> LocalizedString(R.string.func_start)
                        UiCommand.EndFunction -> LocalizedString(R.string.func_end)
                        UiCommand.FunctionCall -> LocalizedString(R.string.call_func)

                        is UiCommand.RepeatUntilGoal -> LocalizedString(R.string.until)
                        is UiCommand.RepeatWhileHasStrawberry -> LocalizedString(R.string.while_a_strawberry)

                        is UiCommand.FunctionDefinition -> LocalizedString(R.string.func_body)
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
