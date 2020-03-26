package io.github.hhui64.titlex.TItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.Ttitle.PlayerTitle;
import io.github.hhui64.titlex.Ttitle.PlayerTitleManager;

public class ListChest extends Chest {

  public ListChest(String chestTitle, int chestSlot) {
    super(chestTitle, chestSlot);
  }

  @Override
  public void open(Player player) {
    Inventory inventoryChest = Bukkit.createInventory(null, this.chestSlot, this.chestTitle);
    reset(player, inventoryChest);
    player.openInventory(inventoryChest);
  }

  @Override
  public void reset(Player player, Inventory inventoryChest) {
    inventoryChest.clear();
    ItemStack[] nameTagItemStackArray = generateNameTagItemStackArray(player);
    if (nameTagItemStackArray != null) {
      inventoryChest.addItem(nameTagItemStackArray);
    }
  }

  /**
   * 命名牌被点击了
   * 
   * @param player
   * @param chest
   * @param itemStack
   * @param clickType
   */
  public void nameTagClick(Player player, Inventory chest, ItemStack itemStack, ClickType clickType) {
    // 获取被点击的命名牌的 lore 信息
    List<String> lore = itemStack.getItemMeta().getLore();
    String id = lore.get(lore.size() - 1).substring(5);

    // 获取玩家称号实体
    PlayerTitle playerTitle = PlayerTitleManager.getPlayerCurrentTitle(player, id);
    // 没有相关信息，则返回
    if (playerTitle == null)
      return;

    // 判断是否强制佩戴，如果为强制佩戴则不修改称号 use 状态
    if (!playerTitle.isForceUse) {
      // 过期了则不执行后续佩戴操作，直接返回，并从玩家仓库中删除该称号ID节点。
      if (playerTitle.isExpired()) {
        PlayerTitleManager.delPlayerCurrentTitle(player, id);
        return;
      }

      // 佩戴该称号，保存配置
      PlayerTitleManager.changePlayerCurrentTitleUseState(player, id, !playerTitle.isUse);;
      ConfigManager.savePlayerData();

      // 重新载入容器
      TitleX.instance.listChest.reset(player, chest);
    }

    // 设置玩家聊天前缀，此时称号正式生效
    PlayerTitleManager.updatePlayerPrefix(player);

    // 发送公屏佩戴提示
    // player.sendMessage(!use ? ("§e你现在戴上了 §r" + title + "§r§e 称号") : ("§7你摘下了 §r"
    // + title + "§r§7 称号"));
  }

  /**
   * 获取玩家仓库的有效称号并生成命名牌 ItemStack，然后返回命名牌 ItemStack 数组
   * 
   * @param player
   * @return 命名牌 ItemStack 数组
   */
  public ItemStack[] generateNameTagItemStackArray(Player player) {
    List<PlayerTitle> playerAllTitles = PlayerTitleManager.getPlayerAllTitles(player);

    if (playerAllTitles.isEmpty())
      return null;

    List<ItemStack> ItemStackList = new ArrayList<>(playerAllTitles.size());
    for (PlayerTitle playerTitle : playerAllTitles) {
      List<String> lore = new ArrayList<String>();
      // 有效时间
      String dateText = playerTitle.exp < 0 ? ConfigManager.getMessage("is-long")
          : (playerTitle.isExpired() ? ConfigManager.getMessage("is-exp")
              : ConfigManager.getMessage("is-day", String.valueOf(playerTitle.getEffectiveDays())));
      lore.add(ConfigManager.getMessage("date", dateText));
      lore.add(" ");
      // 佩戴状态
      if (playerTitle.isForceUse) {
        lore.add(ConfigManager.getMessage("is-force-use"));
      } else if (playerTitle.isUse) {
        lore.add(ConfigManager.getMessage("is-use"));
      }
      // 称号id信息
      lore.add("§7id:" + playerTitle.localTitle.id);
      ItemStackList.add(TitleX.instance.nameTag.create(playerTitle.localTitle.getTitleString(), lore,
          playerTitle.isForceUse || playerTitle.isUse));
    }
    return ItemStackList.toArray(new ItemStack[0]);
  }
}