package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.fancymessage.FancyMessageImpl;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private final String NUM_LIST = "0123456789";
    public static final String CLICK_FILE = "file";
    public static final String CLICK_URL = "url";
    public static final String CLICK_SUGGEST = "suggest";
    public static final String CLICK_CMD = "cmd";

    /**
     * @see MessageApi#load(String, ConfigurationSection)
     */
    public FancyMessage load(String msg, ConfigurationSection config) {
        String[] ss = split(msg);
        FancyMessage fm = null;
        boolean first = true;
        int index = 0;
        for (String s:ss) {
            if (first) first = false;
            else {//读取一个MessagePart
                if (config != null) {
                    ConfigurationSection directConfig = (ConfigurationSection) config.get(""+index);
                    //index+1
                    index++;
                    //text
                    {
                        String text = UtilApi.convert(directConfig.getString("text", ""));
                        if (fm == null) fm = MessageApi.convert(text);
                        else fm.then(text);
                    }
                    //color
                    {
                        String color = directConfig.getString("color", "");
                        if (color != null && !color.isEmpty()) fm.color(ChatColor.getByChar(color));
                    }
                    //styles
                    {
                        String formats = directConfig.getString("formats", "");
                        if (formats != null && !formats.isEmpty()) {
                            ChatColor[] styles = new ChatColor[formats.length()];
                            int i2 = 0;
                            for (char c:formats.toCharArray()) styles[i2++] = ChatColor.getByChar(c);
                            fm.style(styles);
                        }
                    }
                    //onClick
                    {
                        String onClick = UtilApi.convert(directConfig.getString("onClick", ""));
                        if (onClick != null && !onClick.isEmpty()) {
                            String[] s2 = onClick.split(" ");
                            String content = UtilApi.combine(s2, " ", 1, s2.length);
                            if (CLICK_FILE.equals(s2[0])) fm.file(content);
                            else if (CLICK_URL.equals(s2[0])) fm.link(content);
                            else if (CLICK_SUGGEST.equals(s2[0])) fm.suggest(content);
                            else if (CLICK_CMD.equals(s2[0])) fm.command(content);
                        }
                    }
                    //onHover
                    {
                        String onHover = UtilApi.convert(directConfig.getString("onHover", ""));
                        if (onHover != null && !onHover.isEmpty()) fm.itemTooltip(MessageApi.getHoverActionData(onHover));
                    }
                }
            }
            if (!s.isEmpty()) {
                if (fm == null) fm = MessageApi.convert(s);
                else fm.then(s);
            }
        }
        if (fm == null) fm = MessageApi.convert("");
        return fm;
    }

    /**
     * 把字符串以占位符分割,分割后的长度=占位符数量+1
     * @param s 字符串,不为null
     * @return 分割后的字符串序列
     */
    public String[] split(String s) {
        List<String> result = new ArrayList<>();
        StringBuffer add = new StringBuffer();
        for (int i=0;i<s.length();i++) {
            if (s.charAt(i) == '<') {
                boolean hasNum = false;
                boolean hasEnd = false;
                int end = i;
                for (int j=i+1;j<s.length();j++) {
                    if (NUM_LIST.indexOf(s.charAt(j)) != -1) {
                        hasNum = true;
                    }else if (s.charAt(j) == '>') {
                        hasEnd = true;
                        end = j;
                        break;
                    }else {
                        break;
                    }
                }
                //有数字,有结束标志>,说明已经成功检测到了一个完整的替代符
                //start - end
                if (hasNum && hasEnd) {
                    result.add(add.toString());
                    add = new StringBuffer();
                    i = end;
                    continue;
                }
            }
            add.append(s.charAt(i));
        }
        result.add(add.toString());
        return result.toArray(new String[result.size()]);
    }
}
