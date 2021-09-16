package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.registry.ModItems;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileEntity implements FlyingItemEntity {
	@Shadow @Final private static TrackedData<ItemStack> ITEM;
	@Shadow protected abstract boolean hasExplosionEffects();
	@Shadow public abstract ItemStack getStack();

	@Unique public LivingEntity target;

	public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void explodePreDamage(CallbackInfo info, float damage, double d, Vec3d pos, List list, Iterator iterator, LivingEntity entity) {
		target = entity;

		if(entity == getOwner())
			entity.hurtTime = FireworkFrenzy.config.selfDamageInvincibilityTicks;
	}

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void explodePostDamage(CallbackInfo info, float damage, double d, Vec3d pos, List<LivingEntity> list, Iterator<LivingEntity> iterator, LivingEntity entity) {
		if(FireworkFrenzy.config.allowRocketJumping && hasExplosionEffects()) {
			Box box = new Box(getX() - 5.25, getY() - 5.25, getZ() - 5.25, getX() + 5.25, getY() + 5.25, getZ() + 5.25);
			float radius = (float) (box.getXLength() / 2);
			double multiplier = (dataTracker.get(ITEM).getSubNbt("Fireworks").getList("Explosions", 10).size() / 4.5D) * FireworkFrenzy.config.rocketJumpMultiplier;

			for(LivingEntity target : list) {
				if(!target.blockedByShield(DamageSource.firework((FireworkRocketEntity) (Object) this, getOwner()))) {
					Vec3d targetPos = new Vec3d(target.getX(), target.getY() + (target.getHeight() / 2), target.getZ());
					Vec3d velocityDirection = new Vec3d(target.getX() - getX(), targetPos.getY() - getY(), target.getZ() - getZ());
					double inverseDistance = 1 - (velocityDirection.length() / radius);

					target.knockbackVelocity = 0F;
					target.setVelocity(target.getVelocity().getX(), Math.min(1D, Math.abs(target.getVelocity().getY())), target.getVelocity().getZ());
					target.setVelocity(target.getVelocity().add(velocityDirection).multiply(inverseDistance * (target == getOwner() ? multiplier : multiplier * FireworkFrenzy.config.otherEntityKnockback)));
					target.velocityModified = true;
				}
			}
		}

		if(entity == getOwner()) {
			entity.hurtTime = FireworkFrenzy.config.selfDamageInvincibilityTicks;

			if(entity instanceof BlastJumper jumper)
				jumper.setBlastJumping(true);
		}
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
	public float selfDamage(DamageSource source, float amount) {
		if(!FireworkFrenzy.config.onlyAffectCrossbows && hasExplosionEffects())
			return FireworkFrenzy.config.baseDamage * dataTracker.get(ITEM).getSubNbt("Fireworks").getList("Explosions", 10).size();

		return amount;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
	public float crossbowDamage(DamageSource source, float amount) {
		if(FireworkFrenzy.config.enableGunboats && target == getOwner() && target.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.GUNBOATS))
			return 0;

		if(hasExplosionEffects())
			return FireworkFrenzy.config.baseDamage * dataTracker.get(ITEM).getSubNbt("Fireworks").getList("Explosions", 10).size();

		return amount;
	}
}
