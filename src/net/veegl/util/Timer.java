package net.veegl.util;

import org.lwjgl.Sys;

public class Timer
{
	
	private long lastFrame;
	private int  fps;
	private int realFPS;
	private long lastFPS;
	
	
	public Timer()
	{
		getDelta();
		lastFPS = getTime();
	}
	
	
	public int getFps() {
		if(realFPS == 0)
			return 1;
		return realFPS;
	}



	public void tick() {
		if (getTime() - lastFPS > 1000) {
			realFPS = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
}