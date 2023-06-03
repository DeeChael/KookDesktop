package net.deechael.kook.util

object MathUtil {

    fun hexToDigit(string: String): Int {
        val tryConvert = string.toIntOrNull()
        if (tryConvert == null) {
            var total = 0
            var position = 1
            var i = 0
            val reversed = string.reversed()
            while (i < string.length) {
                if (reversed[i] == '-') {
                    if (i != reversed.length - 1) {
                        throw RuntimeException("Unexpected negative signal")
                    }
                }
                total += hexToDigit(reversed[i]) * position
                i++
                position *= 10
            }
            return total
        }
        return tryConvert
    }

    fun hexToDigit(char: Char): Int {
        val tryConvert = char.toString().toIntOrNull()
        if (tryConvert != null) {
            return tryConvert
        }
        return when (char) {
            'a' -> 10
            'b' -> 11
            'c' -> 12
            'd' -> 13
            'e' -> 14
            'f' -> 15
            else -> throw RuntimeException("Invalid hex number")
        }
    }

}