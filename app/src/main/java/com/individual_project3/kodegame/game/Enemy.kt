package com.individual_project3.kodegame.game

data class Enemy(
    val row: Int,
    val col: Int,
    val speed: Float = 1.0f,
    val visionRange: Int = 3,
    val patrol: List<Pair<Int, Int>> = emptyList()
) {
    fun canSee(targetRow: Int, targetCol: Int): Boolean {
        val dr = kotlin.math.abs(targetRow - row)
        val dc = kotlin.math.abs(targetCol - col)
        return dr + dc <= visionRange
    }
}