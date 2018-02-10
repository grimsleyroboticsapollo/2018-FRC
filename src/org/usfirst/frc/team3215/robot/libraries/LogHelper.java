package org.usfirst.frc.team3215.robot.libraries;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class LogHelper {

	private final Object LOG_MUTEX = new Object();

	private long startDateTime = -1;
	private Set<String> logOnceMessages = new HashSet<String>();
	Queue<String> fifoMessages = new LinkedList<String>();

	Thread logThread;

	public LogHelper() {

		// write actual System.out from a queue since it'll randomly drop
		// output if there's too much
		logThread = new Thread(() -> {
			while (!Thread.interrupted()) {

				if (!fifoMessages.isEmpty()) {
					System.out.println(fifoMessages.remove());
				}

				try {
					Thread.sleep(100); // up to 10 messages per second max
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		logThread.setDaemon(true);
		logThread.start();
	}

	/**
	 * Write a message to the log output.
	 */
	public void print(String message) {
		synchronized (LOG_MUTEX) {
			fifoMessages.add(getTimeDisplay() + " " + message);
		}
	}

	/**
	 * Prints a message to the log, but only once.
	 */
	public void printOnce(String message) {
		synchronized (LOG_MUTEX) {
			if (logOnceMessages.add(message)) {
				print(message);
				if (logOnceMessages.size() > 10000) {
					print("PANIC - logOnceMessages > 10000; discarding old set");
					resetLogOnceMessages();
				}
			}
		}
	}

	/**
	 * Set the 0:0:0 time to now.
	 */
	public void resetTimer() {
		startDateTime = (new Date()).getTime();
	}

	/**
	 * Clears out the set of messages logged once so that they can be logged again.
	 */
	public void resetLogOnceMessages() {
		synchronized (LOG_MUTEX) {
			logOnceMessages = new HashSet<String>();
		}
	}

	private String getTimeDisplay() {
		if (startDateTime < 0) {
			resetTimer();
		}
		long nowDateTime = (new Date()).getTime() - startDateTime;
		long minutesSince = nowDateTime / 600000L;
		nowDateTime -= (minutesSince * 60000L);
		long secondsSince = nowDateTime / 1000L;
		nowDateTime -= (secondsSince) * 1000L;

		return "" + minutesSince + ":" + secondsSince + ":" + nowDateTime;
	}

}
