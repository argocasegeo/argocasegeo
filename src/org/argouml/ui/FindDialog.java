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

package org.argouml.ui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.behavior.use_cases.*;
import ru.novosoft.uml.behavior.common_behavior.*;
import ru.novosoft.uml.behavior.collaborations.*;
import ru.novosoft.uml.model_management.*;

import org.tigris.gef.util.*;

import org.argouml.application.api.*;
import org.argouml.uml.*;
import org.argouml.uml.cognitive.*;

/** this one of the few classes in Argo that is
 * self running.
 *
 * The search is buggy and needs work.
 */
public class FindDialog extends JDialog
implements ActionListener, MouseListener {

  ////////////////////////////////////////////////////////////////
  // class variables

  public static FindDialog SINGLETON = new FindDialog();
  public static int nextResultNum = 1;

  public static int _numFinds = 1;

  ////////////////////////////////////////////////////////////////
  // instance variables
  protected JButton     _search     = new JButton("Search");
  protected JButton     _clearTabs  = new JButton("Clear Tabs");
  protected JTabbedPane _tabs       = new JTabbedPane();
  protected JPanel      _nameLocTab = new JPanel();
  protected JPanel     _modifiedTab = new JPanel();
  protected JPanel      _tagValsTab = new JPanel();
  protected JPanel  _constraintsTab = new JPanel();

  protected JComboBox   _elementName = new JComboBox();
  protected JComboBox   _diagramName = new JComboBox();
  protected JComboBox   _location    = new JComboBox();
  protected JComboBox   _type        = new JComboBox();
  protected JPanel      _typeDetails = new JPanel();
  protected JTextField  _tag         = new JTextField();
  protected JTextField  _val         = new JTextField();

  protected JTabbedPane _results     = new JTabbedPane();
  protected JPanel      _help        = new JPanel();
  protected Vector      _resultTabs  = new Vector();

  ////////////////////////////////////////////////////////////////
  // constructors

  public FindDialog() {
    super(ProjectBrowser.TheInstance, "Search");
    getContentPane().setLayout(new BorderLayout());


    initNameLocTab();
    _tabs.addTab("Name and Location", _nameLocTab);

    initModifiedTab();
    _tabs.addTab("Last Modified", _modifiedTab);
    _tabs.setEnabledAt(1, false);

    initTagValsTab();
    _tabs.addTab("Tagged Values", _tagValsTab);
    _tabs.setEnabledAt(2, false);

    initConstraintsTab();
    _tabs.addTab(Argo.localize("UMLMenu", "tab.constraints"), _constraintsTab);
    _tabs.setEnabledAt(3, false);

    //_tabs.addTab("Tagged Values", _tagValsTab);
    _tabs.setMinimumSize(new Dimension(300, 250));

    JPanel north = new JPanel();
    north.setLayout(new BorderLayout());
    north.add(_tabs, BorderLayout.CENTER);
    getContentPane().add(north, BorderLayout.NORTH);

    initHelpTab();
    _results.addTab("Help", _help);
    getContentPane().add(_results, BorderLayout.CENTER);

//     JPanel south = new JPanel();
//     south.setLayout(new FlowLayout(FlowLayout.RIGHT));
//     JPanel buttonPane = new JPanel();
//     buttonPane.setLayout(new GridLayout(1, 4));
    //     buttonPane.add(_clear);
    //     buttonPane.add(_spawn);
    //     buttonPane.add(_go);
    //     buttonPane.add(_close);
//     south.add(buttonPane);
//     getContentPane().add(south, BorderLayout.SOUTH);
//     getRootPane().setDefaultButton(_search);
    _search.addActionListener(this);
    _results.addMouseListener(this);

    _clearTabs.addActionListener(this);
    _clearTabs.setEnabled(false);
    //     _spawn.addActionListener(this);
    //     _go.addActionListener(this);
    //     _close.addActionListener(this);
    setSize(new Dimension(480, 550));
  }

  public void initNameLocTab() {
    _elementName.setEditable(true);
    _elementName.getEditor().getEditorComponent().setBackground(Color.white);
    _diagramName.setEditable(true);
    _diagramName.getEditor().getEditorComponent().setBackground(Color.white);

    _elementName.addItem("*");
    _diagramName.addItem("*");

    // needs-more-work: add recent patterns
    GridBagLayout gb = new GridBagLayout();
    _nameLocTab.setLayout(gb);

    JLabel elementNameLabel = new JLabel("Element Name:");
    JLabel diagramNameLabel = new JLabel("In Diagram:");
    JLabel typeLabel = new JLabel("Element Type:");
    JLabel locLabel = new JLabel("Search In:");

    _location.addItem("Entire Project");

    _typeDetails.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    initTypes();

    _typeDetails.setMinimumSize(new Dimension(200, 100));
    _typeDetails.setPreferredSize(new Dimension(200, 100));
    _typeDetails.setSize(new Dimension(200, 100));

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.ipadx = 3; c.ipady = 3;
    c.gridwidth = 1;

    c.gridx = 0;     c.gridy = 0;
    c.weightx = 0.0;
    gb.setConstraints(elementNameLabel, c);
    _nameLocTab.add(elementNameLabel);

    c.gridx = 1;     c.gridy = 0;
    c.weightx = 1.0;
    gb.setConstraints(_elementName, c);
    _nameLocTab.add(_elementName);

    c.gridx = 0;     c.gridy = 1;
    c.weightx = 0.0;
    gb.setConstraints(diagramNameLabel, c);
    _nameLocTab.add(diagramNameLabel);

    c.gridx = 1;     c.gridy = 1;
    c.weightx = 1.0;
    gb.setConstraints(_diagramName, c);
    _nameLocTab.add(_diagramName);

    // open space at gridy = 2

    c.gridx = 0;     c.gridy = 3;
    c.weightx = 0.0;
    gb.setConstraints(locLabel, c);
    _nameLocTab.add(locLabel);

    c.gridx = 1;     c.gridy = 3;
    c.weightx = 1.0;
    gb.setConstraints(_location, c);
    _nameLocTab.add(_location);

    SpacerPanel spacer = new SpacerPanel();
    c.gridx = 2;     c.gridy = 0;
    c.weightx = 0.0;
    gb.setConstraints(spacer, c);
    _nameLocTab.add(spacer);

    c.gridx = 3;     c.gridy = 0;
    c.weightx = 0.0;
    gb.setConstraints(typeLabel, c);
    _nameLocTab.add(typeLabel);

    c.gridx = 4;     c.gridy = 0;
    c.weightx = 1.0;
    gb.setConstraints(_type, c);
    _nameLocTab.add(_type);

    c.gridx = 3;     c.gridy = 1;
    c.gridwidth = 2; c.gridheight = 5;
    gb.setConstraints(_typeDetails, c);
    _nameLocTab.add(_typeDetails);

    JPanel searchPanel = new JPanel();
    searchPanel.setLayout(new GridLayout(1, 2, 5, 5));
    searchPanel.add(_clearTabs);
    searchPanel.add(_search);
    searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    c.gridx = 0;     c.gridy = 4;
    c.weightx = 0.0; c.weighty = 0.0;
    c.gridwidth = 2; c.gridheight = 1;
    gb.setConstraints(searchPanel, c);
    _nameLocTab.add(searchPanel);
  }

  public void initHelpTab() {
    _help.setLayout(new BorderLayout());
    JTextArea helpText = new JTextArea();
    String s;
    s =
      "Please follow these steps to find model elements:\n\n"+
      "1. Enter search information in the tabs at the top of this window.\n\n"+
      "2. Press the \"Search\" button.  This will produce a new tab.\n\n"+
      "3. The top half of each result tab lists each results.\n"+
      "   + Single clicking on a result shows more information about it,\n"+
      "     including a list of related objects.\n"+
      "   + Double clicking on a result jumps to the selected diagram.\n\n"+
      "You can \"tear-off\" a results tab by double clicking on the tab name.\n"+
      "If you accumulate too many tabs, press \"Clear Tabs\" to remove "+
      "them all.";
    
    helpText.setText(s);
    helpText.setEditable(false);
    _help.add(new JScrollPane(helpText), BorderLayout.CENTER);
  }

  public void setVisible(boolean b) {
    ProjectBrowser pb = ProjectBrowser.TheInstance;
    setLocation(pb.getBounds().x + 100, pb.getBounds().x + 100);
    super.setVisible(b);
  }

   public void initTagValsTab() {
     //  _tag         = new JTextField();
     //  _val         = new JTextField();
   }

  public void initModifiedTab() { }
  public void initConstraintsTab() { }


  public void initTypes() {
    _type.addItem(PredicateType.create());

    _type.addItem(PredicateType.create(MClass.class, MInterface.class));

    _type.addItem(PredicateType.create(MActor.class));
    _type.addItem(PredicateType.create(MAssociation.class));
    _type.addItem(PredicateType.create(MAttribute.class));
    _type.addItem(PredicateType.create(MClassifier.class));
    _type.addItem(PredicateType.create(MCompositeState.class));
    _type.addItem(PredicateType.create(MDependency.class));
    _type.addItem(PredicateType.create(MGeneralization.class));
    _type.addItem(PredicateType.create(MInstance.class));
    _type.addItem(PredicateType.create(MInterface.class));
    _type.addItem(PredicateType.create(MLink.class));
    _type.addItem(PredicateType.create(MClass.class));
    _type.addItem(PredicateType.create(MPackage.class));
    _type.addItem(PredicateType.create(MOperation.class));
    _type.addItem(PredicateType.create(MPseudostate.class));
	//    _type.addItem(PredicateType.create(Realization.class));
    _type.addItem(PredicateType.create(MState.class));
    _type.addItem(PredicateType.create(MStateVertex.class));
    _type.addItem(PredicateType.create(MTransition.class));
    _type.addItem(PredicateType.create(MUseCase.class));

  }

  ////////////////////////////////////////////////////////////////
  // event handlers
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == _search) doSearch();
    if (e.getSource() == _clearTabs) doClearTabs();
