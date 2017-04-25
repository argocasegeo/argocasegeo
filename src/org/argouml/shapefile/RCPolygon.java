/*
 * RCPolygon.java
 *
 * Created on 14 de Julho de 2004, 10:26
 */

package org.argouml.shapefile;

import java.util.Vector;

/**
 *
 * @author  Valerio
 */
public class RCPolygon extends RCPolyLine {
    
    /** Cria uma nova instancia de RCPolygon */
    public RCPolygon() {
    }

    // mesma estrutura da polyline, porém o primeiro ponto de cada parte
    // deve ser igual ao ultimo ponto da mesma parte
    public RCPolygon( Vector fList ) {
        super( fList, ShapeTypes.SHP_POLYGON );
    }
}
