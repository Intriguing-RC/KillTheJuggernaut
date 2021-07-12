package me.intriguing.juggernautevent.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class ConfigurableLocation {

    /**
     * Parses the location from a configuration section
     *
     * @param section The configuration section of the location to be parsed.
     * @return The parsed location.
     */
    public static Location build(ConfigurationSection section) {
        // Get and store the values from the configuration section
        String world = Objects.requireNonNull(section.getString("world"));
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        // Check if world is null
        if (Bukkit.getWorld(world) == null) {
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | World " + world + " is null!");
            return null;
        }

        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        // Check if the block below is not air, and that the player can stand on the block.
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            String locationString = location.toString();
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | Block below is air! Location: " + locationString);
            return null;
        }

        // Check if there is room for the player can stand on the block. (Check if block and above block is air)
        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR || location.getBlock().getType() != Material.AIR) {
            String locationString = location.toString();
            Bukkit.getLogger().severe("Configuration Section: " + section.getName() + " | There is no room for the player at the location! Location: " + locationString);
            return null;
        }

        return location;

    }
}
