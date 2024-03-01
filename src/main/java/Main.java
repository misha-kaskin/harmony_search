import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.lang.Math.*;

public class Main {
    static final List<Color> COLORS = List.of(
            Colors.c1, Colors.c2, Colors.c3,
            Colors.c4, Colors.c5, Colors.c6,
            Colors.c7, Colors.c8, Colors.c9,
            Colors.c10, Colors.c11, Colors.c12,
            Colors.c13, Colors.c14, Colors.c15,
            Colors.c16, Colors.c17, Colors.c18,
            Colors.c19, Colors.c20, Colors.c21,
            Colors.c22, Colors.c23, Colors.c24,
            Colors.c25, Colors.c26, Colors.c27
    );

    static final int GROUP_NUMBER = 27;
    static final int IMG_WIDTH = 600;
    static final int IMG_HEIGHT = 600;
    static final int IMG_TYPE = 1;
    static final int RADIUS = 5;
    static final int MIN_Y = -150;
    static final int MIN_X = -150;
    static final int MAX_Y = 0;
    static final int MAX_X = 0;
    // (x^2 - 3) ^ 2 + (y^2 - 3) ^ 2
    // x^2 + y^2
    // x + y

    static final String FUNCTION = "x * sin (sqrt(abs(x))) + y * sin (sqrt(abs(y)))";
    static int vectorCount = 5;
    static double chooseProbability = 0.5;
    static double modProbability = 0.5;
    static double delta = 1;
    static int stepNumber = 30;
    static HarmonySearch harmonySearch;
    static BufferedImage img;
    static BufferedImage img1;
    static int iterationCount = 0;

    public static void main(String[] args) {
        Expression e = new ExpressionBuilder(FUNCTION)
                .variables("x", "y")
                .build();

        double[][] values = new double[IMG_WIDTH][IMG_HEIGHT];

        double x1 = MIN_X;
        double y1 = MIN_Y;

        double deltaX = ((double) MAX_X - x1) / IMG_WIDTH;
        double deltaY = ((double) MAX_Y - y1) / IMG_HEIGHT;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = 0; i < IMG_WIDTH; i++) {
            double localY = y1;

            for (int j = 0; j < IMG_HEIGHT; j++) {
                e.setVariable("x", x1);
                e.setVariable("y", localY);
                double value = e.evaluate();

                min = min(min, value);
                max = max(max, value);
                values[i][j] = value;

                localY += deltaY;
            }

            x1 += deltaX;
        }

        double step = (max - min) / GROUP_NUMBER;

