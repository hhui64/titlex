package io.github.hhui64.titlex.TConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.hhui64.titlex.TitleX;

public class ConfigManager {
  public String path;
  public YamlConfiguration yamlConfiguration;
  public FileConfiguration config;
  public FileConfiguration save;
  public FileConfiguration titles;

  public void load() throws FileNotFoundException {
    TitleX.instance.saveDefaultConfig();
    loadConfig();
  }

  public void reload() {
    // 重载 config.yml
    TitleX.instance.reloadConfig();
    // 重载 save.yml message.yml 等附加配置
    loadConfig();
    // 重新实例化对象
    TitleX.instance.loadClass();
  }

  public void loadConfig() {
    config = getFileConfiguration();
    save = getYamlConfiguration(TitleX.instance.getDataFolder(), "save.yml");
    titles = getYamlConfiguration(TitleX.instance.getDataFolder(), "titles.yml");
  }

  /**
   * 实时保存 save.yml 数据
   */
  @Deprecated
  public void saveConfig() {
    try {
      save.save(new File(TitleX.instance.getDataFolder(), "save.yml"));
    } catch (IOException e) {
      TitleX.instance.getLogger().info("Save failed!");
      e.printStackTrace();
    }
  }

  /**
   * 保存所有玩家的数据
   */
  public void saveAllPlayerData() {
    TitleX.instance.getLogger().info("Saving data for all players...");
    try {
      // config.save(new File(TitleX.instance.getDataFolder(), "titles.yml"));
      titles.save(new File(TitleX.instance.getDataFolder(), "titles.yml"));
      save.save(new File(TitleX.instance.getDataFolder(), "save.yml"));
      TitleX.instance.getLogger().info("Save succeeded.");
    } catch (IOException e) {
      TitleX.instance.getLogger().info("Save failed!");
      e.printStackTrace();
    }
  }

  private FileConfiguration getFileConfiguration() {
    TitleX.instance.getLogger().info("Loading config.yml ...");
    FileConfiguration l = TitleX.instance.getConfig();
    TitleX.instance.getLogger().info("Loading config.yml succeeded.");
    return l;
  }

  public YamlConfiguration getYamlConfiguration(File parent, String child) {
    TitleX.instance.getLogger().info("Loading extend configs(" + child + ")...");
    File file = new File(parent, child);
    if (!file.exists()) {
      TitleX.instance.getLogger().info("Creating extend " + child + "...");
      InputStream inputStream = TitleX.instance.getResource(child);
      if (inputStream != null) {
        try {
          OutputStream os = new FileOutputStream(file);
          int bytesRead = 0;
          byte[] buffer = new byte[inputStream.available() + 512];
          while ((bytesRead = inputStream.read(buffer, 0, inputStream.available() + 512)) != -1) {
            os.write(buffer, 0, bytesRead);
          }
          os.close();
          inputStream.close();
          file = new File(TitleX.instance.getDataFolder(), child);
          TitleX.instance.getLogger().info("Creating extend " + child + " succeeded.");
        } catch (IOException e) {
          TitleX.instance.getLogger().info("Creating extend " + child + " failed!");
          e.printStackTrace();
        }
      }
    }
    TitleX.instance.getLogger().info("Loading extend configs(" + child + ") succeeded.");
    return YamlConfiguration.loadConfiguration(file);
  }

  /**
   * 系统中是否有指定称号ID
   * 
   * @param titleId
   * @return
   */
  public boolean hasTitle(String titleId) {
    List<String> ts = new ArrayList<>(titles.getKeys(false));
    return ts.contains(titleId);
  }

  /**
   * 玩家是否拥有指定称号ID
   * 
   * @param player
   * @param titleId
   * @return
   */
  public boolean playerHasTitle(Player player, String titleId) {
    List<String> ts = new ArrayList<>(getPlayerTitles(player));
    return ts.contains(titleId);
  }

  /**
   * 获取所有称号ID字符串合集
   * 
   * @return
   */
  public Set<String> getTitles() {
    if (titles != null) {
      return titles.getKeys(false);
    }
    return null;
  }

  /**
   * 获取称号ID的 ConfigurationSection
   * 
   * @return
   */
  public ConfigurationSection getTitle(String titleId) {
    return titles.getConfigurationSection(titleId);
  }

  public ConfigurationSection getPlayerConfigSection(Player player) {
    return save.getConfigurationSection(player.getUniqueId().toString());
  }

