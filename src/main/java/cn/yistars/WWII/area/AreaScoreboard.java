package cn.yistars.WWII.area;

import cn.yistars.WWII.WWII;
import cn.yistars.WWII.config.LangManager;

import com.nametagedit.plugin.NametagEdit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class AreaScoreboard {
    private final GameArea area;
    private final ScoreboardManager manager = Bukkit.getScoreboardManager();

    public AreaScoreboard (GameArea area) {
        this.area = area;

        ScoreboardTimer();
    }

    public void createTeamScoreboard() {

        for (Player player : area.getTeamPlayer(TeamStatus.RED)) {
            NametagEdit.getApi().clearNametag(player);
            NametagEdit.getApi().setPrefix(player, LangManager.getLang("tabRedTeam"));
        }
        for (Player player : area.getTeamPlayer(TeamStatus.BLUE)) {
            NametagEdit.getApi().clearNametag(player);
            NametagEdit.getApi().setPrefix(player, LangManager.getLang("tabBlueTeam"));
        }
        for (Player player : area.getTeamPlayer(TeamStatus.SPECTATOR)) {
            NametagEdit.getApi().clearNametag(player);
            NametagEdit.getApi().setPrefix(player, LangManager.getLang("tabSpectator"));
        }
    }

    public void addSpectator(Player player) {
        NametagEdit.getApi().clearNametag(player);
        NametagEdit.getApi().setPrefix(player, LangManager.getLang("tabSpectator"));
    }

    private void reloadScoreboard(Player player) {
        switch (area.getStatus()) {
            case WAITING:
                assert manager != null;
                Scoreboard board = manager.getNewScoreboard();

                Objective obj = board.registerNewObjective(player.getUniqueId().toString(), "dummy", LangManager.getLang("Scoreboard.Title"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                String[] lineList = LangManager.getScoreboardLang("Scoreboard.Content.Waiting", area, player).split("\n");
                int i = lineList.length;

                for (String line : lineList) {
                    if (line.equals("")) line = " ";

                    Score lineScore = obj.getScore(line);
                    lineScore.setScore(i);

                    i -= 1;
                }

                player.setScoreboard(board);
                break;
            case STARTING:
                // 更新游戏
                assert manager != null;
                board = manager.getNewScoreboard();

                obj = board.registerNewObjective(player.getUniqueId().toString(), "dummy", LangManager.getLang("Scoreboard.Title"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                lineList = LangManager.getScoreboardLang("Scoreboard.Content.Starting", area, player).split("\n");
                i = lineList.length;
                for (String line : lineList) {
                    if (line.equals("")) line = " ";

                    Score lineScore = obj.getScore(line);
                    lineScore.setScore(i);

                    i -= 1;
                }

                player.setScoreboard(board);
                break;
            case PLAYING:
                assert manager != null;
                board = manager.getNewScoreboard();

                obj = board.registerNewObjective(player.getUniqueId().toString(), "dummy", LangManager.getLang("Scoreboard.Title"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                lineList = LangManager.getScoreboardLang("Scoreboard.Content.Playing", area, player).split("\n");
                i = lineList.length;
                for (String line : lineList) {
                    if (line.equals("")) line = " ";

                    Score lineScore = obj.getScore(line);
                    lineScore.setScore(i);

                    i -= 1;
                }

                player.setScoreboard(board);
                break;
        }
    }

    private void ScoreboardTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(WWII.instance, () -> {

            //if (Bukkit.getOnlinePlayers().size() == 0)

            switch (area.getStatus()) {
                case WAITING: case STARTING: case PLAYING:
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        reloadScoreboard(player);
                    }
                    break;
            }
        }, 0, 10);
    }
}
