package com.awa.kissmod.client;

import com.awa.kissmod.KissModConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class KissModCommandRegistration {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(literal("kissmod-rightclick")
                .executes(context -> {
                    KissModConfig.rightClickEnabled = !KissModConfig.rightClickEnabled;
                    KissModConfig.saveConfig();
                    String translationKey = KissModConfig.rightClickEnabled ? "kiss-mod.rightclick.enabled" : "kiss-mod.rightclick.disabled";
                    context.getSource().sendSuccess(() -> Component.translatable(translationKey), false);
                    return 1;
                })
                .then(argument("state", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean state = BoolArgumentType.getBool(context, "state");
                            KissModConfig.rightClickEnabled = state;
                            KissModConfig.saveConfig();
                            String translationKey = state ? "kiss-mod.rightclick.enabled" : "kiss-mod.rightclick.disabled";
                            context.getSource().sendSuccess(() -> Component.translatable(translationKey), false);
                            return 1;
                        })
                )
        );

        dispatcher.register(literal("kissmod-proxlib")
                .executes(context -> {
                    KissModConfig.proxLibEnabled = !KissModConfig.proxLibEnabled;
                    KissModConfig.saveConfig();
                    String translationKey = KissModConfig.proxLibEnabled ? "kiss-mod.proxlib.enabled" : "kiss-mod.proxlib.disabled";
                    context.getSource().sendSuccess(() -> Component.translatable(translationKey), false);
                    return 1;
                })
                .then(argument("state", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean state = BoolArgumentType.getBool(context, "state");
                            KissModConfig.proxLibEnabled = state;
                            KissModConfig.saveConfig();
                            String translationKey = state ? "kiss-mod.proxlib.enabled" : "kiss-mod.proxlib.disabled";
                            context.getSource().sendSuccess(() -> Component.translatable(translationKey), false);
                            return 1;
                        })
                )
        );
    }
}
