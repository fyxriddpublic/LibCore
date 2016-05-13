package com.fyxridd.lib.core;

import com.fyxridd.lib.core.api.CoreApi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CorePlugin extends JavaPlugin{
    public static CorePlugin instance;

    //插件名
    public static String pn;
    //插件jar文件
    public static File file;
    //插件数据文件夹路径
    public static String dataPath;
    //插件版本
    public static String ver;

    private CoreMain coreMain;

    @Override
    public void onLoad() {
        CoreApi.serverPath = System.getProperty("user.dir");
        CoreApi.pluginPath = getFile().getParentFile().getAbsolutePath();
        CoreApi.serverVer = CoreApi.getMcVersion(Bukkit.getServer());

        instance = this;
        pn = getName();
        file = getFile();
        dataPath = CoreApi.pluginPath + File.separator+pn;
        ver = CoreApi.getPluginVersion(getFile());

        //生成文件
        ConfigApi.generateFiles(getFile(), pn);
    }

    //启动插件
    @Override
    public void onEnable() {
        coreMain = new CoreMain();

        //成功启动
        CoreApi.sendConsoleMessage(FormatApi.get(pn, 25, pn, ver).getText());
    }

    //停止插件
    @Override
    public void onDisable() {
        //Info
        CoreMain.info.onDisable();
        //Eco
        CoreMain.ecoManager.onDisable();
        //Per
        CoreMain.perManager.onDisable();
        //ConfigManager
        ConfigManager.onDisable();
        //计时器
        Bukkit.getScheduler().cancelAllTasks();
        //显示插件成功停止信息
        CoreApi.sendConsoleMessage(FormatApi.get(pn, 30, pn, ver).getText());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stop")) {
            if (sender instanceof Player && !sender.isOp()) return true;
            //先T人
            for (Player p:Bukkit.getOnlinePlayers()) p.kickPlayer(get(70).getText());
            //发出关服事件
            Bukkit.getPluginManager().callEvent(new ServerCloseEvent());
            //再关服
            Bukkit.shutdown();
        }
        return true;
    }

    public CoreMain getCoreMain() {
        return coreMain;
    }

    private static FancyMessage get(int id, Object... args) {
        return FormatApi.get(CorePlugin.pn, id, args);
    }
}
