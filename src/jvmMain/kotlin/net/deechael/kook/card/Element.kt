package net.deechael.kook.card

import snw.jkook.message.component.card.Theme
import snw.jkook.message.component.card.element.BaseElement
import snw.jkook.message.component.card.element.ButtonElement
import snw.jkook.message.component.card.element.MarkdownElement
import snw.jkook.message.component.card.element.PlainTextElement

class ButtonBuilder {

    var theme: Theme? = null
    var value: String? = null
    var type: ButtonElement.EventType? = null
    var element: BaseElement = PlainTextElement("Button")

    fun Text(content: String, kmarkdown: Boolean = false, emoji: Boolean = false) {
        if (kmarkdown) {
            this.element = MarkdownElement(content)
        } else {
            this.element = PlainTextElement(content, emoji)
        }
    }

    fun build(): ButtonElement {
        return ButtonElement(this.theme, this.value, this.type, this.element)
    }

}