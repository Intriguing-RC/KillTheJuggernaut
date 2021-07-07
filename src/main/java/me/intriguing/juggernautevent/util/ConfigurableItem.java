package me.intriguing.juggernautevent.util;

import lombok.Getter;
import me.intriguing.juggernautevent.Core;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurableItem {

    @Getter private static ItemStack item;
    public static final Core plugin = Core.getPlugin();

    public static ItemStack build(Map<?, ?> section) {
        Material type = (Material) Objects.requireNonNull(section.get("type"));
        int amount = 1;
        Map<String, Integer> enchantmentsHolder = new HashMap<>();
        String displayName = null;

        if (section.get("amount") != null) {
            amount = (int) section.get("amount");
        }

        if (section.get("display-name") != null) {
            displayName = (String) section.get("display-name");
        }

        if (((Map<?, ?>) section.get("flags")).get("enchantments") != null) {
            Map<?, ?> enchantments = (Map<?, ?>) ((Map<?, ?>) section.get("flags")).get("enchantments");
            enchantments.forEach((k, v) -> {
                enchantmentsHolder.put((String) k, (int) v);
            });
        }

        item = new ItemBuilder(type, amount)
                .setDisplayName(displayName)
                .addEnchantments(enchantmentsHolder)
                .build();

        return item;
    }
}
