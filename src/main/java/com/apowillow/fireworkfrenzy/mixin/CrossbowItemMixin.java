package com.apowillow.fireworkfrenzy.mixin;

import com.apowillow.fireworkfrenzy.FireworkFrenzy;
import com.apowillow.fireworkfrenzy.integration.FireworkFrenzyConfig;
import com.apowillow.fireworkfrenzy.util.BlastJumper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {
	@Unique private static BlastJumper jumper = null;

	public CrossbowItemMixin(Settings settings) { super(settings); }

	@Inject(method = "usageTick", at = @At("HEAD"))
	private void fireworkfrenzy$usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo info) {
		if(user instanceof BlastJumper jumper)
			CrossbowItemMixin.jumper = jumper;
	}

	@Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
	private static void fireworkfrenzy$getPullTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
		if(EnchantmentHelper.getLevel(FireworkFrenzy.AIR_STRIKE, stack) > 0) {
			if(jumper != null && jumper.isBlastJumping())
				info.setReturnValue(FireworkFrenzyConfig.airStrikeJumpingChargeTime);
			else
				info.setReturnValue(FireworkFrenzyConfig.airStrikeGroundedChargeTime);
		}
	}

	@Inject(method = "getProjectiles()Ljava/util/function/Predicate;", at = @At("HEAD"), cancellable = true)
	public void fireworkfrenzy$getProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> info) {
		if(FireworkFrenzyConfig.useRocketsFromInv)
			info.setReturnValue(BOW_PROJECTILES.or(CROSSBOW_HELD_PROJECTILES));
	}

	@ModifyVariable(method = "loadProjectile", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	private static boolean fireworkfrenzy$loadProjectile(boolean bl, LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
		boolean hasInfinity = FireworkFrenzyConfig.crossbowsGetInfinity && EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) > 0;
		boolean arrowsGetInfinity = hasInfinity && projectile.isOf(Items.ARROW);
		boolean rocketsGetInfinity = hasInfinity && FireworkFrenzyConfig.infinityAffectsRockets && projectile.getItem() instanceof FireworkRocketItem;

		return arrowsGetInfinity || rocketsGetInfinity;
	}
}
