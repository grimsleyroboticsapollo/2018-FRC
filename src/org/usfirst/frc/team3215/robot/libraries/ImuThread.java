package org.usfirst.frc.team3215.robot.libraries;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Background thread that reads out the IMU periodically without holding up the
 * code. Next to the current heading, it also computes exponential moving
 * averages as well as "best two of three" averages.
 */
public class ImuThread extends Thread {

	private final Object HEADING_MUTEX = new Object();

	private final BNO055 imu;
	private final LogHelper log;

	private double heading = 0; // most recent heading
	private double headingPrev1 = 0; // previous heading
	private double headingPrev2 = 0; // second previous heading
	private double headingMvgAvg50 = 0; // moving exponential average at 50% weight (changes faster)
	private double headingMvgAvg90 = 0; // moving exponential average at 90% weight (changes slowly)
	private double headingBestTwoOfThree = 0; // out of the three most recent measures, drop the outlier

	private double customCalibrationOffset = 0; // set this to calibrate "0" as starting position

	private boolean imuIsInitialized = false;
	private boolean imuIsCalibrated = false;

	/**
	 * Pass in the IMU object here. Initialization is expected to have been
	 * initiated outside.
	 */
	public ImuThread(LogHelper log, BNO055 imu) {
		this.log = log;
		this.imu = imu;

		log.print("ImuThread constructor");
	}

	@Override
	public void run() {

		while (!Thread.interrupted()) {

			synchronized (HEADING_MUTEX) {

				if (!imu.isSensorPresent()) {
					log.print("PANIC - IMU not present? check wiring");
				}

				if (!imuIsInitialized && imu.isInitialized()) {
					// IMU has finished initialization
					imuIsInitialized = true;
					log.print("IMU has finished initialization");
				}

				if (!imuIsCalibrated && imu.isCalibrated()) {
					// IMU has finished calibration and is ready for accurate readings
					imuIsCalibrated = true;
					BNO055.CalData imuCalibration = imu.getCalibration();
					log.print("IMU is calibrated and ready for accurate readings");
					log.print("- IMU Temp: " + imu.getTemp());
					log.print("- IMU " + imuCalibration);
				}

				if (imuIsInitialized) {

					headingPrev2 = headingPrev1;
					headingPrev1 = heading;
					heading = imu.getHeading() - customCalibrationOffset;

					// calculate the fast-moving exponential average
					headingMvgAvg50 = headingMvgAvg50 * 0.5 + heading * 0.5;

					// calculate the slow-moving (calibration) exponential average
					headingMvgAvg90 = headingMvgAvg90 * 0.9 + heading * 0.1;

					// Calculate the "best two of three" average: Find the two
					// measures that are closest to one another, and build their
					// linear average.
					double diff_0_1 = Math.abs(heading - headingPrev1);
					double diff_0_2 = Math.abs(heading - headingPrev2);
					double diff_1_2 = Math.abs(headingPrev2 - headingPrev1);
					if (diff_0_1 < diff_0_2) {
						if (diff_0_1 < diff_1_2) {
							// current and most recent heading are closest to one another
							headingBestTwoOfThree = (heading + headingPrev1) / 2.;
						} else {
							// current heading is outlier, take average of the previous ones
							headingBestTwoOfThree = (headingPrev1 + headingPrev2) / 2.;
						}
					} else {
						if (diff_0_2 < diff_1_2) {
							// current and 2nd recent heading are closest
							headingBestTwoOfThree = (heading + headingPrev2) / 2.;
						} else {
							// current heading is outlier, take average of the previous ones
							headingBestTwoOfThree = (headingPrev1 + headingPrev2) / 2.;
						}
					}

				} // ... if initialized

			} // ... synchronized

			SmartDashboard.putBoolean("IMU initialized", imuIsInitialized);
			SmartDashboard.putNumber("heading", ((int) (100 * heading)) / 100.);
			SmartDashboard.putNumber("headingMvgAvg90", ((int) (100 * headingMvgAvg90)) / 100.);
			SmartDashboard.putNumber("headingBestTwoOfThree", ((int) (100 * headingBestTwoOfThree)) / 100.);

			try {
				Thread.sleep(40); // must be larger than 20 [ms] due to sensor limitations
			} catch (InterruptedException e) {
				// ignore
			}

		}

	}

	/**
	 * Call this method after you know that the IMU has been still for at least one
	 * second. It will use the slow moving exponential average to fine tune its 0
	 * degree position.
	 */
	public void calibrateZeroHeading() {
		synchronized (HEADING_MUTEX) {
			customCalibrationOffset = headingMvgAvg90;
			heading = 0;
			headingPrev1 = 0;
			headingPrev2 = 0;
			headingMvgAvg50 = 0;
			headingMvgAvg90 = 0;
			headingBestTwoOfThree = 0;
		}
	}

	public void resetZeroHeadingCalibration() {
		customCalibrationOffset = 0;
	}

	/**
	 * Most recent heading (in degrees, without resetting). For example, after two
	 * full turns the value here will be 720.
	 */
	public double getHeading() {
		synchronized (HEADING_MUTEX) {
			return heading;
		}
	}

	/**
	 * Fast moving exponential average. Use for simple smoothing.
	 */
	public double getHeadingMvgAvg50() {
		synchronized (HEADING_MUTEX) {
			return headingMvgAvg50;
		}
	}

	/**
	 * Slow moving exponential average. Use for calibration.
	 */
	public double getHeadingMvgAvg90() {
		synchronized (HEADING_MUTEX) {
			return headingMvgAvg90;
		}
	}

	/**
	 * "Best two of three" average, takes the three most recent measures and
	 * discards the one that is farthest from the other two. Then returns the linear
	 * average of the other two.
	 */
	public double getHeadingBestTwoOfThree() {
		synchronized (HEADING_MUTEX) {
			return headingBestTwoOfThree;
		}
	}

	/**
	 * True if internal initialization of the IMU has completed.
	 */
	public boolean getImuIsInitialized() {
		synchronized (HEADING_MUTEX) {
			return imuIsInitialized;
		}
	}

}
