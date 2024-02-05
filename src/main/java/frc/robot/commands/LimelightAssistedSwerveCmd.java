// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.VisionSubsystem;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.subsystems.Swerve;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import java.util.function.Supplier;


public class LimelightAssistedSwerveCmd extends Command {
  private final VisionSubsystem m_VisionSubsystem;
  private final Swerve s_Swerve;
  private final PIDController limelightPidController;
  private final Supplier<Boolean> aButton;

  public LimelightAssistedSwerveCmd(Swerve s_Swerve, Supplier<Boolean> aButton) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_VisionSubsystem = VisionSubsystem.getInstance();
    limelightPidController = new PIDController(0.15,0,0.325);
    this.s_Swerve = s_Swerve;
    this.aButton = aButton;
    addRequirements(s_Swerve); 
    addRequirements(m_VisionSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // double steerOutput = limelightPidController.calculate(s_Swerve.getHeadingDegrees(),m_VisionSubsystem.getSteeringOffset()) * SwerveConstants.maxAngularVelocity;
    // SmartDashboard.putNumber("BEFORE steeroutput", steerOutput);
    // steerOutput = (steerOutput > SwerveConstants.maxAngularVelocity) ? SwerveConstants.maxAngularVelocity : (steerOutput < -SwerveConstants.maxAngularVelocity) ? -SwerveConstants.maxAngularVelocity : steerOutput;
    // double steerOutput = Normalization(m_VisionSubsystem.getSteeringOffset(), -180, 180, -1, 1) * SwerveConstants.maxAngularVelocity;
    double steeroutput = 0;
    if(m_VisionSubsystem.getTarget() == 1.0)
    {
      steeroutput = limelightPidController.calculate(m_VisionSubsystem.getSteeringOffset());
      
      if(Math.abs(m_VisionSubsystem.getSteeringOffset()) < 1.5) {
       steeroutput = 0;
     }

      steeroutput = (steeroutput > SwerveConstants.maxAngularVelocity)?SwerveConstants.maxAngularVelocity:(steeroutput< -SwerveConstants.maxAngularVelocity)?-SwerveConstants.maxAngularVelocity:steeroutput;
      // System.out.println("success!");
    }
    else
    {
      steeroutput = 0;
    }
    SmartDashboard.putNumber("AFTER steeroutput ",steeroutput);
    SmartDashboard.putNumber("pid output", limelightPidController.calculate(m_VisionSubsystem.getSteeringOffset()));
    SmartDashboard.putNumber("steer off set", m_VisionSubsystem.getSteeringOffset());
    s_Swerve.drive(
            new Translation2d(0d, 0d), 
            // MathUtil.applyDeadband(limelightPidController.calculate(s_Swerve.getHeadingDegrees(),m_VisionSubsystem.getSteeringOffset()), 0), //NOT USABLE UPGRADED
            steeroutput,
            // (s_Swerve.getHeadingDegrees() - m_VisionSubsystem.getSteeringOffset()) * SwerveConstants.maxAngula15/rVelocity, //Original code from TeleopSwerve, NOT USABLE
            true, 
            false
        );
  }

  public double Normalization(double v, double Min, double Max, double newMin, double newMax)
  {
    return (v-Min)/(Max-Min)*(newMax-newMin)+newMin;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}


  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (!aButton.get());
  }
}
