package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.JoystickHelper;
import org.usfirst.frc.team3215.robot.MotorHelper;
import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.ExceptionHelper;

public class TeleopWorker {
	private final static int AXIS_TRIGGER_LEFT = 2;
	private final static int AXIS_TRIGGER_RIGHT = 3;
	private boolean constantIntake = false;

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

			boolean joy1leftBumper = r.joystick1().getRawButton(5);
			boolean joy1rightBumper = r.joystick1().getRawButton(6);
			if (joy1leftBumper) {
				r.motors().linear(MotorHelper.CUBIE_INTAKE, .5);
				constantIntake = false;
			} else if (joy1rightBumper) {
				r.motors().linear(MotorHelper.CUBIE_INTAKE, -0.5);
				constantIntake = true;
			} else if (constantIntake == false) {
				r.motors().linear(MotorHelper.CUBIE_INTAKE, 0.0);
			} else {
				r.motors().linear(MotorHelper.CUBIE_INTAKE, -0.1);
			}

			int joy1POV = r.joystick1().getPOV();

			boolean joy1ExtendCubie = (joy1POV == 270) || (joy1POV == 225) || (joy1POV == 315);
			boolean joy1RetractCubie = (joy1POV == 90) || (joy1POV == 45) || (joy1POV == 135);
			if (joy1ExtendCubie) {
				r.motors().linear(MotorHelper.CUBIE_DEPLOY, 0.3);
			} else if (joy1RetractCubie) {
				r.motors().linear(MotorHelper.CUBIE_DEPLOY, -0.3);
			} else {
				r.motors().linear(MotorHelper.CUBIE_DEPLOY, 0);
			}

			boolean joy1liftUpButton = (joy1POV == 0) || (joy1POV == 315) || (joy1POV == 45);
			boolean joy1liftDownButton = (joy1POV == 180) || (joy1POV == 135) || (joy1POV == 225);
			if (joy1liftUpButton) {
				r.motors().linear(MotorHelper.LIFT, 1.0);
			} else if (joy1liftDownButton) {
				r.motors().linear(MotorHelper.LIFT, -1.0);
			} else {
				r.motors().linear(MotorHelper.LIFT, 0);
			}

			// TODO #JK joy 0 back+start button hold to reset IMU, release to recalibrate
			//
			// (1) make state of joy0 back+start button a private class variable
			// (2) if state changes from "released" to "pushed", reset IMU:
			//
			// r.imu().resetZeroHeadingCalibration();
			//
			// (3) if state changes from "pushed" to released, recalibrate IMU:
			//
			// r.imu().calibrateZeroHeading();

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

}
