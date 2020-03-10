package io.github.hhui64.titlex.TCommand;

import java.io.Console;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import io.github.hhui64.titlex.TitleX;
import io.github.hhui64.titlex.TItem.Chest;

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
            // ...
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
    String[] msg = { "§6================== 称号系统 BY HUANGHU1 ==================", "§e/ttx list  §7-  §f打开称号陈列柜",
        "§e/ttx shop  §7-  §f打开称号商城", "§e/ttx help  §7-  §f查看指令帮助内容",
        "§e/ttx give <玩家名> <称号ID> [天数]  §7-  §f授予玩家指定称号ID", "§e/ttx remove <玩家名> <称号ID>  §7-  §f移除玩家指定称号ID",
        "§e/ttx clear <玩家名>  §7-  §f清除玩家的所有称号", "§e/ttx refresh [玩家名]  §7-  §f刷新指定玩家或所有玩家的称号",
        "§e/ttx reload  §7-  §f重新加载插件", "§6===========================================================", };
    sender.sendMessage(msg);
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
      // 管理指令第二参统一为玩家名称补全
      // if (args.length == 2 && (args[0].equals("give") || args[0].equals("remove")
      // || args[0].equals("clear"))) {
      // // 判断是在控制台操作，返回服务器内游戏过的玩家
      // if (!(sender instanceof Player)) {
      // OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
      // return Arrays.asList(offlinePlayers).stream().map(player -> player.getName())
      // .filter(playerName ->
      // playerName.startsWith(args[1])).collect(Collectors.toList());
      // }
      // }
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

  private boolean checkPermission(CommandSender sender, String p) {
    boolean r = (sender instanceof Player) ? TitleX.instance.vaultApi.permission.playerHas((Player) sender, p) : true;
    if ((sender instanceof Player)) {
      if (!(((Player) sender).isOnline())) {
        sender.sendMessage("§c玩家不存在或已离线");
        return false;
      }
    }
    if (!r) {
      sender.sendMessage("§c你没有权限这样做");
    }
    return r;
  }

  private void list(Player player) {
    TitleX.instance.chest.openChest(player);
  }

  private void give(CommandSender sender, String playerName, String titleId, String time) {
    if (playerName == null)
      return;
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null && player.isOnline()) {
      if (TitleX.instance.configManager.hasTitle(titleId)) {
        try {
          int t = time == "-1" ? -1 : Integer.parseInt(time) * 24 * 60 * 60;
          TitleX.instance.configManager.addPlayerTitle(player, titleId, t);
          TitleX.instance.configManager.saveConfig();
          sender.sendMessage("§a成功将称号 " + titleId + " 添加至玩家 " + playerName + " 的库存中 (" + time + "天)");
          player.sendMessage("§a你被授予了一个称号，请打开称号陈列柜佩戴吧！");
        } catch (NumberFormatException e) {
          sender.sendMessage("§c请输入正确的天数");
        }
      } else {
        sender.sendMessage("§c称号库中没有这个称号ID");
      }
    } else {
      sender.sendMessage("§c玩家不存在或已离线");
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
        sender.sendMessage("§a成功将称号ID " + titleId + " 从玩家 " + playerName + " 的库存中移除");
      } else {
        sender.sendMessage("§c该玩家库存中没有这个称号ID");
      }
    } else {
      sender.sendMessage("§c玩家不存在或已离线");
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
      sender.sendMessage("§a成功清除玩家 " + playerName + " 的所有称号库存");
    } else {
      sender.sendMessage("§c玩家不存在或已离线");
    }
  }

  private void refresh(CommandSender sender) {
    sender.sendMessage("§e正在刷新所有玩家的称号...");
    sender.sendMessage("§a刷新成功");
  }

  private void reload(CommandSender sender) {
    sender.sendMessage("§e正在重新载入插件...");
    TitleX.instance.configManager.reload();
    sender.sendMessage("§a重新载入成功");
  }
}