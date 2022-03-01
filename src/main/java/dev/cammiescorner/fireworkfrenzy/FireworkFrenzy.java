package dev.cammiescorner.fireworkfrenzy;

import dev.cammiescorner.fireworkfrenzy.enchantments.JumperSpecialistEnchantment;
import dev.cammiescorner.fireworkfrenzy.integration.FireworkFrenzyConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FireworkFrenzy implements ModInitializer {
	public static final TrackedData<Boolean> BLAST_JUMPING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Integer> TIME_ON_GROUND = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final String MOD_ID = "fireworkfrenzy";
	public static FireworkFrenzyConfig config;

	public static Enchantment JUMPER_SPECIALIST;

	@Override
	public void onInitialize() {
		AutoConfig.register(FireworkFrenzyConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(FireworkFrenzyConfig.class).getConfig();

		JUMPER_SPECIALIST = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "jumper_specialist"), new JumperSpecialistEnchantment());
	}
}
