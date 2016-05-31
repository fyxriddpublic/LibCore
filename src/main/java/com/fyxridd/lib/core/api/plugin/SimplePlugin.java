package com.fyxridd.lib.core.api.plugin;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.config.GenerateApi;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SimplePlugin extends JavaPlugin implements Listener{
    //插件名
    public String pn;
    //插件jar文件
    public File file;
    //插件数据文件夹路径
    public String dataPath;
    //插件版本
    public String ver;

    @Override
    public void onLoad() {
        pn = getName();
        file = getFile();
        dataPath = CoreApi.pluginPath + File.separator + pn;
        ver = CoreApi.getPluginVersion(file);

        //注册文件生成
        GenerateApi.registerDirToDir(pn, file, dataPath, "resources", "");
        //生成文件
        GenerateApi.generate(pn, false);
    }

    @Override
    public void onEnable() {
        //显示插件成功启动信息
        CoreApi.sendConsoleMessage(CorePlugin.instance.getCoreConfig().getLang().get(25, pn, ver).getText());
    }

    @Override
    public void onDisable() {
        //显示插件成功停止信息
        CoreApi.sendConsoleMessage(CorePlugin.instance.getCoreConfig().getLang().get(30, pn, ver).getText());
    }
}
