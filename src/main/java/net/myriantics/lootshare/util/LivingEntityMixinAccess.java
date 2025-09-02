package net.myriantics.lootshare.util;

import net.minecraft.server.level.ServerPlayer;

public interface LivingEntityMixinAccess {
    ServerPlayer lootshare$getActiveLootsharePlayer();
}
