package dev.timecoding.bouncybeds;

import dev.timecoding.bouncybeds.api.Metrics;
import dev.timecoding.bouncybeds.config.ConfigHandler;
import dev.timecoding.bouncybeds.listener.BouncyBedListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BouncyBeds extends JavaPlugin {


    private Metrics metrics;
    private ConfigHandler configHandler;

    @Override
    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();
        if(this.configHandler.getBoolean("bStats")) {
            this.metrics = new Metrics(this, 17346);
        }
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new BouncyBedListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
