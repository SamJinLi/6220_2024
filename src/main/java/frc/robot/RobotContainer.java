package frc.robot;

import java.nio.file.Path;
import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.util.AlienceColorCoordinateFlip;
import frc.lib.util.RumbleManager;
import frc.lib.util.ShooterConfiguration;
import frc.lib.util.TriggerButton;
import frc.lib.util.TunableNumber;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.AmpCommand;
import frc.robot.commands.ArmIdleCommand;
import frc.robot.commands.ClimberTestCommand;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.IntakeIdleCommand;
import frc.robot.commands.ShooterIdleCommand;
import frc.robot.commands.ShootingTestCommand;
import frc.robot.commands.SpeakerCommand;
import frc.robot.commands.TeleopSwerve;
import frc.robot.commands.Tuning_Arm;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.blinkin;

public class RobotContainer {

  private final SendableChooser<Command> autoChooser;
  /* Controllers */
  private final XboxController driver = new XboxController(0);
  private final Joystick operator = new Joystick(1);
  /* Drive Controls */

  

  /* Driver Buttons */
  private final JoystickButton zeroGyro = new JoystickButton(driver, XboxController.Button.kY.value);
  private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kLeftBumper.value);
  //private final JoystickButton intakeTemporary = new JoystickButton(driver, XboxController.Button.kA.value);
  //private final JoystickButton ampTemporary = new JoystickButton(driver, XboxController.Button.kB.value);
  //private final JoystickButton speakerTemporary = new JoystickButton(driver, XboxController.Button.kX.value);
  private final JoystickButton zeroOdometry = new JoystickButton(driver, XboxController.Button.kBack.value);
  private final JoystickButton override = new JoystickButton(driver, XboxController.Button.kRightBumper.value);

  private final Trigger fireRightTrigger = new TriggerButton(driver, XboxController.Axis.kRightTrigger);
  private final Trigger robotControlLeftTrigger = new TriggerButton(driver, XboxController.Axis.kLeftTrigger);

  /* Operator Buttons */
  private final Trigger intake = new Trigger(()->operator.getRawButton(5));
  private final Trigger amp = new Trigger(()->operator.getRawButton(2));
  private final Trigger noNote = new Trigger(()->operator.getRawButton(12));
  private final Trigger climb = new Trigger(()->operator.getRawButton(4));

  /* Subsystems */
  private final Swerve s_Swerve = new Swerve();
  private final ArmSubsystem s_ArmSubsystem = ArmSubsystem.getInstance();
  private final IntakeSubsystem s_IntakeSubsystem = IntakeSubsystem.getInstance();
  private final ShooterSubsystem s_ShooterSubsystem = ShooterSubsystem.getInstance();
  private final ClimberSubsystem s_ClimberSubsystem = ClimberSubsystem.getInstance();

  //private final PhotonVisionSubsystem p_PhotonVisionSubsystem = PhotonVisionSubsystem.getInstance();
  private final blinkin s_Blinkin = blinkin.getInstance();

  public RobotContainer() {
    ShooterConfiguration.setupRadiusValues();   
    ShooterConfiguration.setupConfigurations();
    Constants.VisionConstants.setTagHeights();

    NamedCommands.registerCommand("shoot", new SpeakerCommand(s_Swerve));
    NamedCommands.registerCommand("pickup", new IntakeCommand(s_Swerve));

    s_Swerve.configureAutoBuilder();

    s_Swerve.setDefaultCommand(
        new TeleopSwerve(
            s_Swerve,
            driver,
            () -> robotCentric.getAsBoolean()
        )
    );

    
    s_ArmSubsystem.setDefaultCommand(new ArmIdleCommand());
    
    //s_ArmSubsystem.setDefaultCommand(new Tuning_Arm(driver));
    
    s_ShooterSubsystem.setDefaultCommand(new ShooterIdleCommand());
    
    s_IntakeSubsystem.setDefaultCommand(new IntakeIdleCommand());
    
    //s_ClimberSubsystem.setDefaultCommand(new ClimberTestCommand(driver));
    
    
    // Build an auto chooser. This will use Commands.none() as the default option.
    autoChooser = AutoBuilder.buildAutoChooser();
    autoChooser.addOption("fourNoteTesting", fourNoteAutoServite());
    autoChooser.addOption("pick up fartherst Note and Shoot", pickUpFarNoteAndShoot());
    // Another option that allows you to specify the default auto by its name
    // autoChooser = AutoBuilder.buildAutoChooser("My Default Auto");    

    SmartDashboard.putData("Auto Chooser", autoChooser);
   

    configureButtonBindings();
    
  }

  private void configureButtonBindings() {
    zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
    zeroOdometry.onTrue(new InstantCommand(() -> s_Swerve.setPose(new Pose2d(new Translation2d(15.3, 5.55), new Rotation2d(0))))); //Red side
    // zeroOdometry.onTrue(new InstantCommand(() -> s_Swerve.setPose(new Pose2d(new Translation2d(1.33, 5.60), new Rotation2d(0))))); //Blue Side

    noNote.onTrue(new InstantCommand(() -> s_IntakeSubsystem.reset()));

    amp.whileTrue(new AmpCommand(
      s_Swerve,
      driver,
      () -> operator.getRawButton(1),
      ()->robotControlLeftTrigger.getAsBoolean()
      )
    );

    if (zeroGyro.getAsBoolean()) {
       driver.setRumble(RumbleType.kLeftRumble, 0.1);
        Timer.delay(0.01);
        driver.setRumble(RumbleType.kLeftRumble, 0);
    }

    intake.whileTrue(new IntakeCommand(
      s_Swerve, 
      driver,  
      () -> robotControlLeftTrigger.getAsBoolean())
      .until(() -> s_IntakeSubsystem.noteInTransit()));

    fireRightTrigger.whileTrue(new SpeakerCommand(
      s_Swerve, 
      driver)
    );
    climb.whileTrue(new ClimberTestCommand(operator));
    // speakerTemporary.whileTrue(new ShootingTestCommand());
  }

  public Command getAutonomousCommand() {
    //s_Swerve.setPose(new Pose2d(15.3,5.55,new Rotation2d(0)));
    return autoChooser.getSelected();
  }

  public SequentialCommandGroup fourNoteAutoServite()
  {
    return new SequentialCommandGroup(
      new SpeakerCommand(s_Swerve),
      new IntakeCommand(s_Swerve),
      new SpeakerCommand(s_Swerve),
      AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(2.2), 5.6, Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180))), AutoConstants.pathConstraints),
      new IntakeCommand(s_Swerve),
      new SpeakerCommand(s_Swerve),
      AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(2.1), 4.4, Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(155))), AutoConstants.pathConstraints),
      new IntakeCommand(s_Swerve),
      new SpeakerCommand(s_Swerve)
    );
  }

  public SequentialCommandGroup pickUpFarNoteAndShoot()
  {
    List<Translation2d> bezierPointToFar = PathPlannerPath.bezierFromPoses(
      new Pose2d(3.9, 6.15, Rotation2d.fromDegrees(0)),
      new Pose2d(7.55, 7.45, Rotation2d.fromDegrees(0))
    );
    PathPlannerPath pathTofar = new PathPlannerPath(
      bezierPointToFar,
      AutoConstants.pathConstraints,
      new GoalEndState(0, Rotation2d.fromDegrees(180))
      );
      pathTofar.preventFlipping = false;
    return new SequentialCommandGroup(
      new SpeakerCommand(s_Swerve),
      new ArmIdleCommand(),
      // AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(7.5), 7.5, Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180))), AutoConstants.PathConstraints),
      AutoBuilder.pathfindThenFollowPath(pathTofar, AutoConstants.pathConstraints),
      new IntakeCommand(s_Swerve),
      new IntakeIdleCommand(),
      AutoBuilder.pathfindToPose(new Pose2d(AlienceColorCoordinateFlip.flip(4), 5.8, Rotation2d.fromDegrees(AlienceColorCoordinateFlip.flipDegrees(180))), AutoConstants.pathConstraints),
      new SpeakerCommand(s_Swerve)
      );
  }

}
