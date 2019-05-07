package main;

import control.algorithms.Statistics;
import control.csv.CSVTools;
import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import control.kinect.KinectSelector;
import kinect4WinSDK.SkeletonData;
import model.DancerData;
import org.apache.commons.csv.CSVFormat;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KinectMe extends PApplet {
    private static final int SCALE = 60;
    private static final int COLS = 60;
    private static final int ROWS = 60;
    private static final String DANCE_POSTURES_CSV_FILE = ".\\res\\dance_postures.csv";

    private Kinect kinect;
    private PShape floor;

    private boolean printHeader = true;
    private List<DancerData> ddl = readDancerDataFromCSV();

    @Override
    public void settings() {
        super.settings();
        size(640, 480, P3D);
    }

    @Override
    public void setup() {
        super.setup();
        smooth();
        stroke(255);

        kinect = new Kinect(this, null, null, null);
        kinect.setHandRadius(0);

        createFloor();
    }

    @Override
    public void draw() {
        background(0);

        //setCamera();

        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);


        DancerData dd1 = getDancerByUUID("8d611f91-ef19-4265-a3b6-99cb41010784");
        DancerData dd2 = new DancerData(this, kinect);

        if (dd1 != null || dd2 != null) {
            dd1.printDancer();
            dd2.printDancer();
        }

        System.out.println(Statistics.correlate(dd1, dd2));

        lights();

        makeFloor();
    }

    private DancerData getDancerByUUID(String uuid) {
        for (DancerData dd :
                ddl) {
            if (dd.getDancerUUID().equals(uuid)) return dd;
        }
        return null;
    }

    private void setCamera() {
        PVector spine = kinect.getSkelPos(KinectAnathomy.SPINE);

        if (spine != null) {
            PVector camPos = new PVector(width/2.f,
                    height/2.f,
                    (height/2.f) / tan(PI * 30.f / 180.f));

            camera(camPos.x, camPos.y, camPos.z,
                    spine.x, spine.y, spine.z,
                    0,1, 0);
        }
    }

    private void makeFloor() {
        pushMatrix();
        translate(-ROWS * SCALE / 2.f, 400, -COLS * SCALE /2.f);
        shape(floor);
        popMatrix();
    }

    private void createFloor() {
        stroke(255);
        noFill();

        floor = createShape();
        for (int z = 0; z < COLS; z++) {
            floor.beginShape(QUAD_STRIP);
            for (int x = 0; x < ROWS; x++) {
                floor.vertex(x * SCALE, 0, z * SCALE);
                floor.vertex(x * SCALE, 0, (z+1) * SCALE);
            }
            floor.endShape();
        }
    }

    private List<DancerData> readDancerDataFromCSV() {
        List<String> headers = generateHeaders(KinectAnathomy.NOT_TRACKED, KinectAnathomy.LABEL);

        HashMap<String, List<String>> allMoves = CSVTools.readCSV(
                Paths.get(DANCE_POSTURES_CSV_FILE),
                CSVFormat.EXCEL,
                headers.toArray(new String[headers.size()]));

        List<DancerData> ddl = new ArrayList<>();

        for (String key :
                allMoves.keySet()) {

            int i = 0;
            List<String> values = allMoves.get(key);
            PVector v = new PVector();
            HashMap<KinectAnathomy, PVector> data = new HashMap<>();

            for (String header :
                    headers) {
                if (header.endsWith("X")) {
                    v = new PVector();
                    v.x = Float.parseFloat(values.get(i));
                } else if (header.endsWith("Y")) {
                    v.y = Float.parseFloat(values.get(i));
                } else if (header.endsWith("Z")) {
                    v.z = Float.parseFloat(values.get(i));
                    data.put(KinectAnathomy.getEnumById(header.split("_Z")[0]), v);
                }
                i += 1;
            }
            ddl.add(new DancerData(this, key, data));
        }

        return ddl;
    }

    @Override
    public void mouseClicked() {
        readDancerDataFromCSV();
    }

    private void writeDancerDataToCSV() {
        DancerData dd = new DancerData(this, kinect);

        List<String> headers = generateHeaders(KinectAnathomy.LABEL, KinectAnathomy.NOT_TRACKED);
        CSVFormat format;
        if (printHeader) {
            format = CSVFormat.EXCEL.withHeader(headers.toArray(new String[headers.size()]));
            printHeader = false;
        } else
            format = CSVFormat.EXCEL;


        CSVTools.writeCSV(Paths.get(DANCE_POSTURES_CSV_FILE), format, dd.getDancerUUID(), dd.getAnathomyData());
        System.out.println("DANCE POSTURE SAVED (" + dd.getDancerUUID() + ")");
    }

    private List<String> generateHeaders(KinectAnathomy label, KinectAnathomy notTracked) {
        List<String> headers = new ArrayList<>();
        headers.add("ID");
        for (KinectAnathomy ka :
                KinectAnathomy.values()) {
            if (!label.equals(ka) && !notTracked.equals(ka)) {
                headers.add(ka.getId() + "_X");
                headers.add(ka.getId() + "_Y");
                headers.add(ka.getId() + "_Z");
            }
        }
        return headers;
    }

    public void appearEvent(SkeletonData _s) {
        kinect.appearEvent(_s);
    }

    public void disappearEvent(SkeletonData skel) {
        kinect.disappearEvent(skel);
    }

    public void moveEvent(SkeletonData _b, SkeletonData _a) {
        kinect.moveEvent(_b, _a);
    }

    public static void main(String[] args) {
        PApplet.main("main.KinectMe");
    }
}
