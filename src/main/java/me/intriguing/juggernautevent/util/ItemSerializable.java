package me.intriguing.juggernautevent.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemSerializable {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemSerializable(Map<?, ?> itemSerialized) {
        if (!itemSerialized.containsKey("type")) {
            itemStack = null;
            return;
        }

        itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial((String) itemSerialized.get("type"))));
        itemMeta = itemStack.getItemMeta();
        if (itemSerialized.containsKey("display-name")) {
            itemMeta.setDisplayName((String) itemSerialized.get("display-name"));
        }

        if (itemSerialized.containsKey("lore")) {
            if (itemSerialized.get("lore") instanceof List) {
                itemMeta.setLore((List<String>) itemSerialized.get("lore"));
            }
        }




    }
}
