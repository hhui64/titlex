package io.github.hhui64.titlex.Ttitle;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

public class Title {
  /**
   * 称号ID
   */
  public String id;
  /**
   * 称号文本的前缀和后缀
   */
  public String container;
  /**
   * 称号主体内容
   */
  public String value;
  /**
   * 称号说明信息
   */
  public List<String> profile;
  /**
   * 单日购买价格
   */
  public double oneDayPrice;
  /**
   * 永久购买价格
   */
  public double permanentPrice;
  /**
   * 最小购买天数
   */
  public int minBuyDays;
  /**
   * 最大购买天数
   */
  public int maxBuyDays;
  /**
   * 是否允许购买
   */
  public boolean isCanBuy;

  /**
   * 称号实体
   * 
   * @param id             {@link #id}
   * @param container      {@link #container}
   * @param value          {@link #value}
   * @param profile        {@link #profile}
   * @param oneDayPrice    {@link #oneDayPrice}
   * @param permanentPrice {@link #permanentPrice}
   * @param minBuyDays     {@link #minBuyDays}
   * @param maxBuyDays     {@link #maxBuyDays}
   * @param isCanBuy       {@link #isCanBuy}
   */
  public Title(String id, String container, String value, List<String> profile, double oneDayPrice,
      double permanentPrice, int minBuyDays, int maxBuyDays, boolean isCanBuy) {
    this.id = id;
    this.container = ChatColor.translateAlternateColorCodes('&', container);
    this.value = ChatColor.translateAlternateColorCodes('&', value);
    this.profile = profile.stream().map(item -> ChatColor.translateAlternateColorCodes('&', item))
        .collect(Collectors.toList());
    this.oneDayPrice = oneDayPrice;
    this.permanentPrice = permanentPrice;
    this.minBuyDays = minBuyDays;
    this.maxBuyDays = maxBuyDays;
    this.isCanBuy = isCanBuy && isCanBuy();
  }

  public final String getTitleString() {
    return String.format(this.container, this.value);
  }

  private final boolean isCanBuy() {
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