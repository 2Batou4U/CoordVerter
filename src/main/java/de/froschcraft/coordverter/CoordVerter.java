package de.froschcraft.coordverter;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public final class CoordVerter extends JavaPlugin implements Listener
{
    // For formatting Minecraft messages.
    @NotNull
    private final MiniMessage mm = MiniMessage.miniMessage();
    @NotNull
    private final FileConfiguration config = this.getConfig();
    @NotNull
    private final Logger log = this.getLogger();

    @Override
    public void onEnable() {
        // Load the logger
        log.info(config.getString("messages.starting-message"));

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("convert")).setExecutor(new CoordVerterCommand(config, mm));
        Objects.requireNonNull(this.getCommand("convert")).setTabCompleter(new CoordVerterTabCompleter());
    }

    public void onDisable() {
        log.info(config.getString("messages.starting-message"));
    }
}
