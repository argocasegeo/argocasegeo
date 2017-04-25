/*
 * RCPoint.java
 *
 * Created on 14 de Julho de 2004, 09:58
 */

package org.argouml.mga.shape.shapefile;

import java.util.Vector;

/**
 *
 * @author  Valerio
 */
public class RCPoint extends RecordContents {
    
    private Point point;            // Coordenadas do ponto
    
    /** Cria uma nova instancia de RCPoint */
    public RCPoint() {
    }
    
    public RCPoint( Vector fList ) {
        super( ShapeTypes.SHP_POINT, fList );
        point = new Point( 0, 0, 0 );
    }
    
    /** Getter for property point.
     * @return Value of property point.
     *
     */
    public org.argouml.mga.shape.shapefile.Point getPoint() {
        return point;
    }
    
    /** Setter for property point.
     * @param point New value of property point.
     *
     */
    public void setPoint(org.argouml.mga.shape.shapefile.Point point) {
        this.point = new Point( point );
    }
    
    public void setPoint( double x, double y ) {
        point.setX( x );
        point.setY( y );
    }
}
