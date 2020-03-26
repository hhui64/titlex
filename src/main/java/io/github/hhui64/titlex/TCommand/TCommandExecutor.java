package io.github.hhui64.titlex.TCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TConfig.ConfigManager;
import io.github.hhui64.titlex.THook.VaultApi;
import io.github.hhui64.titlex.Ttitle.LocalTitleManager;
import io.github.hhui64.titlex.Ttitle.PlayerTitleManager;

public class TCommandExecutor implements TabExecutor {
  public final String COMMAND_NAME = "ttx";
  public String[] subCommands = { "list", "shop", "give", "remove", "clear", "refresh", "reload" }; // 子命令

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 0) {
      if (sender instanceof Player) {
        // 仅游戏内可以使用的指令
        Player player = (Player) sender;
        switch (args[0].toLowerCase()) {
          case "list":
            if (!checkPermission(player, "titlex.use.list"))
              return true;
            list(player);
            return true;
          case "shop":
            if (!checkPermission(player, "titlex.use.shop"))
              return true;
            shop(player);
            return true;
          case "help":
            if (!checkPermission(player, "titlex.use.help"))
              return true;
            sendHelpMessage(sender);
            return true;
        }
      } else {
        // 仅控制台可以使用的指令
      }
      // 控制台和游戏内都可以使用的指令
      switch (args[0].toLowerCase()) {
        case "give":
          if (!checkPermission(sender, "titlex.admin.give"))
            return true;
          if (args.length >= 3 && args.length <= 5) {
            String t = args.length == 3 ? "-1" : args[3];
            boolean isForceUse = args.length >= 5 && (args[4].equalsIgnoreCase(
                "true") || args[4].equalsIgnoreCase("t")) ? true : false; 
            give(sender, args[1], args[2], t, isForceUse);
          } else {
            sender.sendMessage("§e用法: /ttx give <玩家名> <称号ID> [天数] [是否强制佩戴]");
          }
          break;
        case "remove":
          if (!checkPermission(sender, "titlex.admin.remove"))
            return true;
          if (args.length == 3) {
            remove(sender, args[1], args[2]);
          } else {
            sender.sendMessage("§e用法: /ttx remove <玩家名> <称号ID>");
          }
          break;
        case "clear":
          if (!checkPermission(sender, "titlex.admin.clear"))
            return true;
          if (args.length == 2) {
            clear(sender, args[1]);
          } else {
            sender.sendMessage("§e用法: /ttx clear <玩家名>");
          }
          break;
        case "refresh":
          if (!checkPermission(sender, "titlex.admin.refresh"))
            return true;
          if (args.length == 1) {
            refresh(sender);
          } else {
            sender.sendMessage("§e用法: /ttx refresh");
          }
          break;
        case "reload":
          if (!checkPermission(sender, "titlex.admin.reload"))
            return true;
          if (args.length == 1) {
            reload(sender);
          } else {
            sender.sendMessage("§e用法: /ttx reload");
          }
          break;
        default:
          return false;
      }
      return true;
    }
    sendHelpMessage(sender);
    return true;
  }

  private void sendHelpMessage(CommandSender sender) {
    String[] help = ConfigManager.getMessageList("help");
    String[] opHelp = ConfigManager.getMessageList("op-help");
    List<String> finalMsg = new ArrayList<String>();
    finalMsg.addAll(Arrays.asList(help));
    boolean r = (sender instanceof Player) ? VaultApi.permission.playerHas((Player) sender, "titlex.admin") : true;
    if (r)
      finalMsg.addAll(Arrays.asList(opHelp));
    sender.sendMessage(finalMsg.toArray(new String[0]));
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    // 如果此时仅输入了命令 "ttx"，则直接返回所有的子命令
    if (args.length == 0)
      return Arrays.asList(subCommands);
    // 根据已输入的字符，筛选与指令开头部分匹配的补全列表然后返回
    if (args.length == 1)
      return Arrays.stream(subCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    // give, remove, clear 子命令补全玩家名称，各自所需的参数名称等
    if (args.length >= 2 && args.length <= 4) {

      // give 指令补全
      if (args[0].equals("give")) {
        // 第三个参数补全 titles list
        if (args.length == 3) {
          List<String> titles = new ArrayList<>(LocalTitleManager.getAllLocalTitlesIdSet());
          return titles.stream().filter(title -> title.startsWith(args[2])).collect(Collectors.toList());
        }
        // 第四参数补全数字
        if (args.length == 4) {
          String[] num = { "1", "3", "7", "15", "30", "-1" };
          return Arrays.asList(num);
        }
        // 第五参数补全 Boolean 值
        if (args.length == 5) {
          String[] booleanString = { "true", "false", "t", "f" };
          return Arrays.asList(booleanString);
        }
      }

      // remove 指令补全
      if (args[0].equals("remove")) {
        // 第三参数 根据第二参数 player 补全 title list
        if (args.length == 3) {
          if (args[1] == null)
            return null;
          Player player = Bukkit.getPlayerExact(args[1]);
          if (player != null && player.isOnline()) {
            List<String> titles = new ArrayList<>(PlayerTitleManager.getPlayerAllTitlesIdSet(player));
            return titles.stream().filter(title -> title.startsWith(args[2])).collect(Collectors.toList());
          }
        }
      }

    }
    return null;
  }

  /**
   * 检查玩家是否存在或是否拥有权限
   * @param sender
   * @param p
   * @return
   */
  private boolean checkPermission(CommandSender sender, String p) {
    boolean r = (sender instanceof Player) ? VaultApi.permission.playerHas((Player) sender, p) : true;
    if ((sender instanceof Player)) {
      if (!(((Player) sender).isOnline())) {
        sender.sendMessage(ConfigManager.getMessage("no-player"));
        return false;
      }
    }
    if (!r) {
      sender.sendMessage(ConfigManager.getMessage("no-permission"));
    }
    return r;
  }

  private void list(Player player) {
    TitleX.instance.listChest.open(player);
  }

  private void shop(Player player) {
    TitleX.instance.shopChest.open(player);
  }

  private void give(CommandSender sender, String playerName, String id, String days, boolean isForceUse) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      if (LocalTitleManager.getLocalTitle(id) != null) {
        try {
          int intDays = Integer.parseInt(days);
          PlayerTitleManager.addPlayerCurrentTitle(player, id, intDays, isForceUse, false);
          ConfigManager.savePlayerData();
          sender.sendMessage(ConfigManager.getMessage("give-success", id, playerName, intDays, isForceUse));
          player.sendMessage(ConfigManager.getMessage("get-title"));
        } catch (NumberFormatException e) {
          sender.sendMessage(ConfigManager.getMessage("invalid-date"));
        }
      } else {
        sender.sendMessage(ConfigManager.getMessage("no-title"));
      }
    } else {
      sender.sendMessage(ConfigManager.getMessage("no-player"));
    }
  }

  private void remove(CommandSender sender, String playerName, String id) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      if (PlayerTitleManager.getPlayerCurrentTitle(player, id) != null) {
        // 从配置文件中删除，并保存
        PlayerTitleManager.delPlayerCurrentTitle(player, id);
        ConfigManager.savePlayerData();
        // 刷新可用称号生成聊天前缀并设置
        PlayerTitleManager.updatePlayerPrefix(player);
        sender.sendMessage(ConfigManager.getMessage("remove-success", id, playerName));
      } else {
        sender.sendMessage(ConfigManager.getMessage("player-no-title"));
      }
    } else {
      sender.sendMessage(ConfigManager.getMessage("no-player"));
    }
  }

  private void clear(CommandSender sender, String playerName) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      PlayerTitleManager.clearPlayerAllTitles(player);
      ConfigManager.savePlayerData();
      // 清空前缀内容
      VaultApi.chat.setPlayerPrefix(player, null);
      sender.sendMessage(ConfigManager.getMessage("clear-success", playerName));
    } else {
      sender.sendMessage(ConfigManager.getMessage("no-player"));
    }
  }

  private void refresh(CommandSender sender) {
    sender.sendMessage(ConfigManager.getMessage("refreshing"));
    sender.sendMessage(ConfigManager.getMessage("refreshing-success"));
  }

  private void reload(CommandSender sender) {
    sender.sendMessage(ConfigManager.getMessage("reloading"));
    ConfigManager.reloadConfig();
    sender.sendMessage(ConfigManager.getMessage("reloading-success"));
  }
}