package dev.cammiescorner.fireworkfrenzy.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.cammiescorner.fireworkfrenzy.client.sound.BlastJumpingSoundInstance;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements BlastJumper {
	@Shadow @Final protected MinecraftClient client;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) { super(world, profile); }

	@Inject(method = "onTrackedDataSet", at = @At("HEAD"))
	public void onTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
		if(isBlastJumping())
			this.client.getSoundManager().play(new BlastJumpingSoundInstance((ClientPlayerEntity) (Object) this));
	}
}
