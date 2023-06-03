package net.deechael.kookdesktop.util

import java.awt.Desktop
import java.net.URI

object DesktopUtil {

    fun tryOpenUrl(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url));
        }
    }

}