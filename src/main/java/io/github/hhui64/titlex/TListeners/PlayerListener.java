package io.github.hhui64.titlex.TListeners;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.hhui64.titlex.TitleX;

public class PlayerListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (getFlag("on-join")) {
      Player player = event.getPlayer();
      check(player);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (getFlag("on-quit")) {
      Player player = event.getPlayer();
      check(player);
    }
  }

  @EventHandler
  public void onAsyncChat(AsyncPlayerChatEvent event) {
    if (getFlag("on-chat")) {
      Player player = event.getPlayer();
      check(player);
    }
  }

  public void check(Player player) {
    if (player instanceof Player) {
      Set<String> titles = TitleX.instance.configManager.getPlayerTitles(player);
      for (String titleId: titles) {
        // 根据 titleId 获取该称号ID在玩家库存中的状态信息
        boolean status = TitleX.instance.configManager.getPlayerTitleTimeStatus(player, titleId);
        // 过期了从玩家库存中删除称号ID节点
        if (!status) {
          // 删除并保存
          TitleX.instance.configManager.delPlayerTitle(player, titleId);
          TitleX.instance.configManager.saveConfig();
        }
      }
    }
    // 刷新可用称号生成聊天前缀并设置
    TitleX.instance.configManager.setPlayerActiveTitlesToChatPrefix(player);
  }

  public boolean getFlag(String name) {
    return TitleX.instance.getConfig().getConfigurationSection("check").getBoolean(name);
  }
}