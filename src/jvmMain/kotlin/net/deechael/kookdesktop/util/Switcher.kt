@file:Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")

package net.deechael.kookdesktop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

class Switcher(val controller: Controller) {

    @Composable
    fun show() {
        val current by rememberSaveable {
            this.controller.where
        }
        val function: @Composable () -> Unit = if (current == "" && this.cases.isNotEmpty())
            this.cases.iterator().next().value.content
        else (if (this.cases.containsKey(current))
            this.cases[current]!!.content
        else
            @Composable {}) as @Composable () -> Unit

        function()
    }

    internal val cases: MutableMap<String, Case> = mutableMapOf()

}

class Case(val name: String, val content: @Composable () -> Unit) {
}

class Controller {

    internal var where = mutableStateOf("")

    fun goto(goto: String) {
        this.where.value = goto
    }

}

fun Switcher.Case(name: String, content: @Composable () -> Unit) {
    this.cases[name] = net.deechael.kookdesktop.util.Case(name, content)
}

@Composable
fun Switcher(currentPage: String = "", controller: Controller = Controller(), body: Switcher.() -> Unit) {
    controller.goto(currentPage)

    val switcher = Switcher(controller)
    switcher.apply(body)
    switcher.show()
}