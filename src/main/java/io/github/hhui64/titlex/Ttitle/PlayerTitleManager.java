package io.github.hhui64.titlex.Ttitle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.THook.VaultApi;

public class PlayerTitleManager {
  /**
   * 获取指定玩家仓库中指定称号ID的称号实例
   * 
   * @param player
   * @param id
   * @return 成功返回 PlayerTitle 实例，失败返回 null
   */
  public static PlayerTitle getPlayerCurrentTitle(Player player, String id) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection c = ConfigManager.save.getConfigurationSection(uuid + ".titles." + id);
    if (c != null) {
      PlayerTitle playerCurrentTitle = new PlayerTitle(id, c.getBoolean("force-use"), c.getBoolean("use"),
          c.getInt("iat"), c.getInt("exp"));
      return playerCurrentTitle != null && playerCurrentTitle.localTitle != null ? playerCurrentTitle : null;
    }
    return null;
  }

  /**
   * 获取指定玩家仓库中的所有称号实体
   * 
   * @param player
   * @return
   */
  public static List<PlayerTitle> getPlayerAllTitles(Player player) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection c = ConfigManager.save.getConfigurationSection(uuid + ".titles");
    List<PlayerTitle> playerAllTitles = new ArrayList<PlayerTitle>();
    if (c != null) {
      Set<String> titleIdSet = c.getKeys(false);
      if (!titleIdSet.isEmpty()) {
        for (String id : titleIdSet) {
          PlayerTitle playerCurrentTitle = getPlayerCurrentTitle(player, id);
          if (playerCurrentTitle != null)
            playerAllTitles.add(playerCurrentTitle);
        }
      }
    }
    return playerAllTitles;
  }

  /**
   * 获取指定玩家仓库中的所有称号ID字符串 Set
   * 
   * @param player
   * @return
   */
  public static Set<String> getPlayerAllTitlesIdSet(Player player) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection c = ConfigManager.save.getConfigurationSection(uuid + ".titles");
    if (c != null) {
      return c.getKeys(false);
    }
    return new HashSet<String>();
  }

  /**
   * 获取指定玩家仓库中的所有佩戴中且可用的称号实体
   * 
   * @param player
   * @return
   */
  public static List<PlayerTitle> getPlayerAllActiveTitles(Player player) {
    List<PlayerTitle> playerAllTitles = getPlayerAllTitles(player);
    List<PlayerTitle> playerAllActiveTitles = new ArrayList<PlayerTitle>();
    for (PlayerTitle playerTitle : playerAllTitles) {
      if ((playerTitle.isForceUse || playerTitle.isUse) && !playerTitle.isExpired())
        playerAllActiveTitles.add(playerTitle);
    }
    return playerAllActiveTitles;
  }

  /**
   * <pre>
   * 获取玩家所拥有的称号，并拼接为聊天前缀字符串
   * 
   * 因为通过 vault.chat 设置玩家前缀的权重值大于
   * 用户组的前缀，所以必须获取该玩家所属组的组前缀
   * 将其添加在拼接完成的称号字符串结尾以正确显示玩
   * 家的组前缀。
   * </pre>
   * 
   * @param player
   * @return 拼接完成的称号字符串
   */
  public static String getPlayerAllActiveTitlesPrefixString(Player player) {
    List<PlayerTitle> playerAllActiveTitles = getPlayerAllActiveTitles(player);
    List<String> playerAllActiveTitlesPrefixStringList = new ArrayList<String>(0);
    String groupPrefix = VaultApi.chat.getGroupPrefix(player.getWorld(), VaultApi.permission.getPrimaryGroup(player));

    if (playerAllActiveTitles.isEmpty())
      return groupPrefix;

    for (PlayerTitle playerTitle : playerAllActiveTitles) {
      playerAllActiveTitlesPrefixStringList.add(playerTitle.localTitle.getTitleString());
    }
    String joinChar = "§r" + ConfigManager.config.getConfigurationSection("prefix-setting").getString("join-char")
        + "§r";
    String extendString = "§r"
        + ConfigManager.config.getConfigurationSection("prefix-setting").getString("extend-string") + "§r";
    return String.join(joinChar, playerAllActiveTitlesPrefixStringList) + extendString + groupPrefix;
  }

  /**
   * 向玩家称号仓库添加指定称号ID（如玩家已拥有则覆盖该称号ID节点下的配置）
   * 
   * @param player
   * @param id
   * @param days       时间(单位: 天)，如果 < 0 则为永久
   * @param isForceUse
   * @param isUse
   */
  public static void addPlayerCurrentTitle(Player player, String id, int days, boolean isForceUse, boolean isUse) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection c = ConfigManager.save.createSection(uuid + ".titles." + id);
    int nowTime = (int) (System.currentTimeMillis() / 1000);
    int expTime = days < 0 ? -1 : (nowTime + days * 24 * 60 * 60);
    c.set("force-use", isForceUse);
    c.set("use", isUse);
    c.set("iat", nowTime);
    c.set("exp", expTime);
    ConfigManager.save.set(uuid + ".titles." + id, c);
  }

  /**
   * 将指定称号ID从玩家称号仓库中删除
   * 
   * @param player
   * @param id
   */
  public static void delPlayerCurrentTitle(Player player, String id) {
    String uuid = player.getUniqueId().toString();
    ConfigManager.save.set(uuid + ".titles." + id, null);
    if (getPlayerAllTitles(player).isEmpty())
      ConfigManager.save.set(uuid, null);
  }

  /**
   * 改变玩家仓库中指定称号ID的佩戴状态
   * 
   * @param player
   * @param id
   * @param state
   */
  public static void changePlayerCurrentTitleUseState(Player player, String id, boolean state) {
    changePlayerCurrentTitleConfig(player, id, "use", state);
  }

  /**
   * 改变玩家仓库中指定称号ID的配置项
   * 
   * @param player
   * @param id
   * @param key
   * @param value
   */
  public static void changePlayerCurrentTitleConfig(Player player, String id, String key, Object value) {
    String uuid = player.getUniqueId().toString();
    ConfigManager.save.set(uuid + ".titles" + "." + id + "." + key, value);
  }

  /**
   * 清空玩家所有已过期的称号ID
   * 
   * @param player
   * @return 已清除掉的称号个数
   */
  public static int clearPlayerAllExpiredTitles(Player player) {
    List<PlayerTitle> playerAllTitles = getPlayerAllTitles(player);
    int i = 0;
    for (PlayerTitle playerTitle : playerAllTitles) {
      if (playerTitle.isExpired())
        delPlayerCurrentTitle(player, playerTitle.localTitle.id);
        i++;
    }
    return i;
  }

  /**
   * 清空玩家所有的称号ID（也会直接删除顶层UUID）
   * 
   * @param player
   */
  public static void clearPlayerAllTitles(Player player) {
    String uuid = player.getUniqueId().toString();
    ConfigManager.save.set(uuid, null);
  }

  /**
   * 更新玩家称号：获取玩家已佩戴的有效称号并通过 vault-api 设置玩家聊天前缀
   *
   * @param player
   */
  public static void updatePlayerPrefix(Player player) {
    try {
      VaultApi.chat.setPlayerPrefix(player, getPlayerAllActiveTitlesPrefixString(player));
    } catch (Exception e) {
      // player.sendMessage(Message.getMessage("failed-to-set-player-prefix"));
    }
  }
}