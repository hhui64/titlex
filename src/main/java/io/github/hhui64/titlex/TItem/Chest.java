package io.github.hhui64.titlex.TItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;

public class Chest {
  public String chestTitle = "称号陈列柜";
  public int chestSlot = 3 * 9;

  public Chest(String chestTitle, int chestSlot) {
    this.chestTitle = chestTitle;
    this.chestSlot = chestSlot * 9;
  }

  public void openChest(Player player) {
    Inventory chest = Bukkit.createInventory(null, this.chestSlot, this.chestTitle);
    resetChest(player, chest);
    player.openInventory(chest);
  }

  public void resetChest(Player player, Inventory chest) {
    chest.clear();
    ItemStack[] playerNameTagList = getPlayerNameTagList(player);
    if (playerNameTagList != null) {
      chest.addItem(playerNameTagList);
    }
  }

  public ItemStack[] getPlayerNameTagList(Player player) {
    // 获取玩家拥有的称号ID字符串
    Set<String> titles = TitleX.instance.configManager.getPlayerTitles(player);
    if (titles.isEmpty())
      return null;
    // 物品合集，只能用 List 否则排序会出错
    List<ItemStack> list = new ArrayList<>(titles.size());
    // 遍历称号ID合集
    for (String titleId : titles) {
      // 获取该称号ID的 ConfigurationSection
      ConfigurationSection title = TitleX.instance.configManager.getTitle(titleId);
      // 判断是否在 titles 列表中存在这个 id，如果没有则不生成 nametag
      if (title != null) {
        String container = title.getString("container");
        String title_ = title.getString("title");
        String name = String.format(container, title_);
        // 生成命名牌
        List<String> lore = new ArrayList<String>();
        // 获取指定玩家该称号ID的 ConfigurationSection
        ConfigurationSection playerTitle = TitleX.instance.configManager.getPlayerTitleConfigurationSection(player,
            titleId);
        // 判断是否获取成功，且称号没过期
        if (playerTitle != null && TitleX.instance.configManager.getPlayerTitleTimeStatus(player, titleId)) {
          // int iat = item.getInt("iat");
          int now = (int) Math.ceil(System.currentTimeMillis() / 1000);
          int exp = playerTitle.getInt("exp");
          int at = (int) Math.ceil((double) ((double) (exp - now) / (double) 60 / (double) 60 / (double) 24));
          boolean use = playerTitle.getBoolean("use");
          lore.add("§f剩余时间: " + (exp < 0 ? "§a永久" : (exp < now ? "§c已过期" : ("§6" + String.valueOf(at) + "天"))));
          lore.add(" ");
          if (use) {
            lore.add("§b已佩戴");
          }
          lore.add("§7id:" + titleId);
          list.add(TitleX.instance.nameTag.create(name, lore, use));
        }
      }
    }
    return list.toArray(new ItemStack[0]);
  }
}