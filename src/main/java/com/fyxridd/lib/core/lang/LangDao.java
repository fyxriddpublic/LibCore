package com.fyxridd.lib.core.lang;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.SqlApi;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.util.Collection;

public class LangDao {
    private SqlSessionFactory sessionFactory;

    public LangDao() {
        //注册映射文件
        SqlApi.registerMapperXml(new File(CorePlugin.instance.dataPath, "LangUserMapper.xml"));
        //获取会话工厂
        sessionFactory = SqlApi.getSqlSessionFactory();
    }

    /**
     * @return 可能为null
     */
    public LangUser getLangUser(String name) {
        SqlSession session = sessionFactory.openSession();
        try {
            return session.getMapper(LangUserMapper.class).select(name);
        } finally {
            session.close();
        }
    }

    public void saveOrUpdates(Collection<LangUser> c) {
        if (c == null || c.isEmpty()) return;

        SqlSession session = sessionFactory.openSession();
        try {
            LangUserMapper mapper = session.getMapper(LangUserMapper.class);
            for (LangUser user:c) {
                if (mapper.exist(user.getName())) mapper.update(user);
                else mapper.insert(user);
            }
        } finally {
            session.commit();
            session.close();
        }
    }
}
