package control.algorithms;

import control.kinect.KinectAnathomy;
import model.postures.DancerData;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Statistics {
    public static Double euclideanMSE(PApplet parent, DancerData pk, DancerData pCSV) {
        Double result = .0;
        Double totalPoints = .0;

        for (KinectAnathomy ka:
                KinectAnathomy.values()) {
            if (!KinectAnathomy.LABEL.equals(ka) && !KinectAnathomy.NOT_TRACKED.equals(ka)) {
                PVector kv = pk.getAnathomyVector(ka);
                PVector CSVv = pCSV.getAnathomyVector(ka);
                if (kv != null && CSVv != null) result += Math.pow(PVector.dist(kv, CSVv), 2);
                totalPoints += 1.;
            }
        }
        return result / totalPoints;
    }

    public static PVector mean(DancerData dd) {
        List<PVector> data = extractPoints(dd);
        return getAvarage(data);
    }

    public static PVector standardDeviation(DancerData dd) {
        PVector sd = new PVector();
        List<PVector> data = extractPoints(dd);
        PVector avarage = getAvarage(data);

        for (PVector datum :
                data) {
            sd.x = sd.x + (datum.x - avarage.x) * (datum.x - avarage.x);
            sd.y = sd.y + (datum.y - avarage.y) * (datum.y - avarage.y);
            sd.z = sd.z + (datum.z - avarage.z) * (datum.z - avarage.z);
        }

        sd.div(data.size() - 1);
        sd.x = (float) Math.sqrt(sd.x);
        sd.y = (float) Math.sqrt(sd.y);
        sd.z = (float) Math.sqrt(sd.z);

        return sd;
    }

    private static PVector getAvarage(List<PVector> data) {
        PVector avarage = new PVector();

        for (PVector v :
                data) {
            avarage.x += v.x;
            avarage.y += v.y;
            avarage.z += v.z;
        }

        return avarage.div(data.size());
    }

    private static List<PVector> extractPoints(DancerData dd) {
        List<PVector> data = new ArrayList<>();
        for (KinectAnathomy ka :
                KinectAnathomy.values()) {
            if (!KinectAnathomy.LABEL.equals(ka) && !KinectAnathomy.NOT_TRACKED.equals(ka)) {
                data.add(dd.getAnathomyVector(ka));
            }
        }
        return data;
    }

    public static PVector correlate(DancerData dd1, DancerData dd2) {
        List<PVector> x = normalize(dd1);
        List<PVector> y = normalize(dd2);

        if (x == null || y == null) return new PVector();

        List<PVector> xy = new ArrayList<>();

        for (int i = 0; i < x.size(); i++) {
            PVector v = new PVector();
            v.x = x.get(i).x * y.get(i).x;
            v.y = x.get(i).y * y.get(i).y;
            v.z = x.get(i).z * y.get(i).z;
            xy.add(v);
        }

        List<PVector> xx = square(x);
        List<PVector> yy = square(y);

        PVector sumx = sum(x);
        PVector sumy = sum(y);
        PVector sumxy = sum(xy);
        PVector sumxx = sum(xx);
        PVector sumyy = sum(yy);

        PVector sumx2 = new PVector();
        sumx2.x = sumx.x * sumx.x;
        sumx2.y = sumx.y * sumx.y;
        sumx2.z = sumx.z * sumx.z;

        PVector sumy2 = new PVector();
        sumy2.x = sumy.x * sumy.x;
        sumy2.y = sumy.y * sumy.y;
        sumy2.z = sumy.z * sumy.z;

        PVector sumxTimesSumy = new PVector();
        sumxTimesSumy.x = sumx.x * sumy.x;
        sumxTimesSumy.y = sumx.y * sumy.y;
        sumxTimesSumy.z = sumx.z * sumy.z;

        int n = dd1.getAnathomyData().size();
        Double rx = (n * sumxy.x - sumx.x * sumy.x) / Math.sqrt((n * sumxx.x - sumx2.x) * (n * sumyy.x - sumy2.x));
        Double ry = (n * sumxy.y - sumx.y * sumy.y) / Math.sqrt((n * sumxx.y - sumx2.y) * (n * sumyy.y - sumy2.y));
        Double rz = (n * sumxy.z - sumx.z * sumy.z) / Math.sqrt((n * sumxx.z - sumx2.z) * (n * sumyy.z - sumy2.z));

        return new PVector(rx.floatValue(), ry.floatValue(), rz.floatValue());
    }

    private static List<PVector> square(List<PVector> in) {
        return in.stream().map(v -> {
            PVector res = new PVector();
            res.x = v.x*v.x;
            res.y = v.y*v.y;
            res.z = v.z*v.z;
            return res;}).collect(Collectors.toList());
    }

    private static PVector sum(List<PVector> v) {
        return v.stream().reduce(new PVector(), (v1, v2) -> v1.add(v2));
    }

    private static List<PVector> normalize(DancerData dd) {
        List<PVector> norms = new ArrayList<>();
        for (KinectAnathomy ka:
                KinectAnathomy.values()) {
            if (!KinectAnathomy.LABEL.equals(ka) && !KinectAnathomy.NOT_TRACKED.equals(ka)) {
                PVector v1 = new PVector();
                PVector v2 = dd.getAnathomyVector(ka);
                if (v2 == null) return null;
                v1.x = v2.x;
                v1.y = v2.y;
                v1.z = v2.z;
                norms.add(v1.normalize());
            }
        }
        return norms;
    }
}
