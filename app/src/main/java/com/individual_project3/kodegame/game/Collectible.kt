package com.individual_project3.kodegame.game

enum class CollectibleType {STRAWBERRY}

data class Collectible(
    val row: Int,
    val col: Int,
    val type: CollectibleType,
    val points: Int = 1
)