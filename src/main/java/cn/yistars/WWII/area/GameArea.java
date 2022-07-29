package cn.yistars.WWII.area;

import cn.yistars.WWII.config.LangManager;
import cn.yistars.WWII.event.AreaPlayerJoinEvent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class GameArea {
    private final String areaName;
    private AreaStatus status;
    private final Location waitLoc;
    private final ArrayList<Location> blueLoc;
    private final ArrayList<Location> redLoc;
    private final Integer maxPlayer;
    private final Integer minPlayer;
    private final Integer gameEndTime;
    public Integer blueCount = 0;
    public Integer redCount = 0;
    public TeamStatus winTeam;
    public final HashMap<UUID, Integer> playerCount = new HashMap<>(); // 玩家击杀数
    private final Integer targetScore; // 达标分数
    private final ArrayList<UUID> redTeam = new ArrayList<>();; // 红队玩家
    private final ArrayList<UUID> blueTeam=  new ArrayList<>(); // 蓝队玩家
    private final ArrayList<UUID> waitPlayer = new ArrayList<>(); // 等待中的玩家，仅限未开始的时候可用
    private final ArrayList<UUID> spectators = new ArrayList<>(); // 观察者列表，仅限开始游戏后可用
    public final HashMap<UUID, Player> playerHashMap = new HashMap<>(); // 玩家实例与 UUID 映射表
    private final HashMap<UUID, TeamStatus> playerTeam = new HashMap<>(); // 玩家状态
    private final HashMap<UUID, Boolean> playerConnect = new HashMap<>(); // 玩家在线状态

    // 计时有关
    public Integer gameTime = 900; // 游戏剩余时间
    public Integer startTime = 120; // 游戏
    public Integer endTime = 10; // 游戏结束重启事件
    private final AreaTimer Timer;
    private final AreaScoreboard scoreboard;

    // 装备相关
    private final AreaPlayerBackpack playerBackpack;

    public GameArea (String areaName, Location waitLoc, ArrayList<Location> blueLoc, ArrayList<Location> redLoc, Integer maxPlayer, Integer minPlayer, Integer targetScore, Integer gameEndTime) {
        this.areaName = areaName;
        this.status = AreaStatus.WAITING;
        this.waitLoc = waitLoc;
        this.blueLoc = blueLoc;
        this.redLoc = redLoc;
        this.maxPlayer = maxPlayer;
        this.minPlayer = minPlayer;
        this.targetScore = targetScore;
        this.gameEndTime = gameEndTime;
        this.Timer = new AreaTimer(this);
        this.playerBackpack = new AreaPlayerBackpack();
        this.scoreboard = new AreaScoreboard(this);
    }

    /*
        操作类
     */

    /**
     * 进入下个阶段
     * 游戏进度中枢控制
     */
    public void goNextStatus() {
        switch (status) {
            case LOADING:
                status = AreaStatus.WAITING;
                break;
            case WAITING:
                status = AreaStatus.STARTING;
                break;
            case STARTING:
                // 停止计时器
                Timer.gameStartTimer = false;

                status = AreaStatus.PLAYING;
                Collections.shuffle(waitPlayer);
                int blueTeamMaxPlayer = (int) Math.round(waitPlayer.size()/2.0);
                // 开始游戏
                int blueTeamNum = 0, redTeamNum = 0;
                for (UUID player : waitPlayer) {
                    playerHashMap.put(player, Bukkit.getPlayer(player));
                    playerConnect.put(player, true);
                    playerCount.put(player, 0);

                    playerHashMap.get(player).setHealth(20);
                    playerHashMap.get(player).setFoodLevel(20);

                    // 如果蓝队没满就添加到蓝队，否则添加到红队
                    if (blueTeam.size() < blueTeamMaxPlayer) {
                        // 加入蓝队
                        blueTeam.add(player);
                        playerTeam.put(player, TeamStatus.BLUE);

                        playerHashMap.get(player).teleport(blueLoc.get(blueTeamNum));

                        blueTeamNum += 1;
                        // 计算数量
                        if (blueTeamNum >= blueLoc.size()) blueTeamNum = 0;
                        // 给予装备
                        playerBackpack.givePlayerItem(playerHashMap.get(player), TeamStatus.BLUE);
                        // 提示开始
                        playerHashMap.get(player).sendTitle(LangManager.getLang("blueTeamJoinTitle"), LangManager.getLang("blueTeamJoinSubTitle"));

                    } else {
                        // 加入红队
                        redTeam.add(player);
                        playerTeam.put(player, TeamStatus.RED);

                        playerHashMap.get(player).teleport(redLoc.get(redTeamNum));

                        redTeamNum += 1;
                        // 计算数量
                        if (redTeamNum >= redLoc.size()) redTeamNum = 0;
                        // 给予装备
                        playerBackpack.givePlayerItem(playerHashMap.get(player), TeamStatus.RED);
                        // 提示开始
                        playerHashMap.get(player).sendTitle(LangManager.getLang("redTeamJoinTitle"), LangManager.getLang("redTeamJoinSubTitle"));
                    }
                }
                // 启动计时器
                gameTime = gameEndTime;
                Timer.gameStartTimer = false;
                Timer.gameEndTimer = true;

                // 注册队伍
                scoreboard.createTeamScoreboard();

                // 启用 PVP
                World world = blueLoc.get(0).getWorld();
                assert world != null;
                world.setPVP(true);
                world.setDifficulty(Difficulty.HARD);

                break;
            case PLAYING:
                status = AreaStatus.ENDING;
                endGame();
                LangManager.sendEndMsg(this);
                break;
            case ENDING:
                status = AreaStatus.RESTARTING;
                Bukkit.shutdown();
                break;
        }
    }

    /**
     * 新增玩家
     * @param player 玩家
     */
    public void addPlayer(Player player) {
        AreaPlayerJoinEvent areaJoinEvent = new AreaPlayerJoinEvent(this, player);
        Bukkit.getPluginManager().callEvent(areaJoinEvent);

        if (status != AreaStatus.WAITING && status != AreaStatus.STARTING) areaJoinEvent.setCancelled(true);

        if (!areaJoinEvent.isCancelled()) {
            waitPlayer.add(player.getUniqueId());
            playerHashMap.put(player.getUniqueId(), player);

            // 传送
            player.teleport(waitLoc);
            player.getInventory().clear();
            player.setLevel(0);

            player.setGameMode(GameMode.ADVENTURE);

            // 如果计时器未开始且高于等于最低人数
            if (!Timer.gameStartTimer && waitPlayer.size() >= minPlayer) {
                goNextStatus();
                startTime = 120;
                Timer.gameStartTimer = true;
            } else if (waitPlayer.size() >= maxPlayer/2 && startTime > 60) {
                startTime = 60;
                // TODO 提示缩短倒计时
            } else if (waitPlayer.size() >= maxPlayer - 1 && startTime > 10) {
                startTime = 10;
                // TODO 提示缩短倒计时
            }
        }
    }

    public void removePlayer(Player player) {
        switch (status) {
            case WAITING: case STARTING:
                waitPlayer.remove(player.getUniqueId());
                playerHashMap.remove(player.getUniqueId());

                if (Timer.gameStartTimer && waitPlayer.size() < minPlayer) {
                    // 重置计时器
                    status = AreaStatus.WAITING;
                    startTime = 120;
                    Timer.gameStartTimer = false;
                }
                break;
            case PLAYING:
                // TODO 通知所有人断开连接

                break;
            default:
                break;
        }
    }

    public void addSpectators(Player player) {
        // TODO 添加观察者
        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player.getUniqueId());
        scoreboard.addSpectator(player);
    }

    public void removeSpectators(Player player) {
        spectators.remove(player.getUniqueId());
    }

    public void updatePlayer(Player player) {
        if (!playerHashMap.containsKey(player.getUniqueId())) return;
        playerHashMap.put(player.getUniqueId(), player);
    }

    public Player getKiller(Player player) {
        if (player.getKiller() == null) {
            // 如果非人为死亡
            if (player.getLastDamageCause() != null) {
                // 如果有上一个伤害来源
                if (player.getLastDamageCause().getEntity().getType() == EntityType.PLAYER) {
                    return (Player) player.getLastDamageCause().getEntity();
                }
            }
            return null;
        } else {
            // 如果有击杀者则返回
            return player.getKiller().getPlayer();

        }
    }

    /**
     * 重生玩家
     * @param player 重生的玩家
     */
    public void spawnPlayer(Player player) {
        if (player == null) return;

        switch (getPlayerTeam(player.getUniqueId())) {
            case RED:
                Collections.shuffle(redLoc);
                player.teleport(redLoc.get(0));

                playerBackpack.givePlayerItem(player, TeamStatus.RED);
                break;
            case BLUE:
                Collections.shuffle(blueLoc);
                player.teleport(blueLoc.get(0));

                playerBackpack.givePlayerItem(player, TeamStatus.BLUE);
                break;
        }
    }

    public void endGame() {
        if (redCount > blueCount) {
            // 红队胜利
            winTeam = TeamStatus.RED;
            for (UUID uuid : redTeam) {
                if (!playerConnect.get(uuid)) return;

                playerHashMap.get(uuid).sendTitle(LangManager.getLang("redWinTitle"), LangManager.getLang("redWinSubTitle"));

                playerHashMap.get(uuid).setGameMode(GameMode.SPECTATOR);
            }

            for (UUID uuid : blueTeam) {
                if (!playerConnect.get(uuid)) return;

                playerHashMap.get(uuid).sendTitle(LangManager.getLang("blueLossTitle"), LangManager.getLang("blueLossSubTitle"));

                playerHashMap.get(uuid).setGameMode(GameMode.SPECTATOR);
            }

            // TODO 为观察者提示的胜利语句
        } else if (blueCount > redCount) {
            // 蓝队胜利
            winTeam = TeamStatus.BLUE;
            for (UUID uuid : blueTeam) {
                if (!playerConnect.get(uuid)) return;

                playerHashMap.get(uuid).sendTitle(LangManager.getLang("blueWinTitle"), LangManager.getLang("blueWinSubTitle"));

                playerHashMap.get(uuid).setGameMode(GameMode.SPECTATOR);
            }

            for (UUID uuid : redTeam) {
                if (!playerConnect.get(uuid)) return;

                playerHashMap.get(uuid).sendTitle(LangManager.getLang("redLossTitle"), LangManager.getLang("redLossSubTitle"));

                playerHashMap.get(uuid).setGameMode(GameMode.SPECTATOR);
            }
        } else {
            // TODO 平局加时
            winTeam = TeamStatus.NONE;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("很奇妙, 平局了, 不是么?");
            }
        }

        // 启动重启倒计时
        Timer.gameEndTimer = false;
        Timer.gameStopTimer = true;
    }

    /**
     * 增加团队分数
     * @param team 团队
     */
    public void addTeamCount(TeamStatus team) {
        // TODO 加分事件
        switch (team) {
            case BLUE:
                blueCount += 1;
                // 检查胜利
                if (blueCount.equals(targetScore)) {
                    goNextStatus();
                    Timer.gameEndTimer = false;
                }
                break;
            case RED:
                redCount += 1;
                // 检查胜利
                if (redCount.equals(targetScore)) {
                    // 胜利
                    goNextStatus();
                    Timer.gameEndTimer = false;
                }
                break;
        }
    }

    public void addPlayerCount(UUID uuid) {
        if (!playerCount.containsKey(uuid)) return;
        playerCount.put(uuid, playerCount.get(uuid) + 1);
    }

    public void setPlayerConnect(UUID uuid, Boolean isConnect) {
        if (!playerConnect.containsKey(uuid)) return;
        playerConnect.put(uuid, isConnect);
    }

    /*
        查询类
     */
    public Boolean isJoinedPlayer(UUID uuid) {
        return playerHashMap.containsKey(uuid);
    }

    public AreaStatus getStatus() {
        return status;
    }

    public String getAreaName() {
        return areaName;
    }

    public Location getWaitLoc() {
        return this.waitLoc;
    }

    public ArrayList<Location> getBlueLoc() {
        return this.blueLoc;
    }

    public ArrayList<Location> getRedLoc() {
        return this.redLoc;
    }

    public Integer getMaxPlayer() {
        return this.maxPlayer;
    }

    public Integer getMinPlayer() {
        return this.minPlayer;
    }

    public Integer getPlayerCount(UUID uuid) {
        if (!playerCount.containsKey(uuid)) return -1;

        return playerCount.get(uuid);
    }

    public TeamStatus getPlayerTeam(UUID uuid) {
        return playerTeam.getOrDefault(uuid, TeamStatus.SPECTATOR);
    }
    /**
     * 获取当前玩家数量
     * @return 玩家数量，不可用为 -1
     */
    public Integer getNowPlayerNum() {
        switch (status) {
            case WAITING: case STARTING:
                return waitPlayer.size();
            case PLAYING:
                int playerCount = 0;
                for (Boolean isConnect : playerConnect.values()) {
                    if (isConnect) playerCount += 1;
                }
                return playerCount;
            default:
                return -1;
        }
    }

    public ArrayList<Player> getTeamPlayer(TeamStatus teamStatus) {
        ArrayList<Player> players = new ArrayList<>();
        switch (teamStatus) {
            case RED:
                for (UUID uuid : redTeam) {
                    players.add(playerHashMap.get(uuid));
                }
                break;
            case BLUE:
                for (UUID uuid : blueTeam) {
                    players.add(playerHashMap.get(uuid));
                }
                break;
            case SPECTATOR:
                for (UUID uuid : spectators) {
                    players.add(playerHashMap.get(uuid));
                }
                break;
        }
        return players;
    }
}
