package com.fyxridd.lib.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.config.Setter;
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
    private ProtocolManager protocolManager;

    private GenerateManager generateManager;
    private ConfigManager configManager;
    private LimitManager limitManager;
    private PipeManager pipeManager;
    private LangManager langManager;

    private CoreConfig coreConfig;

    private MessageManager messageManager;
    private LogManager logManager;
    private SqlManager sqlManager;
    private PlayerManager playerManager;
    private RealNameManager realNameManager;
    private SyncChatManager syncChatManager;
    private EnterBlockTypeManager enterBlockTypeManager;
    private RealDamageManager realDamageManager;
    private TimeManager timeManager;
    private PerManager perManager;
    private EcoManager ecoManager;
    private VerManager verManager;

    @Override
    public void onLoad() {
        instance = this;
        CoreApi.serverPath = System.getProperty("user.dir");
        CoreApi.pluginPath = getFile().getParentFile().getAbsolutePath();
        CoreApi.serverVer = CoreApi.getMcVersion(Bukkit.getServer());

        protocolManager = ProtocolLibrary.getProtocolManager();
        generateManager = new GenerateManager();
        logManager = new LogManager();

        super.onLoad();
    }

    //启动插件
    @Override
    public void onEnable() {
        messageManager = new MessageManager();
        configManager = new ConfigManager();
        limitManager = new LimitManager();
        pipeManager = new PipeManager();
        langManager = new LangManager();
        
        //注册配置对象
        ConfigApi.register(pn, CoreConfig.class);
        //添加配置监听
        ConfigApi.addListener(pn, CoreConfig.class, new Setter<CoreConfig>() {
            @Override
            public void set(CoreConfig value) {
                coreConfig = value;
            }
        });

        //初始化
        sqlManager = new SqlManager();
        playerManager = new PlayerManager();
        realNameManager = new RealNameManager();
        syncChatManager = new SyncChatManager();
        enterBlockTypeManager = new EnterBlockTypeManager();
        realDamageManager = new RealDamageManager();
        timeManager = new TimeManager();
        perManager = new PerManager();
        ecoManager = new EcoManager();
        verManager = new VerManager();

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
            //(默认使用/stop命令停止服务器有个问题,就是玩家退出不会触发PlayerQuitEvent)
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

    public MessageManager getMessageManager() {
        return messageManager;
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

    public PerManager getPerManager() {
        return perManager;
    }

    public EcoManager getEcoManager() {
        return ecoManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public VerManager getVerManager() {
        return verManager;
    }

    private static FancyMessage get(String player, int id, Object... args) {
        return CorePlugin.instance.getCoreConfig().getLang().get(player, id, args);
    }
}
