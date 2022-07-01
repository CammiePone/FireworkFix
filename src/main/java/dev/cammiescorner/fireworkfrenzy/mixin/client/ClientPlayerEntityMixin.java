package dev.cammiescorner.fireworkfrenzy.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.cammiescorner.fireworkfrenzy.client.sound.BlastJumpingSoundInstance;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements BlastJumper {
	@Shadow @Final protected MinecraftClient client;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) { super(world, profile, publicKey); }

	@Inject(method = "onTrackedDataSet", at = @At("HEAD"))
	public void fireworkfrenzy$onTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
		BlastJumpingSoundInstance soundInstance = new BlastJumpingSoundInstance((ClientPlayerEntity) (Object) this);

		if(isBlastJumping() && !client.getSoundManager().isPlaying(soundInstance))
			client.getSoundManager().play(soundInstance);
	}
}
