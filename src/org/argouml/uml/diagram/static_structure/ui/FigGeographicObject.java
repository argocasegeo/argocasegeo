// Decompiled by DJ v2.9.9.61 Copyright 2000 Atanas Neshkov  Date: 03-11-2003 15:10:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   FigGeographicObject.java

package org.argouml.uml.diagram.static_structure.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.PrintStream;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import org.argouml.application.api.Notation;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlHelper;
import org.argouml.model.uml.foundation.core.CoreFactory;
import org.argouml.model.uml.foundation.core.CoreHelper;
import org.argouml.ui.ArgoJMenu;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.StatusBar;
import org.argouml.uml.diagram.ui.CompartmentFigText;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.argouml.uml.generator.ParserDisplay;
import org.argouml.uml.ui.ActionAddAttribute;
import org.argouml.uml.ui.ActionAddNote;
import org.argouml.uml.ui.ActionAddOperation;
import org.argouml.uml.ui.ActionCompartmentDisplay;
import org.argouml.uml.ui.ActionModifier;
import org.argouml.uml.ui.ActionTriang;
import org.argouml.uml.ui.foundation.core.*;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Selection;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigImage;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;
import org.tigris.gef.util.*;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MBehavioralFeature;
import ru.novosoft.uml.foundation.core.MClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MComponent;
import ru.novosoft.uml.foundation.core.MElementResidence;
import ru.novosoft.uml.foundation.core.MFeature;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MStructuralFeature;
import ru.novosoft.uml.foundation.data_types.MScopeKind;
import ru.novosoft.uml.model_management.MPackage;

//TEMP{
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
//TEMP}

// Referenced classes of package org.argouml.uml.diagram.static_structure.ui:
//            SelectionClass

public class FigGeographicObject extends FigNodeModelElement {

    static List<FigGeographicObject> list;

    private void initializeList(){
        if(list == null){
            list = new ArrayList();
            list.add(this);
        }else if(!list.contains((FigGeographicObject)(this))){
            list.add(this);
        }

    }

    public List<FigGeographicObject> getList(){
        return list;
    }

