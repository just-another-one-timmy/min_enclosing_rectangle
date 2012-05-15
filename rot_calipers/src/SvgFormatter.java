
import java.util.*;
import java.io.*;

public class SvgFormatter {

    public static void line(PrintWriter pw, Vector2d begin, Vector2d end, String color) {
        pw.println("<line x1='" + begin.x + "' y1='" + begin.y + "' x2='" + end.x + "' y2='" + end.y + "' style='stroke:" + color + "'/>");
    }

    public static void point(PrintWriter pw, Vector2d p, String color) {
        pw.println("<circle cx='"+p.x+"' cy='"+p.y+"' r='1' stroke='"+color+"' />");
    }

    public static void circle(PrintWriter pw, Vector2d center, int r, String stroke, String fill) {
        pw.println("<circle cx='"+center.x+"' cy='"+center.y+"' r='"+r+"' stroke='"+stroke+"' fill='"+fill+"'/>");
    }

    public static void polygon(PrintWriter pw, ArrayList<Vector2d> vertices, String color) {
        pw.print("<polygon points='");
        
        for (Vector2d v: vertices) {
            pw.print(v.x+","+v.y+" ");
        }
        
        pw.println("' stroke='"+color+"' fill='none' stroke-width='4'/>");
    }

    public static void points(PrintWriter pw, ArrayList<Vector2d> points, String color) {
        for (Vector2d p: points) {
            point(pw, p, color);
        }
    }

    public static void startSvg(String svgFileName, PrintWriter pw, int width, int height) {
        System.out.println("<image border=\"1\" src=\"imgs/" + svgFileName + "\" />");
        pw.println("<svg width='" + width + "' height='" + height +"'>");
    }

    public static void endSvg(PrintWriter pw) {
        pw.println("</svg>");
        pw.flush();
        pw.close();
    }
}
