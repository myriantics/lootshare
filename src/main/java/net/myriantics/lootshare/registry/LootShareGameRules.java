package net.myriantics.lootshare.registry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import net.myriantics.lootshare.LootShareCommon;

public abstract class LootShareGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> RULE_MOD_ENABLED = register(
            "modEnabled",
            GameRules.Category.DROPS,
            true
    );

    public static final GameRules.Key<GameRules.BooleanValue> RULE_BYPASS_ALLOWLIST_TAG = register(
            "bypassAllowlistTag",
            GameRules.Category.DROPS,
            false
    );

    public static final GameRules.Key<GameRules.BooleanValue> RULE_SHARE_EXP = register(
            "shareExperience",
            GameRules.Category.DROPS,
            false
    );

    public static final GameRules.Key<GameRules.BooleanValue> RULE_PROTECT_DROPS = register(
            "protectDrops",
            GameRules.Category.DROPS,
            true
    );

    public static final GameRules.Key<GameRules.IntegerValue> RULE_ASSIST_WINDOW_TICKS = register(
            "assistWindowTicks",
            GameRules.Category.DROPS,
            1200
    );

    private static GameRules.Key<GameRules.BooleanValue> register(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(LootShareCommon.locateAlt(name), category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    private static GameRules.Key<GameRules.IntegerValue> register(String name, GameRules.Category category, int defaultValue) {
        return GameRuleRegistry.register(LootShareCommon.locateAlt(name), category, GameRuleFactory.createIntRule(defaultValue));
    }

    public static void init() {
        LootShareCommon.LOGGER.info("Registered LootShare's GameRules!");
    }
}
