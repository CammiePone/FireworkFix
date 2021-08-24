package dev.cammiescorner.fireworkfix.integration;

import dev.cammiescorner.fireworkfix.FireworkFix;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = FireworkFix.MOD_ID)
public class FireworkFixConfig implements ConfigData {
	@Comment("Base damage of Firework Rockets.")
	public float baseDamage = 4F;

	@Comment("Should item display damage of Firework Rockets?")
	public boolean showTooltip = true;

	@Comment("Should only affect crossbows?")
	public boolean onlyAffectCrossbows = true;
}