  /**
   * 获取玩家拥有的称号ID字符串合集
   * 
   * @param player
   * @return
   */
  public Set<String> getPlayerTitles(Player player) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection c = save.getConfigurationSection(uuid + ".titles");
    if (c != null) {
      return c.getKeys(false);
    }
    return new HashSet<String>();
  }

  /**
   * 获取玩家所拥有的称号，并拼接为聊天前缀字符串
   * 
   * @param player
   * @return
   */
  public String getPlayerActiveTitles(Player player) {
    List<String> titles = new ArrayList<String>(0);
    // 获取玩家有的称号牌
    for (String titleId : getPlayerTitles(player)) {
      // 判断时效是否有效（过期）
      if (getPlayerTitleTimeStatus(player, titleId)) {
        // 判断是否佩戴
        if (getPlayerTitleActiveStatus(player, titleId)) {
          ConfigurationSection titleC = getTitle(titleId);
          String container = titleC.getString("container");
          String title = titleC.getString("title");
          String name = String.format(container, title);
          titles.add(name);
        }
      }
    }
    String extendString = "";
    if (!titles.isEmpty()) {
      // 获取玩家主用户组的前缀，添加在称号牌子后面
      String groupPrefix = TitleX.instance.vaultApi.chat.getGroupPrefix(player.getWorld(),
          TitleX.instance.vaultApi.permission.getPrimaryGroup(player));
      extendString = "§r" + TitleX.instance.getConfig().getConfigurationSection("title").getString("extend-prefix")
          + groupPrefix;
    }
    // 将所有称号连接起来
    return String.join("§r" + TitleX.instance.getConfig().getConfigurationSection("title").getString("join"), titles)
        + extendString;
  }

  /**
   * 获取指定玩家的指定称号ID的时效状态（是否有效）
   * 
   * @param player
   * @param titleId
   * @return 有效返回 true，过期返回 false
   */
  public boolean getPlayerTitleTimeStatus(Player player, String titleId) {
    ConfigurationSection c = getPlayerTitleConfigurationSection(player, titleId);
    int nowTime = (int) (System.currentTimeMillis() / 1000);
    int expTime = c.getInt("exp");
    return (expTime < 0) || (expTime > nowTime);
  }

  /**
   * 获取指定玩家的指定称号ID的 use 状态（是否佩戴）
   * 
   * @param player
   * @param titleId
   * @return
   */
  public boolean getPlayerTitleActiveStatus(Player player, String titleId) {
    ConfigurationSection c = getPlayerTitleConfigurationSection(player, titleId);
    return c.getBoolean("use");
  }

  /**
   * 获取指定玩家的指定称号ID的 ConfigurationSection
   * 
   * @param player
   * @param titleId
   * @return
   */
  public ConfigurationSection getPlayerTitleConfigurationSection(Player player, String titleId) {
    String uuid = player.getUniqueId().toString();
    return TitleX.instance.configManager.save.getConfigurationSection(uuid + ".titles." + titleId);
  }

  /**
   * 获取指定玩家的所有称号ID的 ConfigurationSection
   * 
   * @param player
   * @return
   */
  public List<ConfigurationSection> getPlayerTitlesConfigurationSection(Player player) {
    List<ConfigurationSection> titlesC = new ArrayList<ConfigurationSection>(0);
    for (String title : getPlayerTitles(player)) {
      titlesC.add(getPlayerTitleConfigurationSection(player, title));
    }
    return titlesC;
  }

  /**
   * 改变玩家指定称号ID的配置项
   * 
   * @param player
   * @param titleId
   * @param key
   * @param value
   */
  public void changePlayerTitleConfig(Player player, String titleId, String key, Object value) {
    save.set(player.getUniqueId().toString() + ".titles" + "." + titleId + "." + key, value);
  }

  /**
   * 向玩家添加指定称号ID（如玩家已拥有则覆盖该称号ID节点下的配置）
   * 
   * @param player
   * @param titleId
   * @param time    时间(单位: 秒)，如果 <= -1 则为永久
   */
  public void addPlayerTitle(Player player, String titleId, int time) {
    String uuid = player.getUniqueId().toString();
    ConfigurationSection configurationSection = save.createSection(uuid + ".titles." + titleId);
    int nowTime = (int) (System.currentTimeMillis() / 1000);
    int expTime = time <= -1 ? -1 : (nowTime + time);
    configurationSection.set("use", false);
    configurationSection.set("iat", nowTime);
    configurationSection.set("exp", expTime);
    save.set(uuid + ".titles." + titleId, configurationSection);
  }

  /**
   * 将称号ID从玩家中删除
   * 
   * @param player
   * @param titleId
   */
  public void delPlayerTitle(Player player, String titleId) {
    String uuid = player.getUniqueId().toString();
    save.set(uuid + ".titles." + titleId, null);
    if (getPlayerTitles(player).isEmpty())
      save.set(uuid, null);
  }

  /**
   * 清空玩家所有的称号ID（也会直接删除顶层UUID）
   * 
   * @param player
   */
  public void clearPlayerTitle(Player player) {
    String uuid = player.getUniqueId().toString();
    save.set(uuid, null);
  }

  /**
   * 获取玩家可用的称号，并通过 vault-api 设置玩家聊天前缀
   * 
   * @param player
   */
  public void setPlayerActiveTitlesToChatPrefix(Player player) {
    try {
      TitleX.instance.vaultApi.chat.setPlayerPrefix(player, getPlayerActiveTitles(player));
    } catch (Exception e) {
    }
  }
}