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

	public FramerateHelper(RobotHardware r) {
		this.r = r;
	}

	public boolean run() {
		long currentTime = System.currentTimeMillis();
		lastStartTime = currentTime;
		long elapsedTime = currentTime - lastStartTime;
		if (elapsedTime < 50) {
			return false;
		}
		if (elapsedTime > 100) {
			r.log("Warning: Elapsed time " + elapsedTime);
			return true;
		}
		return true;
	}
}
