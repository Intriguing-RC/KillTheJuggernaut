package me.intriguing.juggernautevent.util;

import lombok.Getter;
import me.intriguing.juggernautevent.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class ConfigurableLocation {

    @Getter private Location location;
    private static final Core plugin = Core.getPlugin();

    public ConfigurableLocation(ConfigurationSection section) {
        String world = Objects.requireNonNull(section.getString("world"));
        int x = section.getInt("x");
        int y = section.getInt("y");
        int z = section.getInt("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        if (Bukkit.getWorld(world) == null) {
            throw new NullPointerException("World " + world + " is null!");
        }

        location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            String locationString = location.toString();
            location = null;
            throw new RuntimeException("Block below is air! Location: " + locationString);
        }

    }
}
