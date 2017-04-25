/*
 * RCMultiPoint.java
 *
 * Created on 14 de Julho de 2004, 10:00
 */

package org.argouml.shapefile;

import java.util.Vector;

/**
 *
 * @author  Valerio
 */
public class RCMultiPoint extends RecordContents {
    
    private BoundingBox box;        // area de abrangencia do conjunto de pontos
    private long numPoints;
    private Vector listPoint;       // lista de pontos
    
    /** Cria uma nova instancia de RCMultiPoint */
    public RCMultiPoint() {
    }
    
    public RCMultiPoint( Vector fList ) {
        super( ShapeTypes.SHP_MULTIPOINT, fList );
        listPoint = new Vector();
        box = new BoundingBox();
    }
    
    public void addPoint( double X_, double Y_ ) {
        listPoint.add( new Point( X_, Y_, 0 ) );
        box.updateP( listPoint.size(), X_, Y_ );
    }
    
    public Vector getListPoint() {
        return listPoint;
    }

    public void updateBox( int np, double x, double y ) {
        box.updateP( np, x, y );
    }
    
    public BoundingBox getBox() {
        return box;
    }
}
