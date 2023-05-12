package net.deechael.kook.api

import java.io.File
import java.io.InputStream

interface Guild {

    fun getId(): String

    fun getName(): String

    fun getMasterId(): String

    fun getTopic(): String

    fun getIcon(): String

    fun getRegion(): String

    fun isEnableOpen(): Boolean

    fun getOpenId(): String

    fun getDefaultChannelId(): String

    fun getWelcomeChannelId(): String

    fun getChannelCategories(): List<ChannelCategory>

    fun getChannels(): List<PublicChannel>

    fun getUsers(channel: PublicChannel? = null): List<User>

    fun getUser(user: User): GuildUser

    fun getRoles(): List<Role>

    fun createRole(name: String): Role

    fun createTextChannel(name: String, category: ChannelCategory? = null): PublicTextChannel

    fun createVoiceChannel(name: String, category: ChannelCategory? = null, limitAmount: Int? = null, voiceQuality: Int? = null): PublicVoiceChannel

    fun createChannelCategory(name: String): ChannelCategory

    fun leave()

    fun getEmojis(): List<GuildEmoji>

    fun createEmoji(emoji: File, name: String): GuildEmoji

    fun createEmoji(emoji: InputStream, name: String, autoClose: Boolean = false): GuildEmoji

}