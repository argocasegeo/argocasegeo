/*
 * RCPolyLine.java
 *
 * Created on 14 de Julho de 2004, 10:16
 */

package org.argouml.shapefile;

import java.util.Vector;

/**
 *
 * @author  Valerio
 */
public class RCPolyLine extends RecordContents {

    private BoundingBox box;        // area de abrangencia do conjunto de partes
    private int numPoints;
    private int numParts;
    private Vector parts;           // lista de partes (1 ou mais)
    
    /** Creates a new instance of RCPolyLine */
    public RCPolyLine() {
    }
    
    //Constructor Create(FList : TList) ;
    //Constructor CreateP(FList : TList);         // Criar Poligono
    public RCPolyLine( Vector fList, int shapeType ) {
        super( shapeType, fList );
        numPoints = 0;
        parts = new Vector();
        box = new BoundingBox();
    }

    public int addPart() {
        parts.add( new Part() );
        return parts.size();
    }
    
    public Vector getParts() {
        return parts;
    }
    
    public BoundingBox getBox() {
        return box;
    }

    public void setBox( BoundingBox b ) {
        box.setXMin( b.getXMin() );
        box.setYMin( b.getYMin() );
        box.setXMax( b.getXMax() );
        box.setYMax( b.getYMax() );
    }
    
    public void updateBox( int np, double x, double y ) {
        box.updateP( np, x, y );
    }
    
    public void addPoint( int NPart, double X_, double Y_, int seq_ ) {
        Part part = new Part();
        if( NPart <= parts.size() && NPart > 0 ) {
            part = (Part)parts.get( NPart - 1 );
            part.addPoint( X_, Y_, seq_ );
            numPoints++;
            box.updateP( numPoints, X_, Y_ );
        }
    }
}
