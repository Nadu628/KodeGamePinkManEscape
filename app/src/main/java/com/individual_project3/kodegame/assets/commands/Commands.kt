package com.individual_project3.kodegame.assets.commands

sealed class Command {

    data class Move(val dir: Direction) : Command()

    data class Repeat(
        val times: Int,
        val body: List<Command>
    ) : Command()

    data class IfHasStrawberries(
        val min: Int,
        val body: List<Command>
    ) : Command()

    // NEW — Hard-mode commands
    object RepeatUntilGoal : Command()
    object RepeatWhileHasStrawberry : Command()

    // Simple function system
    object FunctionStart : Command()
    object FunctionEnd : Command()
    object FunctionCall : Command()

    object NoOp : Command()
}




object Commands {

    fun parseProgram(input: String): List<Command> {
        val tokens = tokenize(input)
        val (commands, index) = parseCommands(tokens, 0)

        if (index < tokens.size) {
            throw IllegalArgumentException("Unexpected tokens at end: ${tokens.subList(index, tokens.size)}")
        }
        return commands
    }

    private fun tokenize(input: String): List<String> {
        val spaced = input
            .replace("{", " { ")
            .replace("}", " } ")
            .replace(";", " ; ")

        return spaced
            .trim()
            .lowercase()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
    }

    private fun parseCommands(tokens: List<String>, startIndex: Int): Pair<List<Command>, Int> {
        val out = mutableListOf<Command>()
        var i = startIndex

        while (i < tokens.size) {
            when (val token = tokens[i]) {

                "move" -> {
                    val dirToken = tokens.getOrNull(i + 1)
                        ?: throw error("Expected direction after 'move'", i)

                    val dir = parseDirection(dirToken)
                        ?: throw error("Unknown direction '$dirToken'", i)

                    out.add(Command.Move(dir))
                    i += 2
                }

                "repeat" -> {
                    val times = tokens.getOrNull(i + 1)?.toIntOrNull()
                        ?: throw error("Expected integer after 'repeat'", i)

                    val open = tokens.getOrNull(i + 2)
                    if (open != "{") throw error("Expected '{' after 'repeat $times'", i + 2)

                    val (body, next) = parseCommands(tokens, i + 3)
                    out.add(Command.Repeat(times, body))

                    // next should be at closing brace
                    if (tokens.getOrNull(next) != "}") {
                        throw error("Expected '}' to close repeat block", next)
                    }
                    i = next + 1
                }

                "if" -> {
                    val a = tokens.getOrNull(i + 1)
                    val op = tokens.getOrNull(i + 2)
                    val b = tokens.getOrNull(i + 3)

                    if (a == "strawberries" && op == ">=") {
                        val min = b?.toIntOrNull()
                            ?: throw error("Expected number after '>=' in if statement", i + 3)

                        val open = tokens.getOrNull(i + 4)
                        if (open != "{") throw error("Expected '{' after if condition", i + 4)

                        val (body, next) = parseCommands(tokens, i + 5)
                        out.add(Command.IfHasStrawberries(min, body))

                        if (tokens.getOrNull(next) != "}") {
                            throw error("Expected '}' to close if block", next)
                        }
                        i = next + 1
                    } else {
                        throw error("Bad if syntax. Expected: if strawberries >= X { ... }", i)
                    }
                }

                "}" -> return out to i
                ";" -> i++ // ignore semicolons

                else -> throw error("Unexpected token '$token'", i)
            }
        }

        return out to i
    }

    private fun parseDirection(token: String): Direction? = when (token) {
        "up"    -> Direction.UP
        "down"  -> Direction.DOWN
        "left"  -> Direction.LEFT
        "right" -> Direction.RIGHT
        else    -> null
    }

    private fun error(msg: String, index: Int): IllegalArgumentException =
        IllegalArgumentException("$msg at token index $index")
}