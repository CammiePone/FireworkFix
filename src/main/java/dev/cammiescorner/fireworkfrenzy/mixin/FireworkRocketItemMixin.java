package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import dev.cammiescorner.fireworkfrenzy.util.BlastJumper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin extends Item {
	public FireworkRocketItemMixin(Settings settings) { super(settings); }

	@Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	public void fireworkfrenzy$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		if(user instanceof BlastJumper jumper && jumper.isBlastJumping() && FireworkFrenzy.config.boostsCancelRocketJumping)
			jumper.setBlastJumping(false);
	}

	@Inject(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info, NbtCompound nbtCompound, NbtList nbtList) {
		if(FireworkFrenzy.config.showTooltip && nbtList.size() > 0)
			tooltip.add(new TranslatableText(getTranslationKey() + ".damage").append(" ")
					.append(String.valueOf(FireworkFrenzy.config.baseDamage * nbtList.size()))
					.formatted(Formatting.GRAY));
	}
}
