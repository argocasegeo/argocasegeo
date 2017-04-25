// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.ui;
import javax.swing.event.*;
import javax.swing.*;

import org.argouml.ui.ProjectBrowser;
import java.lang.reflect.*;
import ru.novosoft.uml.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

//Pra gerar a figura
import java.awt.*;
import org.argouml.uml.diagram.static_structure.ui.FigGeographicObject;
import org.tigris.gef.presentation.*;

public class UMLCheckBox extends JCheckBox implements ItemListener, UMLUserInterfaceComponent {

    private UMLUserInterfaceContainer _container;
    private UMLBooleanProperty _property;
	public String label2;
    
    /** Creates new BooleanChangeListener */
    public UMLCheckBox(String label,UMLUserInterfaceContainer container,UMLBooleanProperty property) {
        super(label);
		label2=label;
        _container = container;
        _property = property;
        addItemListener(this);
        update();
    }

    public void itemStateChanged(final ItemEvent event) {
    	try{
	   
           Image img;
	     ImageIcon icon;  
	      FigGroup attrVec2;

		System.out.println(this.label2); 
		_property.setProperty(_container.getTarget(),event.getStateChange() == ItemEvent.SELECTED);
		//System.out.println(this._property.getProperty(object)); 
		/*if(event.getStateChange() == ItemEvent.SELECTED)
		{
			System.out.println("Entrou  na mudanca de estado");     
			icon = new ImageIcon("GeographicObject.gif");
			 img= icon.getImage();
			 FigGeographicObject = new FigImage(60, 10, 20, 30,img);
			 FigGeographicObject.setLineWidth(1);
		     attrVec2 = new FigGroup();
		     attrVec2.setFilled(true);
                 attrVec2.setLineWidth(1);
             	attrVec2.addFig(FigGeographicObject);
			new FigGeographicObject(1);
		}*/
    	}
    	catch (PropertyVetoException ve) {
    		ProjectBrowser.TheInstance.getStatusBar().showStatus(ve.getMessage());
    	}
    	update();
        
    }

    public void targetChanged() {
        update();
    }

    public void targetReasserted() {
    }
    
    public void roleAdded(final MElementEvent p1) {
    }
    public void recovered(final MElementEvent p1) {
    }
    public void roleRemoved(final MElementEvent p1) {
    }
    public void listRoleItemSet(final MElementEvent p1) {
    }
    public void removed(final MElementEvent p1) {
    }
    public void propertySet(final MElementEvent event) {
        if(_property.isAffected(event))
            update();
    }
    
    private void update() {
        
	//	System.out.println("Procurando selecao");
		boolean oldState = isSelected();
                
                boolean newState = _property.getProperty(_container.getTarget());
                
        if(newState && oldState != newState){
            setSelected(newState);
        }
        // clear out the residual garbage.
        if (!newState && oldState){
            setSelected(false);
        }
    }
}