//     if (e.getSource() == _spawn) doSpawn();
//     if (e.getSource() == _go) doGo();
//     if (e.getSource() == _close) doClose();
  }

  ////////////////////////////////////////////////////////////////
  // actions

  public void doSearch() {
    _numFinds++;
    String eName = "";
    if (_elementName.getSelectedItem() != null) {
      eName += _elementName.getSelectedItem();
      _elementName.removeItem(eName);
      _elementName.insertItemAt(eName, 0);
      _elementName.setSelectedItem(eName);
    }
    String dName = "";
    if (_diagramName.getSelectedItem() != null) {
      dName += _diagramName.getSelectedItem();
      _diagramName.removeItem(dName);
      _diagramName.insertItemAt(dName, 0);
      _diagramName.setSelectedItem(dName);
    }
    String name = eName;
    if (dName.length() > 0) name += " in " + dName;
    String typeName = _type.getSelectedItem().toString();
    if (!typeName.equals("Any Type")) name += " " + typeName;
    if (name.length() == 0)
      name = "Search"+(nextResultNum++);
    if (name.length() > 15)
      name = name.substring(0, 12) + "...";

    String pName = "";

    Predicate eNamePred = PredicateStringMatch.create(eName);
    Predicate pNamePred = PredicateStringMatch.create(pName);
    Predicate dNamePred = PredicateStringMatch.create(dName);
    Predicate typePred = (Predicate) _type.getSelectedItem();
    PredicateFind pred =
      new PredicateFind(eNamePred, pNamePred, dNamePred, typePred);

    ChildGenFind gen = ChildGenFind.SINGLETON;
    ProjectBrowser pb = ProjectBrowser.TheInstance;
    Object root = pb.getProject();

    TabResults newResults = new TabResults();
    newResults.setTitle(name);
    newResults.setPredicate(pred);
    newResults.setRoot(root);
    newResults.setGenerator(gen);
    _resultTabs.addElement(newResults);
    _results.addTab(name, newResults);
    _clearTabs.setEnabled(true);
    _results.setSelectedComponent(newResults);
    _location.addItem("In Tab: " + name);
    invalidate();
    _results.invalidate();
    validate();
    newResults.run();
    newResults.requestFocus();
  }

  public void doClearTabs() {
    int numTabs = _resultTabs.size();
    for (int i = 0; i < numTabs; i++)
      _results.remove((Component)_resultTabs.elementAt(i));
    _resultTabs.removeAllElements();
    _clearTabs.setEnabled(false);
  }

