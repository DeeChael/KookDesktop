package net.deechael.kookdesktop.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import net.deechael.kook.util.Category
import net.deechael.kook.util.Channel
import net.deechael.kook.util.ChannelLister
import net.deechael.kook.util.Guild
import net.deechael.kookdesktop.KOOK_CLIENT
import net.deechael.kookdesktop.LOGGER
import net.deechael.kookdesktop.util.HorizontalDivider
import net.deechael.kookdesktop.util.Updater

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuildPage(guild: Guild) {
    LOGGER.debug("Rendering guild page with guild id [{}], now listing all the channels", guild.id)
    val channels = ChannelLister.listChannels(KOOK_CLIENT!!, guild.id)

    val temp: Channel? = null

    var viewingChannel by rememberSaveable {
        mutableStateOf(temp)
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.width(256.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = guild.info.name,
                modifier = Modifier.padding(16.dp)
            )
            Divider()
            for (channel in channels) {
                if (channel is Channel) {
                    Card(
                        modifier = Modifier.padding(4.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    viewingChannel = channel
                                }
                        ) {
                            if (channel.voice) {
                                Icon(
                                    imageVector = Icons.Filled.VoiceChat,
                                    contentDescription = "voice channel",
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Chat,
                                    contentDescription = "text channel",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Text(
                                text = channel.name,
                                modifier = Modifier.padding(8.dp).weight(weight = 1f, fill = false),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else if (channel is Category) {
                    var showChannels by rememberSaveable {
                        mutableStateOf(true)
                    }
                    Card(
                        modifier = Modifier.padding(4.dp)
                            .fillMaxWidth()
                            .height(24.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    showChannels = !showChannels
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (showChannels) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "showed"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowRight,
                                        contentDescription = "hided"
                                    )
                                }
                                Text(
                                    text = channel.name,
                                    modifier = Modifier.weight(weight = 1f, fill = false),
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (showChannels) {
                        for (subChannel in channel.channels) {
                            Card(
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    end = 4.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable {
                                            viewingChannel = subChannel
                                        }
                                ) {
                                    if (subChannel.voice) {
                                        Icon(
                                            imageVector = Icons.Filled.VoiceChat,
                                            contentDescription = "voice channel",
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Chat,
                                            contentDescription = "text channel",
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    Text(
                                        text = subChannel.name,
                                        modifier = Modifier.padding(8.dp).weight(weight = 1f, fill = false),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider()
        if (viewingChannel != null) {
            ChannelPage(guild, viewingChannel!!)
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "Click the channel left to view the messages",
                    fontSize = 4.em,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}