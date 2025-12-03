package com.individual_project3.kodegame.game

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
data class PlayerState(var pos: Pos, var strawberries: Int = 0, val startPos: Pos)

sealed class Command {
    object MoveUp : Command()
    object MoveDown : Command()
    object MoveLeft : Command()
    object MoveRight : Command()
    data class Loop(val times: Int, val body: List<Command>) : Command()
    data class IfHasStrawberries(val min: Int, val body: List<Command>) : Command()
    object NoOp : Command()
}

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


class CommandEngine(private val maze: Maze,
    private val maxLoopDepth: Int = 64) {
    fun runProgram(
        program: List<Command>,
        initialState: PlayerState,
        scope: CoroutineScope,
        stepDelayMs: Long = 350L,
        onEvent: suspend (ExecEvent) -> Unit
    ): Job {
        return scope.launch(Dispatchers.Default) {
            val state = PlayerState(initialState.pos, initialState.strawberries, initialState.startPos)

            //helper to call onEvent on Main thread
            suspend fun emit(event: ExecEvent){
                withContext(Dispatchers.Main){onEvent(event)}
            }

            //notify start
            emit(ExecEvent.Started(state))

            //recursive executor with depth tracking
            suspend fun executeList(list: List<Command>, depth: Int = 0) {
                if(depth > maxLoopDepth) throw IllegalStateException("Max loop depth exceeded")
                for (c in list) {
                    ensureActive()
                    when (c) {
                        is Command.MoveUp -> stepMove(state, 0, -1, onEvent)
                        is Command.MoveDown -> stepMove(state, 0, 1, onEvent)
                        is Command.MoveLeft -> stepMove(state, -1, 0, onEvent)
                        is Command.MoveRight -> stepMove(state, 1, 0, onEvent)
                        is Command.Loop -> {
                            val times = max(0, c.times)
                            repeat(times) { executeList(c.body) }
                        }
                        is Command.IfHasStrawberries -> {
                            if(state.strawberries >= c.min) executeList(c.body, depth + 1)
                        }
                        is Command.NoOp -> {}
                    }
                    delay(stepDelayMs)
                }
            }
            try {
                executeList(program)
                if (maze.goal != null && state.pos == maze.goal) emit(ExecEvent.Success(state.pos))
                emit(ExecEvent.Finished(state))
            } catch (e: CancellationException) {
                emit(ExecEvent.Log("Execution cancelled"))
            } catch (t: Throwable) {
                emit(ExecEvent.Log("Execution error: ${t.message}"))
            }
        }
    }

    private suspend fun stepMove(state: PlayerState, dx: Int, dy: Int, emit: suspend (ExecEvent) -> Unit) {
        val newX = state.pos.x + dx
        val newY = state.pos.y + dy
        if (newX < 0 || newX >= maze.width || newY < 0 || newY >= maze.height) {
            emit(ExecEvent.Step(state.pos, state.strawberries)); return
        }
        val candidate = Pos(newX, newY)
        if (maze.walls.contains(candidate)) {
            emit(ExecEvent.Step(state.pos, state.strawberries)); return
        }
        state.pos = candidate
        if (maze.strawberries.contains(candidate)) {
            state.strawberries += 1
            emit(ExecEvent.CollectedStrawberry(candidate, state.strawberries))
        }
        if (maze.enemies.contains(candidate)) {
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
