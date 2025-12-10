package com.individual_project3.kodegame.assets.commands

enum class Direction { UP, DOWN, LEFT, RIGHT }

// These are the commands the engine actually understands & executes.
sealed class Command {

    // Move one tile in a direction
    data class Move(val dir: Direction) : Command()

    // repeat N { body }
    data class Repeat(
        val times: Int,
        val inner: Command
    ) : Command()

    // if strawberries >= min { body }
    data class IfHasStrawberries(
        val min: Int,
        val inner: Command
    ) : Command()

    // For now, advanced forms are represented but not deeply used
    data class RepeatUntilGoal(
        val inner: Command
    ) : Command()

    data class RepeatWhileHasStrawberries(
        val inner: Command
    ) : Command()

    // Defines a function body
    data class FunctionDefinition(
        val inner: List<Command>
    ) : Command()

    // Call previously-defined function
    object FunctionCall : Command()

    // Safe no-op (used for unimplemented things)
    object NoOp : Command()
}


object NestedProgramParser {

    fun toEngineCommands(ui: List<UiCommand>): List<Command> {
        val out = mutableListOf<Command>()
        var i = 0

        var collectingFunction = false
        var functionBody = mutableListOf<Command>()

        fun emit(cmd: Command) {
            if (collectingFunction) {
                functionBody.add(cmd)
            } else {
                out.add(cmd)
            }
        }

        while (i < ui.size) {
            val cmd = ui[i]

            when (cmd) {

                // ---------- FUNCTION START ----------
                UiCommand.FunctionStart -> {
                    collectingFunction = true
                    functionBody = mutableListOf()
                    i++  // move past FunctionStart
                }

                // ---------- FUNCTION END ----------
                UiCommand.EndFunction -> {
                    collectingFunction = false
                    emit(Command.FunctionDefinition(functionBody.toList()))
                    functionBody = mutableListOf()
                    i++ // move past EndFunction
                }

                // ---------- FUNCTION CALL ----------
                UiCommand.FunctionCall -> {
                    emit(Command.FunctionCall)
                    i++
                }

                // ---------- REPEAT N (applies to NEXT command only) ----------
                is UiCommand.Repeat -> {
                    val next = ui.getOrNull(i + 1)
                    val inner = next?.toEngineCommands()?.firstOrNull() ?: Command.NoOp

                    emit(Command.Repeat(cmd.times, inner))

                    // Skip over the body command so it isn't also executed separately.
                    // Example: Repeat 3, Down, Right -> Repeat(3, Down), Right
                    i += 2
                }

                // ---------- IF strawberry (NEXT command only) ----------
                is UiCommand.IfHasStrawberry -> {
                    val next = ui.getOrNull(i + 1)
                    val inner = next?.toEngineCommands()?.firstOrNull() ?: Command.NoOp

                    emit(Command.IfHasStrawberries(min = 1, inner = inner))

                    i += 2
                }

                // ---------- UNTIL GOAL (NEXT command only) ----------
                is UiCommand.RepeatUntilGoal -> {
                    val next = ui.getOrNull(i + 1)
                    val inner = next?.toEngineCommands()?.firstOrNull() ?: Command.NoOp

                    emit(Command.RepeatUntilGoal(inner))

                    i += 2
                }

                // ---------- WHILE strawberries > 0 (NEXT command only) ----------
                is UiCommand.RepeatWhileHasStrawberry -> {
                    val next = ui.getOrNull(i + 1)
                    val inner = next?.toEngineCommands()?.firstOrNull() ?: Command.NoOp

                    emit(Command.RepeatWhileHasStrawberries(inner))

                    i += 2
                }

                // ---------- FUNCTION DEFINITION (if ever created directly) ----------
                is UiCommand.FunctionDefinition -> {
                    val innerCommands = cmd.innerBody.flatMap { it.toEngineCommands() }
                    emit(Command.FunctionDefinition(innerCommands))
                    i++
                }

                // ---------- SIMPLE MOVES ----------
                else -> {
                    cmd.toEngineCommands().forEach { emit(it) }
                    i++
                }
            }
        }

        return out
    }
}
