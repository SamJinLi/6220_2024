package frc.robot.commands;

import frc.robot.Constants.SwerveConstants;
import frc.robot.Constants;
import frc.robot.RobotState;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.Swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;


public class TeleopSwerve extends Command {    
    private Swerve s_Swerve;    
    private DoubleSupplier translationSup;
    private DoubleSupplier strafeSup;
    private DoubleSupplier rotationSup;
    private BooleanSupplier robotCentricSup;

    public TeleopSwerve(Swerve s_Swerve, DoubleSupplier translationSup, DoubleSupplier strafeSup, DoubleSupplier rotationSup, BooleanSupplier robotCentricSup) {
        this.s_Swerve = s_Swerve;
        addRequirements(s_Swerve);

        this.translationSup = translationSup;
        this.strafeSup = strafeSup;
        this.rotationSup = rotationSup;
        this.robotCentricSup = robotCentricSup;
    }

    @Override
    public void execute() {
        /* Get Values, Deadband*/
        double translationVal = MathUtil.applyDeadband(translationSup.getAsDouble(), OIConstants.kDeadband);
        double strafeVal = MathUtil.applyDeadband(strafeSup.getAsDouble(), OIConstants.kDeadband);
        double rotationVal = MathUtil.applyDeadband(rotationSup.getAsDouble(), OIConstants.kDeadband);
        if(RobotState.getInstance().getState() == RobotState.State.AMP){
            s_Swerve.setAutoTurnHeading(90);
            rotationVal = s_Swerve.getTurnPidSpeed();
        }
        else if(RobotState.getInstance().getState() == RobotState.State.SPEAKER){
            s_Swerve.setAutoTurnHeading(s_Swerve.getHeadingToSpeaker());
            rotationVal = s_Swerve.getTurnPidSpeed();
        }
        int invert =  (Constants.isRed) ? -1 : 1; 
        /* Drive */
        s_Swerve.drive(
            new Translation2d(translationVal, strafeVal).times(SwerveConstants.maxSpeed*invert), 
            rotationVal * SwerveConstants.maxAngularVelocity, 
            !robotCentricSup.getAsBoolean(), 
            true
        );
    }
}