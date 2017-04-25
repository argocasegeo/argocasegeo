// Decompiled by DJ v2.9.9.61 Copyright 2000 Atanas Neshkov  Date: 5/1/2004 17:03:36
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PGMLParser.java

package org.argouml.xml.pgml;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.FigInterface;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.presentation.FigNode;
import org.xml.sax.AttributeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PGMLParser extends org.tigris.gef.xml.pgml.PGMLParser
{

    protected PGMLParser()
    {
        _translateUciToOrg = new HashMap();
        _previousNode = null;
        _translateUciToOrg.put("uci.uml.visual.UMLClassDiagram", "org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLUseCaseDiagram", "org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLActivityDiagram", "org.argouml.uml.diagram.activity.ui.UMLActivityDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLCollaborationDiagram", "org.argouml.uml.diagram.collaboration.ui.UMLCollaborationDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLDeploymentDiagram", "org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLStateDiagram", "org.argouml.uml.diagram.state.ui.UMLStateDiagram");
        _translateUciToOrg.put("uci.uml.visual.UMLSequenceDiagram", "org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram");
        _translateUciToOrg.put("uci.uml.visual.FigAssociation", "org.argouml.uml.diagram.ui.FigAssociation");
        _translateUciToOrg.put("uci.uml.visual.FigRealization", "org.argouml.uml.diagram.ui.FigRealization");
        _translateUciToOrg.put("uci.uml.visual.FigGeneralization", "org.argouml.uml.diagram.ui.FigGeneralization");
        _translateUciToOrg.put("uci.uml.visual.FigCompartment", "org.argouml.uml.diagram.ui.FigCompartment");
        _translateUciToOrg.put("uci.uml.visual.FigDependency", "org.argouml.uml.diagram.ui.FigDependency");
        _translateUciToOrg.put("uci.uml.visual.FigEdgeModelElement", "org.argouml.uml.diagram.ui.FigEdgeModelElement");
        _translateUciToOrg.put("uci.uml.visual.FigMessage", "org.argouml.uml.diagram.ui.FigMessage");
        _translateUciToOrg.put("uci.uml.visual.FigNodeModelElement", "org.argouml.uml.diagram.ui.FigNodeModelElement");
        _translateUciToOrg.put("uci.uml.visual.FigNodeWithCompartments", "org.argouml.uml.diagram.ui.FigNodeWithCompartments");
        _translateUciToOrg.put("uci.uml.visual.FigNote", "org.argouml.uml.diagram.ui.FigNote");
        _translateUciToOrg.put("uci.uml.visual.FigTrace", "org.argouml.uml.diagram.ui.FigTrace");
        _translateUciToOrg.put("uci.uml.visual.FigClass", "org.argouml.uml.diagram.static_structure.ui.FigClass");
        _translateUciToOrg.put("uci.uml.visual.FigInterface", "org.argouml.uml.diagram.static_structure.ui.FigInterface");
        _translateUciToOrg.put("uci.uml.visual.FigInstance", "org.argouml.uml.diagram.static_structure.ui.FigInstance");
        _translateUciToOrg.put("uci.uml.visual.FigLink", "org.argouml.uml.diagram.static_structure.ui.FigLink");
        _translateUciToOrg.put("uci.uml.visual.FigPackage", "org.argouml.uml.diagram.static_structure.ui.FigPackage");
        _translateUciToOrg.put("uci.uml.visual.FigActionState", "org.argouml.uml.diagram.activity.ui.FigActionState");
        _translateUciToOrg.put("uci.uml.visual.FigAssociationRole", "org.argouml.uml.diagram.collaboration.ui.FigAssociationRole");
        _translateUciToOrg.put("uci.uml.visual.FigClassifierRole", "org.argouml.uml.diagram.collaboration.ui.FigClassifierRole");
        _translateUciToOrg.put("uci.uml.visual.FigComponent", "org.argouml.uml.diagram.deployment.ui.FigComponent");
        _translateUciToOrg.put("uci.uml.visual.FigComponentInstance", "org.argouml.uml.diagram.deployment.ui.FigComponentInstance");
        _translateUciToOrg.put("uci.uml.visual.FigMNode", "org.argouml.uml.diagram.deployment.ui.FigMNode");
        _translateUciToOrg.put("uci.uml.visual.FigMNodeInstance", "org.argouml.uml.diagram.deployment.ui.FigMNodeInstance");
        _translateUciToOrg.put("uci.uml.visual.FigObject", "org.argouml.uml.diagram.deployment.ui.FigObject");
        _translateUciToOrg.put("uci.uml.visual.FigBranchState", "org.argouml.uml.diagram.state.ui.FigBranchState");
        _translateUciToOrg.put("uci.uml.visual.FigCompositeState", "org.argouml.uml.diagram.state.ui.FigCompositeState");
        _translateUciToOrg.put("uci.uml.visual.FigDeepHistoryState", "org.argouml.uml.diagram.state.ui.FigDeepHistoryState");
        _translateUciToOrg.put("uci.uml.visual.FigFinalState", "org.argouml.uml.diagram.state.ui.FigFinalState");
        _translateUciToOrg.put("uci.uml.visual.FigForkState", "org.argouml.uml.diagram.state.ui.FigForkState");
        _translateUciToOrg.put("uci.uml.visual.FigHistoryState", "org.argouml.uml.diagram.state.ui.FigHistoryState");
        _translateUciToOrg.put("uci.uml.visual.FigInitialState", "org.argouml.uml.diagram.state.ui.FigInitialState");
        _translateUciToOrg.put("uci.uml.visual.FigJoinState", "org.argouml.uml.diagram.state.ui.FigJoinState");
        _translateUciToOrg.put("uci.uml.visual.FigShallowHistoryState", "org.argouml.uml.diagram.state.ui.FigShallowHistoryState");
        _translateUciToOrg.put("uci.uml.visual.FigState", "org.argouml.uml.diagram.state.ui.FigState");
        _translateUciToOrg.put("uci.uml.visual.FigActionState", "org.argouml.uml.diagram.activity.ui.FigActionState");
        _translateUciToOrg.put("uci.uml.visual.FigStateVertex", "org.argouml.uml.diagram.state.ui.FigStateVertex");
        _translateUciToOrg.put("uci.uml.visual.FigTransition", "org.argouml.uml.diagram.state.ui.FigTransition");
        _translateUciToOrg.put("uci.uml.visual.FigActor", "org.argouml.uml.diagram.use_case.ui.FigActor");
        _translateUciToOrg.put("uci.uml.visual.FigUseCase", "org.argouml.uml.diagram.use_case.ui.FigUseCase");
        _translateUciToOrg.put("uci.uml.visual.FigSeqLink", "org.argouml.uml.diagram.sequence.ui.FigSeqLink");
        _translateUciToOrg.put("uci.uml.visual.FigSeqObject", "org.argouml.uml.diagram.sequence.ui.FigSeqObject");
        _translateUciToOrg.put("uci.uml.visual.FigSeqStimulus", "org.argouml.uml.diagram.sequence.ui.FigSeqStimulus");
    }

    protected String translateClassName(String s)
    {
        if(s.startsWith("org."))
            return s;
        if(s.startsWith("uci.gef."))
        {
            String s1 = s.substring(s.lastIndexOf(".") + 1);
            return "org.tigris.gef.presentation." + s1;
        } else
        {
            String s2 = (String)_translateUciToOrg.get(s);
            return s2;
        }
    }

    protected String[] getEntityPaths()
    {
        return _entityPaths;
    }

    public void startElement(String s, AttributeList attributelist)
    {
        if(_elementState == 4 && s.equals("group") && _currentNode != null && attributelist != null && ((_currentNode instanceof FigClass) || (_currentNode instanceof FigInterface)))
        {
            String s1 = attributelist.getValue("description").trim();
            if(s1.endsWith("[0, 0, 0, 0]") || s1.endsWith("[0,0,0,0]"))
            {
                ((FigNodeModelElement)_currentNode).enableSizeChecking(false);
                if(_currentNode != _previousNode)
                {
                    if(_currentNode instanceof FigClass)
                        ((FigClass)_currentNode).setAttributeVisible(false);
                    else
                        ((FigInterface)_currentNode).setOperationVisible(false);
                } else
                {
                    ((FigClass)_currentNode).setOperationVisible(false);
                }
                ((FigNodeModelElement)_currentNode).enableSizeChecking(true);
            }
            _previousNode = _currentNode;
        }
        if(_elementState == 0 && s.equals("group") && _previousNode != null && _nestedGroups > 0)
        {
            String s2 = attributelist.getValue("description").trim();
            if(s2.endsWith("[0, 0, 0, 0]") || s2.endsWith("[0,0,0,0]"))
            {
                ((FigNodeModelElement)_previousNode).enableSizeChecking(false);
                if(_previousNode instanceof FigClass)
                    ((FigClass)_previousNode).setOperationVisible(false);
                ((FigNodeModelElement)_previousNode).enableSizeChecking(true);
            }
        }
        String s3 = attributelist.getValue("description");
        super.startElement(s, attributelist);
    }

    public synchronized Diagram readDiagram(InputStream inputstream, boolean flag)
    {
        try
        {
            System.out.println("=======================================");
            System.out.println("== READING DIAGRAM");
            SAXParserFactory saxparserfactory = SAXParserFactory.newInstance();
            saxparserfactory.setNamespaceAware(false);
            saxparserfactory.setValidating(false);
            initDiagram("org.tigris.gef.base.Diagram");
            _figRegistry = new HashMap();
            SAXParser saxparser = saxparserfactory.newSAXParser();
            InputSource inputsource = new InputSource(inputstream);
            inputsource.setSystemId(systemId);
            inputsource.setEncoding("UTF-8");
            saxparser.parse(inputsource, this);
            if(flag)
                inputstream.close();
            return _diagram;
        }
        catch(SAXException saxexception)
        {
            System.err.println("Exception in readDiagram");
            Exception exception = saxexception.getException();
            if(exception == null)
                saxexception.printStackTrace();
            else
                exception.printStackTrace();
        }
        catch(Exception exception1)
        {
            System.err.println("Exception in readDiagram");
            exception1.printStackTrace();
        }
        return null;
    }

    public static PGMLParser SINGLETON = new PGMLParser();
    protected HashMap _translateUciToOrg;
    private String _entityPaths[] = {
        "/org/argouml/xml/dtd/", "/org/tigris/gef/xml/dtd/"
    };
    protected FigNode _previousNode;

}