package com.fyxridd.lib.core.api;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.core.api.inter.Pos;
import com.fyxridd.lib.core.api.inter.Range;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

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

public class EntityApi {
    /**
     * 获取范围内的所有实体
     * @param range 范围
     * @param accurate 是否精确(不精确时会将范围涉及的区块内所有实体都获取到,这样效率更高)
     * @return 实体列表,不为null
     */
    public static List<Entity> getEntities(Range range, boolean accurate) {
        List<Entity> result = new ArrayList<>();
        Range rangeCopy = range.clone();
        rangeCopy.fit();
        Pos p1 = rangeCopy.getP1();
        Pos p2 = rangeCopy.getP2();
        int xMin = p1.getX()/16 - (p1.getX() < 0?1:0);
        int zMin = p1.getZ()/16 - (p1.getZ() < 0?1:0);
        int xMax = p2.getX()/16 - (p2.getX() < 0?1:0);
        int zMax = p2.getZ()/16 - (p2.getZ() < 0?1:0);
        World w = Bukkit.getWorld(p1.getWorld());
        if (w != null) {
            for (int x = xMin;x<=xMax;x++) {
                for (int z = zMin;z<=zMax;z++) {
                    Chunk c = w.getChunkAt(x, z);
                    if (c != null) {
                        if (c.isLoaded() || c.load(false)) {
                            if (accurate) {
                                for (Entity e:c.getEntities()) {
                                    if (rangeCopy.checkPos(Pos.getPos(e.getLocation()))) result.add(e);
                                }
                            }else Collections.addAll(result, c.getEntities());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取生物上保存的数据
     * @param le 生物
     * @param uid uuid
     * @return 保存的数据,可能为null
     */
    public static String getData(LivingEntity le, UUID uid) {
        EntityLiving el = ((CraftLivingEntity) le).getHandle();
        AttributeInstance ai = el.getAttributeInstance(GenericAttributes.maxHealth);
        if (ai == null) return null;
        AttributeModifier am = ai.a(uid);
        if (am == null) return null;
        else return am.b();
    }

    /**
     * 在生物上保存数据
     * @param le 生物
     * @param uid uuid
     * @param data 数据
     * @return 是否成功
     */
    public static boolean setData(LivingEntity le, UUID uid, String data) {
        try {
            EntityLiving el = ((CraftLivingEntity) le).getHandle();
            AttributeInstance ai = el.getAttributeInstance(GenericAttributes.maxHealth);
            if (ai == null) return false;
            AttributeModifier am = new AttributeModifier(uid, data, 0, 0);
            //先删旧的(没有也不会出错)
            ai.c(am);
            //再添加新的
            ai.b(am);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 指定一个方向,让实体向这个方向轻轻喷射一下
     * @param entity 实体
     * @param from 用来确定方向
     * @param to 用来确定方向
     */
    public static void eject(Entity entity, Location from, Location to) {
        org.bukkit.util.Vector v = to.subtract(from).toVector();
        double length = v.length();
        v.setX(v.getX()/length);
        v.setY(v.getY()/length);
        v.setZ(v.getZ()/length);
        entity.setVelocity(v);
    }

    /**
     * 指定一个方向,让实体向这个方向喷射
     * @param entity 实体
     * @param from 用来确定方向
     * @param to 用来确定方向
     * @param multiply 乘以多少倍
     */
    public static void eject(Entity entity, Location from, Location to, double multiply) {
        org.bukkit.util.Vector v = to.subtract(from).toVector();
        double length = v.length()/multiply;
        v.setX(v.getX()/length);
        v.setY(1.5);
        v.setZ(v.getZ()/length);
        entity.setVelocity(v);
    }

    /**
     * 让实体随机水平方向,垂直方向偏上轻轻喷射一下
     * @param entity 实体
     */
    public static void ejectRandom(Entity entity) {
        org.bukkit.util.Vector v = new org.bukkit.util.Vector(1.0, 0.3, 1.0);
        double d = MathApi.nextInt(0, 10)-5;
        if (d == 0) v.setX(0);
        else v.setX(v.getX()/d);
        d = MathApi.nextInt(0, 10)-5;
        if (d == 0) v.setZ(0);
        else v.setZ(v.getZ()/d);
        double length = v.length();
        v.setX(v.getX() / length);
        v.setZ(v.getZ()/length);
        entity.setVelocity(v);
    }
}
