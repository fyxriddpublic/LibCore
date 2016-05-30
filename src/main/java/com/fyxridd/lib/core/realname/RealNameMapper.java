package com.fyxridd.lib.core.realname;

public interface RealNameMapper {
    /**
     * @return 不存在返回null
     */
    RealName select(String lowerName);

    void insert(RealName realName);
}
