package com.awa.kissmod;

import java.io.*;
import java.util.Properties;

public class KissModConfig {
    private static final String CONFIG_FILE = "config/kissmod.properties";
    private static final String RIGHT_CLICK_KEY = "rightClickEnabled";

    public static boolean loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return true; // 默认值
        }

        try (InputStream input = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            prop.load(input);
            return Boolean.parseBoolean(prop.getProperty(RIGHT_CLICK_KEY, "true"));
        } catch (IOException e) {
            return true;
        }
    }

    public static void saveConfig(boolean state) {
        File configDir = new File("config");
        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            if (!created) {
                KissMod.LOGGER.error("无法创建配置目录 `{}`", configDir.getAbsolutePath());
            }
        }
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.setProperty(RIGHT_CLICK_KEY, String.valueOf(state));
            prop.store(output, "KissMod Configuration");
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }

}