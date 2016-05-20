package com.fyxridd.lib.core.api.fancymessage;

import java.util.Map;

/**
 * 可转换变量的
 * (可被FancyMessagePart的子类实现)
 */
public interface Convertable {
    /**
     * 格式转换<br>
     * 把{0},{1}...这样的替换符转换成对应的变量,注意顺序<br>
     * 为了效率,变量数量应当尽量少
     * @param replace 变量列表,可为空
     */
    void convert(Object... replace);

    /**
     * (单个)格式转换
     * @param from 名(注意名称中不包含{})
     * @param to 值
     */
    void convert(String from, Object to);

    /**
     * 格式转换<br>
     * 把{名称}这样的替换符转换成相应的变量<br>
     * 为了效率,变量数量应当尽量少
     * @param replace 名称-值映射表,可为null(注意名称中不包含{},值为null时会用""代替)
     */
    void convert(Map<String, Object> replace);
}
