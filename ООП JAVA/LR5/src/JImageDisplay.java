import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;

public class JImageDisplay extends JComponent {

    private BufferedImage image;

    public JImageDisplay(int width, int height) {
	    image = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);

	    Dimension size = new Dimension(width, height);
            super.setPreferredSize(size);
    }

    public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    // Мы передаем значение null для ImageObserver
	    g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    public void drawPixel(int x, int y, int rgbColor) {
	    image.setRGB(x, y, rgbColor);
    }

    public void clearImage() {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
            drawPixel(i, j, 0);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

}
