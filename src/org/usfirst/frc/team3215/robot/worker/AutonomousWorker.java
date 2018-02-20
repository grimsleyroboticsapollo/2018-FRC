package org.usfirst.frc.team3215.robot.worker;

import java.util.ArrayList;

import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.DiagnosticLightHelper;
import org.usfirst.frc.team3215.robot.libraries.ExceptionHelper;

public class AutonomousWorker {

	private final RobotHardware r;

	private final static long DURATION_FOREVER = 36000000L; // ten hours
	private final static int ACTION_HALT_AND_WAIT = 0; // reserved index

	// a plan is a series of actions
	private ArrayList<Action> plan;

	// use if an action is a one-off (e.g. flipping an on/off switch)
	private boolean stopCallingCurrentAction;

	private int currentActionNumber;
	private long currentActionStopTime;
	private Action currentAction;

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

		try {

			// prepare a new plan (which is a series of actions)
			plan = new ArrayList<Action>();
			currentActionNumber = -1;
			currentActionStopTime = 0;
			currentAction = null;

			// depending on which autonomous is selected, make a corresponding plan
			switch (r.selectedAutonomous()) {

			// --- when robot is standing on the LEFT side:
			case LEFT_DRIVE_AND_DROP:
				// TODO
				break;

			case LEFT_DRIVE_FORWARD:
				// TODO
				break;

			case LEFT_DRIVE_FORWARD_DELAYED:
				// TODO #JK tweak the following, it's just an example of the framework

				// after a 10 second delay, drive forward for 4 seconds
				plan.add(new Action(ACTION_HALT_AND_WAIT, 10000));
				plan.add(new Action(1, 4000));
				break;

			// --- when robot is standing in the MIDDLE:
			case MIDDLE_DRIVE_AND_DROP:
				// TODO
				break;

			case MIDDLE_DRIVE_TO_LEFT:
				// TODO
				break;

			case MIDDLE_DRIVE_TO_LEFT_DELAYED:
				// TODO
				break;

			case MIDDLE_DRIVE_TO_RIGHT:
				// TODO
				break;

			case MIDDLE_DRIVE_TO_RIGHT_DELAYED:
				// TODO
				break;

			// --- when robot is standing on the RIGHT side:
			case RIGHT_DRIVE_AND_DROP:
				// TODO
				break;

			case RIGHT_DRIVE_FORWARD:
				// TODO
				break;

			case RIGHT_DRIVE_FORWARD_DELAYED:
				// TODO
				break;

			// --- special functions:
			case SELF_TEST:
				// TODO
				break;

			case NOTHING:
				// do nothing at all
				break;

			default:
				r.log("Warning: PANIC - unexpected autonomous: " + r.selectedAutonomous());
				break;
			}

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

	/**
	 * Execute a given action type now.
	 */
	public void doAction(int type) {

		r.logOnce("autonomous action type: " + type);

		try {

			switch (type) {

			case ACTION_HALT_AND_WAIT: // halt all motors without jerking ("do nothing")
				r.motors().haltAllMotorsPeriodic();
				break;

			case 1: // drive forward at 50% speed
				r.motors().drive(0, 0.5, 0, 0);
				r.motors().haltLinearMotorsPeriodic();
				break;

			// TODO #JK add many new action types here

			/*
			 * Example of an action that is only to be executed once:
			 * 
			 * case 123456: r.flipSomeSwitch(true); stopCallingCurrentAction = true; break;
			 * 
			 * This action will be called exactly once, and then it'll still wait for the
			 * specified duration until the next action is called.
			 */

			default:
				r.logOnce("Warning: Unknown action type " + type + " called in autonomous");
			}

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

	/**
	 * Autonomous periodic - execute all actions in the plan.
	 */
	public void periodic() {

		try {

			long nowTime = System.currentTimeMillis();

			if (nowTime >= currentActionStopTime) {
				currentActionNumber++;
				r.setDiagnosticLights(currentActionNumber, 1); // short = action number, long = 1

				if (currentActionNumber >= plan.size()) {
					currentAction = new Action(0, DURATION_FOREVER);
					r.log("Autonomous finished, robot halt.");
					r.setDiagnosticLights(0, 2); // autonomous done
				} else {
					currentAction = plan.get(currentActionNumber);
					r.log("advance plan to action #" + currentActionNumber + ": " + "type " + currentAction.type
							+ " for " + currentAction.duration + " milliseconds");
				}

				currentActionStopTime = nowTime + currentAction.duration;

				// reset on-time flag
				stopCallingCurrentAction = false;
			}

			if (!stopCallingCurrentAction) {
				doAction(currentAction.type);
			}

		} catch (Exception e) {
			r.logOnce(ExceptionHelper.getString(e));
		}

	}

}
