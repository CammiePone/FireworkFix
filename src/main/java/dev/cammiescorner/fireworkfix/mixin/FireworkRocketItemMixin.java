package dev.cammiescorner.fireworkfix.mixin;

import dev.cammiescorner.fireworkfix.FireworkFix;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin extends Item {
	public FireworkRocketItemMixin(Settings settings) { super(settings); }

	@Inject(method = "appendTooltip", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/nbt/NbtCompound;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/NbtList;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info, NbtCompound nbtCompound, NbtList nbtList) {
		if(FireworkFix.config.showTooltip && nbtList.size() > 0)
			tooltip.add(new TranslatableText(getTranslationKey() + ".damage").append(" ")
					.append(String.valueOf(FireworkFix.config.baseDamage * nbtList.size()))
					.formatted(Formatting.GRAY));
	}
}
