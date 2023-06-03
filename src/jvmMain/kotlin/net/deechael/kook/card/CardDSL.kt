package net.deechael.kook.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.mikepenz.markdown.Markdown
import com.mikepenz.markdown.MarkdownColors
import com.mikepenz.markdown.MarkdownDefaults
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import net.deechael.kook.util.ColorUtil
import net.deechael.kookdesktop.style.MATERIAL3_COLOR
import net.deechael.kookdesktop.util.DesktopUtil
import net.deechael.kookdesktop.util.HorizontalDivider
import snw.jkook.entity.abilities.Accessory
import snw.jkook.message.component.BaseComponent
import snw.jkook.message.component.MarkdownComponent
import snw.jkook.message.component.TextComponent
import snw.jkook.message.component.card.CardComponent
import snw.jkook.message.component.card.CardScopeElement
import snw.jkook.message.component.card.MultipleCardComponent
import snw.jkook.message.component.card.Theme
import snw.jkook.message.component.card.element.*
import snw.jkook.message.component.card.module.*
import snw.jkook.message.component.card.structure.BaseStructure
import snw.jkook.message.component.card.structure.Paragraph

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
    } else if (this is CardComponent) {
        this.toComposable()
    } else if (this is MultipleCardComponent) {
        for (component in this.components) {
            component.toComposable()
        }
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
        modifier = Modifier.padding(8.dp),
        colors = MarkdownDefaults.markdownColors(textColor = Color.Unspecified)
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
    val modules = this.modules
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
                for (module in modules) {
                    module.toComposable()
                }
            }
        }
    }
}

@Composable
fun BaseModule.toComposable() {
    if (this is ActionGroupModule) {
        this.toComposable()
    } else if (this is ContainerModule) {
        this.toComposable()
    } else if (this is ContextModule) {
        this.toComposable()
    } else if (this is CountdownModule) {
        this.toComposable()
    } else if (this is DividerModule) {
        this.toComposable()
    } else if (this is FileModule) {
        this.toComposable()
    } else if (this is HeaderModule) {
        this.toComposable()
    } else if (this is ImageGroupModule) {
        this.toComposable()
    } else if (this is InviteModule) {
        this.toComposable()
    } else if (this is SectionModule) {
        this.toComposable()
    }
}

@Composable
fun ActionGroupModule.toComposable() {
    for (interactElement in this.buttons) {
        if (interactElement is ButtonElement) {
            interactElement.toComposable()
        }
    }
}

@Composable
fun ContainerModule.toComposable() {
    for (image in this.images) {
        KamelImage(
            resource = lazyPainterResource(
                data = image.source
            ),
            contentDescription = image.alt ?: ""
        )
    }
}

@Composable
fun ContextModule.toComposable() {
    Row {
        for (element in this@toComposable.modules) {
            if (element is PlainTextElement) {
                element.toComposable(
                    color = MATERIAL3_COLOR.secondary
                )
            } else if (element is MarkdownElement) {
                element.toComposable(
                    colors = MarkdownDefaults.markdownColors(
                        textColor = MATERIAL3_COLOR.secondary
                    )
                )
            } else {
                element.toComposable()
            }
        }
    }
}

@Composable
fun CountdownModule.toComposable() {
    // TODO
}

@Composable
fun DividerModule.toComposable() {
    Divider()
}

@Composable
fun FileModule.toComposable() {
    // TODO
}

@Composable
fun HeaderModule.toComposable() {
    Text(
        text = this.element.content,
        modifier = Modifier.padding(8.dp),
        color = Color.Unspecified,
        fontSize = 1.2.em,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ImageGroupModule.toComposable() {
    for (image in this.images) {
        KamelImage(
            resource = lazyPainterResource(
                data = image.source
            ),
            contentDescription = image.alt ?: ""
        )
    }
}

@Composable
fun InviteModule.toComposable() {
    // TODO
}

@Composable
fun SectionModule.toComposable() {
    if (this.accessory != null && this.mode != null) {
        val accessory = this.accessory!!
        if (accessory is ButtonElement) {
            Row {
                this@toComposable.text.toComposable()
                accessory.toComposable()
            }
        } else if (accessory is ImageElement) {
            val mode = this.mode
            if (mode == Accessory.Mode.LEFT) {
                Row {
                    accessory.toComposable()
                    this@toComposable.text.toComposable()
                }
            } else {
                Row {
                    this@toComposable.text.toComposable()
                    accessory.toComposable()
                }
            }
        }
    } else {
        this.text.toComposable()
    }
}

@Composable
fun CardScopeElement.toComposable() {
    if (this is BaseElement) {
        this.toComposable()
    } else if (this is BaseStructure) {
        this.toComposable()
    }
}

@Composable
fun BaseElement.toComposable(modifier: Modifier = Modifier) {
    if (this is PlainTextElement) {
        this.toComposable(modifier)
    } else if (this is MarkdownElement) {
        this.toComposable(modifier)
    } else if (this is ImageElement) {
        this.toComposable(modifier)
    } else if (this is ButtonElement) {
        this.toComposable(modifier)
    }
}

@Composable
fun PlainTextElement.toComposable(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Text(
        text = this.content,
        modifier = modifier.padding(8.dp),
        color = color
    )
}

@Composable
fun MarkdownElement.toComposable(
    modifier: Modifier = Modifier,
    colors: MarkdownColors = MarkdownDefaults.markdownColors(textColor = Color.Unspecified),
) {
    Markdown(
        content = this.content.trimIndent(),
        modifier = modifier.padding(8.dp),
        colors = colors
    )
}

@Composable
fun ImageElement.toComposable(modifier: Modifier = Modifier) {
    KamelImage(
        resource = lazyPainterResource(
            data = this.source
        ),
        contentDescription = this.alt ?: "",
        modifier = modifier
    )
}

@Composable
fun ButtonElement.toComposable() {
    Button(
        onClick = {
            if (this.eventType == ButtonElement.EventType.RETURN_VAL) {
                val value = this.value
                // TODO send button value
            } else {
                DesktopUtil.tryOpenUrl(this.value)
            }
        },
        shape = RoundedCornerShape(10.dp)
    ) {
        this@toComposable.text.toComposable()
    }
}

@Composable
fun BaseStructure.toComposable() {
    if (this is Paragraph) {
        this.toComposable()
    }
}

@Composable
fun Paragraph.toComposable(modifier: Modifier = Modifier) {
    if (this.columns == 1) {
        Column {
            for (text in this@toComposable.fields) {
                text.toComposable()
            }
        }
    } else if (this.columns == 2) {
        val elements = this@toComposable.fields as List<*>
        val times = if (elements.size % 2 == 0) elements.size / 2 else elements.size / 2 + 1
        Column {
            for (i in 0 until times step 2) {
                Row {
                    (elements[i] as BaseElement).toComposable(modifier.weight(1f))
                    if (i + 1 < elements.size) {
                        (elements[i + 1] as BaseElement).toComposable(modifier.weight(1f))
                    }
                }
            }
        }
    } else if (this.columns == 3) {
        val elements = this@toComposable.fields as List<*>
        val times = if (elements.size % 3 == 0) elements.size / 2 else elements.size / 3 + 1
        Column {
            for (i in 0 until times step 3) {
                Row {
                    (elements[i] as BaseElement).toComposable(modifier.weight(1f))
                    if (i + 1 < elements.size) {
                        (elements[i + 1] as BaseElement).toComposable(modifier.weight(1f))
                    }
                    if (i + 2 < elements.size) {
                        (elements[i + 2] as BaseElement).toComposable(modifier.weight(1f))
                    }
                }
            }
        }
    }
}