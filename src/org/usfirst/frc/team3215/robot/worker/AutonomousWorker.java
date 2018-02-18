package org.usfirst.frc.team3215.robot.worker;

import java.util.ArrayList;

import org.usfirst.frc.team3215.robot.config.RobotHardware;

public class AutonomousWorker {

	private final RobotHardware r;

	// a plan is a series of actions
	private ArrayList<Action> plan;

	// use if an action is a one-off (e.g. flipping an on/off switch)
	private boolean stopCallingCurrentAction;

	/**
	 * Autonomous executes a plan, which is a series of actions. This class defines
	 * a single action.
	 */
	private class Action {

		public final int type;
		public final long duration;

		/**
		 * @param type
		 *            A number indicating the type of action.
		 * @param duration
		 *            The duration in milliseconds for how this action takes.
		 */
		public Action(int type, long duration) {
			this.type = type;
			this.duration = duration;
		}

	}

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

		// prepare a new plan (which is a series of actions)
		plan = new ArrayList<Action>();
		stopCallingCurrentAction = false;

		// depending on which autonomous is selected, make a corresponding plan
		switch (r.selectedAutonomous()) {

		// --- when robot is standing on the LEFT side:
		case LEFT_DRIVE_AND_DROP:
			break;

		case LEFT_DRIVE_FORWARD:
			break;

		case LEFT_DRIVE_FORWARD_DELAYED:
			// after a 10 second delay, drive forward for 4 seconds
			plan.add(new Action(0, 10000));
			plan.add(new Action(1, 4));
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
			// do nothing at all (put a dummy statement here)
			plan.add(new Action(0, 1000));
			break;

		default:
			r.log("PANIC - unexpected autonomous: " + r.selectedAutonomous());
			break;
		}

	}

	/**
	 * Autonomous periodic - execute all actions in the plan.
	 */
	public void periodic() {

		// TODO execute the plan set up during init

	}

	/**
	 * Execute a given action type now.
	 */
	public void doAction(int type) {

		r.logOnce("autonomous action type: " + type);

		switch (type) {

		case 0: // halt all motors without jerking ("do nothing")
			r.motors().haltAllMotorsPeriodic();
			break;

		case 1: // drive forward at 50% speed
			r.motors().drive(0, 0.5, 0, 0);
			r.motors().haltLinearMotorsPeriodic();
			break;

		// TODO #JK add many new action type here

		default:
			r.logOnce("Warning: Unknown action type " + type + " called in autonomous");
		}

	}

}
