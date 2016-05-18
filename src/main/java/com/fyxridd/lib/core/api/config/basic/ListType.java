package com.fyxridd.lib.core.api.config.basic;

import org.bukkit.configuration.Configuration;

import java.util.List;

public enum ListType {
    Boolean,
    Byte,
    Short,
    Integer,
    Long,
    Float,
    Double,
    Character,
    String,
    Object
    ;

    /**
     * 获取类型对应的列表
     */
    public List getList(Configuration config, String path) {
        if (this == ListType.Boolean) return config.getBooleanList(path);
        else if (this == ListType.Byte) return config.getByteList(path);
        else if (this == ListType.Short) return config.getShortList(path);
        else if (this == ListType.Integer) return config.getIntegerList(path);
        else if (this == ListType.Long) return config.getLongList(path);
        else if (this == ListType.Float) return config.getFloatList(path);
        else if (this == ListType.Double) return config.getDoubleList(path);
        else if (this == ListType.Character) return config.getCharacterList(path);
        else if (this == ListType.String) return config.getStringList(path);
        else return config.getList(path);//其它只有Object
    }
}
