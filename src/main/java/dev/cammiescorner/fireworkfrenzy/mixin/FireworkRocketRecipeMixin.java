package dev.cammiescorner.fireworkfrenzy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FireworkRocketRecipe.class)
public abstract class FireworkRocketRecipeMixin extends SpecialCraftingRecipe {
	@Unique private static final Ingredient FIREBALL = Ingredient.ofItems(Items.FIRE_CHARGE);

	public FireworkRocketRecipeMixin(Identifier id, CraftingCategory category) {
		super(id, category);
	}

	@Inject(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$captureStack(RecipeInputInventory recipeInputInventory, World world, CallbackInfoReturnable<Boolean> info, boolean bl, int i, int j, ItemStack itemStack, @Share("itemStack") LocalRef<ItemStack> stackRef) {
		stackRef.set(itemStack);
	}

	@ModifyExpressionValue(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", slice = @Slice(from = @At(value = "FIELD",
		target = "Lnet/minecraft/recipe/FireworkRocketRecipe;FIREWORK_STAR:Lnet/minecraft/recipe/Ingredient;"
	)), at = @At(value = "INVOKE",
		target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z")
	)
	public boolean fireworkfrenzy$allowFireball(boolean original, RecipeInputInventory recipeInputInventory, World world, @Share("itemStack") LocalRef<ItemStack> stackRef) {
		return original || (QuiltLoader.isModLoaded("explosiveenhancement") && FIREBALL.test(stackRef.get()));
	}


	@Inject(method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/nbt/NbtCompound;putByte(Ljava/lang/String;B)V"
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$addFireballTag(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, NbtCompound nbtCompound, NbtList nbtList, int i) {
		if(!QuiltLoader.isModLoaded("explosiveenhancement"))
			return;

		for(int i1 = 0; i1 < recipeInputInventory.size(); i1++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i1);

			if(!itemStack2.isEmpty() && FIREBALL.test(itemStack2))
				nbtCompound.putBoolean("Fireball", true);
		}
	}
}
