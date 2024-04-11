package dev.cammiescorner.fireworkfrenzy.client;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class FireworkFrenzyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		EntityRendererRegistry.register(FireworkFrenzy.DAMAGE_CLOUD, EmptyEntityRenderer::new);
	}
}
