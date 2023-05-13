package net.deechael.kookdesktop.page

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import net.deechael.kook.util.Guild
import net.deechael.kook.util.GuildGroup
import net.deechael.kook.util.GuildLister
import net.deechael.kookdesktop.KOOK_CLIENT
import net.deechael.kookdesktop.LOGGER
import net.deechael.kookdesktop.util.HorizontalDivider

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
                            .align(Alignment.CenterHorizontally)
                            .clickable(true, guild.info.name) {
                                TODO("Switch guild")
                            },
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
                            ) {
                                KamelImage(
                                    resource = lazyPainterResource(
                                        data = guild.info.icon
                                    ),
                                    contentDescription = guild.info.name
                                )
                            }
                        }
                    }
                } else if (guild is GuildGroup) {
                    Text(
                        text = guild.name
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 2.dp
        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}