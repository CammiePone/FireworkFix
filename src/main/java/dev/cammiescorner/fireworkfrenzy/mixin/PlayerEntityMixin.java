package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import dev.cammiescorner.fireworkfrenzy.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements BlastJumper {
	@Shadow public abstract float getAttackCooldownProgress(float baseTime);

	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info) {
		if(isBlastJumping()) {
			if(isOnGround() || (isTouchingWater() && age % 60 == 0) || (FireworkFrenzy.config.elytrasCancelRocketJumping && isFallFlying()))
				setBlastJumping(false);

			if(getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.GUNBOATS)) {
				flyingSpeed = 0.06F;
				fallDistance = 0F;
			}
			else
				flyingSpeed = 0.02F;
		}
	}

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	public void initDataTracker(CallbackInfo info) {
		dataTracker.startTracking(FireworkFrenzy.BLAST_JUMPING, false);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F", ordinal = 0), ordinal = 0)
	public float attackDamage(float damage, Entity target) {
		boolean isCrit = getAttackCooldownProgress(0.5F) > 0.9F && this.fallDistance > 0.0F && !this.onGround && !this.isClimbing() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && !this.hasVehicle() && target instanceof LivingEntity && !this.isSprinting();

		return isBlastJumping() && getMainHandStack().isOf(ModItems.MEME_SPOON) ? damage * (isCrit ? 2 : 3) : damage;
	}

	@Override
	public boolean isBlastJumping() {
		return dataTracker.get(FireworkFrenzy.BLAST_JUMPING);
	}

	@Override
	public void setBlastJumping(boolean blastJumping) {
		dataTracker.set(FireworkFrenzy.BLAST_JUMPING, blastJumping);
	}
}
