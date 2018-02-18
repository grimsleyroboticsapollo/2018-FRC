package org.usfirst.frc.team3215.robot.libraries;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class DiagnosticLightHelper extends Thread {

	public final static int ROBOT_INIT_SHORT = 1;
	public final static int ROBOT_DISABLED_SHORT = 2;
	public final static int ROBOT_AUTONOMOUS_SHORT = 3;
	public final static int ROBOT_TELEOP_SHORT = 4;
	
	private final static long TIME_SHORT = 120;
	private final static long TIME_LONG = 320;
	private final static long TIME_PAUSE = 500;

	private final RobotHardware r;
	private int shortFlashes = 0;
	private int longFlashes = 0;

	public DiagnosticLightHelper(RobotHardware r) {
		this.r = r;

		r.log("DiagnosticLightHelper constructor");
	}

	@Override
	public void run() {

		while (!Thread.interrupted()) {

			for (int i = 0; i < shortFlashes; i++) {
				r.setDiagnosticLight(true);
				nap(TIME_SHORT);
				r.setDiagnosticLight(false);
				nap(TIME_SHORT);
			}

			nap(TIME_PAUSE);

			for (int i = 0; i < longFlashes; i++) {
				r.setDiagnosticLight(true);
				nap(TIME_LONG);
				r.setDiagnosticLight(false);
				nap(TIME_LONG);
			}

			nap(TIME_PAUSE);

		}
	}

	private void nap(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore and let bubble through
		}
	}

	public void setShortFlashes(int shortFlashes) {
		this.shortFlashes = shortFlashes;
	}

	public void setLongFlashes(int longFlashes) {
		this.longFlashes = longFlashes;
	}

	public void setFlashes(int shortFlashes, int longFlashes) {
		this.shortFlashes = shortFlashes;
		this.longFlashes = longFlashes;
	}

}
