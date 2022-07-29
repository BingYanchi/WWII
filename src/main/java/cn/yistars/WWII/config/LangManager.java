package cn.yistars.WWII.config;

import cn.yistars.WWII.WWII;
import cn.yistars.WWII.area.GameArea;
import cn.yistars.WWII.area.TeamStatus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class LangManager {

    public static void sendMsg(Player player, String key, String... args) {
        String msg = WWII.instance.lang.getConfig().getString(key);
        assert msg != null;

        switch (key) {
            case "playerJoin": case "playerLeave":
                msg = msg.replace("%player%", args[0]);
                msg = msg.replace("%nowPlayers%", args[1]);
                msg = msg.replace("%maxPlayers%", args[2]);
                break;
            case "startIn":
                msg = msg.replace("%time%", args[0]);
                break;
            case "playerKill":
                msg = msg.replace("%killedTeam%", args[0]);
                msg = msg.replace("%killedPlayer%", args[1]);
                msg = msg.replace("%killerTeam%", args[2]);
                msg = msg.replace("%killerPlayer%", args[3]);
                break;
            case "playerUnknownKill":
                msg = msg.replace("%killedTeam%", args[0]);
                msg = msg.replace("%killedPlayer%", args[1]);
                break;
            case "playerDisconnect": case "playerReconnect":
                msg = msg.replace("%team%", args[0]);
                msg = msg.replace("%player%", args[1]);
                break;
        }

        if (!(msg.length() <= 0)) {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            player.sendMessage(msg);
        }
    }

    public static String getLang(String key) {
        String msg = WWII.instance.lang.getConfig().getString(key);
        assert msg != null;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getScoreboardLang(String key, GameArea area, Player player) {
        // 公共替换
        String msg = getLang(key);

        msg = msg.replace("%mapName%", area.getAreaName());
        msg = msg.replace("%nowPlayers%", area.getNowPlayerNum().toString());
        msg = msg.replace("%minPlayers%", area.getMinPlayer().toString());
        msg = msg.replace("%maxPlayers%",area.getMaxPlayer().toString());

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        msg = msg.replace("%localtime%", formatter.format(date));

        switch (area.getStatus()) {
            case STARTING:
                msg = msg.replace("%time%", area.startTime.toString());
                break;
            case PLAYING:
                // 队伍内容
                switch (area.getPlayerTeam(player.getUniqueId())) {
                    case RED:
                        msg = msg.replace("%teamName%", getLang("Scoreboard.Role.RedTeam"));
                        break;
                    case BLUE:
                        msg = msg.replace("%teamName%", getLang("Scoreboard.Role.BlueTeam"));
                        break;
                    case SPECTATOR:
                        msg = msg.replace("%teamName%", getLang("Scoreboard.Role.Spectator"));
                        break;
                }
                // 击杀数
                msg = msg.replace("%redKills%", area.redCount.toString());
                msg = msg.replace("%blueKills%", area.blueCount.toString());

                msg = msg.replace("%kills%", area.getPlayerCount(player.getUniqueId()).toString());
                msg = msg.replace("%gameTime%", area.gameTime.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private static String getTeamLang(TeamStatus team, Boolean format) {
        switch (team) {
            case RED:
                if (format) return getLang("redTeamName");
                else return getLang("Scoreboard.Role.RedTeam");
            case BLUE:
                if (format) return getLang("blueTeamName");
                else return getLang("Scoreboard.Role.BlueTeam");
            case SPECTATOR:
                if (format) return getLang("spectatorName");
                else return getLang("Scoreboard.Role.Spectator");
        }
        return "";
    }

    public static void sendKillMsg(Player killed, TeamStatus killedTeam, Player killer, TeamStatus killerTeam) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMsg(player, "playerKill", getTeamLang(killedTeam, true), killed.getName(), getTeamLang(killerTeam, true), killer.getName());
        }
    }

    public static void sendUnknownKillMsg(Player killed, TeamStatus killedTeam) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMsg(player, "playerUnknownKill", getTeamLang(killedTeam, true), killed.getName());
        }
    }

    public static void sendConnectMsg(Player disconnecter, TeamStatus team, Boolean Disconnect) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Disconnect) {
                sendMsg(player, "playerDisconnect", getTeamLang(team, true), disconnecter.getName());
            } else {
                sendMsg(player, "playerReconnect", getTeamLang(team, true), disconnecter.getName());
            }
        }
    }

    public static void sendEndMsg(GameArea area) {
        ArrayList<String> endMsg = new ArrayList<>();
        for (String msg : LangManager.getLang("Info.End").split("\n")) {
            msg = msg.replace("%winTeam%", getTeamLang(area.winTeam, false));
            msg = msg.replace("%redKills%", area.redCount.toString());
            msg = msg.replace("%blueKills%", area.blueCount.toString());

            Integer maxCount = 0;
            UUID mvpPlayer = null;
            for (UUID uuid : area.playerCount.keySet()) {
                if (area.playerCount.get(uuid) > maxCount) {
                    maxCount = area.playerCount.get(uuid);
                    mvpPlayer = uuid;
                }
            }

            if (mvpPlayer == null) {
                msg = msg.replace("%mvpPlayer%", "None");
                msg = msg.replace("%mvpTeam%", "");
            } else {
                msg = msg.replace("%mvpPlayer%", area.playerHashMap.get(mvpPlayer).getName());
                msg = msg.replace("%mvpTeam%", getTeamLang(area.getPlayerTeam(mvpPlayer), true));
            }

            msg = msg.replace("%mvpKills%", maxCount.toString());

            endMsg.add(msg);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String line : endMsg) {
                player.sendMessage(line);
            }
        }
    }
}
