
import java.util.*;

public class Main {

    final double eps = 1e-3;

    public static void main(String[] args) {
        new Main().main();
    }
    ArrayList<Vector2d> originalImage = null;
    int imageWidth = 0, imageHeight = 0;
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
    int minAreaRectNo = -1;
    double minArea = Double.MAX_VALUE;

    void inputPoints() {
        Scanner in = new Scanner(System.in);
        originalImage = new ArrayList<Vector2d>();
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            originalImage.add(new Vector2d(in.nextDouble(), in.nextDouble()));
            int cw = (int) originalImage.get(originalImage.size() - 1).x;
            int ch = (int) originalImage.get(originalImage.size() - 1).y;
            imageWidth = Math.max(imageWidth, cw);
            imageHeight = Math.max(imageHeight, ch);
        }
        n = in.nextInt();
        convexHull = new ArrayList<Vector2d>();
        for (int i = 0; i < n; i++) {
            convexHull.add(new Vector2d(in.nextDouble(), in.nextDouble()));
        }
        imageWidth = (int) (imageWidth * 1.5);
        imageHeight = (int) (imageHeight * 1.5);
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
            cosAngles.add(vNext.calculateAngleCos(vPrev));
            angles.add(Math.acos(cosAngles.get(cosAngles.size() - 1)));
        }
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

                caliperAngles[i] = Math.acos(calipersDir.calculateAngleCos(sideDir));

                if (Math.abs(caliperAngles[i]) < eps) {
                    pointNumber[i] = (pointNumber[i] + 1) % convexHull.size();
                    needRecompute = true;
                }
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

    void findIntersections() {
        Vector2d[] rect = new Vector2d[4];
        for (int i = 0; i < calipers.length; i++) {
            int next = (i + 1) % calipers.length;
            Vector2d p = findIntersection(A[i], B[i], A[next], B[next], -C[i], -C[next]);
            rect[i] = p;
        }
        rects.add(rect);
    }

    void outputCalipers() {
        Vector2d[] rect = rects.get(rects.size() - 1);
        for (int i = 0; i < rect.length; i++) {
            SvgFormatter.line(rect[i], rect[(i + 1) % rect.length], "red");
        }
    }

    void outputCurrentSituation(double minAngle, double overallAngle, int iteration, double currentArea) {
        System.out.println("Iteration #" + iteration + "<br>");
        System.out.println("Overall angle = " + overallAngle);
        System.out.println("Current area = " + currentArea);
        System.out.println("<br>Calipers angles are: <ul>");
        for (double a : caliperAngles) {
            System.out.println("<li>" + toDeg(a) + "</li>");
        }
        System.out.println("</ul>");
        System.out.println("<br>Preparing to rotate by " + minAngle + " degrees<br>");

        SvgFormatter.startSvg(imageWidth, imageHeight);
        SvgFormatter.points(originalImage, "black");
        SvgFormatter.polygon(convexHull, "darkGreen");

        for (int i : pointNumber) {
            Vector2d p = convexHull.get(i);
            SvgFormatter.circle(p, 5, "black", "yellow");
        }

        outputCalipers();

        SvgFormatter.endSvg();
        System.out.println("<hr>");
    }

    void doRotatingIterations() {
        double overallAngle = 0;
        int iteration = 0;

        while (overallAngle < Math.PI / 2) {
            for (int i = 0; i < calipers.length; i++) {
                Vector2d p = convexHull.get(pointNumber[i]);
                A[i] = calipers[i].y;
                B[i] = -calipers[i].x;
                C[i] = -p.x * calipers[i].y + p.y * calipers[i].x;
            }

            findIntersections();

            Vector2d[] r = rects.get(rects.size() - 1);


            Vector2d side1 = r[1].subtract(r[0]), side2 = r[2].subtract(r[1]);
            double curArea = side1.length() * side2.length();
            if (curArea < minArea) {
                minArea = curArea;
                minAreaRectNo = rects.size() - 1;
            }

            int minAngleNum = 0;

            for (int i = 0; i < caliperAngles.length; i++) {
                if (caliperAngles[i] < caliperAngles[minAngleNum]) {
                    minAngleNum = i;
                }
            }
            overallAngle += caliperAngles[minAngleNum];
            outputCurrentSituation(toDeg(caliperAngles[minAngleNum]), toDeg(overallAngle), iteration, curArea);

            for (int i = 0; i < calipers.length; i++) {
                calipers[i] = calipers[i].rotate(caliperAngles[minAngleNum]);
            }

            computeCalipersAngles();
            iteration++;
        }
    }

    void startHtml() {
        System.out.println("<html><body>");
    }

    void endHtml() {
        System.out.println("</body></html>");
    }

    void outputFinalResult() {
        System.out.println("Minimal area = " + minArea+"<br>");
        System.out.println("Rectangle corners coordinates:<br>");
        System.out.println("<ul>");
        Vector2d[] rect = rects.get(minAreaRectNo);
        for (Vector2d corner: rect) {
            System.out.print("<li>" + corner + "</li>");
        }
        System.out.println("</ul>");
        
        SvgFormatter.startSvg(imageWidth, imageHeight);
        
        SvgFormatter.points(originalImage, "black");
        SvgFormatter.polygon(convexHull, "red");
        
        for (int i = 0; i < rect.length; i++) {
            SvgFormatter.line(rect[i], rect[(i+1)%rect.length], "blue");
        }
        
        SvgFormatter.endSvg();
    }

    void main() {
        inputPoints();
        calculateAngles();
        findBoundingCoords();
        makeCalipers();
        computeCalipersAngles();
        startHtml();
        doRotatingIterations();
        outputFinalResult();
        endHtml();
    }
}
