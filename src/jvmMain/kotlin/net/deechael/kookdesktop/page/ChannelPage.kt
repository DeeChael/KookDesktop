package net.deechael.kookdesktop.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import net.deechael.kook.card.toComposable
import net.deechael.kook.util.Channel
import net.deechael.kook.util.ChannelMessage
import net.deechael.kook.util.Guild
import net.deechael.kook.util.MessageLister
import net.deechael.kookdesktop.KOOK_CLIENT
import net.deechael.kookdesktop.style.MATERIAL3_COLOR
import net.deechael.kookdesktop.style.MATERIAL3_TYPOGRAPHY

@Composable
fun RowScope.ChannelPage(guild: Guild, channel: Channel) {
    val messages = MessageLister.listChannelMessages(KOOK_CLIENT!!, channel.id)
    Column(
        modifier = Modifier.weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        for (message in messages) {
            Message(message)
        }
    }
}

@Composable
fun Message(message: ChannelMessage) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        KamelImage(
            resource = lazyPainterResource(
                data = message.author.avatar
            ),
            contentDescription = message.author.name,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MATERIAL3_COLOR.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = message.author.name,
                color = MATERIAL3_COLOR.onPrimary,
                style = MATERIAL3_TYPOGRAPHY.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            message.component.toComposable()
        }
    }
}