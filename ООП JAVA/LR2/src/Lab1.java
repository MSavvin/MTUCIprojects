import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.JOptionPane.showMessageDialog;

public class Lab1 extends JFrame {
    private JTextField textX1;
    private JTextField textY1;
    private JTextField textZ1;
    private JTextField textX2;
    private JTextField textX3;
    private JTextField textY2;
    private JTextField textY3;
    private JTextField textZ2;
    private JTextField textZ3;
    private JButton btnCalculate;
    private JLabel lblResult;
    private JPanel mainPanel;

    public Lab1(){
        setContentPane(mainPanel);
        pack();
        setTitle("Лабораторная работа №2: Основы объектно-ориентированного программирования");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700,300);
        setVisible(true);

        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Point3d pointOne = new Point3d();
                Point3d pointTwo = new Point3d();
                Point3d pointThree = new Point3d();

                try{
                    pointOne.setX(Double.parseDouble(textX1.getText()));
                    pointOne.setY(Double.parseDouble(textY1.getText()));
                    pointOne.setZ(Double.parseDouble(textZ1.getText()));

                    pointTwo.setX(Double.parseDouble(textX2.getText()));
                    pointTwo.setY(Double.parseDouble(textY2.getText()));
                    pointTwo.setZ(Double.parseDouble(textZ2.getText()));

                    pointThree.setX(Double.parseDouble(textX3.getText()));
                    pointThree.setY(Double.parseDouble(textY3.getText()));
                    pointThree.setZ(Double.parseDouble(textZ3.getText()));
                }
                catch (Exception ex)
                {
                    showMessageDialog(null, "Одно из полей заполнено не корректно");
                    return;
                }

                if(pointOne.isEquals(pointTwo) || pointOne.isEquals(pointThree) || pointTwo.isEquals(pointThree)){
                    lblResult.setText("Одна из точек равна другой. Площадь не посчитана.");
                }else {
                    double result = computeArea(pointOne, pointTwo, pointThree);
                    lblResult.setText(String.format("%.2f",result));
                }
            }
        });

    }

    public static double computeArea(Point3d pointOne,Point3d pointTwo,Point3d pointThree){

        /*
        * Формула Герона: S=sqrt(p*(p-a)*(p-b)*(p-c)), где P = (a+b+c)/2; a,b,c - стороны треугольника
         */
        double a = pointOne.distanceTo(pointTwo);
        double b = pointOne.distanceTo(pointThree);
        double c = pointTwo.distanceTo(pointThree);
        double p = (a+b+c)/2;

        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }



    public static void main(String[] args) {
        Lab1 gui = new Lab1();
        //System.out.println("Hello World!");
    }
}
