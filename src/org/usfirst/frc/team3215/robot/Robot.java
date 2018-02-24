package org.usfirst.frc.team3215.robot;

import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.DiagnosticLightHelper;
import org.usfirst.frc.team3215.robot.libraries.FramerateHelper;
import org.usfirst.frc.team3215.robot.worker.AutonomousWorker;
import org.usfirst.frc.team3215.robot.worker.TeleopWorker;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	private final static String LOG_INIT = "==================== ";
	private final static String LOG_INIT_FINISHED = ".   .   .   .   .   .";

	private RobotHardware r = new RobotHardware();
	private AutonomousWorker autonomous = null;
	private TeleopWorker teleop = null;
	private FramerateHelper frames = new FramerateHelper(r);

	private boolean imuZeroHeadingCalibrationIsReset = false;

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
		r.motors().hardStopAllMotors();
		r.imu().resetZeroHeadingCalibration();
		imuZeroHeadingCalibrationIsReset = true;

		// one short light, indicates that the program has started but is not (yet)
		// communicating with a driver station
		r.setDiagnosticLights(DiagnosticLightHelper.ROBOT_INIT_SHORT, 0);

		r.log(LOG_INIT_FINISHED + "robotInit() finished.");
	}

	@Override
	public void robotPeriodic() {
		r.logOnce(LOG_INIT + "robotPeriodic() started the first time.");

	}

	@Override
	public void disabledInit() {
		r.log(LOG_INIT + "disabledInit()");

		// execution (autonomous, test, or teleop) has been stopped
		r.logResetLogOnceMessages();
		r.motors().hardStopAllMotors();

		// two short lights, indicates that the program is started, communicating with a
		// driver station, and idling OK
		r.setDiagnosticLights(DiagnosticLightHelper.ROBOT_DISABLED_SHORT, 0);

		r.log(LOG_INIT_FINISHED + "disabledInit() finished.");
	}

	@Override
	public void disabledPeriodic() {
		r.logOnce(LOG_INIT + "disabledPeriodic() started the first time.");

	}

	// =================================
	// AUTONOMOUS
	// =================================

	@Override
	public void autonomousInit() {
		r.logResetTimer();
		r.log(LOG_INIT + "autonomousInit()");

		if (imuZeroHeadingCalibrationIsReset) {
			r.log("calibrating IMU zero degree heading to: " + String.valueOf(r.imu().getHeadingMvgAvg90()));
			r.imu().calibrateZeroHeading();
			imuZeroHeadingCalibrationIsReset = false;
		} else {
			r.log("IMU already calibrated, continue using existing calibration");
		}

		autonomous = new AutonomousWorker(r);
		autonomous.init();

		r.setDiagnosticLights(DiagnosticLightHelper.ROBOT_AUTONOMOUS_SHORT, 0); // 3 short lights; autonomous has
																				// initialized

		r.log(LOG_INIT_FINISHED + "autonomousInit() finished.");
	}

	@Override
	public void autonomousPeriodic() {
		r.logOnce(LOG_INIT + "autonomousPeriodic() started the first time.");
		if (frames.run()) {
			autonomous.periodic();
		}

	}

	// =================================
	// TELEOP
	// =================================

	@Override
	public void teleopInit() {
		r.logResetTimer();
		r.log(LOG_INIT + "teleopInit()");

		if (imuZeroHeadingCalibrationIsReset) {
			r.log("calibrating IMU zero degree heading to: " + String.valueOf(r.imu().getHeadingMvgAvg90()));
			r.imu().calibrateZeroHeading();
			imuZeroHeadingCalibrationIsReset = false;
		} else {
			r.log("IMU already calibrated, continue using existing calibration");
		}

		teleop = new TeleopWorker(r);
		teleop.init();

		r.setDiagnosticLights(DiagnosticLightHelper.ROBOT_TELEOP_SHORT, 0); // 4 short lights; teleop is initialized

		r.log(LOG_INIT_FINISHED + "teleopInit() finished.");
	}

	@Override
	public void teleopPeriodic() {
		r.logOnce(LOG_INIT + "teleopPeriodic() started the first time.");

		if (frames.run()) {
			r.logOnce("First frame run");

			teleop.periodic();
		}

	}

}