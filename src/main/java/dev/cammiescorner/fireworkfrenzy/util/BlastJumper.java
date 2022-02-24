package dev.cammiescorner.fireworkfrenzy.util;

public interface BlastJumper {
	boolean isBlastJumping();
	void setBlastJumping(boolean isBlastJumping);

	int getTimeOnGround();
	void setTimeOnGround(int timer);
}
