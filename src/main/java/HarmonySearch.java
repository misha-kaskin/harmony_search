import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Arrays;

import static java.lang.Math.*;

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

        double eps = random();

        if (eps < chooseProbability) {
            x = vectorX[numOfVector()];
            eps = random();

            if (eps < modProbability) {
                double ksi = RandGenerator.generate(-1, 1);
                x += ksi * delta;
                x = max(minX, min(x, maxX));
            }
        } else {
            x = RandGenerator.generate(minX, maxX);
        }

        eps = random();

        if (eps < chooseProbability) {
            y = vectorY[numOfVector()];
            eps = random();

            if (eps < modProbability) {
                double ksi = RandGenerator.generate(-1, 1);
                y += ksi * delta;
                y = max(minY, min(y, maxY));
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

        double[] vx;
        double[] vy;
        Model m;

        if (vectorZ[imax] > z) {
            vx = new double[vectorX.length - 1];
            vy = new double[vectorY.length - 1];

            for (int i = 0; i < imax; i++) {
                vx[i] = vectorX[i];
                vy[i] = vectorY[i];
            }

            for (int i = imax + 1; i < vectorX.length; i++) {
                vx[i - 1] = vectorX[i];
                vy[i - 1] = vectorY[i];
            }

            m = new Model(vx, vy, x, y, true, vectorX[imax], vectorY[imax]);

            vectorZ[imax] = z;
            vectorX[imax] = x;
            vectorY[imax] = y;
        } else {
            vx = Arrays.copyOf(vectorX, vectorX.length);
            vy = Arrays.copyOf(vectorY, vectorY.length);
            m = new Model(vx, vy, x, y, false, 0, 0);
        }

        return m;
    }

    int numOfVector() {
        double step = (double) 1 / size;

        double eps = random();
        int num = (int) (eps / step);
        num = min(size - 1, num);

        return num;
    }
}
