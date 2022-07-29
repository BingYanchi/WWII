package cn.yistars.WWII.listener;

import cn.yistars.WWII.area.AreaManager;
import cn.yistars.WWII.area.AreaStatus;
import cn.yistars.WWII.area.GameArea;
import cn.yistars.WWII.area.TeamStatus;
import cn.yistars.WWII.config.LangManager;
import cn.yistars.WWII.event.AreaPlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        GameArea area = AreaManager.area;

        switch (area.getStatus()) {
            case LOADING: case RESTARTING:
                event.getPlayer().kickPlayer("竞技场正在准备中...");
                break;
            case WAITING: case STARTING:
                area.addPlayer(event.getPlayer());

                // 通知所有人加入游戏
                for (Player player : Bukkit.getOnlinePlayers()) {
                    LangManager.sendMsg(player, "playerJoin", event.getPlayer().getName(), area.getNowPlayerNum().toString(), area.getMaxPlayer().toString());
                }

                for (String msg : LangManager.getLang("Info.Tutorial").split("\n")) {
                    event.getPlayer().sendMessage(msg);
                }
                // TODO 满人时禁止加入

                break;
            case PLAYING:
                if (area.isJoinedPlayer(event.getPlayer().getUniqueId())) {
                    // TODO 执行重新加入
                    area.updatePlayer(event.getPlayer());
                    area.setPlayerConnect(event.getPlayer().getUniqueId(), true);
                    area.removePlayer(event.getPlayer());

                    LangManager.sendConnectMsg(event.getPlayer(), area.getPlayerTeam(event.getPlayer().getUniqueId()), false);
                } else {
                    area.addSpectators(event.getPlayer());
                }
                break;
        }
        event.setJoinMessage("");
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        GameArea area = AreaManager.area;

        switch (area.getStatus()) {
            case WAITING: case STARTING:
                // TODO 通知所有人离开了
                area.removePlayer(event.getPlayer());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    LangManager.sendMsg(player, "playerLeave", event.getPlayer().getName(), area.getNowPlayerNum().toString(), area.getMaxPlayer().toString());
                }
            case PLAYING:
                // 如果不是已经加入游戏的玩家
                if (!area.isJoinedPlayer(event.getPlayer().getUniqueId())) {
                    area.removeSpectators(event.getPlayer());
                    return;
                }

                // TODO 通知所有人断开连接
                area.setPlayerConnect(event.getPlayer().getUniqueId(), false);

                LangManager.sendConnectMsg(event.getPlayer(), area.getPlayerTeam(event.getPlayer().getUniqueId()), true);
        }

        event.setQuitMessage("");
    }

    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent event) {
        GameArea area = AreaManager.area;
        event.setDeathMessage("");

        // 如果游戏不在运行中
        if (area.getStatus() != AreaStatus.PLAYING) {
            event.getEntity().spigot().respawn();
            event.getEntity().teleport(area.getWaitLoc());
            return;
        }
        // 如果玩家不是游戏中的玩家
        if (!area.isJoinedPlayer(event.getEntity().getUniqueId())) return;

        AreaPlayerDeathEvent areaPlayerDeathEvent = new AreaPlayerDeathEvent(area, event.getEntity());
        Bukkit.getPluginManager().callEvent(areaPlayerDeathEvent);

        if (!areaPlayerDeathEvent.isCancelled()) {

            switch (area.getPlayerTeam(event.getEntity().getUniqueId())) {
                case RED:
                    area.addTeamCount(TeamStatus.BLUE);
                    break;
                case BLUE:
                    area.addTeamCount(TeamStatus.RED);
                    break;
            }

            switch (area.getPlayerTeam(event.getEntity().getUniqueId())) {
                case RED: case BLUE:
                    // 玩家死亡提示
                    if (event.getEntity().getPlayer() != null) {
                        Player killer = area.getKiller(event.getEntity().getPlayer());

                        if (killer == null || killer == event.getEntity()) {
                            // 如果未知或击杀者与被杀者相同
                            LangManager.sendUnknownKillMsg(event.getEntity(), area.getPlayerTeam(event.getEntity().getUniqueId()));
                        } else {
                            LangManager.sendKillMsg(event.getEntity(), area.getPlayerTeam(event.getEntity().getUniqueId()), killer, area.getPlayerTeam(killer.getUniqueId()));
                            area.addPlayerCount(killer.getUniqueId());
                        }
                    }
                    event.getEntity().spigot().respawn();
                    event.getEntity().getInventory().clear();
                    area.spawnPlayer(event.getEntity());
                    break;
            }
        }
    }
}
