package de.froschcraft.coordverter;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class CoordVerter extends JavaPlugin implements Listener
{
    @Override
    public void onEnable() {
        Logger log = getLogger();
        log.info("Starting CoordVerter.");
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("convert")).setExecutor(new CoordVerterCommand());
        Objects.requireNonNull(this.getCommand("convert")).setTabCompleter(new CoordVerterTabCompleter());
    }

    public void onDisable() {
        Logger log = getLogger();
        log.info("Stopping CoordVerter.");
    }
}
