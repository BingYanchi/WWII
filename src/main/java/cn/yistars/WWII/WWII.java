package cn.yistars.WWII;

import cn.yistars.WWII.area.AreaManager;
import cn.yistars.WWII.command.AdminCommand;
import cn.yistars.WWII.config.ConfigAccessor;
import cn.yistars.WWII.config.ConfigManager;
import cn.yistars.WWII.listener.PlayerListener;
import cn.yistars.WWII.listener.ServerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class WWII extends JavaPlugin {
    public static WWII instance;

    public ConfigAccessor lang = new ConfigAccessor(this, "lang.yml");
    public ConfigAccessor area = new ConfigAccessor(this, "area.yml");
    private final Logger logger = Logger.getLogger("WWII");

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.loadConfig();

        AreaManager.initArea();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new ServerListener(), this);

        this.getCommand("WWII").setExecutor(new AdminCommand());

        // TODO 计分板
        // TODO tab Team

        logger.info("Enabled successfully.");
    }

    public void onDisable() {
        this.logger.info("Disabled successfully.");
    }
}
