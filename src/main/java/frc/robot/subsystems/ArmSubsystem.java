package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

import frc.robot.Constants.ArmConstants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;


public class ArmSubsystem extends SubsystemBase{
    private static final ArmSubsystem INSTANCE = null; //Created so that only 1 instance of arm subsystem is 
    // created at all time. Think of it as a "static" call to the subsystem where you can get static variables

    private final CANSparkMax armMotorA, armMotorB;
    private final DutyCycleEncoder armEncoder;
    private final PIDController pid;
    
    /**
     * Initializes the ArmSubsystem
     */
    private ArmSubsystem() {
        this.armMotorA = new CANSparkMax(ArmConstants.armMotorAID, MotorType.kBrushless);
        this.armMotorB = new CANSparkMax(ArmConstants.armMotorBID, MotorType.kBrushless);

        this.armMotorA.setInverted(ArmConstants.motorAInverted);
        this.armMotorB.setInverted(ArmConstants.motorBInverted);

        this.armMotorA.setIdleMode(null);
        this.armMotorB.setIdleMode(null);

        this.armMotorB.follow(armMotorA);

        this.armEncoder = new DutyCycleEncoder(ArmConstants.k_ENC_PORT);

        this.armMotorA.burnFlash();
        this.armMotorB.burnFlash();

        this.pid = new PIDController(
            ArmConstants.kP,
            ArmConstants.kI,
            ArmConstants.kD);
    }

    /**
     * Converts a real world value (degrees of the arm) to a usable value (Encoder Value in rotations)
     * @param armPosition Arm Position in Degrees
     * @return the corresponding Encoder Value in rotations
     */
    public double convertArmDegreesToEncoderValue(double armPosition){
        return armPosition / 360;
    }

    /**
     * Converts a Encoder Value to a real world value (degrees of the arm)
     * @param encoderValue NEO motor position (in rotations)
     * @return the corresponding arm position to the given Encoder Value
     */
    public double convertEncoderValueToArmDegrees(double encoderValue){
        return encoderValue * 360;
    }

    /**
     * Sets the current of the NEO arm motors for manual driving
     * @param  speed  a value from -1 to 1 (sets current of motor)
     */
    public void drive(double speed){
        if(speed > 1){
            speed = 1;
        }
        else if (speed < -1){
            speed = -1;
        }
        this.armMotorA.set(speed * .75);
    }

    /**
     * Calculates the output of the arm PID for a given setpoint
     * @param  setpoint desired arm position in degrees
     * @return
     */
    public double calculate(double setpoint){
        return pid.calculate(convertEncoderValueToArmDegrees(armEncoder.get()), setpoint);
    }
    /**
     * Gives the position of the arm in degrees
     * @returns the value in degrees of the arm    
     */
    public double getArmPosition(){
        return convertArmDegreesToEncoderValue(this.armEncoder.get());
    }

    @Override
    public void periodic() {
         // This method will be called once per scheduler run
         SmartDashboard.putNumber("Arm Angle", getInstance().getArmPosition());
    }

    /**
     * Accesses the static instance of the ArmSubsystem singleton
     * @return ArmSubsystem Singleton Instance
     */
    public static ArmSubsystem getInstance() {
        if (INSTANCE == null) {
            return new ArmSubsystem();
        }
        return INSTANCE;
    }
}
