package com.individual_project3.kodegame.game

import java.util.Collections.copy

data class PlayerState(
    val row: Int,
    val col: Int,
    val health: Int = 3,
    val score: Int = 0,
    val collected: MutableList<Collectible> = mutableListOf()
) {
    fun moveTo(r: Int, c: Int): PlayerState = copy(row = r, col = c)
    fun damage(amount: Int = 1): PlayerState = copy(health = (health - amount).coerceAtLeast(0))
    fun addScore(points: Int): PlayerState = copy(score = score + points)
    fun collect(item: Collectible): PlayerState {
        collected.add(item)
        return addScore(item.points)
    }
}