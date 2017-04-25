// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// File: FigGeographicField.java
// Classes: FigGeographicField
// Original Author: abonner
// $Id: FigGeographicField.java,v 1.37 2002/09/29 13:06:53 d00mst Exp $

// 21 Mar 2002: Jeremy Bennett (mail@jeremybennett.com). Fix for ever
// increasing vertical size of classes with stereotypes (issue 745).


package org.argouml.uml.diagram.static_structure.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import ru.novosoft.uml.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.model_management.*;

import ru.novosoft.uml.behavior.activity_graphs.*;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.*;
import org.tigris.gef.graph.*;
import org.tigris.gef.util.*;
import org.argouml.language.helpers.*;
import org.argouml.application.api.*;
import org.argouml.uml.*;
import org.argouml.uml.ui.*;
import org.argouml.uml.generator.*;
import org.argouml.uml.diagram.ui.*;
import org.argouml.ui.*;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlHelper;

//TEMP{
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
//TEMP}

/**
 * <p>Class to display graphics for a UML Class in a diagram.</p>
 */

public class FigGeographicField extends FigNodeModelElement {
    
    ////////////////////////////////////////////////////////////////
    // constants
    
    ////////////////////////////////////////////////////////////////
    // instance variables
    
    /**
     * <p>The vector of graphics for attributes (if any). First one is the
     *   rectangle for the entire attributes box.</p>
     */
    protected FigGroup _attrVec;
    
    
    protected FigGroup _attrVec2;//Figura de representacao
    protected FigGroup _attrVec3;
    protected FigGroup _attrVec4;
    protected FigGroup _attrVec5;
    protected FigGroup _attrVec6;
    protected FigGroup _attrVec7;
    protected FigGroup _attrVec8;
    
    protected FigImage _GridOfCels;
    protected FigImage _AdjPolygons;
    protected FigImage _Isolines;
    protected FigImage _GridOfPoints;
    protected FigImage _TIN;
    protected FigImage _IrregularPoints;
    
    
    
    protected boolean Flag_GridOfCels = true;
    protected int Pos_GridOfCels = 0;
    protected boolean Flag_AdjPolygons = true;
    protected int Pos_AdjPolygons = 0;
    protected boolean Flag_Isolines = true;
    protected int Pos_Isolines = 0;
    protected boolean Flag_GridOfPoints = true;
    protected int Pos_GridOfPoints = 0;
    protected boolean Flag_TIN = true;
    protected int Pos_TIN = 0;
    protected boolean Flag_IrregularPoints = true;
    protected int Pos_IrregularPoints = 0;
    
    protected boolean exist_Fig;
    protected boolean showstereotype;
    
    protected FigImage _FigGeographicField;
    Image logo;
    ImageIcon logo2;
    
//TEMP{
    protected ImageIcon logoTemp;
    protected Image logoT;
    protected FigImage fig2;
    protected FigGroup _attrVecTemporal;
//TEMP}    
    
    
    /**
     * <p>The vector of graphics for operations (if any). First one is the
     *   rectangle for the entire operations box.</p>
     */
    protected FigGroup _operVec;
    
    /**
     * <p>The rectangle for the entire attribute box.</p>
     */
    protected FigRect _attrBigPort;
    
    /**
     * <p>The rectangle for the entire operations box.</p>
     */
    protected FigRect _operBigPort;
    
    /**
     * <p>A rectangle to blank out the line that would otherwise appear at the
     *   bottom of the stereotype text box.</p>
     */
    protected FigRect _stereoLineBlinder;
    
    /**
     * <p>Manages residency of a class within a component on a deployment
     *   diagram. Not clear why it is public, or even why it is an instance
     *   variable (rather than local to the method).</p>
     */
    public MElementResidence resident = UmlFactory.getFactory().getCore().createElementResidence();
    
    
    /**
     * <p>Text highlighted by mouse actions on the diagram.</p>
     */
    protected CompartmentFigText highlightedFigText = null;
    
    /**
     * <p>Flag to indicate that we have just been created. This is to fix the
     *   problem with loading classes that have stereotypes already
     *   defined.</p>
     */
    private boolean _newlyCreated = false;
    
    ////////////////////////////////////////////////////////////////
    // constructors
    
    /**
     * <p>Main constructor for a {@link FigGeographicField}.</p>
     *
     * <p>Parent {@link FigNodeModelElement} will have created the main box
     *   {@link #_bigPort} and its name {@link #_name} and stereotype (@link
     *   #_stereo}. This constructor creates a box for the attributes and
     *   operations.</p>
     *
     * <p>The properties of all these graphic elements are adjusted
     *   appropriately. The main boxes are all filled and have outlines.</p>
     *
     * <p>For reasons I don't understand the stereotype is created in a box
     *   with lines. So we have to created a blanking rectangle to overlay the
     *   bottom line, and avoid four compartments showing.</p>
     *
     * <p>There is some complex logic to allow for the possibility that
     *   stereotypes may not be displayed (unlike operations and attributes
     *   this is not a standard thing for UML). Some care is needed to ensure
     *   that additional space is not added, each time a stereotyped class is
     *   loaded.</p>
     *
     * <p>There is a particular problem when loading diagrams with stereotyped
     *   classes. Because we create a FigGeographicField indicating the stereotype is not
     *   displayed, we then add extra space for such classes when they are
     *   first rendered. This ought to be fixed by correctly saving the class
     *   dimensions, but that needs more work. The solution here is to use a
     *   simple flag to indicate the FigGeographicField has just been created.</p>
     *
     * <p><em>Warning</em>. Much of the graphics positioning is hard coded. The
     *   overall figure is placed at location (10,10). The name compartment (in
     *   the parent {@link FigNodeModelElement} is 21 pixels high. The
     *   stereotype compartment is created 15 pixels high in the parent, but we
     *   change it to 19 pixels, 1 more than ({@link #STEREOHEIGHT} here. The
     *   attribute and operations boxes are created at 19 pixels, 2 more than
     *   {@link #ROWHEIGHT}.</p>
     *
     * <p>CAUTION: This constructor (with no arguments) is the only one
     *   that does enableSizeChecking(false), all others must set it true.
     *   This is because this constructor is the only one called when loading
     *   a project. In this case, the parsed size must be maintained.</p>
     */


    static java.util.List<FigGeographicField> list;

    private void initializeList(){
        if(list == null){
            list = new ArrayList();
            list.add(this);
        }else if(!list.contains((FigGeographicField)(this))){
            list.add(this);
        }

    }

    public java.util.List<FigGeographicField> getList(){
        return list;
    }


