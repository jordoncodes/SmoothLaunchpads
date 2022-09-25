package itzjordon.smoothlaunchpads;

import itzjordon.smoothlaunchpads.launchpad.Launchpad;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmoothLaunchpads extends JavaPlugin {

    static SmoothLaunchpads instance = null;

    @Override
    public void onEnable() {
        setup();
        setEvents();
        setCommands();
    }

    @Override
    public void onDisable() {
    }


    public void setup() {
        instance = this;
    }

    public void setEvents() {
        getServer().getPluginManager().registerEvents(new Launchpad(), this);
    }

    public void setCommands() {
    }

    public static SmoothLaunchpads getInstance() {
        return instance;
    }

}
