package model;

import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import processing.core.PVector;

import java.util.HashMap;
import java.util.UUID;

public class DancerData {
    private String dancerUUID;
    private HashMap<KinectAnathomy, PVector> anathomyData;

    public DancerData(String dancerUUID, HashMap<KinectAnathomy, PVector> anathomyData) {
        this.dancerUUID = dancerUUID;
        this.anathomyData = anathomyData;
    }

    public DancerData(Kinect kinect) {
        dancerUUID = UUID.randomUUID().toString();
        anathomyData = new HashMap<>();
        for (KinectAnathomy ka :
                KinectAnathomy.values()) {
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
}
