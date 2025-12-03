package com.individual_project3.kodegame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.individual_project3.kodegame.assets.audio.rememberAudioManager
import com.individual_project3.kodegame.game.*
import com.individual_project3.kodegame.assets.sprites.PlayerAnimState
import com.individual_project3.kodegame.assets.sprites.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo

@Composable
fun GameScreen(
    vm: MazeViewModel = viewModel(factory = MazeViewModelFactory(LocalContext.current)),
    spriteManager: SpriteManager = remember { SpriteManager(LocalContext.current) }
) {
    val scope = rememberCoroutineScope()
    val engine = remember { CommandEngine(mazeModel) }

    val audioManager = rememberAudioManager(
        hit = R.raw.sfx_hit,
        jump = R.raw.sfx_jump,
        drop = R.raw.sfx_drop,
        collect = R.raw.sfx_collecting_fruit,
        button = R.raw.sfx_button_click,
        success = R.raw.sfx_success,
        fail = R.raw.sfx_fail,
        music = R.raw.sfx_game_music,
        autoStartMusic = true
    )

    // UI state
    var program by remember { mutableStateOf(listOf<Command>()) }
    var isRunning by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Ready") }
    var strawberries by remember { mutableStateOf(initialPlayerState.strawberries) }
    var playerPos by remember { mutableStateOf(initialPlayerState.pos) }
    var playerAnimState by remember { mutableStateOf(PlayerAnimState.Idle) }

    // animatables for smooth movement
    val animX = remember { Animatable(playerPos.x.toFloat()) }
    val animY = remember { Animatable(playerPos.y.toFloat()) }

    // sprite frame lists (replace with your drawable ids)
    val playerIdleFrames = remember {
        listOf(
            R.drawable.pink_idle1, R.drawable.pink_idle2, R.drawable.pink_idle3,
            R.drawable.pink_idle4, R.drawable.pink_idle5, R.drawable.pink_idle6,
            R.drawable.pink_idle7, R.drawable.pink_idle8, R.drawable.pink_idle9,
            R.drawable.pink_idle10, R.drawable.pink_idle11
        )
    }
    val playerRunFrames = remember {
        listOf(
            R.drawable.pink_run1, R.drawable.pink_run2, R.drawable.pink_run3, R.drawable.pink_run4,
            R.drawable.pink_run5, R.drawable.pink_run6, R.drawable.pink_run7, R.drawable.pink_run8,
            R.drawable.pink_run9, R.drawable.pink_run10, R.drawable.pink_run11, R.drawable.pink_run12
        )
    }
    val playerJumpFrames = remember { listOf(R.drawable.pink_jump) }
    val playerDropFrames = remember { listOf(R.drawable.pink_drop) }
    val playerHitFrames = remember {
        listOf(
            R.drawable.pink_hit1, R.drawable.pink_hit2, R.drawable.pink_hit3,
            R.drawable.pink_hit4, R.drawable.pink_hit5, R.drawable.pink_hit6,
            R.drawable.pink_hit7
        )
    }

    val enemyFrames = remember {
        listOf(
            R.drawable.enemy_idle1, R.drawable.enemy_idle2, R.drawable.enemy_idle3,
            R.drawable.enemy_idle4, R.drawable.enemy_idle5, R.drawable.enemy_idle6,
            R.drawable.enemy_idle7, R.drawable.enemy_idle8, R.drawable.enemy_idle9,
            R.drawable.enemy_idle10, R.drawable.enemy_idle11
        )
    }
    val strawberryFrames = remember {
        listOf(
            R.drawable.strawberry_idle1, R.drawable.strawberry_idle2, R.drawable.strawberry_idle3,
            R.drawable.strawberry_idle4, R.drawable.strawberry_idle5, R.drawable.strawberry_idle6,
            R.drawable.strawberry_idle7, R.drawable.strawberry_idle8, R.drawable.strawberry_idle9,
            R.drawable.strawberry_idle10, R.drawable.strawberry_idle11
        )
    }

    val palette = listOf(Command.MoveLeft, Command.MoveRight, Command.MoveUp, Command.MoveDown)

    var runJob by remember { mutableStateOf<Job?>(null) }

    // CHANGED: gate UI until SFX loaded
    var audioReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (!audioManager.areSfxReady()) {
            kotlinx.coroutines.delay(40)
        }
        audioReady = true
    }

    val onEvent: suspend (ExecEvent) -> Unit = { ev ->
        when (ev) {
            is ExecEvent.Step -> {
                playerPos = ev.newPos
                strawberries = ev.strawberries
                scope.launch {
                    animX.animateTo(ev.newPos.x.toFloat(), animationSpec = tween(250))
                    animY.animateTo(ev.newPos.y.toFloat(), animationSpec = tween(250))
                }
                playerAnimState = PlayerAnimState.Running
                scope.launch {
                    kotlinx.coroutines.delay(220)
                    playerAnimState = PlayerAnimState.Idle
                }
                statusText = "Moved to ${ev.newPos.x},${ev.newPos.y}"
            }
            is ExecEvent.CollectedStrawberry -> {
                strawberries = ev.strawberries
                audioManager.playSfx(audioManager.idCollect)
                statusText = "Collected strawberry x${ev.strawberries}"
            }
            is ExecEvent.HitEnemyConsumedLife -> {
                strawberries = ev.strawberriesLeft
                audioManager.playSfx(audioManager.idHit)
                playerAnimState = PlayerAnimState.Hit
                scope.launch {
                    kotlinx.coroutines.delay(400)
                    playerAnimState = PlayerAnimState.Idle
                }
                statusText = "Hit enemy, life consumed"
            }
            is ExecEvent.HitEnemyNoLifeReset -> {
                playerPos = ev.resetTo
                scope.launch {
                    animX.snapTo(ev.resetTo.x.toFloat())
                    animY.snapTo(ev.resetTo.y.toFloat())
                }
                audioManager.playSfx(audioManager.idFail)
                playerAnimState = PlayerAnimState.Hit
                scope.launch {
                    kotlinx.coroutines.delay(400)
                    playerAnimState = PlayerAnimState.Idle
                }
                statusText = "Hit enemy, reset"
            }
            is ExecEvent.Success -> {
                audioManager.playSfx(audioManager.idSuccess)
                audioManager.stopBackground()
                statusText = "Level complete!"
            }
            is ExecEvent.Log -> {
                statusText = ev.message
            }
        }
    }

    fun startExecution() {
        if (isRunning) return
        isRunning = true
        audioManager.playSfx(audioManager.idButton)
        statusText = "Running..."
        val state = PlayerState(playerPos, strawberries, initialPlayerState.startPos)
        runJob = engine.runProgram(program, state, scope, stepDelayMs = 400L, onEvent = { e ->
            scope.launch { onEvent(e) }
        })
        runJob?.invokeOnCompletion { scope.launch { isRunning = false } }
    }

    fun resetLevel() {
        runJob?.cancel()
        isRunning = false
        audioManager.playSfx(audioManager.idButton)
        playerPos = initialPlayerState.startPos
        strawberries = initialPlayerState.strawberries
        scope.launch {
            animX.snapTo(playerPos.x.toFloat())
            animY.snapTo(playerPos.y.toFloat())
        }
        statusText = "Reset"
    }

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        // Maze area with dynamic sizing and gradient background
        BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.Transparent)
                ) {
            // Explicitly use the BoxWithConstraints scope properties
            val scopeMaxWidth = this.maxWidth
            val scopeMaxHeight = this.maxHeight

            val cols = maze.width
            val rows = maze.height

            // compute cell size in Dp (use min to fit both dimensions)
            val cellSize: Dp = minOf(scopeMaxWidth / cols, scopeMaxHeight / rows)

            // convert to pixels once
            val cellPx = with(LocalDensity.current) { cellSize.toPx() }
            val gridWidthPx = cols * cellPx
            val gridHeightPx = rows * cellPx

            // compute top-left offsets (in pixels) to center the grid inside the BoxWithConstraints
            val offsetX = (with(LocalDensity.current) { scopeMaxWidth.toPx() } - gridWidthPx) / 2f
            val offsetY = (with(LocalDensity.current) { scopeMaxHeight.toPx() } - gridHeightPx) / 2f

            // gradient background behind the maze
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.verticalGradient(listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))))
            )

            // draw cells and walls using the same offsets
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (y in 0 until rows) {
                    for (x in 0 until cols) {
                        val left = offsetX + x * cellPx
                        val top = offsetY + y * cellPx
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left, top),
                            size = androidx.compose.ui.geometry.Size(cellPx, cellPx),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                        )
                    }
                }

                // walls drawn as grey blocks
                maze.walls.forEach { p ->
                    val left = offsetX + p.x * cellPx
                    val top = offsetY + p.y * cellPx
                    drawRect(
                        color = Color(0xFF9E9E9E),
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                    )
                }
            }

            // overlay sprites using computed offsets and scaled sprite size
            val spriteSize = cellSize * 0.9f

            // strawberries
            maze.strawberries.forEach { pos ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (offsetX + pos.x * cellPx).roundToInt(),
                                (offsetY + pos.y * cellPx).roundToInt()
                            )
                        }
                        .size(spriteSize)
                ) {
                    StrawberrySprite(frames = strawberryFrames, cellSize = spriteSize)
                }
            }

            // enemies
            maze.enemies.forEach { pos ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (offsetX + pos.x * cellPx).roundToInt(),
                                (offsetY + pos.y * cellPx).roundToInt()
                            )
                        }
                        .size(spriteSize)
                ) {
                    EnemySprite(frames = enemyFrames, cellSize = spriteSize)
                }
            }

            // player sprite positioned from animX/animY
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (offsetX + animX.value * cellPx).roundToInt(),
                            (offsetY + animY.value * cellPx).roundToInt()
                        )
                    }
                    .size(spriteSize)
            ) {
                PlayerSprite(
                    state = playerAnimState,
                    idleFrames = playerIdleFrames,
                    runFrames = playerRunFrames,
                    jumpFrames = playerJumpFrames,
                    dropFrames = playerDropFrames,
                    hitFrames = playerHitFrames,
                    cellSize = spriteSize,
                    x = animX.value,
                    y = animY.value
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        // Bottom controls: palette, program boxes, cloud buttons
        Row(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            // Palette column (click-to-add)
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Commands", modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn {
                    items(palette.size) { idx ->
                        val cmd = palette[idx]
                        Card(modifier = Modifier
                            .padding(8.dp)
                            .size(64.dp)
                            .clickable(enabled = audioReady) {
                                program = program + cmd
                                audioManager.playSfx(audioManager.idButton)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (cmd) {
                                        is Command.MoveLeft -> "←"
                                        is Command.MoveRight -> "→"
                                        is Command.MoveUp -> "↑"
                                        is Command.MoveDown -> "↓"
                                        else -> "?"
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Program column shows sequence boxes
            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Program", modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(program.size) { i ->
                        val c = program[i]
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                                Text("${i + 1}.", modifier = Modifier.width(28.dp))
                                Text(text = when (c) {
                                    is Command.MoveLeft -> "Move Left"
                                    is Command.MoveRight -> "Move Right"
                                    is Command.MoveUp -> "Move Up"
                                    is Command.MoveDown -> "Move Down"
                                    is Command.Loop -> "Loop"
                                    else -> "Cmd"
                                }, modifier = Modifier.weight(1f))
                                IconButton(onClick = { program = program.toMutableList().also { it.removeAt(i) } }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
            }

            // Controls column with cloud buttons
            Column(
                modifier = Modifier
                    .weight(0.20f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CloudButtonTwo(text = "Play", modifier = Modifier, onClick = { if (audioReady) startExecution() })
                CloudButtonTwo(text = "Reset", modifier = Modifier, onClick = { resetLevel() })
                CloudButtonTwo(text = "Clear", modifier = Modifier, onClick = { program = emptyList(); audioManager.playSfx(audioManager.idButton) })


                Spacer(modifier = Modifier.height(8.dp))
                Text("Lives: $strawberries")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { audioManager.playSfx(audioManager.idButton); onExit() }) { Text("Exit") }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(statusText, modifier = Modifier.padding(8.dp))
    }
}

