
public class Vector2d {

    double x, y;

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    public Vector2d() {
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
        return this.scalarMultiplication(other) / (this.length() * other.length());
    }
}
