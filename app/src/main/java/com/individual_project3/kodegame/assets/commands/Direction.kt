package com.individual_project3.kodegame.assets.commands

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun delta(): Pair<Int, Int> = when (this) {
        UP    -> 0 to -1
        DOWN  -> 0 to 1
        LEFT  -> -1 to 0
        RIGHT -> 1 to 0
    }
}

