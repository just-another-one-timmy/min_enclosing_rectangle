
import java.util.*;

public class SvgFormatter {

    public static void line(Vector2d begin, Vector2d end, String color) {
        System.out.println("<line x1='" + begin.x + "' y1='" + begin.y + "' x2='" + end.x + "' y2='" + end.y + "' style='stroke:" + color + "'/>");
    }

    public static void point(Vector2d p, String color) {
        System.out.println("<circle cx='"+p.x+"' cy='"+p.y+"' r='1' stroke='"+color+"' />");
    }

    public static void circle(Vector2d center, int r, String stroke, String fill) {
        System.out.println("<circle cx='"+center.x+"' cy='"+center.y+"' r='"+r+"' stroke='"+stroke+"' fill='"+fill+"'/>");
    }

    public static void polygon(ArrayList<Vector2d> vertices, String color) {
        System.out.print("<polygon points='");
        
        for (Vector2d v: vertices) {
            System.out.print(v.x+","+v.y+" ");
        }
        
        System.out.println("' stroke='"+color+"' fill='none' stroke-width='4'/>");
    }

    public static void points(ArrayList<Vector2d> points, String color) {
        for (Vector2d p: points) {
            point(p, color);
        }
    }

    public static void startSvg(int width, int height) {
        System.out.println("<svg width='" + width + "' height='" + height +"'>");
    }

    public static void endSvg() {
        System.out.println("</svg>");
    }
}
