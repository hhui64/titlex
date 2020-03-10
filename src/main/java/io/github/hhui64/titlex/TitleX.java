package io.github.hhui64.titlex;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.hhui64.titlex.TCommand.TCommandExecutor;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.TConfig.I18nConfig;
import io.github.hhui64.titlex.THook.VaultApi;
import io.github.hhui64.titlex.TItem.Chest;
import io.github.hhui64.titlex.TItem.NameTag;
import io.github.hhui64.titlex.TListeners.ChestListener;
import io.github.hhui64.titlex.TListeners.PlayerListener;

public class TitleX extends JavaPlugin {
  public static TitleX instance;

  public PluginManager pluginManager = getServer().getPluginManager();
  public ServicesManager servicesManager = getServer().getServicesManager();

  public VaultApi vaultApi;

  public TCommandExecutor tCommandExecutor;
  public ConfigManager configManager;
  public I18nConfig i18nConfig;
  public Chest chest;
  public NameTag nameTag;

  public TitleX() {
    instance = this;
  }

  @Override
  public void onLoad() {
    getDataFolder().mkdirs();
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
  public void onDisable() {
    configManager.saveAllPlayerData();
  }

  public void loadClass() {
    // FileConfiguration config = getConfig();
    i18nConfig = new I18nConfig();
    chest = new Chest(getConfig().getConfigurationSection("chest").getString("title"),
        getConfig().getConfigurationSection("chest").getInt("slot"));
    nameTag = new NameTag();
  }
}