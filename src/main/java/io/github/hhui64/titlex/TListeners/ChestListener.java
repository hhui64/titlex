package io.github.hhui64.titlex.TListeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;

public class ChestListener implements Listener {
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    // TODO: some...
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    if (isTitleChest(inventory)) {
      Player player = (Player) event.getWhoClicked();
      if (player instanceof Player) {
        if (event.getRawSlot() < inventory.getSize()) {
          ItemStack currentItem = event.getCurrentItem();
          if (currentItem != null && currentItem.getType() == Material.NAME_TAG) {
            if (inventory.getTitle().equalsIgnoreCase(TitleX.instance.configManager.getMessage("list-chest"))) {
              TitleX.instance.listChest.nameTagClick(player, inventory, currentItem, event.getClick());
            }
            if (inventory.getTitle().equalsIgnoreCase(TitleX.instance.configManager.getMessage("shop-chest"))) {
              // TODO: shop chest item click
            }
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
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    Inventory inventory = event.getInventory();
    if (isTitleChest(inventory)) {
      if (event.getWhoClicked() instanceof Player) {
        for (int i = 0; i < inventory.getSize(); i++) {
          if (event.getRawSlots().contains(i)) {
            event.setCancelled(true);
            return;
          }
        }
      }
    }
  }

  /**
   * 判断是否为插件箱子容器
   * 
   * @param inventory
   * @return
   */
  private boolean isTitleChest(Inventory inventory) {
    return inventory.getTitle().equalsIgnoreCase(TitleX.instance.configManager.getMessage("list-chest"))
        || inventory.getTitle().equalsIgnoreCase(TitleX.instance.configManager.getMessage("shop-chest"));
  }
}