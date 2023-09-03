package com.apowillow.fireworkfrenzy.mixin;

import net.minecraft.entity.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin extends Entity {
	@Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

	public AreaEffectCloudEntityMixin(EntityType<?> type, World world) { super(type, world); }

	@Inject(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;addImportantParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void fireworkfrenzy$spawnParticles(CallbackInfo ci, boolean bl, float f, ParticleEffect particleEffect, int i, float g, int j, float h, float k, double d, double e, double l, double n, double o, double p) {
		for(float m = 0.5F; m < getDimensions(getPose()).height - 0.5F; m += 0.5F)
			getWorld().addImportantParticle(particleEffect, d, e + m, l, n, o, p);
	}
}
