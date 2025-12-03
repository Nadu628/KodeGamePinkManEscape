package com.individual_project3.kodegame.game


import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MazeViewModel(
    private val mazeModel: MazeModel = MazeModel(),
    private val audioManager: AudioManager
) : ViewModel() {

    // Current maze being played
    val currentMaze = mutableStateOf<Maze?>(null)

    // Player state used by the CommandEngine and UI
    val playerState = mutableStateOf<PlayerState?>(null)

    // Execution log and events for UI display
    val execLog = mutableStateListOf<String>()

    // Whether a program is currently running
    val isProgramRunning = mutableStateOf(false)

    // Loading state while generating a level
    val isLoading = mutableStateOf(false)

    // Job for the currently running CommandEngine program (so we can cancel)
    private var programJob: Job? = null

    // The last used level number
    private var lastLevel = 0

    // Generate the next level asynchronously
    fun generateNextLevel(mode: DifficultyMode = DifficultyMode.EASY) {
        isLoading.value = true
        viewModelScope.launch {
            // Generate level off the UI thread inside MazeModel (MazeModel should be synchronous but fast;
            // if it is heavy, consider exposing a suspend generator or run on Dispatchers.Default)
            val level = mazeModel.nextLevel(mode)
            // Convert MazeLevel -> Maze (your CommandEngine expects Maze). If your MazeModel returns MazeGrid,
            // adapt this conversion to produce the Maze data class used by CommandEngine.
            val maze = convertMazeLevelToMaze(level)
            currentMaze.value = maze
            // Initialize player at maze.start
            val start = maze.start
            playerState.value = PlayerState(pos = start, strawberries = 0, startPos = start)
            execLog.clear()
            isLoading.value = false
            lastLevel++
        }
    }

    // Generate a specific level number
    fun generateLevel(levelNumber: Int, mode: DifficultyMode = DifficultyMode.EASY) {
        isLoading.value = true
        viewModelScope.launch {
            val level = mazeModel.level(levelNumber, mode)
            val maze = convertMazeLevelToMaze(level)
            currentMaze.value = maze
            val start = maze.start
            playerState.value = PlayerState(pos = start, strawberries = 0, startPos = start)
            execLog.clear()
            isLoading.value = false
            lastLevel = levelNumber
        }
    }

    // Convert MazeLevel (from MazeModel) to the Maze data class used by CommandEngine
    // CHANGED: adapt mapping to your Maze/MazeGrid structure. This example maps TileType.REWARD -> strawberries,
    // TileType.WALL -> walls, and enemies/collectibles from MazeLevel.
    private fun convertMazeLevelToMaze(level: MazeLevel): Maze {
        val grid = level.grid
        val walls = mutableSetOf<Pos>()
        val enemies = mutableSetOf<Pos>()
        val strawberries = mutableSetOf<Pos>()
        for (r in 0 until grid.height) {
            for (c in 0 until grid.width) {
                when (grid.tileAt(r, c)) {
                    TileType.WALL -> walls.add(Pos(c, r))
                    TileType.REWARD -> strawberries.add(Pos(c, r))
                    TileType.HAZARD -> enemies.add(Pos(c, r)) // treat hazards as enemies for engine
                    else -> { /* PATH, START, EXIT ignored here */ }
                }
            }
        }
        // find start and goal positions
        val startPair = grid.indexOfStart()
        val exitPair = grid.indexOfExit()
        val startPos = startPair?.let { Pos(it.second, it.first) } ?: Pos(0, 0)
        val goalPos = exitPair?.let { Pos(it.second, it.first) }
        return Maze(
            width = grid.width,
            height = grid.height,
            walls = walls,
            enemies = enemies,
            strawberries = strawberries,
            start = startPos,
            goal = goalPos
        )
    }

    // Start running a program (list of Command) using CommandEngine
    // Cancels any previously running program
    fun runProgram(program: List<Command>, stepDelayMs: Long = 350L) {
        val maze = currentMaze.value ?: run {
            appendLog("No maze loaded")
            return
        }
        val player = playerState.value ?: run {
            appendLog("No player state")
            return
        }

        // Cancel previous run if active
        programJob?.cancel()

        // Create engine for this maze
        val engine = CommandEngine(maze)

        isProgramRunning.value = true
        appendLog("Program started")

        programJob = engine.runProgram(
            program = program,
            initialState = player,
            scope = viewModelScope,
            stepDelayMs = stepDelayMs
        ) { event ->
            // This callback is invoked on Main by the engine (see engine implementation)
            handleExecEvent(event)
        }
    }

    // Cancel the currently running program
    fun cancelProgram() {
        programJob?.cancel()
        programJob = null
        isProgramRunning.value = false
        appendLog("Program cancelled")
    }

    // Handle ExecEvent updates from CommandEngine
    private fun handleExecEvent(event: ExecEvent) {
        when (event) {
            is ExecEvent.Started -> {
                appendLog("Execution started at ${event.initial.pos}")
            }
            is ExecEvent.Step -> {
                // update player position and strawberries
                val ps = playerState.value
                if (ps != null) {
                    ps.pos = event.newPos
                    ps.strawberries = event.strawberries
                    playerState.value = ps
                } else {
                    playerState.value = PlayerState(event.newPos, event.strawberries, event.newPos)
                }
                appendLog("Step to ${event.newPos} (strawberries=${event.strawberries})")
            }
            is ExecEvent.CollectedStrawberry -> {
                val ps = playerState.value
                if (ps != null) {
                    ps.pos = event.pos
                    ps.strawberries = event.strawberries
                    playerState.value = ps
                }
                appendLog("Collected strawberry at ${event.pos} (total=${event.strawberries})")
                // TODO play collect sound via AudioManager if injected
            }
            is ExecEvent.HitEnemyConsumedLife -> {
                val ps = playerState.value
                if (ps != null) {
                    ps.pos = event.pos
                    ps.strawberries = event.strawberriesLeft
                    playerState.value = ps
                }
                appendLog("Hit enemy at ${event.pos}, lost a strawberry (left=${event.strawberriesLeft})")
            }
            is ExecEvent.HitEnemyNoLifeReset -> {
                val ps = playerState.value
                if (ps != null) {
                    ps.pos = event.resetTo
                    playerState.value = ps
                }
                appendLog("Hit enemy with no strawberries, reset to ${event.resetTo}")
            }
            is ExecEvent.Success -> {
                appendLog("Level success at ${event.pos}")
                isProgramRunning.value = false
                // Optionally auto-generate next level
            }
            is ExecEvent.Log -> {
                appendLog("Log: ${event.message}")
            }
            is ExecEvent.Finished -> {
                appendLog("Execution finished")
                isProgramRunning.value = false
            }
        }
    }

    // Small helper to append to execLog
    private fun appendLog(message: String) {
        execLog.add(0, message) // newest first
        // keep log size reasonable
        if (execLog.size > 200) execLog.removeAt(execLog.lastIndex)
    }

    // Convenience functions to move player manually (UI buttons)
    fun movePlayerUp() = movePlayer(0, -1)
    fun movePlayerDown() = movePlayer(0, 1)
    fun movePlayerLeft() = movePlayer(-1, 0)
    fun movePlayerRight() = movePlayer(1, 0)

    private fun movePlayer(dx: Int, dy: Int) {
        val maze = currentMaze.value ?: return
        val ps = playerState.value ?: return
        val newX = ps.pos.x + dx
        val newY = ps.pos.y + dy
        if (newX < 0 || newX >= maze.width || newY < 0 || newY >= maze.height) return
        val candidate = Pos(newX, newY)
        if (maze.walls.contains(candidate)) return
        ps.pos = candidate
        // collect if strawberry
        if (maze.strawberries.contains(candidate)) {
            ps.strawberries += 1
            // Optionally remove strawberry from maze so it can't be recollected
            val newStrawberries = maze.strawberries.toMutableSet().apply { remove(candidate) }
            currentMaze.value = maze.copy(strawberries = newStrawberries)
        }
        // enemy handling
        if (maze.enemies.contains(candidate)) {
            if (ps.strawberries > 0) {
                ps.strawberries -= 1
            } else {
                ps.pos = ps.startPos
            }
        }
        playerState.value = ps
    }

    // Clean up when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        programJob?.cancel()
    }
}

class MazeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // create dependencies here
        val audioManager = AudioManager(context.applicationContext)
        val mazeModel = MazeModel()

        @Suppress("UNCHECKED_CAST")
        return MazeViewModel(mazeModel, audioManager) as T
    }
}

