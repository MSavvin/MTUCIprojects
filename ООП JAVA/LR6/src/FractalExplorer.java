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
    private JComboBox<String> fractalChooser;

    private JButton saveButton;
    private JButton resetButton;
    //Данный изврат сделан из-за требований задачника.
    //Мы не можем переопределить метод toString() как статический
    //поэтому будем создавать все варианты для получения имени,
    //а при выборе - копировать "рабочий указатель" в gen
    private FractalGenerator gen;
    private FractalGenerator genMan;
    private FractalGenerator genCorn;
    private FractalGenerator genShip;
    //конец изврата
    private Rectangle2D.Double range;
    private int rowsRemaining;
    /**
     * Управление доступностью кнопок и прочих вещей во время рассчётов.
     */
    private void enableUIThings(boolean val) {
        fractalChooser.setEnabled(val);
        saveButton.setEnabled(val);
        resetButton.setEnabled(val);
    }
    /**
     *  Воркер для работы в отдельном потоке
     */
    private class FractalWorker extends SwingWorker<Object, Object> {

        private int external_y;

        private int[] RGBVals;

        //Передаём в конструкторе номер текущей строки с которой будем работать
        public FractalWorker(int y) {
            external_y = y;
        }
        //Метод, непосредственно обрабатываемый в отдельном потоке
        public Object doInBackground() {

            RGBVals = new int[displaySize];
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, displaySize, external_y);

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

                RGBVals[x] = rgbColor;
            }

            return null;
        }
        // Метод, выполняемый после заверщения работы.
        public void done() {
            for (int x = 0; x < displaySize; x++) {
                image.drawPixel(x, external_y, RGBVals[x]);
            }

            image.repaint(0, 0, external_y, displaySize, 1);
            rowsRemaining--;
            if (rowsRemaining < 1) {
                enableUIThings(true);
            }
        }
    }
    
    /**
    * Обработка взаимодействия с интерфейсом
    */
    private class FractalHandler implements ActionListener {
	
        public void actionPerformed(ActionEvent e) {
            // если пришло от комбобокса...
            if (e.getSource() == fractalChooser) {
                String currentItem = fractalChooser.getSelectedItem().toString();

                if(genMan.toString() == currentItem){
                    gen = genMan;
                }else if(genCorn.toString() == currentItem){
                    gen = genCorn;
                }else if(genShip.toString() == currentItem){
                    gen = genShip;
                }

                range = new Rectangle2D.Double();
                gen.getInitialRange(range);
                drawFractal();

            }else {
            //... иначе смотрим от какой кнопки пришло
                    switch(e.getActionCommand())
                    {
                        case "reset":
                            range = new Rectangle2D.Double();
                            gen.getInitialRange(range);
                            drawFractal();
                            break;
                        case "save":
                            String imgPath = "./img";

                            File f = new File(imgPath);
                            if(!f.exists()) {
                              f.mkdir();
                            }

                            JFileChooser chooser = new JFileChooser(imgPath);
                            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                            chooser.setFileFilter(filter);
                            chooser.setAcceptAllFileFilterUsed(false);

                            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    f = chooser.getSelectedFile();
                                    String filePath = f.getPath();
                                    if (!filePath.toLowerCase().endsWith(".png")) {
                                        f = new File(filePath + ".png");
                                    }

                                    ImageIO.write(image.getImage(), "png", f);
                                }
                                catch (IOException exc) {
                                    JOptionPane.showMessageDialog(null, "Error: Couldn't save image ( "
                                            + exc.getMessage() + " )");
                                }
                            }
                            break;
                        default:
                            System.out.println("something went wrong");
                            return;
                    }
                }
        }
    }

    /**
     * Обработка зума по правому и левому клику мышью
     */
    private class MouseHandler extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {

	    if (rowsRemaining > 0) {
		return;
	    }

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
        genMan = new Mandelbrot();
        genCorn = new Tricorn();
        genShip = new BurningShip();

        gen = genMan;
        range = new Rectangle2D.Double();
        gen.getInitialRange(range);
    }

/**
 *  Создание формы
 */
    public void createAndShowGUI() {
		JFrame frame  = new JFrame("Fractal Explorer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout( new BorderLayout());
		FractalHandler handler = new FractalHandler();
		
		JPanel fractalPanel = new JPanel();

		JLabel label = new JLabel("Fractal: ");
		fractalPanel.add(label);

		fractalChooser = new JComboBox<String>();
		fractalChooser.addItem(genMan.toString());
		fractalChooser.addItem(genCorn.toString());
		fractalChooser.addItem(genShip.toString());
		fractalChooser.addActionListener(handler);
		fractalPanel.add(fractalChooser);

		frame.getContentPane().add(fractalPanel, BorderLayout.NORTH);

		image = new JImageDisplay(displaySize, displaySize);
		frame.getContentPane().add(image, BorderLayout.CENTER);


		JPanel buttonsPanel = new JPanel();

		saveButton = new JButton("Save Image");
		saveButton.setActionCommand("save"); 
		saveButton.addActionListener(handler);
		buttonsPanel.add(saveButton);

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
     *  Рисование фрактала. На время отрисовки блокируем интерфейс.
     */
    public void drawFractal() {
        enableUIThings(false);

        rowsRemaining = displaySize;
        for (int y = 0; y < displaySize; y++) {
            FractalWorker worker = new FractalWorker(y);
            worker.execute();
        }
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
