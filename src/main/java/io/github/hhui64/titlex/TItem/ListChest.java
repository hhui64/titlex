package io.github.hhui64.titlex.TItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.hhui64.titlex.TitleX;

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
    ItemStack[] playerNameTagList = getPlayerNameTagList(player);
    if (playerNameTagList != null) {
      inventoryChest.addItem(playerNameTagList);
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
    String titleId = lore.get(lore.size() - 1).substring(5);
    ConfigurationSection playerTitle = TitleX.instance.configManager.getPlayerTitleConfigurationSection(player,
        titleId);

    // 没有相关信息，则返回
    if (playerTitle == null)
      return;

    // 获取该称号佩戴状态
    boolean isForceUse = playerTitle.getBoolean("force-use");
    boolean isUse = playerTitle.getBoolean("use");

    // 判断是否强制佩戴，如果为强制佩戴则不修改称号 use 状态
    if (!isForceUse) {
      // 根据 lore 的 id 获取该称号ID在玩家库存中的状态信息
      boolean timeStatus = TitleX.instance.configManager.getPlayerTitleTimeStatus(player, titleId);

      // 过期了则不执行后续佩戴操作，直接返回，并从玩家库存中删除称号ID节点。
      if (!timeStatus) {
        TitleX.instance.configManager.delPlayerTitle(player, titleId);
        return;
      }

      // 佩戴该称号，保存配置
      TitleX.instance.configManager.changePlayerTitleConfig(player, titleId, "use", !isUse);
      TitleX.instance.configManager.saveConfig();

      // 重新载入容器
      TitleX.instance.listChest.reset(player, chest);
    }

    // 设置玩家聊天前缀，此时称号正式生效
    TitleX.instance.configManager.setPlayerActiveTitlesToChatPrefix(player);

    // 发送公屏佩戴提示
    // player.sendMessage(!use ? ("§e你现在戴上了 §r" + title + "§r§e 称号") : ("§7你摘下了 §r"
    // + title + "§r§7 称号"));
  }

  /**
   * 获取玩家的命名牌，返回命名牌 ItemStack 数组
   * 
   * @param player
   * @return
   */
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
          // 获取佩戴状态
          boolean isForceUse = playerTitle.getBoolean("force-use");
          boolean isUse = playerTitle.getBoolean("use");
          // 计算到期时间
          int now = (int) Math.ceil(System.currentTimeMillis() / 1000);
          int exp = playerTitle.getInt("exp");
          int at = (int) Math.ceil((double) ((double) (exp - now) / (double) 60 / (double) 60 / (double) 24));
          // 添加 lore 信息
          String dateMsg = exp < 0 ? TitleX.instance.configManager.getMessage("is-long")
              : (exp < now ? TitleX.instance.configManager.getMessage("is-exp")
                  : TitleX.instance.configManager.getMessage("is-day", String.valueOf(at)));
          lore.add(TitleX.instance.configManager.getMessage("date", dateMsg));
          lore.add(" ");
          if (isForceUse) {
            lore.add(TitleX.instance.configManager.getMessage("is-force-use"));
          } else if (isUse) {
            lore.add(TitleX.instance.configManager.getMessage("is-use"));
          }
          lore.add("§7id:" + titleId);
          list.add(TitleX.instance.nameTag.create(name, lore, isForceUse || isUse));
        }
      }
    }
    return list.toArray(new ItemStack[0]);
  }
}