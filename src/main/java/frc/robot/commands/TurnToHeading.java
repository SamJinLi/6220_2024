package frc.robot.commands;

import frc.robot.subsystems.Swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;


import edu.wpi.first.wpilibj2.command.Command;


public class TurnToHeading extends Command {    
    private Swerve s_Swerve;    
    private double heading;
    public TurnToHeading(Swerve s_Swerve, double heading) {
        this.s_Swerve = s_Swerve;
        addRequirements(s_Swerve);
        this.heading = heading;
    }

    @Override
    public void initialize(){
        s_Swerve.setIsAutoTurning(true);
    }

    @Override
    public void execute() {
        s_Swerve.setAutoTurnHeading(heading);
    }

    @Override
    public void end(boolean interrupted) {
        s_Swerve.setIsAutoTurning(false);
    }
}