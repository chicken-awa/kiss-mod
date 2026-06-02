package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

//? if >=1.21.1 {
/*@EventBusSubscriber(modid = KissMod.MOD_ID, value = Dist.CLIENT)
*///? } else {
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = KissMod.MOD_ID, value = Dist.CLIENT)
//? }
public class KissModClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(KissModNetworkHandler.class);
        NeoForge.EVENT_BUS.register(KissModClient.class);
        NeoForge.EVENT_BUS.register(KissModCommandRegistration.class);
        KissModClient.init();
        ModList.get().getModContainerById(KissMod.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (modContainer, parent) -> KissModConfig.getScreen(parent))
        );
    }
}
