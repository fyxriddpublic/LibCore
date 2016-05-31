package com.fyxridd.lib.core.api.config;

import java.io.File;

import com.fyxridd.lib.core.CorePlugin;

/**
 * 生成文件Api
 */
public class GenerateApi {
    /**
     * @param pluginName 插件名(注册的插件名)
     * @param pluginFile 插件文件(源文件会从此文件内取)
     * @param dataFolder 插件数据保存目录(目标文件放置位置会以此目录为父路径)
     * @param srcFile 路径以/分隔
     * @param tarFile 路径以/分隔
     */
    public static void registerFileToFile(String pluginName, File pluginFile, String dataFolder, String srcFile, String tarFile) {
        CorePlugin.instance.getGenerateManager().registerFileToFile(pluginName, pluginFile, dataFolder, srcFile, tarFile);
    }

    /**
     * @param srcFile 路径以/分隔
     * @param tarDir 路径以/分隔
     */
    public static void registerFileToDir(String pluginName, File pluginFile, String dataFolder, String srcFile, String tarDir) {
        CorePlugin.instance.getGenerateManager().registerFileToDir(pluginName, pluginFile, dataFolder, srcFile, tarDir);
    }

    /**
     * @param srcDir 路径以/分隔
     * @param tarDir 路径以/分隔
     */
    public static void registerDirToDir(String pluginName, File pluginFile, String dataFolder, String srcDir, String tarDir) {
        CorePlugin.instance.getGenerateManager().registerDirToDir(pluginName, pluginFile, dataFolder, srcDir, tarDir);
    }

    /**
     * 生成文件
     * @param override 是否覆盖
     */
    public static void generate(String plugin, boolean override) {
        CorePlugin.instance.getGenerateManager().generate(plugin, override);
    }
}
