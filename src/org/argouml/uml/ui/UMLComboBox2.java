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

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import ru.novosoft.uml.MElementEvent;

/**
 * ComboBox for UML modelelements. This implementation does not use 
 * reflection and seperates Model, View and Controller better then does
 * UMLComboBox. In the future UMLComboBoxModel and UMLComboBox will be
 * replaced with this implementation to improve performance.
 */
public abstract class UMLComboBox2
    extends JComboBox
    implements UMLUserInterfaceComponent {

    protected UMLUserInterfaceContainer container = null;
    
    /**
     * Constructor for UMLActivatorComboBox.
     * @param arg0
     */
    public UMLComboBox2(UMLUserInterfaceContainer container, UMLComboBoxModel2 arg0) {
        super(arg0);
        setContainer(container);
        addActionListener(this);
    }

    /**
     * Returns the container.
     * @return UMLUserInterfaceContainer
     */
    public UMLUserInterfaceContainer getContainer() {
        return container;
    }

    /**
     * Sets the container.
     * @param container The container to set
     */
    public void setContainer(UMLUserInterfaceContainer container) {
        this.container = container;
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetChanged()
     */
    public void targetChanged() {
        ((UMLComboBoxModel2)getModel()).targetChanged();
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetReasserted()
     */
    public void targetReasserted() {
        ((UMLComboBoxModel2)getModel()).targetReasserted();
    }

    /**
     * @see ru.novosoft.uml.MElementListener#listRoleItemSet(MElementEvent)
     */
    public void listRoleItemSet(MElementEvent e) {
        ((UMLComboBoxModel2)getModel()).listRoleItemSet(e);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#propertySet(MElementEvent)
     */
    public void propertySet(MElementEvent e) {
        // same as itemstatechanged because they happen at the same moment
        // therefore no action here
        ((UMLComboBoxModel2)getModel()).propertySet(e);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#recovered(MElementEvent)
     */
    public void recovered(MElementEvent e) {
        ((UMLComboBoxModel2)getModel()).recovered(e);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#removed(MElementEvent)
     */
    public void removed(MElementEvent e) {
        ((UMLComboBoxModel2)getModel()).removed(e);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleAdded(MElementEvent)
     */
    public void roleAdded(MElementEvent e) {
        ((UMLComboBoxModel2)getModel()).roleAdded(e);
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleRemoved(MElementEvent)
     */
    public void roleRemoved(MElementEvent e) {
        ((UMLComboBoxModel2)getModel()).roleRemoved(e);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        int i = getSelectedIndex();
        if ( i >= 0) {
            doIt(arg0);
        }
    }
    
    protected abstract void doIt(ActionEvent event);

}
