
import java.util.*;

public class Main {

    final double eps = 1e-3;

    public static void main(String[] args) {
        new Main().main();
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
    int minAreaRectNo = -1;
    ArrayList<Vector2d> highlightCircles = new ArrayList<Vector2d>();

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
                
                if (Double.isNaN(caliperAngles[i])) {
                    System.out.println("Nan occured: ");
                    System.out.println(calipersDir + "   *   " + sideDir);
                    System.out.println("<br>cos = " +calipersDir.calculateAngleCos(sideDir)  +"</br>");
                    outputCircle(convexHull.get(pointNumber[i]).x, convexHull.get(pointNumber[i]).y, 100, " style='stroke:red'");
                }
                
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
        for (int i = 0; i < calipers.length; i++) {
            highlightCircles.add(convexHull.get(pointNumber[i]));
        }
    }

    void outputCircle(double x, double y, double r, String style) {
        System.out.println("<circle cx = '" + x +"' cy='"+y+"' r='"+r+"' "+style+" />");
    }
    
    void outputTouchingPoints() {
        for (int x: pointNumber) {
            Vector2d p = convexHull.get(x);
            outputCircle(p.x, p.y, 1, " style=\"stroke:gold\"");
        }
    }
    
    void outputLine(Vector2d b, Vector2d e, String style) {
        System.out.print("<polyline points=\" ");
        
        System.out.print(b.x+","+b.y+" ");
        System.out.print(e.x+","+e.y+" ");
        
        System.out.println("\"" + style+" />");
    }
    
    void outputCalipers() {
        /*
        for (int i = 0; i < calipers.length; i++) {
            Vector2d c = calipers[i];
            Vector2d p = convexHull.get(pointNumber[i]);
            
            Vector2d one = new Vector2d(p.x + c.x*100, p.y + c.y*100);
            Vector2d two = new Vector2d(p.x - c.x*100, p.y - c.y*100);
            
            outputLine(one, two, " style=\"stroke:red\"");
        }*/
        Vector2d[] rect = rects.get(rects.size()-1);
        for (int i = 0; i < rect.length; i++) {
            outputLine(rect[i], rect[(i+1)%rect.length], " style=\"stroke:red\"");
        }
    }
    
    void outputCurrentSituation(double minAngle) {
        System.out.println("Highlighted points: ");
        for (int x: pointNumber) {
            System.out.print(" "+x);
        }
        System.out.println("<br>Preparing to rotate by " + minAngle+" degrees<br>");
        System.out.println("Calipers angles = <ul>");
        for (double a: caliperAngles) {
            System.out.println("<li>"+toDeg(a)+"</li>");
        }
        System.out.println("</ul>");
        
        System.out.println("<svg width='600' height='600' transofmation='scale(1)'>");
        
        outputOriginalImage();
        outputConvexHullPolygon();
        outputConvexHullPoints();
        outputTouchingPoints();
        outputCalipers();
        
        System.out.println("</svg>");
    }
    
    void doRotatingIterations() {
        double overallAngle = 0;
        double minArea = Double.MAX_VALUE;
                
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
            outputCurrentSituation(toDeg(caliperAngles[minAngleNum]));
            
            for (int i = 0; i < calipers.length; i++) {
                calipers[i] = calipers[i].rotate(caliperAngles[minAngleNum]);
            }

            computeCalipersAngles();
        }
    }

    void startHtml() {
        System.out.println("<html><body>");
    }
    void endHtml() {
        System.out.println("</body></html>");
    }
    
    
    void output() {
        System.out.println("<html><body><svg width=\"1000\" height=\"1000\"><g transform=\"translate(10,20) scale(1)\">");
        outputOriginalImage();
        outputConvexHullPolygon();
        outputConvexHullPoints();
        outputHighlightCircles();
        outputRects();
        System.out.println("</g></svg></body></html>");
    }

    void outputOriginalImage() {
        for (Vector2d p : originalImage) {
            System.out.println("<circle cx=\"" + p.x + "\" cy=\"" + p.y + "\" r=\"1\" />");
        }
    }

    void outputHighlightCircles() {
        for (Vector2d p : highlightCircles) {
            System.out.println("<circle cx=\"" + p.x + "\" cy=\"" + p.y + "\" r=\"3\" style=\"stroke:gold\"/>");
        }
    }

    void outputConvexHullPolygon() {
        System.out.print("<polygon points=\"");

        for (Vector2d p : convexHull) {
            System.out.print(p.x + "," + p.y + " ");
        }

        System.out.println("\" style=\"fill:none; stroke:DarkGreen;stroke-width:1\" />");
    }

    void outputConvexHullPoints() {
        for (Vector2d p: convexHull) {
            outputCircle(p.x, p.y, 2, " style='stroke:black'");
        }
    }
    
    void outputRects() {
        //for (Vector2d[] rect : rects) {
        System.out.print("<polygon points=\"");
        Vector2d[] rect = rects.get(minAreaRectNo);
        for (Vector2d p : rect) {
            System.out.print(p.x + "," + p.y + " ");
        }
        System.out.println("\" style=\"fill:none; stroke:DarkRed;stroke-width:1\" />");
        //}
    }

    void main() {
        inputPoints();
        calculateAngles();
        findBoundingCoords();
        makeCalipers();
        computeCalipersAngles();
        startHtml();
        doRotatingIterations();
        endHtml();
        //output();
    }
}
