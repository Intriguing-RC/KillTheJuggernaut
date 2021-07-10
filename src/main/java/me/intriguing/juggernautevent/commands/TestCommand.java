package me.intriguing.juggernautevent.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Objects;

public class TestCommand extends SubCommand {

    @Override
    public String getLabel() {
        return "test";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            ItemStack itemTest = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionMeta = Objects.requireNonNull((PotionMeta) itemTest.getItemMeta());
            potionMeta.setBasePotionData(new PotionData(PotionType.valueOf("INSTANT_HEAL")));
            itemTest.setItemMeta(potionMeta);

            player.getInventory().addItem(itemTest);
        }
    }
}
