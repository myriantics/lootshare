# LootShare

Simple serverside lootshare mod for Fabric.

## Configuration

There are 2 primary ways to configure LootShare, those being tags and gamerules.

### Entity Type Tags:

- ***#lootshare:lootshare_allowlist***
  - Entities in this tag have the lootshare functionality enabled. By default, this includes anything in the #c:bosses tag, as well as wardens and elder guardians.
  - The gamerule **lootshare.bypassAllowListTag** enables lootshare functionality for all LivingEntities.
- ***#lootshare:lootshare_denylist***
  - Entities in this tag are excluded from lootshare functionality.
  - The gamerule **lootshare.bypassAllowListTag** respects this tag - any denylisted entitytypes remain denylisted.
  - By default, this tag is empty.

### GameRules:

- ***lootshare.modEnabled***
  - Enables / disables the mod's functionality.
  - Defaults to true.
- ***lootshare.bypassAllowlistTag***
  - When enabled, all non-denylisted LivingEntities have lootshare functionality enabled.
  - Defaults to false.
- ***lootshare.shareExperience***
  - When enabled, experience drops are multiplied by the amount of assisting players.
  - Kind of jank atm, if this is a desired feature I could improve it
  - Defaults to false.
- ***lootshare.protectDrops***
  - When enabled, item drops can only be picked up by a specific assisting player.
  - When disabled, item drops are free to merge and be picked up by anyone.
  - Defaults to true.
- ***lootshare.assistWindowTicks***
  - Defines the amount of time (in game ticks) that can pass before a player is removed from the kill-assist list.
  - Defaults to 1200 ticks. (1 minute)