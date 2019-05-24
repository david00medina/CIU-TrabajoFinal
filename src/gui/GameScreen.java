package gui;

import control.algorithms.Statistics;
import control.algorithms.Transformation;
import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import control.kinect.KinectSelector;
import kinect4WinSDK.SkeletonData;
import model.postures.DancerData;
import model.sound.Song;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

import java.util.List;
import java.util.Random;

public class GameScreen extends Screen
{
    private static final int SCALE = 60;
    private static final int COLS = 60;
    private static final int ROWS = 60;
    private static final int THRESHOLD = 2000;
    private static String POSTURES_DIR;

    private Kinect kinect;
    private PShape floor;
    private Song song;

    private boolean printHeader = false;
    private long timeBegin = 0;
    private int currentPos;

    private List<DancerData> ddl;

    GameScreen(PApplet parent, String csvPath, String posDir) {
        super(parent, csvPath);

        kinect = new Kinect(this.parent, null, null, null);
        kinect.setHandRadius(0);

        POSTURES_DIR = posDir;

        createFloor();

        preprocessDancerData();

        currentPos = new Random().nextInt(ddl.size());
    }

    private void preprocessDancerData() {
        ddl = readDancerDataFromCSV();
        translateToOrigin();
    }

    private void translateToOrigin() {
        for (DancerData dd :
                ddl) {
            Transformation.translateToOrigin(parent, dd, KinectAnathomy.SPINE);
        }
    }

    public void show() {
        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);

        DancerData liveDancer = new DancerData(parent, kinect);
        Transformation.translateToOrigin(parent, liveDancer, KinectAnathomy.SPINE);

        nextPosture();
        DancerData ddCSV = ddl.get(currentPos);

        System.out.println(getScore(liveDancer, ddCSV));

        makeFloor();
    }

    private DancerData getDancerDataByUUID(String uuid) {
        for (DancerData dd :
                ddl) {
            if (dd.getDancerUUID().equals(uuid)) return dd;
        }
        return null;
    }

    private int getScore(DancerData ddK, DancerData ddCSV) {
        double err = Statistics.euclideanMSE(parent, ddK, ddCSV);
        if (err > 2000) return 0;
        return (int) Math.abs(err - THRESHOLD);
    }

    private void nextPosture() {
        if(song.songPosition() - timeBegin >= 5000) {
            timeBegin = song.songPosition();
            currentPos = new Random().nextInt(ddl.size());

        }

        PImage img = parent.loadImage(POSTURES_DIR + "\\" + ddl.get(currentPos).getDancerUUID() + ".png");
        parent.pushMatrix();
        parent.translate(300, 300, 200);
//        ddl.get(currentPos).drawDancerData();
        parent.image(img, 0, 0, img.width / 4, img.height / 4);
        parent.popMatrix();
    }

    private void makeFloor() {
        parent.pushMatrix();
        parent.translate(-ROWS * SCALE / 2.f, 400, -COLS * SCALE /2.f);
        parent.shape(floor);
        parent.popMatrix();
    }

    private void createFloor() {
        parent.stroke(255);
        parent.noFill();

        floor = parent.createShape();
        for (int z = 0; z < COLS; z++) {
            floor.beginShape(parent.QUAD_STRIP);
            for (int x = 0; x < ROWS; x++) {
                floor.vertex(x * SCALE, 0, z * SCALE);
                floor.vertex(x * SCALE, 0, (z+1) * SCALE);
            }
            floor.endShape();
        }
    }

    public void setSong(Song song) {
        this.song = song;
        this.song.play();
    }

    void appearEvent(SkeletonData _s) {
        kinect.appearEvent(_s);
    }

    void disappearEvent(SkeletonData _s) {
        kinect.disappearEvent(_s);
    }

    void moveEvent(SkeletonData _b, SkeletonData _a) {
        kinect.moveEvent(_b, _a);
    }
}
