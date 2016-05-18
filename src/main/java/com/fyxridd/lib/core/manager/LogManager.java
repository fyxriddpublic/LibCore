package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.log.Level;

import java.util.HashMap;
import java.util.Map;

public class LogManager {
    /**
     * 日志类型信息上下文
     */
    private class LogContext {
        private String type;

        public LogContext(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    //日志类型 日志类型上下文
    private Map<String, LogContext> types = new HashMap<>();

    /**
     * @see com.fyxridd.lib.core.api.log.LogApi#register(String)
     */
    public void register(String type) {
        types.put(type, new LogContext(type));
    }

    /**
     * @see com.fyxridd.lib.core.api.log.LogApi#log(String, Level, Object)
     */
    public void log(String type, Level level, Object msg) {
        //todo 暂时简单地记录到控制台
        if (level == Level.INFO) CoreApi.info(msg);
        else if (level == Level.WARN) CoreApi.warn(msg);
        else if (level == Level.SEVERE) CoreApi.severe(msg);
    }
}
