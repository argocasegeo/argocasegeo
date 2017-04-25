/*
 * ActionTriang.java
 *
 * Created on 12 de Janeiro de 2005, 16:52
 */
package org.argouml.uml.ui;
import java.awt.event.*;
import java.util.List;
import org.argouml.uml.diagram.static_structure.ui.FigGeographicField;
import org.argouml.uml.diagram.static_structure.ui.FigGeographicObject;
import org.argouml.uml.diagram.static_structure.ui.FigNonGeographicObject;
import org.argouml.uml.diagram.ui.SelectionWButtons;


public class ActionTriang extends UMLAction {
    
    ////////////////////////////////////////////////////////////////
    // static variables

    private boolean show = true;

    public boolean isShow() {
        return show;
    }
    public static ActionTriang SINGLETON = new ActionTriang();
    
    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionTriang() {
	super("Show/Clear UMLGeoFrame Stereotype", NO_ICON);
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent ae) {
        SelectionWButtons.toggleShowRapidButtons();
        show = SelectionWButtons._showRapidButtons;
        
        FigGeographicObject test = new FigGeographicObject();
        List<FigGeographicObject> l = test.getList();
        l.remove(test);
        for(int i=0;i< l.size();i++){
            test = l.get(i);
            test.representation(0);
        }
        FigNonGeographicObject test2 = new FigNonGeographicObject();
        List<FigNonGeographicObject> l2 = test2.getList();
        l2.remove(test2);
        for(int i=0;i< l2.size();i++){
            test2 = l2.get(i);
            test2.representation();
        }
        FigGeographicField test3 = new FigGeographicField();
        List<FigGeographicField> l3 = test3.getList();
        l3.remove(test3);
        for(int i=0;i< l3.size();i++){
            test3 = l3.get(i);
            test3.representation(0);
        }

  }
    
} /* end class ActionTriang */

