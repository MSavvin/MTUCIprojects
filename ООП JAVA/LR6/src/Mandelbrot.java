import java.awt.geom.Rectangle2D;

/**
 * Класс для вычисления фрактала Мандельброта
 */
public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;

	/**
	* Изменение поля прямоугольника для отображения правильного начального диапазона для фрактала.
	*/
    public void getInitialRange(Rectangle2D.Double range) {
		range.x = -2;
		range.y = -1.5;

		range.width = 3;
		range.height = 3;
    }
   /**
	 * Функция для фрактала Мандельброта имеет вид: zn = zn-1*2 + c, где все значения — это комплексные числа,
	 * z0 = 0, и с - определенная точка фрактала, которую мы отображаем на экране.     *
     *  Мы представим это в виде формул:
     *  x{n+1}={x{n}}^{2}-{y{n}}^{2}+x{0}
     *  y{n+1}=2{x{n}}{y{n}}+y{0}
     *  zn2 = xn2 + yn2 > 4
     */

    public int numIterations(double x, double y) {
		int count = 0;
		double Xn = 0;
		double Yn = 0;
		double zn2 = 0;

		while (count < MAX_ITERATIONS && zn2 < 4) {
			count++;

			double Xn1 = Math.pow(Xn, 2) - Math.pow(Yn, 2) + x;
			double Yn1 = 2 * Xn * Yn + y;

			zn2 = Math.pow(Xn1, 2) + Math.pow(Yn1, 2);

            Xn = Xn1;
            Yn = Yn1;
		}

		return count < MAX_ITERATIONS ? count : -1;
    }

    public String toString() {
        return "Mandelbrot";
    }
}