/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto3quickhull;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author Piky
 */
public class QuickHull {
    
    //LIMITE_PARALELO define un número de elementos que determina si un programa se realiza con paralelización
    //static final = constante de clase
    static final int LIMITE_PARALELO = 100;

    public class QuickHullPar extends RecursiveAction{
        private ArrayList<Point> puntos = new ArrayList<>();
         
        //Paralelización
        /**
         * 
         * @param puntos Es el arreglo que contiene todos los puntos que se van a evaluar
         */
        public QuickHullPar(ArrayList<Point> puntos){
            this.puntos = puntos;
        }

        @Override
        /**
         * Método que determina si se va a realizar de forma secuencial o paralela 
         */
        protected void compute(){
            if(puntos.size() <= LIMITE_PARALELO)
                quickHull(puntos);
            else{
                ArrayList<Point> lista1 = new ArrayList<>();
                ArrayList<Point> lista2 = new ArrayList<>();
                    
                int mitad = puntos.size() / 2;
                int total = puntos.size();
                    
                List<Point> lista1Aux =  puntos.subList(0, mitad);
                List<Point> lista2Aux =  puntos.subList(mitad, total);
                
                lista1.addAll(lista1Aux);
                lista2.addAll(lista2Aux);
                    
                QuickHullPar izq = new QuickHullPar(lista1);
                QuickHullPar der = new QuickHullPar(lista2);
                    
                //invoca/ejecuta las dos tareas que le mandemos 
                invokeAll(izq,der);
                quickHull(puntos);
            }
        }
    }
        
    /**
     * 
     * @param puntos Es el arreglo que contiene todos los puntos que se van a evaluar
     * @return regresa la lista de puntos que forman el convexhull
     */
    public ArrayList<Point> useQuickHull(ArrayList<Point> puntos){
        //busca el numero de procesadores disponibles   
        ForkJoinPool fjp = new ForkJoinPool();
        //invoca la tarea paralela
        fjp.invoke(new QuickHullPar(puntos));
        return puntos;
    }
        
    
    //Se ingresa lista de puntos
    /**
     * 
     * @param puntos Es el arreglo que contiene todos los puntos que se van a evaluar
     * @return regresa la lista de puntos que forman el convexhull de manera secuencial
     */
    public ArrayList<Point> quickHull(ArrayList<Point> puntos){
        //Lista ligada de puntos
        ArrayList<Point> convexHull = new ArrayList<>();
        //Minimo 3 coordenadas
        if(puntos.size() < 3)
            return(ArrayList) puntos.clone();
        
        int puntoMin = -1;
        int puntoMax = -1;
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        
        //Se comparan todos los puntos para encontrar el menor y el mayor en X
        for (int i = 0; i < puntos.size(); i++) {
            //La coordenada en x de ese punto
            if(puntos.get(i).x < minX){
                minX = puntos.get(i).x;
                puntoMin = i;
            }
            
            if(puntos.get(i).x > maxX){
                maxX = puntos.get(i).x;
                puntoMax = i;
            }
        }
        
        //Se agregan a convexHull y se eliminan de la lista general
        Point A = puntos.get(puntoMin);
        Point B = puntos.get(puntoMax);
        convexHull.add(A);
        convexHull.add(B);
        puntos.remove(A);
        puntos.remove(B);
        
        ArrayList<Point> setDerecho = new ArrayList<>();
        ArrayList<Point> setIzquierdo = new ArrayList<>();
        
        //Compara todos los puntos con la linea formada entre el minimo y máximo en x y los clasifica segun se posición
        for (int i = 0; i < puntos.size(); i++) {
            Point p = puntos.get(i);
            if(ladoPunto(A,B,p) == -1)
                setIzquierdo.add(p);
            else if(ladoPunto(A,B,p) == 1){
                setDerecho.add(p);
            }
        }
        
        findHull(A,B,setDerecho,convexHull);
        findHull(B,A,setIzquierdo,convexHull);
        
        return convexHull;
        
    }
    
    //ubicación de un punto con respecto a una recta
    /**
     * 
     * @param A punto inicial de la recta
     * @param B punto final de la recta
     * @param P punto que se va a comparar con la recta BA
     * @return 
     */
    public int ladoPunto(Point A, Point B, Point P){
        //El determinante de los vectores BA y PA o el producto cruz, para obtener el ángulo definido por 3 puntos en el plano
        // uxv = det(uv) = (ux)(vy)-(uy)(ux)
        int cpl = (B.x -A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        //a la derecha, se forma un ángulo positivo de rotación alrededor de A y B, hacia P
        if(cpl > 0)
            return 1;
        //Adentro de la figura, son coliniales
        else if(cpl == 0)
            return 0;
        //A la izquierda, se forma un ángulo negativo de rotación alrededor de A y B, hacia P
        else
            return -1;
    }
    
    //Calcula la distancia entre 3 puntos
    /**
     * 
     * @param A punto inicial de la recta
     * @param B punto final de la recta
     * @param C punto que se va a comparar con la recta BA
     * @return 
     */
    public int distancia(Point A, Point B, Point C){
        //diferencia de B y A en "x" y en "y"
        int ABx = B.x -A.x;
        int ABy = B.y -A.y;
        int num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        
        if(num < 0)
            num = -num;
        
        return num;
    }
    
    //Busca el punto más alejado a la linea formada y lo agrega al covexhull
    /**
     * 
     * @param A punto incial de la recta
     * @param B punto final de la recta
     * @param lista lista donde se encuentran todos los puntos a evaluar
     * @param hull lista donde se va a almacenar los puntos que forman el convexhull
     */
    public void findHull(Point A,Point B, ArrayList<Point> lista, ArrayList<Point> hull){
        //índice que ocupa B en el hull
        int PosIn = hull.indexOf(B);
        //Condiciones de salida
        if(lista.isEmpty())
            return;
        if(lista.size() == 1){
            Point p = lista.get(0);
            lista.remove(p);
            hull.add(PosIn,p);
            return;
        }
        
        int distMax = Integer.MIN_VALUE;
        int puntoMax = -1;
        
        //para todos los elementos de a lista de puntos se saca su distancia con respecto a la linea y se guarda la distancia mas lejana y su punto
        for(int i=0;i<lista.size();i++){
            Point p = lista.get(i);
            int distancia = distancia(A,B,p);
            if(distancia > distMax){
                distMax = distancia;
                puntoMax = i;
            }
        }
        //Obtiene el punto mas lejano usando su ubicación, lo quita del arreglo de puntos y lo agrega al hull en la posición del punto B, recorriendo todo a la derecha
        Point P = lista.get(puntoMax);
        lista.remove(puntoMax);
        hull.add(PosIn,P);
        
        //Recorre la lista de puntos y agrega a una nueva lista los elementos a la derecha de la linea formada por los puntos A y P
        //No se incluyen elementos a la izquierda porque esos estarían adentro de la figura
        ArrayList<Point> ListDerechoAP = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            Point M = lista.get(i);
            if(ladoPunto(A,P,M) == 1)
                ListDerechoAP.add(M);
        }
        //Recorre la lista de puntos y agrega a una nueva lista los elementos a la derecha de la linea formada por los puntos P y B
        ArrayList<Point> ListDerechoPB = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            Point M = lista.get(i);
            if(ladoPunto(P,B,M) == 1)
                ListDerechoPB.add(M);
        }
        
        //Recursividad con el punto más alejado y las lineas que se formaron con el 
        findHull(A,P,ListDerechoAP,hull);
        findHull(P,B,ListDerechoPB,hull);
    }
    
}
           
        
    

