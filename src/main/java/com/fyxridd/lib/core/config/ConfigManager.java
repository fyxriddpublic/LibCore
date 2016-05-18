package com.fyxridd.lib.core.config;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.basic.Config;
import com.fyxridd.lib.core.api.config.basic.ListHelper;
import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.basic.Required;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert.ConfigConverter;
import com.fyxridd.lib.core.api.config.convert.ListConvert;
import com.fyxridd.lib.core.api.config.convert.PrimeConvert;
import com.fyxridd.lib.core.api.event.ReloadConfigEvent;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

/**
 * 读取配置管理
 */
public class ConfigManager {
    /**
     * 设置器
     */
    public interface Setter<T> {
        /**
         * 在值需要更新时调用(需要注意的是,可能会被额外多次调用)
         * @param t 新值
         */
        void set(T t);
    }

    /**
     * 配置上下文
     */
    private class ConfigContext<T> {
        //定义对象,运行过程中不会变动
        private String plugin;
        private Class<T> configClass;
        private Set<Setter<T>> setter = new HashSet<>();
        //配置对象,插件重载成功后会更新
        private T configInstance;

        public ConfigContext(String plugin, Class<T> configClass) {
            this.plugin = plugin;
            this.configClass = configClass;
        }

        public String getPlugin() {
            return plugin;
        }

        public Class<T> getConfigClass() {
            return configClass;
        }

        public Set<Setter<T>> getSetter() {
            return setter;
        }

        public T getConfigInstance() {
            return configInstance;
        }

        public void setConfigInstance(T configInstance) {
            this.configInstance = configInstance;
        }
    }

    public static final String LOG_TO_USER = "Config-ToUser";
    public static final String LOG_TO_PROGRAMMER = "Config-ToProgrammer";
    
    private Map<String, Map<Class<?>, ConfigContext<?>>> configs = new HashMap<>();

