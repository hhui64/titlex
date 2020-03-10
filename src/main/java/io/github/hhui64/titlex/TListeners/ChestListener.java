package io.github.hhui64.titlex.TListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TItem.Chest;

public class ChestListener implements Listener {
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    event.setCancelled(false);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    if (isTitleChest(inventory)) {
      Player player = (Player) event.getWhoClicked();
      if (player instanceof Player) {
        if (event.getRawSlot() < TitleX.instance.chest.chestSlot) {
          ItemStack currentItem = event.getCurrentItem();
          if (currentItem != null && currentItem.getType() == Material.NAME_TAG) {
            nameTagClick(player, inventory, currentItem, event.getClick());
          }
          event.setCancelled(true);
          return;
        }
        if (event.isShiftClick()) {
          event.setCancelled(true);
          return;
        }
      }
    }
    event.setCancelled(false);
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    Inventory inventory = event.getInventory();
    if (isTitleChest(inventory)) {
      Player player = (Player) event.getWhoClicked();
      if (player instanceof Player) {
        Set<Integer> rawSlots = event.getRawSlots();
        for (int i = 0; i < TitleX.instance.chest.chestSlot; i++) {
          if (rawSlots.contains(i)) {
            event.setCancelled(true);
            return;
          }
        }
      }
    }
    event.setCancelled(false);
  }

  private boolean isTitleChest(Inventory inventory) {
    return inventory.getTitle().equalsIgnoreCase(TitleX.instance.chest.chestTitle);
  }

  public void nameTagClick(Player player, Inventory chest, ItemStack itemStack, ClickType clickType) {
    // 获取被点击的命名牌的 lore 信息
    List<String> lore = itemStack.getItemMeta().getLore();
    String titleId = lore.get(lore.size() - 1).substring(5);

    // 根据 lore 的 id 获取该称号ID在玩家库存中的状态信息
    boolean status = TitleX.instance.configManager.getPlayerTitleTimeStatus(player, titleId);
    // 过期了则不执行后续佩戴操作，直接返回，并从玩家库存中删除称号ID节点。
    if (!status) {
      TitleX.instance.configManager.delPlayerTitle(player, titleId);
      return;
    }

    // 获取该称号佩戴状态
    boolean use = TitleX.instance.configManager.getPlayerTitleActiveStatus(player, titleId); // lore.get(lore.size() - 2) == "§b已佩戴";

    // 佩戴该称号，保存配置
    TitleX.instance.configManager.changePlayerTitleConfig(player, titleId, "use", !use);
    TitleX.instance.configManager.saveConfig();

    // 重新载入容器
    TitleX.instance.chest.resetChest(player, chest);

    // 设置玩家聊天前缀，此时称号正式生效
    TitleX.instance.configManager.setPlayerActiveTitlesToChatPrefix(player);
    // player.sendMessage(!use ? ("§e你现在戴上了 §r" + title + "§r§e 称号") : ("§7你摘下了 §r"
    // + title + "§r§7 称号"));
  }
}