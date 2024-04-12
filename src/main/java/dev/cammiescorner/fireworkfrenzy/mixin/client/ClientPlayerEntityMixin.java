package dev.cammiescorner.fireworkfrenzy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.cammiescorner.fireworkfrenzy.client.sound.BlastJumpingSoundInstance;
import dev.cammiescorner.fireworkfrenzy.common.util.BlastJumper;
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

	@Inject(method = "onTrackedDataUpdate", at = @At("HEAD"))
	public void fireworkfrenzy$onTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
		BlastJumpingSoundInstance soundInstance = new BlastJumpingSoundInstance((ClientPlayerEntity) (Object) this);

		if(isBlastJumping() && !client.getSoundManager().isPlaying(soundInstance))
			client.getSoundManager().play(soundInstance);
	}

	@ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
	))
	public boolean fireworkfrenzy$noSlowDownWhileRocketJumping(boolean original) {
		return original && !isBlastJumping();
	}
}
