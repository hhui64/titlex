package io.github.hhui64.titlex.TItem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShopChest extends Chest {

  public ShopChest(String chestTitle, int chestSlot) {
    super(chestTitle, chestSlot);
  }

  @Override
  public void open(Player player) {
    Inventory inventoryChest = Bukkit.createInventory(null, this.chestSlot, this.chestTitle);
    player.openInventory(inventoryChest);
  }

  @Override
  public void reset(Player player, Inventory inventoryChest) {
    // TODO Auto-generated method stub

  }

}