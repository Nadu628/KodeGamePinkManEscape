package com.individual_project3.kodegame.assets.commands

object Commands {
    fun parseProgram(input: String): List<Command> {
        val tokens = tokenize(input)
        val (commands, _) = parseCommands(tokens, 0)
        return commands
    }

    private fun tokenize(input: String): List<String> {
        return input.trim().lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    }

    private fun parseCommands(tokens: List<String>, startIndex: Int): Pair<List<Command>, Int> {
        val out = mutableListOf<Command>()
        var i = startIndex
        while (i < tokens.size) {
            when (tokens[i]) {
                "move" -> {
                    val dirToken = tokens.getOrNull(i + 1) ?: break
                    val dir = when (dirToken) {
                        "up" -> Direction.UP
                        "down" -> Direction.DOWN
                        "left" -> Direction.LEFT
                        "right" -> Direction.RIGHT
                        else -> null
                    }
                    if (dir != null) {
                        out.add(Command.Move(dir))
                        i += 2
                    } else break
                }
                "repeat" -> {
                    val times = tokens.getOrNull(i + 1)?.toIntOrNull() ?: break
                    val (body, next) = parseCommands(tokens, i + 2)
                    out.add(Command.Repeat(times, body))
                    i = next
                }
                "if" -> {
                    if (tokens.getOrNull(i + 1) == "strawberries" && tokens.getOrNull(i + 2) == ">=") {
                        val threshold = tokens.getOrNull(i + 3)?.toIntOrNull() ?: break
                        val (body, next) = parseCommands(tokens, i + 4)
                        out.add(Command.IfHasStrawberries(threshold, body))
                        i = next
                    } else break
                }
                ";" -> { i++; break }
                else -> break
            }
        }
        return out to i
    }
}
