package io.github.hhui64.titlex.THook;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.hhui64.titlex.TitleX;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultApi {
  public Permission permission = null;
  public Economy economy = null;
  public Chat chat = null;

  public boolean init() {
    boolean hasNull = false;
    // 获取权限系统实例
    RegisteredServiceProvider<Permission> permissionProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.permission.Permission.class);
    if (permissionProvider != null) {
      if ((permission = permissionProvider.getProvider()) == null)
        hasNull = true;
    }
    // 初始化聊天系统实例
    RegisteredServiceProvider<Chat> chatProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.chat.Chat.class);
    if (chatProvider != null) {
      if ((chat = chatProvider.getProvider()) == null)
        hasNull = true;
    }
    // 初始化经济系统实例
    RegisteredServiceProvider<Economy> economyProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null) {
      if ((economy = economyProvider.getProvider()) == null)
        hasNull = true;
    }
    return !hasNull;
  }
}