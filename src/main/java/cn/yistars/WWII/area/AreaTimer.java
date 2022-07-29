package cn.yistars.WWII.area;

import cn.yistars.WWII.WWII;
import cn.yistars.WWII.config.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AreaTimer {

    public Boolean gameEndTimer = false;
    public Boolean gameStartTimer = false;
    public Boolean gameStopTimer = false;

    public AreaTimer (GameArea area) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(WWII.instance, () -> {
            // 游戏结束倒计时
            if (gameEndTimer) {
                area.gameTime -= 1;

                if (area.gameTime <= 0) {
                    gameEndTimer = false;
                    area.goNextStatus();
                }
            }

            // 游戏即将开始
            if (gameStartTimer) {
                area.startTime -= 1;

                if (area.startTime <= 0) {
                    gameStartTimer = false;
                    area.goNextStatus();
                } else {
                    if (area.startTime <= 5) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            LangManager.sendMsg(player, "startIn", String.valueOf(area.startTime));
                        }
                    }
                }
            }
            // 游戏结束倒计时
            if (gameStopTimer) {
                area.endTime -= 1;

                if (area.endTime <= 0) {
                    gameEndTimer = false;
                    area.goNextStatus();
                }
            }
        }, 0, 20);
    }
}
