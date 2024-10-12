package dev.cammiescorner.fireworkfrenzy.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.common.compat.FireworkFrenzyConfig;
import dev.cammiescorner.fireworkfrenzy.common.util.BlastJumper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {
	@Shadow private static float getPullProgress(int useTicks, ItemStack stack) { return 0; }

	@Unique private static BlastJumper jumper = null;

	public CrossbowItemMixin(Settings settings) { super(settings); }

	@Inject(method = "usageTick", at = @At("HEAD"))
	private void fireworkfrenzy$usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo info) {
		if(user instanceof BlastJumper jumper)
			CrossbowItemMixin.jumper = jumper;
	}

	@Inject(method = "usageTick", at = @At("TAIL"))
	private void fireworkfrenzy$stopUsingItem(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo info) {
		if(jumper != null && EnchantmentHelper.getLevel(FireworkFrenzy.AIR_STRIKE, stack) > 0 && jumper.isBlastJumping() && getPullProgress(getMaxUseTime(stack) - remainingUseTicks, stack) >= 1f)
			user.stopUsingItem();
	}

	@ModifyReturnValue(method = "isUsedOnRelease", at = @At("RETURN"))
	private boolean fireworkfrenzy$aaaaa(boolean original, ItemStack stack) {
		if(jumper != null && EnchantmentHelper.getLevel(FireworkFrenzy.AIR_STRIKE, stack) > 0 && jumper.isBlastJumping())
			return false;

		return original;
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

	@ModifyConstant(method = "getPullTime", constant = @Constant(intValue = 25))
	private static int fireworkfrenzy$configurablePullTime(int original) {
		return FireworkFrenzyConfig.crossbowChargeTime;
	}

	@ModifyConstant(method = "getPullTime", constant = @Constant(intValue = 5))
	private static int fireworkfrenzy$configurableQuickChargeModifier(int original) {
		return FireworkFrenzyConfig.quickChargeModifier;
	}

	@ModifyReturnValue(method = "getPullTime", at = @At("RETURN"))
	private static int fireworkfrenzy$changeDefaultPullTimes(int original) {
		return Math.max(1, original);
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
