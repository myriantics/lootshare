package net.myriantics.lootshare;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import net.myriantics.lootshare.registry.LootShareGameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootShareCommon implements ModInitializer {
	public static final String MOD_ID = "lootshare";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        LootShareGameRules.init();

		LOGGER.info("LootShare has initialized!");
	}

    public static ResourceLocation locate(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static String locateAlt(String name) {
        return MOD_ID + "." + name;
    }
}