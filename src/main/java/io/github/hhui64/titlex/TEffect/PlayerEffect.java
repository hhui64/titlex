package io.github.hhui64.titlex.TEffect;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.hhui64.titlex.TitleX;

public class PlayerEffect extends BukkitRunnable {
  private Player player;
  private double degree = 0;

  public PlayerEffect(Player player) {
    this.player = player;
  }

  @Override
  public void run() {
    // 用于检查玩家是否不在线的情况
    if (player == null || !player.isOnline()) {
      cancel();
    }
    Location playerLocation = player.getLocation();
    // 转弧度制
    double radians = Math.toRadians(degree);
    // 这里我写得简单了一点，我们将玩家的坐标克隆之后直接进行x, y, z的变换
    // 不难看出，我们这里是想建立一个 0.3 为半径的圆，作为我们想要实现的皇冠
    Location playEffectLocation = playerLocation.clone().add(0.3 * Math.cos(radians), 2D, 0.3 * Math.sin(radians));
    // 粒子播放，这里我使用了类库
    // ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.ORANGE), playEffectLocation, 50);
    // 我们只需要degree在0~360度内即可
    if (degree >= 360) {
      degree = 0;
    } else {
      // 这里其实就是修改了步长为20 degree
      degree += 20;
    }
  }

  /**
   * 开启特效的方法
   */
  public void startEffect() {
    runTaskTimer(TitleX.instance, 0L, 1L);
  }

  /**
   * 关闭特效的方法
   */
  public void stopEffect() {
    cancel();
  }
}