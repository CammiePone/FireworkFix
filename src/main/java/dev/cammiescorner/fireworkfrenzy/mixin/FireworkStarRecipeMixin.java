package dev.cammiescorner.fireworkfrenzy.mixin;

import com.google.common.collect.Lists;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.Map;

@Mixin(FireworkStarRecipe.class)
public class FireworkStarRecipeMixin {
	@Shadow @Final private static Ingredient TYPE_MODIFIER;
	@Shadow @Final private static Ingredient FLICKER_MODIFIER;
	@Shadow @Final private static Ingredient TRAIL_MODIFIER;
	@Shadow @Final private static Ingredient GUNPOWDER;
	@Shadow @Final private static Map<Item, FireworkRocketItem.Type> TYPE_MODIFIER_MAP;
	@Unique private static final Ingredient POTION_MODIFIER = Ingredient.ofItems(Items.SPLASH_POTION);

	/**
	 * @author Cammie - Firework Frenzy
	 * @reason cant continue the loop otherwise :/
	 */
	@Overwrite
	public boolean matches(CraftingInventory craftingInventory, World world) {
		boolean isGunpowder = false;
		boolean isDye = false;
		boolean isTypeModifier = false;
		boolean isDiamond = false;
		boolean isGlowstone = false;
		boolean isPotion = false;

		for(int i = 0; i < craftingInventory.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);

			if(itemStack.isEmpty())
				continue;

			if(GUNPOWDER.test(itemStack)) {
				if(isGunpowder)
					return false;

				isGunpowder = true;
				continue;
			}
			if(itemStack.getItem() instanceof DyeItem) {
				isDye = true;
				continue;
			}
			if(TYPE_MODIFIER.test(itemStack)) {
				if(isTypeModifier)
					return false;

				isTypeModifier = true;
				continue;
			}
			if(TRAIL_MODIFIER.test(itemStack)) {
				if(isDiamond)
					return false;

				isDiamond = true;
				continue;
			}
			if(FLICKER_MODIFIER.test(itemStack)) {
				if(isGlowstone)
					return false;

				isGlowstone = true;
				continue;
			}
			if(POTION_MODIFIER.test(itemStack)) {
				if(isPotion)
					return false;

				isPotion = true;
				continue;
			}

			return false;
		}

		return isGunpowder && isDye;
	}

	/**
	 * @author Cammie - Firework Frenzy
	 * @reason cant continue the loop otherwise :/
	 */
	@Overwrite
	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
		NbtCompound tag = itemStack.getOrCreateSubNbt("Explosion");
		FireworkRocketItem.Type type = FireworkRocketItem.Type.SMALL_BALL;
		ArrayList<Integer> list = Lists.newArrayList();

		for(int i = 0; i < craftingInventory.size(); ++i) {
			ItemStack itemStack2 = craftingInventory.getStack(i);

			if(itemStack2.isEmpty())
				continue;

			if(TYPE_MODIFIER.test(itemStack2)) {
				type = TYPE_MODIFIER_MAP.get(itemStack2.getItem());
				continue;
			}
			if(POTION_MODIFIER.test(itemStack2)) {
				tag.putString("Potion", Registries.POTION.getId(PotionUtil.getPotion(itemStack2)).toString());

				continue;
			}
			if(FLICKER_MODIFIER.test(itemStack2)) {
				tag.putBoolean("Flicker", true);
				continue;
			}
			if(TRAIL_MODIFIER.test(itemStack2)) {
				tag.putBoolean("Trail", true);
				continue;
			}
			if(!(itemStack2.getItem() instanceof DyeItem))
				continue;

			list.add(((DyeItem) itemStack2.getItem()).getColor().getFireworkColor());
		}

		tag.putIntArray("Colors", list);
		tag.putByte("Type", (byte) type.getId());

		return itemStack;
	}
}
