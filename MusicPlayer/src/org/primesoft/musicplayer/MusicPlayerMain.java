/*
 * MusicPlayer a plugin that allows you to play custom music.
 * Copyright (c) 2014, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) MusicPlayer contributors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution,
 * 3. Redistributions of source code, with or without modification, in any form 
 *    other then free of charge is not allowed,
 * 4. Redistributions in binary form in any form other then free of charge is 
 *    not allowed.
 * 5. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 6. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 7. The original author of the software is allowed to sublicense the software 
 *    or its parts using any license terms he sees fit.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.primesoft.musicplayer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.musicplayer.commands.PlayMidiCommand;
import org.primesoft.musicplayer.commands.ReloadCommand;

/**
 *
 * @author SBPrime
 */
public class MusicPlayerMain extends JavaPlugin {

    private static final Logger s_log = Logger.getLogger("Minecraft.MusicPlayer");
    private static String s_prefix = null;
    private static final String s_logFormat = "%s %s";

    /**
     * The instance of the class
     */
    private static MusicPlayerMain s_instance;

    /**
     * Send message to the log
     *
     * @param msg
     */
    public static void log(String msg) {
        if (s_log == null || msg == null || s_prefix == null) {
            return;
        }

        s_log.log(Level.INFO, String.format(s_logFormat, s_prefix, msg));
    }

    /**
     * Sent message directly to player
     *
     * @param player
     * @param msg
     */
    public static void say(Player player, String msg) {
        if (player == null) {
            log(msg);
        } else {
            player.sendRawMessage(msg);
        }
    }

    /**
     * The instance of the class
     *
     * @return
     */
    public static MusicPlayerMain getInstance() {
        return s_instance;
    }

    /**
     * The plugin root command
     */
    private PluginCommand m_commandReload;

    private PluginCommand m_commandTest;

    private PluginCommand m_commandPlay;


    /**
     * The plugin version
     */
    private String m_version;

    /**
     * The music player
     */
    private MusicPlayer m_player;

    public String getVersion() {
        return m_version;
    }

    @Override
    public void onEnable() {
        Server server = getServer();
        PluginDescriptionFile desc = getDescription();
        s_prefix = String.format("[%s]", desc.getName());
        s_instance = this;

        m_version = desc.getVersion();
        m_player = new MusicPlayer(this, server.getScheduler());

        ReloadCommand reloadHandler = new ReloadCommand(this);
        PlayMidiCommand playHandler = new PlayMidiCommand(this, m_player);

        m_commandTest = getCommand("test");
        
        m_commandPlay = getCommand("playmidi");
        m_commandPlay.setExecutor(playHandler);

        m_commandReload = getCommand("mpreload");
        m_commandReload.setExecutor(reloadHandler);

        if (!reloadHandler.ReloadConfig(null)) {
            log("Error loading config");
            return;
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        m_player.stop();
        super.onDisable();
    }
    
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if (player != null) {
            if (command.equals(m_commandTest)) {
                doTest(player);
                return true;
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    private void doTest(final Player player) {
        player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.G));
    }
}
