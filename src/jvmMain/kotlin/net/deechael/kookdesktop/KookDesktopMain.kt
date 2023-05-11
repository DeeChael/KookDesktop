package net.deechael.kookdesktop
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import net.deechael.kookdesktop.page.Login
import net.deechael.kookdesktop.util.Case
import net.deechael.kookdesktop.util.Controller
import net.deechael.kookdesktop.util.Switcher

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        val controller = Controller()
        Switcher(
            currentPage = "login",
            controller = controller
        ) {
            Case("login") {
                Login {
                    controller.goto("home")
                }
            }

            Case("home") {

            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kook Desktop"
    ) {
        App()
    }
}
