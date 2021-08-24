package dev.cammiescorner.fireworkfix;

import dev.cammiescorner.fireworkfix.integration.FireworkFixConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class FireworkFix implements ModInitializer {
	public static final String MOD_ID = "fireworkfix";
	public static FireworkFixConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(FireworkFixConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(FireworkFixConfig.class).getConfig();
	}
}
