package dev.cammiescorner.fireworkfrenzy.common.entities;

import dev.cammiescorner.fireworkfrenzy.FireworkFrenzy;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Holder;
import net.minecraft.world.World;

import java.util.List;

public class DamageCloudEntity extends AreaEffectCloudEntity {
	public DamageCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void tick() {
		super.tick();

		List<LivingEntity> affectedEntities = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox());

		for(LivingEntity target : affectedEntities)
			if(age % 10 == 0)
				target.damage(FireworkFrenzy.damageCloud(this, getOwner()), 4);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityDimensions.changing(getRadius() * 2, getRadius() * 2);
	}
}
