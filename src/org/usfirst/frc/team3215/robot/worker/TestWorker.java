package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class TestWorker {

	private final RobotHardware r;

	/**
	 * Constructor - called during global init
	 */
	public TestWorker(RobotHardware r) {
		this.r = r;
		r.log("TestWorker() constructor");
	}

	/**
	 * Test init
	 */
	public void init() {

		r.log("init() - test");

	}

	/**
	 * Test periodic
	 */
	public void periodic() {

		// TODO

	}

}
