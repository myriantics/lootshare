package net.myriantics.lootshare.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.lootshare.registry.LootShareGameRules;
import net.myriantics.lootshare.util.LivingEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private Level level;

    @Inject(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V")
    )
    private void klaxon$restrictItemPickup(
            ItemStack itemStack,
            float f,
            CallbackInfoReturnable<ItemEntity> cir,
            @Local ItemEntity itemEntity
    ) {
        if (this instanceof LivingEntityMixinAccess access && access.lootshare$getActiveLootsharePlayer() != null && level.getGameRules().getBoolean(LootShareGameRules.RULE_PROTECT_DROPS)) {
            itemEntity.setTarget(access.lootshare$getActiveLootsharePlayer().getUUID());
        }
    }
}
