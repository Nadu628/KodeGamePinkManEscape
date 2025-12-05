package com.individual_project3.kodegame.assets.commands


// Visual / UI commands that correspond to blocks the child drags
sealed class UiCommand {
    object MoveUp : UiCommand()
    object MoveDown : UiCommand()
    object MoveLeft : UiCommand()
    object MoveRight : UiCommand()
    // later: Loop, If, Function, etc.
}

// Map visual blocks to engine commands
fun UiCommand.toEngineCommands(): List<Command> = when (this) {
    UiCommand.MoveUp    -> listOf(Command.Move(Direction.UP))
    UiCommand.MoveDown  -> listOf(Command.Move(Direction.DOWN))
    UiCommand.MoveLeft  -> listOf(Command.Move(Direction.LEFT))
    UiCommand.MoveRight -> listOf(Command.Move(Direction.RIGHT))
}