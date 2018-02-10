package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class TeleopWorker {

	private final RobotHardware r;

	/**
	 * Constructor - called during global init
	 */
	public TeleopWorker(RobotHardware r) {
		this.r = r;
		r.log("TeleopWorker() constructor");
	}

	/**
	 * Teleop init
	 */
	public void init() {
		r.log("init() - teleop");

	}

	/**
	 * Teleop periodic
	 */
	public void periodic() {

		// TODO

	}

}
