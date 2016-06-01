package com.fyxridd.lib.core.lang;

public interface LangUserMapper {
    boolean exist(String name);

    /**
     * @return 不存在返回null
     */
    LangUser select(String name);

    void insert(LangUser langUser);

    void update(LangUser langUser);
}
