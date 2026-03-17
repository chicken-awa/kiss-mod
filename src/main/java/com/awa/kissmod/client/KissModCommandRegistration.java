package com.awa.kissmod.client;

import com.awa.kissmod.KissModConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class KissModCommandRegistration {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("kissmod-rightclick")
                        .executes(context -> {
                            KissModConfig.rightClickEnabled = !KissModConfig.rightClickEnabled;
                            KissModConfig.saveConfig();
                            String translationKey = KissModConfig.rightClickEnabled ? "kiss-mod.rightclick.enabled" : "kiss-mod.rightclick.disabled";
                            context.getSource().sendFeedback(Text.translatable(translationKey));
                            return 1;
                        })
                        .then(argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    KissModConfig.rightClickEnabled = state;
                                    KissModConfig.saveConfig();
                                    String translationKey = state ? "kiss-mod.rightclick.enabled" : "kiss-mod.rightclick.disabled";
                                    context.getSource().sendFeedback(Text.translatable(translationKey));
                                    return 1;
                                })
                        )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("kissmod-proxlib")
                        .executes(context -> {
                            KissModConfig.proxLibEnabled = !KissModConfig.proxLibEnabled;
                            KissModConfig.saveConfig();
                            String translationKey = KissModConfig.proxLibEnabled ? "kiss-mod.proxlib.enabled" : "kiss-mod.proxlib.disabled";
                            context.getSource().sendFeedback(Text.translatable(translationKey));
                            return 1;
                        })
                        .then(argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    KissModConfig.proxLibEnabled = state;
                                    KissModConfig.saveConfig();
                                    String translationKey = state ? "kiss-mod.proxlib.enabled" : "kiss-mod.proxlib.disabled";
                                    context.getSource().sendFeedback(Text.translatable(translationKey));
                                    return 1;
                                })
                        )
                )
        );
    }
}
