package cn.yistars.WWII.command;

import cn.yistars.WWII.WWII;
import cn.yistars.WWII.area.AreaManager;
import cn.yistars.WWII.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command commands, String label, String[] args) {
        if (!sender.hasPermission("WWII.admin")) {
            sender.sendMessage("无权使用");
            return false;
        }
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {
            case "forcestart":
                AreaManager.area.goNextStatus();

                break;
            case "reload":
                ConfigManager.loadConfig();

                sender.sendMessage("成功重载配置文件");
                break;
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("forceStart", "reload");
        } else {
            return Collections.emptyList();
        }
    }
}
