package dev.cammiescorner.fireworkfrenzy.common.util;

public interface BlastJumper {
	boolean isBlastJumping();
	void setBlastJumping(boolean isBlastJumping);

	int getTimeOnGround();
	void setTimeOnGround(int timer);
}
