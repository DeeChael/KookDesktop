package net.deechael.kook.api

interface User {

    fun getId(): String

}

interface GuildUser: User {

    fun getJoinedChannels(): List<PublicVoiceChannel>

    fun setNickname(nickname: String)

    fun addRole(role: Role)

    fun removeRole(role: Role)

    fun kickout()

    fun mute()

    fun unmute()

}