    public FigGeographicField() {
        initializeList();
        // Set name box. Note the upper line will be blanked out if there is
        // eventually a stereotype above.
        _name.setLineWidth(1);
        _name.setFilled(true);
        
        // this rectangle marks the attribute section; all attributes are inside it
        _attrBigPort = new FigRect(10, 30, 60, ROWHEIGHT+2, Color.black, Color.white);
        _attrBigPort.setFilled(true);
        _attrBigPort.setLineWidth(1);
        
        // Attributes inside. First one is the attribute box itself.
        _attrVec = new FigGroup();
        _attrVec.setFilled(true);
        _attrVec.setLineWidth(1);
        _attrVec.addFig(_attrBigPort);
        
        // this rectangle marks the operation section; all operations are inside it
        _operBigPort = new FigRect(10, 31+ROWHEIGHT, 60, ROWHEIGHT+2, Color.black, Color.white);
        _operBigPort.setFilled(true);
        _operBigPort.setLineWidth(1);
        
        _operVec = new FigGroup();
        _operVec.setFilled(true);
        _operVec.setLineWidth(1);
        _operVec.addFig(_operBigPort);
        
        // Set properties of the stereotype box. Make it 1 pixel higher than
        // before, so it overlaps the name box, and the blanking takes out both
        // lines. Initially not set to be displayed, but this will be changed
        // when we try to render it, if we find we have a stereotype.
        
        //Colocado por Maur�cio Fid�lis Rodrigues J�nior- Projeto XMLGeoFrame
        
        Rectangle   rect   = getBounds();
        //_stereo.setText("Interface");
        
        
        
        _stereo.setExpandOnly(true);
        _stereo.setFilled(true);
        _stereo.setLineWidth(1);
        _stereo.setEditable(false);
        _stereo.setHeight(STEREOHEIGHT+1); // +1 to have 1 pixel overlap with _name
        _stereo.setDisplayed(true);
        
        // A thin rectangle to overlap the boundary line between stereotype
        // and name. This is just 2 pixels high, and we rely on the line
        // thickness, so the rectangle does not need to be filled. Whether to
        // display is linked to whether to display the stereotype.
        _stereoLineBlinder = new FigRect(11, 10+STEREOHEIGHT, 58, 2, Color.white, Color.white);
        _stereoLineBlinder.setLineWidth(1);
        _stereoLineBlinder.setDisplayed(true);
        
        if (!_stereo.isDisplayed()) {
            _stereoLineBlinder.setDisplayed(true);
            _stereo.setDisplayed(true);
            
            if( !_newlyCreated ) {
                rect.y      -= STEREOHEIGHT;
                rect.height += STEREOHEIGHT;
                setBounds(rect.x, rect.y, rect.width, rect.height);
            }
        }
        
        // Mark this as newly created. This is to get round the problem with
        // creating figs for loaded classes that had stereotypes. They are
        // saved with their dimensions INCLUDING the stereotype, but since we
        // pretend the stereotype is not visible, we add height the first time
        // we render such a class. This is a complete fudge, and really we
        // ought to address how class objects with stereotypes are saved. But
        // that will be hard work.
        //    _newlyCreated = true;
        
        // Put all the bits together, suppressing bounds calculations until
        // we're all done for efficiency.
        enableSizeChecking(false);
        suppressCalcBounds = true;
        addFig(_bigPort);
        addFig(_stereo);
        addFig(_name);
        addFig(_stereoLineBlinder);
        addFig(_attrVec);
        addFig(_operVec);
        suppressCalcBounds = false;
        
        // Set the bounds of the figure to the total of the above (hardcoded)
        setBounds(10, 10, 60, 22+2*ROWHEIGHT);
    }
    
    /**
     * <p>Constructor for use if this figure is created for an existing class
     *   node in the metamodel.</p>
     *
     * <p>Set the figure's name according to this node. This is used when the
     *   user click's on 'add to diagram' in the navpane.  Don't know if this
     *   should rather be done in one of the super classes, since similar code
     *   is used in FigInterface.java etc.  Andreas Rueckert
     *   &lt;a_rueckert@gmx.net&gt;</p>
     *
     * @param gm   Not actually used in the current implementation
     *
     * @param node The UML object being placed.
     */
    public FigGeographicField(GraphModel gm, Object node) {
        this();
        enableSizeChecking(true);
        setOwner(node);
        if ((node instanceof MClassifier) && (((MClassifier)node).getName() != null))
            _name.setText(((MModelElement)node).getName());
    }
    
    public String placeString() { return "new Class"; }
    
    public Object clone() {
        FigGeographicField figClone = (FigGeographicField) super.clone();
        Vector v = figClone.getFigs();
        figClone._bigPort = (FigRect) v.elementAt(0);
        figClone._stereo = (FigText) v.elementAt(1);
        figClone._name = (FigText) v.elementAt(2);
        figClone._stereoLineBlinder = (FigRect) v.elementAt(3);
        figClone._attrVec = (FigGroup) v.elementAt(4);
        figClone._operVec = (FigGroup) v.elementAt(5);
        return figClone;
    }
    
    ////////////////////////////////////////////////////////////////
    // accessors
    
    public Selection makeSelection() {
        return new SelectionClass4(this);
    }
    
    
    /**
     * Build a collection of menu items relevant for a right-click popup menu on a Package.
     *
     * @param     me     a mouse event
     * @return           a collection of menu items
     */
    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = super.getPopUpActions(me);
        JMenu addMenu = new JMenu("Add");
        addMenu.add(ActionAddAttribute.SINGLETON);
        addMenu.add(ActionAddOperation.SINGLETON);
        addMenu.add(ActionAddNote.SINGLETON);
        popUpActions.insertElementAt(addMenu, popUpActions.size() - 1);
        JMenu showMenu = new JMenu("Show");
        if(_attrVec.isDisplayed() && _operVec.isDisplayed())
            showMenu.add(ActionCompartmentDisplay.HideAllCompartments);
        else if(!_attrVec.isDisplayed() && !_operVec.isDisplayed())
            showMenu.add(ActionCompartmentDisplay.ShowAllCompartments);
        
        if (_attrVec.isDisplayed())
            showMenu.add(ActionCompartmentDisplay.HideAttrCompartment);
        else
            showMenu.add(ActionCompartmentDisplay.ShowAttrCompartment);
        
        if (_operVec.isDisplayed())
            showMenu.add(ActionCompartmentDisplay.HideOperCompartment);
        else
            showMenu.add(ActionCompartmentDisplay.ShowOperCompartment);
        
        popUpActions.insertElementAt(showMenu, popUpActions.size() - 1);
        
        // Block added by BobTarling 7-Jan-2001
        MClass mclass = (MClass) getOwner();
        ArgoJMenu modifierMenu = new ArgoJMenu("Representation");
        
        
        //Mauricio
        Class mclass2 = MActionState.class;
        
        
        modifierMenu.addCheckItem(new ActionModifier("GridOfCels", "isGridOfCels", "isGridOfCels", "setGridOfCels", mclass));
        modifierMenu.addCheckItem(new ActionModifier("AdjPolygons", "isAdjPolygons", "isAdjPolygons", "setAdjPolygons", mclass));
        modifierMenu.addCheckItem(new ActionModifier("Isolines", "isIsolines", "isIsolines", "setIsolines", mclass));
        modifierMenu.addCheckItem(new ActionModifier("GridOfPoints", "�sGridOfPoints", "isGridOfPoints", "setGridOfPoints", mclass));
        modifierMenu.addCheckItem(new ActionModifier("TIN", "isTIN", "isTIN", "setTIN", mclass));
        modifierMenu.addCheckItem(new ActionModifier("IrregularPoints", "isIrregularPoints", "isIrregularPoints", "setIrregularPoints", mclass));
        
