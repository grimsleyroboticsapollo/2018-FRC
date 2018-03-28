package org.usfirst.frc.team3215.robot;

import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.AnglesHelper;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JoystickHelper {

	private final RobotHardware r;

	public JoystickHelper(RobotHardware r) {
		this.r = r;

		r.joystick0().setRumble(RumbleType.kLeftRumble, 0);
		r.joystick0().setRumble(RumbleType.kRightRumble, 0);
		r.joystick1().setRumble(RumbleType.kLeftRumble, 0);
		r.joystick1().setRumble(RumbleType.kRightRumble, 0);
	}

	private double targetDriveDirection;
	private double targetOrientationAngle;
	private double driveSpeed;
	private double turnSpeed;
	private final static double JOY_THRESHOLD = 0.1;
	private final static double SLOW_FACTOR = 3.;
	private final static double REGULAR_FACTOR = 2.;
	private final static double FAST_FACTOR = 1.;
	private final static double TRIGGER_SCALING_FACTOR = 15;
	private boolean turnAsYouGo = true;

	private final static double RAD_TO_DEGREES = 180. / Math.PI;

	// raw axis numbers
	private final static int AXIS_LEFT_X = 0;
	private final static int AXIS_LEFT_Y = 1;
	private final static int AXIS_TRIGGER_LEFT = 2;
	private final static int AXIS_TRIGGER_RIGHT = 3;
	private final static int AXIS_RIGHT_X = 4;
	private final static int AXIS_RIGHT_Y = 5;

	/**
	 * Given a joystick position, calculates the angle where it is pointing at.
	 *
	 * @param joyX
	 *            The joystick x-axis position. 0 = neutral, -1.0 is left, and +1.0
	 *            is right
	 * @param joyY
	 *            The joystick y-axis position. 0 = neutral, -1.0 is down, and +1.0
	 *            is up
	 * @return Angle in degrees, between 0 (including) and 360 (excluding) degrees.
	 *         0 degrees is considered "forward", 90 is "left", and 270 is "right".
	 *         <p>
	 *         Usage exmample:
	 *         <p>
	 *         double angle = JoystickHelper.getAngle(leftX, leftY);
	 */
	public double getAngle(double joyX, double joyY) {

		double joyXabs = Math.abs(joyX);
		boolean joyXpositive = (joyX > 0);
		double joyYabs = Math.abs(joyY);
		boolean joyYpositive = (joyY > 0);

		if (joyY == 0) {
			if (joyXpositive) {
				return 270.;
			} else {
				return 90.;
			}
		}

		if (joyX == 0) {
			if (joyYpositive) {
				return 0.;
			} else {
				return 180.;
			}
		}

		if (joyXpositive) {
			// 3rd or 4th quadrant
			if (joyYpositive) {
				// 4th quadrant
				return 270. + Math.atan(joyYabs / joyXabs) * RAD_TO_DEGREES;
			} else {
				return 180. + Math.atan(joyXabs / joyYabs) * RAD_TO_DEGREES;
			}
		} else {
			// 1st or 2nd quadrant
			if (joyYpositive) {
				// 1st quadrant
				return 0. + Math.atan(joyXabs / joyYabs) * RAD_TO_DEGREES;
			} else {
				return 90. + Math.atan(joyYabs / joyXabs) * RAD_TO_DEGREES;
			}
		}
	}

	/**
	 * Reads the joystick positions and calculates the intended drive target
	 * direction, speed at which to drive there, target orientation, and the speed
	 * at which to get to that direction.
	 * 
	 * Joystick hierarchy is as follows (first condition met wins):
	 * 
	 * Drive target direction:
	 * 
	 * Joystick 0 left Hand direction given? -> Drive there in field orientation.
	 * Joystick 0 POV direction given? -> Drive there in robot orientation. Joystick
	 * 1 left Hand direction given? -> Drive slowing there in robot orientation.
	 * 
	 * Target orientation:
	 * 
	 * Joystick 0 right Hand direction given? -> Point there in field orientation.
	 * Joystick 0 left Hand direction given and "turn as you go" is on? -> Point
	 * there in field orientation. Joystick 0 left/right trigger is pushed? -> Turn
	 * in robot orientation based on how deeply pushed. Joystick 1 right Hand moved
	 * to left/right direction? Turn in robot orientation based on how far moved.
	 * 
	 * Drive speed and turn speed for joystick 0 are first calculated based on how
	 * far the Hands are moved, and then left unchanged if the right bumper ("drive
	 * fast") is pushed, divided by a certain factor if no bumper is pressed
	 * ("normal"), and divide by a certain greater factor if the left bumper is
	 * pressed ("slow speed"). Speeds from joystick 1 is always the same (slow).
	 */
	public void read() {

		// (1) determine target drive direction and drive speed

		double joy0x = r.joystick0().getRawAxis(AXIS_LEFT_X);
		double joy0y = -r.joystick0().getRawAxis(AXIS_LEFT_Y);
		double joy0angle = getAngle(joy0x, joy0y);
		double joy0speed = Math.sqrt(joy0x * joy0x + joy0y * joy0y);

		double joy0rightX = r.joystick0().getRawAxis(AXIS_RIGHT_X);
		double joy0rightY = -r.joystick0().getRawAxis(AXIS_RIGHT_Y);
		double joy0rightAngle = getAngle(joy0rightX, joy0rightY);
		double joy0rightSpeed = Math.sqrt(joy0rightX * joy0rightX + joy0rightY * joy0rightY);

		double joy1x = r.joystick1().getRawAxis(AXIS_LEFT_X);
		double joy1y = -r.joystick1().getRawAxis(AXIS_LEFT_Y);
		double joy1angle = getAngle(joy1x, joy1y);
		double joy1speed = Math.sqrt(joy1x * joy1x + joy1y * joy1y);

		double joy1rightX = r.joystick1().getRawAxis(AXIS_RIGHT_X);
		double joy1rightY = -r.joystick1().getRawAxis(AXIS_RIGHT_Y);
		double joy1rightAngle = getAngle(joy1rightX, joy1rightY);
		double joy1rightSpeed = Math.sqrt(joy1rightX * joy1rightX + joy1rightY * joy1rightY);

		boolean joy0leftBumper = r.joystick0().getRawButton(5);
		boolean joy0rightBumper = r.joystick0().getRawButton(6);
		double joy0leftTrigger = r.joystick0().getRawAxis(AXIS_TRIGGER_LEFT);
		double joy0rightTrigger = r.joystick0().getRawAxis(AXIS_TRIGGER_RIGHT);
		double effectiveTrigger = joy0leftTrigger - joy0rightTrigger;
		boolean joy0buttonA = r.joystick0().getRawButton(1);
		boolean joy0buttonB = r.joystick0().getRawButton(2);
		boolean joy0buttonX = r.joystick0().getRawButton(3);
		boolean joy0buttonY = r.joystick0().getRawButton(4);

		double speedFactor;

		if (joy0leftBumper) {
			speedFactor = SLOW_FACTOR;
		} else if (joy0rightBumper) {
			speedFactor = FAST_FACTOR;
		} else {
			speedFactor = REGULAR_FACTOR;
		}

		if (joy0speed > JOY_THRESHOLD) {

			targetDriveDirection = joy0angle;
			driveSpeed = joy0speed / speedFactor;
		} else if (r.joystick0().getPOV() >= 0) {

			double povAngle = 360. - r.joystick0().getPOV();
			targetDriveDirection = r.imu().getHeadingBestTwoOfThree() + povAngle;
			driveSpeed = 1. / speedFactor;
		} else if (joy1speed > JOY_THRESHOLD) {

			targetDriveDirection = r.imu().getHeadingBestTwoOfThree() + joy1angle;
			driveSpeed = joy1speed / SLOW_FACTOR;
		} else {
			driveSpeed = 0;
		}

		// (2) determine target orientation angle and turn speed

		if (joy0rightSpeed > JOY_THRESHOLD) {

			targetOrientationAngle = joy0rightAngle;
			turnSpeed = joy0rightSpeed / speedFactor;
		} else if ((joy0speed > JOY_THRESHOLD) && turnAsYouGo) {

			targetOrientationAngle = joy0angle;
			turnSpeed = joy0speed / speedFactor;
		} else if ((joy0leftTrigger > JOY_THRESHOLD) || (joy0rightTrigger > JOY_THRESHOLD)) {

			targetOrientationAngle = r.imu().getHeadingBestTwoOfThree() + effectiveTrigger * TRIGGER_SCALING_FACTOR;
			turnSpeed = Math.abs(effectiveTrigger);
		} else if (Math.abs(joy1rightX) > JOY_THRESHOLD) {

			targetOrientationAngle = r.imu().getHeadingBestTwoOfThree() - joy1rightX * TRIGGER_SCALING_FACTOR;
			turnSpeed = Math.abs(joy1rightX);
		} else {

			targetOrientationAngle = r.imu().getHeadingMvgAvg90();
			turnSpeed = driveSpeed;
			// if the robot is driving (in robot orientation) try to hold the orientation
		}

		// Turn as you go

		if (joy0buttonA || joy0buttonB) {

			turnAsYouGo = true;
		} else if (joy0buttonX || joy0buttonY) {

			turnAsYouGo = false;
		}

		// (3) limit speeds to within the allowable interval [0, 1.0]
		if (driveSpeed > 1.0)
			driveSpeed = 1.;

		if (driveSpeed < 0)
			driveSpeed = 0;

		if (turnSpeed > 1.0)
			turnSpeed = 1.;

		if (turnSpeed < .0)
			turnSpeed = .0;

		// (4) limit angle to within the angle convention [0, 360[
		targetDriveDirection = AnglesHelper.getPlainAngle(targetDriveDirection);
		targetOrientationAngle = AnglesHelper.getPlainAngle(targetOrientationAngle);

		// (5) all done - put values on dashboard for troubleshooting
		SmartDashboard.putNumber("targetDriveDirection", ((int) (100 * targetDriveDirection)) / 100.);
		SmartDashboard.putNumber("driveSpeed", ((int) (100 * driveSpeed)) / 100.);
		SmartDashboard.putNumber("targetOrientationAngle", ((int) (100 * targetOrientationAngle)) / 100.);
		SmartDashboard.putNumber("turnSpeed", ((int) (100 * turnSpeed)) / 100.);
		SmartDashboard.putBoolean("turnAsYouGo", turnAsYouGo);
	}

	/**
	 * Passing the drive variables computed earlier to the motor helper to actually
	 * drive the motors.
	 */
	public void drive(MotorHelper motorHelper) {
		motorHelper.drive(targetDriveDirection, driveSpeed, targetOrientationAngle, turnSpeed);
	}

}
