package dev.cammiescorner.fireworkfix.integration;

import dev.cammiescorner.fireworkfix.FireworkFix;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = FireworkFix.MOD_ID)
public class FireworkFixConfig implements ConfigData {
	@Comment("Number of Invincibility Ticks when a player hits themselves" +
			"\n    with a Firework Rocket? (Vanilla: 10)")
	public int selfDamageInvincibilityTicks = 0;

	@Comment("Base damage of Firework Rockets.")
	public float baseDamage = 4F;

	@Comment("Multiplier for Rocket Jump Velocity.")
	public double rocketJumpMultiplier = 1D;

	@Comment("Multiplier for other entity's Firework Rocket Knockback")
	public double otherEntityKnockback = 0.333D;

	@Comment("Should item display damage of Firework Rockets?")
	public boolean showTooltip = true;

	@Comment("Should damage scaling only affect Crossbows?")
	public boolean onlyAffectCrossbows = true;

	@Comment("Should players be able to Rocket Jump? (Experimental)")
	public boolean allowRocketJumping = false;
}
