package com.theplonk.juggernautevent.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemBuilder {

    @Getter private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack setType(Material material) {
        this.itemStack.setType(material);
        return this.itemStack;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta newItemMeta = this.getItemMeta();
        newItemMeta.setDisplayName(name);
        this.getItemStack().setItemMeta(newItemMeta);
        return this;
    }

    public ItemBuilder removeDisplayName() {
        ItemMeta newItemMeta = this.getItemMeta();
        newItemMeta.setDisplayName(null);
        this.getItemStack().setItemMeta(newItemMeta);
        return this;
    }

    public ItemBuilder removeEnchantment(String enchantment) {
        this.getItemStack().removeEnchantment(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(enchantment))));
        return this;
    }

    public ItemBuilder addLoreLine(String loreLine) {
        if (!this.getItemMeta().hasLore()) return this;
        ItemMeta newItemMeta = this.getItemMeta();
        List<String> lore = Objects.requireNonNull(newItemMeta.getLore());
        lore.add(loreLine);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta newItemMeta = this.getItemMeta();
        newItemMeta.setLore(lore);
        this.itemStack.setItemMeta(newItemMeta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchantment(String enchantment, int durability) {
        this.getItemStack().addUnsafeEnchantment(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(enchantment.toLowerCase()))), durability);
        return this;
    }

    public ItemBuilder addEnchantments(Map<String, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    public ItemBuilder setPotionEffect(String effect, boolean duration, boolean amplifier) {

        if (effect == null) return this;

        ItemMeta newItemMeta = this.getItemMeta();
         if (itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.POTION || itemStack.getType() == Material.TIPPED_ARROW) {
            PotionMeta newPotionMeta = (PotionMeta) newItemMeta.clone();
            String potionEffect = effect.toUpperCase();

            if ((!PotionType.valueOf(potionEffect).isExtendable() || !PotionType.valueOf(potionEffect).isUpgradeable())) {
                if (duration || amplifier) {
                    Bukkit.getLogger().severe("Potion Type: " + PotionType.valueOf(potionEffect) + " is not extendable or upgradable. Please check vanilla values and fix your config.");
                }

                newPotionMeta.setBasePotionData(new PotionData(PotionType.valueOf(potionEffect)));
            } else {
                newPotionMeta.setBasePotionData(new PotionData(PotionType.valueOf(potionEffect), duration, amplifier));
            }

            this.getItemStack().setItemMeta(newPotionMeta);
        }

        return this;
    }

    private ItemMeta getItemMeta() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            return itemMeta;
        }

        Bukkit.getLogger().severe("Item Meta of " + itemStack.serialize().toString() + " is null!");
        return null;
    }

    public ItemStack build() {
        return this.getItemStack();
    }
}
