// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;

// import java.util.List;
// import java.util.Optional;

// import javax.lang.model.util.ElementScanner14;

// import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
// import org.photonvision.targeting.PhotonTrackedTarget;

// import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.VecBuilder;
// import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.Unit;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.math.geometry.Transform2d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.VisionConstants;
// import frc.robot.subsystems.Swerve;

/** Add your docs here. */
public class PhotonvisionCalculations {
    public static AprilTagFieldLayout aprilFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    public static PhotonCamera[] cameras = {new PhotonCamera("Arducam_OV2311_USB_Camera_0"), new PhotonCamera("Arducam_OV2311_USB_Camera_1")}; //LEFT IS ID 0, RIGHT IS ID 1
    // public PhotonPipelineResult cameraZeroResults, cameraOneResults;
    // public static Transform3d camToCenterRobotZero = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0,0,0));//Cam mounted facing forward, half a meter forward of center, half a meter up from center. //TODO: need change
    // public static Transform3d camToCenterRobotOne = new Transform3d(new Translation3d(0.5,0.0,0.5), new Rotation3d(0,0,0));
    // public static PhotonPoseEstimator photonPoseEstimatorOne  = new PhotonPoseEstimator(aprilFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameras[1], VisionConstants.camToCenterRobotOne);
    public static final Field2d[] theField = {
        new Field2d(),
        new Field2d()
    };
    public static PhotonPoseEstimator[] estimatedPhotonPoses  = 
    {
        new PhotonPoseEstimator(aprilFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameras[0], VisionConstants.camerasToCenter[0]),
        new PhotonPoseEstimator(aprilFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameras[1], VisionConstants.camerasToCenter[1])
    };
    
    public ShuffleboardTab visionTab = Shuffleboard.getTab("Photonvision");
    
    static double camZeroLatency, camOneLatency;
    
    public static void initPhoton()
    {
        cameras[0].setPipelineIndex(0);
        cameras[1].setPipelineIndex(0);
        estimatedPhotonPoses[0].setMultiTagFallbackStrategy(PoseStrategy.CLOSEST_TO_REFERENCE_POSE);
        estimatedPhotonPoses[1].setMultiTagFallbackStrategy(PoseStrategy.CLOSEST_TO_REFERENCE_POSE);
        
        // Pose2d botPose2d = updatePhotonRobotPose(cameras[0].getLatestResult(), cameras[1].getLatestResult(), poseEstimator, s_Swerve.visionMeasurementStdDevConstant.get());
        // if(botPose2d == null)
        // {
            //     return;
            // }
            
            // Translation2d botTranslation2d = botPose2d.getTranslation();
            
            
            // poseEstimator.addVisionMeasurement(new Pose2d(botTranslation2d, s_Swerve.getHeading()), Timer.getFPGATimestamp() - (averageCamLatency/1000));
            
            
            // double averageCamLatency = (camZeroLatency + camOneLatency)/2;
            
            
            // Shuffleboard.getTab("Photonvision").add("camera zero latency in ms", camZeroLatency);
        // Shuffleboard.getTab("Photonvision").add("camera one latency in ms", camOneLatency);
    }
    
