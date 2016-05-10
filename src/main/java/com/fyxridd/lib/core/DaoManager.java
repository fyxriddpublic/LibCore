package com.fyxridd.lib.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DaoManager {
    public DaoManager() {
        File configFile = new File(CorePlugin.dataPath, "mybatis-config.xml");
        InputStream is;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
    }
}
