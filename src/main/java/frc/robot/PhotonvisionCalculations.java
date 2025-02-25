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
import org.photonvision.targeting.PhotonTrackedTarget;

import com.ctre.phoenix6.Timestamp;

// import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.VecBuilder;
// import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
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
import frc.robot.subsystems.Swerve;

/** Add your docs here. */
public class PhotonvisionCalculations {
    public static AprilTagFieldLayout aprilFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    public static PhotonCamera[] cameras = {new PhotonCamera("Right_Ardu_Cam"), new PhotonCamera("Left_Ardu_cam")}; //LEFT IS ID 0, RIGHT IS ID 1
    // public PhotonPipelineResult cameraZeroResults, cameraOneResults;
    // public static Transform3d camToCenterRobotZero = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0,0,0));//Cam mounted facing forward, half a meter forward of center, half a meter up from center. //TODO: need change
    // public static Transform3d camToCenterRobotOne = new Transform3d(new Translation3d(0.5,0.0,0.5), new Rotation3d(0,0,0));
    // public static PhotonPoseEstimator photonPoseEstimatorOne  = new PhotonPoseEstimator(aprilFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameras[1], VisionConstants.camToCenterRobotOne);
    public static Field2d theField = new Field2d();
    public static PhotonPoseEstimator[] photonPoseEstimators  = 
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
        photonPoseEstimators[0].setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);//TODO: there's also avg_best_targets but IDK how that would work with 1 tag, so does lowest_ambiguity gotta check the GUI to make sure lowest ambiguity works
        photonPoseEstimators[1].setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
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
    @Deprecated
    /**This method have is under the threat of very unsyncrinized timeStamp because we're taking the sum of everything and dividing it
     * We should have a separate class that deal with the whole two cameras separately like how Ranger Robotics did theirs (they had their own PoseEstimator to take care the vision inputs)
     * 
     * In Theory we could just pass in the vision estimated pose and ambiguity and then the poseEstimator do the weighting part, not we give it a weighted part...
     */
    public static double xSum = 0, ySum = 0;
    public static int camNumerator = 0;
    public static double movementAddition = 10;
    public static double timeStampSum = 0; //Ok so techniqully you're supposed to do this separately but we're doing all our processing on a coprossesor so I think we should be good.
    public static void updateCamerasPoseEstimation(Swerve s_Swerve, SwerveDrivePoseEstimator poseEstimator, double 
    camTrustValue)
    {

        // double timestamp = 0;
        double localCamTrustVal = camTrustValue;
        xSum = 0;
        ySum = 0;
        camNumerator = 0;
        // double visionStdDev = camTrustValue;
        
        // double xSum = 0;
        // double ySum = 0;
        // double deltaSum = 0;
        // int numOfCams = 0;
        // Pose3d[] weightedEstimatePose;
        // double nonMultiAddition = 20;
        // int[] weighs = {1,1}; 
        //Initialize the array this way for future camera addition if needed
        // int[] weighs = new int[cameras.length];
        // for(int i = 0; i < weighs.length; i ++)
        // {
        //     weighs[i] = 1;
        // }

        // Optional<EstimatedRobotPose>[] optionalEstimateRobotPose = new Optional[cameras.length];
        // for (int i = 0; i < cameras.length; i++) {
        // // if (!Logger.hasReplaySource()) {
        //     optionalEstimateRobotPose[i] = photonPoseEstimators[i].update();
        // // }

        // Logger.processInputs(
            // "Estimates/AprilTag/" + cameras[i].getCameraName(), visionInputs[i]);
        // }
        for(int i = 0; i < cameras.length; i ++)
        {
            // String instanceLogKey =
            // "Estimates/AprilTag/" + camera[camera].getCameraName() + "/";
            // Optional<EstimatedRobotPose> estimatedRobotPose = photonPoseEstimators.update();
            Optional<EstimatedRobotPose> estimatedRobotPose = photonPoseEstimators[i].update();
            estimatedRobotPose.ifPresentOrElse(
                estimate -> {
                    // SmartDashboard.putNumber("estimated x", estimate.estimatedPose.getX());
                    // SmartDashboard.putNumber("estimated y", estimate.estimatedPose.getY());
                    // if(                   
                    // !(estimate.estimatedPose.getX() < -VisionConstants.fieldBorderMargin
                    // || estimate.estimatedPose.getX()
                    //     > VisionConstants.fieldSize.getX() + VisionConstants.fieldBorderMargin
                    // || estimate.estimatedPose.getY() < -VisionConstants.fieldBorderMargin
                    // || estimate.estimatedPose.getY()
                    //     > VisionConstants.fieldSize.getY() + VisionConstants.fieldBorderMargin
                    // || estimate.estimatedPose.getZ() < -VisionConstants.zMargin
                    // || estimate.estimatedPose.getZ() > VisionConstants.zMargin)
                    // )
                    // {
                        xSum += estimate.estimatedPose.getX();
                        ySum += estimate.estimatedPose.getY();
                        
                        camNumerator ++;

                        timeStampSum += estimate.timestampSeconds;
                        
                        if(estimate.strategy.equals(PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR))
                        {
                            xSum += estimate.estimatedPose.getX();
                            ySum += estimate.estimatedPose.getY();
                            timeStampSum += estimate.timestampSeconds;
                            camNumerator ++;
                        }
                    //}
                },
                () -> {
                    // SmartDashboard.putNumber("estimated x", 0);
                    // SmartDashboard.putNumber("estimated y", 0);
                }
            );
           
        // Optional<EstimatedRobotPose> closestPose = null;
        // double closetDistanceToPrevTarget = Double.MAX_VALUE;
        // Pose2d prevPose = poseEstimator.getEstimatedPosition();
        // boolean hasTarget = false;
        // for(int i = 0; i < cameras.length; i ++)
        // {
            
        //     photonPoseEstimators[i].setReferencePose(prevPose);
        //     Optional<EstimatedRobotPose> estimatedRobotPose = photonPoseEstimators[i].update();

            
        //     if(estimatedRobotPose.isPresent()) {
        //         double estimatedX = estimatedRobotPose.get().estimatedPose.getX();
        //         double estimatedY = estimatedRobotPose.get().estimatedPose.getY();
        //         if(closestPose != null) {
                    
        //             double distanceToPrevPose = PhotonUtils.getDistanceToPose(prevPose, new Pose2d(estimatedX, estimatedY, new Rotation2d()));
        //             //System.out.println("distanceTo prevpose: " + distanceToPrevPose);
        //             if(distanceToPrevPose < closetDistanceToPrevTarget) {
        //                 //System.out.println("CLOSEST POSE UPDATE!");
        //                 closestPose = estimatedRobotPose;
        //                 closetDistanceToPrevTarget = distanceToPrevPose;
                        
        //             }
        //         } else {
        //             closestPose = estimatedRobotPose;
        //         }
        //         theField[i].setRobotPose(new Pose2d(estimatedX, estimatedY, new Rotation2d()));
        //         //poseEstimator.addVisionMeasurement(new Pose2d(estimatedX, estimatedY, poseEstimator.getEstimatedPosition().getRotation()), Timer.getFPGATimestamp() - latency);
        //         hasTarget = true;
        //     }
            
        }

        
        double avgX = camNumerator != 0 ? xSum/camNumerator : 0;
        double avgY = camNumerator != 0 ? ySum/camNumerator : 0;
        double avgTimeStamp = camNumerator != 0 ? timeStampSum/camNumerator : 0;

        Pose2d weightedEstimatePose = new Pose2d(new Translation2d(avgX, avgY), new Rotation2d());
        theField.setRobotPose(weightedEstimatePose);
        // SmartDashboard.putData("Weighted pose stuff", theField);
        // SmartDashboard.putNumber("xSum", xSum);
        // SmartDashboard.putNumber("ySum", ySum);
        // SmartDashboard.putNumber("camNumerator", camNumerator);
        // SmartDashboard.putNumber("localCamTrustVal", localCamTrustVal);
        // SmartDashboard.putNumber("Avgtimestamp", avgTimeStamp);
        // System.out.println(weightedEstimatePose.toString());
        if(s_Swerve.getRobotRelativeSpeeds().vxMetersPerSecond > 1 || s_Swerve.getRobotRelativeSpeeds().vyMetersPerSecond > 1)
        {
            localCamTrustVal += movementAddition;
        }
        if(camNumerator > 0)
        {
            poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(localCamTrustVal,localCamTrustVal, Double.MAX_VALUE));
            poseEstimator.addVisionMeasurement(weightedEstimatePose, timeStampSum );//addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds, Matrix<N3,N1> visionMeasurementStdDevs) : void
            // System.out.println("Updated vision inputs!");
        }
        // Reject estimates that are off of the field
        
        // double visionStdDev = .5;
        // double movementAddition = 10;
        // double nonMultiAddition = 20;
        
        // if(hasTarget) {
        //     if((s_Swerve.getRobotRelativeSpeeds().vxMetersPerSecond > 1 || s_Swerve.getRobotRelativeSpeeds().vyMetersPerSecond > 1)) {
        //         visionStdDev += movementAddition;
        //     }
        //     if(closestPose.get().strategy != PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR) {
        //         visionStdDev += nonMultiAddition;
        //     }
        //     //poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));
        //     poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));
        //     poseEstimator.addVisionMeasurement(new Pose2d(closestPose.get().estimatedPose.getX(), closestPose.get().estimatedPose.getY(), poseEstimator.getEstimatedPosition().getRotation()), closestPose.get().timestampSeconds);//Change If needed//Double.max_value for the last parameter because we don't want to believe the camera on rotation at all
        //     //System.out.println("X: " + closestPose.get().estimatedPose.getX() + " Y: " + closestPose.get().estimatedPose.getY() + " VisionStdDev: " + visionStdDev);
        //     //System.out.println("Added Measurement with deviation: " + visionStdDev + "Closest Pose X" + closestPose.get().estimatedPose.getX());
        // }
    }

    @Deprecated
    public static void updateCamerasPoseEstimation(SwerveDrivePoseEstimator poseEstimator, double camTrustValue)
    {
        ArrayList<Optional<EstimatedRobotPose>> estimatedPoses = getEstimatedGlobalPose(poseEstimator.getEstimatedPosition());
        PhotonPipelineResult cameraResult[] = {cameras[0].getLatestResult(), cameras[1].getLatestResult()};
        // if(cameraResult[0].getBestTarget() != null && cameraResult[1].getBestTarget() != null)
        // {
        //     if((Math.abs(cameraResult[0].getBestTarget().getBestCameraToTarget().getX() - cameraResult[1].getBestTarget().getBestCameraToTarget().getX())> 0.5) && (Math.abs(cameraResult[0].getBestTarget().getBestCameraToTarget().getY() - cameraResult[1].getBestTarget().getBestCameraToTarget().getY())> 0.5))
        //     {
        //         PhotonPipelineResult temp = cameraResult[0];
        //         cameraResult[0] = cameraResult[1];
        //         cameraResult[1] = temp;
        //     }
        // }
        if(estimatedPoses.get(0).isPresent() && estimatedPoses.get(1).isPresent() && (Math.abs(estimatedPoses.get(0).get().estimatedPose.getX() - estimatedPoses.get(1).get().estimatedPose.getX()) > 0.5) && (Math.abs(estimatedPoses.get(0).get().estimatedPose.getY() - estimatedPoses.get(1).get().estimatedPose.getY()) > 0.5))
        {
            // Optional<EstimatedRobotPose> temp = estimatedPoses.get(1);
            System.out.println("hi");
            estimatedPoses.add(estimatedPoses.remove(0));
        }
        for(int i = 0; i < cameras.length; i ++)
        {
             // photonPoseEstimators[i].setLastPose(poseEstimator.getEstimatedPosition());
            
             double latencySec = cameraResult[i].getLatencyMillis() /1000; 
             double distanceToTarget = Double.MAX_VALUE;
            //  if(cameraResult.getBestTarget() != null) {
            //      distanceToTarget = cameraResult.hasTargets() ? PhotonUtils.getDistanceToPose(poseEstimator.getEstimatedPosition(), new Pose2d(cameraResult.getBestTarget().getBestCameraToTarget().getX(), cameraResult.getBestTarget().getBestCameraToTarget().getY(), poseEstimator.getEstimatedPosition().getRotation())) : Double.MAX_VALUE;
            //  }
             if(cameraResult[i].getBestTarget() != null) {
                
                distanceToTarget = cameraResult[i].hasTargets() ? PhotonUtils.getDistanceToPose(poseEstimator.getEstimatedPosition(), new Pose2d(cameraResult[i].getBestTarget().getBestCameraToTarget().getX(), cameraResult[i].getBestTarget().getBestCameraToTarget().getY(), poseEstimator.getEstimatedPosition().getRotation())) : Double.MAX_VALUE;
            }

            // range = Math.abs(range);

            // double range = 0; //range set to 0 to test
            // if(cameraResult.getMultiTagResult().estimatedPose.isPresent)
            // {
            //     Transform3d fieldToCamera = cameraResult.getMultiTagResult().estimatedPose.best;
            //     // Pose2d newPose = new Pose2d(new Translation2d(fieldToCamera.getX(), fieldToCamera.getY));
                // double visionStdDev = camTrustValues * -(1 + (range * range / 30));
                // double visionStdDev = 0.1;
                // if(photonPoseEstimators[i].getReferencePose() != null)
                // {
                    // poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(visionStdDev, visionStdDev, Double.MAX_VALUE));//Change If needed//Double.max_value for the last parameter because we don't want to believe the camera on rotation at all
                    // if(estimatedPoses.get(i).isPresent())
                    // {
                        // poseEstimator.addVisionMeasurement(new Pose2d(estimatedPoses.get(i).get().estimatedPose.getX(), estimatedPoses.get(i).get().estimatedPose.getY(), poseEstimator.getEstimatedPosition().getRotation()), Timer.getFPGATimestamp() - latencySec);
                        // theField[i].setRobotPose(new Pose2d(estimatedPoses.get(i).get().estimatedPose.getX(), estimatedPoses.get(i).get().estimatedPose.getY(), poseEstimator.getEstimatedPosition().getRotation()));
                        // SmartDashboard.putData("Pose for " + i, theField[i]);
                    // }
                    //System.out.println("Pose estimate updated for :" + i + " and it's visionStdDev Value is : " + visionStdDev + " range = " + distanceToTarget);
        }
    }

    public static ArrayList<Optional<EstimatedRobotPose>> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
        ArrayList<Optional<EstimatedRobotPose>> robotPoses = new ArrayList<Optional<EstimatedRobotPose>>();
        for(PhotonPoseEstimator estimator : photonPoseEstimators)
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
