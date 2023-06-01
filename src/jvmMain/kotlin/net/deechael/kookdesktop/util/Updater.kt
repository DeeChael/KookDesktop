package net.deechael.kookdesktop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

class Updater {

    private val example: @Composable () -> Unit = {}

    internal var current = mutableStateOf(example)

    @Composable
    fun show() {
        val current by rememberSaveable {
            this.current
        }
        current()
    }

    fun update(next: @Composable () -> Unit) {
        this.current.value = next
    }

}

