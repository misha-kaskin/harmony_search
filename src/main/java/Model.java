public class Model {
    final double[] vectorX;
    final double[] vectorY;

    public Model(double[] vectorX, double[] vectorY) {
        this.vectorX = vectorX;
        this.vectorY = vectorY;
    }

    public double[] getVectorX() {
        return vectorX;
    }

    public double[] getVectorY() {
        return vectorY;
    }
}