        img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, IMG_TYPE);

        for (int i = 0; i < IMG_WIDTH; i++) {
            for (int j = 0; j < IMG_HEIGHT; j++) {
                int number = (int) ((values[i][j] - min) / step);
                number = min(GROUP_NUMBER - 1, number);

                int group = GROUP_NUMBER - 1 - number;

                Color color = COLORS.get(group);
                img.setRGB(i, IMG_HEIGHT - 1 - j, color.getRGB());
            }
        }

        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
        jFrame.setSize(900, 750);
        ImageIcon imageIcon = new ImageIcon(img);
        JLabel imgLabel = new JLabel(imageIcon);
        JLabel labelX = new JLabel("x = 0");
        JLabel labelY = new JLabel("y = 0");
        JLabel labelZ = new JLabel("z = 0");
        JPanel jPanel = new JPanel();
        JPanel imgPanel = new JPanel();

        imgLabel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                labelX.setText("x = " + mapX(e.getX()));
                labelY.setText("y = " + mapY(e.getY()));
                labelZ.setText("z = " + (int) values[e.getX()][IMG_HEIGHT - 1 - e.getY()]);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                labelX.setText("x = " + mapX(e.getX()));
                labelY.setText("y = " + mapY(e.getY()));
                labelZ.setText("z = " + (int) values[e.getX()][IMG_HEIGHT - 1 - e.getY()]);
            }
        });

        imgPanel.add(imgLabel);
        jFrame.add(imgPanel, BorderLayout.CENTER);

        jPanel.add(labelX);
        jPanel.add(labelY);
        jPanel.add(labelZ);
        jFrame.add(jPanel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(6, 2));
        JLabel vectorCountLabel = new JLabel("Кол-во векторов");
        SpinnerNumberModel snmVector = new SpinnerNumberModel(5, 1, 1000, 1);
        JSpinner vectorSpinner = new JSpinner(snmVector);
        vectorSpinner.addChangeListener(el -> vectorCount = (int) vectorSpinner.getValue());

        JLabel chooseProbabilityLabel = new JLabel("Вер-ть выбора");
        SpinnerNumberModel snmChoose = new SpinnerNumberModel(0.5, 0.01, 0.99, 0.01);
        JSpinner chooseSpinner = new JSpinner(snmChoose);
        chooseSpinner.addChangeListener(el -> chooseProbability = (double) chooseSpinner.getValue());

        JLabel modProbabilityLabel = new JLabel("Вер-ть модиф");
        SpinnerNumberModel snmMod = new SpinnerNumberModel(0.5, 0.01, 0.99, 0.01);
        JSpinner modSpinner = new JSpinner(snmMod);
        modSpinner.addChangeListener(el -> modProbability = (double) modSpinner.getValue());

        JLabel deltaLabel = new JLabel("Измен");
        SpinnerNumberModel snmDelta = new SpinnerNumberModel(1, 0.01, 50, 0.5);
        JSpinner deltaSpinner = new JSpinner(snmDelta);
        deltaSpinner.addChangeListener(el -> delta = (double) deltaSpinner.getValue());

        JLabel safeLabel = new JLabel("Сохр");
        JButton safeButton = new JButton("Сохр");
        safeButton.addActionListener(el -> {
            iterationCount = 0;
            harmonySearch = new HarmonySearch(vectorCount, MIN_X, MIN_Y, MAX_X, MAX_Y, chooseProbability, modProbability, delta, FUNCTION);
            imgLabel.setIcon(new ImageIcon(img));
            imgPanel.updateUI();
        });

        JLabel stepNumberLabel = new JLabel("Кол-во итераций");
        SpinnerNumberModel snmStepNumber = new SpinnerNumberModel(30, 1, 1000, 1);
        JSpinner stepNumberSpinner = new JSpinner(snmStepNumber);
        stepNumberSpinner.addChangeListener(el -> stepNumber = (int) stepNumberSpinner.getValue());

        rightPanel.add(vectorCountLabel);
        rightPanel.add(vectorSpinner);
        rightPanel.add(chooseProbabilityLabel);
        rightPanel.add(chooseSpinner);
        rightPanel.add(modProbabilityLabel);
        rightPanel.add(modSpinner);
        rightPanel.add(deltaLabel);
        rightPanel.add(deltaSpinner);
        rightPanel.add(stepNumberLabel);
        rightPanel.add(stepNumberSpinner);
        rightPanel.add(safeLabel);
        rightPanel.add(safeButton);

        JPanel externalPanel = new JPanel();
        externalPanel.add(rightPanel);

        JButton calcButton = new JButton("Вычислить шаг");
        calcButton.addActionListener(el -> {
            img1 = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, IMG_TYPE);

            for (int i = 0; i < IMG_WIDTH; i++) {
                for (int j = 0; j < IMG_HEIGHT; j++) {
                    img1.setRGB(i, j, img.getRGB(i, j));
                }
            }

            if (iterationCount == stepNumber - 1) {
                iterationCount++;
                Model m = harmonySearch.search();
                int minIdx = 0;
                for (int i = 0; i < m.vectorX.length; i++) {
                    int x1tmp = (int) m.vectorX[i];
                    int y1tmp = (int) m.vectorY[i];
                    int x2tmp = (int) m.vectorX[minIdx];
                    int y2tmp = (int) m.vectorY[minIdx];

                    double val1 = e.setVariable("x", x1tmp).setVariable("y", y1tmp).evaluate();
                    double val2 = e.setVariable("x", x2tmp).setVariable("y", y2tmp).evaluate();

                    if (val1 < val2) {
                        minIdx = i;
                    }
                }

                int xTmp = (int) m.getX();
                int yTmp = (int) m.getY();

                double val1 = e.setVariable("x", xTmp).setVariable("y", yTmp).evaluate();
                double val2 = e.setVariable("x", m.getVectorX()[minIdx])
                        .setVariable("y", m.getVectorY()[minIdx])
                        .evaluate();

                if (val1 < val2) {
                    drawCircle(xTmp, yTmp, Color.WHITE);
                } else {
                    drawCircle((int) m.getVectorX()[minIdx], (int) m.getVectorY()[minIdx], Color.WHITE);
                }

                imgLabel.setIcon(new ImageIcon(img1));
                imgPanel.updateUI();

                return;
            } else if (iterationCount > stepNumber - 1) {
                return;
            }

            Model m = harmonySearch.search();
            iterationCount++;

            for (int i = 0; i < m.vectorX.length; i++) {
                int x = (int) m.getVectorX()[i];
                int y = (int) m.getVectorY()[i];

                drawCircle(x, y, Color.WHITE);
            }

            if (m.isDeleted()) {
                drawCircle((int) m.getDelX(), (int) m.getDelY(), Color.GRAY);
            }

            drawCircle((int) m.getX(), (int) m.getY(), Color.BLACK);

            imgLabel.setIcon(new ImageIcon(img1));
            imgPanel.updateUI();
        });
        JPanel downPanel = new JPanel();
        downPanel.add(calcButton);

        jFrame.add(externalPanel, BorderLayout.EAST);
        jFrame.add(downPanel, BorderLayout.SOUTH);
        jFrame.setVisible(true);
    }

    static int mapX(int i) {
        double deltaX = (double) (MAX_X - MIN_X) / IMG_WIDTH;
        return MIN_X + (int) (deltaX * i);
    }

    static int mapY(int j) {
        j = IMG_HEIGHT - 1 - j;
        double deltaY = (double) (MAX_Y - MIN_Y) / IMG_HEIGHT;
        return MIN_Y + (int) (deltaY * j);
    }

    static int mapI(int x) {
        double deltaX = (double) (MAX_X - MIN_X) / IMG_WIDTH;
        int tmp = x - MIN_X;
        return (int) (tmp / deltaX);
    }

    static int mapJ(int y) {
        double deltaY = (double) (MAX_Y - MIN_Y) / IMG_HEIGHT;
        int tmp = y - MIN_Y;
        return IMG_HEIGHT - 1 - (int) (tmp / deltaY);
    }

    static void drawCircle(int x, int y, Color c) {
        x = mapI(x);
        y = mapJ(y);

        for (int i = x - RADIUS; i <= x + RADIUS; i++) {
            int k = min(IMG_WIDTH - 1, max(0, i));

            for (int j = y - RADIUS; j <= y + RADIUS; j++) {
                int l = min(IMG_HEIGHT - 1, max(0, j));

                double dist = sqrt(pow(x - k, 2) + pow(y - l, 2));
                if (dist < RADIUS - 1) {
                    img1.setRGB(k, l, c.getRGB());
                } else if (dist < RADIUS + 1) {
                    img1.setRGB(k, l, Color.GRAY.getRGB());
                }
            }
        }
    }
}
