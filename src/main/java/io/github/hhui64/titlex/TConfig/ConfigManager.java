package io.github.hhui64.titlex.TConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.hhui64.titlex.TitleX;

public class ConfigManager {
  public static String path;
  public static YamlConfiguration yamlConfiguration;
  public static FileConfiguration config;
  public static FileConfiguration save;
  public static FileConfiguration titles;
  public static FileConfiguration messages;

  /**
   * 初始化加载
   */
  public static void init() throws FileNotFoundException {
    TitleX.instance.saveDefaultConfig();
    loadConfig();
  }

  /**
   * 重载配置文件
   */
  public static void reloadConfig() {
    // 重载 config.yml
    TitleX.instance.reloadConfig();
    // 重载 save.yml message.yml 等附加配置
    loadConfig();
    // 重新实例化对象
    TitleX.instance.init();
  }

  /**
   * 载入配置文件
   */
  public static void loadConfig() {
    config = getFileConfiguration();
    save = getYamlConfiguration(TitleX.instance.getDataFolder(), "save.yml");
    titles = getYamlConfiguration(TitleX.instance.getDataFolder(), "titles.yml");
    messages = getYamlConfiguration(TitleX.instance.getDataFolder(), "messages.yml");
  }

  /**
   * 实时保存 save.yml 数据
   */
  @Deprecated
  public static void savePlayerData() {
    try {
      save.save(new File(TitleX.instance.getDataFolder(), "save.yml"));
    } catch (IOException e) {
      TitleX.instance.getLogger().info("failed to save player data!");
      e.printStackTrace();
    }
  }

  /**
   * 保存插件所有数据
   */
  public static void saveAllData() {
    TitleX.instance.getLogger().info("Saving data for all players...");
    try {
      titles.save(new File(TitleX.instance.getDataFolder(), "titles.yml"));
      save.save(new File(TitleX.instance.getDataFolder(), "save.yml"));
      TitleX.instance.getLogger().info("Save succeeded.");
    } catch (IOException e) {
      TitleX.instance.getLogger().info("Save failed!");
      e.printStackTrace();
    }
  }

  /**
   * 获取 config.yml 文件 FileConfiguration
   * 
   * @return
   */
  private static FileConfiguration getFileConfiguration() {
    TitleX.instance.getLogger().info("Loading config.yml ...");
    FileConfiguration fc = TitleX.instance.getConfig();
    TitleX.instance.getLogger().info("Loading config.yml succeeded.");
    return fc;
  }

  /**
   * 获取自定义 yml 文件 FileConfiguration
   * 
   * @param parent 路径
   * @param child 文件名
   * @return
   */
  public static YamlConfiguration getYamlConfiguration(File parent, String child) {
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
   * 获取语言文件的指定 key value
   * 
   * @param key
   * @param format
   * @return
   */
  public static String getMessage(String key, Object... format) {
    return ChatColor.translateAlternateColorCodes('&' ,format.length > 0 ? String.format(messages.getString(key), format) : messages.getString(key));
  }

  /**
   * 获取语言文件 list
   * 
   * @param key
   * @return
   */
  public static String[] getMessageList(String key) {
    return messages.getList(key).toArray(new String[0]);
  }
}