// Decompiled by DJ v2.9.9.61 Copyright 2000 Atanas Neshkov  Date: 03-11-2003 15:10:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   FigNetworkObject.java

package org.argouml.uml.diagram.static_structure.ui;


import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;



// Referenced classes of package org.argouml.uml.diagram.static_structure.ui:
//            SelectionClass

public class FigNetworkObject extends FigGeographicObject {
    
    public FigNetworkObject() {
        super(true);
    }
    
    public FigNetworkObject(GraphModel graphmodel, Object obj) {
        super(graphmodel,obj,true);
    }
    
   public Selection makeSelection() {
        return new SelectionClass5(this);
    }
    
   
}