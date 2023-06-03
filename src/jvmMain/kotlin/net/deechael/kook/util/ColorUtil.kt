package net.deechael.kook.util

import androidx.compose.ui.graphics.Color

object ColorUtil {

    fun hexToRgb(hex: String): Color {
        return Color(MathUtil.hexToDigit(hex))
    }

}