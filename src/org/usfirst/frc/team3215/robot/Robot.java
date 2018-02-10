package org.usfirst.frc.team3215.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	RobotHardware r = new RobotHardware();

	@Override
	public void robotInit() {
		System.out.println("robotInit()");
		r.init();
		// TODO
	}

	@Override
	public void autonomousInit() {
		System.out.println("autonomousInit()");
		//TODO
	}

	@Override
	public void autonomousPeriodic() {
		System.out.println("autonomousPeriodic()");
		// TODO
	}

	@Override
	public void teleopPeriodic() {
		System.out.println("teleopPeriodic()");
		// TODO
	}

	@Override
	public void testPeriodic() {
		System.out.println("testPeriodic()");
		// TODO
	}

}