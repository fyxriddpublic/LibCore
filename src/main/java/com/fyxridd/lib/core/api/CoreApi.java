package com.fyxridd.lib.core.api;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.Tps;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.fancymessage.FancyMessageImpl;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreApi {
    //服务端所在的文件夹路径
    public static String serverPath;
    //插件文件夹路径
    public static String pluginPath;
    //服务端版本
    public static String serverVer;

    private static final String VERSION_PATTERN = "\\(MC: [0-9.]{5}\\)";

    /**
     * 在指定的位置播放一下声音(升级时的音效)
     * 比如玩家获得称号,技能等
     */
    public static void tipSound(Location loc) {
        loc.getWorld().playSound(loc, Sound.LEVEL_UP, 3f, 1.2f);
    }

    /**
     * 在指定的位置显示一下效果(村庄快乐时的效果)
     * 比如玩家获得称号,技能等
     * @param e 取这个实体附近32格内的玩家进行显示
     * @param loc 显示效果的位置
     */
    public static void tipEffect(Entity e, Location loc) {
        PlayerApi.showSpec(null, e, 32, loc, EnumWrappers.Particle.VILLAGER_HAPPY, 22, 0.9f, true);
    }

    /**
     * 切换门的开关状态
     * 版本更新后可能异常
     * @param b 门两个方块中的一个,可为null(null时无效果)
     */
    public static void toggleDoor(Block b) {
        if (b == null) return;

        //方块检测
        if (b.getData() >= (byte)8) b = b.getRelative(BlockFace.DOWN);
        if (b == null || (b.getType() != Material.IRON_DOOR_BLOCK && b.getType() != Material.WOODEN_DOOR)) return;

        if (b.getData() <= (byte)3) b.setData((byte) (b.getData()+4));
        else b.setData((byte) (b.getData()-4));
        b.getState().update(true);
        //声音
        try {
            b.getWorld().playEffect(b.getRelative(BlockFace.UP).getLocation(), Effect.DOOR_TOGGLE, 0);
        } catch (Exception e) {
            //声音可能不存在
        }
    }

    /**
     * 在控制台显示信息(优先进行有颜色显示)
     */
    public static void sendConsoleMessage(Object message) {
        if (Bukkit.getConsoleSender() != null) Bukkit.getConsoleSender().sendMessage(message.toString());
        else info(message);
    }

    public static void info(Object message) {
        CorePlugin.instance.getLogger().info(message.toString());
    }

    public static void warn(Object message) {
        CorePlugin.instance.getLogger().warning(message.toString());
    }

    public static void severe(Object message) {
        CorePlugin.instance.getLogger().severe(message.toString());
    }

    /**
     * 输出调试信息<br>
     *     是否输出由配置文件中的debug选项而定
     * @param msg 调试信息,可为null
     */
    public static void debug(String msg) {
        if (CorePlugin.instance.getCoreConfig().isDebug()) System.out.println("["+UtilApi.getSimpleDateTime()+"] "+msg);
    }

    /**
     * 获取与某个位置最近的玩家
     * @param l 位置
     * @return 不存在返回null
     * @throws IllegalArgumentException 如果l为null
     */
    public static Player getNearestPlayer(Location l) {
        Player p = null;
        double distance = 9999;
        for (Player pp:l.getWorld().getPlayers()) {
            if (p == null) {
                p = pp;
                distance = pp.getLocation().distance(l);
            }else {
                double dis = pp.getLocation().distance(l);
                if (dis < distance) {
                    p = pp;
                    distance = dis;
                }
            }
        }
        return p;
    }

    /**
     * 获取某个位置附近的玩家
     * @param l 位置,可为null(null时返回空列表)
     * @param range 范围,>=0.0(<0.0时返回空列表)
     * @return 不为null
     */
    public static List<Player> getNearbyPlayers(Location l, double range) {
        List<Player> result = new ArrayList<>();
        if (l == null || range < 0.0) return result;
        for (Player p:l.getWorld().getPlayers()) {
            if (l.distance(p.getLocation()) <= range) result.add(p);
        }
        return result;
    }

    /**
     * 给附近指定范围内的所有玩家发送方块更新包
     * @param loc 位置,可为null(null时无效果)
     * @param range 范围,>=0.0(<0.0时无效果)
     * @param block 方块,可为null(null时无效果)
     */
    public static void updateBlock(Location loc, double range, Block block) {
        if (loc == null || range < 0.0 || block == null) return;

        WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange();
        wrapper.setLocation(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        wrapper.setBlockData(WrappedBlockData.createData(block.getType(), block.getData()));
        PacketContainer pc = wrapper.getHandle();
        for (Player p:CoreApi.getNearbyPlayers(loc, range)) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc, true);
            } catch (InvocationTargetException e1) {
            }
        }
    }

    /**
     * @see com.fyxridd.lib.core.Tps#getTps()
     */
    public static double getTps() {
        return Tps.getTps();
    }

    /**
     * 从"plugin.yml"里获取插件版本
     * @param plugin 插件对应的jar文件
     * @return 插件版本字符串,出错返回null
     */
    public static String getPluginVersion(File plugin) {
        JarInputStream jis = null;
        try {
            jis = new JarInputStream(new FileInputStream(plugin));
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equalsIgnoreCase("plugin.yml")) {
                    YamlConfiguration config = new YamlConfiguration();
                    config.load(jis);
                    return config.getString("version",null);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (InvalidConfigurationException e) {
        } finally {
            try {
                if (jis != null) jis.close();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取客户端版本
     * @param server
     * @return 如"1.4.7"这种模式的,出错返回null
     */
    public static String getMcVersion(Server server) {
        try {
            Pattern p = Pattern.compile(VERSION_PATTERN);
            Matcher m = p.matcher(server.getBukkitVersion());
            if (m.find()) {
                String result = m.group();
                if (result != null && !result.trim().isEmpty()) return result.substring(result.indexOf(" ")+1,result.indexOf(")"));
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取服务器的端口
     * @param server
     * @return 端口
     */
    public static int getPort(Server server) {
        return server.getPort();
    }

    /**
     * 获取物品lore中包含指定信息的行
     * @param itemStack 物品,不为null
     * @param message 信息,不为null
     * @return 返回第一次检测成功的行,没有则返回null
     */
    public static String getLine(ItemStack itemStack, String message) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        List<String> lore = itemMeta.getLore();
        if (lore == null) return null;
        for (String s:lore) {
            if (s.indexOf(message) != -1) return s;
        }
        return null;
    }

    /**
     * 获取实体类型
     * @param s 实体类型定义字符串,可以是实体ID或者实体对应的enum值(非实体名)
     * @return 对应的实体类型,出错返回null
     */
    public static EntityType getEntityType(String s) {
        EntityType entityType = null;
        try {
            entityType = EntityType.fromId(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            try {
                entityType = EntityType.valueOf(EntityType.class, s);
            } catch (Exception e1) {
            }
        }
        return entityType;
    }

    /**
     * 获取Material
     * @param s Material定义字符串,可以是方块或物品类型名或ID,可为null(null时返回null)
     * @return 对应的Material,出错返回null
     */
    public static Material getMaterial(String s) {
        if (s == null) return null;
        try {
            try {
                int id = Integer.parseInt(s);
                return Material.getMaterial(id);
            }catch (NumberFormatException e) {
                return Material.getMaterial(s);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 显示闪电
     * @param loc 位置
     * @param range 范围
     * @param effect 是否有闪电效果(着火)
     * @param silent 是否安静(无声音)
     */
    public static void strikeLightning(Location loc, int range, boolean effect, boolean silent) {
        CraftWorld cw = (CraftWorld)loc.getWorld();
        net.minecraft.server.v1_8_R3.World w = cw.getHandle();
        EntityLightning lightning = new EntityLightning(w, loc.getX(), loc.getY(), loc.getZ(), !effect);
        PacketPlayOutSpawnEntityWeather pc = new PacketPlayOutSpawnEntityWeather(lightning);
        PacketPlayOutNamedSoundEffect pc1 = new PacketPlayOutNamedSoundEffect("random.explode", loc.getX(), loc.getY(), loc.getZ(), 2f, 0f);
        PacketPlayOutNamedSoundEffect pc2 = new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", loc.getX(), loc.getY(), loc.getZ(), 2f, 0f);
        for (Player p:loc.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) < range) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pc);
                if (!silent) {
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pc1);
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pc2);
                }
            }
        }
    }

    /**
     * @see #sendMsg(Location, double, boolean, FancyMessage, boolean)
     */
    public static void sendMsg(Location l, double range, boolean nearest, String msg, boolean force) {
        sendMsg(l, range, nearest, MessageApi.convert(msg), force);
    }

    /**
     * 给附近玩家显示信息
     * @param l 位置
     * @param range 范围,>0
     * @param nearest true时只给最近的一个玩家显示信息
     * @param msg 信息
     * @param force 是否强制显示
     */
    public static void sendMsg(Location l, double range, boolean nearest, FancyMessage msg, boolean force) {
        Player p = null;
        double d = 0;
        double temp;
        for (Player tar:l.getWorld().getPlayers()) {
            if (tar.isOnline() && (temp=tar.getLocation().distance(l)) <= range) {
                if (!nearest) MessageApi.send(tar, msg, force);
                else {
                    if (p == null) {
                        p = tar;
                        d = temp;
                    }else {
                        if (temp < d) {
                            p = tar;
                            d = temp;
                        }
                    }
                }
            }
        }
        if (p != null) MessageApi.send(p, msg, force);
    }

    /**
     * 获取附魔
     * @param s 附魔名(非枚举名)或ID
     * @return 附魔,没有返回null
     */
    public static Enchantment getEnchantment(String s) {
        try {
            int id = Integer.parseInt(s);
            return Enchantment.getById(id);
        } catch (NumberFormatException e) {
            return Enchantment.getByName(s);
        }
    }

    /**
     * 获取药效
     * @param s 药效类型名(非枚举名)或ID
     * @return 药效类型,不存在返回null
     */
    public static PotionEffectType getPotionEffectType(String s) {
        PotionEffectType potionEffectType;
        try {
            potionEffectType = PotionEffectType.getById(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            potionEffectType = PotionEffectType.getByName(s);
        }
        return potionEffectType;
    }

    /**
     * 获取墙上的牌子方块所依附的方块
     * @param block 墙上的牌子方块
     * @return 如果方块类型非墙上的牌子或其它异常返回null
     * @throws IllegalArgumentException 如果block为null
     */
    public static Block getSignAttachedBlock(Block block) {
        if (block.getType() != Material.WALL_SIGN) return null;

        int face = block.getData() & 0x7;
        if (face == 3) {
            return block.getRelative(BlockFace.NORTH);
        }
        if (face == 4) {
            return block.getRelative(BlockFace.EAST);
        }
        if (face == 2) {
            return block.getRelative(BlockFace.SOUTH);
        }
        if (face == 5) {
            return block.getRelative(BlockFace.WEST);
        }

        return null;
    }

    /**
     * 获取陷阱门依附的方块
     * @param block 陷阱门方块
     * @return 如果方块类型非陷阱门或其它异常返回null
     * @throws IllegalArgumentException 如果block为null
     */
    public static Block getTrapDoorAttachedBlock(Block block) {
        if (block.getType() != Material.TRAP_DOOR) return null;

        int face = block.getData() & 0x3;
        if (face == 1) {
            return block.getRelative(BlockFace.NORTH);
        }
        if (face == 2) {
            return block.getRelative(BlockFace.EAST);
        }
        if (face == 0) {
            return block.getRelative(BlockFace.SOUTH);
        }
        if (face == 3) {
            return block.getRelative(BlockFace.WEST);
        }

        return null;
    }

    /**
     * 获取活塞的朝向
     * @param block 活塞(扩展)方块
     * @return 异常返回BlockFace.SELF
     * @throws IllegalArgumentException 如果block为null
     */
    public static BlockFace getPistonFacing(Block block) {
        Material type = block.getType();
        if ((type != Material.PISTON_BASE) &&
                (type != Material.PISTON_STICKY_BASE) &&
                (type != Material.PISTON_EXTENSION)) {
            return BlockFace.SELF;
        }

        int face = block.getData() & 0x7;
        switch (face)
        {
            case 0:
                return BlockFace.DOWN;
            case 1:
                return BlockFace.UP;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.WEST;
            case 5:
                return BlockFace.EAST;
        }

        return BlockFace.SELF;
    }

    /**
     * 检测两个位置是否在相同的方块中
     * @param l1 位置1,可为null(null时返回false)
     * @param l2 位置2,可为null(null时返回false)
     * @return 是否在相同的方块中
     */
    public static boolean isInSameBlock(Location l1, Location l2) {
        if (l1 == null || l2 == null) return false;

        return l1.getWorld().equals(l2.getWorld()) && l1.getBlockX() == l2.getBlockX() &&
                l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
    }
}
