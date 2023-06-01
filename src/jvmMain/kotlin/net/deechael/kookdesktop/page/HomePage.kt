package net.deechael.kookdesktop.page

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import net.deechael.kook.util.Guild
import net.deechael.kook.util.GuildGroup
import net.deechael.kook.util.GuildLister
import net.deechael.kookdesktop.KOOK_CLIENT
import net.deechael.kookdesktop.LOGGER
import net.deechael.kookdesktop.util.HorizontalDivider
import net.deechael.kookdesktop.util.Updater

@Preview
@Composable
fun HomePreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface {
            Home()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    LOGGER.debug("Rendering Home page, now listing all the guilds")
    val guilds = GuildLister.listGroups(KOOK_CLIENT!!)

    val guildPageUpdater = Updater()

    guildPageUpdater.update {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "Click the guild left to view the channels",
                fontSize = 4.em,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
                .width(64.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(
                modifier = Modifier.height(
                    8.dp
                )
            )
            for (guild in guilds) {
                if (guild is Guild) {
                    LOGGER.debug("Rendered guild {}", guild.id)
                    Card(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                            .width(48.dp)
                            .height(48.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        TooltipArea(tooltip = {
                            Card(
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = guild.info.name,
                                    modifier = Modifier.padding(
                                        top = 4.dp,
                                        bottom = 4.dp,
                                        start = 8.dp,
                                        end = 8.dp
                                    )
                                )
                            }
                        }) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(48.dp)
                                    .clickable(true, guild.info.name) {
                                        guildPageUpdater.update {
                                            GuildPage(guild)
                                        }
                                    }
                            ) {
                                if (guild.info.icon != "") {
                                    KamelImage(
                                        resource = lazyPainterResource(
                                            data = guild.info.icon
                                        ),
                                        contentDescription = guild.info.name
                                    )
                                } else {
                                    Text(
                                        text = guild.info.name
                                    )
                                }
                            }
                        }
                    }
                } else if (guild is GuildGroup) {
                    var showGuilds by rememberSaveable {
                        mutableStateOf(false)
                    }

                    Card(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 4.dp,
                                end = 4.dp
                            )
                            .width(56.dp)
                            .requiredWidthIn(
                                min = 56.dp
                            )
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        TooltipArea(tooltip = {
                            Card(
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = guild.name,
                                    modifier = Modifier.padding(
                                        top = 4.dp,
                                        bottom = 4.dp,
                                        start = 8.dp,
                                        end = 8.dp
                                    )
                                )
                            }
                        }) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(48.dp)
                                    .height(48.dp)
                                    .clickable(true, guild.name) {
                                        showGuilds = !showGuilds
                                    }
                            ) {
                                Text(
                                    text = if (guild.name != "") guild.name else "Folder"
                                )
                            }
                        }
                        if (showGuilds) {
                            for (subGuild in guild.guilds) {
                                TooltipArea(tooltip = {
                                    Card(
                                        shape = RoundedCornerShape(5.dp)
                                    ) {
                                        Text(
                                            text = subGuild.info.name,
                                            modifier = Modifier.padding(
                                                top = 4.dp,
                                                bottom = 4.dp,
                                                start = 8.dp,
                                                end = 8.dp
                                            )
                                        )
                                    }
                                }) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .background(Color(guild.color))
                                            .padding(4.dp)
                                            .height(48.dp)
                                            .width(48.dp)
                                            .clickable(true, subGuild.info.name) {
                                                guildPageUpdater.update {
                                                    GuildPage(subGuild)
                                                }
                                            }
                                    ) {
                                        if (subGuild.info.icon != "") {
                                            KamelImage(
                                                resource = lazyPainterResource(
                                                    data = subGuild.info.icon
                                                ),
                                                contentDescription = subGuild.info.name
                                            )
                                        } else {
                                            Text(
                                                text = subGuild.info.name
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 2.dp
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            guildPageUpdater.show()
        }
    }
}