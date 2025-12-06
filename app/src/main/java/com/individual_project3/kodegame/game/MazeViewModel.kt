package com.individual_project3.kodegame.game

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.commands.PlayerState
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.commands.Command
import com.individual_project3.kodegame.assets.commands.CommandEngine
import com.individual_project3.kodegame.assets.commands.Commands
import com.individual_project3.kodegame.assets.commands.ExecEvent
import com.individual_project3.kodegame.assets.commands.Pos
import com.individual_project3.kodegame.assets.commands.UiCommand
import com.individual_project3.kodegame.assets.commands.toEngineCommands
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.data.progress.ChildProgressEntity

enum class PlayerAnimState { Idle, Run, Jump, Drop, Hit }

class MazeViewModel(
    private val mazeModel: MazeModel,
    val audioManager: AudioManager
) : ViewModel() {

    val currentMaze = mutableStateOf<Maze?>(null)
    val playerState = mutableStateOf<PlayerState?>(null)
    val execLog = mutableStateListOf<String>()
    val isProgramRunning = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val playerAnimState = mutableStateOf(PlayerAnimState.Idle)
    val firstPositionSyncedFlag = mutableStateOf(false)
    private var functionBody: List<Command> = emptyList()
    val totalStrawberries = mutableStateOf(0)
    val levelsCompleted = mutableStateOf(0)



    var lastProgram: List<Command> = emptyList()
        private set

    val uiProgram = mutableStateListOf<UiCommand>()

    private var programJob: Job? = null
    private var animResetJob: Job? = null

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val audio = AudioManager(context.applicationContext)

            audio.loadSfx(
                R.raw.sfx_jump,
                R.raw.sfx_drop,
                R.raw.sfx_hit,
                R.raw.sfx_collecting_fruit,
                R.raw.sfx_success
            )

            val model = MazeModel()
            @Suppress("UNCHECKED_CAST")
            return MazeViewModel(model, audio) as T
        }
    }

    fun parseProgramFromText(text: String): List<Command> = Commands.parseProgram(text)

    fun startBackgroundMusic(@RawRes musicRes: Int) {
        audioManager.startBackground(musicRes)
    }

    fun stopBackgroundMusic() {
        audioManager.stopBackground()
    }

    fun addUiCommand(cmd: UiCommand) {
        uiProgram.add(cmd)
    }

    fun removeUiCommandAt(index: Int) {
        if (index in uiProgram.indices) uiProgram.removeAt(index)
    }

    fun runUiProgram(stepDelayMs: Long = 350L) {
        if (uiProgram.isEmpty()) {
            appendLog("Program is empty")
            return
        }
        val engineProgram = uiProgram.flatMap { it.toEngineCommands() }
        setLastProgram(engineProgram)
        runLastProgram(stepDelayMs)
    }

    fun setLastProgram(program: List<Command>) {
        // Extract function if present
        val extracted = extractFunction(program)
        functionBody = extracted.functionBody
        lastProgram = extracted.mainProgram
    }

    private data class ExtractedProgram(
        val mainProgram: List<Command>,
        val functionBody: List<Command>
    )

    private fun extractFunction(program: List<Command>): ExtractedProgram {
        val main = mutableListOf<Command>()
        val func = mutableListOf<Command>()

        var collectingFunction = false

        for (cmd in program) {
            when (cmd) {
                Command.FunctionStart -> {
                    collectingFunction = true
                    func.clear()
                }

                Command.FunctionEnd -> {
                    collectingFunction = false
                }

                is Command.FunctionCall -> {
                    main.add(cmd)
                }

                else -> {
                    if (collectingFunction) func.add(cmd)
                    else main.add(cmd)
                }
            }
        }

        return ExtractedProgram(mainProgram = main, functionBody = func)
    }




    fun runLastProgram(stepDelayMs: Long = 350L) {
        if (lastProgram.isEmpty()) {
            appendLog("No program set")
            return
        }
        runProgram(lastProgram, stepDelayMs)
    }

    // ------- LEVEL / MAZE -------

    fun generateNextLevel(mode: DifficultyMode = DifficultyMode.EASY) {
        isLoading.value = true
        viewModelScope.launch {
            val level = withContext(Dispatchers.Default) { mazeModel.nextLevel(mode) }
            val maze = convertMazeLevelToMaze(level)
            currentMaze.value = maze

            val startPos = maze.start
            playerState.value = PlayerState(
                pos = startPos,
                strawberries = 0,
                startPos = startPos
            )

            execLog.clear()
            uiProgram.clear()

            playerAnimState.value = PlayerAnimState.Idle

            isLoading.value = false
        }
    }

    private fun convertMazeLevelToMaze(level: MazeLevel): Maze {
        val grid = level.grid

        val walls = mutableSetOf<Pos>()
        val enemies = mutableSetOf<Pos>()
        val strawberries = mutableSetOf<Pos>()

        for (r in 0 until grid.height) {
            for (c in 0 until grid.width) {
                val pos = Pos(x = c, y = r)
                when (grid.tileAt(r, c)) {
                    TileType.WALL   -> walls.add(pos)
                    TileType.HAZARD -> enemies.add(pos)
                    TileType.REWARD -> strawberries.add(pos)
                    else -> {}
                }
            }
        }

        val startPair = grid.indexOfStart()
        val exitPair = grid.indexOfExit()

        val startPos = startPair?.let { Pos(x = it.second, y = it.first) } ?: Pos(0, 0)
        val goalPos  = exitPair ?.let { Pos(x = it.second, y = it.first) }

        // --- Build initial maze ---
        var maze = Maze(
            width = grid.width,
            height = grid.height,
            walls = walls,
            enemies = enemies,
            strawberries = strawberries,
            start = startPos,
            goal = goalPos
        )

        // --- BFS helper to get neighbors ---
        fun neighborsOf(p: Pos): List<Pos> =
            listOf(
                Pos(p.x + 1, p.y),
                Pos(p.x - 1, p.y),
                Pos(p.x, p.y + 1),
                Pos(p.x, p.y - 1)
            ).filter {
                it.x in 0 until maze.width &&
                        it.y in 0 until maze.height &&
                        it !in maze.walls
            }

        // --- BFS to extract the main solution path ---
        fun bfsPath(start: Pos, goal: Pos?): List<Pos> {
            if (goal == null) return emptyList()

            val queue = ArrayDeque<Pos>()
            val cameFrom = mutableMapOf<Pos, Pos?>()

            queue.add(start)
            cameFrom[start] = null

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current == goal) break

                for (n in neighborsOf(current)) {
                    if (n !in cameFrom) {
                        cameFrom[n] = current
                        queue.add(n)
                    }
                }
            }

            if (goal !in cameFrom) return emptyList()

            val path = mutableListOf<Pos>()
            var cur: Pos? = goal
            while (cur != null) {
                path.add(cur)
                cur = cameFrom[cur]
            }
            return path.reversed()
        }

        val mainPath = bfsPath(startPos, goalPos)

        // --- Expand safe zone around solution path ---
        val safeZone = mainPath.flatMap { p ->
            listOf(
                p,
                Pos(p.x + 1, p.y),
                Pos(p.x - 1, p.y),
                Pos(p.x, p.y + 1),
                Pos(p.x, p.y - 1)
            )
        }.toSet()

        // --- Remove enemies that sit on or near the correct path ---
        val filteredEnemies = enemies.filterNot { it in safeZone }.toSet()

        // --- Return cleaned maze ---
        return maze.copy(enemies = filteredEnemies)
    }

    private fun computeMainPath(
        width: Int,
        height: Int,
        walls: Set<Pos>,
        start: Pos,
        goal: Pos?
    ): Set<Pos> {
        if (goal == null) return emptySet()

        val directions = listOf(
            Pos(0, -1), // up
            Pos(0, 1),  // down
            Pos(-1, 0), // left
            Pos(1, 0)   // right
        )

        val visited = Array(height) { BooleanArray(width) }
        val parent = mutableMapOf<Pos, Pos?>()

        val queue = ArrayDeque<Pos>()
        queue.add(start)
        visited[start.y][start.x] = true
        parent[start] = null

        var found = false

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == goal) {
                found = true
                break
            }

            for (d in directions) {
                val nx = current.x + d.x
                val ny = current.y + d.y

                if (nx !in 0 until width || ny !in 0 until height) continue
                if (visited[ny][nx]) continue

                val next = Pos(nx, ny)
                if (next in walls) continue

                visited[ny][nx] = true
                parent[next] = current
                queue.add(next)
            }
        }

        if (!found) return emptySet()

        // Reconstruct path from goal → start
        val path = mutableSetOf<Pos>()
        var cur: Pos? = goal
        while (cur != null) {
            path.add(cur)
            cur = parent[cur]
        }
        return path
    }

    // ------- PROGRAM EXECUTION -------

    fun runProgram(program: List<Command>, stepDelayMs: Long = 350L) {
        val maze = currentMaze.value ?: run { appendLog("No maze loaded"); return }
        val player = playerState.value ?: run { appendLog("No player state"); return }

        programJob?.cancel()
        val engine = CommandEngine(maze)

        isProgramRunning.value = true
        appendLog("Program started")

        programJob = engine.runProgram(
            program = program,
            initialState = player,
            scope = viewModelScope,
            stepDelayMs = stepDelayMs
        ) { event ->
            handleExecEvent(event)
        }
    }

    fun cancelProgram() {
        programJob?.cancel()
        programJob = null
        isProgramRunning.value = false
        appendLog("Program cancelled")
    }

    private fun handleExecEvent(event: ExecEvent) {
        when (event) {
            is ExecEvent.Started -> {
                appendLog("Execution started at ${event.initial.pos}")
                playerAnimState.value = PlayerAnimState.Idle
            }

            is ExecEvent.Step -> {
                Log.d("MazeVM", "Step to ${event.newPos}")
                val prev = playerState.value?.pos
                val newPos = event.newPos
                val ps = playerState.value
                if (ps != null) {
                    playerState.value = ps.copy(
                        pos = newPos,
                        strawberries = event.strawberries
                    )
                } else {
                    playerState.value = PlayerState(newPos, event.strawberries, newPos)
                }

                val dy = if (prev != null) newPos.y - prev.y else 0
                when {
                    dy < 0 -> {
                        setPlayerAnimStateWithTimeout(
                            PlayerAnimState.Jump,
                            revertTo = PlayerAnimState.Run,
                            timeoutMs = 300L
                        )
                        audioManager.play(R.raw.sfx_jump)
                    }
                    dy > 0 -> {
                        setPlayerAnimStateWithTimeout(
                            PlayerAnimState.Drop,
                            revertTo = PlayerAnimState.Run,
                            timeoutMs = 300L
                        )
                        audioManager.play(R.raw.sfx_drop)
                    }
                    else -> {
                        setPlayerAnimStateWithTimeout(
                            PlayerAnimState.Run,
                            revertTo = PlayerAnimState.Idle,
                            timeoutMs = 250L
                        )
                    }
                }

                appendLog("Step to ${event.newPos} (strawberries=${event.strawberries})")
            }

            is ExecEvent.CollectedStrawberry -> {
                val ps = playerState.value
                totalStrawberries.value += 1
                if (ps != null) {
                    playerState.value = ps.copy(
                        pos = event.pos,
                        strawberries = event.strawberries
                    )
                }

                //remove berry from Maze object
                currentMaze.value?.let { maze ->
                    val updated = maze.strawberries.toMutableSet()
                    updated.remove(event.pos)
                    currentMaze.value = maze.copy(strawberries = updated)
                }

                appendLog("Collected strawberry at ${event.pos}")
                audioManager.play(R.raw.sfx_collecting_fruit)

                setPlayerAnimStateWithTimeout(
                    PlayerAnimState.Run,
                    revertTo = PlayerAnimState.Idle,
                    timeoutMs = 300L
                )
            }

            is ExecEvent.HitEnemyConsumedLife -> {
                val ps = playerState.value
                if (ps != null) {
                    playerState.value = ps.copy(
                        pos = event.pos,
                        strawberries = event.strawberriesLeft
                    )
                }
                appendLog("Hit enemy at ${event.pos}, lost a strawberry (left=${event.strawberriesLeft})")
                audioManager.play(R.raw.sfx_hit)
                setPlayerAnimStateWithTimeout(
                    PlayerAnimState.Hit,
                    revertTo = PlayerAnimState.Idle,
                    timeoutMs = 600L
                )
            }

            is ExecEvent.HitEnemyNoLifeReset -> {
                val ps = playerState.value
                if (ps != null) {
                    playerState.value = ps.copy(pos = event.resetTo)
                }
                appendLog("Hit enemy with no strawberries, reset to ${event.resetTo}")
                audioManager.play(R.raw.sfx_hit)
                setPlayerAnimStateWithTimeout(
                    PlayerAnimState.Hit,
                    revertTo = PlayerAnimState.Idle,
                    timeoutMs = 600L
                )
            }

            is ExecEvent.Success -> {
                appendLog("Level success at ${event.pos}")
                levelsCompleted.value += 1
                saveProgressToDatabase()
                isProgramRunning.value = false
                audioManager.play(R.raw.sfx_success)
                playerAnimState.value = PlayerAnimState.Idle
            }

            is ExecEvent.Log -> appendLog("Log: ${event.message}")

            is ExecEvent.Finished -> {
                appendLog("Execution finished")
                isProgramRunning.value = false
                playerAnimState.value = PlayerAnimState.Idle
                firstPositionSyncedFlag.value = false
            }
        }
    }

    fun saveProgressToDatabase(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            val entity = ChildProgressEntity(
                childId = "child1",                  // replace later with real childId
                totalStrawberries = totalStrawberries.value,
                levelsCompleted = levelsCompleted.value
            )

            val db = AppDatabase.getInstance(context)
            db.progressDao().insertRecord(entity)
        }
    }

    fun resetPlayerToStart() {
        val maze = currentMaze.value ?: return
        val ps = playerState.value ?: return

        playerState.value = ps.copy(
            pos = maze.start,
            strawberries = 0
        )
        playerAnimState.value = PlayerAnimState.Idle
    }

    private fun setPlayerAnimStateWithTimeout(
        state: PlayerAnimState,
        revertTo: PlayerAnimState,
        timeoutMs: Long
    ) {
        animResetJob?.cancel()
        playerAnimState.value = state
        animResetJob = viewModelScope.launch {
            delay(timeoutMs)
            if (playerAnimState.value == state) playerAnimState.value = revertTo
            animResetJob = null
        }
    }

    private fun appendLog(message: String) {
        execLog.add(0, message)
        if (execLog.size > 200) execLog.removeAt(execLog.lastIndex)
    }

    override fun onCleared() {
        super.onCleared()
        programJob?.cancel()
        animResetJob?.cancel()
    }
}
