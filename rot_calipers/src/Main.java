
import java.util.*;

public class Main {

    public static void main(String[] args) {
        new Main().main();
    }
    ArrayList<Vector2d> points = null;
    // angles[i] -- angle between vectors (point[i-1],point[i]) and (point[i], point[i+1])
    ArrayList<Double> cosAngles = null, angles = null;
    double left, right, top, bottom;

    void inputPoints() {
        Scanner in = new Scanner(System.in);
        points = new ArrayList<Vector2d>();
        while (in.hasNextDouble()) {
            points.add(new Vector2d(in.nextDouble(), in.nextDouble()));
        }
    }

    double toDeg(double x) {
        return x * 180 / Math.PI;
    }

    void calculateAngles() {
        cosAngles = new ArrayList<Double>();
        angles = new ArrayList<Double>();
        for (int i = 0; i < points.size(); i++) {
            int prev = i - 1 < 0 ? points.size() - 1 : i - 1,
                    next = (i + 1) % points.size();
            Vector2d vNext = points.get(next).subtract(points.get(i)),
                    vPrev = points.get(i).subtract(points.get(prev));
            System.out.println("i = " + i);
            System.out.println("vNext = " + vNext);
            System.out.println("vPrev = " + vPrev);
            cosAngles.add(vNext.calculateAngleCos(vPrev));
            angles.add(Math.acos(cosAngles.get(cosAngles.size() - 1)));
        }
        for (int i = 0; i < angles.size(); i++) {
            double c = cosAngles.get(i), a = angles.get(i);
            System.out.println(c + " -> " + toDeg(a));
        }
    }

    void findBoundingCoords() {
        left = points.get(0).x;
        right = points.get(0).x;
        top = points.get(0).y;
        bottom = points.get(0).y;
        for (Vector2d p : points) {
            left = Math.min(left, p.x);
            right = Math.max(right, p.x);
            top = Math.max(top, p.y);
            bottom = Math.max(bottom, p.y);
        }
    }

    void makeCalipers() {
        //
    }
    
    void main() {
        inputPoints();
        calculateAngles();
        findBoundingCoords();
    }
}
