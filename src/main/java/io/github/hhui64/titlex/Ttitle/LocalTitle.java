package io.github.hhui64.titlex.Ttitle;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class LocalTitle extends Title {
  /**
   * 本地称号的配置节点
   */
  public ConfigurationSection c;

  public LocalTitle(ConfigurationSection c, String id, String container, String value,
      List<String> profile, double oneDayPrice, double permanentPrice, int minBuyDays, int maxBuyDays, boolean isCanBuy) {
    super(id, container, value, profile, oneDayPrice, permanentPrice, minBuyDays, maxBuyDays, isCanBuy);
    this.c = c;
  }
}