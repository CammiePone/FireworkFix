package dev.cammiescorner.fireworkfrenzy.client;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

public class FireworkFrenzyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(FireworkFrenzy.DAMAGE_CLOUD, EmptyEntityRenderer::new);
	}
}
