package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.JoystickHelper;
import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class TeleopWorker {

	private final RobotHardware r;
	private JoystickHelper joysticks;

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
		joysticks = new JoystickHelper(r);
	}

	/**
	 * Teleop periodic
	 */
	public void periodic() {

		// read the joysticks and calculate drive variables
		joysticks.read();

		// drive the motors from those drive variables
		joysticks.drive(r.motors());

		// TODO read all the other joystick buttons and drive some motors
		
	}

}
