package com.individual_project3.kodegame.game

import kotlin.random.Random
import java.util.ArrayDeque

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

        // Start carving from top-left-ish
        val startR = if (h % 2 == 0) 1 else 0
        val startC = if (w % 2 == 0) 1 else 0

        // Helper: neighbors 2 steps away
        fun neighborsTwoSteps(cell: Cell): List<Cell> {
            val dirs = listOf(
                Cell(cell.r - 2, cell.c),
                Cell(cell.r + 2, cell.c),
                Cell(cell.r, cell.c - 2),
                Cell(cell.r, cell.c + 2)
            )
            return dirs.filter { inBounds(it.r, it.c) }
        }

        // Weighted choice for DFS carving
        fun pickNext(stackTop: Cell): Cell? {
            val options = neighborsTwoSteps(stackTop).filter {
                tiles[idx(it.r, it.c)] == TileType.WALL
            }
            if (options.isEmpty()) return null

            val weighted = options.map {
                val around = neighborsTwoSteps(it).count { n ->
                    tiles[idx(n.r, n.c)] == TileType.PATH
                }
                val branchWeight = params.branchingFactor * (1 + around)
                val deadEndWeight = params.deadEndFavor * (1 + (2 - around).coerceAtLeast(0))
                it to (branchWeight + deadEndWeight)
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

        // Depth-first carving - this produces a maze "tree"
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

        // Collect all path cells
        val pathCells = buildList {
            for (r in 0 until h) {
                for (c in 0 until w) {
                    if (tiles[idx(r, c)] == TileType.PATH) add(Cell(r, c))
                }
            }
        }

        // BFS to find a "far" EXIT for nicer path length
        fun bfsFarthest(start: Cell): Cell {
            val visited = Array(h) { BooleanArray(w) { false } }
            val queue = ArrayDeque<Pair<Cell, Int>>()
            queue.add(start to 0)
            visited[start.r][start.c] = true

            var farthest = start
            var maxDist = 0

            while (queue.isNotEmpty()) {
                val (current, dist) = queue.removeFirst()
                if (dist > maxDist) {
                    maxDist = dist
                    farthest = current
                }
                val neighbors = listOf(
                    Cell(current.r - 1, current.c),
                    Cell(current.r + 1, current.c),
                    Cell(current.r, current.c - 1),
                    Cell(current.r, current.c + 1)
                ).filter {
                    inBounds(it.r, it.c) &&
                            !visited[it.r][it.c] &&
                            tiles[idx(it.r, it.c)] == TileType.PATH
                }

                for (n in neighbors) {
                    visited[n.r][n.c] = true
                    queue.add(n to dist + 1)
                }
            }

            return farthest
        }

        // START and EXIT: always on PATH, always connected
        val start = pathCells.firstOrNull() ?: Cell(startR, startC)
        val exit = if(params.shortExit){
            pathCells.firstOrNull{cell ->
                val dist = kotlin.math.abs(cell.r - start.r) + kotlin.math.abs(cell.c - start.c)
                dist in 4..(w+h)/3
            } ?: start
        }else{
            bfsFarthest(start)
        }
        tiles[idx(start.r, start.c)] = TileType.START
        tiles[idx(exit.r, exit.c)] = TileType.EXIT

        // Place hazards / rewards only on PATH tiles (never on walls)
        fun sprinkle(type: TileType, density: Double) {
            val candidates = pathCells
                .filter {
                    val t = tiles[idx(it.r, it.c)]
                    t == TileType.PATH && it != start && it != exit
                }
                .shuffled(rnd)

            val count = (candidates.size * density).toInt()
            for (i in 0 until count.coerceAtMost(candidates.size)) {
                val cell = candidates[i]
                tiles[idx(cell.r, cell.c)] = type
            }
        }

        // Small boosts based on concepts (LOOPS → more rewards, CONDITIONALS → more hazards)
        val hazardBoost = if (LearningConcept.CONDITIONALS in concepts) 0.01 else 0.0
        val rewardBoost = if (LearningConcept.LOOPS in concepts) 0.01 else 0.0

        sprinkle(
            TileType.HAZARD,
            (params.hazardDensity + hazardBoost).coerceAtMost(
                if (params.enemyCount <= 1) 0.10 else 0.20
            )
        )
        sprinkle(
            TileType.REWARD,
            (params.rewardDensity + rewardBoost).coerceAtMost(0.25)
        )

        return MazeGrid(width = w, height = h, tiles = tiles)
    }
}