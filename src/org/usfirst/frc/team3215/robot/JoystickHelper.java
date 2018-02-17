package org.usfirst.frc.team3215.robot;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JoystickHelper {

	private RobotHardware r;

	public JoystickHelper(RobotHardware r) {
		this.r = r;
	}

	private double targetDriveDirection;
	private double targetOrientationAngle;
	private double driveSpeed;
	private double turnSpeed;
	private final static double JOY_THRESHOLD = 0.1;
	private final static double SLOW_FACTOR = 3.;
	private final static double REGULAR_FACTOR = 2.;
	private final static double FAST_FACTOR = 1.;

	private final static double RAD_TO_DEGREES = 180. / Math.PI;

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

		double joy0x = r.joystick0().getX(Hand.kLeft);
		double joy0y = -r.joystick0().getY(Hand.kLeft);
		double joy0angle = getAngle(joy0x, joy0y);
		double joy0speed = Math.sqrt(joy0x * joy0x + joy0y * joy0y);
		double joy1x = r.joystick1().getX(Hand.kLeft);
		double joy1y = -r.joystick1().getY(Hand.kLeft);
		double joy1angle = getAngle(joy1x, joy1y);
		double joy1speed = Math.sqrt(joy1x * joy1x + joy1y * joy1y);
		boolean leftBumper = r.joystick0().getRawButton(5);
		boolean rightBumper = r.joystick0().getRawButton(6);
		double speedFactor;
		if (leftBumper) {
			speedFactor = SLOW_FACTOR;
		} else if (rightBumper) {

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

		// TODO #JK implement orientation calculation

		// TODO #JK limit angle
		SmartDashboard.putNumber("targetDriveDirection", targetDriveDirection);
		SmartDashboard.putNumber("driveSpeed", driveSpeed);
	}

}
