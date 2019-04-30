package control.csv;

import control.kinect.KinectAnathomy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import processing.core.PVector;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVTools {
    public static void writeCSV(Path path, CSVFormat format, String uuid, HashMap<KinectAnathomy, PVector> dd) {
        try {
            CSVPrinter p = new CSVPrinter(new FileWriter(path.toString(), true), format);

            List<Object> points = new ArrayList<>();
            points.add(uuid);
            for (KinectAnathomy ka :
                    KinectAnathomy.values()) {
                if (!KinectAnathomy.NOT_TRACKED.equals(ka) && !KinectAnathomy.LABEL.equals(ka)) {
                    PVector v = dd.get(ka);
                    points.add(v.x);
                    points.add(v.y);
                    points.add(v.z);
                }
            }

            p.printRecord(points);
            p.close(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, List<String>> readCSV(Path path, CSVFormat format, String... headers) {
        HashMap<String, List<String>> result = new HashMap<>();
        try {
            Iterable<CSVRecord> records = format.withFirstRecordAsHeader().parse(new FileReader(path.toString()));
            for (CSVRecord record :
                    records) {

                List<String> values = new ArrayList<>();

                for (String header :
                        headers) {
                    values.add(record.get(header));
                }
                result.put(record.get(headers[0]), values);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
