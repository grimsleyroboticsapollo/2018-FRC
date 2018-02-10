package org.usfirst.frc.team3215.robot.worker;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class AutonomousWorker {

	private final RobotHardware r;

	/**
	 * Constructor - called during global init
	 */
	public AutonomousWorker(RobotHardware r) {
		this.r = r;
		r.log("AutonomousWorker() constructor");
	}

	/**
	 * Autonomous init
	 */
	public void init() {

		r.log("init() - selected autonomous: " + r.selectedAutonomous().toString());

		// TODO in the init(), set up the plan for each of the possible autonomous modes

		switch (r.selectedAutonomous()) {

		// --- when robot is standing on the LEFT side:
		case LEFT_DRIVE_AND_DROP:
			break;
		case LEFT_DRIVE_FORWARD:
			break;
		case LEFT_DRIVE_FORWARD_DELAYED:
			break;

		// --- when robot is standing in the MIDDLE:
		case MIDDLE_DRIVE_AND_DROP:
			break;
		case MIDDLE_DRIVE_TO_LEFT:
			break;
		case MIDDLE_DRIVE_TO_LEFT_DELAYED:
			break;
		case MIDDLE_DRIVE_TO_RIGHT:
			break;
		case MIDDLE_DRIVE_TO_RIGHT_DELAYED:
			break;

		// --- when robot is standing on the RIGHT side:
		case RIGHT_DRIVE_AND_DROP:
			break;
		case RIGHT_DRIVE_FORWARD:
			break;
		case RIGHT_DRIVE_FORWARD_DELAYED:
			break;

		// --- special functions:
		case SELF_TEST:
			break;
		case NOTHING:
			break;

		default:
			r.log("PANIC - unexpected autonomous: " + r.selectedAutonomous());
			break;
		}

	}

	/**
	 * Autonomous periodic
	 */
	public void periodic() {

		// TODO execute the plan set up during init

	}

}
