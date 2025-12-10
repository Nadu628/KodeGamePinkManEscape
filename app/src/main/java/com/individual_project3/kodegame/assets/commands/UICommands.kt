package com.individual_project3.kodegame.assets.commands

// Visual / UI commands that correspond to blocks the child drags
sealed class UiCommand {

    object MoveUp : UiCommand()
    object MoveDown : UiCommand()
    object MoveLeft : UiCommand()
    object MoveRight : UiCommand()

    // repeat N { body }
    data class Repeat(
        val times: Int,
        val body: MutableList<UiCommand> = mutableListOf()
    ) : UiCommand()

    // if has at least 1 strawberry { body }
    data class IfHasStrawberry(
        val body: MutableList<UiCommand> = mutableListOf()
    ) : UiCommand()

    // repeat until goal reached { body }
    data class RepeatUntilGoal(
        val body: MutableList<UiCommand> = mutableListOf()
    ) : UiCommand()

    // while strawberries > 0 { body }
    data class RepeatWhileHasStrawberry(
        val body: MutableList<UiCommand> = mutableListOf()
    ) : UiCommand()

    // function markers in the UI
    object FunctionStart : UiCommand()
    object EndFunction : UiCommand()
    object FunctionCall : UiCommand()

    // explicit function body (for later)
    data class FunctionDefinition(
        val innerBody: MutableList<UiCommand> = mutableListOf()
    ) : UiCommand()
}

fun UiCommand.toEngineCommands(): List<Command> = when (this) {

    UiCommand.MoveUp    -> listOf(Command.Move(Direction.UP))
    UiCommand.MoveDown  -> listOf(Command.Move(Direction.DOWN))
    UiCommand.MoveLeft  -> listOf(Command.Move(Direction.LEFT))
    UiCommand.MoveRight -> listOf(Command.Move(Direction.RIGHT))

    // Control blocks are handled in NestedProgramParser,
    // so we map them to placeholders if ever called directly.
    is UiCommand.Repeat ->
        listOf(Command.Repeat(times = this.times, inner = Command.NoOp))

    is UiCommand.IfHasStrawberry ->
        listOf(Command.IfHasStrawberries(min = 1, inner = Command.NoOp))

    is UiCommand.RepeatUntilGoal ->
        listOf(Command.RepeatUntilGoal(inner = Command.NoOp))

    is UiCommand.RepeatWhileHasStrawberry ->
        listOf(Command.RepeatWhileHasStrawberries(inner = Command.NoOp))

    UiCommand.FunctionCall ->
        listOf(Command.FunctionCall)

    UiCommand.FunctionStart -> emptyList()
    UiCommand.EndFunction   -> emptyList()

    is UiCommand.FunctionDefinition -> {
        val innerBodyCommands = this.innerBody.flatMap { it.toEngineCommands() }
        listOf(Command.FunctionDefinition(inner = innerBodyCommands))
    }
}
