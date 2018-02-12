package org.usfirst.frc.team3215.robot.config;

import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc.team3215.robot.libraries.BNO055;
import org.usfirst.frc.team3215.robot.libraries.ImuThread;
import org.usfirst.frc.team3215.robot.libraries.LogHelper;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark; // Spark Motor Controller
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor; // VEX Motor Controller
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * This class holds all hardware information for the robot, to be used in all
 * stages of the robot code (init, autonomous init, autonomous periodic, and
 * teleop periodic).
 */
public class RobotHardware {

	private boolean robotHardwareIsInitialized = false;

	// Constants
	private final static int CAMERA_RESOLUTION_X = 640;
	private final static int CAMERA_RESOLUTION_Y = 480;
	private final static int CAMERA_EXPOSURE_DEFAULT = 75;

	// Joysticks
	private Joystick joystick0 = new Joystick(0);
	private Joystick joystick1 = new Joystick(1);
	private Joystick Joystick2 = new Joystick(2);

	// Motors
	private SpeedController motor0 = new Victor(0); // connected to PWM port 0
	private SpeedController motor1 = new Victor(1); // connected to PWM port 1
	private SpeedController motor2 = new Victor(2); // connected to PWM port 2
	private SpeedController motor3 = new Victor(3); // connected to PWM port 3
	private SpeedController motor4 = new Victor(4); // connected to PWM port 4
	private SpeedController motor5 = new Victor(5); // connected to PWM port 5
	private SpeedController motor6 = new Victor(6); // connected to PWM port 6
	private SpeedController motor7 = new Victor(7); // connected to PWM port 7
	private SpeedController motor8 = new Spark(8); // connected to PWM port 8 (Spark)
	private SpeedController motor9 = new Spark(9); // connected to PWM port 9 (Spark)

	private Set<SpeedController> allMotors = new HashSet<SpeedController>();

	private MecanumDrive mecanumDrive;
	
	// camera, sensors
	UsbCamera usbCamera;
	private BNO055 imu;
	private ImuThread imuThread;

	// status variables
	private SendableChooser<AutonomousModes> autonomousChooser; // the chooser as shown on the dashboard

	// other
	private LogHelper logHelper = new LogHelper();

	// one-time initialization
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

					robotHardwareIsInitialized = true;

					// initialize helper sets
					allMotors.add(motor0);
					allMotors.add(motor1);
					allMotors.add(motor2);
					allMotors.add(motor3);
					allMotors.add(motor4);
					allMotors.add(motor5);
					allMotors.add(motor6);
					allMotors.add(motor7);
					allMotors.add(motor8);
					allMotors.add(motor9);
					
					// initialize Mecanum library
					// argument order: frontLeft, rearLeft, frontRight, rearRight
					// TODO mecanumDrive = new MecanumDrive(motor0, motor1, motor2, motor3);

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

	public Joystick joystick2() {
		return Joystick2;
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
	
	public void stopAllMotors() {
		for (SpeedController thisMotor: allMotors) {
			thisMotor.stopMotor();
		}
	}
	
	public MecanumDrive mecanum() {
		return mecanumDrive;
	}

}
