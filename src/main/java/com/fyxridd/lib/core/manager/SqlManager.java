package com.fyxridd.lib.core.manager;

import java.io.*;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.fyxridd.lib.core.CorePlugin;

public class SqlManager {
    private final String MYBATIS_CONFIG_FILE = "sql/mybatis-config.xml";
    private SqlSessionFactory sqlSessionFactory;

    public SqlManager() {
        File configFile = new File(CorePlugin.instance.dataPath, MYBATIS_CONFIG_FILE);
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @see com.fyxridd.lib.sql.api.SqlApi#registerMapperXml(File)
     */
    public static void registerMapperXml(SqlSessionFactory sqlSessionFactory, File file) {
        if (file.exists()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            try {
                new XMLMapperBuilder(new FileInputStream(file), configuration, file.toURI().toString(), configuration.getSqlFragments()).parse();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
