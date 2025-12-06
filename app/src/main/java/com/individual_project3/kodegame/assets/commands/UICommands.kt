package com.individual_project3.kodegame.assets.commands


// Visual / UI commands that correspond to blocks the child drags
sealed class UiCommand {

    object MoveUp : UiCommand()
    object MoveDown : UiCommand()
    object MoveLeft : UiCommand()
    object MoveRight : UiCommand()

    data class Repeat(val times: Int) : UiCommand()

    object IfHasStrawberry : UiCommand()

    object FunctionStart : UiCommand()
    object FunctionCall : UiCommand()
    object EndFunction : UiCommand()  // optional UI use

    object RepeatUntilGoal : UiCommand()
    object RepeatWhileHasStrawberry : UiCommand()
}




// Map visual blocks to engine commands
fun UiCommand.toEngineCommands(): List<Command> = when (this) {

    UiCommand.MoveUp    -> listOf(Command.Move(Direction.UP))
    UiCommand.MoveDown  -> listOf(Command.Move(Direction.DOWN))
    UiCommand.MoveLeft  -> listOf(Command.Move(Direction.LEFT))
    UiCommand.MoveRight -> listOf(Command.Move(Direction.RIGHT))

    is UiCommand.Repeat -> listOf(
        Command.Repeat(
            times = this.times,
            body = emptyList() // UI does NOT build nested commands yet — safe placeholder
        )
    )

    UiCommand.IfHasStrawberry ->
        listOf(Command.IfHasStrawberries(min = 1, body = emptyList()))

    UiCommand.FunctionStart -> listOf(Command.FunctionStart)
    UiCommand.EndFunction   -> listOf(Command.FunctionEnd)
    UiCommand.FunctionCall  -> listOf(Command.FunctionCall)

    UiCommand.RepeatUntilGoal -> listOf(Command.NoOp) // placeholder
    UiCommand.RepeatWhileHasStrawberry -> listOf(Command.NoOp) // placeholder
}
