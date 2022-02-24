package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {
	@Unique private boolean changedBowProjectiles = false;

	public CrossbowItemMixin(Settings settings) { super(settings); }

	@Inject(method = "getProjectiles()Ljava/util/function/Predicate;", at = @At("HEAD"))
	public void fireworkfrenzy$getProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> info) {
		if(FireworkFrenzy.config.useRocketsFromInv && !changedBowProjectiles) {
			BOW_PROJECTILES = BOW_PROJECTILES.or(CROSSBOW_HELD_PROJECTILES);
			changedBowProjectiles = true;
		}
	}

	@ModifyVariable(method = "loadProjectile", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	private static boolean fireworkfrenzy$loadProjectile(boolean bl, LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
		boolean hasInfinity = FireworkFrenzy.config.crossbowsGetInfinity && EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) > 0;
		boolean arrowsGetInfinity = hasInfinity && projectile.isOf(Items.ARROW);
		boolean rocketsGetInfinity = hasInfinity && FireworkFrenzy.config.infinityAffectsRockets && projectile.getItem() instanceof FireworkRocketItem;

		return arrowsGetInfinity || rocketsGetInfinity;
	}
}