    public static void updateCamerasPoseEstimation(SwerveDrivePoseEstimator poseEstimator, double 
    camTrustValues)
    {
        ArrayList<Optional<EstimatedRobotPose>> estimatedPoses = getEstimatedGlobalPose(poseEstimator.getEstimatedPosition());
        for(int i = 0; i < cameras.length; i ++)
        {
            // estimatedPhotonPoses[i].setLastPose(poseEstimator.getEstimatedPosition());
            
            PhotonPipelineResult cameraResult = cameras[i].getLatestResult();
            double latencySec = cameraResult.getLatencyMillis() /1000;
            

            // double range =
            //             cameraResult.hasTargets() ?
            //             PhotonUtils.calculateDistanceToTargetMeters(
            //                     (i == 1) ? 0.2159 :0.21209, //set to left if id is 1, set to right if id is 0
            //                     Units.inchesToMeters(VisionConstants.aprilTagHeightInches[cameraResult.getBestTarget().getFiducialId() - 1]),
                                
            //                     Rotation2d.fromDegrees(i == 1 ? VisionConstants.leftArduCamPitchOffsetRad : VisionConstants.rightArduCamPitchOffsetRad).getRadians(),
            //                     Rotation2d.fromDegrees(cameraResult.getBestTarget().getPitch()).getRadians())
            //                     :
            //                     Double.MAX_VALUE;
            double distanceToTarget = Double.MAX_VALUE;
            if(cameraResult.getBestTarget() != null) {
                distanceToTarget = cameraResult.hasTargets() ? PhotonUtils.getDistanceToPose(poseEstimator.getEstimatedPosition(), new Pose2d(cameraResult.getBestTarget().getBestCameraToTarget().getX(), cameraResult.getBestTarget().getBestCameraToTarget().getY(), poseEstimator.getEstimatedPosition().getRotation())) : Double.MAX_VALUE;
            }

            // range = Math.abs(range);

            // double range = 0; //range set to 0 to test
            // if(cameraResult.getMultiTagResult().estimatedPose.isPresent)
            // {
            //     Transform3d fieldToCamera = cameraResult.getMultiTagResult().estimatedPose.best;
            //     // Pose2d newPose = new Pose2d(new Translation2d(fieldToCamera.getX(), fieldToCamera.getY));
                // double visionStdDev = camTrustValues * -(1 + (range * range / 30));
                double visionStdDev = 10 + (10 - (1 + (distanceToTarget * distanceToTarget / 30)));
                // if(estimatedPhotonPoses[i].getReferencePose() != null)
                // {
                    poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));//Change If needed//Double.max_value for the last parameter because we don't want to believe the camera on rotation at all
                    if(estimatedPoses.get(i).isPresent())
                    {
                        poseEstimator.addVisionMeasurement(new Pose2d(estimatedPoses.get(i).get().estimatedPose.getX(), estimatedPoses.get(i).get().estimatedPose.getY(), poseEstimator.getEstimatedPosition().getRotation()), Timer.getFPGATimestamp() - latencySec);
                        theField[i].setRobotPose(new Pose2d(estimatedPoses.get(i).get().estimatedPose.getX(), estimatedPoses.get(i).get().estimatedPose.getY(), poseEstimator.getEstimatedPosition().getRotation()));
                        SmartDashboard.putData("Pose for " + i, theField[i]);
                    }
                    //System.out.println("Pose estimate updated for :" + i + " and it's visionStdDev Value is : " + visionStdDev + " range = " + distanceToTarget);
                    
