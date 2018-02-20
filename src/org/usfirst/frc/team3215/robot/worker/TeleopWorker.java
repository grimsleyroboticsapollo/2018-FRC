package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.JoystickHelper;
import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.ExceptionHelper;

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

		try {

			joysticks = new JoystickHelper(r);

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}
	}

	/**
	 * Teleop periodic
	 */
	public void periodic() {

		try {
			// read the joysticks and calculate drive variables
			joysticks.read();

			// drive the motors from those drive variables
			joysticks.drive(r.motors());

			// TODO read all the other joystick buttons and drive some motors

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

}
