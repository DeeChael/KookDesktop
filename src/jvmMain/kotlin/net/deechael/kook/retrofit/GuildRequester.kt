package net.deechael.kook.retrofit

import net.deechael.kook.api.Guild
import net.deechael.kook.api.KookAPI
import net.deechael.kook.util.QueryBuilder

class GuildRequester(val requester: AbstractRequester) {

    fun listGuilds(
        page: Int? = null,
        pageSize: Int? = null,
        sort: String? = null
    ): List<Guild> {
        val guilds = mutableListOf<Guild>()

        val response = this.requester.get(
            KookAPI.GuildAPI.LIST, QueryBuilder.of()
            .with("page", page)
            .with("page_size", pageSize)
            .with("sort", sort)
            .build())

        // TODO deal with datas

        return guilds.toList()
    }

    fun viewGuild(guildId: String): Guild {

        val response = this.requester.get(
            KookAPI.GuildAPI.VIEW, QueryBuilder.of()
            .with("guild_id", guildId)
            .build())

        TODO()
    }

}