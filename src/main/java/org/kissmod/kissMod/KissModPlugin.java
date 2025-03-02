package org.kissmod.kissMod;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kissmod.kissMod.packet.KissPacketManager;

public final class KissModPlugin extends JavaPlugin {

    public static final String MOD_ID = "kiss-mod";
    @Getter
    @Setter
    private static KissModPlugin instance;

    public static String id(String path) {
        return "%s:%s".formatted(MOD_ID, path);
    }

    @Override
    public void onEnable() {
        KissModPlugin.setInstance(this);
        KissPacketManager.register();
    }


    @Override
    public void onDisable() {
    }
}