        popUpActions.insertElementAt(modifierMenu, popUpActions.size() - 1);
        // end of block
        
        return popUpActions;
    }
    
    public FigGroup getOperationsFig() { return _operVec; }
    public FigGroup getAttributesFig() { return _attrVec; }
    
    /**
     * Returns the status of the operation field.
     * @return true if the operations are visible, false otherwise
     */
    public boolean isOperationVisible() { return _operVec.isDisplayed(); }
    
    /**
     * Returns the status of the attribute field.
     * @return true if the attributes are visible, false otherwise
     */
    public boolean isAttributeVisible() { return _attrVec.isDisplayed(); }
    
    public void setAttributeVisible(boolean isVisible) {
        checkSize = true;
        Rectangle rect = getBounds();
        int h = checkSize ? (ROWHEIGHT*Math.max(1,_attrVec.getFigs().size()-1)+2) * rect.height / getMinimumSize().height : 0;
        if ( _attrVec.isDisplayed() ) {
            if ( !isVisible ) {
                damage();
                Enumeration enumeracao = _attrVec.getFigs().elements();
                while (enumeracao.hasMoreElements())
                    ((Fig)(enumeracao.nextElement())).setDisplayed(false);
                _attrVec.setDisplayed(false);
                setBounds(rect.x, rect.y, rect.width, rect.height - h);
            }
        }
        else {
            if ( isVisible ) {
                Enumeration enumeracao = _attrVec.getFigs().elements();
                while (enumeracao.hasMoreElements())
                    ((Fig)(enumeracao.nextElement())).setDisplayed(true);
                _attrVec.setDisplayed(true);
                setBounds(rect.x, rect.y, rect.width, rect.height + h);
                damage();
            }
        }
    }
    
    public void setOperationVisible(boolean isVisible) {
        checkSize = true;
        Rectangle rect = getBounds();
        int h = checkSize ? (ROWHEIGHT*Math.max(1,_operVec.getFigs().size()-1)+2) * rect.height / getMinimumSize().height : 0;
        if ( _operVec.isDisplayed() ) {
            if ( !isVisible ) {
                damage();
                Enumeration enumeracao = _operVec.getFigs().elements();
                while (enumeracao.hasMoreElements())
                    ((Fig)(enumeracao.nextElement())).setDisplayed(false);
                _operVec.setDisplayed(false);
                setBounds(rect.x, rect.y, rect.width, rect.height - h);
            }
        }
        else {
            if ( isVisible ) {
                Enumeration enumeracao = _operVec.getFigs().elements();
                while (enumeracao.hasMoreElements())
                    ((Fig)(enumeracao.nextElement())).setDisplayed(true);
                _operVec.setDisplayed(true);
                setBounds(rect.x, rect.y, rect.width, rect.height + h);
                damage();
            }
        }
    }
    
    
    /**
     * <p>Gets the minimum size permitted for a class on the diagram.</p>
     *
     * <p>Parts of this are hardcoded, notably the fact that the name
     *   compartment has a minimum height of 21 pixels.</p>
     *
     * @return  the size of the minimum bounding box.
     */
    public Dimension getMinimumSize() {
        
        // Use "aSize" to build up the minimum size. Start with the size of the
        // name compartment and build up.
        
        Dimension aSize = _name.getMinimumSize();
        int h = aSize.height;
        int w = aSize.width;
        
        // Ensure that the minimum height of the name compartment is at least
        // 21 pixels (hardcoded).
        
        if (aSize.height < 21) {
            aSize.height = 21;
        }
        
        // If we have a stereotype displayed, then allow some space for that
        // (width and height)
        
        if (_stereo.isDisplayed()) {
            aSize.width = Math.max(aSize.width, _stereo.getMinimumSize().width);
            aSize.height += STEREOHEIGHT;
        }
        
        // Allow space for each of the attributes we have
        
        if (_attrVec.isDisplayed()) {
            
            // Loop through all the attributes, to find the widest (remember
            // the first fig is the box for the whole lot, so ignore it).
            
            Enumeration enumeracao = _attrVec.getFigs().elements();
            enumeracao.nextElement();  // ignore
            
            while (enumeracao.hasMoreElements()) {
                int elemWidth = ((FigText)enumeracao.nextElement()).getMinimumSize().width + 2;
                aSize.width = Math.max(aSize.width, elemWidth);
            }
            
            // Height allows one row for each attribute (remember to ignore the
            // first element.
            
            aSize.height += ROWHEIGHT * Math.max(1, _attrVec.getFigs().size() -1 ) + 1;
        }
        
        // Allow space for each of the operations we have
        
        if (_operVec.isDisplayed()) {
            
            // Loop through all the operations, to find the widest (remember
            // the first fig is the box for the whole lot, so ignore it).
            
            Enumeration enumeracao = _operVec.getFigs().elements();
            enumeracao.nextElement();  // ignore
            
            while (enumeracao.hasMoreElements()) {
                int elemWidth = ((FigText)enumeracao.nextElement()).getMinimumSize().width + 2;
                aSize.width = Math.max(aSize.width, elemWidth);
            }
            
            aSize.height += ROWHEIGHT * Math.max(1, _operVec.getFigs().size() - 1) + 1;
        }
        
        // And now aSize has the answer
        
        return aSize;
    }
    
    public void setFillColor(Color lColor) {
        super.setFillColor(lColor);
        _stereoLineBlinder.setLineColor(lColor);
    }
    
    public void setLineColor(Color lColor) {
        super.setLineColor(lColor);
        _stereoLineBlinder.setLineColor(_stereoLineBlinder.getFillColor());
    }
    
    public void translate(int dx, int dy) {
        super.translate(dx, dy);
        Editor ce = Globals.curEditor();
        Selection sel = ce.getSelectionManager().findSelectionFor(this);
        if (sel instanceof SelectionClass)
            ((SelectionClass)sel).hideButtons();
    }
    
    ////////////////////////////////////////////////////////////////
    // user interaction methods
    
    public void mousePressed(MouseEvent me) {
        super.mousePressed(me);
        boolean targetIsSet = false;
        int i = 0;
        Editor ce = Globals.curEditor();
        Selection sel = ce.getSelectionManager().findSelectionFor(this);
        if (sel instanceof SelectionClass)
            ((SelectionClass)sel).hideButtons();
        unhighlight();
        //display attr/op properties if necessary:
        Rectangle r = new Rectangle(me.getX() - 1, me.getY() - 1, 2, 2);
        Fig f = hitFig(r);
        if (f == _attrVec && _attrVec.getHeight() > 0) {
            Vector v = _attrVec.getFigs();
            i = (v.size()-1) * (me.getY() - f.getY() - 3) / _attrVec.getHeight();
            if (i >= 0 && i < v.size()-1) {
                targetIsSet = true;
                f = (Fig)v.elementAt(i+1);
                ((CompartmentFigText)f).setHighlighted(true);
                highlightedFigText = (CompartmentFigText)f;
                ProjectBrowser.TheInstance.setTarget(f);
            }
        }
        else if (f == _operVec && _operVec.getHeight() > 0) {
            Vector v = _operVec.getFigs();
            i = (v.size()-1) * (me.getY() - f.getY() - 3) / _operVec.getHeight();
            if (i >= 0 && i < v.size()-1) {
                targetIsSet = true;
                f = (Fig)v.elementAt(i+1);
                ((CompartmentFigText)f).setHighlighted(true);
                highlightedFigText = (CompartmentFigText)f;
                ProjectBrowser.TheInstance.setTarget(f);
            }
        }
        if (targetIsSet == false)
            ProjectBrowser.TheInstance.setTarget(getOwner());
    }
    
    public void mouseExited(MouseEvent me) {
        super.mouseExited(me);
        unhighlight();
    }
    
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            CompartmentFigText ft = unhighlight();
            if (ft != null) {
                int i = _attrVec.getFigs().indexOf(ft);
                FigGroup fg = _attrVec;
                if (i == -1) {
                    i = _operVec.getFigs().indexOf(ft);
                    fg = _operVec;
                }
                if (i != -1) {
                    if (key == KeyEvent.VK_UP) {
                        ft = (CompartmentFigText)getPreviousVisibleFeature(fg,ft,i);
                    } else {
                        ft = (CompartmentFigText)getNextVisibleFeature(fg,ft,i);
                    }
                    if (ft != null) {
                        ft.setHighlighted(true);
                        highlightedFigText = ft;
                        return;
                    }
                }
            }
        } else if (key == KeyEvent.VK_ENTER && highlightedFigText != null) {
            highlightedFigText.startTextEditor(ke);
            ke.consume();
            return;
        }
        super.keyPressed(ke);
    }
    
    public void setEnclosingFig(Fig encloser) {
        Fig oldEncloser = getEnclosingFig();
        super.setEnclosingFig(encloser);
        if (!(getOwner() instanceof MModelElement)) return;
        MModelElement me = (MModelElement) getOwner();
        MNamespace m = null;
        ProjectBrowser pb = ProjectBrowser.TheInstance;
        
        try {
            // If moved into an Package
            if (encloser != null && oldEncloser != encloser &&
            encloser.getOwner() instanceof MPackage) {
                me.setNamespace((MNamespace) encloser.getOwner());
            }
            
            // If default Namespace is not already set
            if (me.getNamespace() == null &&
            pb.getTarget() instanceof UMLDiagram) {
                m = (MNamespace) ((UMLDiagram)pb.getTarget()).getNamespace();
                me.setNamespace(m);
            }
        }
        catch (Exception e) {
            System.out.println("could not set package due to:"+e + "' at "+encloser);
        }
        
        // The next if-clause is important for the Deployment-diagram
        // it detects if the enclosing fig is a component, in this case
        // the ImplementationLocation will be set for the owning MClass
        if (encloser != null && (encloser.getOwner() instanceof MComponent)) {
            MComponent component = (MComponent) encloser.getOwner();
            MClass cl = (MClass) getOwner();
            resident.setImplementationLocation(component);
            resident.setResident(cl);
        }
        else {
            resident.setImplementationLocation(null);
            resident.setResident(null);
        }
    }
    
    ////////////////////////////////////////////////////////////////
    // internal methods
    
    protected void textEdited(FigText ft) throws PropertyVetoException {
        super.textEdited(ft);
        MClassifier cls = (MClassifier) getOwner();
        if (cls == null) return;
        int i = _attrVec.getFigs().indexOf(ft);
        if (i != -1) {
            highlightedFigText = (CompartmentFigText)ft;
            highlightedFigText.setHighlighted(true);
            try {
                ParserDisplay.SINGLETON.parseAttributeFig(cls,(MAttribute)highlightedFigText.getOwner(),highlightedFigText.getText().trim());
                ProjectBrowser.TheInstance.getStatusBar().showStatus("");
            } catch (ParseException pe) {
                ProjectBrowser.TheInstance.getStatusBar().showStatus("Error: " + pe + " at " + pe.getErrorOffset());
            }
            return;
        }
        i = _operVec.getFigs().indexOf(ft);
        if (i != -1) {
            highlightedFigText = (CompartmentFigText)ft;
            highlightedFigText.setHighlighted(true);
            try {
                ParserDisplay.SINGLETON.parseOperationFig(cls,(MOperation)highlightedFigText.getOwner(),highlightedFigText.getText().trim());
                ProjectBrowser.TheInstance.getStatusBar().showStatus("");
            } catch (ParseException pe) {
                ProjectBrowser.TheInstance.getStatusBar().showStatus("Error: " + pe + " at " + pe.getErrorOffset());
            }
            return;
        }
    }
    
    protected FigText getPreviousVisibleFeature(FigGroup fgVec, FigText ft, int i) {
        if (fgVec == null || i < 1 )
            return null;
        FigText ft2 = null;
        Vector v = fgVec.getFigs();
        if (i >= v.size() || !((FigText)v.elementAt(i)).isDisplayed())
            return null;
        do {
            i--;
            while (i < 1) {
                fgVec = (fgVec == _attrVec) ? _operVec : _attrVec;
                v = fgVec.getFigs();
                i = v.size() - 1;
            }
            ft2 = (FigText)v.elementAt(i);
            if (!ft2.isDisplayed())
                ft2 = null;
        } while (ft2 == null);
        return ft2;
    }
    
    protected FigText getNextVisibleFeature(FigGroup fgVec, FigText ft, int i) {
        if (fgVec == null || i < 1 )
            return null;
        FigText ft2 = null;
        Vector v = fgVec.getFigs();
        if (i >= v.size() || !((FigText)v.elementAt(i)).isDisplayed())
            return null;
        do {
            i++;
            while (i >= v.size()) {
                fgVec = (fgVec == _attrVec) ? _operVec : _attrVec;
                v = fgVec.getFigs();
                i = 1;
            }
            ft2 = (FigText)v.elementAt(i);
            if (!ft2.isDisplayed())
                ft2 = null;
        } while (ft2 == null);
        return ft2;
    }
    
    protected void createFeatureIn(FigGroup fg, InputEvent ie) {
        CompartmentFigText ft = null;
        MClassifier cls = (MClassifier)getOwner();
        if (cls == null)
            return;
        if (fg == _attrVec)
            ActionAddAttribute.SINGLETON.actionPerformed(null);
        else
            ActionAddOperation.SINGLETON.actionPerformed(null);
        ft = (CompartmentFigText)fg.getFigs().lastElement();
        if (ft != null) {
            ft.startTextEditor(ie);
            ft.setHighlighted(true);
            highlightedFigText = ft;
        }
    }
    
    protected CompartmentFigText unhighlight() {
        CompartmentFigText ft;
        Vector v = _attrVec.getFigs();
        int i;
        for (i = 1; i < v.size(); i++) {
            ft = (CompartmentFigText)v.elementAt(i);
            if (ft.isHighlighted()) {
                ft.setHighlighted(false);
                highlightedFigText = null;
                return ft;
            }
        }
        v = _operVec.getFigs();
        for (i = 1; i < v.size(); i++) {
            ft = (CompartmentFigText)v.elementAt(i);
            if (ft.isHighlighted()) {
                ft.setHighlighted(false);
                highlightedFigText = null;
                return ft;
            }
        }
        return null;
    }
    
    protected void modelChanged() {
        super.modelChanged();
        Rectangle rect = getBounds();
        int xpos = _attrBigPort.getX();
        int ypos = _attrBigPort.getY();
        MClassifier cls = (MClassifier) getOwner();
        if (cls == null)
            return;
        int acounter = 1;
        Collection strs = UmlHelper.getHelper().getCore().getAttributes(cls);
        if (strs != null) {
            Iterator iter = strs.iterator();
            Vector figs = _attrVec.getFigs();
            CompartmentFigText attr;
            while (iter.hasNext()) {
                MStructuralFeature sf = (MStructuralFeature) iter.next();
                if (figs.size() <= acounter) {
                    attr = new CompartmentFigText(xpos+1, ypos+1+(acounter-1)*ROWHEIGHT, 0, ROWHEIGHT-2, _attrBigPort); // bounds not relevant here
                    attr.setFilled(false);
                    attr.setLineWidth(0);
                    attr.setFont(LABEL_FONT);
                    attr.setTextColor(Color.black);
                    attr.setJustification(FigText.JUSTIFY_LEFT);
                    attr.setMultiLine(false);
                    _attrVec.addFig(attr);
                } else {
                    attr = (CompartmentFigText)figs.elementAt(acounter);
                }
                attr.setText(Notation.generate(this,sf));
                attr.setOwner(sf);
                // underline, if static
                attr.setUnderline(MScopeKind.CLASSIFIER.equals(sf.getOwnerScope()));
                acounter++;
            }
            if (figs.size() > acounter) {
                //cleanup of unused attribute FigText's
                for (int i=figs.size()-1; i>=acounter; i--)
                    _attrVec.removeFig((Fig)figs.elementAt(i));
            }
            getUpdatedSize(_attrVec, xpos, ypos, 0, 0);
        }
        int ocounter = 1;
        Collection behs = UmlHelper.getHelper().getCore().getOperations(cls);
        if (behs != null) {
            behs.removeAll(strs);
            Iterator iter = behs.iterator();
            Vector figs = _operVec.getFigs();
            CompartmentFigText oper;
            while (iter.hasNext()) {
                MBehavioralFeature bf = (MBehavioralFeature) iter.next();
                if (figs.size() <= ocounter) {
                    oper = new CompartmentFigText(xpos+1, ypos+1+(ocounter-1)*ROWHEIGHT, 0, ROWHEIGHT-2, _operBigPort); // bounds not relevant here
                    oper.setFilled(false);
                    oper.setLineWidth(0);
                    oper.setFont(LABEL_FONT);
                    oper.setTextColor(Color.black);
                    oper.setJustification(FigText.JUSTIFY_LEFT);
                    oper.setMultiLine(false);
                    _operVec.addFig(oper);
                } else {
                    oper = (CompartmentFigText)figs.elementAt(ocounter);
                }
                oper.setText(Notation.generate(this,bf));
                oper.setOwner(bf);
                // underline, if static
                oper.setUnderline(MScopeKind.CLASSIFIER.equals(bf.getOwnerScope()));
                // italics, if abstract
                //oper.setItalic(((MOperation)bf).isAbstract()); // does not properly work (GEF bug?)
                if (((MOperation)bf).isAbstract()) oper.setFont(ITALIC_LABEL_FONT);
                else oper.setFont(LABEL_FONT);
                ocounter++;
            }
            if (figs.size() > ocounter) {
                //cleanup of unused operation FigText's
                for (int i=figs.size()-1; i>=ocounter; i--)
                    _operVec.removeFig((Fig)figs.elementAt(i));
            }
        }
        
        if (cls.isAbstract()) _name.setFont(ITALIC_LABEL_FONT);
        else _name.setFont(LABEL_FONT);
        
        representation(0);
        setBounds(rect.x, rect.y, rect.width, rect.height); // recalculates all bounds
    }
    
    public void refreshStereotype(){
        MClassifier mclassifier = (MClassifier) getOwner();

        if(ActionTriang.SINGLETON != null)
            showstereotype = ActionTriang.SINGLETON.isShow();

        
        if( 1 < 2 ) {
            removeFig(_attrVec2);
            //Geographic Field
            int x = _bigPort.getX();
            int y = _bigPort.getY();
            int w = _bigPort.getWidth();


            logo2 = ResourceLoader.lookupIconResource("GeographicField");
            if(!showstereotype)
                logo2 = ResourceLoader.lookupIconResource("null");
            logo = logo2.getImage();
            if(!showstereotype)
                _FigGeographicField = new FigImage((x+w-18)-1, y+2, 17, 18,logo);
            else
                _FigGeographicField = new FigImage((x+w-17), y, 17, 19,logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec2 = new FigGroup();
            _attrVec2.setFilled(true);
            _attrVec2.setLineWidth(1);
            _attrVec2.addFig(_FigGeographicField);
        }
    }


    //Colocado por Maur�cio Fid�lis Rodrigues J�nior- Projeto XML-GeoFrame
    // Modificado por Val�rio Moys�s Vilela
    public void representation(int i){
        MClassifier mclassifier = (MClassifier) getOwner();

        if(ActionTriang.SINGLETON != null)
            showstereotype = ActionTriang.SINGLETON.isShow();

        
        if( 1 < 2 ) {
            removeFig(_attrVec2);
            //Geographic Field
            int x = _bigPort.getX();
            int y = _bigPort.getY();
            int w = _bigPort.getWidth();


            logo2 = ResourceLoader.lookupIconResource("GeographicField");
            if(!showstereotype)
                logo2 = ResourceLoader.lookupIconResource("null");
            logo = logo2.getImage();
            if(!showstereotype)
                _FigGeographicField = new FigImage((x+w-18)-1, y+2, 17, 18,logo);
            else
                _FigGeographicField = new FigImage((x+w-17), y, 17, 19,logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec2 = new FigGroup();
            _attrVec2.setFilled(true);
            _attrVec2.setLineWidth(1);
            _attrVec2.addFig(_FigGeographicField);
            
//TEMP{
    removeFig(_attrVecTemporal);
    
    _attrVecTemporal = new FigGroup();
    _attrVecTemporal.setFilled(true);
    _attrVecTemporal.setLineWidth(1);

/////////====================== relogio da entidade      
      if ( mclassifier != null && mclassifier.isInterval()){ 
        logoTemp = ResourceLoader.lookupIconResource("Interval");
        logoT = logoTemp.getImage();  
        fig2 = new FigImage(x +2, y +2, 16, 16, logoT);
        fig2.setFilled(true);
        fig2.setLineWidth(1);
        _attrVecTemporal.addFig(fig2);
      }
      else if ( mclassifier != null && mclassifier.isInstant()){   
        logoTemp = ResourceLoader.lookupIconResource("Instant");
        logoT = logoTemp.getImage();  
        fig2 = new FigImage(x +2, y +2, 16, 16, logoT);
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
            
            
            
          
            
        }
        
        if(i==2) return;
        
        if ((mclassifier.isGridOfPoints() && Flag_GridOfPoints) || ((!Flag_GridOfPoints) && (i==1))) {				//Point
            
            if( i == 1 )
                removeFig(_attrVec3);
            
            if( i != 1 ) {
                Pos_GridOfPoints = Pos_GridOfCels;
                if( Pos_AdjPolygons > Pos_GridOfPoints )
                    Pos_GridOfPoints = Pos_AdjPolygons;
                if( Pos_IrregularPoints > Pos_GridOfPoints )
                    Pos_GridOfPoints = Pos_IrregularPoints;
                if( Pos_Isolines > Pos_GridOfPoints )
                    Pos_GridOfPoints = Pos_Isolines;
                if( Pos_TIN > Pos_GridOfPoints )
                    Pos_GridOfPoints = Pos_TIN;
                Pos_GridOfPoints++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("GridOfPoints");
            logo= logo2.getImage();
            if( Pos_GridOfPoints > 0 )
                _GridOfPoints= new FigImage(( x + w ) - ( 17 * Pos_GridOfPoints ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec3 = new FigGroup();
            _attrVec3.setFilled(true);
            _attrVec3.setLineWidth(1);
            _attrVec3.addFig(_GridOfPoints);
            
            
              String linha = null;
     /*   try{ 
        File f = new File("C:\\Arquivos de programas\\ArgoUML\\Codigo\\org\\argouml\\config.dat");
        FileReader reader = new FileReader(f);
        BufferedReader leitor = new BufferedReader(reader);
       
        linha = leitor.readLine();
        } catch( Exception e ){ e.printStackTrace();}
        
        if( linha.equals("show"))*/
              addFig(_attrVec3);
              exist_Fig=true;
              Flag_GridOfPoints=false;/*         
          
            exist_Fig= true;
            Flag_GridOfPoints=false;*/
        }
        else if( !mclassifier.isGridOfPoints() ) {
            if( Pos_GridOfCels > Pos_GridOfPoints && Pos_GridOfPoints > 0 )
                Pos_GridOfCels--;
            if( Pos_AdjPolygons > Pos_GridOfPoints && Pos_GridOfPoints > 0 )
                Pos_AdjPolygons--;
            if( Pos_IrregularPoints > Pos_GridOfPoints && Pos_GridOfPoints > 0 )
                Pos_IrregularPoints--;
            if( Pos_Isolines > Pos_GridOfPoints && Pos_GridOfPoints > 0 )
                Pos_Isolines--;
            if( Pos_TIN > Pos_GridOfPoints && Pos_GridOfPoints > 0 )
                Pos_TIN--;
            Pos_GridOfPoints = 0;
            
            removeFig(_attrVec3);
            Flag_GridOfPoints=true;
        }
        
        if((mclassifier.isAdjPolygons()&&Flag_AdjPolygons)||((!Flag_AdjPolygons)&&(i==1))) {
            
            if(i==1)
                removeFig(_attrVec4);
            
            if( i != 1 ) {
                Pos_AdjPolygons = Pos_GridOfCels;
                if( Pos_GridOfPoints > Pos_AdjPolygons )
                    Pos_AdjPolygons = Pos_GridOfPoints;
                if( Pos_IrregularPoints > Pos_AdjPolygons )
                    Pos_AdjPolygons = Pos_IrregularPoints;
                if( Pos_Isolines > Pos_AdjPolygons )
                    Pos_AdjPolygons = Pos_Isolines;
                if( Pos_TIN > Pos_AdjPolygons )
                    Pos_AdjPolygons = Pos_TIN;
                Pos_AdjPolygons++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("AdjPolygons");
            logo= logo2.getImage();
            if( Pos_AdjPolygons > 0 )
                _AdjPolygons= new FigImage( ( x + w ) - ( 17 * Pos_AdjPolygons ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec4 = new FigGroup();
            _attrVec4.setFilled(true);
            _attrVec4.setLineWidth(1);
            _attrVec4.addFig(_AdjPolygons);
            addFig(_attrVec4);
            exist_Fig=true;
            Flag_AdjPolygons=false;
        }
        else if( !mclassifier.isAdjPolygons() ) {
            if( Pos_GridOfCels > Pos_AdjPolygons && Pos_AdjPolygons > 0 )
                Pos_GridOfCels--;
            if( Pos_GridOfPoints > Pos_AdjPolygons && Pos_AdjPolygons > 0 )
                Pos_GridOfPoints--;
            if( Pos_IrregularPoints > Pos_AdjPolygons && Pos_AdjPolygons > 0 )
                Pos_IrregularPoints--;
            if( Pos_Isolines > Pos_AdjPolygons && Pos_AdjPolygons > 0 )
                Pos_Isolines--;
            if( Pos_TIN > Pos_AdjPolygons && Pos_AdjPolygons > 0 )
                Pos_TIN--;
            Pos_AdjPolygons = 0;
            
            removeFig(_attrVec4);
            Flag_AdjPolygons=true;
        }
        
        if((mclassifier.isIsolines()&&Flag_Isolines)||((!Flag_Isolines)&&(i==1))) {		//Polygon
            
            if(i==1)
                removeFig(_attrVec5);
            
            if( i != 1 ) {
                Pos_Isolines = Pos_GridOfCels;
                if( Pos_GridOfPoints > Pos_Isolines )
                    Pos_Isolines = Pos_GridOfPoints;
                if( Pos_IrregularPoints > Pos_Isolines )
                    Pos_Isolines = Pos_IrregularPoints;
                if( Pos_AdjPolygons > Pos_Isolines )
                    Pos_Isolines = Pos_AdjPolygons;
                if( Pos_TIN > Pos_Isolines )
                    Pos_Isolines = Pos_TIN;
                Pos_Isolines++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("Isolines");
            logo= logo2.getImage();
            if( Pos_Isolines > 0 )
                _Isolines= new FigImage( ( x + w ) - ( 17 * Pos_Isolines ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec5 = new FigGroup();
            _attrVec5.setFilled(true);
            _attrVec5.setLineWidth(1);
            _attrVec5.addFig(_Isolines);
            addFig(_attrVec5);
            exist_Fig=true;
            Flag_Isolines=false;
        }
        else if(!mclassifier.isIsolines()) {
            if( Pos_GridOfCels > Pos_Isolines && Pos_Isolines > 0 )
                Pos_GridOfCels--;
            if( Pos_GridOfPoints > Pos_Isolines && Pos_Isolines > 0 )
                Pos_GridOfPoints--;
            if( Pos_IrregularPoints > Pos_Isolines && Pos_Isolines > 0 )
                Pos_IrregularPoints--;
            if( Pos_AdjPolygons > Pos_Isolines && Pos_Isolines > 0 )
                Pos_AdjPolygons--;
            if( Pos_TIN > Pos_Isolines && Pos_Isolines > 0 )
                Pos_TIN--;
            Pos_Isolines = 0;
            
            removeFig(_attrVec5);
            Flag_Isolines=true;
        }
        
        if((mclassifier.isTIN()&&Flag_TIN)||((!Flag_TIN)&&(i==1))) {   //ComplexObj
            
            if(i==1)
                removeFig(_attrVec6);
            
            if( i != 1 ) {
                Pos_TIN = Pos_GridOfCels;
                if( Pos_GridOfPoints > Pos_TIN )
                    Pos_TIN = Pos_GridOfPoints;
                if( Pos_IrregularPoints > Pos_TIN )
                    Pos_TIN = Pos_IrregularPoints;
                if( Pos_AdjPolygons > Pos_TIN )
                    Pos_TIN = Pos_AdjPolygons;
                if( Pos_Isolines > Pos_TIN )
                    Pos_TIN = Pos_Isolines;
                Pos_TIN++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("TIN");
            logo= logo2.getImage();
            if( Pos_TIN > 0 )
                _TIN= new FigImage( ( x + w ) - ( 17 * Pos_TIN ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec6 = new FigGroup();
            _attrVec6.setFilled(true);
            _attrVec6.setLineWidth(1);
            _attrVec6.addFig(_TIN);
            addFig(_attrVec6);
            exist_Fig=true;
            Flag_TIN=false;
        }
        else if( !mclassifier.isTIN() ) {
            if( Pos_GridOfCels > Pos_TIN && Pos_TIN > 0 )
                Pos_GridOfCels--;
            if( Pos_GridOfPoints > Pos_TIN && Pos_TIN > 0 )
                Pos_GridOfPoints--;
            if( Pos_IrregularPoints > Pos_TIN && Pos_TIN > 0 )
                Pos_IrregularPoints--;
            if( Pos_AdjPolygons > Pos_TIN && Pos_TIN > 0 )
                Pos_AdjPolygons--;
            if( Pos_Isolines > Pos_TIN && Pos_TIN > 0 )
                Pos_Isolines--;
            Pos_TIN = 0;
            
            removeFig(_attrVec6);
            Flag_TIN = true;
        }
        
        if((mclassifier.isGridOfCels()&&Flag_GridOfCels)||((!Flag_GridOfCels)&&(i==1))) {   //ComplexObj
            
            if(i==1)
                removeFig(_attrVec7);
            
            if( i != 1 ) {
                Pos_GridOfCels = Pos_TIN;
                if( Pos_GridOfPoints > Pos_GridOfCels )
                    Pos_GridOfCels = Pos_GridOfPoints;
                if( Pos_IrregularPoints > Pos_GridOfCels )
                    Pos_GridOfCels = Pos_IrregularPoints;
                if( Pos_AdjPolygons > Pos_GridOfCels )
                    Pos_GridOfCels = Pos_AdjPolygons;
                if( Pos_Isolines > Pos_GridOfCels )
                    Pos_GridOfCels = Pos_Isolines;
                Pos_GridOfCels++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("GridOfCels");
            logo= logo2.getImage();
            if( Pos_GridOfCels > 0 )
                _GridOfCels= new FigImage( ( x + w ) - ( 17 * Pos_GridOfCels ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec7 = new FigGroup();
            _attrVec7.setFilled(true);
            _attrVec7.setLineWidth(1);
            _attrVec7.addFig(_GridOfCels);
            addFig(_attrVec7);
            exist_Fig=true;
            Flag_GridOfCels=false;
        }
        else if( !mclassifier.isGridOfCels() ) {
            if( Pos_TIN > Pos_GridOfCels && Pos_GridOfCels > 0 )
                Pos_TIN--;
            if( Pos_GridOfPoints > Pos_GridOfCels && Pos_GridOfCels > 0 )
                Pos_GridOfPoints--;
            if( Pos_IrregularPoints > Pos_GridOfCels && Pos_GridOfCels > 0 )
                Pos_IrregularPoints--;
            if( Pos_AdjPolygons > Pos_GridOfCels && Pos_GridOfCels > 0 )
                Pos_AdjPolygons--;
            if( Pos_Isolines > Pos_GridOfCels && Pos_GridOfCels > 0 )
                Pos_Isolines--;
            Pos_GridOfCels = 0;
            
            removeFig(_attrVec7);
            Flag_GridOfCels=true;
        }
        
        if((mclassifier.isIrregularPoints()&&Flag_IrregularPoints)||((!Flag_IrregularPoints)&&(i==1))) {   //ComplexObj
            
            if(i==1)
                removeFig(_attrVec8);
            
            if( i != 1 ) {
                Pos_IrregularPoints = Pos_TIN;
                if( Pos_GridOfPoints > Pos_IrregularPoints )
                    Pos_IrregularPoints = Pos_GridOfPoints;
                if( Pos_GridOfCels > Pos_IrregularPoints )
                    Pos_IrregularPoints = Pos_GridOfCels;
                if( Pos_AdjPolygons > Pos_IrregularPoints )
                    Pos_IrregularPoints = Pos_AdjPolygons;
                if( Pos_Isolines > Pos_IrregularPoints )
                    Pos_IrregularPoints = Pos_Isolines;
                Pos_IrregularPoints++;
            }
            
            int x= _bigPort.getX();
            int y= _bigPort.getY();
            int w=_bigPort.getWidth();
            logo2= ResourceLoader.lookupIconResource("IrregularPoints");
            logo= logo2.getImage();
            if( Pos_IrregularPoints > 0 )
                _IrregularPoints= new FigImage( ( x + w ) - ( 17 * Pos_IrregularPoints ), y + 20, 17, 19, logo);
            _FigGeographicField.setFilled(true);
            _FigGeographicField.setLineWidth(1);
            _attrVec8 = new FigGroup();
            _attrVec8.setFilled(true);
            _attrVec8.setLineWidth(1);
            _attrVec8.addFig(_IrregularPoints);
            addFig(_attrVec8);
            exist_Fig=true;
            Flag_IrregularPoints=false;
        }
        else if( !mclassifier.isIrregularPoints() ) {
            if( Pos_TIN > Pos_IrregularPoints && Pos_IrregularPoints > 0 )
                Pos_TIN--;
            if( Pos_GridOfPoints > Pos_IrregularPoints && Pos_IrregularPoints > 0 )
                Pos_GridOfPoints--;
            if( Pos_GridOfCels > Pos_IrregularPoints && Pos_IrregularPoints > 0 )
                Pos_GridOfCels--;
            if( Pos_AdjPolygons > Pos_IrregularPoints && Pos_IrregularPoints > 0 )
                Pos_AdjPolygons--;
            if( Pos_Isolines > Pos_IrregularPoints && Pos_IrregularPoints > 0 )
                Pos_Isolines--;
            Pos_IrregularPoints = 0;
            
            removeFig(_attrVec8);
            Flag_IrregularPoints=true;
        }
        
    }
    
    
    public void renderingChanged() {
        super.renderingChanged();
        updateStereotypeText();
        modelChanged();
    }
    
    protected void updateStereotypeText() {
        
        MModelElement me = (MModelElement) getOwner();
        
        // if (me == null) {
        //      return;
        //  }
        
        Rectangle   rect   = getBounds();
        MStereotype stereo = me.getStereotype();
/*
    if ((stereo == null) ||
        (stereo.getName() == null) ||
        (stereo.getName().length() == 0)) {
 
      if (_stereo.isDisplayed()) {
          _stereoLineBlinder.setDisplayed(false);
          _stereo.setDisplayed(false);
        rect.y      += STEREOHEIGHT;
        rect.height -= STEREOHEIGHT;
         setBounds(rect.x, rect.y, rect.width, rect.height);
      }
    }
    else {*/
        // _stereo.setText(Notation.generateStereotype(this,stereo));
        _stereo.setText("  ");
        if (!_stereo.isDisplayed()) {
            _stereoLineBlinder.setDisplayed(true);
            _stereo.setDisplayed(true);
            
            // Only adjust the stereotype height if we are not newly
            // created. This gets round the problem of loading classes with
            // stereotypes defined, which have the height already including
            // the stereotype.
            
            if( !_newlyCreated ) {
                rect.y      -= STEREOHEIGHT;
                rect.height += STEREOHEIGHT;
                setBounds(rect.x, rect.y, rect.width, rect.height);
            }
            
        }
        //}
        
        // Whatever happened we are no longer newly created, so clear the
        // flag. Then set the bounds for the rectangle we have defined.
        
        _newlyCreated = false;
        
        
    }
    
    /**
     * <p>Sets the bounds, but the size will be at least the one returned by
     *   {@link #getMinimumSize()}, unless checking of size is disabled.</p>
     *
     * <p>If the required height is bigger, then the additional height is
     *   equally distributed among all figs (i.e. compartments), such that the
     *   cumulated height of all visible figs equals the demanded height<p>.
     *
     * <p>Some of this has "magic numbers" hardcoded in. In particular there is
     *   a knowledge that the minimum height of a name compartment is 21
     *   pixels.</p>
     *
     * @param x  Desired X coordinate of upper left corner
     *
     * @param y  Desired Y coordinate of upper left corner
     *
     * @param w  Desired width of the FigGeographicField
     *
     * @param h  Desired height of the FigGeographicField
     */
    public void setBounds(int x, int y, int w, int h) {
        
        // Save our old boundaries (needed later), and get minimum size
        // info. "aSize will be used to maintain a running calculation of our
        // size at various points.
        
        // "extra_each" is the extra height per displayed fig if requested
        // height is greater than minimal. "height_correction" is the height
        // correction due to rounded division result, will be added to the name
        // compartment
        
        Rectangle oldBounds = getBounds();
        Dimension aSize = checkSize ? getMinimumSize() : new Dimension(w,h);
        
        int newW = Math.max(w,aSize.width);
        int newH = h;
        
        int extra_each = 0;
        int height_correction = 0;
        
        // First compute all nessessary height data. Easy if we want less than
        // the minimum
        
        if (newH <= aSize.height) {
            
            // Just use the mimimum
            newH = aSize.height;
            
        } else {
            
            // Split the extra amongst the number of displayed compartments
            
            int displayedFigs = 1; //this is for _name
            
            if (_attrVec.isDisplayed()) {
                displayedFigs++;
            }
            
            if (_operVec.isDisplayed()) {
                displayedFigs++;
            }
            
            // Calculate how much each, plus a correction to put in the name
            // comparment if the result is rounded
            
            extra_each        = (newH - aSize.height) / displayedFigs;
            height_correction = (newH - aSize.height) -
            (extra_each * displayedFigs);
        }
        
        // Now resize all sub-figs, including not displayed figs. Start by the
        // name. We override the getMinimumSize if it is less than our view (21
        // pixels hardcoded!). Add in the shared extra, plus in this case the
        // correction.
        
        int height = _name.getMinimumSize().height;
        
        if (height < 21) {
            height = 21;
        }
        
        height += extra_each + height_correction;
        
        // Now sort out the stereotype display. If the stereotype is displayed,
        // move the upper boundary of the name compartment up and set new
        // bounds for the name and the stereotype compatments and the
        // stereoLineBlinder that blanks out the line between the two
        
        int currentY = y;
        
        if (_stereo.isDisplayed()) {
            currentY += STEREOHEIGHT;
        }
        
        //_name.setBounds(x,currentY,newW,height);
        _name.setBounds(x, y, newW, STEREOHEIGHT + 1);
        //_stereo.setBounds(x,y,newW,STEREOHEIGHT + 1);
        _stereo.setBounds(x, currentY, newW, height);
        _stereoLineBlinder.setBounds(x + 1,y + STEREOHEIGHT,newW - 2,2);
        
        // Advance currentY to where the start of the attribute box is,
        // remembering that it overlaps the next box by 1 pixel. Calculate the
        // size of the attribute box, and update the Y pointer past it if it is
        // displayed.
        
        currentY += height-1;  // -1 for 1 pixel overlap
        
        int na = (_attrVec.isDisplayed()) ? Math.max(1,_attrVec.getFigs().size()-1) : 0;
        int no = (_operVec.isDisplayed()) ? Math.max(1,_operVec.getFigs().size()-1) : 0;
        if (checkSize) {
            height = ROWHEIGHT * na + 2 + extra_each;
        } else if (newH > currentY-y && na+no > 0) {
            height = (newH+y-currentY) * na / (na+no) + 1;
        } else {
            height = 1;
        }
        aSize = getUpdatedSize(_attrVec, x, currentY, newW, height);
        
        if (_attrVec.isDisplayed()) {
            currentY += aSize.height - 1;  // -1 for 1 pixel overlap
        }
        
        // Finally update the bounds of the operations box
        
        aSize = getUpdatedSize(_operVec, x, currentY, newW,
        newH + y - currentY);
        
        // set bounds of big box
        
        _bigPort.setBounds(x,y,newW,newH);
        
        // Now force calculation of the bounds of the figure, update the edges
        // and trigger anyone who's listening to see if the "bounds" property
        // has changed.
        
        calcBounds();
        updateEdges();
        firePropChange("bounds", oldBounds, getBounds());
        if(exist_Fig)
            representation(1);
        else representation(2);
    }
    
    /**
     * @see org.tigris.gef.presentation.Fig#setOwner(Object)
     */
    public void setOwner(Object own) {
        MClass cl;
        cl = (MClass)getOwner();
        if (cl != null) {
            Iterator it = cl.getFeatures().iterator();
            while (it.hasNext()) {
                MFeature feat = (MFeature)it.next();
                feat.removeMElementListener(this);
            }
        }
        super.setOwner(own);
        cl = (MClass)own;
        if (cl != null) {
            Iterator it = cl.getFeatures().iterator();
            while (it.hasNext()) {
                MFeature feat = (MFeature)it.next();
                feat.removeMElementListener(this);
                feat.addMElementListener(this);
            }
        }
    }
    
} /* end class FigGeographicField */
