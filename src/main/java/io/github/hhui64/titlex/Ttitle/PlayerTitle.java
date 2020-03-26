package io.github.hhui64.titlex.Ttitle;

public class PlayerTitle {
  public LocalTitle localTitle;
  public boolean isForceUse = false;
  public boolean isUse = false;
  public int iat = -1;
  public int exp = -1;

  public PlayerTitle(String id, boolean isForceUse, boolean isUse, int iat, int exp) {
    this.localTitle = LocalTitleManager.getLocalTitle(id);
    this.isForceUse = isForceUse;
    this.isUse = isUse;
    this.iat = iat;
    this.exp = exp;
  }

  /**
   * 获取剩余天数
   * @return 整数型天数，不足一天的按一天计算。
   */
  public int getEffectiveDays() {
    int now = (int) Math.ceil(System.currentTimeMillis() / 1000);
    int at = (int) Math.ceil((double) ((double) (exp - now) / (double) 60 / (double) 60 / (double) 24));
    return at;
  }

  /**
   * 是否过期
   * @return 过期返回 true，有效返回 false。
   */
  public boolean isExpired() {
    if (exp < 0)
      return false;
    int now = (int) Math.ceil(System.currentTimeMillis() / 1000);
    return now >= exp;
  }
}