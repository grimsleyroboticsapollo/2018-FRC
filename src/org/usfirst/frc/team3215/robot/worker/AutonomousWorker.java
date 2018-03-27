package org.usfirst.frc.team3215.robot.worker;

import java.util.ArrayList;

import org.usfirst.frc.team3215.robot.MotorHelper;
import org.usfirst.frc.team3215.robot.config.RobotHardware;
import org.usfirst.frc.team3215.robot.libraries.DiagnosticLightHelper;
import org.usfirst.frc.team3215.robot.libraries.ExceptionHelper;

import edu.wpi.first.wpilibj.DriverStation;

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

		// From https://www.chiefdelphi.com/forums/showthread.php?p=1741663:
		//
		// Game data is provided in the next line of code, and is specified in the
		// following format: As viewed from the driver station, the first character is
		// the switch our team owns ('L' means we own the left switch, 'R' means we own
		// the right switch). The second character is which side of the scale we own.
		// The third character is which side of the far switch we own ('L' or 'R').
		String gameData = DriverStation.getInstance().getGameSpecificMessage();

		// defensive coding: never trust remote inputs; make SURE it's three characters:
		if (gameData == null) {
			r.log("warning: game data is null; forcing 'LLL' just so that autonomous doesn't crash");
			gameData = "LLL";
		} else if (gameData.length() < 3) {
			r.log("warning: game data length is less than 3 ('" + gameData
					+ "'); appending LLL just so that autonomous doesn't crash");
			gameData += "LLL";
		}
		gameData = gameData.toUpperCase();

		// now that we are sure it's three characters, evaluate:
		r.log("game data found: " + gameData);
		boolean ourSwitchIsLeft = (gameData.charAt(0) == 'L');
		boolean scaleIsLeft = (gameData.charAt(1) == 'L');
		boolean theirSwitchIsLeft = (gameData.charAt(2) == 'L');

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
				if (ourSwitchIsLeft) {
					plan.add(new Action(1, 2000));
					plan.add(new Action(15, 500));
					plan.add(new Action(11, 500));
					plan.add(new Action(21, 1000));
					plan.add(new Action(10, 2000));
				} else {
					plan.add(new Action(0, 10000));
					plan.add(new Action(1, 2000));
				}
				break; 
				
			case LEFT_DRIVE_FORWARD:
				plan.add(new Action(0, 8000));
				plan.add(new Action(18, 5000));
				break;

			// --- when robot is standing in the MIDDLE:
			case MIDDLE_DRIVE_AND_DROP:
				plan.add(new Action(1, 200));
				if (ourSwitchIsLeft) {
					plan.add(new Action(14, 2000));
					plan.add(new Action(1, 800));
					plan.add(new Action(15, 500));
					plan.add(new Action(11, 500));
					plan.add(new Action(21, 1000));
					plan.add(new Action(10, 2000));
				} else {
					plan.add(new Action(13, 2000));
					plan.add(new Action(1, 800));
					plan.add(new Action(15, 500));
					plan.add(new Action(11, 500));
					plan.add(new Action(22, 1000));
					plan.add(new Action(10, 2000));
				}
				break;

			case MIDDLE_DRIVE_TO_LEFT:
				plan.add(new Action(0, 5200));
				plan.add(new Action(1, 200));
				plan.add(new Action(5, 6000));
				plan.add(new Action(20, 2400));
				break;

			case MIDDLE_DRIVE_TO_RIGHT:
				plan.add(new Action(0, 6200));
				plan.add(new Action(1, 200));
				plan.add(new Action(6, 5000));
				plan.add(new Action(20, 2300));
				break;

			// --- when robot is standing on the RIGHT side:
			case RIGHT_DRIVE_AND_DROP:
				if (ourSwitchIsLeft) {
					plan.add(new Action(0, 10000));
					plan.add(new Action(1, 2000));
				} else {
					plan.add(new Action(15, 500));
					plan.add(new Action(11, 500));
					plan.add(new Action(22, 1000));
					plan.add(new Action(10, 2000));
				}
				break;

			case RIGHT_DRIVE_FORWARD:
				plan.add(new Action(0, 8000));
				plan.add(new Action(19, 5000));
				break;

			// --- special functions:
			case SELF_TEST:

				// 2 second delay
				plan.add(new Action(ACTION_HALT_AND_WAIT, 2000));

				plan.add(new Action(1, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(2, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(3, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(4, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(5, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(6, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(7, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(8, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(9, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(10, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(11, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(12, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(13, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(14, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(15, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(16, 1000));
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
				plan.add(new Action(17, 100)); // just nick it (ratchet)
				plan.add(new Action(ACTION_HALT_AND_WAIT, 1000));
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
				r.motors().drive(0, 0.5, 0, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 2: // drive backwards at 50%
				r.motors().drive(180, 0.5, 180, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 3: // drive right at 50%
				r.motors().drive(270, 0.5, 270, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 4: // drive left at 50%
				r.motors().drive(90, 0.5, 90, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 5: // point at 240 drive at 60 at 50%
				r.motors().drive(60, 0.5, 240, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 6: // point at 120 drive at 300 at 50%
				r.motors().drive(300, 0.5, 120, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 7: // point to 60 drive to 240 at 50%
				r.motors().drive(240, 0.5, 60, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 8: // point to 300 drive to 120 at 50%
				r.motors().drive(120, 0.5, 300, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 9: // cube in-take
				r.motors().linearSingle(MotorHelper.CUBIE_INTAKE, -0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 10: // cube output
				r.motors().linearSingle(MotorHelper.CUBIE_INTAKE, 0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 11: // lift up
				r.motors().linearSingle(MotorHelper.LIFT, 0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 12: // lift down
				r.motors().linearSingle(MotorHelper.LIFT, -0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 13: // point to 300 go to 300 50% speed
				r.motors().drive(300, .5, 300, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 14: // point to 60 go to 60 50% speed
				r.motors().drive(60, .5, 60, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 15: // extend cubie
				r.motors().linearSingle(MotorHelper.CUBIE_DEPLOY, 0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 16: // retract cubie
				r.motors().linearSingle(MotorHelper.CUBIE_DEPLOY, -0.5);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 17: // power winch
				r.motors().linearSingle(MotorHelper.WINCH, 1.0);
				r.motors().haltDriveMotorsPeriodic();
				break;

			case 18: // point to 10 drive to 10 at 50%
				r.motors().drive(10, 0.5, 10, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 19: // point to 350 drive to 350 at 50%
				r.motors().drive(350, 0.5, 350, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 20: // point to 180 drive to 0 at 50%
				r.motors().drive(0, 0.5, 180, 0.5);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 21: // point to 270 at 30% drive to 270 at 20%
				r.motors().drive(270, 0.20, 270, 0.30);
				r.motors().haltLinearMotorsPeriodic();
				break;

			case 22: // point to 90 at 30% drive to 90 at 20%
				r.motors().drive(90, 0.20, 90, 0.30);
				r.motors().haltLinearMotorsPeriodic();
				break;

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
