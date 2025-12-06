package com.individual_project3.kodegame.assets.commands
import com.individual_project3.kodegame.assets.commands.Direction
import kotlinx.coroutines.*
import kotlin.math.max

data class Pos(val x: Int, val y: Int)

data class Maze(
    val width: Int,
    val height: Int,
    val walls: Set<Pos> = emptySet(),
    val enemies: Set<Pos> = emptySet(),
    val strawberries: Set<Pos> = emptySet(),
    val start: Pos = Pos(0, 0),
    val goal: Pos? = null
)

data class PlayerState(
    var pos: Pos,
    var strawberries: Int = 0,
    val startPos: Pos
)

sealed class ExecEvent {
    data class Step(val newPos: Pos, val strawberries: Int) : ExecEvent()
    data class CollectedStrawberry(val pos: Pos, val strawberries: Int) : ExecEvent()
    data class HitEnemyConsumedLife(val pos: Pos, val strawberriesLeft: Int) : ExecEvent()
    data class HitEnemyNoLifeReset(val resetTo: Pos) : ExecEvent()
    data class Success(val pos: Pos) : ExecEvent()
    data class Log(val message: String) : ExecEvent()
    data class Started(val initial: PlayerState) : ExecEvent()
    data class Finished(val final: PlayerState) : ExecEvent()
}

// ---------------------------
// Command execution engine
// ---------------------------

class CommandEngine(
    private val maze: Maze,
    private val maxLoopDepth: Int = 64
) {

    fun runProgram(
        program: List<Command>,
        initialState: PlayerState,
        scope: CoroutineScope,
        stepDelayMs: Long = 350L,
        onEvent: suspend (ExecEvent) -> Unit
    ): Job = scope.launch(Dispatchers.Default) {

        val state = PlayerState(
            pos = initialState.pos,
            strawberries = initialState.strawberries,
            startPos = initialState.startPos
        )

        suspend fun emit(event: ExecEvent) =
            withContext(Dispatchers.Main) { onEvent(event) }

        emit(ExecEvent.Started(state))

        suspend fun movePlayer(dir: Direction) {
            val (dx, dy) = when (dir) {
                Direction.UP -> 0 to -1
                Direction.DOWN -> 0 to 1
                Direction.LEFT -> -1 to 0
                Direction.RIGHT -> 1 to 0
            }
            stepMove(state, dx, dy, ::emit)
        }

        suspend fun executeList(list: List<Command>, depth: Int = 0) {
            if (depth > maxLoopDepth)
                throw IllegalStateException("Max loop depth exceeded")

            for (cmd in list) {
                ensureActive()

                when (cmd) {

                    is Command.Move ->
                        movePlayer(cmd.dir)

                    is Command.Repeat ->
                        repeat(max(0, cmd.times)) {
                            executeList(cmd.body, depth + 1)
                        }

                    is Command.IfHasStrawberries ->
                        if (state.strawberries >= cmd.min) {
                            executeList(cmd.body, depth + 1)
                        }

                    // ----- TEMP: No-op commands -----

                    Command.FunctionStart -> Unit
                    Command.FunctionEnd -> Unit
                    Command.FunctionCall -> Unit

                    is Command.RepeatUntilGoal -> Unit
                    is Command.RepeatWhileHasStrawberry -> Unit

                    Command.NoOp -> Unit
                }


                delay(stepDelayMs)
            }
        }

        try {
            executeList(program)

            if (maze.goal != null && state.pos == maze.goal)
                emit(ExecEvent.Success(state.pos))

            emit(ExecEvent.Finished(state))

        } catch (e: CancellationException) {
            emit(ExecEvent.Log("Execution cancelled"))
        } catch (t: Throwable) {
            emit(ExecEvent.Log("Execution error: ${t.message}"))
        }
    }

    private suspend fun stepMove(
        state: PlayerState,
        dx: Int,
        dy: Int,
        emit: suspend (ExecEvent) -> Unit
    ) {
        val newX = state.pos.x + dx
        val newY = state.pos.y + dy

        if (newX !in 0 until maze.width || newY !in 0 until maze.height) {
            emit(ExecEvent.Step(state.pos, state.strawberries))
            return
        }

        val candidate = Pos(newX, newY)

        if (candidate in maze.walls) {
            emit(ExecEvent.Step(state.pos, state.strawberries))
            return
        }

        state.pos = candidate

        if (candidate in maze.strawberries) {
            state.strawberries += 1
            emit(ExecEvent.CollectedStrawberry(candidate, state.strawberries))
            return
        }

        if (candidate in maze.enemies) {
            if (state.strawberries > 0) {
                state.strawberries -= 1
                emit(ExecEvent.HitEnemyConsumedLife(candidate, state.strawberries))
            } else {
                state.pos = state.startPos
                emit(ExecEvent.HitEnemyNoLifeReset(state.startPos))
            }
        } else {
            emit(ExecEvent.Step(state.pos, state.strawberries))
        }
    }
}
