package org.usfirst.frc.team3215.robot.libraries;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

/**
 * Limits the time interval between two periodic calls to a predictable
 * interval. This will aid e.g. in motor protection to slow down the rate when
 * the driver commands a full speed reversal.
 */
public class FramerateHelper {

	private RobotHardware r;
	private long lastStartTime = 0;
	private final static long FRAME_TIME = 50;

	public FramerateHelper(RobotHardware r) {
		this.r = r;
	}

	public boolean run() {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - lastStartTime;
		
		
		if (elapsedTime < FRAME_TIME) {
			return false;
		}
		if (elapsedTime > (2 * FRAME_TIME)) {
			lastStartTime = currentTime;
			r.log("Warning: Elapsed time " + elapsedTime);
			return true;
		}
		lastStartTime = currentTime;
		return true;
	}
}
