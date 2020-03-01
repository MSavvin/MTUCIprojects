import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.Rectangle2D;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import javax.swing.*;

public class FractalExplorer {

    private int displaySize;
    private JImageDisplay image;
    private JButton resetButton;
    private FractalGenerator gen;
    private Rectangle2D.Double range;
/**
 *Обработка нажатия на кнопку сброса
 */
    private class FractalHandler implements ActionListener {
	
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("reset")) {
            range = new Rectangle2D.Double();
            gen.getInitialRange(range);

            drawFractal();
            }
        }
    }

    /**
     * Обработка зума по правому и левому клику мышью
     */
    private class MouseHandler extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {

            double scale;
            if(e.getButton()== MouseEvent.BUTTON1)
                scale=0.5;
            else
                scale= 1.5;

            double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width,
                    displaySize, e.getX());

            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height,
                    displaySize, e.getY());

            gen.recenterAndZoomRange(range, xCoord, yCoord, scale);
            drawFractal();
        }
    }

/**
 *  Конструктор класса. Служит для инициализации основных объектов проекта.
 */
    public FractalExplorer(int size) {
	displaySize = size;
	gen = new Mandelbrot();
	range = new Rectangle2D.Double();
	gen.getInitialRange(range);
    }

/**
 *  Создание формы
 */
    public void createAndShowGUI() {
		JFrame frame  = new JFrame("Fractal of Mandelbrot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout( new BorderLayout());

		FractalHandler handler = new FractalHandler();

		image = new JImageDisplay(displaySize, displaySize);
		frame.getContentPane().add(image, BorderLayout.NORTH);

		JPanel buttonsPanel = new JPanel();

		resetButton = new JButton("Reset");
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(handler);
		buttonsPanel.add(resetButton);

		frame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        frame.getContentPane().addMouseListener(new MouseHandler());

		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
    }
    /**
     *  Рисование фрактала. На время отрисовки блокируем кнопку сброса.
     */
    public void drawFractal() {
        resetButton.setEnabled(false);

        for (int y = 0; y < displaySize; y++) {
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height,displaySize, y);
            for (int x = 0; x < displaySize; x++) {
                double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, x);
                int numIters;
                int rgbColor = 0;
                float hue;

                numIters = gen.numIterations(xCoord, yCoord);
                if (numIters >= 0) {
                    hue = 0.7f + numIters / 200f;
                    rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                }

                image.drawPixel(x, y, rgbColor);
            }
            image.repaint();
        }
        resetButton.setEnabled(true);
    }
    /**
     *  Точка входа в приложение
     */
    public static void main(String[] args) {
	FractalExplorer explorer = new FractalExplorer(800);
	explorer.createAndShowGUI();
	explorer.drawFractal();
    }

}
