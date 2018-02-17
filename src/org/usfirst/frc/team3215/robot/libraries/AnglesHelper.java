package org.usfirst.frc.team3215.robot.libraries;

/**
 * Little helper library around angles.
 *
 */
public class AnglesHelper {

	/**
	 * Given an incoming angle in degrees that may include turns (e.g. "720 degrees"
	 * means forward after having turned two times around counterclockwise), returns
	 * the angle thrown back into the [0,360[ interval.
	 */
	public static double getPlainAngle(double angleAndTurns) {
		double turns = Math.floor(angleAndTurns / 360.);
		return angleAndTurns - 360. * turns;
	}

	/**
	 * Given two incoming angles in degrees, returns the degrees in the [-180,180[
	 * interval in which to turn quickest from the first to the second angle.
	 * Positive values mean turning counterclockwise, negative values mean
	 * clockwise.
	 */
	public static double getAngleDifference(double angle1, double angle2) {
		double angleDiff = angle2 - angle1;
		double turnsLeftRight = Math.floor(0.5 + angleDiff / 360.);
		return angleDiff - 360. * turnsLeftRight;
	}

}
