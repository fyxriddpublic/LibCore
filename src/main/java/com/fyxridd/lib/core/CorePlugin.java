package com.fyxridd.lib.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.event.ServerCloseEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.config.ConfigManager;
import com.fyxridd.lib.core.config.GenerateManager;
import com.fyxridd.lib.core.config.LimitManager;
import com.fyxridd.lib.core.config.PipeManager;
import com.fyxridd.lib.core.lang.LangManager;
import com.fyxridd.lib.core.lang.PlayerManager;
import com.fyxridd.lib.core.manager.*;
import com.fyxridd.lib.core.realname.RealNameManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CorePlugin extends SimplePlugin{
    public static CorePlugin instance;
    public static boolean libChatShowHook;
    private ProtocolManager protocolManager;

    private GenerateManager generateManager;
    private ConfigManager configManager;
    private LimitManager limitManager;
    private PipeManager pipeManager;
    private CoreConfig coreConfig;

    private SqlManager sqlManager;
    private LogManager logManager;
    private PlayerManager playerManager;
    private LangManager langManager;
    private RealNameManager realNameManager;
    private SyncChatManager syncChatManager;
    private EnterBlockTypeManager enterBlockTypeManager;
    private RealDamageManager realDamageManager;
    private TimeManager timeManager;

    @Override
    public void onLoad() {
        CoreApi.serverPath = System.getProperty("user.dir");
        CoreApi.pluginPath = getFile().getParentFile().getAbsolutePath();
        CoreApi.serverVer = CoreApi.getMcVersion(Bukkit.getServer());

        protocolManager = ProtocolLibrary.getProtocolManager();

        super.onLoad();
    }

    //启动插件
    @Override
    public void onEnable() {
        instance = this;
        try {
            Class.forName("com.fyxridd.lib.show.chat.ShowPlugin");
            libChatShowHook = true;
        }catch (Exception e) {
            libChatShowHook = false;
        }

        generateManager = new GenerateManager();
        configManager = new ConfigManager();
        limitManager = new LimitManager();
        pipeManager = new PipeManager();
        
        //注册配置对象
        ConfigApi.register(pn, CoreConfig.class);
        //添加配置监听
        ConfigApi.addListener(pn, CoreConfig.class, new ConfigManager.Setter<CoreConfig>() {
            @Override
            public void set(CoreConfig value) {
                coreConfig = value;
            }
        });

        //初始化
        sqlManager = new SqlManager();
        logManager = new LogManager();
        playerManager = new PlayerManager();
        langManager = new LangManager();
        realNameManager = new RealNameManager();
        syncChatManager = new SyncChatManager();
        enterBlockTypeManager = new EnterBlockTypeManager();
        realDamageManager = new RealDamageManager();
        timeManager = new TimeManager();

        super.onEnable();
    }

    //停止插件
    @Override
    public void onDisable() {
        //计时器
        Bukkit.getScheduler().cancelAllTasks();

        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stop")) {
            if (sender instanceof Player && !sender.isOp()) return true;
            //先T人
            for (Player p:Bukkit.getOnlinePlayers()) p.kickPlayer(get(p.getName(), 70).getText());
            //发出关服事件
            Bukkit.getPluginManager().callEvent(new ServerCloseEvent());
            //再关服
            Bukkit.shutdown();
        }
        return true;
    }

    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public LimitManager getLimitManager() {
        return limitManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public PipeManager getPipeManager() {
        return pipeManager;
    }

    public GenerateManager getGenerateManager() {
        return generateManager;
    }

    public CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public RealNameManager getRealNameManager() {
        return realNameManager;
    }

    public SyncChatManager getSyncChatManager() {
        return syncChatManager;
    }

    public EnterBlockTypeManager getEnterBlockTypeManager() {
        return enterBlockTypeManager;
    }

    public RealDamageManager getRealDamageManager() {
        return realDamageManager;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    private static FancyMessage get(String player, int id, Object... args) {
        return CorePlugin.instance.getCoreConfig().getLang().get(player, id, args);
    }
}
