import java.awt.geom.Rectangle2D;

/**
 *  * Класс для вычисления Burning Ship-фрактала
 */
public class BurningShip extends FractalGenerator{
    public static final int MAX_ITERATIONS = 2000;
    
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2.5;
        
        range.width = 4;
        range.height = 4;
    }
    
    /**
     * Уравнение имеет вид zn = (|Re(zn-1)| + i |Im(zn-1)|)2 + c.
     * Другими словами, мы берём абсолютное значение каждого компонента zn-1 на каждой итерации.
    */
    public int numIterations(double x, double y) {
		int count = 0;
		double Xn = 0;
		double Yn = 0;
		double zn2 = 0;
        
        while(count < MAX_ITERATIONS && zn2 < 4) {
	    count++;
	    
	    double Yn1 = Math.abs(2 * Xn * Yn) + y;
		double Xn1 = Math.pow(Xn, 2) - Math.pow(Yn, 2) + x;
	    
		zn2 = Math.pow(Xn1, 2) + Math.pow(Yn1, 2);
		
		Xn = Xn1;
		Yn = Yn1;
	    }
        
        return count < MAX_ITERATIONS ? count : -1;
    }
    
    public String toString() {
        return "Burning Ship";
    }
}
