package com.fyxridd.lib.core.config;

import com.fyxridd.lib.core.api.UtilApi;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 生成文件管理
 */
public class GenerateManager implements Listener {
    /**
     * 生成上下文
     */
    private abstract class GenerateContext {
        protected String plugin;
        protected File pluginFile;
        protected String dataFolder;

        public GenerateContext(String plugin, File pluginFile, String dataFolder) {
            this.plugin = plugin;
            this.pluginFile = pluginFile;
            this.dataFolder = dataFolder;
        }

        abstract void generate(boolean override);

        public String getPlugin() {
            return plugin;
        }

        public File getPluginFile() {
            return pluginFile;
        }

        public String getDataFolder() {
            return dataFolder;
        }
    }

    private class FileToFileGenerateContext extends GenerateContext{
        //路径用/分隔
        private String srcFile;
        private String tarFile;

        public FileToFileGenerateContext(String plugin, File pluginFile, String dataFolder, String srcFile, String tarFile) {
            super(plugin, pluginFile, dataFolder);
            this.srcFile = srcFile;
            this.tarFile = tarFile;
        }

        @Override
        public void generate(boolean override) {
            JarInputStream jis = null;
            FileOutputStream fos = null;
            try {
                jis = new JarInputStream(new FileInputStream(pluginFile));
                JarEntry entry;
                byte[] buff = new byte[1024];
                int read;
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (entry.getName().equals(srcFile)) {
                        File file = new File(dataFolder, tarFile);
                        File parent = file.getParentFile();
                        if (parent != null) parent.mkdirs();
                        if (override || !file.exists()) {
                            file.createNewFile();
                            fos = new FileOutputStream(file);
                            while((read = jis.read(buff)) > 0) fos.write(buff, 0, read);
                            fos.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (jis != null) jis.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                }
            }
        }

        public String getSrcFile() {
            return srcFile;
        }

        public String getTarFile() {
            return tarFile;
        }
    }

    private class FileToDirGenerateContext extends GenerateContext{
        private String srcFile;
        //最后带'/'
        private String tarDir;

        public FileToDirGenerateContext(String plugin, File pluginFile, String dataFolder, String srcFile, String tarDir) {
            super(plugin, pluginFile, dataFolder);
            this.srcFile = srcFile;
            this.tarDir = tarDir;
            if (this.tarDir.isEmpty() || this.tarDir.indexOf(this.tarDir.length()-1) != '/') this.tarDir += "/";
        }

        @Override
        public void generate(boolean override) {
            JarInputStream jis = null;
            FileOutputStream fos = null;
            try {
                jis = new JarInputStream(new FileInputStream(pluginFile));
                JarEntry entry;
                byte[] buff = new byte[1024];
                int read;
                File dir = new File(dataFolder, tarDir);
                dir.mkdirs();
                if (dir.exists() && dir.isDirectory()) {
                    while ((entry = jis.getNextJarEntry()) != null) {
                        if (entry.getName().equals(srcFile)) {
                            File outFile = new File(dir, UtilApi.getFileNameWithSuffix(entry.getName()));
                            if (outFile.exists()) {
                                if (!override) break;
                            } else outFile.createNewFile();
                            fos = new FileOutputStream(outFile);
                            while ((read = jis.read(buff)) > 0) fos.write(buff, 0, read);
                            fos.close();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (jis != null) jis.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                }
            }
        }

        public String getSrcFile() {
            return srcFile;
        }

        public String getTarDir() {
            return tarDir;
        }
    }

    private class DirToDirGenerateContext extends GenerateContext{
        //最后带'/'
        private String srcDir;
        //最后带'/'
        private String tarDir;

        public DirToDirGenerateContext(String plugin, File pluginFile, String dataFolder, String srcDir, String tarDir) {
            super(plugin, pluginFile, dataFolder);
            this.srcDir = srcDir;
            if (this.srcDir.isEmpty() || this.srcDir.indexOf(this.srcDir.length()-1) != '/') this.srcDir += "/";
            this.tarDir = tarDir;
            if (this.tarDir.isEmpty() || this.tarDir.indexOf(this.tarDir.length()-1) != '/') this.tarDir += "/";
        }

        @Override
        public void generate(boolean override) {
            JarInputStream jis = null;
            FileOutputStream fos = null;
            try {
                jis = new JarInputStream(new FileInputStream(pluginFile));
                JarEntry entry;
                byte[] buff = new byte[1024];
                int read;
                File dir = new File(dataFolder, tarDir);
                dir.mkdirs();
                if (dir.exists() && dir.isDirectory()) {
                    while ((entry = jis.getNextJarEntry()) != null) {
                        if (entry.getName().startsWith(srcDir)) {
                            String suffix = entry.getName().substring(srcDir.length());
                            if (entry.isDirectory()) {
                                new File(dir, suffix).mkdirs();
                            }else {
                                File outFile = new File(dir, suffix);
                                if (outFile.exists()) {
                                    if (!override) continue;
                                }else outFile.createNewFile();
                                fos = new FileOutputStream(outFile);
                                while((read = jis.read(buff)) > 0) fos.write(buff, 0, read);
                                fos.close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (jis != null) jis.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                }
            }
        }

        public String getSrcDir() {
            return srcDir;
        }

        public String getTarDir() {
            return tarDir;
        }
    }

    private Map<String, List<GenerateContext>> generates = new HashMap<>();

    /**
     * @see com.fyxridd.lib.core.api.config.GenerateApi#registerFileToFile(String, File, String, String, String)
     */
    public void registerFileToFile(String pluginName, File pluginFile, String dataFolder, String srcFile, String tarFile) {
        register(new FileToFileGenerateContext(pluginName, pluginFile, dataFolder, srcFile, tarFile));
    }

    /**
     * @see com.fyxridd.lib.core.api.config.GenerateApi#registerFileToDir(String, File, String, String, String)
     */
    public void registerFileToDir(String pluginName, File pluginFile, String dataFolder, String srcFile, String tarDir) {
        register(new FileToDirGenerateContext(pluginName, pluginFile, dataFolder, srcFile, tarDir));
    }

    /**
     * @see com.fyxridd.lib.core.api.config.GenerateApi#registerDirToDir(String, File, String, String, String)
     */
    public void registerDirToDir(String pluginName, File pluginFile, String dataFolder, String srcDir, String tarDir) {
        register(new DirToDirGenerateContext(pluginName, pluginFile, dataFolder, srcDir, tarDir));
    }

    /**
     * 生成文件
     * @param override 是否覆盖
     */
    public void generate(String plugin, boolean override) {
        List<GenerateContext> list = generates.get(plugin);
        if (list != null) {//有注册
            for (GenerateContext generateContext:list) generateContext.generate(override);
        }
    }

    private void register(GenerateContext generateContext) {
        List<GenerateContext> list = generates.get(generateContext.getPlugin());
        if (list == null) {
            list = new ArrayList<>();
            generates.put(generateContext.getPlugin(), list);
        }
        list.add(generateContext);
    }
}
