package cn.yistars.WWII.area;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class AreaPlayerBackpack {

    public AreaPlayerBackpack() {}

    public void givePlayerItem(Player player, TeamStatus team) {
        // 公共的装备
        ItemStack CrossBow = new ItemStack(Material.CROSSBOW);
        player.getInventory().setItem(0, CrossBow);

        ItemStack Bow = new ItemStack(Material.BOW);
        player.getInventory().setItem(1, Bow);

        ItemStack IronSword = new ItemStack(Material.IRON_SWORD);
        player.getInventory().setItem(2, IronSword);

        ItemStack Spyglass = new ItemStack(Material.SPYGLASS);

        ItemStack GoldApple = new ItemStack(Material.GOLDEN_APPLE, 4);

        ItemStack RabbitStew = new ItemStack(Material.RABBIT_STEW);

        ItemStack Bread = new ItemStack(Material.BREAD, 32);
        // 剧毒药水
        ItemStack LingeringPotion = new ItemStack(Material.LINGERING_POTION);
        PotionMeta Poison = (PotionMeta) LingeringPotion.getItemMeta();
        Poison.setBasePotionData(new PotionData(PotionType.POISON));
        //Poison.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 22, 0), true);
        LingeringPotion.setItemMeta(Poison);

        ItemStack TippedArrow = new ItemStack(Material.TIPPED_ARROW, 64);
        PotionMeta Arrow = (PotionMeta) TippedArrow.getItemMeta();
        Arrow.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
        TippedArrow.setItemMeta(Arrow);

        switch (team) {
            case RED:
                // 物品
                player.getInventory().setItem(4, LingeringPotion);
                player.getInventory().setItem(5, LingeringPotion);
                player.getInventory().setItem(6, LingeringPotion);

                player.getInventory().setItem(7, RabbitStew);
                player.getInventory().setItem(10, RabbitStew);
                player.getInventory().setItem(11, RabbitStew);

                player.getInventory().setItem(8, GoldApple);

                player.getInventory().setItem(9, Bread);

                player.getInventory().setItem(12, Spyglass);

                player.getInventory().setItem(13, TippedArrow);
                player.getInventory().setItem(14, TippedArrow);

                // 盔甲
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

                break;
            case BLUE:
                // 物品
                player.getInventory().setItem(3, Spyglass);

                player.getInventory().setItem(6, GoldApple);

                player.getInventory().setItem(7, RabbitStew);
                player.getInventory().setItem(11, RabbitStew);
                player.getInventory().setItem(12, RabbitStew);

                player.getInventory().setItem(8, Bread);

                player.getInventory().setItem(9, TippedArrow);
                player.getInventory().setItem(10, TippedArrow);

                // 盔甲
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

                break;
        }
    }
}
