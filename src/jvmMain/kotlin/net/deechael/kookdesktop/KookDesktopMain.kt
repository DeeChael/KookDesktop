package net.deechael.kookdesktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import net.deechael.kookdesktop.page.Home
import net.deechael.kookdesktop.page.Login
import net.deechael.kookdesktop.style.MATERIAL3_COLOR
import net.deechael.kookdesktop.util.Case
import net.deechael.kookdesktop.util.Controller
import net.deechael.kookdesktop.util.Switcher
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import snw.jkook.JKook
import snw.kookbc.impl.CoreImpl
import snw.kookbc.impl.KBCClient
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.awt.Dimension
import java.lang.management.ManagementFactory

val VERSION = "1.0.0"

val LOGGER = LoggerFactory.getLogger("KookDesktop")

val CLI_LOGGER = LoggerFactory.getLogger("KookCli")

val HTTP_CLIENT = OkHttpClient.Builder()
    .addInterceptor {
        it.proceed(
            it.request()
                .newBuilder()
                .header(
                    "accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
                )
                .header(
                    "accept-language",
                    "en-US,en;q=0.9,zh-CN;q=0.8,zh-Hans;q=0.7,zh;q=0.6"
                )
                .header(
                    "user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"
                )
                .build()
        )
    }
    .build()
var KOOK_CLIENT: KBCClient? = null

val KOOK_SCOPE = CoroutineScope(Job())

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = MATERIAL3_COLOR
    ) {
        Surface {

            val controller = Controller()
            Switcher(
                currentPage = "login",
                controller = controller
            ) {
                Case("login") {
                    Login {
                        LOGGER.debug("Login successfully, goto Home")
                        controller.goto("home")
                    }
                }

                Case("home") {
                    Home()
                }
            }
        }
    }
}

fun logSystemInfo() {
    val runtimeMX = ManagementFactory.getRuntimeMXBean()
    val osMX = ManagementFactory.getOperatingSystemMXBean()
    if (runtimeMX != null && osMX != null) {
        LOGGER.debug("System information is following:")
        LOGGER.debug(
            "Java: {} ({} {} by {})",
            runtimeMX.specVersion,
            runtimeMX.vmName,
            runtimeMX.vmVersion,
            runtimeMX.vmVendor
        )
        LOGGER.debug("Host: {} {} (Architecture: {})", osMX.name, osMX.version, osMX.arch)
    } else {
        LOGGER.debug("Unable to read system info")
    }
}

fun main() = application {
    Thread.currentThread().name = "Main Thread"
    SysOutOverSLF4J.registerLoggingSystem("org.apache.logging")
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()

    LOGGER.info("===============================================")
    LOGGER.info("==   WELCOME TO KOOK DESKTOP BY DEECHAEL !   ==")
    LOGGER.info("==        BASED ON COMPOSE AND JKOOK!        ==")
    LOGGER.info("===============================================")

    logSystemInfo()

    val core = CoreImpl(LOGGER)
    JKook.setCore(core)

    Window(
        onCloseRequest = {
            if (KOOK_CLIENT?.isRunning == true) {
                KOOK_CLIENT?.shutdown()
            }
            exitApplication()
        },
        title = "Kook Desktop"
    ) {
        this.window.minimumSize = Dimension(800, 600)
        App()
    }
}
