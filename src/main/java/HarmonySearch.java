import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class HarmonySearch {
    double[] vectorX;
    double[] vectorY;
    double[] vectorZ;

    final int size;
    final double chooseProbability;
    final double modProbability;
    final double delta;
    final int minX;
    final int maxX;
    final int minY;
    final int maxY;
    final String function;

    public HarmonySearch(int size, int minX, int minY, int maxX, int maxY, double chooseProbability, double modProbability, double delta, String function) {
        this.size = size;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.function = function;

        vectorX = new double[size];
        vectorY = new double[size];
        vectorZ = new double[size];

        Expression e = new ExpressionBuilder(function)
                .variables("x", "y")
                .build();

        for (int i = 0; i < size; i++) {
            double x = vectorX[i] = RandGenerator.generate(minX, maxX);
            double y = vectorY[i] = RandGenerator.generate(minY, maxY);

            e.setVariable("x", x);
            e.setVariable("y", y);
            vectorZ[i] = e.evaluate();
        }

        this.chooseProbability = chooseProbability;
        this.modProbability = modProbability;
        this.delta = delta;
    }

    public Model search() {
        double x;
        double y;

        double eps = Math.random();

        if (eps < chooseProbability) {
            x = vectorX[numOfVector()];
            eps = Math.random();

            if (eps < modProbability) {
                double ksi = RandGenerator.generate(-1, 1);
                x += ksi * delta;
            }
        } else {
            x = RandGenerator.generate(minX, maxX);
        }

        eps = Math.random();

        if (eps < chooseProbability) {
            y = vectorY[numOfVector()];
            eps = Math.random();

            if (eps < modProbability) {
                double ksi = RandGenerator.generate(-1, 1);
                y += ksi * delta;
            }
        } else {
            y = RandGenerator.generate(minY, maxY);
        }

        Expression e = new ExpressionBuilder(function)
                .variables("x", "y")
                .build();

        e.setVariable("x", x);
        e.setVariable("y", y);

        double z = e.evaluate();

        int imax = 0;

        for (int i = 0; i < size; i++) {
            if (vectorZ[imax] < vectorZ[i]) {
                imax = i;
            }
        }

        if (vectorZ[imax] > z) {
            vectorZ[imax] = z;
            vectorX[imax] = x;
            vectorY[imax] = y;
        }

        return new Model(vectorX, vectorY);
    }

    int numOfVector() {
        double step = (double) 1 / size;

        double eps = Math.random();
        int num = (int) (eps / step);
        num = Math.min(size - 1, num);

        return num;
    }
}
