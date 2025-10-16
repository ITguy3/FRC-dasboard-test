// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private final RobotContainer m_robotContainer = new RobotContainer();

  // === Added for timer and NetworkTables ===
  private Timer matchTimer;
  private NetworkTable table;
  private NetworkTableEntry timerEntry;

  /** This function is run when the robot is first started up and should be used for any initialization code. */
  @Override
  public void robotInit() {
    // Initialize and start timer
    matchTimer = new Timer();
    matchTimer.reset();
    matchTimer.start();

    // Get or create a NetworkTable for your custom dashboard
    table = NetworkTableInstance.getDefault().getTable("DashboardData");
    timerEntry = table.getEntry("MatchTime");

    // If running simulation, host a NetworkTables server locally
    if (isSimulation()) {
      System.out.println("[SIM] Starting local NetworkTables server on localhost...");
      NetworkTableInstance.getDefault().startServer();
    } else {
      System.out.println("[ROBORIO] Using FMS/NT client connection...");
    }
  }

  @Override
  public void robotPeriodic() {
    // Run command scheduler
    CommandScheduler.getInstance().run();

    // Continuously update the timer value on NetworkTables
    double time = matchTimer.get();
    timerEntry.setDouble(time);

    // Optional: print timer in simulation for quick check
    if (isSimulation()) {
      //System.out.printf("[SIM TIMER] %.2f seconds\n", time);
    }
  }

  @Override
  public void disabledInit() {
    // Reset timer whenever robot disables
    matchTimer.reset();
    matchTimer.start();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {
    // Nothing special yet, but we can extend this later for dashboard sim data
  }

  @Override
  public void simulationPeriodic() {}
}
