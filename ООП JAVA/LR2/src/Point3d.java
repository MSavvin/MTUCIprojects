/**
 * трёхмерный класс точки.
 **/
public class Point3d{
    /** координата Х**/
    private double xCoord;
    /** координата Y**/
    private double yCoord;
    /** координата Z**/
    private double zCoord;
    /** Конструктор инициализации**/
    public Point3d(double x, double y, double z){
        xCoord = x;
        yCoord = y;
        zCoord = z;
    }
    /** Конструктор по умолчанию**/
    public Point3d(){
        this(0,0,0);
    }
    /** Возвращение координаты Х**/
    public double getX(){
        return xCoord;
    }
    /** Возвращение координаты Y**/
    public double getY(){
        return yCoord;
    }
    /** Возвращение координаты Z**/
    public double getZ(){
        return zCoord;
    }
    /** Установка значения координаты Х**/
    public void setX(double x){
        xCoord = x;
    }
    /** Установка значения координаты Y**/
    public void setY(double y){
        yCoord = y;
    }
    /** Установка значения координаты Z**/
    public void setZ(double z){
        zCoord = z;
    }
    /** Сравнение значений двух объектов**/
    public boolean isEquals(Point3d obj){
        if( this.xCoord == obj.xCoord &&
                this.yCoord == obj.yCoord &&
                this.zCoord == obj.zCoord)
        return true;
	else
        return false;
    }
    /** Расстояние между двумя точками с точностью до двух знаков**/
    public double distanceTo(Point3d obj){
//какой-то код с точностью до 2 знаков после запятой.
        double tempX = this.xCoord - obj.xCoord;
        double tempY = this.yCoord - obj.yCoord;
        double tempZ = this.zCoord - obj.zCoord;
        return Math.sqrt( Math.pow(tempX,2)+Math.pow(tempY,2)+ Math.pow(tempZ,2));
    }
}