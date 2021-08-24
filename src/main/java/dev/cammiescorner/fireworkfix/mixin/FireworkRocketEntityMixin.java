package dev.cammiescorner.fireworkfix.mixin;

import dev.cammiescorner.fireworkfix.FireworkFix;
import net.minecraft.entity.*;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity implements FlyingItemEntity {
	@Shadow @Final private static TrackedData<ItemStack> ITEM;

	@Shadow protected abstract boolean hasExplosionEffects();

	public FireworkRocketEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void explode(CallbackInfo info, float damage, double d, Vec3d pos, List list, Iterator iterator, LivingEntity entity) {
		if(FireworkFix.config.allowRocketJumping) {
			Vec3d vec3d = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
			double inverseDistance = getPos().distanceTo(vec3d) != 0 ? 1 / getPos().distanceTo(vec3d) : 1;

			entity.setVelocity(getVelocity().multiply(-inverseDistance * FireworkFix.config.rocketJumpMultiplier));
			entity.velocityModified = true;
		}
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
	public float selfDamage(float amount) {
		if(!FireworkFix.config.onlyAffectCrossbows && hasExplosionEffects())
			return FireworkFix.config.baseDamage * dataTracker.get(ITEM).getSubNbt("Fireworks").getList("Explosions", 10).size();

		return amount;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
	public float crossbowDamage(float amount) {
		if(hasExplosionEffects())
			return FireworkFix.config.baseDamage * dataTracker.get(ITEM).getSubNbt("Fireworks").getList("Explosions", 10).size();

		return amount;
	}
}
