package dev.cammiescorner.fireworkfrenzy.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.common.compat.ExplosiveEnhancementCompat;
import dev.cammiescorner.fireworkfrenzy.common.entities.DamageCloudEntity;
import dev.cammiescorner.fireworkfrenzy.common.compat.FireworkFrenzyConfig;
import dev.cammiescorner.fireworkfrenzy.common.util.BlastJumper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileEntity implements FlyingItemEntity {
	@Shadow @Final private static TrackedData<ItemStack> ITEM;
	@Shadow protected abstract boolean hasExplosionEffects();

	@Shadow private @Nullable LivingEntity shooter;

	@Shadow private int lifeTime;
	@Unique public FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
	@Unique public LivingEntity directTarget;
	@Unique public float blastSize = 2F;
	@Unique public float knockbackAmount = 1F;
	@Unique public int glowingAmount = 0;

	public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	void fireworkfrenzy$noRandomFuse(World world, double x, double y, double z, ItemStack stack, CallbackInfo info, int i) {
		setVelocity(0, 0.05, 0);

		if(EnchantmentHelper.getLevel(FireworkFrenzy.FIXED_FUSE, dataTracker.get(ITEM)) > 0)
			lifeTime = 10 * i + 6;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"
	))
	private double fireworkfrenzy$blastRadius(double value) {
		NbtCompound tag = dataTracker.get(ITEM).getSubNbt("Fireworks");
		Set<FireworkRocketItem.Type> types = EnumSet.noneOf(FireworkRocketItem.Type.class);

		if(tag != null) {
			NbtList nbtList = tag.getList("Explosions", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbt = nbtList.getCompound(i);

				if(nbt.contains("Type"))
					types.add(FireworkRocketItem.Type.values()[nbt.getByte("Type")]);
				if(nbt.getBoolean("Trail"))
					knockbackAmount += 0.1F;
				if(nbt.getBoolean("Flicker"))
					glowingAmount += 20;
			}
		}

		if(types.contains(FireworkRocketItem.Type.LARGE_BALL))
			blastSize = 5F;
		else if(types.contains(FireworkRocketItem.Type.STAR))
			blastSize = 3F;
		else
			blastSize = 2F;

		return blastSize;
	}

	@Inject(method = "explode", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
			ordinal = 1
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$explodePostDamage(CallbackInfo info, float damage, ItemStack stack, NbtCompound tag, NbtList nbtList, double d, Vec3d vec, List<LivingEntity> list, Iterator<LivingEntity> iterator, LivingEntity target, @Share("target") LocalRef<LivingEntity> targetRef) {
		targetRef.set(target);

		if(hasExplosionEffects() && tag != null) {
			float radius = blastSize / 2;
			DamageSource source = getDamageSources().fireworks(self, getOwner());

			if(!target.blockedByShield(source)) {
				Vec3d targetPos = target.getPos().add(0, MathHelper.clamp(getY() - target.getY(), 0, target.getHeight()), 0);
				Vec3d direction = targetPos.subtract(getPos());
				double distance = direction.length() - (getWidth() * 0.5) - (target.getWidth() * 0.5);
				double inverseDistance = MathHelper.clamp(1 - (distance / radius), 0, 1);
				float fireworkDamage = (target instanceof PlayerEntity ? FireworkFrenzyConfig.playerDamage : FireworkFrenzyConfig.mobDamage) * nbtList.size() + (tag.getBoolean("Fireball") ? FireworkFrenzyConfig.fireballDamageBonus : 0);

				if(target == getOwner() && EnchantmentHelper.getLevel(FireworkFrenzy.TAKEOFF, target.getEquippedStack(EquipmentSlot.FEET)) > 0)
					fireworkDamage = 0;
				if(EnchantmentHelper.getLevel(FireworkFrenzy.AIR_STRIKE, stack) > 0 && getOwner() instanceof BlastJumper jumper && jumper.isBlastJumping())
					fireworkDamage *= FireworkFrenzyConfig.airStrikeDamageMultiplier;

				if(glowingAmount > 0)
					target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, glowingAmount, 0, false, false));

				if(target == directTarget)
					target.damage(source, fireworkDamage);
				else
					target.damage(source, (float) (fireworkDamage * inverseDistance));

				if(FireworkFrenzyConfig.allowRocketJumping) {
					double multiplier = (nbtList.size() * 0.4) * FireworkFrenzyConfig.rocketJumpMultiplier * knockbackAmount;
					target.setVelocity(target.getVelocity().getX(), Math.min(1D, Math.abs(target.getVelocity().getY())), target.getVelocity().getZ());
					target.setVelocity(target.getVelocity().add(direction).multiply(inverseDistance * (target == getOwner() ? multiplier : multiplier * FireworkFrenzyConfig.otherEntityKnockBack)));
					target.velocityModified = true;
				}
			}
		}

		if(target instanceof BlastJumper jumper && FireworkFrenzyConfig.allowRocketJumping) {
			jumper.setTimeOnGround(0);
			jumper.setBlastJumping(true);
		}
	}

	@Inject(method = "explode", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void fireworkfrenzy$spawnPotionCloud(CallbackInfo info, @Share("target") LocalRef<LivingEntity> targetRef) {
		ItemStack stack = dataTracker.get(ITEM);
		NbtCompound tag = stack.isEmpty() ? null : stack.getSubNbt("Fireworks");
		Set<FireworkRocketItem.Type> types = EnumSet.noneOf(FireworkRocketItem.Type.class);

		if(tag != null) {
			NbtList nbtList = tag.getList("Explosions", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbt = nbtList.getCompound(i);

				if(nbt.contains("Type"))
					types.add(FireworkRocketItem.Type.values()[nbt.getByte("Type")]);
			}

			if(types.contains(FireworkRocketItem.Type.STAR)) {
				DamageCloudEntity cloud = FireworkFrenzy.DAMAGE_CLOUD.create(getWorld());

				if(cloud != null) {
					cloud.setRadius(blastSize);
					cloud.setOwner(shooter);
					cloud.setDuration(200);
					cloud.setColor(0xf8d26a);
					cloud.setPosition(getPos().add(0, -cloud.getRadius(), 0));
					getWorld().spawnEntity(cloud);
				}
			}

			if(types.contains(FireworkRocketItem.Type.BURST) && targetRef.get() instanceof PlayerEntity player && player.isBlocking())
				player.disableShield(false);
		}
	}

	@Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;explodeAndRemove()V"))
	public void fireworkfrenzy$directHit(EntityHitResult entityHitResult, CallbackInfo info) {
		if(entityHitResult.getEntity() instanceof LivingEntity target)
			directTarget = target;
	}

	@WrapWithCondition(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFireworkParticle(DDDDDDLnet/minecraft/nbt/NbtCompound;)V"))
	public boolean fireworkfrenzy$yeet(World world, double d, double d1, double d2, double d3, double d4, double d5, NbtCompound nbtCompound) {
		return !QuiltLoader.isModLoaded("explosiveenhancement");
	}

	@Inject(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFireworkParticle(DDDDDDLnet/minecraft/nbt/NbtCompound;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$particleTime(byte status, CallbackInfo info, ItemStack itemStack, NbtCompound tag, Vec3d vec3d) {
		if(QuiltLoader.isModLoaded("explosiveenhancement")) {
			if(tag.getBoolean("Fireball"))
				ExplosiveEnhancementCompat.spawnEnhancedBooms(getWorld(), getX(), getY(), getZ(), 1.25f);
			else
				getWorld().addFireworkParticle(getX(), getY(), getZ(), vec3d.x, vec3d.y, vec3d.z, tag);
		}
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
	public float fireworkfrenzy$selfDamage(DamageSource source, float amount) {
		return 0;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
	public float fireworkfrenzy$crossbowDamage(DamageSource source, float amount) {
		return 0;
	}
}
