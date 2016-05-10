package com.fyxridd.lib.core.api.fancymessage;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface FancyMessage extends Cloneable{
    FancyMessage text(final String text);
	
	FancyMessage color(final ChatColor color);

	FancyMessage style(final ChatColor... styles);
	
	FancyMessage file(final String path);
	
	FancyMessage link(final String url);
	
	FancyMessage suggest(final String command);
	
	FancyMessage command(final String command);
	
	FancyMessage itemTooltip(final String itemJSON);
	
	FancyMessage itemTooltip(final ItemStack itemStack);
	
	FancyMessage tooltip(final String text);

	FancyMessage then(final Object obj);

    List<FancyMessagePart> getMessageParts();

    /**
     * 获取无格式文本(在没有hover与click的情况下信息内容是一样的)
     */
    String getText();

    /**
     * 将另一个FancyMessage结合进来
     * @param fm 另一个FancyMessage
     * @param front true表示将另一个fm放前面,false表示将另一个fm放后面
     */
    void combine(FancyMessage fm, boolean front);

	String toJSONString();

	FancyMessage clone();
}
