package com.fyxridd.lib.core.realname;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.SqlApi;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;

public class RealNameDao {
    private SqlSessionFactory sessionFactory;

    public RealNameDao() {
        //注册映射文件
        SqlApi.registerMapperXml(new File(CorePlugin.instance.dataPath, "RealNameMapper.xml"));
        //计时器
        sessionFactory = SqlApi.getSqlSessionFactory();
    }

    /**
     * @return 可能为null
     */
    public RealName getRealName(String lowerName) {
        SqlSession session = sessionFactory.openSession();
        try {
            return session.getMapper(RealNameMapper.class).select(lowerName);
        } finally {
            session.close();
        }
    }

    public void insert(RealName realName) {
        SqlSession session = sessionFactory.openSession();
        try {
            session.getMapper(RealNameMapper.class).insert(realName);
        } finally {
            session.commit();
            session.close();
        }
    }
}
