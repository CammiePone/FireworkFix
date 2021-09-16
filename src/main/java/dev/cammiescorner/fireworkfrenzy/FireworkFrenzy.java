package dev.cammiescorner.fireworkfrenzy;

import dev.cammiescorner.fireworkfrenzy.integration.FireworkFrenzyConfig;
import dev.cammiescorner.fireworkfrenzy.registry.ModItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;

public class FireworkFrenzy implements ModInitializer {
	public static final TrackedData<Boolean> BLAST_JUMPING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final String MOD_ID = "fireworkfrenzy";
	public static FireworkFrenzyConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(FireworkFrenzyConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(FireworkFrenzyConfig.class).getConfig();

		ModItems.register();
	}
}
