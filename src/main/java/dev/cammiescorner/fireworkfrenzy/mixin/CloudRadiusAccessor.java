package dev.cammiescorner.fireworkfrenzy.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AreaEffectCloudEntity.class)
public interface CloudRadiusAccessor {
	@Accessor("RADIUS")
	static TrackedData<Float> getRadiusTracker() {
		return null;
	}
}
