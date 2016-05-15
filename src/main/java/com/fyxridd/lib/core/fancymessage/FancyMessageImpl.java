package com.fyxridd.lib.core.fancymessage;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.fancymessage.Optimizable;
import org.bukkit.ChatColor;
import org.json.JSONException;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;

public class FancyMessageImpl implements FancyMessage,Optimizable {
	private final Map<Integer, FancyMessagePart> messageParts;

	private FancyMessageImpl(Map<Integer, FancyMessagePart> messageParts) {
		this.messageParts = messageParts;
	}

	/**
	 * 请勿直接调用此构造器,使用MessageApi.convert(String)来代替
	 * @see MessageApi#convert(String)
     */
	public FancyMessageImpl(String firstPartText) {
		messageParts = new HashMap<>();
        then(firstPartText);
	}

    @Override
	public FancyMessageImpl text(final String text) {
		latest().setText(text);
		return this;
	}

    @Override
	public FancyMessageImpl color(final ChatColor color) {
		if (!color.isColor()) {
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		latest().setColor(color);
		return this;
	}

    @Override
	public FancyMessageImpl style(final ChatColor... styles) {
		for (final ChatColor style : styles) {
			if (!style.isFormat()) {
				throw new IllegalArgumentException(style.name() + " is not a style");
			}
		}
		latest().setStyles(styles);
		return this;
	}

    @Override
	public FancyMessageImpl file(final String path) {
		onClick("open_file", path);
		return this;
	}

    @Override
	public FancyMessageImpl link(final String url) {
		onClick("open_url", url);
		return this;
	}

    @Override
	public FancyMessageImpl suggest(final String command) {
		onClick("suggest_command", command);
		return this;
	}

    @Override
	public FancyMessageImpl command(final String command) {
		onClick("run_command", command);
		return this;
	}

    @Override
	public FancyMessageImpl itemTooltip(final String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}

    @Override
	public FancyMessageImpl tooltip(final String text) {
		final String[] lines = text.split("\\n");
		if (lines.length <= 1) {
			onHover("show_text", text);
		} else {
			itemTooltip(MessageApi.makeMultilineTooltip(lines));
		}
		return this;
	}

    @Override
	public FancyMessageImpl then(final Object obj) {
		messageParts.put(messageParts.size(), new FancyMessagePart(obj.toString()));
		return this;
	}

    @Override
    public Map<Integer, FancyMessagePart> getMessageParts() {
        return messageParts;
    }

    @Override
	public String getText() {
		String result = "";
        for (int index=0;index<messageParts.size();index++) result += messageParts.get(index).getText();
		return result;
	}

    @Override
    public void combine(FancyMessage fm, boolean front) {
        //复制
        FancyMessage fmClone = fm.clone();
        //移位
        if (front) MessageApi.move(messageParts, fmClone.getMessageParts().size());
        else MessageApi.move(fmClone.getMessageParts(), messageParts.size());
        //添加
        messageParts.putAll(fmClone.getMessageParts());
    }

    @Override
    public String toJSONString() {
        final JSONStringer json = new JSONStringer();
        try {
            if (messageParts.size() == 1) {
                latest().writeJson(json);
            } else {
                json.object().key("text").value("").key("extra").array();
                for (int index=0;index<messageParts.size();index++) messageParts.get(index).writeJson(json);
                json.endArray().endObject();
            }
        } catch (final JSONException e) {
            throw new RuntimeException("invalid message");
        }
        return json.toString();
    }

    @Override
    public FancyMessageImpl clone() {
        Map<Integer, FancyMessagePart> messageParts = new HashMap<>();
        for (Map.Entry<Integer, FancyMessagePart> entry:this.messageParts.entrySet()) messageParts.put(entry.getKey(), entry.getValue().clone());
        return new FancyMessageImpl(messageParts);
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public void optimize() {
        int index = 0;
        while (true) {
            if (index >= messageParts.size()) break;
            FancyMessagePart mpFrom = messageParts.get(index);
            //检测删除空的MessagePart
            if (mpFrom.isEmpty()) messageParts.remove(index);
            else {
                if (index >= messageParts.size()-1) break;
                //对每个MessagePart与其后的MessagePart进行合并检测
                FancyMessagePart mpTo = messageParts.get(index + 1);
                if (mpFrom.isSame(mpTo)) {
                    mpFrom.combine(mpTo);
                    messageParts.remove(index + 1);
                } else index++;
            }
        }
    }

    public FancyMessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}

	private void onClick(final String name, final String data) {
		final FancyMessagePart latest = latest();
		latest.setClickActionName(name);
        latest.setClickActionData(data);
	}

	private void onHover(final String name, final String data) {
		final FancyMessagePart latest = latest();
        latest.setHoverActionName(name);
        latest.setHoverActionData(data);
	}
}
