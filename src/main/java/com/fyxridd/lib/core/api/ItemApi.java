package com.fyxridd.lib.core.api;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fyxridd.lib.core.api.event.FixDamageEvent;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.core.api.inter.FancyMessage;
import com.fyxridd.lib.core.api.nbt.AttributeStorage;
import com.fyxridd.lib.core.api.nbt.Attributes;

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

public class ItemApi {
    public static class ItemUidReturn {
        private ItemStack is;
        private String uid;
        private boolean create;

        public ItemUidReturn(ItemStack is, String uid, boolean create) {
            this.is = is;
            this.uid = uid;
            this.create = create;
        }

        public ItemStack getIs() {
            return is;
        }

        public String getUid() {
            return uid;
        }

        /**
         * 是否新建(新建指物品需要更新)
         */
        public boolean isCreate() {
            return create;
        }
    }

    public static final ItemMeta EmptyIm = new ItemStack(1).getItemMeta();
    private static UUID fixDamageUid = UUID.fromString("0dd52480-7e43-41e2-8a43-e0af83c614ec");
    private static UUID itemUid = UUID.fromString("24ca113c-6c2e-49e8-b41a-535156a6febc");

    /**
     * 获取物品的Uid,不存在则会新建
     * (同时会修正物品的伤害值)
     * @param is 物品,不为null
     * @param generate 没有时是否生成
     * @return uid信息,可为null
     */
    public static ItemUidReturn getUid(ItemStack is, boolean generate) {
        String data = getData(is, itemUid);
        boolean create = false;
        if (data == null) {
            if (generate) {
                create = true;
                data = UUID.randomUUID().toString();
                is = setData(is, itemUid, data);
            }else return null;
        }
        return new ItemUidReturn(is, data, create);
    }

    /**
     * 获取保存在物品上的数据
     * @param is 物品,不为null
     * @param key 唯一的key,不为null
     * @return 数据,不存在返回null
     */
    public static String getData(ItemStack is, UUID key) {
        AttributeStorage as = AttributeStorage.newTarget(is, key);
        return as.getData();
    }

    /**
     * 设置保存在物品上的数据
     * @param is 物品,不为null
     * @param key 唯一的key,不为null
     * @param data 数据,null表示删除
     * @return 设置后的物品
     */
    public static ItemStack setData(ItemStack is, UUID key, String data) {
        AttributeStorage as = AttributeStorage.newTarget(is, key);
        as.setData(data);
        is = as.getTarget();
        //修正伤害
        is = fixDamage(is);
        //返回
        return is;
    }

    /**
     * 修正伤害
     * @param is 物品
     * @return 修正后的物品,可能与原来的相同或不同
     */
    public static ItemStack fixDamage(ItemStack is) {
        Integer damage = CoreMain.fixDamage.get(is.getTypeId());
        if (damage != null && damage > 0) {//物品本身是有伤害的,需要检测
            //解析
            Attributes.Attribute a = null;
            Attributes attributes = new Attributes(is);
            Iterator<Attributes.Attribute> it = attributes.values();
            while (it.hasNext()) {
                Attributes.Attribute attribute = it.next();
                if (attribute.getUUID().equals(fixDamageUid)) {
                    a = attribute;
                    break;
                }
            }

            //发出事件
            FixDamageEvent fixDamageEvent = new FixDamageEvent(is, damage, true);
            Bukkit.getPluginManager().callEvent(fixDamageEvent);

            //处理
            if (fixDamageEvent.isSet()) {//设置
                //检测新建
                if (a == null) {
                    a = Attributes.Attribute.newBuilder().uuid(fixDamageUid).type(Attributes.AttributeType.GENERIC_ATTACK_DAMAGE).amount(0).name("fixDamage").operation(Attributes.Operation.ADD_NUMBER).build();
                    attributes.add(a);
                }
                //设置数量
                a.setAmount(damage);
                //更新物品
                is = attributes.getStack();
            }else {//删除
                if (a != null) {
                    attributes.remove(a);
                    //更新物品
                    is = attributes.getStack();
                }
            }
        }

        //返回
        return is;
    }

    private static FancyMessage get(int id, Object... args) {
        return FormatApi.get(CorePlugin.pn, id, args);
    }
}
