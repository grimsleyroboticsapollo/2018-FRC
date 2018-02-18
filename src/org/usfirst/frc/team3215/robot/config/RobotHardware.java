package org.usfirst.frc.team3215.robot.config;

import org.usfirst.frc.team3215.robot.MotorHelper;
import org.usfirst.frc.team3215.robot.libraries.BNO055;
import org.usfirst.frc.team3215.robot.libraries.DiagnosticLightHelper;
import org.usfirst.frc.team3215.robot.libraries.ImuThread;
import org.usfirst.frc.team3215.robot.libraries.LogHelper;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class holds hardware information for the robot, to be used in all stages
 * of the robot code (init, autonomous init, autonomous periodic, and teleop
 * periodic).
 * 
 * For motors, see MotorHelper.java
 */
public class RobotHardware {

	private boolean robotHardwareIsInitialized = false;

	// Constants
	private final static int CAMERA_RESOLUTION_X = 640;
	private final static int CAMERA_RESOLUTION_Y = 480;
	private final static int CAMERA_EXPOSURE_DEFAULT = 75;

	// Motors (see MotorHelper.java for motor-specific functionality)
	private MotorHelper motors;
		
	// Joysticks
	private Joystick joystick0 = new Joystick(0);
	private Joystick joystick1 = new Joystick(1);

	// diagnostic light (on/off) on DIO port 0
	DigitalOutput diagnosticLight = new DigitalOutput(0);

	// camera, sensors
	UsbCamera usbCamera;
	private BNO055 imu;
	private ImuThread imuThread;

	// the chooser as shown on the SmartDashboard
	private SendableChooser<AutonomousModes> autonomousChooser;

	// other
	private LogHelper logHelper = new LogHelper();
	private DiagnosticLightHelper diagnosticHelper;

	// one-time initialization - must restart program in order to force another
	// initialization
	public void init() {
		System.out.println("RobotHardware.init()");

		if (!robotHardwareIsInitialized) {
			synchronized (this) {
				if (!robotHardwareIsInitialized) {

					// create the autonomous select values and put them on the dashboard
					log("RobotHardware.init() - put autonomous choices to dashboard");
					autonomousChooser = new SendableChooser<>();

					for (AutonomousModes thisMode : AutonomousModes.values()) {
						if (thisMode == AutonomousModes.NOTHING) {
							autonomousChooser.addDefault(thisMode.toString(), thisMode);
						} else {
							autonomousChooser.addObject(thisMode.toString(), thisMode);
						}
					}

					SmartDashboard.putData("Autonomous", autonomousChooser);

					// initialize the camera
					log("RobotHardware.init() - initialize USB camera");
					usbCamera = CameraServer.getInstance().startAutomaticCapture();
					usbCamera.setResolution(CAMERA_RESOLUTION_X, CAMERA_RESOLUTION_Y);
					usbCamera.setExposureManual(CAMERA_EXPOSURE_DEFAULT);

					// kick off IMU initialization (will complete later)
					log("RobotHardware.init() - initialize IMU");
					imu = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS, BNO055.vector_type_t.VECTOR_EULER);
					imuThread = new ImuThread(logHelper, imu);
					imuThread.setDaemon(true);
					imuThread.start();

					// kick off the diagnostic helper (light)
					log("RobotHardware.init() - initialize diagnostic light");
					diagnosticHelper = new DiagnosticLightHelper(this);
					diagnosticHelper.setDaemon(true);
					diagnosticHelper.start();

					// initialize motors
					log("RobotHardware.init() - initialize motors");
					motors = new MotorHelper(this);

					// all done!
					robotHardwareIsInitialized = true;

					log("RobotHardware.init() - initialization complete");
				}
			}
		}

	}

	// accessors

	/**
	 * Get the selected autonomous mode (from the SmartDashboard)
	 */
	public AutonomousModes selectedAutonomous() {
		return autonomousChooser.getSelected();
	}

	/**
	 * Returns whether the IMU has finished initialization
	 */
	public boolean isImuInitialized() {
		return imuThread.getImuIsInitialized();
	}

	/**
	 * Once initialized, returns IMU calibration data.
	 */
	public BNO055.CalData imuCalibration() {
		if (!isImuInitialized()) {
			return null;
		} else {
			return imu.getCalibration();
		}
	}

	/**
	 * Access to IMU measurements are through the periodic IMU thread.
	 */
	public ImuThread imu() {
		return imuThread;
	}

	public Joystick joystick0() {
		return joystick0;
	}

	public Joystick joystick1() {
		return joystick1;
	}

	public UsbCamera usbCamera() {
		return usbCamera;
	}

	public void log(String message) {
		logHelper.print(message);
	}

	public void logOnce(String message) {
		logHelper.printOnce(message);
	}

	public void logResetTimer() {
		logHelper.resetTimer();
	}

	public void logResetLogOnceMessages() {
		logHelper.resetLogOnceMessages();
	}

	public MotorHelper motors() {
		return motors;
	}

	public void setDiagnosticLight(boolean setOn) {
		diagnosticLight.set(setOn);
	}

	public void setDiagnosticLights(int shortFlashes, int longFlashes) {
		diagnosticHelper.setFlashes(shortFlashes, longFlashes);
	}

}
