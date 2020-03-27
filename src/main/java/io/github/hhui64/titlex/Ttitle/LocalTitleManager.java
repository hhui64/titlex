package io.github.hhui64.titlex.Ttitle;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import io.github.hhui64.titlex.TConfig.ConfigManager;

public class LocalTitleManager {
  /**
   * 获取本地称号ID实例
   * 
   * @param id
   * @return 成功返回 Title 实例，失败返回 null
   */
  public static LocalTitle getLocalTitle(String id) {
    ConfigurationSection c = ConfigManager.titles.getConfigurationSection(id);
    if (c != null) {
      return new LocalTitle(c, id, c.getString("container"), c.getString("value"), c.getStringList("profile"),
          c.getDouble("one-day-price"), c.getDouble("permanent-price"), c.getInt("min-buy-days"),
          c.getInt("max-buy-days"), c.getBoolean("can-buy"));
    }
    return null;
  }

  /**
   * 创建称号实例
   * 
   * @param id
   * @param container
   * @param value
   * @param profile
   * @param oneDayPrice
   * @param permanentPrice
   * @param minBuyDays
   * @param maxBuyDays
   * @param isCanBuy
   * @return
   */
  public static Title createTitle(String id, String container, String value, List<String> profile, double oneDayPrice,
      double permanentPrice, int minBuyDays, int maxBuyDays, boolean isCanBuy) {
    return new Title(id, container, value, profile, oneDayPrice, permanentPrice, minBuyDays, maxBuyDays, isCanBuy);
  }

  /**
   * 获取所有称号ID的字符串 Set
   * 
   * @param player
   * @return
   */
  public static Set<String> getAllLocalTitlesIdSet() {
    return ConfigManager.titles.getKeys(false);
  }
}