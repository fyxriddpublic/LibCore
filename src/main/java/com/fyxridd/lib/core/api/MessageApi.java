package com.fyxridd.lib.core.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.PlayerTipEvent;
import com.fyxridd.lib.core.api.fancymessage.Convertable;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.ver.vers.GetHoverActionDataVer;
import com.fyxridd.lib.core.fancymessage.FancyMessageImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONStringer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageApi {
    //false表示未获取过
    private static boolean getHoverActionDataVerFlag;
    private static GetHoverActionDataVer getHoverActionDataVer;

    /**
     * 发送信息
     * @param force 是否强制显示
     */
    public static void send(CommandSender sender, List<FancyMessage> msgs, boolean force) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            //发出玩家显示聊天信息事件
            PlayerTipEvent playerTipEvent = new PlayerTipEvent(p, msgs, force);
            Bukkit.getPluginManager().callEvent(playerTipEvent);

            if (!playerTipEvent.isCancelled()) {
                for (FancyMessage msg:msgs) sendChatPacket(p, msg);
            }
        }else {
            for (FancyMessage msg: msgs) sender.sendMessage(msg.getText());
        }
    }

    /**
     * 发送信息
     * @param force 是否强制显示
     */
    public static void send(CommandSender sender, FancyMessage msg, boolean force) {
        List<FancyMessage> list = new ArrayList<>();
        list.add(msg);
        send(sender, list, force);
    }

    /**
     * @see #send(CommandSender, FancyMessage, boolean)
     */
    public static void send(CommandSender sender, String msg, boolean force) {
        send(sender, convert(msg), force);
    }

    /**
     * 给玩家发送信息(不重要的)<br>
     * 玩家存在且在线时发送
     * @param player 准确的玩家名,不为null
     * @param msg 信息,不为null
     * @param force 是否强制
     */
    public static void send(String player, FancyMessage msg, boolean force) {
        Player p = Bukkit.getServer().getPlayerExact(player);
        if (p != null && p.isOnline()) send(p, msg, force);
    }

    /**
     * @see #send(String, FancyMessage, boolean)
     */
    public static void send(String player, String msg, boolean force) {
        send(player, convert(msg), force);
    }

    /**
     * 直接向玩家发送聊天信息包(不会发出事件,也不会检测ProtocolManager的限制)
     */
    public static void sendChatPacket(Player p, FancyMessage msg) {
        try {
            PacketContainer pc = new PacketContainer(PacketType.Play.Server.CHAT);
            pc.getChatComponents().write(0, WrappedChatComponent.fromJson(msg.toJSONString()));
            CorePlugin.instance.getProtocolManager().sendServerPacket(p, pc, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 往后移位
     * @param offset 移的数量,<=0时无效果
     */
    public static void move(Map<Integer, FancyMessagePart> map, int offset) {
        if (offset <= 0) return;

        //移
        for (int index=map.size()-1;index>=0;index--) {
            map.put(index+offset, map.get(index));
        }

        //删
        for (int index=0;index<offset;index++) {
            map.remove(index);
        }
    }

    /**
     * @return Json String
     */
    public static String makeMultilineTooltip(String[] lines) {
        final JSONStringer json = new JSONStringer();
        try {
            json.object().key("id").value(1);
            json.key("tag").object().key("display").object();
            json.key("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
            json.key("Lore").array();
            for (int i = 1; i < lines.length; i++) {
                final String line = lines[i];
                json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
            }
            json.endArray().endObject().endObject().endObject();
        } catch (final JSONException e) {
            throw new RuntimeException("invalid tooltip");
        }
        return json.toString();
    }

    public static String getHoverActionData(String line) {
        return makeMultilineTooltip(line.split("\n"));
    }

    /**
     * 转换变量(如果无法转换则不会转换)
     * @see Convertable#convert(Object...)
     * @param msg 信息
     * @param replace 变量
     */
    public static void convert(FancyMessage msg, Object... replace) {
        for (FancyMessagePart mp:msg.getMessageParts().values()) mp.convert(replace);
    }

    /**
     * 转换单个变量(如果无法转换则不会转换)
     * @see Convertable#convert(String, Object)
     */
    public static void convert(FancyMessage msg, String from, String to) {
        for (FancyMessagePart mp:msg.getMessageParts().values()) mp.convert(from, to);
    }

    /**
     * 转换变量(如果无法转换则不会转换)
     * @see Convertable#convert(Map)
     */
    public static void convert(FancyMessage msg, Map<String, Object> replace) {
        for (FancyMessagePart mp:msg.getMessageParts().values()) mp.convert(replace);
    }

    /**
     * 根据物品获取悬浮提示信息(mc能识别处理的)
     * @param is 物品,不为null
     * @return 提示信息,异常返回null
     */
    public static String getHoverActionData(ItemStack is) {//todo
        if (!getHoverActionDataVerFlag) {
            getHoverActionDataVerFlag = true;
            getHoverActionDataVer = VerApi.get(GetHoverActionDataVer.class);
        }
        if (getHoverActionDataVer != null) return getHoverActionDataVer.getHoverActionData(is);
        else return null;
//        return CraftItemStack.asNMSCopy(is).save(new NBTTagCompound()).toString();
    }

    public static void color(FancyMessagePart mp, final ChatColor color) {
        if (!color.isColor()) throw new IllegalArgumentException(color.name() + " is not a color");
        mp.setColor(color);
    }

    public static void style(FancyMessagePart mp, final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) throw new IllegalArgumentException(style.name() + " is not a style");
        }
        mp.setStyles(styles);
    }

    public static void file(FancyMessagePart mp, final String path) {
        onClick(mp, "open_file", path);
    }

    public static void link(FancyMessagePart mp, final String url) {
        onClick(mp, "open_url", url);
    }

    public static void suggest(FancyMessagePart mp, final String command) {
        onClick(mp, "suggest_command", command);
    }

    public static void command(FancyMessagePart mp, final String command) {
        onClick(mp, "run_command", command);
    }

    public static void itemTooltip(FancyMessagePart mp, final String itemJSON, final String hoverActionString) {
        onHover(mp, "show_item", itemJSON, hoverActionString);
    }

    public static void itemTooltip(FancyMessagePart mp, final ItemStack itemStack, final String hoverActionString) {
        itemTooltip(mp, getHoverActionData(itemStack), hoverActionString);
    }

    /**
     * 将纯文字转化为FancyMessage格式
     * @param msg 纯文字
     * @return 对应的FancyMessage
     */
    public static FancyMessage convert(String msg) {
        return new FancyMessageImpl(msg);
    }

    private static void onClick(FancyMessagePart mp, final String name, final String data) {
        mp.setClickActionName(name);
        mp.setClickActionData(data);
    }

    private static void onHover(FancyMessagePart mp, final String name, final String data, final String hoverActionString) {
        mp.setHoverActionName(name);
        mp.setHoverActionData(data);
    }

    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
        return CorePlugin.instance.getMessageManager().load(msg, config);
    }
}
