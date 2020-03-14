package io.github.hhui64.titlex;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

import io.github.hhui64.titlex.TCommand.TCommandExecutor;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.THook.VaultApi;
import io.github.hhui64.titlex.TItem.ListChest;
import io.github.hhui64.titlex.TItem.NameTag;
import io.github.hhui64.titlex.TItem.ShopChest;
import io.github.hhui64.titlex.TListeners.ChestListener;
import io.github.hhui64.titlex.TListeners.PlayerListener;

public class TitleX extends JavaPlugin {
  public static TitleX instance;

  public PluginManager pluginManager = getServer().getPluginManager();
  public ServicesManager servicesManager = getServer().getServicesManager();

  public VaultApi vaultApi;

  public TCommandExecutor tCommandExecutor;
  public ConfigManager configManager;
  public ListChest listChest;
  public ShopChest shopChest;
  public NameTag nameTag;

  public TitleX() {
    instance = this;
  }

  @Override
  public void onLoad() {
    getDataFolder().mkdirs();
    // 实例化对象
    configManager = new ConfigManager();
    // 载入配置
    try {
      configManager.load();
      loadClass();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onEnable() {
    // Hook vault-api
    getLogger().info("Hooking vault-api...");
    vaultApi = new VaultApi();
    if (vaultApi.init()) {
      getLogger().info("Succeeded to hook vault-api.");
    } else {
      getLogger().info("Failed to hook vault-api! is installed?");
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
    configManager.saveAllPlayerData();
  }

  public void loadClass() {
    listChest = new ListChest(configManager.getMessage("list-chest"),
        getConfig().getConfigurationSection("list-chest").getInt("slot"));
    shopChest = new ShopChest(configManager.getMessage("shop-chest"),
        getConfig().getConfigurationSection("shop-chest").getInt("slot"));
    nameTag = new NameTag();
  }
}