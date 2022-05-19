/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto3quickhull;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Piky
 */
public class Proyecto3QuickHull {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        QuickHull quickHull = new QuickHull();
        ArrayList<Point> puntos = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        int x,y;
        int num;
        
        System.out.println("QUICKHULL");
        System.out.println("Ingrese la cantidad de puntos");
        num = scan.nextInt();
        for (int i = 0; i < num; i++) {
            System.out.println("Coordenada "+ (i+1));
            x = (int) (Math.random() * 501 );
            y = (int) (Math.random() * 501 );
            System.out.println("("+x+","+y+")");
            
            //Agrega el punto creado a la lista
            Point punto = new Point(x,y);
            puntos.add(i,punto); 
        }
        
        
        ArrayList<Point> convexhull = quickHull.useQuickHull(puntos);
        System.out.println("El convexhull esta formado por los puntos: ");
        for (int i = 0; i < convexhull.size(); i++) {
            System.out.println("(" + convexhull.get(i).x + ","+convexhull.get(i).y + ")");
        }
    }
    
    

}
