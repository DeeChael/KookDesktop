package net.deechael.kook.util

import net.deechael.kookdesktop.LOGGER
import snw.kookbc.impl.KBCClient
import snw.kookbc.impl.network.HttpAPIRoute

interface GuildItem

class Guild(val id: String, val info: GuildInfo) : GuildItem

class GuildGroup(val id: String, val name: String, val color: Int) : GuildItem {

    val guilds: MutableList<Guild> = mutableListOf()

}

data class GuildInfo(
    val name: String,
    val icon: String,
)

object GuildLister {
    fun listGroups(client: KBCClient): List<GuildItem> {
        LOGGER.debug("Listing guilds joined")

        val guilds = mutableListOf<GuildItem>()

        val response = client.networkClient.get(HttpAPIRoute.GUILD_FOLDERS.toFullURL())
        LOGGER.debug("Fetching folders result: {}", response)
        val datas = resolveData(client)

        val total = response["meta"].asJsonObject["total"].asInt
        val pageSize = response["meta"].asJsonObject["page_size"].asInt
        var now = 0
        while (now * pageSize < total) {
            val response = client.networkClient.get(HttpAPIRoute.GUILD_FOLDERS.toFullURL())
            LOGGER.debug("Fetching guilds in page {}", now + 1)

            for (element in response["items"].asJsonArray) {
                val obj = element.asJsonObject
                if (obj["id"].asString != "" && !obj["color"].isJsonNull) {
                    val group = GuildGroup(obj["id"].asString, obj["name"].asString, obj["color"].asInt)
                    LOGGER.debug(
                        "Found the info of guild group with id [{}] and name [{}]",
                        obj["id"].asString,
                        obj["name"].asString
                    )
                    for (guild in obj["guild_ids"].asJsonArray) {
                        LOGGER.debug(
                            "Found the info of guild with id [{}] and name [{}]",
                            guild.asString,
                            datas[guild.asString]!!.name
                        )
                        group.guilds.add(Guild(guild.asString, datas[guild.asString]!!))
                    }
                    guilds.add(group)
                } else {
                    val id = obj["guild_ids"].asJsonArray[0].asString
                    guilds.add(Guild(id, datas[id]!!))
                }
            }

            now++
        }


        return guilds.toList()
    }

    fun resolveData(client: KBCClient): Map<String, GuildInfo> {
        LOGGER.debug("Resolving data")

        val datas = mutableMapOf<String, GuildInfo>()

        val detail = client.networkClient.get(HttpAPIRoute.GUILD_JOINED_LIST.toFullURL())

        LOGGER.debug("Detail data raw: {}", detail)

        for (guild in detail["items"].asJsonArray) {
            val guild = guild.asJsonObject
            datas[guild["id"].asString] = GuildInfo(guild["name"].asString, guild["icon"].asString)
        }

        return datas.toMap()
    }

}