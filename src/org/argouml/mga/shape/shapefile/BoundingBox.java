/*
 * BoundingBox.java
 *
 * Created on 14 de Julho de 2004, 08:28
 *
 * Ddefine a região de abrangência de uma entidade,
 * ou seja, o menor retângulo que abrange todos os pontos de uma
 * entidade (MultiPoint, PolyLine, Polygon, Arquivo Shape, etc.)
 *
 */

package org.argouml.mga.shape.shapefile;

/**
 *
 * @author  Valerio
 */
public class BoundingBox {
    
    private double xMin, xMax, yMin, yMax;      // Coordenadas do retangulo
    
    /** Cria uma nova instancia de BoundingBox */
    public BoundingBox() {
    }
    
    public BoundingBox( double xmin, double xmax, double ymin, double ymax ) {
        xMin = xmin;
        xMax = xmax;
        yMin = ymin;
        yMax = ymax;
    }
    
    public BoundingBox( BoundingBox box ) {
        xMin = box.xMin;
        xMax = box.xMax;
        yMin = box.yMin;
        yMax = box.yMax;
    }
    
    // Atualiza a regiao de abrangencia com base na inclusao de um ponto
    // Informar np == 1 para descartar a regiao atual  (inicialização)
    public void updateP( int np, double X, double Y ) {
        if( np == 1 ) {
            xMin = X;
            xMax = X;
            yMin = Y;
            yMax = Y;
        }
        else {
            if( X > xMax )
                xMax = X;
            else if( X < xMin )
                xMin = X;
            if( Y > yMax )
                yMax = Y;
            else if( Y < yMin )
                yMin = Y;
        }
    }
    
    // Atualiza a região de abrangência com base em outra região
    // Informar np == 1 para descartar a regiao atual  (inicialização)
    public void updateB( int np, BoundingBox b ) {
        if( np == 1 ) {
            xMin = b.xMin; 
            xMax = b.xMax;
            yMin = b.yMin; 
            yMax = b.yMax;
        }
        else {
            if( b.xMin < xMin )
                xMin = b.xMin;
            else if( b.xMax > xMax )
                xMax = b.xMax;
            if( b.yMin < yMin )
                yMin = b.yMin;
            else if( b.yMax > yMax )
                yMax = b.yMax;
        }
    }
    
    /** Getter for property xMax.
     * @return Value of property xMax.
     *
     */
    public double getXMax() {
        return xMax;
    }
    
    /** Setter for property xMax.
     * @param xMax New value of property xMax.
     *
     */
    public void setXMax(double xMax) {
        this.xMax = xMax;
    }
    
    /** Getter for property xMin.
     * @return Value of property xMin.
     *
     */
    public double getXMin() {
        return xMin;
    }
    
    /** Setter for property xMin.
     * @param xMin New value of property xMin.
     *
     */
    public void setXMin(double xMin) {
        this.xMin = xMin;
    }
    
    /** Getter for property yMax.
     * @return Value of property yMax.
     *
     */
    public double getYMax() {
        return yMax;
    }
    
    /** Setter for property yMax.
     * @param yMax New value of property yMax.
     *
     */
    public void setYMax(double yMax) {
        this.yMax = yMax;
    }
    
    /** Getter for property yMin.
     * @return Value of property yMin.
     *
     */
    public double getYMin() {
        return yMin;
    }
    
    /** Setter for property yMin.
     * @param yMin New value of property yMin.
     *
     */
    public void setYMin(double yMin) {
        this.yMin = yMin;
    }
    
}
