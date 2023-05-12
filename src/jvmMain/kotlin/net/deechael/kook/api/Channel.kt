package net.deechael.kook.api

interface Channel {

    fun getId(): String

    fun getType(): ChannelTypes

}

interface TextChannel: Channel {

    fun send(message: String) {

    }

    override fun getType(): ChannelTypes {
        return ChannelTypes.TEXT
    }

}

interface VoiceChannel: Channel {

    override fun getType(): ChannelTypes {
        return ChannelTypes.VOICE
    }

}

interface PrivateChannel: Channel, TextChannel {
}

interface PublicChannel: Channel {

    fun getUsers(): List<User>

    fun getName(): String

    fun getUserId(): String

    fun getTopic(): String

    fun getParentId(): String

    fun getLevel(): Int

    fun getGuild(): Guild

    fun getGuildId(): String

    fun delete()

    fun updateName(name: String)

}

interface PublicTextChannel: PublicChannel, TextChannel {

    fun updateTopic(topic: String)

    fun updateSlowMode(mode: SlowModeTypes)

}

interface PublicVoiceChannel: PublicChannel, VoiceChannel {

}

interface ChannelCategory: PublicChannel {

    fun createTextChannel(name: String, category: ChannelCategory? = null): PublicTextChannel

    fun createVoiceChannel(name: String, category: ChannelCategory? = null, limitAmount: Int? = null, voiceQuality: Int? = null): PublicVoiceChannel

    override fun getType(): ChannelTypes {
        return ChannelTypes.CATEGORY
    }

}