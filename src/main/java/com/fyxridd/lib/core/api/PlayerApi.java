package com.fyxridd.lib.core.api;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.hashList.HashList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerApi {
    private static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    //有延时更新的玩家列表
    private static HashSet<String> updateInvs = new HashSet<>();

    /**
     * 延时(0tick)更新背包
     */
    public static void updateInventoryDelay(final Player p) {
        //已经有更新延时
        if (updateInvs.contains(p.getName())) return;
        //添加缓存
        updateInvs.add(p.getName());
        //延时更新
        Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.instance, new Runnable() {
            @Override
            public void run() {
                //删除缓存
                updateInvs.remove(p.getName());
                //检测更新
                if (p.isOnline() && !p.isDead()) p.updateInventory();
            }
        });
    }

    /**
     * @see #sendTitleAll(String, String, boolean, int)
     */
    public static void sendTitleAll(String title, String subTitle, boolean instant) {
        CoreMain.title.sendTitleAll(title, subTitle, instant);
    }

    /**
     * 给所有玩家发送标题
     * @param title 标题,可为null
     * @param subTitle 子标题,可为null
     * @param instant 是否立即显示
     * @param time 显示多长时间,单位tick
     */
    public static void sendTitleAll(String title, String subTitle, boolean instant, int time) {
        CoreMain.title.sendTitleAll(title, subTitle, instant, time);
    }

    /**
     * @see #sendTitle(Player, String, String, boolean, int)
     */
    public static void sendTitle(Player p, String title, String subTitle, boolean instant) {
        CoreMain.title.sendTitle(p, title, subTitle, instant);
    }

    /**
     * 给玩家发送标题
     * @param p 玩家
     * @param title 标题,可为null
     * @param subTitle 子标题,可为null
     * @param instant 是否立即显示
     * @param time 显示多长时间,单位tick
     */
    public static void sendTitle(Player p, String title, String subTitle, boolean instant, int time) {
        CoreMain.title.sendTitle(p, title, subTitle, instant, time);
    }

    /**
     * 获取攻击的玩家
     * @param damager 直接伤害者
     * @return 如果直接伤害者是玩家直接返回玩家;如果是发射物并且发射物是玩家发出的则返回发射者;其它情况均返回null
     */
    public static Player getPlayerDamager(Entity damager) {
        if (damager == null) return null;
        else if (damager instanceof Player) return (Player) damager;
        else if (damager instanceof Projectile) {
            ProjectileSource ps = ((Projectile) damager).getShooter();
            if (ps != null && ps instanceof Player) return (Player) ps;
            else return null;
        }else return null;
    }

    /**
     * 是否指定玩家的最后一击杀死了生物
     * @param entity 被杀死的生物
     * @param killer 杀死生物的玩家的名字
     */
    public static boolean isLastDamagedByPlayer(LivingEntity entity, String killer) {
        EntityDamageEvent entityDamageEvent = entity.getLastDamageCause();
        if (entityDamageEvent != null && entityDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entityDamageEvent;
            Player damager = PlayerApi.getPlayerDamager(entityDamageByEntityEvent.getDamager());
            return damager != null && damager.getName().equals(killer);
        }
        return false;
    }

    /**
     * 给目标玩家添加生命值并进行提示<br>
     * 不会超过玩家的生命上限
     * @param p 玩家,可为null(null时无效果)
     * @param add 增加的生命,<=0时无效果
     */
    public static void addHealth(Player p, double add) {
        if (p == null || add <= 0) return;

        double origin = p.getHealth();
        p.setHealth(Math.min(p.getMaxHealth(), p.getHealth()+add));
        ShowApi.tip(p, get(50, (p.getHealth() - origin)), false);
    }

    /**
     * 其它聊天插件不能自行给玩家发送聊天信息或调用ShowApi.tip方法,而要调用此方法,否则不会有延时显示聊天信息的功能
     * @param p 玩家,可为null(null时无效果)
     * @param msg 聊天信息,可为null(null时无效果)
     * @param force 是否强制显示
     */
    public static void addChat(Player p, FancyMessage msg, boolean force) {
        CoreMain.chatManager.addChat(p, msg, force);
    }

    /**
     * @see RealName#getRealName(CommandSender, String)
     */
    public static String getRealName(CommandSender sender, String name) {
        return RealName.getRealName(sender, name);
    }

    /**
     * 给玩家发送信息(不重要的)<br>
     * 玩家存在且在线时发送
     * @param name 准确的玩家名,不为null
     * @param msg 信息,不为null
     * @param force 是否强制
     */
    public static void sendMsg(String name, String msg, boolean force) {
        Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null && p.isOnline()) ShowApi.tip(p, msg, force);
    }

    /**
     * 给玩家发送信息(不重要的)<br>
     * 玩家存在且在线时发送
     * @param name 准确的玩家名,不为null
     * @param msg 信息,不为null
     * @param force 是否强制
     */
    public static void sendMsg(String name, FancyMessage msg, boolean force) {
        Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null && p.isOnline()) ShowApi.tip(p, msg, force);
    }

    /**
     * 检测玩家是否在线,不在线时会进行提示
     * @param p 玩家,可为null(null时不会进行提示)
     * @param tar (精确的)目标玩家名,可为null(null时不会进行提示并且返回null)
     * @return 在线的玩家,不在线返回null
     */
    public static Player checkOnline(Player p, String tar) {
        if (tar == null) return null;

        Player tarP = Bukkit.getPlayerExact(tar);
        if (tarP == null) {
            if (p != null) ShowApi.tip(p, FormatApi.get(CorePlugin.pn, 40, tar), true);
            return null;
        }
        return tarP;
    }

    /**
     * 显示特效<br>
     * 显示目标:<br>
     *      1. 目标玩家(p)<br>
     *      2. 附近玩家(entity+range)<br>
     *
     * @param p 目标玩家(null时目标玩家不会显示)
     *
     * @param entity 会获取entity附近range范围内的所有玩家进行显示(null时附近玩家不会显示)
     * @param range 显示范围,单位格(<=0时附近玩家不会显示)
     *
     * @param loc 显示位置
     * @param type 特效类型,enum值
     * @param count 数量
     * @param offset 最大偏移
     * @param longDistance 是否长距离
     */
    public static void showSpec(Player p, Entity entity, int range, Location loc, EnumWrappers.Particle type, int count, float offset, boolean longDistance) {
        WrapperPlayServerWorldParticles particles = new WrapperPlayServerWorldParticles();
        particles.setNumberOfParticles(count);
        particles.setParticleType(type);
        particles.setLongDistance(longDistance);
        particles.setX((float) loc.getX());
        particles.setY((float) loc.getY());
        particles.setZ((float) loc.getZ());
        particles.setOffsetX(offset);
        particles.setOffsetY(offset);
        particles.setOffsetZ(offset);
        PacketContainer packet = particles.getHandle();

        try {
            //目标玩家
            if (p != null) protocolManager.sendServerPacket(p, packet, true);
            //附近玩家
            if (entity != null && range > 0) {
                for (Entity e : entity.getNearbyEntities(range, range, range)) {
                    if (e instanceof Player) protocolManager.sendServerPacket((Player) e, packet, true);
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static FancyMessage get(int id, Object... args) {
        return FormatApi.get(CorePlugin.pn, id, args);
    }
}
