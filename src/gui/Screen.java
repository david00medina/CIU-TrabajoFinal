package gui;

import control.csv.CSVTools;
import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import model.postures.DancerData;
import org.apache.commons.csv.CSVFormat;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class Screen
{
    protected PApplet parent;
    protected HashMap<UISelector, PImage> UIResources;
    private String DANCE_POSTURES_CSV_FILE;

    Screen(PApplet parent, HashMap<UISelector, PImage> UIResources, String DANCE_POSTURES_CSV_FILE) {
        this.parent = parent;
        this.UIResources = UIResources;
        this.DANCE_POSTURES_CSV_FILE = DANCE_POSTURES_CSV_FILE;
    }

    Screen(PApplet parent, HashMap<UISelector, PImage> UIResources) {
        this.parent = parent;
        this.UIResources = UIResources;
    }

    abstract void show();

    List<DancerData> readDancerDataFromCSV() {
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
            ddl.add(new DancerData(parent, key, data));
        }

        return ddl;
    }

    protected boolean writeDancerDataToCSV(Kinect kinect, boolean printHeader) {
        DancerData dd = new DancerData(parent, kinect);

        List<String> headers = generateHeaders(KinectAnathomy.LABEL, KinectAnathomy.NOT_TRACKED);
        CSVFormat format;
        if (printHeader) {
            format = CSVFormat.EXCEL.withHeader(headers.toArray(new String[headers.size()]));
        } else {
            format = CSVFormat.EXCEL;
        }

        CSVTools.writeCSV(Paths.get(DANCE_POSTURES_CSV_FILE), format, dd.getDancerUUID(), dd.getAnathomyData());
        System.out.println("DANCE POSTURE SAVED (" + dd.getDancerUUID() + ")");

        return false;
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
}
