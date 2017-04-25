/*
 * Part.java
 *
 * Created on 14 de Julho de 2004, 08:41
 *
 * Parte de um polígono ou polyline (lista de pontos que compoe a parte)
 */

package org.argouml.shapefile;

import java.util.*;

/**
 *
 * @author  Valerio
 */
public class Part {
    
    private Vector listPoints;          // lista de pontos
    private int offset;                 // Deslocamento: soma de todos os pontos das
                                        // partes anteriores a esta (utilizado no metodo
                                        // de gravação dos dados do arquivo SHAPE)
    
    
    /** Cria uma nova instacia de Part */
    public Part() {
        listPoints = new Vector();
    }
    
    public Part( int os, Vector lp ) {
        offset = os;
        listPoints = new Vector( lp );
    }
    
    public Part( Part p ) {
        offset = p.offset;
        listPoints = new Vector( p.listPoints );
    }
    
    // Adiciona um ponto à parte (no fim da lista)
    public void addPoint( double x, double y, int seq ) {
        listPoints.add( new Point( x, y, seq ) );
    }
    
    public Vector getListPoints() {
        return listPoints;
    }
    
    public int getNumPoints() {
        return listPoints.size();
    }
    
    // Apaga o ponto, de acordo com o ID de Usuário
    // se o SEQ_ for um valor negativo, o último ponto é apagado...
    public void deletePoint( int seq ) {
        Point p;
        
        if( seq < 0 )
            listPoints.remove( listPoints.size() - 1 );
        else {
            for( int ind = 0; ind < listPoints.size(); ind++ ) {
                p = (Point)listPoints.get( ind );
                if( p.getSeq() == seq ) {
                    listPoints.remove( ind );
                    return;
                }
            }
        }
    }
    
    public void sort() {
        Collections.sort( listPoints );
    }
    
    /** Getter for property offset.
     * @return Value of property offset.
     *
     */
    public int getOffset() {
        return offset;
    }
    
    /** Setter for property offset.
     * @param offset New value of property offset.
     *
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
}
