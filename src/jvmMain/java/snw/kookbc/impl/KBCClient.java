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

package snw.kookbc.impl;

import org.jetbrains.annotations.Nullable;
import snw.jkook.Core;
import snw.jkook.command.CommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.plugin.Plugin;
import snw.jkook.plugin.PluginDescription;
import snw.jkook.plugin.UnknownDependencyException;
import snw.jkook.util.Validate;
import snw.kookbc.impl.console.Console;
import snw.kookbc.impl.entity.builder.EntityBuilder;
import snw.kookbc.impl.entity.builder.EntityUpdater;
import snw.kookbc.impl.entity.builder.MessageBuilder;
import snw.kookbc.impl.network.Connector;
import snw.kookbc.impl.network.HttpAPIRoute;
import snw.kookbc.impl.network.NetworkClient;
import snw.kookbc.impl.network.Session;
import snw.kookbc.impl.plugin.InternalPlugin;
import snw.kookbc.impl.scheduler.SchedulerImpl;
import snw.kookbc.impl.storage.EntityStorage;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

// The client representation.
public class KBCClient {
    private volatile boolean running = true;
    private final CoreImpl core;
    private final NetworkClient networkClient;
    private final EntityStorage storage;
    private final EntityBuilder entityBuilder;
    private final MessageBuilder msgBuilder;
    private final EntityUpdater entityUpdater;
    private final File pluginsFolder;
    private final Session session = new Session(null);
    private final InternalPlugin internalPlugin;
    private final ReentrantLock shutdownLock;
    private final Condition shutdownCondition;

    protected final ExecutorService eventExecutor;
    protected Connector connector;
    protected List<Plugin> plugins;

    public KBCClient(CoreImpl core, File pluginsFolder, String token) {
        this(core, pluginsFolder, token, null, null, null, null, null);
    }

