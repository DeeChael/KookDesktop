package net.deechael.kook.util

import com.google.gson.JsonObject
import net.deechael.kookdesktop.LOGGER
import snw.jkook.entity.User
import snw.jkook.message.Message
import snw.jkook.message.TextChannelMessage
import snw.jkook.message.component.BaseComponent
import snw.kookbc.impl.KBCClient
import snw.kookbc.impl.message.TextChannelMessageImpl
import snw.kookbc.impl.network.HttpAPIRoute
import snw.kookbc.util.GsonUtil


class AuthorInfo(val id: String, val name: String, val avatar: String) {

}

class ChannelMessage(val id: String, val author: AuthorInfo, val component: BaseComponent) {

}

object MessageLister {

    fun listChannelMessages(client: KBCClient, channel: String): List<ChannelMessage> {
        LOGGER.debug("Listing messages of channel with id [{}]", channel)

        val messages = mutableListOf<ChannelMessage>()

        val response = client.networkClient.get(
            String.format("%s?target_id=%s", HttpAPIRoute.CHANNEL_MESSAGE_QUERY.toFullURL(), channel)
        )
        LOGGER.debug("Fetching messages result: {}", response)

        for (element in response["items"].asJsonArray) {
            val obj = element.asJsonObject

            messages.add(parseChannelMessage(client, obj))
        }

        return messages.toList()
    }

    private fun parseChannelMessage(client: KBCClient, jsonObject: JsonObject): ChannelMessage {
        val id = GsonUtil.get(jsonObject, "id").asString
        val authorObj: JsonObject = jsonObject.getAsJsonObject("author")
        val component: BaseComponent = client.getMessageBuilder().buildComponent(jsonObject)
        return ChannelMessage(
            id,
            AuthorInfo(
                GsonUtil.get(authorObj, "id").asString,
                GsonUtil.get(authorObj, "username").asString,
                GsonUtil.get(authorObj, "avatar").asString
            ),
            component
        )
    }

}