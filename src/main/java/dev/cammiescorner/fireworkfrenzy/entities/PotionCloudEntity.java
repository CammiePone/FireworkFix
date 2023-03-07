package dev.cammiescorner.fireworkfrenzy.entities;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class PotionCloudEntity extends AreaEffectCloudEntity {
	public PotionCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityDimensions.changing(getRadius() * 2, getRadius() * 2);
	}
}
