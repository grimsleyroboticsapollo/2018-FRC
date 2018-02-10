package org.usfirst.frc.team3215.robot;

import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.worker.AutonomousWorker;
import org.usfirst.frc.team3215.robot.worker.TeleopWorker;
import org.usfirst.frc.team3215.robot.worker.TestWorker;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	private final static String LOG_INIT = "==================== ";
	private final static String LOG_INIT_FINISHED = ".   .   .   .   .   .";

	RobotHardware r = new RobotHardware();
	AutonomousWorker autonomous = null;
	TeleopWorker teleop = null;
	TestWorker test = null;

	// =================================
	// GLOBAL
	// =================================

	@Override
	public void robotInit() {
		r.logResetTimer();
		r.logResetLogOnceMessages();
		r.log(LOG_INIT + "robotInit()");

		// master initialization of robot hardware and sensors
		r.init();
		r.stopAllMotors();
		r.imu().resetZeroHeadingCalibration();

		r.log(LOG_INIT_FINISHED + "robotInit() finished.");
	}

	@Override
	public void robotPeriodic() {
		r.logOnce(LOG_INIT + "robotPeriodic() started the first time.");

		r.logOnce(LOG_INIT_FINISHED + "robotPeriodic() finished the first time.");
	}

	@Override
	public void disabledInit() {
		r.log(LOG_INIT + "disabledInit()");

		// execution (autonomous, test, or teleop) has been stopped
		r.logResetLogOnceMessages();
		r.stopAllMotors();
		r.imu().resetZeroHeadingCalibration();

		r.log(LOG_INIT_FINISHED + "disabledInit() finished.");
	}

	@Override
	public void disabledPeriodic() {
		r.logOnce(LOG_INIT + "disabledPeriodic() started the first time.");

		r.logOnce(LOG_INIT_FINISHED + "disabledPeriodic() finished the first time.");
	}

	// =================================
	// AUTONOMOUS
	// =================================

	@Override
	public void autonomousInit() {
		r.logResetTimer();
		r.log(LOG_INIT + "autonomousInit()");

		r.log("calibrating IMU zero degree heading to: " + String.valueOf(r.imu().getHeadingMvgAvg90()));
		r.imu().calibrateZeroHeading();

		autonomous = new AutonomousWorker(r);
		autonomous.init();

		r.log(LOG_INIT_FINISHED + "autonomousInit() finished.");
	}

	@Override
	public void autonomousPeriodic() {
		r.logOnce(LOG_INIT + "autonomousPeriodic() started the first time.");

		autonomous.periodic();

		r.logOnce(LOG_INIT_FINISHED + "autonomousPeriodic() finished the first time.");
	}

	// =================================
	// TELEOP
	// =================================

	@Override
	public void teleopInit() {
		r.logResetTimer();
		r.log(LOG_INIT + "teleopInit()");

		r.log("calibrating IMU zero degree heading to: " + String.valueOf(r.imu().getHeadingMvgAvg90()));
		r.imu().calibrateZeroHeading();

		teleop = new TeleopWorker(r);
		teleop.init();

		r.log(LOG_INIT_FINISHED + "teleopInit() finished.");
	}

	@Override
	public void teleopPeriodic() {
		r.logOnce(LOG_INIT + "teleopPeriodic() started the first time.");

		teleop.periodic();

		r.logOnce(LOG_INIT_FINISHED + "teleopPeriodic() finished the first time.");
	}

	// =================================
	// TEST
	// =================================

	@Override
	public void testInit() {
		r.logResetTimer();
		r.log(LOG_INIT + "testInit()");

		r.log("calibrating IMU zero degree heading to: " + String.valueOf(r.imu().getHeadingMvgAvg90()));
		r.imu().calibrateZeroHeading();

		test = new TestWorker(r);
		test.init();

		r.log(LOG_INIT_FINISHED + "testInit() finished.");
	}

	@Override
	public void testPeriodic() {
		r.logOnce(LOG_INIT + "testPeriodic() started the first time.");

		test.periodic();

		r.logOnce(LOG_INIT_FINISHED + "testPeriodic() finished the first time.");
	}

}