    public FigGeographicObject(boolean net) {

        initializeList();

        isNet = net;

        Flag_Point = true;
        Flag_Line = true;
        Flag_Polygon = true;
        Flag_ComplexObj = true;
        Flag_Net = true;
        Flag_Arc = true;
        Flag_Node = true;
        Flag_DirectArc = true;
        resident = UmlFactory.getFactory().getCore().createElementResidence();
        highlightedFigText = null;
        _newlyCreated = false;
        _name.setLineWidth(1);
        _name.setFilled(true);
        _attrBigPort = new FigRect(10, 30, 60, 19, Color.black, Color.white);
        _attrBigPort.setFilled(true);
        _attrBigPort.setLineWidth(1);
        _attrVec = new FigGroup();
        _attrVec.setFilled(true);
        _attrVec.setLineWidth(1);
        _attrVec.addFig(_attrBigPort);
        _operBigPort = new FigRect(10, 48, 60, 19, Color.black, Color.white);
        _operBigPort.setFilled(true);
        _operBigPort.setLineWidth(1);
        _operVec = new FigGroup();
        _operVec.setFilled(true);
        _operVec.setLineWidth(1);
        _operVec.addFig(_operBigPort);
        Rectangle rectangle = getBounds();
        //_stereo.setText("Interface");
        _stereo.setExpandOnly(true);
        _stereo.setFilled(true);
        _stereo.setLineWidth(1);
        _stereo.setEditable(false);
        _stereo.setHeight(19);
        _stereo.setDisplayed(true);
        _stereoLineBlinder = new FigRect(11, 28, 58, 2, Color.white, Color.white);
        _stereoLineBlinder.setLineWidth(1);
        _stereoLineBlinder.setDisplayed(true);
        if(!_stereo.isDisplayed()) {
            _stereoLineBlinder.setDisplayed(true);
            _stereo.setDisplayed(true);
            if(!_newlyCreated) {
                rectangle.y -= 18;
                rectangle.height += 18;
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        enableSizeChecking(false);
        suppressCalcBounds = true;
        addFig(_bigPort);
        addFig(_stereo);
        addFig(_name);
        addFig(_stereoLineBlinder);
        addFig(_attrVec);
        addFig(_operVec);
        suppressCalcBounds = false;
        setBounds(10, 10, 60, 56);
    }
    
    public FigGeographicObject() {

        initializeList();

        Flag_Point = true;
        Flag_Line = true;
        Flag_Polygon = true;
        Flag_ComplexObj = true;
        Flag_Net = true;
        Flag_Arc = true;
        Flag_Node = true;
        Flag_DirectArc = true;
        resident = UmlFactory.getFactory().getCore().createElementResidence();
        highlightedFigText = null;
        _newlyCreated = false;
        _name.setLineWidth(1);
        _name.setFilled(true);
        _attrBigPort = new FigRect(10, 30, 60, 19, Color.black, Color.white);
        _attrBigPort.setFilled(true);
        _attrBigPort.setLineWidth(1);
        _attrVec = new FigGroup();
        _attrVec.setFilled(true);
        _attrVec.setLineWidth(1);
        _attrVec.addFig(_attrBigPort);
        _operBigPort = new FigRect(10, 48, 60, 19, Color.black, Color.white);
        _operBigPort.setFilled(true);
        _operBigPort.setLineWidth(1);
        _operVec = new FigGroup();
        _operVec.setFilled(true);
        _operVec.setLineWidth(1);
        _operVec.addFig(_operBigPort);
        Rectangle rectangle = getBounds();
        //_stereo.setText("Interface");
        _stereo.setExpandOnly(true);
        _stereo.setFilled(true);
        _stereo.setLineWidth(1);
        _stereo.setEditable(false);
        _stereo.setHeight(19);
        _stereo.setDisplayed(true);
        _stereoLineBlinder = new FigRect(11, 28, 58, 2, Color.white, Color.white);
        _stereoLineBlinder.setLineWidth(1);
        _stereoLineBlinder.setDisplayed(true);
        if(!_stereo.isDisplayed()) {
            _stereoLineBlinder.setDisplayed(true);
            _stereo.setDisplayed(true);
            if(!_newlyCreated) {
                rectangle.y -= 18;
                rectangle.height += 18;
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        enableSizeChecking(false);
        suppressCalcBounds = true;
        addFig(_bigPort);
        addFig(_stereo);
        addFig(_name);
        addFig(_stereoLineBlinder);
        addFig(_attrVec);
        addFig(_operVec);
        suppressCalcBounds = false;
        setBounds(10, 10, 60, 56);
    }
    
    public FigGeographicObject(GraphModel graphmodel, Object obj) {
        this();
        enableSizeChecking(true);
        setOwner(obj);
        if(((MClassifier)obj).getName() != null)
            _name.setText(((MModelElement)obj).getName());
    }

    public FigGeographicObject(GraphModel graphmodel, Object obj , boolean net) {
        this(net);
        enableSizeChecking(true);
        setOwner(obj);
        if(((MClassifier)obj).getName() != null)
            _name.setText(((MModelElement)obj).getName());
    }
    public String placeString() {
        return "new Class";
    }
    
    public Object clone() {
        FigGeographicObject figgeographicobject = (FigGeographicObject)super.clone();
        Vector vector = figgeographicobject.getFigs();
        figgeographicobject._bigPort = (FigRect)vector.elementAt(0);
        figgeographicobject._stereo = (FigText)vector.elementAt(1);
        figgeographicobject._name = (FigText)vector.elementAt(2);
        figgeographicobject._stereoLineBlinder = (FigRect)vector.elementAt(3);
        figgeographicobject._attrVec = (FigGroup)vector.elementAt(4);
        figgeographicobject._operVec = (FigGroup)vector.elementAt(5);
        return figgeographicobject;
    }
    
    public Selection makeSelection() {
        return new SelectionClass2(this);
    }
    
    public Vector getPopUpActions(MouseEvent mouseevent) {
        Vector vector = super.getPopUpActions(mouseevent);
        JMenu jmenu = new JMenu("Add");
        jmenu.add(ActionAddAttribute.SINGLETON);
        jmenu.add(ActionAddOperation.SINGLETON);
        jmenu.add(ActionAddNote.SINGLETON);
        vector.insertElementAt(jmenu, vector.size() - 1);
        JMenu jmenu1 = new JMenu("Show");
        if(_attrVec.isDisplayed() && _operVec.isDisplayed())
            jmenu1.add(ActionCompartmentDisplay.HideAllCompartments);
        else
            if(!_attrVec.isDisplayed() && !_operVec.isDisplayed())
                jmenu1.add(ActionCompartmentDisplay.ShowAllCompartments);
        if(_attrVec.isDisplayed())
            jmenu1.add(ActionCompartmentDisplay.HideAttrCompartment);
        else
            jmenu1.add(ActionCompartmentDisplay.ShowAttrCompartment);
        if(_operVec.isDisplayed())
            jmenu1.add(ActionCompartmentDisplay.HideOperCompartment);
        else
            jmenu1.add(ActionCompartmentDisplay.ShowOperCompartment);
        vector.insertElementAt(jmenu1, vector.size() - 1);
        MClass mclass = (MClass)getOwner();
        ArgoJMenu argojmenu = new ArgoJMenu("Representation");
        argojmenu.addCheckItem(new ActionModifier("Point", "isPoint", "isPoint", "setPoint", mclass));
        argojmenu.addCheckItem(new ActionModifier("Line", "isLine", "isLine", "setLine", mclass));
        argojmenu.addCheckItem(new ActionModifier("Polygon", "isPolygon", "isPolygon", "setPolygon", mclass));
        argojmenu.addCheckItem(new ActionModifier("ComplexObj", "isComplexObj", "isComplexObj", "setComplexObj", mclass));

        argojmenu.addCheckItem(new ActionModifier("Net", "isNet", "isNet", "setNet", mclass));
        argojmenu.addCheckItem(new ActionModifier("Arc", "isArc", "isArc", "setArc", mclass));
        argojmenu.addCheckItem(new ActionModifier("Node", "isNode", "isNode", "setNode", mclass));
        argojmenu.addCheckItem(new ActionModifier("DirectArc", "isDirectArc", "isDirectArc", "setDirectArc", mclass));

        
        
        vector.insertElementAt(argojmenu, vector.size() - 1);
        return vector;
    }
    
    public FigGroup getOperationsFig() {
        return _operVec;
    }
    
    public FigGroup getAttributesFig() {
        return _attrVec;
    }
    
    public boolean isOperationVisible() {
        return _operVec.isDisplayed();
    }
    
    public boolean isAttributeVisible() {
        return _attrVec.isDisplayed();
    }

    public void setAttributeVisible(boolean flag) {
        checkSize = true;
        Rectangle rectangle = getBounds();
        int i = checkSize ? ((17 * Math.max(1, _attrVec.getFigs().size() - 1) + 2) * rectangle.height) / getMinimumSize().height : 0;
        if(_attrVec.isDisplayed()) {
            if(!flag) {
                damage();
                for(Enumeration enumeration = _attrVec.getFigs().elements(); enumeration.hasMoreElements(); ((Fig)enumeration.nextElement()).setDisplayed(false));
                _attrVec.setDisplayed(false);
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height - i);
            }
        } else
            if(flag) {
                for(Enumeration enumeration1 = _attrVec.getFigs().elements(); enumeration1.hasMoreElements(); ((Fig)enumeration1.nextElement()).setDisplayed(true));
                _attrVec.setDisplayed(true);
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height + i);
                damage();
            }
    }
    
    public void setOperationVisible(boolean flag) {
        checkSize = true;
        Rectangle rectangle = getBounds();
        int i = checkSize ? ((17 * Math.max(1, _operVec.getFigs().size() - 1) + 2) * rectangle.height) / getMinimumSize().height : 0;
        if(_operVec.isDisplayed()) {
            if(!flag) {
                damage();
                for(Enumeration enumeration = _operVec.getFigs().elements(); enumeration.hasMoreElements(); ((Fig)enumeration.nextElement()).setDisplayed(false));
                _operVec.setDisplayed(false);
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height - i);
            }
        } else
            if(flag) {
                for(Enumeration enumeration1 = _operVec.getFigs().elements(); enumeration1.hasMoreElements(); ((Fig)enumeration1.nextElement()).setDisplayed(true));
                _operVec.setDisplayed(true);
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height + i);
                damage();
            }
    }
    
    public Dimension getMinimumSize() {
        Dimension dimension = _name.getMinimumSize();
        int i = dimension.height;
        int j = dimension.width;
        if(dimension.height < 21)
            dimension.height = 21;
        if(_stereo.isDisplayed()) {
            dimension.width = Math.max(dimension.width, _stereo.getMinimumSize().width);
            dimension.height += 18;
        }
        if(_attrVec.isDisplayed()) {
            Enumeration enumeration = _attrVec.getFigs().elements();
            enumeration.nextElement();
            while(enumeration.hasMoreElements()) {
                int k = ((FigText)enumeration.nextElement()).getMinimumSize().width + 2;
                dimension.width = Math.max(dimension.width, k);
            }
            dimension.height += 17 * Math.max(1, _attrVec.getFigs().size() - 1) + 1;
        }
        if(_operVec.isDisplayed()) {
            Enumeration enumeration1 = _operVec.getFigs().elements();
            enumeration1.nextElement();
            while(enumeration1.hasMoreElements()) {
                int l = ((FigText)enumeration1.nextElement()).getMinimumSize().width + 2;
                dimension.width = Math.max(dimension.width, l);
            }
            dimension.height += 17 * Math.max(1, _operVec.getFigs().size() - 1) + 1;
        }
        return dimension;
    }
    
    public void setFillColor(Color color) {
        super.setFillColor(color);
        _stereoLineBlinder.setLineColor(color);
    }
    
    public void setLineColor(Color color) {
        super.setLineColor(color);
        _stereoLineBlinder.setLineColor(_stereoLineBlinder.getFillColor());
    }
    
    public void translate(int i, int j) {
        super.translate(i, j);
        Editor editor = Globals.curEditor();
        Selection selection = editor.getSelectionManager().findSelectionFor(this);
        if(selection instanceof SelectionClass)
            ((SelectionClass)selection).hideButtons();
    }
    
