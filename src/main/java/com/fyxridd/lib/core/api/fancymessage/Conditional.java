package com.fyxridd.lib.core.api.fancymessage;

/**
 * 可条件选择显示的
 * (可被FancyMessagePart的子类实现)
 */
public interface Conditional {
    /**
     * 条件显示检测
     */
    boolean checkCondition();
}