    public ConfigManager() {
        //注册日志上下文
        LogApi.register(LOG_TO_USER);
        LogApi.register(LOG_TO_PROGRAMMER);
        
        //监听重载配置文件事件
        Bukkit.getPluginManager().registerEvent(ReloadConfigEvent.class, CorePlugin.instance, EventPriority.HIGHEST, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                reload(((ReloadConfigEvent)event).getPlugin());
            }
        }, CorePlugin.instance);
    }

    /**
     * @see ConfigApi#register(String, Class)
     */
    public <T> void register(String plugin, Class<T> configClass) {
        Map<Class<?>, ConfigContext<?>> map = configs.get(plugin);
        if (map == null) {
            map = new HashMap<>();
            configs.put(plugin, map);
        }
        //新建
        ConfigContext context = new ConfigContext<>(plugin, configClass);
        map.put(configClass, context);
        //马上读取
        reload(context);
    }

    /**
     * @see ConfigApi#addListener(String, Class, Setter)
     */
    public void addListener(String plugin, Class configClass, Setter setter) {
        Map<Class<?>, ConfigContext<?>> map = configs.get(plugin);
        if (map != null) {
            ConfigContext<?> context = map.get(configClass);
            if (context != null) {
                //添加监听
                context.getSetter().add(setter);
                //马上设置一次
                if (context.getConfigInstance() != null) setter.set(context.getConfigInstance());
            }
        }
    }

    /**
     * @see ConfigApi#reload(String)
     */
    public void reload(String plugin) {
        Map<Class<?>, ConfigContext<?>> contexts = configs.get(plugin);
        if (contexts != null) {//有注册
            //处理开始
            for (Class<?> c:contexts.keySet()) reload(plugin, c);
        }
    }

    /**
     * @see ConfigApi#reload(String, Class)
     */
    public boolean reload(String plugin, Class<?> configClass) {
        try {
            ConfigContext<?> context = configs.get(plugin).get(configClass);
            if (context != null) return reload(context);
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @see ConfigApi#get(String, Class)
     */
    public <T> T get(String plugin, Class<T> configClass) {
        try {
            return (T) configs.get(plugin).get(configClass).getConfigInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 重载指定的配置上下文
     * @return false表示读取出错(内部会自动进行提示)
     */
    private <T> boolean reload(ConfigContext<T> context){
        try {
            //生成文件
            try {
                CorePlugin.instance.getGenerateManager().generate(context.getPlugin(), false);
            } catch (Exception e) {
                throw new Exception("generate file error!", e);
            }

            //整个类读取的配置(必须)
            Configuration classConfig;
            String classConfigFileName;
            {
                Config config = context.getConfigClass().getAnnotation(Config.class);
                if (config != null) classConfigFileName = config.value();
                else classConfigFileName = Config.DEFAULT_CONFIG_FILE_NAME;

                classConfig = loadConfig(context.getPlugin(), classConfigFileName);
            }
            //将配置进行缓存
            //每个类变量可能有各自的配置,对于相同的配置,不需要重复读取,而应该缓存来提高效率
            Map<String, Configuration> cachedConfigs = new HashMap<>();

            //整个类的Path前缀
            String pathPrefix = null;
            {
                Path path = context.getConfigClass().getAnnotation(Path.class);
                if (path != null) pathPrefix = path.value();
            }

            //构造结果对象
            T configInstance = UtilApi.newInstance(context.getConfigClass());
            if (configInstance == null) throw new Exception("new instance error!");

            //设置类变量
            Field[] fields = context.getConfigClass().getDeclaredFields();
            if (fields != null) {
                for (Field field:fields) {
                    Path path = field.getAnnotation(Path.class);
                    if (path != null) {//有Path,需要设置值
                        try {
                            //包装类
                            Class<?> c = UtilApi.wrap(field.getType());

                            //检测读取的配置
                            Configuration useConfig;
                            {
                                Config config = field.getAnnotation(Config.class);
                                if (config != null && !config.value().equals(classConfigFileName)) {
                                    useConfig = cachedConfigs.get(config.value());
                                    if (useConfig == null) {//没有缓存
                                        useConfig = loadConfig(context.getPlugin(), config.value());//读取
                                        cachedConfigs.put(config.value(), useConfig);//添加缓存
                                    }
                                }else useConfig = classConfig;
                            }

                            //路径
                            String pathStr = path.value();
                            if (pathPrefix != null && !pathPrefix.isEmpty()) pathStr = pathPrefix+"."+pathStr;

                            //Required
                            {
                                if (!useConfig.contains(pathStr)) {//无此路径
                                    Required required = field.getAnnotation(Required.class);
                                    if (required != null) throw new Exception("required!");
                                    else continue;//不用设置值
                                }
                            }

                            //Prime转换器
                            Object value = null;
                            boolean isSet = false;
                            {
                                PrimeConvert primeConvert = field.getAnnotation(PrimeConvert.class);
                                if (primeConvert != null) {//使用Prime转换器
                                    isSet = true;

                                    value = UtilApi.newInstance(primeConvert.value()).convert(context.getPlugin(), primeConvert.primeType().getValue(useConfig, pathStr));
                                }
                            }
                            //List转换器
                            {
                                if (!isSet) {
                                    ListConvert listConvert = field.getAnnotation(ListConvert.class);
                                    if (listConvert != null) {//使用List转换器
                                        isSet = true;

                                        value = UtilApi.newInstance(listConvert.value()).convert(context.getPlugin(), listConvert.listType().getList(useConfig, pathStr));
                                    }
                                }
                            }
                            //Config转换器
                            {
                                if (!isSet) {
                                    ConfigConvert configConvert = field.getAnnotation(ConfigConvert.class);
                                    if (configConvert != null) {//使用Config转换器
                                        isSet = true;

                                        ConfigConverter<?> configConverter = UtilApi.newInstance(configConvert.value());
                                        value = configConverter.convert(context.getPlugin(), (ConfigurationSection) useConfig.get(pathStr));
                                    }
                                }
                            }
                            //直接设置值
                            {
                                if (!isSet) {
                                    if (c == Boolean.class) {
                                        value = useConfig.getBoolean(pathStr);
                                    }else if (c == Byte.class) {
                                        value = (byte) useConfig.getInt(pathStr);
                                    }else if (c == Short.class) {
                                        value = (short) useConfig.getInt(pathStr);
                                    }else if (c == Integer.class) {
                                        value = useConfig.getInt(pathStr);
                                    }else if (c == Long.class) {
                                        value = useConfig.getLong(pathStr);
                                    }else if (c == Float.class) {
                                        value = (float) useConfig.getDouble(pathStr);
                                    }else if (c == Double.class) {
                                        value = useConfig.getDouble(pathStr);
                                    }else if (c == String.class) {
                                        value = useConfig.getString(pathStr);
                                    }else if (c == List.class) {
                                        ListHelper listHelper = field.getAnnotation(ListHelper.class);
                                        if (listHelper == null) throw new Exception("lack ListHelper!");
                                        value = listHelper.value().getList(useConfig, pathStr);
                                    }else throw new Exception("can't direct convert!");
                                }
                            }

                            //限制
                            Annotation[] annotations = field.getAnnotations();
                            if (annotations != null) {
                                for (Annotation annotation:annotations) CorePlugin.instance.getLimitManager().checkLimit(c, field, value, annotation);
                            }

                            //管道
                            if (annotations != null) {
                                for (Annotation annotation:annotations) value = CorePlugin.instance.getPipeManager().checkPipe(c, field, value, annotation);
                            }

                            //设置类变量的值
                            UtilApi.setField(field, configInstance, value);
                        } catch (Exception e) {
                            throw new Exception("set field '"+field.getName()+"' error: "+e.getMessage(), e);
                        }
                    }
                }
            }
            //成功
            context.setConfigInstance(configInstance);
            for (Setter setter:context.getSetter()) setter.set(configInstance);
            return true;
        } catch (Exception e) {
            //用户(服主)看的
            LogApi.log(LOG_TO_USER, Level.SEVERE, "load instance of configClass '"+context.getConfigClass().getName()+"' error: "+e.getMessage());
            //程序员看的
            LogApi.log(LOG_TO_PROGRAMMER, Level.SEVERE, UtilApi.convertException(e));
            //返回null
            return false;
        }
    }

    private Configuration loadConfig(String plugin, String fileName) throws Exception{
        Configuration config= new YamlConfiguration();
        ((YamlConfiguration)config).load(new File(new File(CoreApi.pluginPath, plugin), fileName));
        return config;
    }
}
