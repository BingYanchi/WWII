package cn.yistars.WWII.event;

import cn.yistars.WWII.area.GameArea;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AreaPlayerJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final GameArea area;
    private Boolean isCancelled;

    public AreaPlayerJoinEvent(GameArea area, Player player) {
        this.area = area;
        this.player = player;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public GameArea getArea() {
        return area;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


}