    public void mousePressed(MouseEvent mouseevent) {
        super.mousePressed(mouseevent);
        boolean flag = false;
        boolean flag1 = false;
        Editor editor = Globals.curEditor();
        Selection selection = editor.getSelectionManager().findSelectionFor(this);
        if(selection instanceof SelectionClass)
            ((SelectionClass)selection).hideButtons();
        unhighlight();
        Rectangle rectangle = new Rectangle(mouseevent.getX() - 1, mouseevent.getY() - 1, 2, 2);
        Fig fig = hitFig(rectangle);
        if(fig == _attrVec && _attrVec.getHeight() > 0) {
            Vector vector = _attrVec.getFigs();
            int i = ((vector.size() - 1) * (mouseevent.getY() - fig.getY() - 3)) / _attrVec.getHeight();
            if(i >= 0 && i < vector.size() - 1) {
                flag = true;
                fig = (Fig)vector.elementAt(i + 1);
                ((CompartmentFigText)fig).setHighlighted(true);
                highlightedFigText = (CompartmentFigText)fig;
                ProjectBrowser.TheInstance.setTarget(fig);
            }
        } else
            if(fig == _operVec && _operVec.getHeight() > 0) {
                Vector vector1 = _operVec.getFigs();
                int j = ((vector1.size() - 1) * (mouseevent.getY() - fig.getY() - 3)) / _operVec.getHeight();
                if(j >= 0 && j < vector1.size() - 1) {
                    flag = true;
                    Fig fig1 = (Fig)vector1.elementAt(j + 1);
                    ((CompartmentFigText)fig1).setHighlighted(true);
                    highlightedFigText = (CompartmentFigText)fig1;
                    ProjectBrowser.TheInstance.setTarget(fig1);
                }
            }
        if(!flag)
            ProjectBrowser.TheInstance.setTarget(getOwner());
    }
    
    public void mouseExited(MouseEvent mouseevent) {
        super.mouseExited(mouseevent);
        unhighlight();
    }
    
    public void keyPressed(KeyEvent keyevent) {
        int i = keyevent.getKeyCode();
        if(i == 38 || i == 40) {
            CompartmentFigText compartmentfigtext = unhighlight();
            if(compartmentfigtext != null) {
                int j = _attrVec.getFigs().indexOf(compartmentfigtext);
                FigGroup figgroup = _attrVec;
                if(j == -1) {
                    j = _operVec.getFigs().indexOf(compartmentfigtext);
                    figgroup = _operVec;
                }
                if(j != -1) {
                    if(i == 38)
                        compartmentfigtext = (CompartmentFigText)getPreviousVisibleFeature(figgroup, compartmentfigtext, j);
                    else
                        compartmentfigtext = (CompartmentFigText)getNextVisibleFeature(figgroup, compartmentfigtext, j);
                    if(compartmentfigtext != null) {
                        compartmentfigtext.setHighlighted(true);
                        highlightedFigText = compartmentfigtext;
                        return;
                    }
                }
            }
        } else
            if(i == 10 && highlightedFigText != null) {
                highlightedFigText.startTextEditor(keyevent);
                keyevent.consume();
                return;
            }
        super.keyPressed(keyevent);
    }
    
    public void setEnclosingFig(Fig fig) {
        Fig fig1 = getEnclosingFig();
        super.setEnclosingFig(fig);
        if(!(getOwner() instanceof MModelElement))
            return;
        MModelElement mmodelelement = (MModelElement)getOwner();
        Object obj = null;
        ProjectBrowser projectbrowser = ProjectBrowser.TheInstance;
        try {
            if(fig != null && fig1 != fig && (fig.getOwner() instanceof MPackage))
                mmodelelement.setNamespace((MNamespace)fig.getOwner());
            if(mmodelelement.getNamespace() == null && (projectbrowser.getTarget() instanceof UMLDiagram)) {
                MNamespace mnamespace = ((UMLDiagram)projectbrowser.getTarget()).getNamespace();
                mmodelelement.setNamespace(mnamespace);
            }
        }
        catch(Exception exception) {
            System.out.println("could not set package due to:" + exception + "' at " + fig);
        }
        if(fig != null && (fig.getOwner() instanceof MComponent)) {
            MComponent mcomponent = (MComponent)fig.getOwner();
            MClass mclass = (MClass)getOwner();
            resident.setImplementationLocation(mcomponent);
            resident.setResident(mclass);
        } else {
            resident.setImplementationLocation(null);
            resident.setResident(null);
        }
    }
    
    protected void textEdited(FigText figtext)
    throws PropertyVetoException {
        super.textEdited(figtext);
        MClassifier mclassifier = (MClassifier)getOwner();
        if(mclassifier == null)
            return;
        int i = _attrVec.getFigs().indexOf(figtext);
        if(i != -1) {
            highlightedFigText = (CompartmentFigText)figtext;
            highlightedFigText.setHighlighted(true);
            try {
                ParserDisplay.SINGLETON.parseAttributeFig(mclassifier, (MAttribute)highlightedFigText.getOwner(), highlightedFigText.getText().trim());
                ProjectBrowser.TheInstance.getStatusBar().showStatus("");
            }
            catch(ParseException parseexception) {
                ProjectBrowser.TheInstance.getStatusBar().showStatus("Error: " + parseexception + " at " + parseexception.getErrorOffset());
            }
            return;
        }
        i = _operVec.getFigs().indexOf(figtext);
        if(i != -1) {
            highlightedFigText = (CompartmentFigText)figtext;
            highlightedFigText.setHighlighted(true);
            try {
                ParserDisplay.SINGLETON.parseOperationFig(mclassifier, (MOperation)highlightedFigText.getOwner(), highlightedFigText.getText().trim());
                ProjectBrowser.TheInstance.getStatusBar().showStatus("");
            }
            catch(ParseException parseexception1) {
                ProjectBrowser.TheInstance.getStatusBar().showStatus("Error: " + parseexception1 + " at " + parseexception1.getErrorOffset());
            }
            return;
        } else {
            return;
        }
    }
    
    protected FigText getPreviousVisibleFeature(FigGroup figgroup, FigText figtext, int i) {
        if(figgroup == null || i < 1)
            return null;
        FigText figtext1 = null;
        Vector vector = figgroup.getFigs();
        if(i >= vector.size() || !((FigText)vector.elementAt(i)).isDisplayed())
            return null;
        do {
            for(i--; i < 1; i = vector.size() - 1) {
                figgroup = figgroup != _attrVec ? _attrVec : _operVec;
                vector = figgroup.getFigs();
            }
            
            figtext1 = (FigText)vector.elementAt(i);
            if(!figtext1.isDisplayed())
                figtext1 = null;
        } while(figtext1 == null);
        return figtext1;
    }
    
    protected FigText getNextVisibleFeature(FigGroup figgroup, FigText figtext, int i) {
        if(figgroup == null || i < 1)
            return null;
        FigText figtext1 = null;
        Vector vector = figgroup.getFigs();
        if(i >= vector.size() || !((FigText)vector.elementAt(i)).isDisplayed())
            return null;
        do {
            for(i++; i >= vector.size(); i = 1) {
                figgroup = figgroup != _attrVec ? _attrVec : _operVec;
                vector = figgroup.getFigs();
            }
            
            figtext1 = (FigText)vector.elementAt(i);
            if(!figtext1.isDisplayed())
                figtext1 = null;
        } while(figtext1 == null);
        return figtext1;
    }
    
