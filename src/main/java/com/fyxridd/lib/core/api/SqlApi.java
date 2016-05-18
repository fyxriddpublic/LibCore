package com.fyxridd.lib.core.api;

import org.apache.ibatis.session.SqlSessionFactory;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.manager.SqlManager;

import java.io.File;

public class SqlApi {
    /**
     * 动态注册mapper的xml文件到mybatis
     * @param file mapper文件
     */
    public static void registerMapperXml(File file) {
        SqlManager.registerMapperXml(getSqlSessionFactory(), file);
    }

    /**
     * 获取会话工厂
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return CorePlugin.instance.getSqlManager().getSqlSessionFactory();
    }
}