    public KBCClient(
            CoreImpl core, File pluginsFolder, String token,
            /* Customizable components are following: */
            @Nullable NetworkClient networkClient,
            @Nullable EntityStorage storage,
            @Nullable EntityBuilder entityBuilder,
            @Nullable MessageBuilder msgBuilder,
            @Nullable EntityUpdater entityUpdater
    ) {
        if (pluginsFolder != null) {
            if (pluginsFolder.exists()) {
                Validate.isTrue(pluginsFolder.isDirectory(), "The provided pluginsFolder object is not a directory.");
            } else {
                pluginsFolder.mkdirs();
            }
        }
        this.core = core;
        this.pluginsFolder = pluginsFolder;
        this.core.init(this);
        this.networkClient = Optional.ofNullable(networkClient).orElseGet(() -> new NetworkClient(this, token));
        this.storage = Optional.ofNullable(storage).orElseGet(() -> new EntityStorage(this));
        this.entityBuilder = Optional.ofNullable(entityBuilder).orElseGet(() -> new EntityBuilder(this));
        this.msgBuilder = Optional.ofNullable(msgBuilder).orElseGet(() -> new MessageBuilder(this));
        this.entityUpdater = Optional.ofNullable(entityUpdater).orElseGet(() -> new EntityUpdater(this));
        this.internalPlugin = new InternalPlugin(this);
        this.eventExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "Event Executor"));
        this.shutdownLock = new ReentrantLock();
        this.shutdownCondition = this.shutdownLock.newCondition();
    }

    // The result of this method can prevent the users to execute the console command,
    //  so that some possible problems won't be caused.
    // (e.g. Kook user stopped the client)
    private CommandExecutor wrapConsoleCmd(Consumer<Object[]> reallyThingToRun) {
        return (sender, arguments, message) -> {
            if (sender instanceof User) {
                if (message != null) {
                    message.sendToSource("你不能这样做，因为你正在尝试执行仅后台可用的命令。");
                }
            } else {
                reallyThingToRun.accept(arguments);
            }
        };
    }

    public Core getCore() {
        return core;
    }

    public File getPluginsFolder() {
        return pluginsFolder;
    }

    public boolean isRunning() {
        return running;
    }

    // Note for hardcore developers:
    // You can also put this client into your project as a module to communicate with Kook
    // Call this to start KookBC, then you can use JKook API.
    // WARN: Set the JKook Core by constructing CoreImpl and call getCore().setCore() using it first,
    // or you will get NullPointerException.
    public synchronized void start() {
        // Print version information
        getCore().getLogger().info("Starting {} version {}", getCore().getImplementationName(), getCore().getImplementationVersion());
        getCore().getLogger().info("This VM is running {} version {} (Implementing API version {})", getCore().getImplementationName(), getCore().getImplementationVersion(), getCore().getAPIVersion());
        getCore().getLogger().info("Working directory: {}", new File(".").getAbsolutePath());

        core.getLogger().debug("Fetching Bot user object");
        User botUser = getEntityBuilder().buildUser(
                getNetworkClient().get(HttpAPIRoute.USER_ME.toFullURL()));
        getStorage().addUser(botUser);
        core.setUser(botUser);
        enablePlugins();
        getCore().getLogger().debug("Loading all the plugins from plugins folder");
        getCore().getLogger().debug("Starting Network");
        startNetwork();
        getCore().getLogger().info("Done! Type \"help\" for help.");
    }

    protected void loadAllPlugins() {
        if (pluginsFolder == null) {
            return; // If you just want to use JKook API?
        }
        if (plugins != null) {
            return;
        }
        List<Plugin> plugins = new LinkedList<>(Arrays.asList(getCore().getPluginManager().loadPlugins(getPluginsFolder())));
        //noinspection ComparatorMethodParameterNotUsed
        plugins.sort(
                (o1, o2) ->
                        (o1.getDescription().getDepend().contains(o2.getDescription().getName())
                                ||
                                o1.getDescription().getSoftDepend().contains(o2.getDescription().getName()))
                                ? 1 : -1
        );

        this.plugins = plugins;
    }

    private void enablePlugins() {
        if (plugins == null) { // no plugins? do nothing!
            // if the plugins was not loaded, we can't continue
            // the loadPlugins method is protected, NOT private, so it is possible to be empty!
            return;
        }

        // we must call onLoad() first.
        for (Iterator<Plugin> iterator = plugins.iterator(); iterator.hasNext(); ) {
            Plugin plugin = iterator.next();

            // onLoad
            PluginDescription description = plugin.getDescription();
            plugin.getLogger().info("Loading {} version {}", description.getName(), description.getVersion());
            try {
                plugin.onLoad();
            } catch (Throwable e) {
                plugin.getLogger().error("Unable to load this plugin", e);
                iterator.remove();
            }
            // end onLoad
        }

        for (Iterator<Plugin> iterator = plugins.iterator(); iterator.hasNext(); ) {
            Plugin plugin = iterator.next();

            try {
                plugin.reloadConfig(); // ensure the default configuration will be loaded
            } catch (Exception e) {
                plugin.getLogger().error("Unable to load configuration", e);
            }

            // onEnable
            try {
                getCore().getPluginManager().enablePlugin(plugin);
            } catch (UnknownDependencyException e) {
                getCore().getLogger().error("Unable to enable plugin {} because unknown dependency detected.", plugin.getDescription().getName(), e);
                iterator.remove();
                continue;
            }
            if (!plugin.isEnabled()) {
                iterator.remove();
            } else {
                // Add the plugin into the known list to ensure the dependency system will work correctly
                getCore().getPluginManager().addPlugin(plugin);
            }
            // end onEnable
        }
    }

    protected void startNetwork() {
        connector = new Connector(this);
        connector.start();
    }

    // If you need console (normally you won't need it), call this
    // Note that this method won't return until the client stopped,
    // so call it in a single thread.
    public void loop() {
        getCore().getLogger().debug("Starting console");
        try {
            new Console(this).start();
            getCore().getLogger().debug("Missing starting console");
        } catch (Exception e) {
            getCore().getLogger().error("Unexpected situation happened during the main loop.", e);
        }
        getCore().getLogger().debug("REPL end");
    }

    // Shutdown this client, and loop() method will return after this method completes.
    public synchronized void shutdown() {
        getCore().getLogger().debug("Client shutdown request received");
        if (!isRunning()) {
            getCore().getLogger().debug("The client has already stopped");
            return;
        }
        running = false; // make sure the client will shut down if Bot wish the client stop.

        getCore().getLogger().info("Stopping client");
        getCore().getPluginManager().clearPlugins();

        shutdownNetwork();
        eventExecutor.shutdown();
        getCore().getLogger().info("Stopping core");
        getCore().getLogger().info("Stopping scheduler (If the application got into infinite loop, please kill this process!)");
        ((SchedulerImpl) getCore().getScheduler()).shutdown();
        getCore().getLogger().info("Client stopped");

        // region Emit shutdown signal
        shutdownLock.lock();
        try {
            shutdownCondition.signalAll();
        } finally {
            shutdownLock.unlock();
        }
        // endregion
    }

    public void waitUntilShutdown() {
        if (!running) {
            return;
        }
        shutdownLock.lock();
        try {
            while (isRunning()) {
                try {
                    shutdownCondition.await();
                } catch (InterruptedException ignored) {
                    // interrupted, but ignore
                }
            }
        } finally {
            shutdownLock.unlock();
        }
    }

    protected void shutdownNetwork() {
        if (connector != null) {
            connector.shutdown();
        }
    }

    public InternalPlugin getInternalPlugin() {
        return internalPlugin;
    }

    public EntityStorage getStorage() {
        return storage;
    }

    public EntityBuilder getEntityBuilder() {
        return entityBuilder;
    }

    public MessageBuilder getMessageBuilder() {
        return msgBuilder;
    }

    public EntityUpdater getEntityUpdater() {
        return entityUpdater;
    }

    public Connector getConnector() {
        return connector;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public Session getSession() {
        return session;
    }

    public ExecutorService getEventExecutor() {
        return eventExecutor;
    }

}