    protected void createFeatureIn(FigGroup figgroup, InputEvent inputevent) {
        CompartmentFigText compartmentfigtext = null;
        MClassifier mclassifier = (MClassifier)getOwner();
        if(mclassifier == null)
            return;
        if(figgroup == _attrVec)
            ActionAddAttribute.SINGLETON.actionPerformed(null);
        else
            ActionAddOperation.SINGLETON.actionPerformed(null);
        compartmentfigtext = (CompartmentFigText)figgroup.getFigs().lastElement();
        if(compartmentfigtext != null) {
            compartmentfigtext.startTextEditor(inputevent);
            compartmentfigtext.setHighlighted(true);
            highlightedFigText = compartmentfigtext;
        }
    }
    
    protected CompartmentFigText unhighlight() {
        Vector vector = _attrVec.getFigs();
        for(int i = 1; i < vector.size(); i++) {
            CompartmentFigText compartmentfigtext = (CompartmentFigText)vector.elementAt(i);
            if(compartmentfigtext.isHighlighted()) {
                compartmentfigtext.setHighlighted(false);
                highlightedFigText = null;
                return compartmentfigtext;
            }
        }
        
        vector = _operVec.getFigs();
        for(int j = 1; j < vector.size(); j++) {
            CompartmentFigText compartmentfigtext1 = (CompartmentFigText)vector.elementAt(j);
            if(compartmentfigtext1.isHighlighted()) {
                compartmentfigtext1.setHighlighted(false);
                highlightedFigText = null;
                return compartmentfigtext1;
            }
        }
        
        return null;
    }


    public void refreshStereotype(){
        MClassifier mclassifier = (MClassifier)getOwner();

        removeFig(_attrVec2);
        int j = _bigPort.getX();
        int k1 = _bigPort.getY();
        int l2 = _bigPort.getWidth();
        
        if(ActionTriang.SINGLETON != null)
            showstereotype = ActionTriang.SINGLETON.isShow();
      

        if ( mclassifier != null && isNet &&(mclassifier.isPoint() ||mclassifier.isLine() ||mclassifier.isComplexObj() ||mclassifier.isPolygon())){
                logo2 = ResourceLoader.lookupIconResource("NetworkObject");
                logo = logo2.getImage();
                _FigNetworkObject = new FigImage((j + l2) - 34, k1, 17, 19, logo);
                logo2 = ResourceLoader.lookupIconResource("GeographicObject");
                logo = logo2.getImage();
                _FigGeographicObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);

        }else if(isNet){
           logo2 = ResourceLoader.lookupIconResource("NetworkObject");
           logo = logo2.getImage();
           _FigNetworkObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);
           if(_FigGeographicObject !=  null){
               _attrVec2.removeFig(_FigGeographicObject);
               _FigGeographicObject =  null;
           }
        }else{
           logo2 = ResourceLoader.lookupIconResource("GeographicObject");
           logo = logo2.getImage();
           _FigGeographicObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);
           if(_FigNetworkObject !=  null){
               _attrVec2.removeFig(_FigNetworkObject);
               _FigNetworkObject =  null;
           }
        }
        if(_FigGeographicObject != null){
            _FigGeographicObject.setFilled(true);
            _FigGeographicObject.setLineWidth(1);
        }
        if(_FigNetworkObject != null){
            _FigNetworkObject.setFilled(true);
            _FigNetworkObject.setLineWidth(1);
        }
        _attrVec2 = new FigGroup();
        _attrVec2.setFilled(true);
        _attrVec2.setLineWidth(1);
        if(_FigNetworkObject != null){
            _attrVec2.addFig(_FigNetworkObject);
        }
        if(_FigGeographicObject != null){
            _attrVec2.addFig(_FigGeographicObject);
        }
        if(!showstereotype){
            _attrVec2.removeFig(_FigGeographicObject);
            _attrVec2.removeFig(_FigNetworkObject);
            logo2 = ResourceLoader.lookupIconResource("null");
            logo = logo2.getImage();
            _FigGeographicObject = new FigImage((j + l2) - 18, k1+1, 17, 18, logo);
            _FigGeographicObject.setFilled(true);
            _FigGeographicObject.setLineWidth(1);
           _attrVec2.addFig(_FigGeographicObject);

        }
    }

    public void representation(int i) {
        MClassifier mclassifier = (MClassifier)getOwner();

        removeFig(_attrVec2);
        int j = _bigPort.getX();
        int k1 = _bigPort.getY();
        int l2 = _bigPort.getWidth();

        if(ActionTriang.SINGLETON != null)
            showstereotype = ActionTriang.SINGLETON.isShow();
      

        if ( mclassifier != null && isNet &&(mclassifier.isPoint() ||mclassifier.isLine() ||mclassifier.isComplexObj() ||mclassifier.isPolygon())){
                logo2 = ResourceLoader.lookupIconResource("NetworkObject");
                logo = logo2.getImage();
                _FigNetworkObject = new FigImage((j + l2) - 34, k1, 17, 19, logo);
                logo2 = ResourceLoader.lookupIconResource("GeographicObject");
                logo = logo2.getImage();
                _FigGeographicObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);

        }else if(isNet){
           logo2 = ResourceLoader.lookupIconResource("NetworkObject");
           logo = logo2.getImage();
           _FigNetworkObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);
           if(_FigGeographicObject !=  null){
               _attrVec2.removeFig(_FigGeographicObject);
               _FigGeographicObject =  null;
           }
        }else{
           logo2 = ResourceLoader.lookupIconResource("GeographicObject");
           logo = logo2.getImage();
           _FigGeographicObject = new FigImage((j + l2) - 17, k1, 17, 19, logo);
           if(_FigNetworkObject !=  null){
               _attrVec2.removeFig(_FigNetworkObject);
               _FigNetworkObject =  null;
           }
        }
        if(_FigGeographicObject != null){
            _FigGeographicObject.setFilled(true);
            _FigGeographicObject.setLineWidth(1);
        }
        if(_FigNetworkObject != null){
            _FigNetworkObject.setFilled(true);
            _FigNetworkObject.setLineWidth(1);
        }
        _attrVec2 = new FigGroup();
        _attrVec2.setFilled(true);
        _attrVec2.setLineWidth(1);
        if(_FigNetworkObject != null){
            _attrVec2.addFig(_FigNetworkObject);
        }
        if(_FigGeographicObject != null){
            _attrVec2.addFig(_FigGeographicObject);
        }
        if(!showstereotype){
            _attrVec2.removeFig(_FigGeographicObject);
            _attrVec2.removeFig(_FigNetworkObject);
            logo2 = ResourceLoader.lookupIconResource("null");
            logo = logo2.getImage();
            _FigGeographicObject = new FigImage((j + l2) - 18, k1+1, 17, 18, logo);
            _FigGeographicObject.setFilled(true);
            _FigGeographicObject.setLineWidth(1);
           _attrVec2.addFig(_FigGeographicObject);

        }
//TEMP{
    removeFig(_attrVecTemporal);
    
    _attrVecTemporal = new FigGroup();
    _attrVecTemporal.setFilled(true);
    _attrVecTemporal.setLineWidth(1);

