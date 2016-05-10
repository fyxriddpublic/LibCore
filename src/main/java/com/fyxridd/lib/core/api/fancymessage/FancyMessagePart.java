package com.fyxridd.lib.core.api.fancymessage;

import org.bukkit.ChatColor;
import org.json.JSONException;
import org.json.JSONWriter;

public class FancyMessagePart implements Cloneable{
    protected String text;
    protected ChatColor color;
    protected ChatColor[] styles;
    protected String clickActionName;
    protected String clickActionData;
    protected String hoverActionName;
    protected String hoverActionData;

    public FancyMessagePart(String text) {
        this.text = text;
    }

    public FancyMessagePart(String text, ChatColor color,
                             ChatColor[] styles, String clickActionName,
                             String clickActionData, String hoverActionName,
                             String hoverActionData) {
        super();
        this.text = text;
        this.color = color;
        this.styles = styles;
        this.clickActionName = clickActionName;
        this.clickActionData = clickActionData;
        this.hoverActionName = hoverActionName;
        this.hoverActionData = hoverActionData;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public ChatColor[] getStyles() {
        return styles;
    }

    public void setStyles(ChatColor[] styles) {
        this.styles = styles;
    }

    public String getClickActionName() {
        return clickActionName;
    }

    public void setClickActionName(String clickActionName) {
        this.clickActionName = clickActionName;
    }

    public String getClickActionData() {
        return clickActionData;
    }

    public void setClickActionData(String clickActionData) {
        this.clickActionData = clickActionData;
    }

    public String getHoverActionName() {
        return hoverActionName;
    }

    public void setHoverActionName(String hoverActionName) {
        this.hoverActionName = hoverActionName;
    }

    public String getHoverActionData() {
        return hoverActionData;
    }

    public void setHoverActionData(String hoverActionData) {
        this.hoverActionData = hoverActionData;
    }

    /**
     * 获取格式字符串
     * @return 如'ln',空则返回''
     */
    public String getFormatsStr() {
        if (styles == null) return "";
        String result = "";
        for (ChatColor cc:styles) result += cc.getChar();
        return result;
    }

    /**
     * 获取颜色字符串
     * @return 如'e',空则返回''
     */
    public String getColorStr() {
        if (color == null) return "";
        else return String.valueOf(color.getChar());
    }

    public JSONWriter writeJson(final JSONWriter json) throws JSONException {
        json.object().key("text").value(text);
        if (color != null) {
            json.key("color").value(color.name().toLowerCase());
        }
        if (styles != null) {
            for (final ChatColor style : styles) {
                json.key(style == ChatColor.UNDERLINE ? "underlined" : style.name().toLowerCase()).value(true);
            }
        }
        if (clickActionName != null && clickActionData != null) {
            json.key("clickEvent")
                    .object()
                    .key("action").value(clickActionName)
                    .key("value").value(clickActionData)
                    .endObject();
        }
        if (hoverActionName != null && hoverActionData != null) {
            json.key("hoverEvent")
                    .object()
                    .key("action").value(hoverActionName)
                    .key("value").value(hoverActionData)
                    .endObject();
        }
        return json.endObject();
    }

    @Override
    public FancyMessagePart clone() {
        //color
        ChatColor color;
        if (this.color != null) color = ChatColor.getByChar(this.color.getChar());
        else color = null;
        //styles
        ChatColor[] styles;
        if (this.styles != null) styles = this.styles.clone();
        else styles = null;
        //新建
        return new FancyMessagePart(text, color, styles, clickActionName, clickActionData, hoverActionName, hoverActionData);
    }

    /**
     * 检测两个MessagePart是否可以合并<br>
     *     以下情况时,可以合并:<br>
     *     onClick都为空<br>
     *     onHover都为空<br>
     *     color相同<br>
     *     formats相同
     * @param mp 检测的MessagePart
     * @return 是否可以进行合并
     */
    public boolean isSame(FancyMessagePart mp) {
        if ((clickActionData == null || clickActionData.isEmpty()) && (mp.clickActionData == null || mp.clickActionData.isEmpty())) {
            if ((hoverActionData == null || hoverActionData.isEmpty()) && (mp.hoverActionData == null || mp.hoverActionData.isEmpty())) {
                //color
                if (color == null) {
                    if (mp.color != null) return false;
                } else {
                    if (mp.color == null || !color.equals(mp.color)) return false;
                }
                //formats
                if (styles == null) {
                    return mp.styles == null;
                } else {
                    if (mp.styles == null) return false;
                    if (styles.length == mp.styles.length) {
                        for (ChatColor cc : styles) {
                            boolean has = false;
                            for (ChatColor dd : mp.styles) {
                                if (cc.equals(dd)) {
                                    has = true;
                                    break;
                                }
                            }
                            if (!has) return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 将mp合并进来<br>
     *     (不检测是否可合并)
     * @param mp 要合并进来的MessagePart
     */
    public void combine(FancyMessagePart mp) {
        text += mp.text;
    }

    /**
     * 检测是否为空
     * @return 是否为空
     */
    public boolean isEmpty() {
        return text.isEmpty() && color == null && styles == null && clickActionName == null && hoverActionName == null;
    }
}
