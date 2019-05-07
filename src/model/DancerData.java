import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.HashMap;
import java.util.UUID;

public class DancerData {
    private PApplet parent;
    private String dancerUUID;
    private HashMap<KinectAnathomy, PVector> anathomyData;

    public DancerData(PApplet parent, String dancerUUID, HashMap<KinectAnathomy, PVector> anathomyData) {
        this.parent = parent;
        this.dancerUUID = dancerUUID;
        this.anathomyData = anathomyData;
    }

    public DancerData(PApplet parent, Kinect kinect) {
        this.parent = parent;
        dancerUUID = UUID.randomUUID().toString();
        anathomyData = new HashMap<>();
        for (KinectAnathomy ka :
                KinectAnathomy.values()) {
            if (!KinectAnathomy.LABEL.equals(ka) && !KinectAnathomy.NOT_TRACKED.equals(ka))
                anathomyData.put(ka, kinect.getSkelPos(ka));
        }
    }

    public String getDancerUUID() {
        return dancerUUID;
    }

    public HashMap<KinectAnathomy, PVector> getAnathomyData() {
        return anathomyData;
    }

    public PVector getAnathomyVector(KinectAnathomy ka) {
        return anathomyData.get(ka);
    }

    public void printDancer() {
        if (!anathomyData.isEmpty()) {
            // Body
            drawBody();

            // Left Arm
            drawLeftArm();

            // Right Arm
            drawRightArm();

            // Left Leg
            drawLeftLeg();

            // Right Leg
            drawRightLeg();
        }
    }

    private void drawRightLeg() {
        drawBone(anathomyData.get(KinectAnathomy.HIP_RIGHT), anathomyData.get(KinectAnathomy.KNEE_RIGHT));
        drawBone(anathomyData.get(KinectAnathomy.KNEE_RIGHT), anathomyData.get(KinectAnathomy.ANKLE_RIGHT));
        drawBone(anathomyData.get(KinectAnathomy.ANKLE_RIGHT), anathomyData.get(KinectAnathomy.FOOT_RIGHT));
    }

    private void drawLeftLeg() {
        drawBone(anathomyData.get(KinectAnathomy.HIP_LEFT), anathomyData.get(KinectAnathomy.KNEE_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.KNEE_LEFT), anathomyData.get(KinectAnathomy.ANKLE_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.ANKLE_LEFT), anathomyData.get(KinectAnathomy.FOOT_LEFT));
    }

    private void drawRightArm() {
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_RIGHT), anathomyData.get(KinectAnathomy.ELBOW_RIGHT));
        drawBone(anathomyData.get(KinectAnathomy.ELBOW_RIGHT), anathomyData.get(KinectAnathomy.WRIST_RIGHT));
        drawBone(anathomyData.get(KinectAnathomy.WRIST_RIGHT), anathomyData.get(KinectAnathomy.HAND_RIGHT));
    }

    private void drawLeftArm() {
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_LEFT), anathomyData.get(KinectAnathomy.ELBOW_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.ELBOW_LEFT), anathomyData.get(KinectAnathomy.WRIST_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.WRIST_LEFT), anathomyData.get(KinectAnathomy.HAND_LEFT));
    }

    private void drawBody() {
        drawBone(anathomyData.get(KinectAnathomy.HEAD), anathomyData.get(KinectAnathomy.SHOULDER_CENTER));
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_CENTER), anathomyData.get(KinectAnathomy.SHOULDER_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_CENTER), anathomyData.get(KinectAnathomy.SHOULDER_RIGHT));
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_CENTER), anathomyData.get(KinectAnathomy.SPINE));
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_LEFT), anathomyData.get(KinectAnathomy.SPINE));
        drawBone(anathomyData.get(KinectAnathomy.SHOULDER_RIGHT), anathomyData.get(KinectAnathomy.SPINE));
        drawBone(anathomyData.get(KinectAnathomy.SPINE), anathomyData.get(KinectAnathomy.HIP_CENTER));
        drawBone(anathomyData.get(KinectAnathomy.HIP_CENTER), anathomyData.get(KinectAnathomy.HIP_LEFT));
        drawBone(anathomyData.get(KinectAnathomy.HIP_CENTER), anathomyData.get(KinectAnathomy.HIP_RIGHT));
    }

    private void drawBone(PVector v1, PVector v2) {
        if (v1 != null && v2 != null) {
            parent.pushStyle();
            parent.stroke(255, 0, 0);
            parent.strokeWeight(3.f);
            parent.line(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
            parent.popStyle();
        }
    }
}
