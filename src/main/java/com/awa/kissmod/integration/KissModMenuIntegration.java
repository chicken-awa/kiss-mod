package com.awa.kissmod.integration;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.awa.kissmod.KissModConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

public class KissModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            return KissModConfig::getScreen;
        } catch (NoClassDefFoundError | Exception e) {
            return null;
        }
    }
}
