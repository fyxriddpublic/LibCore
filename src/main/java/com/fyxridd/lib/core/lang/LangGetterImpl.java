package com.fyxridd.lib.core.lang;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.lang.LangGetter;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangGetterImpl implements LangGetter{
    private Map<String, Map<Integer, FancyMessage>> langs = new HashMap<>();

    public LangGetterImpl(String plugin, ConfigurationSection config) throws Exception {
        Map<String, String> result = new HashMap<>();
        Map<String, Object> map = config.getValues(true);
        for (Map.Entry<String, Object> entry:map.entrySet()) result.put(entry.getKey(), (String) entry.getValue());
        loadLangs(plugin, result);
    }

    @Override
    public FancyMessage get(String player, int id, Object... args) {
        return getLang(CorePlugin.instance.getPlayerManager().getLang(player), id, args);
    }

    @Override
    public FancyMessage get(int id, Object... args) {
        return getLang(CorePlugin.instance.getCoreConfig().getDefaultLang(), id, args);
    }

    /**
     * @param lang null表示使用默认语言
     * @param id 语言ID
     * @param args 变量
     * @return 可为null
     */
    @Override
    public FancyMessage getLang(String lang, int id, Object... args) {
        FancyMessage result = null;

        //指定
        if (lang != null) {
            Map<Integer, FancyMessage> map = langs.get(lang);
            if (map != null) result = map.get(id);
        }

        //默认
        if (result == null) {
            String defaultLang = CorePlugin.instance.getCoreConfig().getDefaultLang();
            if (defaultLang.equals(lang)) return null;//要读取的语言就是默认的语言
            Map<Integer, FancyMessage> map = langs.get(defaultLang);
            if (map != null) result = map.get(id);
        }

        //转换变量
        if (result != null) LangManager.convertArgs(result, args);

        //返回
        return result;
    }

    /**
     * 读取所有的语言配置
     * @param map '语言名 语言文件(相对路径,相对于此插件的配置文件目录)'
     */
    private void loadLangs(String plugin, Map<String, String> map) throws Exception {
        File parentFolder = new File(CoreApi.pluginPath, plugin);
        for (Map.Entry<String, String> entry:map.entrySet()) {
            String lang = entry.getKey();
            String langFilePath = entry.getValue();
            File langFile = new File(parentFolder, langFilePath);
            loadLang(lang, langFile);
        }
    }

    private void loadLang(String lang, File langFile) throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(langFile);
        loadLang(lang, config);
    }

    private void loadLang(String lang, ConfigurationSection config) throws Exception {
        //hash
        HashMap<Integer, FancyMessage> hash = new HashMap<>();
        //读取语言
        for (String key:config.getValues(false).keySet()) {
            String[] ss = key.split("\\-");
            if (ss.length == 2 && ss[0].equalsIgnoreCase("show")) {
                int id = Integer.parseInt(ss[1]);
                String msg = UtilApi.convert(config.getString(key));
                hash.put(id, LangManager.load(msg, (ConfigurationSection) config.get("info-" + id)));
            }
        }
        //添加
        langs.put(lang, hash);
    }
}
