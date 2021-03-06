package io.github.hhui64.titlex;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

import io.github.hhui64.titlex.TCommand.TCommandExecutor;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.THook.VaultApi;
import io.github.hhui64.titlex.TItem.ListChest;
import io.github.hhui64.titlex.TItem.ShopChest;
import io.github.hhui64.titlex.TListeners.ChestListener;
import io.github.hhui64.titlex.TListeners.PlayerListener;
import io.github.hhui64.titlex.TMessage.Message;

public class TitleX extends JavaPlugin {
  public static TitleX instance;
  public PluginManager pluginManager = getServer().getPluginManager();
  public ServicesManager servicesManager = getServer().getServicesManager();
  public TCommandExecutor tCommandExecutor;
  public ListChest listChest;
  public ShopChest shopChest;

  public TitleX() {
    instance = this;
  }

  @Override
  public void onLoad() {
    getDataFolder().mkdirs();
    try {
      // 载入配置
      ConfigManager.init();
      // 实例化对象
      init();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onEnable() {
    // Hook vault-api
    getLogger().info("Hooking Vault...");
    if (VaultApi.init()) {
      getLogger().info("Vault hooked.");
    } else {
      getLogger().info("Hook Vault failed! Are you sure it is installed?");
      pluginManager.disablePlugin(pluginManager.getPlugin("TitleX"));
    }
    // 注册命令处理器
    tCommandExecutor = new TCommandExecutor();
    getCommand(tCommandExecutor.COMMAND_NAME).setExecutor(tCommandExecutor);
    // 注册监听器
    pluginManager.registerEvents(new ChestListener(), this);
    pluginManager.registerEvents(new PlayerListener(), this);
  }

  @Override
  public void onDisable() {
    // ConfigManager.savePlayerData();
  }

  /**
   * 初始化实例化对象
   */
  public void init() {
    listChest = new ListChest(Message.getMessage("list-chest"),
        getConfig().getConfigurationSection("list-chest").getInt("slot"));
    shopChest = new ShopChest(Message.getMessage("shop-chest"),
        getConfig().getConfigurationSection("shop-chest").getInt("slot"));
  }
}