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

package snw.kookbc.impl.plugin;

import net.deechael.kookdesktop.KookDesktopMainKt;
import org.slf4j.Logger;
import snw.jkook.Core;
import snw.jkook.plugin.Plugin;
import snw.jkook.plugin.PluginDescription;
import snw.jkook.util.Validate;
import snw.kookbc.impl.KBCClient;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

// A plugin implementation as a placeholder to call methods that require plugin instance.
// DO NOT USE THIS OUTSIDE snw.kookbc PACKAGE.
public final class InternalPlugin implements Plugin {
    private static final PluginDescription DESCRIPTION;

    static {
        DESCRIPTION = new PluginDescription(
                "KookDesktop",
                KookDesktopMainKt.getVERSION(),
                KookDesktopMainKt.getVERSION(),
                "Just a placeholder. Only for internal use.",
                "https://www.github.com/DeeChael/KookDesktop",
                "null",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    private final KBCClient client;

    // DO NOT USE THIS.
    // IF YOU WANT THE INSTANCE, USE KBCClient#getInternalPlugin instead.
    public InternalPlugin(KBCClient client) {
        Validate.notNull(client);
        this.client = client;
    }

    @Override
    public Core getCore() {
        return client.getCore();
    }

    @Override
    public File getDataFolder() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public PluginDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Logger getLogger() {
        return getCore().getLogger();
    }

    @Override
    public InputStream getResource(String arg0) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean isEnabled() {
        return true; // always true
    }

    @Override
    public void onDisable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void onEnable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void onLoad() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void reloadConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveDefaultConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveResource(String arg0, boolean arg1, boolean arg2) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setEnabled(boolean arg0) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
