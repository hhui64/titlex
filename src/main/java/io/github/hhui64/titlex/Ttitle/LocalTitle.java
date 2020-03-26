package io.github.hhui64.titlex.Ttitle;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class LocalTitle extends Title {
  public ConfigurationSection configurationSection;

  public LocalTitle(ConfigurationSection configurationSection, String id, String container, String value,
      List<String> profile, double oneDayPrice, double permanentPrice, int minBuyDays, int maxBuyDays) {
    super(id, container, value, profile, oneDayPrice, permanentPrice, minBuyDays, maxBuyDays);
    this.configurationSection = configurationSection;
  }
}