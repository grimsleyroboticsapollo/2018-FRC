package org.usfirst.frc.team3215.robot;

import java.util.ArrayList;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * All functions to drive the motors.
 *
 */
public class MotorHelper {

	private final RobotHardware r;

	// 10 motor controllers
	private ArrayList<SpeedController> motors = new ArrayList<SpeedController>(10);

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
		motors.add(8, new Victor(8));
		motors.add(9, new Victor(9));

		// initialize Mecanum library
		// argument order: frontLeft, rearLeft, frontRight, rearRight
		// TODO mecanumDrive = new MecanumDrive(motor0, motor1, motor2, motor3);
	}
	
	public void stopAllMotors() {
		for (SpeedController thisMotor : motors) {
			thisMotor.stopMotor();
		}
	}

	// TODO implement all functions

}
