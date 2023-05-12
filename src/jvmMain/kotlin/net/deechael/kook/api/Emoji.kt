package net.deechael.kook.api

interface Emoji {
}

interface GuildEmoji: Emoji {

    fun getId(): String

    fun getName(): String

    fun getUser(): GuildUser

    fun getGuild(): Guild

    fun getGuildId(): String

    fun updateName(name: String)

    fun delete()

}