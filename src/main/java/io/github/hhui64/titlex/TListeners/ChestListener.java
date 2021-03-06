package io.github.hhui64.titlex.TListeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.TMessage.Message;

public class ChestListener implements Listener {
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.isCancelled())
      return;
    Inventory inventory = event.getInventory();
    if (isTitleChest(event.getView())) {
      Player player = (Player) event.getWhoClicked();
      if (player instanceof Player) {
        if (event.getRawSlot() < inventory.getSize()) {
          ItemStack currentItem = event.getCurrentItem();
          if (currentItem != null && currentItem.getType() == Material.NAME_TAG) {
            if (event.getView().getTitle().equalsIgnoreCase(Message.getMessage("list-chest"))) {
              TitleX.instance.listChest.nameTagClick(player, inventory, currentItem, event.getClick());
            }
            if (event.getView().getTitle().equalsIgnoreCase(Message.getMessage("shop-chest"))) {
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
    if (event.isCancelled())
      return;
    Inventory inventory = event.getInventory();
    if (isTitleChest(event.getView())) {
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
  private boolean isTitleChest(InventoryView inventoryView) {
    return inventoryView.getTitle().equalsIgnoreCase(Message.getMessage("list-chest"))
        || inventoryView.getTitle().equalsIgnoreCase(Message.getMessage("shop-chest"));
  }
}