package com.individual_project3.kodegame.assets.commands

import com.individual_project3.kodegame.assets.commands.Direction

sealed class Command {
    data class Move(val dir: Direction) : Command()
    data class Repeat(val times: Int, val body: List<Command>) : Command()
    data class IfHasStrawberries(val min: Int, val body: List<Command>) : Command()
    object NoOp : Command()
}