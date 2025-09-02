package net.myriantics.lootshare.tag;

import net.fabricmc.fabric.mixin.registry.sync.RegistryKeysMixin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.myriantics.lootshare.LootShareCommon;

public class LootShareEntityTypeTags {
    public static final TagKey<EntityType<?>> LOOTSHARE_ALLOWLIST = createTag("lootshare_allowlist");

    public static final TagKey<EntityType<?>> LOOTSHARE_DENYLIST = createTag("lootshare_denylist");

    private static TagKey<EntityType<?>> createTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, LootShareCommon.locate(name));
    }
}
