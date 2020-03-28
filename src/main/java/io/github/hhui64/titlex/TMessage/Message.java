package io.github.hhui64.titlex.TMessage;

import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import io.github.hhui64.titlex.TConfig.ConfigManager;

public class Message {
  /**
   * 获取语言文件的指定 key value
   * 
   * @param key
   * @param format
   * @return 值
   */
  public static String getMessage(String key, Object... format) {
    return ChatColor.translateAlternateColorCodes('&',
        format.length > 0 ? String.format(ConfigManager.messages.getString(key), format)
            : ConfigManager.messages.getString(key));
  }

  /**
   * 获取语言文件 list
   * 
   * @param key
   * @return
   */
  public static String[] getMessageList(String key) {
    return ConfigManager.messages.getStringList(key).stream()
        .map(item -> ChatColor.translateAlternateColorCodes('&', item)).collect(Collectors.toList())
        .toArray(new String[0]);
  }
}