/////////====================== relogio da entidade      
      if ( mclassifier != null && mclassifier.isInterval()){ 
        logoTemp = ResourceLoader.lookupIconResource("Interval");
        logoT = logoTemp.getImage();  
        fig2 = new FigImage(j +2, k1 +2, 16, 16, logoT);
        fig2.setFilled(true);
        fig2.setLineWidth(1);
        _attrVecTemporal.addFig(fig2);
      }
      else if ( mclassifier != null && mclassifier.isInstant()){   
        logoTemp = ResourceLoader.lookupIconResource("Instant");
        logoT = logoTemp.getImage();  
        fig2 = new FigImage(j +2, k1 +2, 16, 16, logoT);
        fig2.setFilled(true);
        fig2.setLineWidth(1);
        _attrVecTemporal.addFig(fig2);
      }
        
/////////====================== granularidade
  if (mclassifier != null) {
       //n�o dever ser poss�vel marcar granularidade se classe n�o tiver estere�tipo Instante ou Intervalo
       if (!mclassifier.isInterval() && !mclassifier.isInstant()) { 
         if(mclassifier.isTimeGranularity()) { 
            mclassifier.setTimeGranularity(false);
            removeFig(_attrVecTemporal);
            removeFig(_attrVec2);
         }   
         if(mclassifier.isDateGranularity()) {
            mclassifier.setDateGranularity(false);
            removeFig(_attrVecTemporal);
            removeFig(_attrVec2);
         }   
         if(mclassifier.isTimestampGranularity()) {
          mclassifier.setTimestampGranularity(false);
          removeFig(_attrVecTemporal);
          removeFig(_attrVec2);
         } 
       }
   
   //granularidade Date � a padr�o    
   if( (mclassifier.isInterval() || mclassifier.isInstant()) && !mclassifier.isTimeGranularity() && !mclassifier.isDateGranularity() && !mclassifier.isTimestampGranularity() ) {      
       mclassifier.setDateGranularity(true);
       removeFig(_attrVecTemporal);
       removeFig(_attrVec2);
   }    
  }    
/////////======================        
        
//////////////////////////Este trecho mantem o botao da chave primaria selecionado quando le arquivo XMI (se atributo for chave primaria)        
  if (mclassifier != null) {
   Collection collection = UmlHelper.getHelper().getCore().getAttributes(mclassifier);  
         
          
    if (collection != null) { 
      Iterator iterator = collection.iterator();
      while(iterator.hasNext()) {
        MAttribute mattribute = (MAttribute)iterator.next();
  
        Collection tvalues = mattribute.getTaggedValues();
        Iterator tv_iterator = tvalues.iterator();
        
        /* //imprime os TaggedValues do atributo e seus valores (true ou false)
        Iterator teste = tvalues.iterator();
        while(teste.hasNext())
          System.out.println(teste.next());           
        */   
        
        while (tv_iterator.hasNext()) {
            MTaggedValue tv = (MTaggedValue)tv_iterator.next();
            //necess�rio somente na leitura de arquivo{ - fazer mais eficiente
            if(tv.getTag().equals("transient") && tv.getValue().equals("true"))
               mattribute.setTaggedValue("transient", "true");
            if(tv.getTag().equals("volatile") && tv.getValue().equals("true"))
               mattribute.setTaggedValue("volatile", "true");
            if(tv.getTag().equals("primary key") && tv.getValue().equals("true"))
               mattribute.setTaggedValue("primary key", "true");
            //necess�rio somente na leitura de arquivo}
        }          
      }            
    }   
  }                 
////////////////////////////////////////////        
   
   //se classe tiver estere�tipo Instante ou Intervalo, colocar figura do re�gio nela 
   if(mclassifier != null && (mclassifier.isInterval() || mclassifier.isInstant()) )   
     addFig(_attrVecTemporal);           
