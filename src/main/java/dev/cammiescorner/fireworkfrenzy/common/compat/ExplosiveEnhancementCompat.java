package dev.cammiescorner.fireworkfrenzy.common.compat;

import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.superkat.explosiveenhancement.ExplosiveEnhancement;
import net.superkat.explosiveenhancement.ExplosiveEnhancementClient;
import net.superkat.explosiveenhancement.api.ExplosiveApi;

public class ExplosiveEnhancementCompat {
	public static void spawnEnhancedBooms(World world, double x, double y, double z, float power) {
		boolean isUnderWater = false;
		BlockPos pos = BlockPos.create(x, y, z);

		if(ExplosiveEnhancementClient.config.underwaterExplosions && world.getFluidState(pos).isIn(FluidTags.WATER)) {
			isUnderWater = true;

			if(ExplosiveEnhancementClient.config.debugLogs)
				ExplosiveEnhancement.LOGGER.info("particle is underwater!");
		}

		ExplosiveApi.spawnParticles(world, x, y, z, power, isUnderWater, false, true);
		world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 4f, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2f) * 0.7f, false);
	}
}
