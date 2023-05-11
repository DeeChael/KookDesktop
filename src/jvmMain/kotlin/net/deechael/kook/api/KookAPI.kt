package net.deechael.kook.api

object KookAPI {

    object GuildAPI {

        val LIST = "api/v3/guild/list"
        val VIEW = "api/v3/guild/view"
        val USERLIST = "api/v3/guild/user-list"
        val NICKNAME = "api/v3/guild/nickname"
        val LEAVE = "api/v3/guild/leave"
        val KICKOUT = "api/v3/guild/kickout"

    }

    object GuildMuteAPI {

        val LIST = "api/v3/guild-mute/list"
        val CREATE = "api/v3/guild-mute/create"
        val DELETE = "api/v3/guild-mute/delete"
        val HISTORY = "api/v3/guild-mute/history"

    }

}