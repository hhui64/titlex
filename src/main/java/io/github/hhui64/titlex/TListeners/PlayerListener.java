package io.github.hhui64.titlex.TListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.Ttitle.PlayerTitleManager;

public class PlayerListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (getFlagState("on-join")) {
      Player player = event.getPlayer();
      refresh(player);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (getFlagState("on-quit")) {
      Player player = event.getPlayer();
      refresh(player);
    }
  }

  @EventHandler
  public void onAsyncChat(AsyncPlayerChatEvent event) {
    if (getFlagState("on-chat")) {
      Player player = event.getPlayer();
      refresh(player);
    }
  }

  @EventHandler
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
    if (getFlagState("on-player-change-world")) {
      Player player = event.getPlayer();
      refresh(player);
    }
  }

  /**
   * 刷新、清除指定玩家过期失效称号，并通过 vault 重新设置玩家称号
   * 
   * @param player
   */
  public void refresh(Player player) {
    if (player instanceof Player) {
      PlayerTitleManager.clearPlayerAllExpiredTitles(player);
      ConfigManager.savePlayerData();
      PlayerTitleManager.updatePlayerPrefix(player);
    }
  }

  /**
   * 获取检测 flag 的启用状态
   * 
   * @param name
   * @return
   */
  public boolean getFlagState(String name) {
    return TitleX.instance.getConfig().getConfigurationSection("refresh-on").getBoolean(name);
  }
}