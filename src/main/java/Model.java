public class Model {
    final double[] vectorX;
    final double[] vectorY;
    final double x;
    final double y;
    final boolean isDeleted;
    final double delX;
    final double delY;

    public Model(double[] vectorX, double[] vectorY, double x, double y, boolean isDeleted, double delX, double delY) {
        this.vectorX = vectorX;
        this.vectorY = vectorY;
        this.x = x;
        this.y = y;
        this.isDeleted = isDeleted;
        this.delX = delX;
        this.delY = delY;
    }

    public double[] getVectorX() {
        return vectorX;
    }

    public double[] getVectorY() {
        return vectorY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDelX() {
        return delX;
    }

    public double getDelY() {
        return delY;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
