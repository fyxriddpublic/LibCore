package com.fyxridd.lib.core.manager.realname;

import org.apache.ibatis.annotations.Param;

public interface RealNameMapper {
    /**
     * @return 不存在返回null
     */
    RealName select(@Param("lowerName") String lowerName);

    void insert(@Param("realName") RealName realName);
}
