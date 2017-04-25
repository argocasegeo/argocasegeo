/*
 * Ponto.java
 *
 * Created on 15 de Junho de 2004, 16:13
 *
 * Objeto para armazenar o ponto em coordenadas de ponto flutuante (double)
 */

package org.argouml.mga.shape.shapefile;

import java.util.*;
/**
 *
 * @author  Valerio
 */
public class Point implements Comparable {

    private double X, Y;
    private int seq;        // numero sequencial para o ponto
                            // valor utilizado para uma possivel ordenacao
                            // apenas valores positivos devem ser usados

    /** Creates a new instance of Ponto */
    public Point( double x, double y, int s ) {
        X = x;
        Y = y;
        seq = s;
    }

    public Point( Point p ) {
        X = p.X;
        Y = p.X;
        seq = p.seq;
    }

    /** Getter for property seq.
     * @return Value of property seq.
     *
     */
    public int getSeq() {
        return seq;
    }

    /** Setter for property seq.
     * @param seq New value of property seq.
     *
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }

    /** Getter for property X.
     * @return Value of property X.
     *
     */
    public double getX() {
        return X;
    }

    /** Setter for property X.
     * @param X New value of property X.
     *
     */
    public void setX(double X) {
        this.X = X;
    }

    /** Getter for property Y.
     * @return Value of property Y.
     *
     */
    public double getY() {
        return Y;
    }

    /** Setter for property Y.
     * @param Y New value of property Y.
     *
     */
    public void setY(double Y) {
        this.Y = Y;
    }
    
    public int compareTo(Object o) {
        Integer i1 = new Integer( seq );
        Integer i2 = new Integer( ((Point) o).seq );
        return i1.compareTo( i2 );
    }    
    

}
