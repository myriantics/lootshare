package net.myriantics.lootshare.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.myriantics.lootshare.registry.LootShareGameRules;
import net.myriantics.lootshare.tag.LootShareEntityTypeTags;
import net.myriantics.lootshare.util.LivingEntityMixinAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityMixinAccess {
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Unique
    private final Map<ServerPlayer, Integer> assistingPlayers = HashMap.newHashMap(128);

    @Unique
    private ServerPlayer activeLootsharePlayer = null;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public ServerPlayer lootshare$getActiveLootsharePlayer() {
        return activeLootsharePlayer;
    }

    @Inject(
            method = "hurt",
            at = @At(value = "TAIL")
    )
    private void lootshare$addAttackingPlayers(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (lastHurtByPlayer instanceof ServerPlayer serverPlayer) {
            // add player to the map and reset their ticks to the current tick
            // basically keep hitting the boss within a minute to keep the bonus items
            assistingPlayers.put(serverPlayer, serverPlayer.server.getTickCount());
        }
    }

    @Inject(
            method = "baseTick",
            at = @At(value = "HEAD")
    )
    private void lootshare$purgeStaleAssistingPlayers(CallbackInfo ci) {
        for (ServerPlayer serverPlayer : List.copyOf(assistingPlayers.keySet())) {
            // if a player is removed or if they've exceeded the max assist window ticks, bonk them from the map
            if (serverPlayer.isRemoved() || (serverPlayer.server.getTickCount() - assistingPlayers.get(serverPlayer)) > serverPlayer.level().getGameRules().getInt(LootShareGameRules.RULE_ASSIST_WINDOW_TICKS)) {
                assistingPlayers.remove(serverPlayer);
            }
        }
    }

    @Inject(
            method = "dropAllDeathLoot",
            at = @At(value = "HEAD")
    )
    private void lootshare$computeLootSharability(
            ServerLevel serverLevel,
            DamageSource damageSource,
            CallbackInfo ci,
            @Share("lootshare.shouldLootshare") LocalBooleanRef shouldLootshare
    ) {
        GameRules gameRules = serverLevel.getGameRules();

        // we can only lootshare if the mod is enabled, and we are in the allowlist tag (OR the rule to bypass said tag is on), and we're not in the denylist tag.
        shouldLootshare.set(gameRules.getBoolean(LootShareGameRules.RULE_MOD_ENABLED) && (gameRules.getBoolean(LootShareGameRules.RULE_BYPASS_ALLOWLIST_TAG) || getType().is(LootShareEntityTypeTags.LOOTSHARE_ALLOWLIST)) && !getType().is(LootShareEntityTypeTags.LOOTSHARE_DENYLIST));
    }

	@WrapOperation(
            method = "dropAllDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V")
    )
	private void lootshare$shareLootTableDrops(
            LivingEntity instance,
            DamageSource damageSource,
            boolean bl,
            Operation<Void> original,
            @Share("lootshare.shouldLootshare") LocalBooleanRef shouldLootshare)
    {
        if (shouldLootshare.get() && !assistingPlayers.isEmpty()) {
            for (ServerPlayer serverPlayer : assistingPlayers.keySet()) {
                activeLootsharePlayer = serverPlayer;
                original.call(instance, damageSource, bl);
            }
            activeLootsharePlayer = null;
        } else {
            original.call(instance, damageSource, bl);
        }
	}

    @WrapOperation(
            method = "dropAllDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V")
    )
    private void lootshare$shareCustomDeathDrops(
            LivingEntity instance,
            ServerLevel serverLevel,
            DamageSource damageSource,
            boolean bl,
            Operation<Void> original,
            @Share("lootshare.shouldLootshare") LocalBooleanRef shouldLootshare
    ) {
        if (shouldLootshare.get() && !assistingPlayers.isEmpty()) {
            for (ServerPlayer serverPlayer : assistingPlayers.keySet()) {
                activeLootsharePlayer = serverPlayer;
                original.call(instance, serverLevel, damageSource, bl);
            }
            activeLootsharePlayer = null;
        } else {
            original.call(instance, serverLevel, damageSource, bl);
        }
    }

    @WrapOperation(
            method = "dropAllDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience(Lnet/minecraft/world/entity/Entity;)V")
    )
    private void lootshare$shareExperienceDrops(
            LivingEntity instance,
            Entity entity,
            Operation<Void> original,
            @Share("lootshare.shouldLootshare") LocalBooleanRef shouldLootshare
    ) {
        if (shouldLootshare.get() && !assistingPlayers.isEmpty() && level().getGameRules().getBoolean(LootShareGameRules.RULE_SHARE_EXP)) {
            for (ServerPlayer serverPlayer : assistingPlayers.keySet()) {
                activeLootsharePlayer = serverPlayer;
                original.call(instance, entity);
            }
            activeLootsharePlayer = null;
        } else {
            original.call(instance, entity);
        }
    }
}