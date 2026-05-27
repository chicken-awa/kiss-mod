package com.awa.kissmod;

import com.awa.kissmod.client.KissModConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class KissModConfig {
    private static final Path CONFIG_PATH = 
            FabricLoader.getInstance().getConfigDir().resolve("kissmod.properties");

    public static boolean rightClickEnabled = true;
    public static boolean soundEnabled = true;
    public static double soundVolume = 1.0;
    public static double soundPitch = 1.0;
    public static int particleCount = 10;
    public static boolean debugLogging = false;
    public static boolean showOwnKiss = true;
    public static boolean showOthersKiss = true;
    public static int triggerCooldown = 175;
    public static boolean proxLibEnabled = false;
    public static boolean proxLibWhitelistMode = false;
    public static List<String> proxLibServerList = new ArrayList<>();

    public static double centerOffsetX = 0.0;
    public static double centerOffsetY = 0.0;
    public static double centerOffsetZ = 0.0;
    public static double maxOffsetX = 0.5;
    public static double maxOffsetY = 0.5;
    public static double maxOffsetZ = 0.5;

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            saveConfig();
            return;
        }
        try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
            Properties prop = new Properties();
            prop.load(input);
            rightClickEnabled = Boolean.parseBoolean(prop.getProperty("rightClickEnabled", "true"));
            soundEnabled = Boolean.parseBoolean(prop.getProperty("soundEnabled", "true"));
            soundVolume = Double.parseDouble(prop.getProperty("soundVolume", "1.0"));
            soundPitch = Double.parseDouble(prop.getProperty("soundPitch", "1.0"));
            debugLogging = Boolean.parseBoolean(prop.getProperty("debugLogging", "false"));
            showOwnKiss = Boolean.parseBoolean(prop.getProperty("showOwnKiss", "true"));
            showOthersKiss = Boolean.parseBoolean(prop.getProperty("showOthersKiss", "true"));
            triggerCooldown = Integer.parseInt(prop.getProperty("triggerCooldown", "175"));
            proxLibEnabled = Boolean.parseBoolean(prop.getProperty("proxLibEnabled", "false"));
            proxLibWhitelistMode = Boolean.parseBoolean(prop.getProperty("proxLibWhitelistMode", "false"));
            String serverListStr = prop.getProperty("proxLibServerList", "");
            proxLibServerList = serverListStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(serverListStr.split(",")));
            particleCount = Integer.parseInt(prop.getProperty("particleCount", "10"));
            centerOffsetX = Double.parseDouble(prop.getProperty("centerOffsetX", "0.0"));
            centerOffsetY = Double.parseDouble(prop.getProperty("centerOffsetY", "0.0"));
            centerOffsetZ = Double.parseDouble(prop.getProperty("centerOffsetZ", "0.0"));
            maxOffsetX = Double.parseDouble(prop.getProperty("maxOffsetX", "0.5"));
            maxOffsetY = Double.parseDouble(prop.getProperty("maxOffsetY", "0.5"));
            maxOffsetZ = Double.parseDouble(prop.getProperty("maxOffsetZ", "0.5"));
        } catch (IOException e) {
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
            getProperties().store(output, "KissMod Configuration");
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }

    @NotNull
    private static Properties getProperties() {
        Properties prop = new Properties();
        prop.setProperty("rightClickEnabled", String.valueOf(rightClickEnabled));
        prop.setProperty("soundEnabled",      String.valueOf(soundEnabled));
        prop.setProperty("soundVolume",       String.valueOf(soundVolume));
        prop.setProperty("soundPitch",        String.valueOf(soundPitch));
        prop.setProperty("particleCount",     String.valueOf(particleCount));
        prop.setProperty("debugLogging",      String.valueOf(debugLogging));
        prop.setProperty("showOwnKiss",       String.valueOf(showOwnKiss));
        prop.setProperty("showOthersKiss",    String.valueOf(showOthersKiss));
        prop.setProperty("triggerCooldown",   String.valueOf(triggerCooldown));
        prop.setProperty("proxLibEnabled",    String.valueOf(proxLibEnabled));
        prop.setProperty("proxLibWhitelistMode", String.valueOf(proxLibWhitelistMode));
        prop.setProperty("proxLibServerList", String.join(",", proxLibServerList));
        prop.setProperty("centerOffsetX",     String.valueOf(centerOffsetX));
        prop.setProperty("centerOffsetY",     String.valueOf(centerOffsetY));
        prop.setProperty("centerOffsetZ",     String.valueOf(centerOffsetZ));
        prop.setProperty("maxOffsetX",        String.valueOf(maxOffsetX));
        prop.setProperty("maxOffsetY",        String.valueOf(maxOffsetY));
        prop.setProperty("maxOffsetZ",        String.valueOf(maxOffsetZ));
        return prop;
    }

    public static Screen getScreen(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            try {
                return KissModConfigScreen.createConfigScreen(parent);
            } catch (Exception e) {
                KissMod.LOGGER.error("创建配置界面失败", e);
                return createFallbackScreen(parent);
            }
        } else {
            return createFallbackScreen(parent);
        }
    }

    private static Screen createFallbackScreen(Screen parent) {
        return new net.minecraft.client.gui.screens.Screen(
                net.minecraft.network.chat.Component.translatable("message.kissmod.config_required")
        ) {
            @Override
            protected void init() {
                net.minecraft.client.gui.components.Button backButton =
                        net.minecraft.client.gui.components.Button.builder(
                                        net.minecraft.network.chat.Component.translatable("gui.back"),
                                        (button) -> net.minecraft.client.Minecraft.getInstance().setScreen(parent)
                                )
                                .bounds(this.width / 2 - 100, this.height / 2 + 20, 200, 20)
                                .build();

                this.addRenderableWidget(backButton);
            }

            @Override
            public void render(net.minecraft.client.gui.GuiGraphics context, int mouseX, int mouseY, float delta) {
                super.render(context, mouseX, mouseY, delta);

                context.drawCenteredString(
                        this.font,
                        this.title,
                        this.width / 2,
                        this.height / 2 - 10,
                        0xFFFFFFFF
                );
            }
        };
    }
}