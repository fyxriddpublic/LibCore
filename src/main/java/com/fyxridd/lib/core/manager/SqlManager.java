package com.fyxridd.lib.core.manager;

import java.io.*;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.fyxridd.lib.core.CorePlugin;

public class SqlManager {
    private final String MYBATIS_CONFIG_FILE = "sql/mybatis-config.xml";
    private final String MYBATIS_PROPERTIES_FILE = "sql/mybatis-config.properties";
    private SqlSessionFactory sqlSessionFactory;

    public SqlManager() {
        Properties properties = new Properties();
        {
            InputStream is = null;
            try {
                is = new FileInputStream(new File(CorePlugin.instance.dataPath, MYBATIS_PROPERTIES_FILE));
                properties.load(is);
            } catch (IOException e) {
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

        File configFile = new File(CorePlugin.instance.dataPath, MYBATIS_CONFIG_FILE);
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(is, properties);
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
     * @see com.fyxridd.lib.core.api.SqlApi#registerMapperXml(File)
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
