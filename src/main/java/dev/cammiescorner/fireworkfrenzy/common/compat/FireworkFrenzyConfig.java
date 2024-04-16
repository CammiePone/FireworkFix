package dev.cammiescorner.fireworkfrenzy.common.compat;

import eu.midnightdust.lib.config.MidnightConfig;

public class FireworkFrenzyConfig extends MidnightConfig {
	@Entry public static int crossbowChargeTime = 20;
	@Entry public static int quickChargeModifier = 5;

	@Entry public static int airStrikeJumpingChargeTime = 2;
	@Entry public static int airStrikeGroundedChargeTime = 30;
	@Entry public static double airStrikeDamageMultiplier = 0.5;

	@Entry public static float mobDamage = 3f;
	@Entry public static float playerDamage = 3f;
	@Entry public static float fireballDamageBonus = 6f;
	@Entry public static float burstChanceToDisableShields = 0.25f;

	@Entry public static boolean allowRocketJumping = true;
	@Entry public static double rocketJumpMultiplier = 1d;
	@Entry public static double otherEntityKnockBack = 0.5d;
	@Entry public static double airStrafingMultiplier = 3d;
	@Entry public static boolean elytraCancelsRocketJumping = true;
	@Entry public static boolean boostsCancelRocketJumping = true;

	@Entry public static boolean crossbowsGetInfinity = true;
	@Entry public static boolean infinityAffectsRockets = true;
	@Entry public static boolean useRocketsFromInv = true;

	@Entry public static boolean rocketsHaveFalloff = true;
	@Entry public static float falloffPerMeter = 1f;
	@Entry public static float maximumFalloffDamage = 16f;
	@Entry public static float startOfFalloff = 3f;

	@Entry public static boolean showTooltip = true;
}
