package com.individual_project3.kodegame.game

import kotlin.random.Random

object MazeGenerator {

    private data class Cell(val r: Int, val c: Int)

    fun generate(
        params: DifficultyParams,
        seed: Long,
        concepts: List<LearningConcept>
    ): MazeGrid {
        val rnd = Random(seed)
        val w = params.width
        val h = params.height

        // Initialize walls
        val tiles = MutableList(w * h) { TileType.WALL }

        fun inBounds(r: Int, c: Int) = r in 0 until h && c in 0 until w
        fun idx(r: Int, c: Int) = r * w + c
        fun setPath(r: Int, c: Int) { tiles[idx(r, c)] = TileType.PATH }

        // Start positions for even/odd dimensions
        val startR = if (h % 2 == 0) 1 else 0
        val startC = if (w % 2 == 0) 1 else 0

        fun neighborsTwoSteps(cell: Cell): List<Cell> {
            val dirs = listOf(
                Cell(cell.r - 2, cell.c),
                Cell(cell.r + 2, cell.c),
                Cell(cell.r, cell.c - 2),
                Cell(cell.r, cell.c + 2)
            )
            return dirs.filter { inBounds(it.r, it.c) }
        }

        // Bias next carve by branchingFactor and deadEndFavor
        fun pickNext(stackTop: Cell): Cell? {
            val options = neighborsTwoSteps(stackTop).filter { tiles[idx(it.r, it.c)] == TileType.WALL }
            if (options.isEmpty()) return null
            val weighted = options.map {
                val around = neighborsTwoSteps(it).count { n -> tiles[idx(n.r, n.c)] == TileType.PATH }
                val branchWeight = params.branchingFactor * (1 + around)
                val deadEndWeight = params.deadEndFavor * (1 + (2 - around).coerceAtLeast(0))
                val weight = branchWeight + deadEndWeight
                it to weight
            }
            val sum = weighted.sumOf { it.second }
            val roll = rnd.nextDouble() * sum
            var acc = 0.0
            for ((cell, wgt) in weighted) {
                acc += wgt
                if (roll <= acc) return cell
            }
            return weighted.last().first
        }

        // DFS carve
        val stack = ArrayDeque<Cell>()
        val startCell = Cell(startR, startC)
        setPath(startR, startC)
        stack.add(startCell)

        while (stack.isNotEmpty()) {
            val current = stack.last()
            val next = pickNext(current)
            if (next == null) {
                stack.removeLast()
            } else {
                val midR = (current.r + next.r) / 2
                val midC = (current.c + next.c) / 2
                setPath(midR, midC)
                setPath(next.r, next.c)
                stack.add(next)
            }
        }

        // Gather path cells
        val pathCells = buildList {
            for (r in 0 until h) for (c in 0 until w)
                if (tiles[idx(r, c)] == TileType.PATH) add(Cell(r, c))
        }

        // Place START and EXIT on extremes
        if (pathCells.isNotEmpty()) {
            val start = pathCells.first()
            val exit = pathCells.last()
            tiles[idx(start.r, start.c)] = TileType.START
            tiles[idx(exit.r, exit.c)] = TileType.EXIT
        }

        // Sprinkle hazards and rewards
        fun sprinkle(type: TileType, density: Double) {
            val candidates = pathCells.shuffled(rnd).filter {
                val t = tiles[idx(it.r, it.c)]
                t == TileType.PATH
            }
            val count = (candidates.size * density).toInt()
            for (i in 0 until count) {
                val cell = candidates[i]
                tiles[idx(cell.r, cell.c)] = type
            }
        }

        // Concept-driven micro adjustments
        val hazardBoost = if (LearningConcept.CONDITIONALS in concepts) 0.01 else 0.0
        val rewardBoost = if (LearningConcept.LOOPS in concepts) 0.01 else 0.0

        sprinkle(TileType.HAZARD, (params.hazardDensity + hazardBoost).coerceAtMost(0.20))
        sprinkle(TileType.REWARD, (params.rewardDensity + rewardBoost).coerceAtMost(0.25))

        return MazeGrid(width = w, height = h, tiles = tiles)
    }
}