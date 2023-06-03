package net.deechael.kook.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.Markdown
import net.deechael.kook.util.ColorUtil
import net.deechael.kookdesktop.util.DesktopUtil
import net.deechael.kookdesktop.util.HorizontalDivider
import snw.jkook.message.component.BaseComponent
import snw.jkook.message.component.MarkdownComponent
import snw.jkook.message.component.TextComponent
import snw.jkook.message.component.card.CardComponent
import snw.jkook.message.component.card.Theme
import snw.jkook.message.component.card.element.ButtonElement
import snw.jkook.message.component.card.module.ActionGroupModule
import snw.jkook.message.component.card.module.BaseModule
import snw.jkook.message.component.card.module.ContainerModule

val HEX_COLOR_REGEX = Regex("#[0-9a-f]{6}")

fun Theme.getColor(): Color? {
    if (this == Theme.PRIMARY) {
        return ColorUtil.hexToRgb("7acc35")
    } else if (this == Theme.SUCCESS) {
        return ColorUtil.hexToRgb("00d287")
    } else if (this == Theme.DANGER) {
        return ColorUtil.hexToRgb("ff3200")
    } else if (this == Theme.WARNING) {
        return ColorUtil.hexToRgb("ff8200")
    } else if (this == Theme.INFO) {
        return ColorUtil.hexToRgb("00d287")
    } else if (this == Theme.SECONDARY) {
        return ColorUtil.hexToRgb("66686b")
    } else if (this == Theme.NONE) {
        return null
    }
    throw RuntimeException("Unknown theme")
}

@Composable
fun BaseComponent.toComposable() {
    if (this is MarkdownComponent) {
        this.toComposable()
    } else if (this is TextComponent) {
        this.toComposable()
    }
}

@Composable
fun TextComponent.toComposable() {
    Text(
        text = this.toString(),
        modifier = Modifier.padding(8.dp)
    )
}


@Composable
fun MarkdownComponent.toComposable() {
    Markdown(
        content = this.toString().trimIndent(),
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun CardComponent.toComposable() {
    var color = Color(0, 0, 0)
    if (this.color != null && HEX_COLOR_REGEX.matches(this.color!!)) {
        color = ColorUtil.hexToRgb(this.color!!.substring(1))
    } else if (this.theme != null) {
        val temp = this.theme!!.getColor()
        if (temp != null) {
            color = temp
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                thickness = 5.dp,
                color = color
            )
            Column {

            }
        }
    }
}

@Composable
fun BaseModule.toComposable() {
    if (this is ActionGroupModule) {
        this.toComposable()
    }
}

@Composable
fun ActionGroupModule.toComposable() {
    for (interactElement in this.buttons) {
        if (interactElement is ButtonElement) {
            Button(
                onClick = {
                    // TODO send button value
                    if (interactElement.eventType == ButtonElement.EventType.RETURN_VAL) {
                        val value = interactElement.value
                    } else {
                        DesktopUtil.tryOpenUrl(interactElement.value)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${interactElement.text}"
                )
            }
        }
    }
}

@Composable
fun ContainerModule.toComposable() {

}