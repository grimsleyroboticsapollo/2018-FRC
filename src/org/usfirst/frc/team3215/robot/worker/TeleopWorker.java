package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.JoystickHelper;
import org.usfirst.frc.team3215.robot.MotorHelper;
import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.ExceptionHelper;

public class TeleopWorker {
	private final static int AXIS_TRIGGER_LEFT = 2;
	private final static int AXIS_TRIGGER_RIGHT = 3;

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

			// winch buttons!
			double joy1leftTrigger = r.joystick1().getRawAxis(AXIS_TRIGGER_LEFT);
			double joy1rightTrigger = r.joystick1().getRawAxis(AXIS_TRIGGER_RIGHT);
			if ((joy1leftTrigger > 0.1) && (joy1rightTrigger > 0.1)) {
				double winchSpeed = (joy1leftTrigger + joy1rightTrigger) / 2.0;
				r.motors().linear(MotorHelper.WINCH, winchSpeed);

			} else {
				r.motors().linear(MotorHelper.WINCH, 0);
			}

			// TODO read all the other joystick buttons and drive some motors

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

}
