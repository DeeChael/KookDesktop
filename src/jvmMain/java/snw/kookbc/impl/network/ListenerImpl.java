/*
 *     KookBC -- The Kook Bot Client & JKook API standard implementation for Java.
 *     Copyright (C) 2022 - 2023 KookBC contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package snw.kookbc.impl.network;

import com.google.gson.JsonObject;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.event.Event;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.event.pm.PrivateMessageReceivedEvent;
import snw.jkook.message.Message;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.TextComponent;
import snw.kookbc.impl.KBCClient;
import snw.kookbc.impl.command.CommandManagerImpl;
import snw.kookbc.impl.event.EventFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static snw.kookbc.util.GsonUtil.get;

public class ListenerImpl implements Listener {
    protected final KBCClient client;
    protected final Object lck = new Object();

    public ListenerImpl(KBCClient client) {
        this.client = client;
    }

    @Override
    public void executeEvent(Frame frame) {
        if (!(frame.getType() == MessageType.PONG)) { // I hate PONG logging messages
            client.getCore().getLogger().debug("Got payload frame: {}", frame);
        }
        if (frame.getType() == null) {
            client.getCore().getLogger().warn("Unknown event type!");
            return;
        }
        switch (frame.getType()) {
            case EVENT:
                client.getEventExecutor().execute(() -> event(frame));
                break;
            case HELLO:
                hello(frame);
                break;
            case PING:
                client.getCore().getLogger().debug("Impossible Message from remote: type is PING.");
                break;
            case PONG:
                client.getCore().getLogger().trace("Got PONG");
                client.getConnector().pong();
                break;
            case RESUME:
                client.getCore().getLogger().debug("Impossible Message from remote: type is RESUME.");
                break;
            case RECONNECT:
                client.getCore().getLogger().warn("Got RECONNECT request from remote. Attempting to reconnect.");
                client.getConnector().requestReconnect();
                break;
            case RESUME_ACK:
                client.getCore().getLogger().info("Resume finished");
                client.getSession().setId(frame.getData().get("session_id").getAsString());
                break;
        }
    }

    protected void event(Frame frame) {
        synchronized (lck) {
            client.getCore().getLogger().debug("Got EVENT");
            Session session = client.getSession();
            AtomicInteger sn = session.getSN();
            Set<Frame> buffer = session.getBuffer();
            int expected = Session.UPDATE_FUNC.applyAsInt(sn.get());
            int actual = frame.getSN();
            if (actual > expected) {
                client.getCore().getLogger().warn("Unexpected wrong SN, expected {}, got {}", expected, actual);
                client.getCore().getLogger().warn("We will process it later.");
                buffer.add(frame);
            } else if (expected == actual) {
                event0(frame);
                session.increaseSN();
                if (!buffer.isEmpty()) {
                    int continueId = sn.get() + 1;
                    do {
                        boolean found = false;
                        Iterator<Frame> bufferIterator = buffer.iterator();
                        while (bufferIterator.hasNext()) {
                            Frame bufFrame = bufferIterator.next();
                            if (bufFrame.getSN() == continueId) {
                                found = true;       // we found the frame matching the continueId,
                                // so we will continue after the frame got processed
                                event0(bufFrame);
                                session.increaseSN(); // make sure the SN will update!
                                continueId++;
                                bufferIterator.remove(); // we won't need this frame, because it has processed
                                client.getCore().getLogger().debug("Processed message in buffer with SN {}", bufFrame.getSN());
                                break;
                            }
                        }
                        if (!found) {
                            break;
                        }
                    } while (true);
                }
            } else {
                client.getCore().getLogger().warn("Unexpected old message from remote. Dropped it.");
            }
        }
    }

    protected void event0(Frame frame) {
        Event event;
        try {
            event = EventFactory.getEvent(client, frame);
        } catch (Exception e) {
            client.getCore().getLogger().error("Unable to create event from payload.");
            client.getCore().getLogger().error("Event payload: {}", frame);
            e.printStackTrace();
            return;
        }
        if (event == null) {
            return;
        }
        if (!executeCommand(event)) {
            client.getCore().getEventManager().callEvent(event);
        }
    }

    protected void hello(Frame frame) {
        client.getCore().getLogger().debug("Got HELLO");
        client.getConnector().setConnected(true);
        JsonObject object = frame.getData();
        int status = get(object, "code").getAsInt();
        if (status == 0) {
            client.getSession().setId(get(object, "session_id").getAsString());
        } else {
            client.getConnector().requestReconnect();
        }
    }

    // return true if the component is a command and executed (whether success or failed).
    protected boolean executeCommand(Event event) {
        if (!(event instanceof ChannelMessageEvent || event instanceof PrivateMessageReceivedEvent))
            return false; // not a message-related event

        // region extract data
        User sender;
        Message msg;
        TextChannel channel = null;
        BaseComponent baseComponent; // raw
        TextComponent component = null;
        if (event instanceof ChannelMessageEvent) {
            msg = ((ChannelMessageEvent) event).getMessage();
            channel = ((ChannelMessageEvent) event).getChannel();
        } else {
            msg = ((PrivateMessageReceivedEvent) event).getMessage();
        }
        sender = msg.getSender();
        baseComponent = msg.getComponent();
        if (baseComponent instanceof TextComponent) {
            component = (TextComponent) baseComponent;
        }
        // endregion

        // condition check
        if (component == null) return false; // not a text component!
        if (sender == client.getCore().getUser()) return false; // prevent self call

        // prepare data
        String cmdLine = component.toString();
        CommandManagerImpl cmdMan = (CommandManagerImpl) client.getCore().getCommandManager();

        // execute command
        try {
            return cmdMan.executeCommand(sender, cmdLine, msg);
        } catch (Exception e) {
            client.getCore().getLogger().error("Unexpected exception while we attempting to execute command from remote.", e);
            return true; // Although this failed, but it is a valid command
        }
    }
}
