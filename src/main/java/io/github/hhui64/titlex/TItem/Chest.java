package io.github.hhui64.titlex.TItem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Chest {
  public String chestTitle;
  public int chestSlot;

  public Chest(String chestTitle, int chestSlot) {
    this.chestTitle = chestTitle;
    this.chestSlot = chestSlot * 9;  
  }

  public abstract void open(Player player);

  public abstract void reset(Player player, Inventory inventoryChest);
}