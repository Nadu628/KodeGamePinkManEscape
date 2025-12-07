package com.individual_project3.kodegame.assets.commands

enum class Direction { UP, DOWN, LEFT, RIGHT }

// These are the commands the engine actually understands & executes.
sealed class Command {

    // Move one tile in a direction
    data class Move(val dir: Direction) : Command()

    // repeat N { body }
    data class Repeat(
        val times: Int,
        val body: List<Command>
    ) : Command()

    // if strawberries >= min { body }
    data class IfHasStrawberries(
        val min: Int,
        val body: List<Command>
    ) : Command()

    // For now, advanced forms are represented but not deeply used
    data class RepeatUntilGoal(
        val body: List<Command>
    ) : Command()

    data class RepeatWhileHasStrawberries(
        val body: List<Command>
    ) : Command()

    // Defines a function body
    data class FunctionDefinition(
        val body: List<Command>
    ) : Command()

    // Call previously-defined function
    object FunctionCall : Command()

    // Safe no-op (used for unimplemented things)
    object NoOp : Command()
}



// NestedProgramParser.kt
object NestedProgramParser {
    fun toEngineCommands(ui: List<UiCommand>): List<Command> {
        return ui.flatMap { it.toEngineCommands() }
    }
}
