package cn.yistars.WWII.listener;

import cn.yistars.WWII.area.AreaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
         switch (AreaManager.area.getStatus()) {
             case LOADING:
                 event.setMotd("LOADING");
                 break;
             case WAITING:
                 event.setMotd("WAITING");
                 break;
             case STARTING:
                 event.setMotd("STARTING");
                 break;
             case PLAYING:
                 event.setMotd("PLAYING");
                 break;
             case ENDING:
                 event.setMotd("ENDING");
                 break;
             case RESTARTING:
                 event.setMotd("RESTARTING");
                 break;
             default:
                 event.setMotd("UNKNOWN");
         }
    }
}