//   public void doSpawn() { }

//   public void doGo() { }

//   public void doClose() { }

  ////////////////////////////////////////////////////////////////
  // MouseListener implementation

  public void mousePressed(MouseEvent me) { }
  public void mouseReleased(MouseEvent me) { }
  public void mouseEntered(MouseEvent me) { }
  public void mouseExited(MouseEvent me) { }
  public void mouseClicked(MouseEvent me) {
    int tab = _results.getSelectedIndex();
    if (tab != -1) {
      Rectangle tabBounds = _results.getBoundsAt(tab);
      if (!tabBounds.contains(me.getX(), me.getY())) return;
      if (tab >= 1 && me.getClickCount() >= 2)
	myDoubleClick(tab-1); //help tab is 0
    }
  }

  public void myDoubleClick(int tab) {
    JPanel t = (JPanel) _resultTabs.elementAt(tab);
    if (t instanceof TabSpawnable) {
      ((TabSpawnable)t).spawn();
      _resultTabs.removeElementAt(tab);
      // try {
	_location.removeItem("In Tab:" + ((TabSpawnable)t).getTitle());
	// needs-more-work: two tabs with the same name?
      // }
      // catch (IllegalArgumentException ex) {
	// System.out.println("problem removing tab in FindDialog.java");
      // }
    }
  }

  public static void main(String args[]) {
    FindDialog.SINGLETON.show();
  }
} /* end class FindDialog */
