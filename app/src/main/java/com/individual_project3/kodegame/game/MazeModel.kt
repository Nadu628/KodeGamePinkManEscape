package com.individual_project3.kodegame.game

import kotlin.random.Random

// Difficulty and learning concepts
enum class DifficultyMode { EASY, HARD }
enum class LearningConcept { LOOPS, CONDITIONALS, FUNCTIONS, VARIABLES, DEBUGGING }

// Tile types
enum class TileType { WALL, PATH, START, EXIT, HAZARD, REWARD }

// Immutable grid
data class MazeGrid(
    val width: Int,
    val height: Int,
    val tiles: List<TileType> // tiles[row * width + col]
) {
    fun tileAt(row: Int, col: Int): TileType {
        require(row in 0 until height && col in 0 until width) { "Out of bounds" }
        return tiles[row * width + col]
    }

    //returns start of tile
    fun indexOfStart(): Pair<Int, Int>? {
        for (r in 0 until height) {
            for (c in 0 until width) {
                if (tileAt(r, c) == TileType.START) return r to c
            }
        }
        return null
    }

    // Returns exit tile or null if none
    fun indexOfExit(): Pair<Int, Int>? {
        for (r in 0 until height) {
            for (c in 0 until width) {
                if (tileAt(r, c) == TileType.EXIT) return r to c
            }
        }
        return null
    }

}

// Level bundle
data class MazeLevel(
    val level: Int,
    val difficulty: DifficultyMode,
    val concepts: List<LearningConcept>,
    val grid: MazeGrid,
    val enemies: List<Enemy>,
    val collectibles: List<Collectible>
)

// Tunable parameters
data class DifficultyParams(
    val width: Int,
    val height: Int,
    val hazardDensity: Double,
    val rewardDensity: Double,
    val enemyCount: Int,
    val branchingFactor: Double,
    val deadEndFavor: Double
)

// Plans learning concepts per level
object LearningGoalPlanner {
    fun goalsFor(level: Int, mode: DifficultyMode): List<LearningConcept> {
        return when (mode) {
            DifficultyMode.EASY -> listOf(
                LearningConcept.VARIABLES,
                LearningConcept.LOOPS,
                LearningConcept.CONDITIONALS
            ).shuffled().take(if (level % 3 == 0) 3 else 2)

            DifficultyMode.HARD -> listOf(
                LearningConcept.FUNCTIONS,
                LearningConcept.LOOPS,
                LearningConcept.CONDITIONALS,
                LearningConcept.DEBUGGING
            ).shuffled().take(3)
        }
    }
}

// Produces parameters from difficulty + level
object DifficultyParamFactory {
    fun create(level: Int, mode: DifficultyMode): DifficultyParams {
        val baseW = if (mode == DifficultyMode.EASY) 15 else 21
        val baseH = if (mode == DifficultyMode.EASY) 11 else 17
        val width = (baseW + (level / 2)).coerceAtMost(if (mode == DifficultyMode.EASY) 25 else 31)
        val height = (baseH + (level / 2)).coerceAtMost(if (mode == DifficultyMode.EASY) 21 else 27)

        return when (mode) {
            DifficultyMode.EASY -> DifficultyParams(
                width = width,
                height = height,
                hazardDensity = 0.02 + (level * 0.002),
                rewardDensity = 0.06 + (level * 0.003),
                enemyCount = (level / 3).coerceAtMost(3),
                branchingFactor = 0.35,
                deadEndFavor = 0.30
            )
            DifficultyMode.HARD -> DifficultyParams(
                width = width,
                height = height,
                hazardDensity = 0.05 + (level * 0.004),
                rewardDensity = 0.03 + (level * 0.002),
                enemyCount = (1 + level / 2).coerceAtMost(7),
                branchingFactor = 0.55,
                deadEndFavor = 0.45
            )
        }
    }
}

// High-level orchestration
class MazeModel(
    private val baseSeed: Long = System.currentTimeMillis()
) {
    private var lastLevelGenerated: Int = 0

    fun nextLevel(mode: DifficultyMode): MazeLevel {
        val level = lastLevelGenerated + 1
        val seed = computeSeedForLevel(level, mode)
        lastLevelGenerated = level
        return generateLevel(level, mode, seed)
    }

    fun level(level: Int, mode: DifficultyMode): MazeLevel {
        val seed = computeSeedForLevel(level, mode)
        return generateLevel(level, mode, seed)
    }

    private fun computeSeedForLevel(level: Int, mode: DifficultyMode): Long {
        return baseSeed xor (level.toLong() shl 16) xor mode.ordinal.toLong()
    }

    private fun generateLevel(level: Int, mode: DifficultyMode, seed: Long): MazeLevel {
        val params = DifficultyParamFactory.create(level, mode)
        val concepts = LearningGoalPlanner.goalsFor(level, mode)

        // Generate topology from the separate generator
        val grid = MazeGenerator.generate(params, seed, concepts)

        // Spawn entities
        val enemies = EnemySpawner.spawn(grid, params, seed)
        val collectibles = CollectibleSpawner.spawn(grid, params, seed, concepts)

        return MazeLevel(
            level = level,
            difficulty = mode,
            concepts = concepts,
            grid = grid,
            enemies = enemies,
            collectibles = collectibles
        )
    }
}

// Enemy placement
object EnemySpawner {
    fun spawn(grid: MazeGrid, params: DifficultyParams, seed: Long): List<Enemy> {
        val rnd = Random(seed xor 0xE11E)
        val enemies = mutableListOf<Enemy>()
        var placed = 0

        for (r in 1 until grid.height - 1) {
            for (c in 1 until grid.width - 1) {
                if (grid.tileAt(r, c) == TileType.PATH && placed < params.enemyCount) {
                    val chance = if (params.enemyCount > 3) 0.09 else 0.04
                    if (rnd.nextDouble() < chance) {
                        enemies.add(
                            Enemy(
                                row = r,
                                col = c,
                                speed = if (params.enemyCount > 3) 1.4f else 1.0f,
                                visionRange = if (params.enemyCount > 3) 5 else 3,
                                patrol = listOf(r to c)
                            )
                        )
                        placed++
                    }
                }
            }
        }
        return enemies
    }
}

// Collectibles placement using learning concepts
object CollectibleSpawner {
    fun spawn(
        grid: MazeGrid,
        params: DifficultyParams,
        seed: Long,
        concepts: List<LearningConcept>
    ): List<Collectible> {
        val rnd = Random(seed xor 0xC011EC7)
        val items = mutableListOf<Collectible>()
        val targetCount = if (LearningConcept.LOOPS in concepts) 6 else 3

        var placed = 0
        for (r in 1 until grid.height - 1) {
            for (c in 1 until grid.width - 1) {
                if (grid.tileAt(r, c) == TileType.REWARD && placed < targetCount) {
                    items.add(
                        Collectible(
                            row = r,
                            col = c,
                            type = CollectibleType.STRAWBERRY,
                            points = 10
                        )
                    )
                    placed++
                }
            }
        }

        while (placed < targetCount) {
            val rr = rnd.nextInt(1, grid.height - 1)
            val cc = rnd.nextInt(1, grid.width - 1)
            if (grid.tileAt(rr, cc) == TileType.PATH) {
                items.add(
                    Collectible(
                        row = rr,
                        col = cc,
                        type = CollectibleType.STRAWBERRY,
                        points = 5
                    )
                )
                placed++
            }
        }

        return items
    }
}