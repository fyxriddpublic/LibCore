package com.fyxridd.lib.core.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.PlayerTipEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.fancymessage.FancyMessageImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONStringer;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MessageApi {
    /**
     * 发送信息
     */
    public static void sendGraceful(CommandSender sender, FancyMessage msg, boolean force) {
        if (sender instanceof Player) send((Player) sender, msg, force);
        else sender.sendMessage(msg.getText());
    }

    /**
     * @see #send(Player, FancyMessage, boolean)
     */
    public static void send(Player p, String msg, boolean force) {
        send(p, convert(msg), force);
    }

    /**
     * 向玩家发送FancyMessage
     * 会发出玩家提示事件
     * @param p 玩家
     * @param msg 信息
     * @param force 是否强制显示
     */
    public static void send(Player p, FancyMessage msg, boolean force) {
        //发出玩家显示聊天信息事件
        PlayerTipEvent playerTipEvent = new PlayerTipEvent(p, msg, force);
        Bukkit.getPluginManager().callEvent(playerTipEvent);

        if (!playerTipEvent.isCancelled()) sendChatPacket(p, msg);
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
     * 根据物品获取悬浮提示信息(mc能识别处理的)
     * @param is 物品,不为null
     * @return 提示信息
     */
    public static String getHoverActionData(ItemStack is) {
        return CraftItemStack.asNMSCopy(is).save(new NBTTagCompound()).toString();
    }

    public static void color(FancyMessagePart mp, final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }
        mp.color = color;
    }

    public static void style(FancyMessagePart mp, final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        mp.styles = styles;
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
        itemTooltip(mp, CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString(), hoverActionString);
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
        mp.clickActionName = name;
        mp.clickActionData = data;
    }

    private static void onHover(FancyMessagePart mp, final String name, final String data, final String hoverActionString) {
        mp.hoverActionName = name;
        mp.hoverActionData = data;
        mp.hoverActionString = hoverActionString;
    }

    /**
     * 保存信息为字符串
     * @param msg 可为null(null时返回null)
     * @return 可为null
     */
    public static String save(FancyMessage msg) {
        if (msg == null) return null;

        //保存到config
        YamlConfiguration config = new YamlConfiguration();
        FormatApi.save(1, config, msg);
        return config.saveToString();
    }

    /**
     * 从字符串中读取信息
     * @param data 可为null(null时返回null)
     * @return 可为null
     */
    public static FancyMessage load(String data) {
        if (data == null) return null;

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
            return FormatApi.load(config.getString("show-1"), (MemorySection) config.get("info-1"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage load(String msg, ConfigurationSection config) {
        return CorePlugin.instance.getCoreManager().getMessageManager().load(msg, config);
    }
}