//TEMP}        
        
        
        /*String linha = null;
        try{
        FileReader reader = new FileReader(new File("C:\\Arquivos de programas\\ArgoUML\\Codigo\\org\\argouml\\config.dat"));
        BufferedReader leitor = new BufferedReader(reader);
       
        linha = leitor.readLine();
        } catch( Exception e ){ e.printStackTrace();}
        
        if( linha.equals("show"))*/
            addFig(_attrVec2);
        
        if(i == 2)
            return;
        if(mclassifier.isPoint() && Flag_Point || !Flag_Point && i == 1) {
            if(i == 1)
                removeFig(_attrVec3);
            
            if( i != 1 ) {
                Pos_Point = Pos_Line;
                if( Pos_Polygon > Pos_Point )
                    Pos_Point = Pos_Polygon;
                if( Pos_ComplexObj > Pos_Point )
                    Pos_Point = Pos_ComplexObj;
                if( Pos_Arc > Pos_Point )
                    Pos_Point = Pos_Arc;
                if( Pos_Node > Pos_Point )
                    Pos_Point = Pos_Node;
                if( Pos_DirectArc > Pos_Point )
                    Pos_Point = Pos_DirectArc;
                Pos_Point++;
            }
            
            int k = _bigPort.getX();
            int l1 = _bigPort.getY();
            int i3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Point");
            logo = logo2.getImage();
            if( Pos_Point > 0 )
                _Point = new FigImage((k + i3) - (17 * Pos_Point), l1 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec3 = new FigGroup();
            _attrVec3.setFilled(true);
            _attrVec3.setLineWidth(1);
            _attrVec3.addFig(_Point);
            addFig(_attrVec3);
            exist_Fig = true;
            Flag_Point = false;
        } else if( !mclassifier.isPoint() ) {
            if( Pos_Line > Pos_Point && Pos_Point > 0 )
                Pos_Line--;
            if( Pos_Polygon > Pos_Point && Pos_Point > 0 )
                Pos_Polygon--;
            if( Pos_ComplexObj > Pos_Point && Pos_Point > 0 )
                Pos_ComplexObj--;
            if( Pos_Arc > Pos_Point && Pos_Point > 0 )
                Pos_Arc--;
            if( Pos_Node > Pos_Point && Pos_Point > 0 )
                Pos_Node--;
            if( Pos_DirectArc > Pos_Point && Pos_Point > 0 )
                Pos_DirectArc--;
            Pos_Point = 0;
            
            if(!mclassifier.isRoot()) {
                removeFig(_attrVec3);
                Flag_Point = true;
            }
        }
        if(mclassifier.isLine() && Flag_Line || !Flag_Line && i == 1) {
            if(i == 1)
                removeFig(_attrVec4);
            
            if( i != 1 ) {
                Pos_Line = Pos_Point;
                if( Pos_Polygon > Pos_Line )
                    Pos_Line = Pos_Polygon;
                if( Pos_ComplexObj > Pos_Line )
                    Pos_Line = Pos_ComplexObj;
                if( Pos_Arc > Pos_Line )
                    Pos_Line = Pos_Arc;
                if( Pos_Node > Pos_Line )
                    Pos_Line = Pos_Node;
                if( Pos_DirectArc > Pos_Line )
                    Pos_Line = Pos_DirectArc;
                Pos_Line++;
            }
            
            int l = _bigPort.getX();
            int i2 = _bigPort.getY();
            int j3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Line");
            logo = logo2.getImage();
            if( Pos_Line > 0 )
                _Line = new FigImage((l + j3) - (17 * Pos_Line), i2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec4 = new FigGroup();
            _attrVec4.setFilled(true);
            _attrVec4.setLineWidth(1);
            _attrVec4.addFig(_Line);
            addFig(_attrVec4);
            exist_Fig = true;
            Flag_Line = false;
        } else if( !mclassifier.isLine() ) {
            if( Pos_Point > Pos_Line && Pos_Line > 0 )
                Pos_Point--;
            if( Pos_Polygon > Pos_Line && Pos_Line > 0 )
                Pos_Polygon--;
            if( Pos_ComplexObj > Pos_Line && Pos_Line > 0 )
                Pos_ComplexObj--;
            if( Pos_Arc > Pos_Line && Pos_Line > 0 )
                Pos_Arc--;
            if( Pos_Node > Pos_Line && Pos_Line > 0 )
                Pos_Node--;
            if( Pos_DirectArc > Pos_Line && Pos_Line > 0 )
                Pos_DirectArc--;
            Pos_Line = 0;
            
            if(!mclassifier.isAbstract()) {
                removeFig(_attrVec4);
                Flag_Line = true;
            }
        }
        if(mclassifier.isPolygon() && Flag_Polygon || !Flag_Polygon && i == 1) {
            if(i == 1)
                removeFig(_attrVec5);
            
            if( i != 1 ) {
                Pos_Polygon = Pos_Point;
                if( Pos_Line > Pos_Polygon )
                    Pos_Polygon = Pos_Line;
                if( Pos_ComplexObj > Pos_Polygon )
                    Pos_Polygon = Pos_ComplexObj;
                if( Pos_Arc > Pos_Polygon )
                    Pos_Polygon = Pos_Arc;
                if( Pos_Node > Pos_Polygon )
                    Pos_Polygon = Pos_Node;
                if( Pos_DirectArc > Pos_Polygon )
                    Pos_Polygon = Pos_DirectArc;
                Pos_Polygon++;
            }
            
            int i1 = _bigPort.getX();
            int j2 = _bigPort.getY();
            int k3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Polygon");
            logo = logo2.getImage();
            if( Pos_Polygon > 0 )
                _Polygon = new FigImage((i1 + k3) - (17 * Pos_Polygon), j2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec5 = new FigGroup();
            _attrVec5.setFilled(true);
            _attrVec5.setLineWidth(1);
            _attrVec5.addFig(_Polygon);
            addFig(_attrVec5);
            exist_Fig = true;
            Flag_Polygon = false;
        } else if( !mclassifier.isPolygon() ) {
            if( Pos_Point > Pos_Polygon && Pos_Polygon > 0 )
                Pos_Point--;
            if( Pos_Line > Pos_Polygon && Pos_Polygon > 0 )
                Pos_Line--;
            if( Pos_ComplexObj > Pos_Polygon && Pos_Polygon > 0 )
                Pos_ComplexObj--;
            if( Pos_Arc > Pos_Polygon && Pos_Polygon > 0 )
                Pos_Arc--;
            if( Pos_Node > Pos_Polygon && Pos_Polygon > 0 )
                Pos_Node--;
            if( Pos_DirectArc > Pos_Polygon && Pos_Polygon > 0 )
                Pos_DirectArc--;
            Pos_Polygon = 0;
            
            if(!mclassifier.isLeaf()) {
                removeFig(_attrVec5);
                Flag_Polygon = true;
            }
        }
        if(mclassifier.isComplexObj() && Flag_ComplexObj || !Flag_ComplexObj && i == 1) {
            if(i == 1)
                removeFig(_attrVec6);
            
            if( i != 1 ) {
                Pos_ComplexObj = Pos_Point;
                if( Pos_Line > Pos_ComplexObj )
                    Pos_ComplexObj = Pos_Line;
                if( Pos_Polygon > Pos_ComplexObj )
                    Pos_ComplexObj = Pos_Polygon;
                if( Pos_Arc > Pos_ComplexObj )
                    Pos_ComplexObj = Pos_Arc;
                if( Pos_Node > Pos_ComplexObj )
                    Pos_ComplexObj = Pos_Node;
                if( Pos_DirectArc > Pos_ComplexObj )
                    Pos_ComplexObj = Pos_DirectArc;
                Pos_ComplexObj++;
            }
            
            int j1 = _bigPort.getX();
            int k2 = _bigPort.getY();
            int l3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Complex");
            logo = logo2.getImage();
            if( Pos_ComplexObj > 0 )
                _ComplexObj = new FigImage((j1 + l3) - (17 * Pos_ComplexObj), k2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec6 = new FigGroup();
            _attrVec6.setFilled(true);
            _attrVec6.setLineWidth(1);
            _attrVec6.addFig(_ComplexObj);
            addFig(_attrVec6);
            exist_Fig = true;
            Flag_ComplexObj = false;
        } else if( !mclassifier.isComplexObj() ) {
            if( Pos_Point > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_Point--;
            if( Pos_Line > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_Line--;
            if( Pos_Polygon > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_Polygon--;
            if( Pos_Arc > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_Arc--;
            if( Pos_Node > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_Node--;
            if( Pos_DirectArc > Pos_ComplexObj && Pos_ComplexObj > 0 )
                Pos_DirectArc--;
            Pos_ComplexObj = 0;
            
            removeFig(_attrVec6);
            Flag_ComplexObj = true;
        }
        if(mclassifier.isArc() && Flag_Arc || !Flag_Arc && i == 1) {
            if(i == 1)
                removeFig(_attrVec7);

            if( i != 1 ) {
                Pos_Arc = Pos_Point;
                if( Pos_Line > Pos_Arc )
                    Pos_Arc = Pos_Line;
                if( Pos_Polygon > Pos_Arc )
                    Pos_Arc = Pos_Polygon;
                if( Pos_ComplexObj > Pos_Arc )
                    Pos_Arc = Pos_ComplexObj;
                if( Pos_Node > Pos_Arc )
                    Pos_Arc = Pos_Node;
                if( Pos_DirectArc > Pos_Arc )
                    Pos_Arc = Pos_DirectArc;
                Pos_Arc++;
            }

            int j1 = _bigPort.getX();
            int k2 = _bigPort.getY();
            int l3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Arc");
            logo = logo2.getImage();
            if( Pos_Arc > 0 )
                _Arc = new FigImage((j1 + l3) - (17 * Pos_Arc), k2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec7 = new FigGroup();
            _attrVec7.setFilled(true);
            _attrVec7.setLineWidth(1);
            _attrVec7.addFig(_Arc);
            addFig(_attrVec7);
            exist_Fig = true;
            Flag_Arc = false;
        } else if( !mclassifier.isArc() ) {
            if( Pos_Point > Pos_Arc && Pos_Arc > 0 )
                Pos_Point--;
            if( Pos_Line > Pos_Arc && Pos_Arc > 0 )
                Pos_Line--;
            if( Pos_Polygon > Pos_Arc && Pos_Arc > 0 )
                Pos_Polygon--;
            if( Pos_ComplexObj > Pos_Arc && Pos_Arc > 0 )
                Pos_ComplexObj--;
            if( Pos_Node > Pos_Arc && Pos_Arc > 0 )
                Pos_Node--;
            if( Pos_DirectArc > Pos_Arc && Pos_Arc > 0 )
                Pos_DirectArc--;
            Pos_Arc = 0;

            if(!mclassifier.isAbstract()) {
                removeFig(_attrVec7);
                Flag_Arc = true;
             }
        }

        if(mclassifier.isNode() && Flag_Node || !Flag_Node && i == 1) {
            if(i == 1)
                removeFig(_attrVec8);

            if( i != 1 ) {
                Pos_Node = Pos_Point;
                if( Pos_Line > Pos_Node )
                    Pos_Node = Pos_Line;
                if( Pos_Polygon > Pos_Node )
                    Pos_Node = Pos_Polygon;
                if( Pos_ComplexObj > Pos_Node )
                    Pos_Node = Pos_ComplexObj;
                if( Pos_Arc > Pos_Node )
                    Pos_Node = Pos_Arc;
                if( Pos_DirectArc > Pos_Node )
                    Pos_Node = Pos_DirectArc;
                Pos_Node++;
            }

            int j1 = _bigPort.getX();
            int k2 = _bigPort.getY();
            int l3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("Node");
            logo = logo2.getImage();
            if( Pos_Node > 0 )
                _Node = new FigImage((j1 + l3) - (17 * Pos_Node), k2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec8 = new FigGroup();
            _attrVec8.setFilled(true);
            _attrVec8.setLineWidth(1);
            _attrVec8.addFig(_Node);
            addFig(_attrVec8);
            exist_Fig = true;
            Flag_Node = false;
        } else if( !mclassifier.isNode() ) {
            if( Pos_Point > Pos_Node && Pos_Node > 0 )
                Pos_Point--;
            if( Pos_Line > Pos_Node && Pos_Node > 0 )
                Pos_Line--;
            if( Pos_Polygon > Pos_Node && Pos_Node > 0 )
                Pos_Polygon--;
            if( Pos_ComplexObj > Pos_Node && Pos_Node > 0 )
                Pos_ComplexObj--;
            if( Pos_Arc > Pos_Node && Pos_Node > 0 )
                Pos_Arc--;
            if( Pos_DirectArc > Pos_Node && Pos_Node > 0 )
                Pos_DirectArc--;
            Pos_Node = 0;

            if(!mclassifier.isRoot()) {
                removeFig(_attrVec8);
                Flag_Node = true;
             }
        }

        if(mclassifier.isDirectArc() && Flag_DirectArc || !Flag_DirectArc && i == 1) {
            if(i == 1)
                removeFig(_attrVec9);

            if( i != 1 ) {
                Pos_DirectArc = Pos_Point;
                if( Pos_Line > Pos_DirectArc )
                    Pos_DirectArc = Pos_Line;
                if( Pos_Polygon > Pos_DirectArc )
                    Pos_DirectArc = Pos_Polygon;
                if( Pos_ComplexObj > Pos_DirectArc )
                    Pos_DirectArc = Pos_ComplexObj;
                if( Pos_Arc > Pos_DirectArc )
                    Pos_DirectArc = Pos_Arc;
                if( Pos_Node > Pos_DirectArc )
                    Pos_DirectArc = Pos_Node;
                Pos_DirectArc++;
            }

            int j1 = _bigPort.getX();
            int k2 = _bigPort.getY();
            int l3 = _bigPort.getWidth();
            logo2 = ResourceLoader.lookupIconResource("DirectArc");
            logo = logo2.getImage();
            if( Pos_DirectArc > 0 )
                _DirectArc = new FigImage((j1 + l3) - (17 * Pos_DirectArc), k2 + 20, 17, 19, logo);
            if(_FigNetworkObject != null){
                _FigNetworkObject.setFilled(true);
                _FigNetworkObject.setLineWidth(1);
            }
            else{
                _FigGeographicObject.setFilled(true);
                _FigGeographicObject.setLineWidth(1);
            }
            _attrVec9 = new FigGroup();
            _attrVec9.setFilled(true);
            _attrVec9.setLineWidth(1);
            _attrVec9.addFig(_DirectArc);
            addFig(_attrVec9);
            exist_Fig = true;
            Flag_DirectArc = false;
        } else if( !mclassifier.isDirectArc() ) {
            if( Pos_Point > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_Point--;
            if( Pos_Line > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_Line--;
            if( Pos_Polygon > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_Polygon--;
            if( Pos_ComplexObj > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_ComplexObj--;
            if( Pos_Arc > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_Arc--;
            if( Pos_Node > Pos_DirectArc && Pos_DirectArc > 0 )
                Pos_Node--;
            Pos_DirectArc = 0;

            if(!mclassifier.isAbstract()) {
                removeFig(_attrVec9);
                Flag_DirectArc = true;
             }
        }
    }
    
    protected void modelChanged() {
        super.modelChanged();
        Rectangle rectangle = getBounds();
        int i = _attrBigPort.getX();
        int j = _attrBigPort.getY();
        MClassifier mclassifier = (MClassifier)getOwner();
        if(mclassifier == null)
            return;
        int k = 1;
        Collection collection = UmlHelper.getHelper().getCore().getAttributes(mclassifier);
        if(collection != null) {
            Iterator iterator = collection.iterator();
            Vector vector = _attrVec.getFigs();
            while(iterator.hasNext()) {
                MStructuralFeature mstructuralfeature = (MStructuralFeature)iterator.next();
                CompartmentFigText compartmentfigtext;
                if(vector.size() <= k) {
                    compartmentfigtext = new CompartmentFigText(i + 1, j + 1 + (k - 1) * 17, 0, 15, _attrBigPort);
                    compartmentfigtext.setFilled(false);
                    compartmentfigtext.setLineWidth(0);
                    compartmentfigtext.setFont(FigNodeModelElement.LABEL_FONT);
                    compartmentfigtext.setTextColor(Color.black);
                    compartmentfigtext.setJustification(0);
                    compartmentfigtext.setMultiLine(false);
                    _attrVec.addFig(compartmentfigtext);
                } else {
                    compartmentfigtext = (CompartmentFigText)vector.elementAt(k);
                }
                compartmentfigtext.setText(Notation.generate(this, mstructuralfeature));
                compartmentfigtext.setOwner(mstructuralfeature);
                compartmentfigtext.setUnderline(MScopeKind.CLASSIFIER.equals(mstructuralfeature.getOwnerScope()));
                k++;
            }
            if(vector.size() > k) {
                for(int i1 = vector.size() - 1; i1 >= k; i1--)
                    _attrVec.removeFig((Fig)vector.elementAt(i1));
                
            }
            getUpdatedSize(_attrVec, i, j, 0, 0);
        }
        int l = 1;
        Collection collection1 = UmlHelper.getHelper().getCore().getOperations(mclassifier);
        if(collection1 != null) {
            collection1.removeAll(collection);
            Iterator iterator1 = collection1.iterator();
            Vector vector1 = _operVec.getFigs();
            while(iterator1.hasNext()) {
                MBehavioralFeature mbehavioralfeature = (MBehavioralFeature)iterator1.next();
                CompartmentFigText compartmentfigtext1;
                if(vector1.size() <= l) {
                    compartmentfigtext1 = new CompartmentFigText(i + 1, j + 1 + (l - 1) * 17, 0, 15, _operBigPort);
                    compartmentfigtext1.setFilled(false);
                    compartmentfigtext1.setLineWidth(0);
                    compartmentfigtext1.setFont(FigNodeModelElement.LABEL_FONT);
                    compartmentfigtext1.setTextColor(Color.black);
                    compartmentfigtext1.setJustification(0);
                    compartmentfigtext1.setMultiLine(false);
                    _operVec.addFig(compartmentfigtext1);
                } else {
                    compartmentfigtext1 = (CompartmentFigText)vector1.elementAt(l);
                }
                compartmentfigtext1.setText(Notation.generate(this, mbehavioralfeature));
                compartmentfigtext1.setOwner(mbehavioralfeature);
                compartmentfigtext1.setUnderline(MScopeKind.CLASSIFIER.equals(mbehavioralfeature.getOwnerScope()));
                if(((MOperation)mbehavioralfeature).isAbstract())
                    compartmentfigtext1.setFont(FigNodeModelElement.ITALIC_LABEL_FONT);
                else
                    compartmentfigtext1.setFont(FigNodeModelElement.LABEL_FONT);
                l++;
            }
            if(vector1.size() > l) {
                for(int j1 = vector1.size() - 1; j1 >= l; j1--)
                    _operVec.removeFig((Fig)vector1.elementAt(j1));
                
            }
        }
        if(mclassifier.isAbstract())
            _name.setFont(FigNodeModelElement.ITALIC_LABEL_FONT);
        else
            _name.setFont(FigNodeModelElement.LABEL_FONT);
        representation(0);
        setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void renderingChanged() {
        super.renderingChanged();
        updateStereotypeText();
        modelChanged();
    }
    
    protected void updateStereotypeText() {
        MModelElement mmodelelement = (MModelElement)getOwner();
        Rectangle rectangle = getBounds();
        ru.novosoft.uml.foundation.extension_mechanisms.MStereotype mstereotype = mmodelelement.getStereotype();
        _stereo.setText( "  " );
        if(!_stereo.isDisplayed()) {
            _stereoLineBlinder.setDisplayed(true);
            _stereo.setDisplayed(true);
            if(!_newlyCreated) {
                rectangle.y -= 18;
                rectangle.height += 18;
                setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        _newlyCreated = false;
    }
    
    public void setBounds(int i, int j, int k, int l) {
        Rectangle rectangle = getBounds();
        Dimension dimension = checkSize ? getMinimumSize() : new Dimension(k, l);
        int i1 = Math.max(k, dimension.width);
        int j1 = l;
        int k1 = 0;
        int l1 = 0;
        if(j1 <= dimension.height) {
            j1 = dimension.height;
        } else {
            int i2 = 1;
            if(_attrVec.isDisplayed())
                i2++;
            if(_operVec.isDisplayed())
                i2++;
            k1 = (j1 - dimension.height) / i2;
            l1 = j1 - dimension.height - k1 * i2;
        }
        int j2 = _name.getMinimumSize().height;
        if(j2 < 21)
            j2 = 21;
        j2 += k1 + l1;
        int k2 = j;
        if(_stereo.isDisplayed())
            k2 += 18;
        //_name.setBounds(i, k2, i1, j2);
        _name.setBounds(i, j, i1, 19);
        //_stereo.setBounds(i, j, i1, 19);
        _stereo.setBounds(i, k2, i1, j2);
        _stereoLineBlinder.setBounds(i + 1, j + 18, i1 - 2, 2);
        k2 += j2 - 1;
        int l2 = _attrVec.isDisplayed() ? Math.max(1, _attrVec.getFigs().size() - 1) : 0;
        int i3 = _operVec.isDisplayed() ? Math.max(1, _operVec.getFigs().size() - 1) : 0;
        if(checkSize)
            j2 = 17 * l2 + 2 + k1;
        else
            if(j1 > k2 - j && l2 + i3 > 0)
                j2 = (((j1 + j) - k2) * l2) / (l2 + i3) + 1;
            else
                j2 = 1;
        dimension = getUpdatedSize(_attrVec, i, k2, i1, j2);
        if(_attrVec.isDisplayed())
            k2 += dimension.height - 1;
        dimension = getUpdatedSize(_operVec, i, k2, i1, (j1 + j) - k2);
        _bigPort.setBounds(i, j, i1, j1);
        calcBounds();
        updateEdges();
        firePropChange("bounds", rectangle, getBounds());
        if(exist_Fig)
            representation(1);
        else
            representation(2);
    }
    
    public void setOwner(Object obj) {
        MClass mclass = (MClass)getOwner();
        if(mclass != null) {
            MFeature mfeature;
            for(Iterator iterator = mclass.getFeatures().iterator(); iterator.hasNext(); mfeature.removeMElementListener(this))
                mfeature = (MFeature)iterator.next();
            
        }
        super.setOwner(obj);
        mclass = (MClass)obj;
        if(mclass != null) {
            MFeature mfeature1;
            for(Iterator iterator1 = mclass.getFeatures().iterator(); iterator1.hasNext(); mfeature1.addMElementListener(this)) {
                mfeature1 = (MFeature)iterator1.next();
                mfeature1.removeMElementListener(this);
            }
            
        }
    }
  
    protected FigGroup _attrVec;                   
    protected FigGroup _attrVec3;
    protected FigGroup _attrVec4;
    protected FigGroup _attrVec5;
    protected FigGroup _attrVec6;
    protected FigGroup _attrVec7;
    protected FigGroup _attrVec8;
    protected FigGroup _attrVec9;
    protected FigImage _Point;
    protected FigImage _Line;
    protected FigImage _Polygon;
    protected FigImage _ComplexObj;
    protected FigImage _Net;
    protected FigImage _Arc;
    protected FigImage _Node;
    protected FigImage _DirectArc;
    protected boolean Flag_Point;
    protected int Pos_Point = 0;
    protected boolean Flag_Line;
    protected int Pos_Line = 0;
    protected boolean Flag_Polygon;
    protected int Pos_Polygon = 0;
    protected boolean Flag_ComplexObj;
    protected int Pos_ComplexObj = 0;
    protected boolean Flag_Net;
    protected int Pos_Net = 0;
    protected boolean Flag_Arc;
    protected int Pos_Arc = 0;
    protected boolean Flag_Node;
    protected int Pos_Node = 0;
    protected boolean Flag_DirectArc;
    protected int Pos_DirectArc = 0;
    protected boolean exist_Fig;
    protected boolean showstereotype;
    protected FigGroup _operVec;
    protected FigRect _attrBigPort;
    protected FigRect _operBigPort;
    protected FigRect _stereoLineBlinder;
    public MElementResidence resident;
    protected CompartmentFigText highlightedFigText;
    private boolean _newlyCreated;
    protected FigGroup _attrVec2;
    protected FigImage _FigGeographicObject;
    protected FigImage _FigNetworkObject;
    Image logo;
    ImageIcon logo2;
    boolean isNet;
    
//TEMP{
    protected ImageIcon logoTemp;
    protected Image logoT;
    protected FigImage fig2;
    protected FigGroup _attrVecTemporal;
//TEMP}    
    
}

