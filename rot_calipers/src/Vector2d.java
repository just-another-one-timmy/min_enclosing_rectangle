
public class Vector2d {

    double x, y;

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    public Vector2d(Vector2d from) {
        this.x = from.x;
        this.y = from.y;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(x - other.x, y - other.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double scalarMultiplication(Vector2d other) {
        return x * other.x + y * other.y;
    }

    public double calculateAngleCos(Vector2d other) {
        double res = this.scalarMultiplication(other) / (this.length() * other.length());
        res = Math.max(res, -1.0);
        res = Math.min(res, +1.0);
        return res;
    }

    public Vector2d rotate(double angle) {
        double cosa = Math.cos(angle), sina = Math.sin(angle);
        double newx = x * cosa - y * sina;
        double newy = x * sina + y * cosa;
        return new Vector2d(newx, newy);
    }
}
