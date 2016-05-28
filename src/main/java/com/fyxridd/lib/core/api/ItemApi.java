package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.FixDamageEvent;
import com.fyxridd.lib.core.api.nbt.AttributeStorage;
import com.fyxridd.lib.core.api.nbt.Attributes;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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

    public static final ItemMeta EmptyIm = new ItemStack(Material.STONE).getItemMeta();
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
        Integer damage = CorePlugin.instance.getCoreConfig().getFixDamage().get(is.getTypeId());
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
    

    /**
     * 检测指定的物品是否有耐久度
     * @param is 检测的物品,不为null
     * @return 是否有耐久度
     */
    public static boolean hasDurability(ItemStack is) {
        return is.getMaxStackSize() == 1 && is.getType().getMaxDurability() > 1;
    }

    /**
     * 检测容器中是否有空格子
     * @param inv 容器,不为null
     */
    public static boolean hasEmptySlot(Inventory inv) {
        return hasEmptySlots(inv, 1);
    }

    /**
     * 检测容器中是否有指定数量的空格子
     * @param inv 容器,不为null
     * @param amount 数量
     */
    public static boolean hasEmptySlots(Inventory inv, int amount) {
        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
                sum++;
                if (sum >= amount) return true;
            }
        }
        return false;
    }

    /**
     * 获取容器中空格子的数量
     * @param inv 容器,不为null
     * @return 没有返回0
     */
    public static int getEmptySlots(Inventory inv) {
        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) sum++;
        }
        return sum;
    }

    /**
     * 检测玩家背包是否为空
     * @param p 检测的玩家
     * @return 背包为空返回true,否则返回false
     */
    public static boolean checkEmpty(Player p) {
        PlayerInventory pi = p.getInventory();
        for (int i=0;i<40;i++) {
            if (pi.getItem(i) != null && !pi.getItem(i).getType().equals(Material.AIR)) return false;
        }
        return true;
    }

    /**
     * 不检测小id
     * @see #hasNormalItem(org.bukkit.inventory.Inventory, Material, int, int)
     */
    public static boolean hasNormalItem(Inventory inv,Material type,int amount) {
        return hasNormalItem(inv, type, -1, amount);
    }

    /**
     * 检测容器中是否有指定数量的'普通'物品(ItemMeta为空)
     * @param inv 容器,不为null
     * @param type 物品类型
     * @param smallId 物品小id,-1表示不检测小id
     * @param amount 物品数量
     * @return 是否含有指定数量的物品
     */
    public static boolean hasNormalItem(Inventory inv, Material type, int smallId, int amount) {
        //之所以不调用getNormalAmount是为了提高效率!
        int sum = 0;
        for (ItemStack is:inv.getContents()) {
            if (is != null &&
                    is.getType() == type &&
                    (smallId == -1 || is.getDurability() == smallId) &&
                    isItemMetaEmpty(is.getItemMeta())) {
                sum += is.getAmount();
                if (sum >= amount) return true;
            }
        }
        return false;
    }

    /**
     * 不忽略物品名
     * @see #hasExactItem(org.bukkit.inventory.Inventory, org.bukkit.inventory.ItemStack, int, boolean)
     */
    public static boolean hasExactItem(Inventory inv, ItemStack is, int amount) {
        return hasExactItem(inv, is, amount, false);
    }

    /**
     * 检测容器中是否有指定数量的'精确'物品
     * @param inv 容器,不为null
     * @param is 物品
     * @param amount 物品数量
     * @param ignoreName 是否忽略物品名
     * @return 是否含有指定数量的精确物品
     */
    public static boolean hasExactItem(Inventory inv, ItemStack is, int amount, boolean ignoreName) {
        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            ItemStack check = inv.getItem(i);
            if (check != null && isSameItem(is, check, ignoreName)) {
                sum += inv.getItem(i).getAmount();
                if (sum >= amount) return true;
            }
        }
        return false;
    }

    /**
     * 不检测小id
     * @see #removeNormalItem(org.bukkit.inventory.Inventory, Material, int, int, boolean)
     */
    public static boolean removeNormalItem(Inventory inv, Material type, int amount, boolean force) {
        return removeNormalItem(inv, type, -1, amount, force);
    }

    /**
     * 从指定容器中移除指定数量的'普通'物品(ItemMeta为空)
     * @param inv 容器,不为null
     * @param type 物品类型
     * @param smallId 物品小id,-1表示不检测小id
     * @param amount 要移除的数量
     * @param force 如果容器中物品数量不足,是否移除已经拥有的
     * @return 如果容器中没有指定数量的指定物品,返回false
     */
    public static boolean removeNormalItem(Inventory inv, Material type, int smallId, int amount, boolean force) {
        if (amount <= 0) return true;
        if (hasNormalItem(inv, type, smallId, amount)) {
            for (int i=0;i<inv.getSize();i++) {
                if (inv.getItem(i) != null){
                    ItemStack is = inv.getItem(i);
                    if (is.getType() == type && (smallId == -1 || is.getDurability() == smallId) && isItemMetaEmpty(is.getItemMeta())) {
                        if (amount >= is.getAmount()) {
                            amount -= is.getAmount();
                            inv.setItem(i, null);
                        }else {
                            is.setAmount(is.getAmount()-amount);
                            amount = 0;
                        }
                        if (amount <= 0) break;
                    }
                }
            }
            return true;
        }else if (force) {
            for (int i=0;i<inv.getSize();i++) {
                if (inv.getItem(i) != null){
                    ItemStack is = inv.getItem(i);
                    if (is.getType() == type && (smallId == -1 || is.getDurability() == smallId) && isItemMetaEmpty(is.getItemMeta())) {
                        if (amount >= is.getAmount()) {
                            amount -= is.getAmount();
                            inv.setItem(i, null);
                        }else {
                            is.setAmount(is.getAmount()-amount);
                            amount = 0;
                        }
                        if (amount <= 0) break;
                    }
                }
            }
            return false;
        }else return false;
    }

    /**
     * 从指定容器中移除指定数量的指定物品(精确的)
     * @param inv 容器,不为null
     * @param is 物品,不为null
     * @param amount 要移除的数量
     * @param force 如果容器中物品数量不足,是否移除已经拥有的
     * @return 如果容器中没有指定数量的指定物品,返回false
     */
    public static boolean removeExactItem(Inventory inv, ItemStack is, int amount, boolean force) {
        return removeExactItem(inv, is, amount, force, false);
    }

    /**
     * 从指定容器中移除指定数量的指定物品(精确的)
     * @param inv 容器,不为null
     * @param is 物品,不为null
     * @param amount 要移除的数量
     * @param force 如果容器中物品数量不足,是否移除已经拥有的
     * @param ignoreName 是否忽略物品名
     * @return 如果容器中没有指定数量的指定物品,返回false
     */
    public static boolean removeExactItem(Inventory inv, ItemStack is, int amount, boolean force, boolean ignoreName) {
        if (amount <= 0) return true;
        if (force || hasExactItem(inv, is, amount, ignoreName)) {
            //需要减少的数量
            int need = amount;
            for (int i=0;i<inv.getSize();i++) {
                ItemStack is2 = inv.getItem(i);
                if (is2 != null && isSameItem(is, is2, ignoreName)) {//检测相同成功,减少物品
                    int has = is2.getAmount();
                    if (need <= has) {//结束
                        if (has == need) inv.setItem(i, null);
                        else is2.setAmount(has-need);
                        need = 0;
                        break;
                    }else {
                        need -= has;
                        inv.setItem(i, null);
                        continue;
                    }
                }
            }
            return need <= 0;
        }else return false;
    }

    /**
     * 不检测小id
     * @see #getNormalItemAmount(org.bukkit.inventory.Inventory, int, int)
     */
    public static int getNormalItemAmount(Inventory inv,int id) {
        return getNormalItemAmount(inv, id, -1);
    }

    /**
     * 获取指定容器中指定id与小id的'普通'物品的数量(ItemMeta为空)
     * @param inv 容器,不为null
     * @param id 物品id
     * @return 数量,>=0
     */
    public static int getNormalItemAmount(Inventory inv,int id, int smallId) {
        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            ItemStack is = inv.getItem(i);
            if (is != null &&
                    is.getTypeId() == id &&
                    (smallId == -1 || is.getDurability() == smallId) &&
                    isItemMetaEmpty(is.getItemMeta())) {
                sum += is.getAmount();
            }
        }
        return sum;
    }

    /**
     * 不忽略物品名
     * @see #getExactItemAmount(org.bukkit.inventory.Inventory, org.bukkit.inventory.ItemStack, boolean)
     */
    public static int getExactItemAmount(Inventory inv, ItemStack is) {
        return getExactItemAmount(inv, is, false);
    }

    /**
     * 获取指定容器中指定物品的数量<br>
     * 会检测id,durability,ItemMeta,attributes
     * @param inv 容器,不为null
     * @param is 物品,不为null
     * @param ignoreName 是否忽略物品名
     * @return 数量,>=0
     */
    public static int getExactItemAmount(Inventory inv, ItemStack is, boolean ignoreName) {
        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            ItemStack check = inv.getItem(i);
            if (check != null && isSameItem(is, check, ignoreName)) sum += inv.getItem(i).getAmount();
        }
        return sum;
    }

    /**
     * 不忽略物品名
     * @see #isSameItem(org.bukkit.inventory.ItemStack, org.bukkit.inventory.ItemStack, boolean)
     */
    public static boolean isSameItem(ItemStack is1, ItemStack is2) {
        return isSameItem(is1, is2, false);
    }

    /**
     * 检测是否是相同的物品<br>
     * 会检测id,durability,ItemMeta,attributes<br>
     * 检测不包括数量
     * @param is1 物品1,不为null
     * @param is2 物品2,不为null
     * @param ignoreName 是否忽略物品名
     * @return 是否相同
     */
    public static boolean isSameItem(ItemStack is1, ItemStack is2, boolean ignoreName) {
        //id,durability
        if (is1.getType().equals(is2.getType()) && is1.getDurability() == is2.getDurability()) {
            if (is1.getType().equals(Material.AIR)) return true;//空气,特殊情况
            //itemMeta
            ItemMeta im1 = is1.getItemMeta();
            ItemMeta im2 = is2.getItemMeta();
            if (im1 == null) {
                if (im2 != null) return false;
            }else {
                if (im2 == null) return false;
                else {
                    if (ignoreName) im2.setDisplayName(im1.getDisplayName());//把两个名字强制改成一样的
                    if (!im1.equals(im2)) return false;
                }
            }
            //attributes
            if (!hasSameAttributes(is1, is2)) return false;
            //相同
            return true;
        }
        return false;
    }

    /**
     * 检测两个物品的Attributes是否相同
     * @param is1 物品1,不为null
     * @param is2 物品2,不为null
     * @return 是否相同
     */
    public static boolean hasSameAttributes(ItemStack is1, ItemStack is2) {
        //空气,特殊情况
        if (is1.getType().equals(Material.AIR)) {
            return is2.getType().equals(Material.AIR);
        }else {
            if (is2.getType().equals(Material.AIR)) return false;
        }
        //其它物品
        Attributes a1 = new Attributes(is1);
        Attributes a2 = new Attributes(is2);
        if (a1.size() == a2.size()) {
            if (a1.size() == 0) return true;//两个Attri都为空
            //检测
            Iterator<Attributes.Attribute> it1 = a1.values();
            Iterator<Attributes.Attribute> it2 = a2.values();
            while (it1.hasNext()) {
                if (!it1.next().equals(it2.next())) return false;
            }
            return true;
        }
        //Attri数量不一样
        return false;
    }

    /**
     * 检测ItemMeta是否为空
     * @param itemMeta 可为null,null时返回true
     */
    public static boolean isItemMetaEmpty(ItemMeta itemMeta) {
        if (itemMeta == null) return true;
        return itemMeta.equals(EmptyIm);
    }
}
