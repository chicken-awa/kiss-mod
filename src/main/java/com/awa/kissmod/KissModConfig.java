package com.awa.kissmod;

import net.minecraft.client.gui.screen.Screen;

import java.io.*;
import java.util.Properties;

public class KissModConfig {
    private static final String CONFIG_FILE = "config/kissmod.properties";
    private static final String RIGHT_CLICK_KEY = "rightClickEnabled";

    public static boolean loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return true;
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
            updateClientConfig(state);
        } catch (IOException e) {
            KissMod.LOGGER.error("保存配置文件失败", e);
        }
    }
    private static void updateClientConfig(boolean state) {
        try {
            Class<?> clientClass = Class.forName("com.awa.kissmod.client.KissModClient");
            java.lang.reflect.Field field = clientClass.getDeclaredField("rightClickEnabled");
            field.setAccessible(true);
            field.setBoolean(null, state);
        } catch (Exception e) {
            KissMod.LOGGER.warn("无法更新配置: {}", e.getMessage());
        }
    }
    public static Screen getScreen(Screen parent) {
        try {
            Class<?> configBuilderClass = Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            Object builder = configBuilderClass.getMethod("create")
                    .invoke(null);

            builder.getClass().getMethod("setParentScreen", Screen.class)
                    .invoke(builder, parent);
            builder.getClass().getMethod("setTitle", net.minecraft.text.Text.class)
                    .invoke(builder, net.minecraft.text.Text.translatable("title.kissmod.config"));

            builder.getClass().getMethod("setSavingRunnable", Runnable.class)
                    .invoke(builder, (Runnable) () -> {});

            Object entryBuilder = builder.getClass().getMethod("entryBuilder")
                    .invoke(builder);
            Object general = builder.getClass().getMethod("getOrCreateCategory", net.minecraft.text.Text.class)
                    .invoke(builder, net.minecraft.text.Text.translatable("category.kissmod.general"));

            Object booleanToggle = entryBuilder.getClass().getMethod("startBooleanToggle", net.minecraft.text.Text.class, boolean.class)
                    .invoke(entryBuilder, net.minecraft.text.Text.translatable("option.kissmod.rightClickEnabled"), loadConfig());
            booleanToggle.getClass().getMethod("setDefaultValue", boolean.class)
                    .invoke(booleanToggle, true);
            booleanToggle.getClass().getMethod("setTooltip", net.minecraft.text.Text[].class)
                    .invoke(booleanToggle, (Object) new net.minecraft.text.Text[]{
                            net.minecraft.text.Text.translatable("tooltip.kissmod.rightClickEnabled")
                    });
            booleanToggle.getClass().getMethod("setSaveConsumer", java.util.function.Consumer.class)
                    .invoke(booleanToggle, (java.util.function.Consumer<Boolean>) KissModConfig::saveConfig);
            Object builtEntry = booleanToggle.getClass().getMethod("build")
                    .invoke(booleanToggle);
            Class<?> abstractEntryClass = Class.forName("me.shedaniel.clothconfig2.api.AbstractConfigListEntry");
            general.getClass().getMethod("addEntry", abstractEntryClass)
                    .invoke(general, builtEntry);
            return (Screen) builder.getClass().getMethod("build")
                    .invoke(builder);

        } catch (Exception e) {
            return new net.minecraft.client.gui.screen.Screen(
                    net.minecraft.text.Text.translatable("message.kissmod.config_required")
            ) {
                @Override
                protected void init() {
                    // 获取屏幕的宽度和高度
                    int screenWidth = this.width;
                    int screenHeight = this.height;

                    // 创建一个返回按钮
                    net.minecraft.client.gui.widget.ButtonWidget backButton =
                            net.minecraft.client.gui.widget.ButtonWidget.builder(
                                            net.minecraft.text.Text.translatable("gui.back"),
                                            (button) -> {
                                                // 点击按钮时，关闭当前屏幕，回到父屏幕
                                                net.minecraft.client.MinecraftClient.getInstance().setScreen(parent);
                                            }
                                    )
                                    // 居中放置按钮
                                    .dimensions(screenWidth / 2 - 100, screenHeight / 2 + 20, 200, 20)
                                    .build();

                    this.addDrawableChild(backButton);
                }

                @Override
                public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
                    // 1. 渲染背景 (必须在最前面)
                    this.renderBackground(context, mouseX, mouseY, delta);

                    // 2. 渲染所有子组件 (按钮，这些组件的渲染是清晰的)
                    super.render(context, mouseX, mouseY, delta);

                    // 3. 渲染标题文本
                    // 必须在 super.render() 之后调用，以确保它在按钮上面
                    context.drawCenteredTextWithShadow(
                            this.textRenderer,
                            this.title,
                            this.width / 2,
                            this.height / 2 - 10, // 略微向上移动
                            0xFFFFFF // 白色
                    );
                }
            };
        }
    }
}