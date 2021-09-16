package dev.cammiescorner.fireworkfrenzy.registry;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.items.GunboatsItem;
import dev.cammiescorner.fireworkfrenzy.items.MemeSpoonItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
	//-----Items-----//
	public static final Item MEME_SPOON = new MemeSpoonItem();
	public static final Item GUNBOATS = new GunboatsItem(new GunboatsArmourMaterial(), EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT));

	//-----Registry-----//
	public static void register() {
		if(FireworkFrenzy.config.enableMemeSpoon)
			Registry.register(Registry.ITEM, new Identifier(FireworkFrenzy.MOD_ID, "meme_spoon"), MEME_SPOON);
		if(FireworkFrenzy.config.enableGunboats)
			Registry.register(Registry.ITEM, new Identifier(FireworkFrenzy.MOD_ID, "gunboats"), GUNBOATS);
	}

	public static class GunboatsArmourMaterial implements ArmorMaterial {
		@Override
		public int getDurability(EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return 3;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return null;
		}

		@Override
		public String getName() {
			return "gunboats";
		}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	}
}
