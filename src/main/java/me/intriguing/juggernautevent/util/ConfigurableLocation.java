package me.intriguing.juggernautevent.util;

import me.intriguing.juggernautevent.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class ConfigurableLocation {

    private static final Core plugin = Core.getPlugin();

    public static Location build(ConfigurationSection section) {
        String world = Objects.requireNonNull(section.getString("world"));
        int x = section.getInt("x");
        int y = section.getInt("y");
        int z = section.getInt("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        if (Bukkit.getWorld(world) == null) {
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | World " + world + " is null!");
            return null;
        }

        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            String locationString = location.toString();
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | Block below is air! Location: " + locationString);
            return null;
        }

        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR || location.getBlock().getType() != Material.AIR) {
            String locationString = location.toString();
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | There is no room for the player at the location! Location: " + locationString);
            return null;
        }

        return location;

    }
}
