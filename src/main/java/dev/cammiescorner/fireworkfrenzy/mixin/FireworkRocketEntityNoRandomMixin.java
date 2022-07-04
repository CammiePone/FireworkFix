package dev.cammiescorner.fireworkfrenzy.mixin;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityNoRandomMixin extends ProjectileEntity implements FlyingItemEntity {
    @Shadow private int lifeTime;
    @Shadow @Final private static TrackedData<ItemStack> ITEM;

    public FireworkRocketEntityNoRandomMixin(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }
    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    void fireworkfrenzy$recalculateWithoutRandom(World world, double x, double y, double z, ItemStack stack,CallbackInfo info)
    {
        if(!FireworkFrenzy.config.disableFireworkRandomness)
            return;
        int i = 1;
        if (!stack.isEmpty() && stack.hasNbt()) {
            this.dataTracker.set(ITEM, stack.copy());
            i += stack.getOrCreateSubNbt("Fireworks").getByte("Flight");
        }
        //original code looks like this,
        //this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
        //this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
        this.setVelocity(0, 0.05, 0);
        this.lifeTime = 10 * i + 6;
    }
}
