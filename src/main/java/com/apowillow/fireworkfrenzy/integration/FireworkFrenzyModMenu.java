package com.apowillow.fireworkfrenzy.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.apowillow.fireworkfrenzy.FireworkFrenzy;

public class FireworkFrenzyModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> FireworkFrenzyConfig.getScreen(parent, FireworkFrenzy.MOD_ID);
	}
}
