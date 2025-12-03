package com.individual_project3.kodegame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.sprites.SpriteManager
import com.individual_project3.kodegame.game.MazeGrid
import com.individual_project3.kodegame.game.MazeRendererWithSprites
import com.individual_project3.kodegame.game.MazeViewModel
import com.individual_project3.kodegame.game.PlayerAnimState
import com.individual_project3.kodegame.game.MazeLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun GameScreen() {
    val context = LocalContext.current
    val vm: MazeViewModel = viewModel(factory = MazeViewModel.Factory(context))
    val spriteManager = remember { SpriteManager(context) }
    val scope = rememberCoroutineScope()

    // UI-only state
    var programText by remember { mutableStateOf("") }

    // Animatables for smooth movement
    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }

    // CHANGED: frame index for player animation cycling
    var playerFrameIndex by remember { mutableStateOf(0) }

    // Preload sprites
    LaunchedEffect(Unit) { spriteManager.preloadAllAsync(scope) }

    // Animate when VM playerState changes
    val playerState = vm.playerState.value
    LaunchedEffect(playerState?.pos) {
        val pos = playerState?.pos ?: return@LaunchedEffect
        launch { animX.animateTo(pos.x.toFloat()) }
        launch { animY.animateTo(pos.y.toFloat()) }
    }

    // CHANGED: cycle frames based on vm.playerAnimState, with special timing for Hit
    LaunchedEffect(vm.playerAnimState.value) {
        playerFrameIndex = 0
        val state = vm.playerAnimState.value
        val frameCount = when (state) {
            PlayerAnimState.Jump -> spriteManager.playerJumpFrames.size
            PlayerAnimState.Drop -> spriteManager.playerDropFrames.size
            PlayerAnimState.Run -> spriteManager.playerRunFrames.size
            PlayerAnimState.Hit -> spriteManager.playerHitFrames.size
            else -> spriteManager.playerIdleFrames.size
        }.coerceAtLeast(1)

        val frameDuration = when (state) {
            PlayerAnimState.Hit -> 140L
            PlayerAnimState.Jump, PlayerAnimState.Drop -> 120L
            PlayerAnimState.Run -> 80L
            else -> 140L
        }

        while (vm.playerAnimState.value == state) {
            playerFrameIndex = (playerFrameIndex + 1) % frameCount
            delay(frameDuration)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { vm.generateNextLevel() }) { Text("New Level") }
            Spacer(modifier = Modifier.width(8.dp))
            if (!vm.isProgramRunning.value) {
                Button(onClick = {
                    val parsed = vm.parseProgramFromText(programText)
                    vm.setLastProgram(parsed)
                    vm.runLastProgram()
                }) { Text("Play") }
            } else {
                Button(onClick = { vm.cancelProgram() }) { Text("Stop") }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        vm.currentMaze.value?.let { level ->
            val playerImage: ImageBitmap? = when (vm.playerAnimState.value) {
                PlayerAnimState.Jump -> spriteManager.playerJumpFrames.getOrNull(playerFrameIndex)
                PlayerAnimState.Drop -> spriteManager.playerDropFrames.getOrNull(playerFrameIndex)
                PlayerAnimState.Run -> spriteManager.playerRunFrames.getOrNull(playerFrameIndex)
                PlayerAnimState.Hit -> spriteManager.playerHitFrames.getOrNull(playerFrameIndex)
                else -> spriteManager.playerIdleFrames.getOrNull(playerFrameIndex)
            }

            MazeRendererWithSprites(
                maze = level,
                playerImage = playerImage,
                playerAnimX = animX.value,
                playerAnimY = animY.value,
                spriteManager = spriteManager
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No level loaded")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(value = programText, onValueChange = { programText = it }, modifier = Modifier.weight(1f).background(Color.White))
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val parsed = vm.parseProgramFromText(programText)
                    vm.setLastProgram(parsed)
                }) { Text("Set Program") }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Execution Log")
                vm.execLog.take(8).forEach { Text(it) }
            }
        }
    }
}
