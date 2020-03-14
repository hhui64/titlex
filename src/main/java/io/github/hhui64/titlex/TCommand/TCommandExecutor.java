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
          if (args.length == 3 || args.length == 4) {
            String t;
            if (args.length == 3) {
              t = "-1";
            } else {
              t = args[3];
            }
            give(sender, args[1], args[2], t);
          } else {
            sender.sendMessage("§e用法: /ttx give <玩家名> <称号ID> [天数]");
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
    sender.sendMessage(TitleX.instance.configManager.getMessageList("help"));
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
      // give 第三个参数补全 titles list
      if (args.length == 3 && args[0].equals("give")) {
        List<String> titles = new ArrayList<>(TitleX.instance.configManager.getTitles());
        return titles.stream().filter(title -> title.startsWith(args[2])).collect(Collectors.toList());
      }
      // give 第四参数补全数字
      if (args.length == 4 && args[0].equals("give")) {
        String[] num = { "1", "3", "7", "15", "30", "-1" };
        return Arrays.asList(num);
      }
      // remove 第三参数 根据第二参数 player 补全 title list
      if (args.length == 3 && args[0].equals("remove")) {
        if (args[1] == null)
          return null;
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player != null && player.isOnline()) {
          // String uuid = player.getUniqueId().toString();
          List<String> titles = new ArrayList<>(TitleX.instance.configManager.getPlayerTitles(player));
          return titles.stream().filter(title -> title.startsWith(args[2])).collect(Collectors.toList());
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
    boolean r = (sender instanceof Player) ? TitleX.instance.vaultApi.permission.playerHas((Player) sender, p) : true;
    if ((sender instanceof Player)) {
      if (!(((Player) sender).isOnline())) {
        sender.sendMessage(TitleX.instance.configManager.getMessage("no-player"));
        return false;
      }
    }
    if (!r) {
      sender.sendMessage(TitleX.instance.configManager.getMessage("no-permission"));
    }
    return r;
  }

  private void list(Player player) {
    TitleX.instance.listChest.open(player);
  }

  private void shop(Player player) {
    TitleX.instance.shopChest.open(player);
  }

  private void give(CommandSender sender, String playerName, String titleId, String time) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      if (TitleX.instance.configManager.hasTitle(titleId)) {
        try {
          int t = Integer.parseInt(time);
          t = t < 0 ? -1 : t * 24 * 60 * 60;
          TitleX.instance.configManager.addPlayerTitle(player, titleId, t);
          TitleX.instance.configManager.saveConfig();
          sender.sendMessage(TitleX.instance.configManager.getMessage("give-success", titleId, playerName, t));
          player.sendMessage(TitleX.instance.configManager.getMessage("get-title"));
        } catch (NumberFormatException e) {
          sender.sendMessage(TitleX.instance.configManager.getMessage("invalid-date"));
        }
      } else {
        sender.sendMessage(TitleX.instance.configManager.getMessage("no-title"));
      }
    } else {
      sender.sendMessage(TitleX.instance.configManager.getMessage("no-player"));
    }
  }

  private void remove(CommandSender sender, String playerName, String titleId) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      if (TitleX.instance.configManager.playerHasTitle(player, titleId)) {
        // 从配置文件中删除
        TitleX.instance.configManager.delPlayerTitle(player, titleId);
        TitleX.instance.configManager.saveConfig();
        // 刷新可用称号生成聊天前缀并设置
        TitleX.instance.configManager.setPlayerActiveTitlesToChatPrefix(player);
        sender.sendMessage(TitleX.instance.configManager.getMessage("remove-success", titleId, playerName));
      } else {
        sender.sendMessage(TitleX.instance.configManager.getMessage("player-no-title"));
      }
    } else {
      sender.sendMessage(TitleX.instance.configManager.getMessage("no-player"));
    }
  }

  private void clear(CommandSender sender, String playerName) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      TitleX.instance.configManager.clearPlayerTitle(player);
      TitleX.instance.configManager.saveConfig();
      // 清空前缀内容
      TitleX.instance.vaultApi.chat.setPlayerPrefix(player, null);
      sender.sendMessage(TitleX.instance.configManager.getMessage("clear-success", playerName));
    } else {
      sender.sendMessage(TitleX.instance.configManager.getMessage("no-player"));
    }
  }

  private void refresh(CommandSender sender) {
    sender.sendMessage(TitleX.instance.configManager.getMessage("refreshing"));
    sender.sendMessage(TitleX.instance.configManager.getMessage("refreshing-success"));
  }

  private void reload(CommandSender sender) {
    sender.sendMessage(TitleX.instance.configManager.getMessage("reloading"));
    TitleX.instance.configManager.reload();
    sender.sendMessage(TitleX.instance.configManager.getMessage("reloading-success"));
  }
}