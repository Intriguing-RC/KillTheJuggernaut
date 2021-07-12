package com.theplonk.juggernautevent.managers;

import com.theplonk.juggernautevent.Core;
import com.theplonk.juggernautevent.util.ConfigurableItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    public static final Core plugin = Core.getPlugin();

    public static Map<Integer, ItemStack> getJuggernautInventory() {
        List<Map<?, ?>> items = plugin.getSettingsManager().juggernautInventory;
        Map<Integer, ItemStack> inventory = new HashMap<>();
        items.forEach(map -> inventory.put((int) map.get("slot"), ConfigurableItem.build(map)));

        return inventory;
    }

    public static Map<Integer, ItemStack> getNormalInventory() {
        List<Map<?, ?>> items = plugin.getSettingsManager().normalInventory;
        Map<Integer, ItemStack> inventory = new HashMap<>();
        items.forEach(map -> inventory.put((int) map.get("slot"), ConfigurableItem.build(map)));

        return inventory;
    }

}
