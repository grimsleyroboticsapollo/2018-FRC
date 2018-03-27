package org.usfirst.frc.team3215.robot;

import java.util.ArrayList;

import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.AnglesHelper;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * All functions to drive the motors.
 */
public class MotorHelper {

	// Constants for PWM channel
	public final static int LIFT = 4;
	public final static int CUBIE_DEPLOY = 5;
	public final static int CUBIE_INTAKE = 6;
	public final static int WINCH = 7;

	public final static double THRESHOLD_ANGLE = 40;
	private final static double DEGREES_TO_RAD = Math.PI / 180.;
	public final static double SPEED_INCREMENT = 0.1;
	private final RobotHardware r;

	// 10 motor controllers
	private ArrayList<SpeedController> motors = new ArrayList<SpeedController>(10);
	private double previousSpeed[] = new double[10];
	// reference to FRC mecanum wheel library
	private MecanumDrive mecanumDrive;

	public MotorHelper(RobotHardware r) {
		this.r = r;

		// initialize motors

		motors.add(0, new Spark(0)); // Front-left Mecanum connected to PWM port 0
		motors.add(1, new Spark(1)); // Rear-left Mecanum connected to PWM port 1
		motors.add(2, new Spark(2)); // Front-right Mecanum connected to PWM port 2
		motors.add(3, new Spark(3)); // Rear-right Mecanum connected to PWM port 3
		motors.add(4, new Victor(4));
		motors.add(5, new Victor(5));
		motors.add(6, new Victor(6));
		motors.add(7, new Victor(7));

		// initialize Mecanum library
		// argument order: frontLeft, rearLeft, frontRight, rearRight
		mecanumDrive = new MecanumDrive(motors.get(0), motors.get(1), motors.get(2), motors.get(3));
	}

	/**
	 * Helper to force stop all motors now.
	 */
	public void hardStopAllMotors() {
		for (SpeedController thisMotor : motors) {
			thisMotor.stopMotor();
		}
	}

	/**
	 * Helper to halt all motors gracefully now. Needs to be called several times
	 * (periodically) so that motors slow without jerking.
	 */
	public void haltAllMotorsPeriodic() {
		haltDriveMotorsPeriodic();
		haltLinearMotorsPeriodic();
	}

	/**
	 * Halts the drive motors gracefully (i.e. wihout jerking). Needs to be called
	 * periodically to execute slowdown properly until actual halt.
	 */
	public void haltDriveMotorsPeriodic() {
		drive(0, 0, 0, 0);
	}

	/**
	 * Halts all linear motors gracefully (i.e. without jerking). Needs to be called
	 * periodicatlly to execute slowdown properly until actual halt.
	 */
	public void haltLinearMotorsPeriodic() {
		linear(LIFT, 0);
		linear(WINCH, 0);
		linear(CUBIE_DEPLOY, 0);
		linear(CUBIE_INTAKE, 0);
	}

	/**
	 * Have the robot drive into a given direction, at a given speed, and have it
	 * orient towards a given angle, at a given rate of turn.
	 * 
	 * @param targetDriveDirection
	 *            The direction into which to drive (in field orientation). Forward
	 *            is 0, left is 90, back is 180, and right is 270 degrees.
	 * @param driveSpeed
	 *            The speed at which to drive. 0 is stopped, 1 is full speed.
	 * @param targetOrientationAngle
	 *            The field orientation angle into which to turn.
	 * @param turnSpeed
	 *            The rate of turn at which to aim for the target orientation. 0 is
	 *            no turn, 1 is fastest turn. Important: If you do care to have the
	 *            robot turn into a certain orientation, this value must be greater
	 *            than zero!
	 */
	public void drive(double targetDriveDirection, double driveSpeed, double targetOrientationAngle, double turnSpeed) {

		double currentAngle = r.imu().getHeadingBestTwoOfThree();
		double angleDifference = AnglesHelper.getAngleDifference(currentAngle, targetOrientationAngle);
		double effectiveTurnSpeed;

		if (Math.abs(angleDifference) < THRESHOLD_ANGLE) {
			if (Math.abs(angleDifference) > 10 && turnSpeed > 0.5) {
				turnSpeed = 0.5;
			} else if (Math.abs(angleDifference) > 1 && turnSpeed > 0.2) {
				turnSpeed = 0.2;
			} else if (Math.abs(angleDifference) <= 1) {
				turnSpeed = 0;
			}
		} else {
			driveSpeed = 0;
		}
		effectiveTurnSpeed = turnSpeed * Math.signum(angleDifference);

		double robotDriveDirection = -targetDriveDirection + currentAngle;
		if (Math.abs(effectiveTurnSpeed) < 0.05) {
			driveSpeed = driveSpeed * (2.
					- Math.cos(DEGREES_TO_RAD * robotDriveDirection) * Math.cos(DEGREES_TO_RAD * robotDriveDirection));
		}
		// Maybe implement check to prevent motor brownout
		mecanumDrive.drivePolar(driveSpeed, robotDriveDirection, -effectiveTurnSpeed);
	}

	/**
	 * Given a motor controller number and speed, drives that motor. This function
	 * implements gradual ramp-up / -down to avoid jerking.
	 * 
	 * @param motorNumber
	 *            The PWM port the motor controller is connected to.
	 * @param speed
	 *            The (target) speed at which to drive that motor, between -1. and
	 *            1.
	 */
	public void linear(int motorNumber, double speed) {
		double speedDifference = speed - previousSpeed[motorNumber];
		if (speedDifference > SPEED_INCREMENT) {
			speedDifference = SPEED_INCREMENT;
		}
		if (speedDifference < -SPEED_INCREMENT) {
			speedDifference = -SPEED_INCREMENT;
		}
		double effectiveSpeed = speedDifference + previousSpeed[motorNumber];
		if (effectiveSpeed > 1.0) {
			effectiveSpeed = 1.0;
		}
		if (effectiveSpeed < -1.0) {
			effectiveSpeed = -1.0;
		}
		previousSpeed[motorNumber] = effectiveSpeed;

		motors.get(motorNumber).set(effectiveSpeed);

	}

	/**
	 * Given a motor controller number and speed, drives that motor. This function
	 * implements gradual ramp-up / -down to avoid jerking. Also stops all other
	 * linear motors to avoid not stopping a linear motor from a previous autonomous
	 * action.
	 * 
	 * @param motorNumber
	 *            The PWM port the motor controller is connected to.
	 * @param speed
	 *            The (target) speed at which to drive that motor, between -1. and
	 *            1.
	 */
	public void linearSingle(int motorNumber, double speed) {

		// (1) drive the linear motor
		linear(motorNumber, speed);

		// (2) stop all others
		if (motorNumber != LIFT) {
			linear(LIFT, 0);
		}
		if (motorNumber != WINCH) {
			linear(WINCH, 0);
		}
		if (motorNumber != CUBIE_DEPLOY) {
			linear(CUBIE_DEPLOY, 0);
		}
		if (motorNumber != CUBIE_INTAKE) {
			linear(CUBIE_INTAKE, 0);
		}

	}

}
