package net.myriantics.lootshare.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.myriantics.lootshare.tag.LootShareEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class LootShareEntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
    public LootShareEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(LootShareEntityTypeTags.LOOTSHARE_ALLOWLIST)
                .forceAddTag(ConventionalEntityTypeTags.BOSSES)
                .add(EntityType.WARDEN)
                .add(EntityType.ELDER_GUARDIAN);
    }
}
