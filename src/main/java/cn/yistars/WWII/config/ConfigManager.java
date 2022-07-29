package cn.yistars.WWII.config;

import cn.yistars.WWII.WWII;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    public static void loadConfig() {
        WWII.instance.saveDefaultConfig();
        WWII.instance.reloadConfig();
        WWII.instance.lang.saveDefaultConfig();
        WWII.instance.lang.reloadConfig();
        WWII.instance.area.saveDefaultConfig();
        WWII.instance.area.reloadConfig();
    }
}
