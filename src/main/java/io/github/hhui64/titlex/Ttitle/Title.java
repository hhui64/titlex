package io.github.hhui64.titlex.Ttitle;

import java.math.BigDecimal;
import java.util.List;

import org.bukkit.ChatColor;

public class Title {
  public String id;
  public String container;
  public String value;
  public List<String> profile;
  public double oneDayPrice;
  public double permanentPrice;
  public int minBuyDays;
  public int maxBuyDays;

  public Title(String id, String container, String value, List<String> profile, 
      double oneDayPrice,
      double permanentPrice, int minBuyDays, int maxBuyDays) {
    this.id = id;
    this.container = container;
    this.value = value;
    this.profile = profile;
    this.oneDayPrice = oneDayPrice;
    this.permanentPrice = permanentPrice;
    this.minBuyDays = minBuyDays;
    this.maxBuyDays = maxBuyDays;
  }

  public final String getTitleString() {
    return ChatColor.translateAlternateColorCodes('&', String.format(this.container, this.value));
  }

  public final boolean isCanBuy() {
    return (this.minBuyDays + this.maxBuyDays > 0) && this.oneDayPrice >= 0 && this.permanentPrice >= 0;
  }

  public final double getBuyDaysTotalMoney(int days) {
    if (isCanBuy()) {
      return days < 0 ? this.permanentPrice
          : new BigDecimal(days * this.oneDayPrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    } else {
      return -1;
    }
  }
}