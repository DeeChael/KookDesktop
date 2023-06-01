package net.deechael.kook.util

import net.deechael.kookdesktop.LOGGER
import snw.kookbc.impl.KBCClient
import snw.kookbc.impl.network.HttpAPIRoute

abstract class ChannelItem(val id: String, val name: String, val level: Int) {

}

class Channel(id: String, name: String, val parent: String, level: Int, val voice: Boolean) :
    ChannelItem(id, name, level) {

}

class Category(id: String, name: String, level: Int) : ChannelItem(id, name, level) {

    val channels: MutableList<Channel> = mutableListOf()

}

object ChannelLister {

    fun listChannels(client: KBCClient, guild: String): List<ChannelItem> {
        LOGGER.debug("Listing channels of guild with id [{}]", guild)

        val channels = mutableListOf<ChannelItem>()

        val response = client.networkClient.get(
            String.format("%s?guild_id=%s", HttpAPIRoute.CHANNEL_LIST.toFullURL(), guild)
        )
        LOGGER.debug("Fetching channels result: {}", response)

        val rawDatas = mutableListOf<ChannelItem>()

        val categories = mutableMapOf<String, Category>()

        val total = response["meta"].asJsonObject["total"].asInt
        val pageSize = response["meta"].asJsonObject["page_size"].asInt
        var now = 0
        while (now * pageSize < total) {
            LOGGER.debug("Fetching channels in page {}", now + 1)

            for (element in response["items"].asJsonArray) {
                val obj = element.asJsonObject
                if (obj["is_category"].asBoolean) {
                    val category = Category(obj["id"].asString, obj["name"].asString, obj["level"].asInt)
                    LOGGER.debug("Found category with id [{}] and name [{}]", category.id, category.name)
                    rawDatas.add(category)
                    categories[category.id] = category
                } else {
                    val channel = Channel(
                        obj["id"].asString,
                        obj["name"].asString,
                        obj["parent_id"].asString,
                        obj["level"].asInt,
                        obj["type"].asInt == 2
                    )
                    LOGGER.debug("Found channel with id [{}] and name [{}]", channel.id, channel.name)
                    rawDatas.add(channel)
                }
            }

            now++
        }

        for (channel in rawDatas) {
            if (channel is Category) {
                channels.add(channel)
            } else if (channel is Channel) {
                if (channel.parent.isNotEmpty()) {
                    categories[channel.parent]!!.channels.add(channel)
                } else {
                    channels.add(channel)
                }
            }
        }

        channels.sortBy {
            if (it is Category) {
                it.channels.sortByDescending { sub ->
                    sub.level
                }
                it.channels.sortWith { a, b ->
                    if (a.voice && b.voice) {
                        0
                    } else if (a.voice) {
                        1
                    } else {
                        -1
                    }
                }
            }
            it.level
        }

        return channels.toList()
    }

}