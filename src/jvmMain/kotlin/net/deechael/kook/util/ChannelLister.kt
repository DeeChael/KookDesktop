package net.deechael.kook.util

import net.deechael.kookdesktop.LOGGER
import snw.kookbc.impl.KBCClient
import snw.kookbc.impl.network.HttpAPIRoute

interface ChannelItem {

    fun getId(): String

    fun getName(): String

    fun getLevel(): Int

}

class Channel(val id: String, val name: String, val parent: String, val level: Int, val voice: Boolean) : ChannelItem {

    override fun getId(): String {
        return this.id
    }

    override fun getName(): String {
        return this.name
    }

    override fun getLevel(): Int {
        return this.level
    }

}

class Category(val id: String, val name: String, val level: Int) : ChannelItem {

    val channels: MutableList<Channel> = mutableListOf()

    override fun getId(): String {
        return this.id
    }

    override fun getName(): String {
        return this.name
    }

    override fun getLevel(): Int {
        return this.level
    }

}

object ChannelLister {

    fun listGroups(client: KBCClient, guild: String): List<ChannelItem> {
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
                    val channel = Channel(obj["id"].asString, obj["name"].asString, obj["parent_id"].asString, obj["level"].asInt, obj["type"].asInt == 2)
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
                    channels.add(channel)
                } else {
                    categories[channel.parent]!!.channels.add(channel)
                }
            }
        }

        channels.sortBy {
            if (it is Category) {
                it.channels.sortBy { sub ->
                    sub.level
                }
            }
            it.getLevel()
        }

        return channels.toList()
    }

}