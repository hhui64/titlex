package io.github.hhui64.titlex.TItem;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NameTag {
  public static ItemStack create(String name, List<String> lore, Boolean isActive) {
    ItemStack nameTag = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = nameTag.getItemMeta();
    meta.setDisplayName(name);
    if (isActive) {
      meta.addEnchant(Enchantment.LUCK, 1, false);
    }
    meta.setLore(lore);
    nameTag.setItemMeta(meta);
    return nameTag;
  }
}