                    // poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));//Change If needed//Double.max_value for the last parameter because we don't want to believe the camera on rotation at all
                    // poseEstimator.addVisionMeasurement(new Pose2d(estimatedPoses.get(i)., estimatedPhotonPoses[i].getRobotToCameraTransform().getY(), poseEstimator.getEstimatedPosition().getRotation()), Timer.getFPGATimestamp() - latencySec);
                    // System.out.println("Pose estimate updated for :" + i + " and it's visionStdDev Value is : " + visionStdDev + " est.x pose = " + estimatedPhotonPoses[i].getRobotToCameraTransform().getX());
                    // }
            // }
            // else if(cameraResult.hasTargets())
            // {
            //     PhotonTrackedTarget fieldToCamera = cameraResult.getBestTarget();
            //     double distanceToTarget = fieldToCamera.
            // }
            SmartDashboard.putNumber("visionStdDev: " + i, visionStdDev);
        }
        

        // PhotonPipelineResult camZeroResult = cameras[0].getLatestResult();
        // PhotonPipelineResult camOneResult = cameras[1].getLatestResult();

        // camZeroLatency = camZeroResult.getLatencyMillis();
        // camOneLatency = camOneResult.getLatencyMillis();
        
        // Pose2d cameraZeroPose = updateCamPoseMultiTag(camZeroResult, poseEstimator);
        // Pose2d cameraOnePose = updateCamPoseMultiTag(camOneResult, poseEstimator);
        
        // if(cameraZeroPose != null)
        // {
        //     // poseEstimator.setVisionMeasurementStdDevs(null);
        //     poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(0.2, 0.2, Double.MAX_VALUE));
        //     poseEstimator.addVisionMeasurement(cameraZeroPose, Timer.getFPGATimestamp() - (camZeroLatency/1000));
        //     //System.out.println("Added vision measurement to camera 0 : " + cameraZeroPose.getX());
        // }
        // if (cameraOnePose != null)
        // {
        //     poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(0.2, 0.2, Double.MAX_VALUE));
        //     poseEstimator.addVisionMeasurement(cameraOnePose, Timer.getFPGATimestamp() - (camOneLatency/1000));
        //     //System.out.println("Added vision measurement to camera 1 : " + cameraOnePose.getX());
        // }
        
        // Shuffleboard.getTab("Photonvision").add("average camera latency in ms", averageCamLatency);
    }

    public static ArrayList<Optional<EstimatedRobotPose>> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
        ArrayList<Optional<EstimatedRobotPose>> robotPoses = new ArrayList<Optional<EstimatedRobotPose>>();
        for(PhotonPoseEstimator estimator : estimatedPhotonPoses)
        {
            estimator.setReferencePose(prevEstimatedRobotPose);
            robotPoses.add(estimator.update());
        }
        return robotPoses;
    }

    // public static Pose2d updateCamPoseMultiTag(PhotonPipelineResult cameraZeroResults, SwerveDrivePoseEstimator poseEstimator)
    // {
    //     if (cameraZeroResults.getMultiTagResult().estimatedPose.isPresent) {
    //         Transform3d fieldToCamera = cameraZeroResults.getMultiTagResult().estimatedPose.best;
    //         // Pose2d newPose = new Pose2d(new Translation2d(field) + VisionConstants.CENTER_OF_FIELD.getX(), (totalY/count) + VisionConstants.CENTER_OF_FIELD.getY()), poseEstimator.getEstimatedPosition().getRotation());
    //         Pose2d newPose = new Pose2d(new Translation2d(fieldToCamera.getX(), fieldToCamera.getY()), poseEstimator.getEstimatedPosition().getRotation());
    //         return newPose;
    //       }
    //     return null;
    // }
    
    //Same method, so redundent
    // public static Pose2d updateCamPose(PhotonPipelineResult cameraOneResults, SwerveDrivePoseEstimator poseEstimator)
    // {
    //     if (cameraOneResults.getMultiTagResult().estimatedPose.isPresent) {
    //         Transform3d fieldToCamera = cameraOneResults.getMultiTagResult().estimatedPose.best;
    //         // Pose2d newPose = new Pose2d(new Translation2d(field) + VisionConstants.CENTER_OF_FIELD.getX(), (totalY/count) + VisionConstants.CENTER_OF_FIELD.getY()), poseEstimator.getEstimatedPosition().getRotation());
    //         Pose2d newPose = new Pose2d(new Translation2d(fieldToCamera.getX(), fieldToCamera.getY()), poseEstimator.getEstimatedPosition().getRotation());
    //         return newPose;
    //       }
    //     return null;
    // }

    // public static Pose2d updatePhotonRobotPose(PhotonPipelineResult cameraZeroResults, PhotonPipelineResult cameraOneResults, SwerveDrivePoseEstimator poseEstimator, double constant)
    // {
    //     PhotonPipelineResult cameraZeroResult = cameras[0].getLatestResult();
    //     PhotonPipelineResult cameraOneResult = cameras[1].getLatestResult();
        
    //     if (cameraZeroResult.getMultiTagResult().estimatedPose.isPresent) {
    //         Transform3d fieldToCamera = cameraZeroResult.getMultiTagResult().estimatedPose.best;
    //       }

    //     if (cameraOneResult.getMultiTagResult().estimatedPose.isPresent) {
    //         Transform3d fieldToCamera = cameraOneResult.getMultiTagResult().estimatedPose.best;
    //       }
        
    //     int count = 0;
    //     double totalX = 0;
    //     double totalY = 0;
    //     double totalAngle = 0;
    //     double totalDistance = 0;

        
           
    //         // if(cameraZeroResults.hasTargets())
    //         // {
    //         //      List<PhotonTrackedTarget> targetsCamZero = cameraZeroResults.getTargets();
    //         //      Shuffleboard.getTab("Photonvision").add("camera zero targets", targetsCamZero.size());
    //         //     for(PhotonTrackedTarget target : targetsCamZero)
    //         //     {
    //         //         Transform3d aprilTagPoseEstimate = target.getAlternateCameraToTarget();

    //         //         totalX += aprilTagPoseEstimate.getX();
    //         //         totalY += aprilTagPoseEstimate.getY();
    //         //         totalAngle += aprilTagPoseEstimate.getRotation().getAngle();
    //         //         totalDistance += aprilTagPoseEstimate.getTranslation().getDistance(new Translation3d(0,0,0));
    //         //         count ++;
    //         //     }
    //         // }
    //         // if(cameraOneResults.hasTargets())
    //         // {
    //         //     List<PhotonTrackedTarget> targetsCamOne = cameraOneResults.getTargets();
    //         //     Shuffleboard.getTab("Photonvision").add("camera one targets", targetsCamOne.size());
    //         //     for(PhotonTrackedTarget target : targetsCamOne)
    //         //     {
    //         //         Transform3d aprilTagPoseEstimate = target.getAlternateCameraToTarget();

    //         //         totalX += aprilTagPoseEstimate.getX();
    //         //         totalY += aprilTagPoseEstimate.getY();
    //         //         totalAngle += aprilTagPoseEstimate.getRotation().getAngle();
    //         //         totalDistance += aprilTagPoseEstimate.getTranslation().getDistance(new Translation3d(0,0,0));
    //         //         count ++;
    //         //     }
    //         // }

    //         if(count > 0)
    //         {
    //             double distance = totalDistance/count;
    //             Shuffleboard.getTab("Photonvision").add("Distance", distance);
    //             if(distance > 4)
    //             {
    //                 return null;
    //             }
    //             else
    //             {
    //                 double visionStdDev = constant * (1+ (distance*distance / 30));
    //                 poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));
    //             }

    //             Pose2d newPose = new Pose2d(new Translation2d((totalX/count) + VisionConstants.CENTER_OF_FIELD.getX(), (totalY/count) + VisionConstants.CENTER_OF_FIELD.getY()), poseEstimator.getEstimatedPosition().getRotation());
    //             return newPose;
    //         }

            
    //     return null;
    // }

    /*
     * public static void updatePoseEstimation(SwerveDrivePoseEstimator poseEstimator, Swerve s_Swerve) {
        LimelightHelpers.setPipelineIndex(VisionConstants.LIMELIGHT3_NAME_STRING, 1);
        LimelightResults limelightResults = LimelightHelpers.getLatestResults(VisionConstants.LIMELIGHT3_NAME_STRING);
        
        if(limelightResults.targetingResults.targets_Fiducials == null) {
            //System.out.println("No Fiducial Targets");
            return;
        }

        
        Pose2d botPose2d = updateVisionRobotPose2d(limelightResults, poseEstimator, s_Swerve.visionMeasurementStdDevConstant.get());
        if(botPose2d == null) {
            return;
        }

        double latency = limelightResults.targetingResults.latency_capture + limelightResults.targetingResults.latency_jsonParse;
        
        Translation2d botTranslation2d = botPose2d.getTranslation();


        poseEstimator.addVisionMeasurement(new Pose2d(botTranslation2d, s_Swerve.getHeading()), Timer.getFPGATimestamp() - (latency/1000.0));
          
     */
}
