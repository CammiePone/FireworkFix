package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements BlastJumper {
	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "tick", at = @At("TAIL"))
	public void fireworkfrenzy$tick(CallbackInfo info) {
		if(isBlastJumping()) {
			airStrafingSpeed *= FireworkFrenzy.config.airStrafingMultiplier;

			if(!world.isClient() && (isOnGround() || isSubmergedInWater()))
				setTimeOnGround(getTimeOnGround() + 1);

			if(getTimeOnGround() > 2 || hasVehicle() || (FireworkFrenzy.config.elytrasCancelRocketJumping && isFallFlying()) || !isAlive())
				setBlastJumping(false);
		}
	}

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	public void fireworkfrenzy$initDataTracker(CallbackInfo info) {
		dataTracker.startTracking(FireworkFrenzy.BLAST_JUMPING, false);
		dataTracker.startTracking(FireworkFrenzy.TIME_ON_GROUND, 0);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void fireworkfrenzy$readNbt(NbtCompound tag, CallbackInfo info) {
		setBlastJumping(tag.getBoolean("BlastJumping"));
		setTimeOnGround(tag.getInt("TimeOnGround"));
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void fireworkfrenzy$writeNbt(NbtCompound tag, CallbackInfo info) {
		tag.putBoolean("BlastJumping", isBlastJumping());
		tag.putInt("TimeOnGround", getTimeOnGround());
	}

	@Override
	public boolean isBlastJumping() {
		return dataTracker.get(FireworkFrenzy.BLAST_JUMPING);
	}

	@Override
	public void setBlastJumping(boolean blastJumping) {
		dataTracker.set(FireworkFrenzy.BLAST_JUMPING, blastJumping);
	}

	@Override
	public int getTimeOnGround() {
		return dataTracker.get(FireworkFrenzy.TIME_ON_GROUND);
	}

	@Override
	public void setTimeOnGround(int timer) {
		dataTracker.set(FireworkFrenzy.TIME_ON_GROUND, timer);
	}
}
