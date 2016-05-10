package com.fyxridd.lib.core.api.fancymessage;

/**
 * 悬浮提示可被物品化的
 * (可被FancyMessagePart的子类实现)
 */
public interface Itemable {
    /**
     * @return 返回绑定的悬浮物品名,可为null
     */
    String getHoverItem();
}
