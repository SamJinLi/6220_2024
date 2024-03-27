// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.AutoCmd;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;

// import com.pathplanner.lib.auto.AutoBuilder;
// import com.pathplanner.lib.path.EventMarker;
// import com.pathplanner.lib.path.GoalEndState;
// import com.pathplanner.lib.path.PathPlannerPath;
// import com.pathplanner.lib.path.PathPoint;
// import com.pathplanner.lib.path.RotationTarget;

// import edu.wpi.first.math.Pair;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib.util.AlienceColorCoordinateFlip;
import frc.robot.Constants.AutoConstants;
import frc.robot.commands.ArmIdleCommand;
// import frc.lib.util.Auto_GetMostRecentPoses;
// import frc.robot.Constants.AutoConstants;
import frc.robot.commands.IntakeCommand;
// import frc.robot.commands.IntakeFarNoteCmd;
import frc.robot.commands.SpeakerCommand;
import frc.robot.subsystems.PhotonVisionSubsystem;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.VisionSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class pickUpFarNoteTesting extends SequentialCommandGroup {
  /** Creates a new pickUpFarNoteTesting. */
  // List<RotationTarget> holonomicRotations = new ArrayList<RotationTarget>();
  private PhotonVisionSubsystem s_VisionSubsystem = PhotonVisionSubsystem.getInstance();
  public pickUpFarNoteTesting(Swerve s_Swerve) {
      // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      // new IntakeFarNoteCmd(s_Swerve, s_Swerve.getClosestNotePosition())
      // new FarNoteCommand(s_Swerve),
      // new IntakeCommand(s_Swerve),
      // new SpeakerCommand(s_Swerve),
      // new FarNoteCommand(s_Swerve),
      // new IntakeCommand(s_Swerve),
      // new SpeakerCommand(s_Swerve),
      // new FarNoteCommand(s_Swerve),
      // new IntakeCommand(s_Swerve),
      // new SpeakerCommand(s_Swerve),
      // new FarNoteCommand(s_Swerve),
      // new IntakeCommand(s_Swerve),
      // new SpeakerCommand(s_Swerve),
      // new FarNoteCommand(s_Swerve),
      // new IntakeCommand(s_Swerve),
      // new SpeakerCommand(s_Swerve)
      new SpeakerCommand(s_Swerve),
      //AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(2.90), 6.25, new Rotation2d(Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180)).getRadians())), AutoConstants.pathConstraints, 2),
      //AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(2.90), 6.25, new Rotation2d(Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180)).getRadians())), AutoConstants.pathConstraints, 2),
      AutoBuilder.pathfindToPose(AutoConstants.CENTERNOTE_POSE2DS[0], AutoConstants.pathConstraints, 2, 1),
      new IntakeCommand(s_Swerve),
      Commands.deadline(AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(1.6), 6.25, new Rotation2d(Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180)).getRadians())), AutoConstants.pathConstraints, 2), new ArmIdleCommand()),
      new SpeakerCommand(s_Swerve),
      AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(2.90), 6.25, new Rotation2d(Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180)).getRadians())), AutoConstants.pathConstraints, 2),
      AutoBuilder.pathfindToPose(AutoConstants.CENTERNOTE_POSE2DS[1], AutoConstants.pathConstraints, 2, 1),
      //Commands.waitSeconds(.1),
      new IntakeCommand(s_Swerve),
      Commands.deadline(AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(1.6), 6.25, new Rotation2d(Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180)).getRadians())), AutoConstants.pathConstraints, 2), new ArmIdleCommand()),
      new SpeakerCommand(s_Swerve)
    );
  }

  private Command isThereNote(Swerve s_Swerve) {
    //s_VisionSubsystem.getHasTargets()
    if(s_VisionSubsystem.getHasTargets()) {
      AutoConstants.currentCenterNotePos ++;
      System.out.println("Auto Sees note");
      return new IntakeCommand(s_Swerve);
    } else {
            System.out.println("Auto does not see note");
      AutoConstants.currentCenterNotePos ++;
      if(AutoConstants.currentCenterNotePos < AutoConstants.howManyNotesAreWeAttempting){
        return new SequentialCommandGroup(AutoBuilder.pathfindToPose(AutoConstants.CENTERNOTE_POSE2DS[AutoConstants.notePoseIDForAttempting[AutoConstants.currentCenterNotePos]], AutoConstants.pathConstraints, 0, 1.5),Commands.waitSeconds(.4), isThereNote(s_Swerve));
      }
      else
      {
        AutoConstants.currentCenterNotePos --;
        return AutoBuilder.pathfindToPose(AutoConstants.CENTERNOTE_POSE2DS[AutoConstants.notePoseIDForAttempting[AutoConstants.currentCenterNotePos]], AutoConstants.pathConstraints);
      }
    }
    
  }
}
