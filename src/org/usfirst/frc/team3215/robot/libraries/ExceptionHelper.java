package org.usfirst.frc.team3215.robot.libraries;

public class ExceptionHelper {

	public final static String getString(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Warning: Unexpected exception: " + e.getMessage() + " caused by " + e.getCause());

		int steCount = 0;
		for (StackTraceElement ste : e.getStackTrace()) {
			steCount++;
			if (steCount <= 5) {
				// append the first five stack trace elements
				sb.append("\n   ").append(ste.getClassName()).append(".").append(ste.getMethodName()).append(" line ")
						.append(ste.getLineNumber());
			}
		}

		return sb.append("...").toString();
	}

}
