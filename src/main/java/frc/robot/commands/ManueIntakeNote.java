// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class ManueIntakeNote extends Command {
  /** Creates a new ManuelEjectNote. */
  private final IntakeSubsystem s_IntakeSubsystem = IntakeSubsystem.getInstance();
  private final ShooterSubsystem s_ShooterSubsystem = ShooterSubsystem.getInstance();
  public ManueIntakeNote() {
    addRequirements(s_IntakeSubsystem,s_ShooterSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // s_IntakeSubsystem.reset();
    s_IntakeSubsystem.setFiring(true);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    s_IntakeSubsystem.simpleDrive(false, 0.15);
    // s_ShooterSubsystem.spinManually(-0.5);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    s_IntakeSubsystem.simpleDrive(false, 0);
    // s_IntakeSubsystem.setFiring(false);
    // s_IntakeSubsystem.reset();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
