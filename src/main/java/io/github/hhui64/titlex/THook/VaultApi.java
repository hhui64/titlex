package io.github.hhui64.titlex.THook;

import org.bukkit.plugin.RegisteredServiceProvider;

import io.github.hhui64.titlex.TitleX;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultApi {
  public static Permission permission = null;
  public static Economy economy = null;
  public static Chat chat = null;

  public static boolean init() {
    RegisteredServiceProvider<Permission> permissionProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.permission.Permission.class);
    if (permissionProvider != null)
      permission = (Permission) permissionProvider.getProvider();
    RegisteredServiceProvider<Chat> chatProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.chat.Chat.class);
    if (chatProvider != null)
      chat = (Chat) chatProvider.getProvider();
    RegisteredServiceProvider<Economy> economyProvider = TitleX.instance.servicesManager
        .getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (economyProvider != null)
      economy = (Economy) economyProvider.getProvider();
    return permission != null && economy != null && chat != null;
  }
}