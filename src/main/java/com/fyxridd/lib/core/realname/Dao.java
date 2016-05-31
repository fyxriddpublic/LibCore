package com.fyxridd.lib.core.realname;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.SqlApi;

import com.fyxridd.lib.core.api.exception.NotReadyException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bukkit.Bukkit;

import java.io.File;

public class Dao {
    private SqlSessionFactory sessionFactory;

    public Dao() {
        //注册映射文件
        SqlApi.registerMapperXml(new File(CorePlugin.instance.dataPath, "RealNameMapper.xml"));
        //计时器
        Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.instance, new Runnable() {
            @Override
            public void run() {
                sessionFactory = SqlApi.getSqlSessionFactory();
            }
        });
    }

    public boolean isReady() {
        return sessionFactory != null;
    }

    /**
     * @return 可能为null
     */
    public RealName getRealName(String lowerName) throws NotReadyException {
        //服务还没准备好
        if (!isReady()) throw new NotReadyException();

        SqlSession session = sessionFactory.openSession();
        try {
            return session.getMapper(RealNameMapper.class).select(lowerName);
        } finally {
            session.close();
        }
    }

    public void insert(RealName realName) throws NotReadyException {
        //服务还没准备好
        if (!isReady()) throw new NotReadyException();

        SqlSession session = sessionFactory.openSession();
        try {
            session.getMapper(RealNameMapper.class).insert(realName);
        } finally {
            session.commit();
            session.close();
        }
    }
}
