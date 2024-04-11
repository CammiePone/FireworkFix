package dev.cammiescorner.fireworkfrenzy.client.sound;

import dev.cammiescorner.fireworkfrenzy.common.util.BlastJumper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.random.RandomGenerator;

public class BlastJumpingSoundInstance extends MovingSoundInstance {
	private final ClientPlayerEntity player;
	private int tickCount;

	public BlastJumpingSoundInstance(ClientPlayerEntity player) {
		super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, RandomGenerator.createLegacy());
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
	}

	public void tick() {
		++this.tickCount;

		if(!this.player.isRemoved() && !this.player.isFallFlying() && (this.tickCount <= 20 || (this.player instanceof BlastJumper jumper && jumper.isBlastJumping()))) {
			this.x = this.player.getX();
			this.y = this.player.getY();
			this.z = this.player.getZ();
			float f = (float) this.player.getVelocity().lengthSquared();
			this.volume = f / 4F;
			this.pitch = 1F;
		}
		else {
			this.setDone();
		}
	}
}
