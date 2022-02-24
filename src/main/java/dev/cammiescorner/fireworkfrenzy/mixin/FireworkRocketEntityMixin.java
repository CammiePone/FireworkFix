package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.EntityHitResult;
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

	@Unique public LivingEntity splashTarget;
	@Unique public LivingEntity directTarget;
	@Unique public double damageMultiplier;

	public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1),  locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$explodePreDamage(CallbackInfo info, float damage, ItemStack stack, NbtCompound tag, NbtList nbtList, double d, Vec3d vec, List<LivingEntity> list, Iterator<LivingEntity> iterator, LivingEntity entity) {
		splashTarget = entity;
	}

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$explodePostDamage(CallbackInfo info, float damage, ItemStack stack, NbtCompound tag, NbtList nbtList, double d, Vec3d vec, List<LivingEntity> list, Iterator<LivingEntity> iterator, LivingEntity entity) {
		NbtCompound subNbt = dataTracker.get(ITEM).getSubNbt("Fireworks");

		if(FireworkFrenzy.config.allowRocketJumping && hasExplosionEffects() && subNbt != null) {
			Box box = getBoundingBox().expand(d);
			float radius = (float) (box.getXLength() / 2);
			double multiplier = (subNbt.getList("Explosions", NbtElement.COMPOUND_TYPE).size() / 4.5D) * FireworkFrenzy.config.rocketJumpMultiplier;

			for(LivingEntity target : list) {
				if(!target.blockedByShield(DamageSource.firework((FireworkRocketEntity) (Object) this, getOwner()))) {
					EntityHitResult hitResult = ProjectileUtil.raycast(this, getPos(), target.getPos(), box, hit -> !hit.isSpectator(), box.getAverageSideLength() * 2);
					Vec3d targetPos;
					Vec3d velocityDirection;

					if(hitResult != null) {
						targetPos = hitResult.getPos();
						velocityDirection = new Vec3d(targetPos.getX() - getX(), targetPos.getY() - getY(), targetPos.getZ() - getZ());
						damageMultiplier = Math.max(0, 1 - (velocityDirection.length() / radius));
					}

					targetPos = new Vec3d(target.getX(), target.getY() + (target.getHeight() / 2), target.getZ());
					velocityDirection = new Vec3d(target.getX() - getX(), targetPos.getY() - getY(), target.getZ() - getZ());
					double inverseDistance = Math.max(0, 1 - (velocityDirection.length() / radius));

					target.knockbackVelocity = 0F;
					target.setVelocity(target.getVelocity().getX(), Math.min(1D, Math.abs(target.getVelocity().getY())), target.getVelocity().getZ());
					target.setVelocity(target.getVelocity().add(velocityDirection).multiply(inverseDistance * (target == getOwner() ? multiplier : multiplier * FireworkFrenzy.config.otherEntityKnockback)));
					target.velocityModified = true;
				}
			}
		}

		if(entity instanceof BlastJumper jumper) {
			jumper.setTimeOnGround(0);
			jumper.setBlastJumping(true);
		}
	}

	@Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;explodeAndRemove()V"))
	public void fireworkfrenzy$directHit(EntityHitResult entityHitResult, CallbackInfo info) {
		if(entityHitResult.getEntity() instanceof LivingEntity target)
			directTarget = target;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
	public float fireworkfrenzy$selfDamage(DamageSource source, float amount) {
		NbtCompound subNbt = dataTracker.get(ITEM).getSubNbt("Fireworks");

		if(hasExplosionEffects() && subNbt != null)
			return (float) ((FireworkFrenzy.config.baseDamage * subNbt.getList("Explosions", NbtElement.COMPOUND_TYPE).size()) * damageMultiplier);

		return amount;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
	public float fireworkfrenzy$crossbowDamage(DamageSource source, float amount) {
		NbtCompound subNbt = dataTracker.get(ITEM).getSubNbt("Fireworks");

		if(hasExplosionEffects() && subNbt != null) {
			float damage = FireworkFrenzy.config.baseDamage * subNbt.getList("Explosions", NbtElement.COMPOUND_TYPE).size();

			if(splashTarget == directTarget)
				return damage;

			return (float) (damage * damageMultiplier);
		}

		return amount;
	}
}
