package dev.cammiescorner.fireworkfrenzy.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class TakeoffEnchantment extends Enchantment {
	public TakeoffEnchantment() {
		super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
	}

	@Override
	public int getMinPower(int level) {
		return 23;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	protected boolean canAccept(Enchantment other) {
		return other != Enchantments.FEATHER_FALLING && super.canAccept(other);
	}
}
