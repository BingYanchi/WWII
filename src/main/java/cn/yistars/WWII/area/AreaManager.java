package cn.yistars.WWII.area;

import cn.yistars.WWII.WWII;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Objects;

public class AreaManager {
    public static GameArea area;
    public static void initArea() {
        FileConfiguration areaConfig = WWII.instance.area.getConfig();

        Location waitLoc = getLocation(Objects.requireNonNull(areaConfig.getString("waitLocation")));

        ArrayList<Location> blueLoc = new ArrayList<>();
        for (String locString : areaConfig.getStringList("blueLocation")) {
            blueLoc.add(getLocation(locString));
        }

        ArrayList<Location> redLoc = new ArrayList<>();
        for (String locString : areaConfig.getStringList("redLocation")) {
            redLoc.add(getLocation(locString));
        }

        area = new GameArea(areaConfig.getString("displayName"), waitLoc, blueLoc, redLoc, areaConfig.getInt("maxPlayer"), areaConfig.getInt("minPlayer"), areaConfig.getInt("targetScore"), areaConfig.getInt("gameTime"));

        // GameRule
        World world = blueLoc.get(0).getWorld();
        assert world != null;
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setPVP(false);
        world.setDifficulty(Difficulty.PEACEFUL);
    }

    private static Location getLocation(String locString) {

        String[] locList = locString.split(", ");
        String worldName = locList[0];

        double x = Double.parseDouble(locList[1]);
        double y = Double.parseDouble(locList[2]);
        double z = Double.parseDouble(locList[3]);

        float yaw = Float.parseFloat(locList[4]);
        float pitch = Float.parseFloat(locList[5]);

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
