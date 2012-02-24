
import java.util.*;

public class Main {

    final double eps = 1e-3;

    public static void main(String[] args) {
        int needNo = -1;
        if (args.length > 0) {
            needNo = Integer.parseInt(args[0]);
        }
        new Main().main(needNo);
    }
    ArrayList<Vector2d> originalImage = null;
    ArrayList<Vector2d> convexHull = null;
    // angles[i] -- angle between vectors (point[i-1],point[i]) and (point[i], point[i+1])
    ArrayList<Double> cosAngles = null, angles = null;
    double left, right, top, bottom;
    Vector2d[] calipers = null;
    // which point caliper is touching; if there are two, then last in caliper's direction
    int[] pointNumber = null;
    // angle between caliper and next edge
    double[] caliperAngles = null;
    double[] A = null, B = null, C = null; // for line equations
    ArrayList<Vector2d[]> rects = new ArrayList<Vector2d[]>();

    void inputPoints() {
        Scanner in = new Scanner(System.in);
        originalImage = new ArrayList<Vector2d>();
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            originalImage.add(new Vector2d(in.nextDouble(), in.nextDouble()));
        }
        n = in.nextInt();
        convexHull = new ArrayList<Vector2d>();
        for (int i = 0; i < n; i++) {
            convexHull.add(new Vector2d(in.nextDouble(), in.nextDouble()));
        }
    }

    double toDeg(double x) {
        return x * 180 / Math.PI;
    }

    void calculateAngles() {
        cosAngles = new ArrayList<Double>();
        angles = new ArrayList<Double>();
        for (int i = 0; i < convexHull.size(); i++) {
            int prev = i - 1 < 0 ? convexHull.size() - 1 : i - 1,
                    next = (i + 1) % convexHull.size();
            Vector2d vNext = convexHull.get(next).subtract(convexHull.get(i)),
                    vPrev = convexHull.get(i).subtract(convexHull.get(prev));
            //System.out.println("i = " + i);
            //System.out.println("vNext = " + vNext);
            //System.out.println("vPrev = " + vPrev);
            cosAngles.add(vNext.calculateAngleCos(vPrev));
            angles.add(Math.acos(cosAngles.get(cosAngles.size() - 1)));
        }
        //for (int i = 0; i < angles.size(); i++) {
        //    double c = cosAngles.get(i), a = angles.get(i);
        //    System.out.println(c + " -> " + toDeg(a));
        //}
    }

    void findBoundingCoords() {
        left = convexHull.get(0).x;
        right = convexHull.get(0).x;
        top = convexHull.get(0).y;
        bottom = convexHull.get(0).y;
        for (Vector2d p : convexHull) {
            left = Math.min(left, p.x);
            right = Math.max(right, p.x);
            top = Math.max(top, p.y);
            bottom = Math.min(bottom, p.y);
        }
    }

    void makeCalipers() {
        calipers = new Vector2d[4];
        calipers[0] = new Vector2d(0, -1); // left
        calipers[1] = new Vector2d(+1, 0); // bot
        calipers[2] = new Vector2d(0, +1); // right
        calipers[3] = new Vector2d(-1, 0); // top
        pointNumber = new int[calipers.length];
        caliperAngles = new double[calipers.length];
        A = new double[calipers.length];
        B = new double[calipers.length];
        C = new double[calipers.length];
        for (int i = 0; i < convexHull.size(); i++) {
            Vector2d p = convexHull.get(i);
            if (p.x < convexHull.get(pointNumber[0]).x) {
                pointNumber[0] = i;
            }
            if (p.y < convexHull.get(pointNumber[1]).y) {
                pointNumber[1] = i;
            }
            if (p.x > convexHull.get(pointNumber[2]).x) {
                pointNumber[2] = i;
            }
            if (p.y > convexHull.get(pointNumber[3]).y) {
                pointNumber[3] = i;
            }
        }
        //System.out.println("Starting rect:");
        //for (Vector2d s : calipers) {
        //    System.out.println(s);
        //}
    }

    // makes vector from points[vertexNumber] to points[vertexNumber+1]
    Vector2d makeVector(int vertexNumber) {
        return convexHull.get((vertexNumber + 1) % convexHull.size()).subtract(convexHull.get(vertexNumber));
    }

    void computeCalipersAngles() {
        boolean needRecompute = true;
        do {
            needRecompute = false;
            for (int i = 0; i < calipers.length; i++) {
                Vector2d calipersDir = calipers[i];
                Vector2d sideDir = makeVector(pointNumber[i]);
                //System.out.println("i = " + i);
                //System.out.println("calipersDir = " + calipersDir);
                //System.out.println("side = " + sideDir);
                caliperAngles[i] = Math.acos(calipersDir.calculateAngleCos(sideDir));
                //System.out.println("angle = " + toDeg(caliperAngles[i]));
                if (Math.abs(caliperAngles[i]) < eps) {
                    pointNumber[i] = (pointNumber[i] + 1) % convexHull.size();
                    needRecompute = true;
                }
            }
            if (needRecompute) {
                //System.out.println("Zero angles exist, recomputing needed");
            }
        } while (needRecompute);
    }

    Vector2d findIntersection(double a, double b, double c, double d, double e, double f) {
        double det = a * d - b * c;
        if (det == 0) {
            System.exit(-123);
        }
        return new Vector2d((e * d - b * f) / det, (a * f - e * c) / det);
    }

    void findIntersections(boolean addRect) {
        Vector2d[] rect = new Vector2d[4];
        for (int i = 0; i < calipers.length; i++) {
            int next = (i + 1) % calipers.length;
            Vector2d p = findIntersection(A[i], B[i], A[next], B[next], -C[i], -C[next]);
            rect[i] = p;
            //System.out.println("Intersection point " + i + ": " + p);
        }
        if (addRect) {
            rects.add(rect);
        }
    }

    void doRotatingIterations(int needNo) {
        double overallAngle = 0;
        int iterationNo = 0;
        while (overallAngle < Math.PI / 2) {

            //System.out.println("Now lines are:");
            for (int i = 0; i < calipers.length; i++) {
                Vector2d p = convexHull.get(pointNumber[i]);
                A[i] = calipers[i].y;
                B[i] = -calipers[i].x;
                C[i] = -p.x * calipers[i].y + p.y * calipers[i].x;
            }

            findIntersections(iterationNo == needNo);

            //System.out.println("iteration! overallAngle = " + overallAngle);
            int minAngleNum = 0;

            for (int i = 0; i < caliperAngles.length; i++) {
                if (caliperAngles[i] < caliperAngles[minAngleNum]) {
                    minAngleNum = i;
                }
            }

            overallAngle += caliperAngles[minAngleNum];

            System.out.println("Rotating by " + toDeg(caliperAngles[minAngleNum]));

            for (int i = 0; i < calipers.length; i++) {
                //System.out.print(calipers[i] + " (rotating) --> ");
                calipers[i] = calipers[i].rotate(caliperAngles[minAngleNum]);
                //System.out.println(calipers[i]);
            }

            computeCalipersAngles();
            iterationNo++;
        }
    }

    void output() {
        System.out.println("<html><body><svg width=\"1000\" height=\"1000\"><g transform=\"translate(10,20) scale(1)\">");
        outputOriginalImage();
        outputConvexHull();
        outputRects();
        System.out.println("</g></svg></body></html>");
    }

    void outputOriginalImage() {
        for (Vector2d p : originalImage) {
            System.out.println("<circle cx=\"" + p.x + "\" cy=\"" + p.y + "\" r=\"1\" />");
        }
    }

    void outputConvexHull() {
        System.out.print("<polygon points=\"");

        for (Vector2d p : convexHull) {
            System.out.print(p.x + "," + p.y + " ");
        }

        System.out.println("\" style=\"fill:none; stroke:DarkGreen;stroke-width:1\" />");
    }

    void outputRects() {
        for (Vector2d[] rect : rects) {
            System.out.print("<polygon points=\"");
            for (Vector2d p : rect) {
                System.out.print(p.x + "," + p.y + " ");
            }
            System.out.println("\" style=\"fill:none; stroke:DarkRed;stroke-width:1\" />");
        }
    }

    void main(int needNo) {
        inputPoints();
        calculateAngles();
        findBoundingCoords();
        makeCalipers();
        computeCalipersAngles();
        doRotatingIterations(needNo);
        output();
    }
}
