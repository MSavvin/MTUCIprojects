import java.awt.geom.Rectangle2D;

/**
 * Класс для вычисления Tricorn-фрактала
 */
public class Tricorn extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;

    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;

        range.width = 4;
        range.height = 4;
    }

    /**
     * Практически идентичен фракталу Мандельброта
     */

    public int numIterations(double x, double y) {
		int count = 0;
		double Xn = 0;
		double Yn = 0;
		double zn2 = 0;

        while (count < MAX_ITERATIONS && zn2 < 4) {
            count++;

			double Xn1 = Math.pow(Xn, 2) - Math.pow(Yn, 2) + x;
			double Yn1 = -2 * Xn * Yn + y;

			zn2 = Math.pow(Xn1, 2) + Math.pow(Yn1, 2);

            Xn = Xn1;
            Yn = Yn1;
        }

        return count < MAX_ITERATIONS ? count : -1;
    }
    
    public String toString() {
	    return "Tricorn";
    }
}
