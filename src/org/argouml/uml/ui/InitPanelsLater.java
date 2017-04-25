// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package org.argouml.uml.ui;

import java.io.PrintStream;
import java.util.*;
import org.apache.log4j.Category;
import org.argouml.application.api.Argo;
import org.argouml.swingext.Orientation;
import org.argouml.uml.diagram.state.ui.PropPanelUMLStateDiagram;
import org.argouml.uml.diagram.ui.PropPanelString;
import org.argouml.uml.ui.behavior.activity_graphs.PropPanelActionState;
import org.argouml.uml.ui.behavior.collaborations.PropPanelAssociationEndRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelAssociationRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelClassifierRole;
import org.argouml.uml.ui.behavior.collaborations.PropPanelCollaboration;
import org.argouml.uml.ui.behavior.collaborations.PropPanelMessage;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelCallAction;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelComponentInstance;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelInstance;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelLink;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelNodeInstance;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelObject;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelSignal;
import org.argouml.uml.ui.behavior.common_behavior.PropPanelStimulus;
import org.argouml.uml.ui.behavior.state_machines.PropPanelCallEvent;
import org.argouml.uml.ui.behavior.state_machines.PropPanelCompositeState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelFinalState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelGuard;
import org.argouml.uml.ui.behavior.state_machines.PropPanelPseudostate;
import org.argouml.uml.ui.behavior.state_machines.PropPanelSimpleState;
import org.argouml.uml.ui.behavior.state_machines.PropPanelTransition;
import org.argouml.uml.ui.behavior.use_cases.PropPanelActor;
import org.argouml.uml.ui.behavior.use_cases.PropPanelExtend;
import org.argouml.uml.ui.behavior.use_cases.PropPanelExtensionPoint;
import org.argouml.uml.ui.behavior.use_cases.PropPanelInclude;
import org.argouml.uml.ui.foundation.core.PropPanelAbstraction;
import org.argouml.uml.ui.foundation.core.PropPanelAssociation;
import org.argouml.uml.ui.foundation.core.PropPanelAssociationEnd;
import org.argouml.uml.ui.foundation.core.PropPanelAttribute;
import org.argouml.uml.ui.foundation.core.PropPanelComponent;
import org.argouml.uml.ui.foundation.core.PropPanelDataType;
import org.argouml.uml.ui.foundation.core.PropPanelDependency;
import org.argouml.uml.ui.foundation.core.PropPanelGeneralization;
import org.argouml.uml.ui.foundation.core.PropPanelInterface;
import org.argouml.uml.ui.foundation.core.PropPanelNode;
import org.argouml.uml.ui.foundation.core.PropPanelOperation;
import org.argouml.uml.ui.foundation.core.PropPanelParameter;
import org.argouml.uml.ui.foundation.extension_mechanisms.PropPanelStereotype;

// Referenced classes of package org.argouml.uml.ui:
//            UMLUserInterfaceContainer, TabProps

class InitPanelsLater2
    implements Runnable
{

    public InitPanelsLater2(Hashtable hashtable, TabProps tabprops, Orientation orientation)
    {
        _panels = null;
        _panels = hashtable;
        _tabProps = tabprops;
        _orientation = orientation;
    }

    public void run()
    {
        try
        {
            _panels.put(ru.novosoft.uml.behavior.activity_graphs.MActionStateImpl.class, new PropPanelActionState());
            _panels.put(ru.novosoft.uml.behavior.use_cases.MActorImpl.class, new PropPanelActor());
            _panels.put(ru.novosoft.uml.foundation.core.MAssociationImpl.class, new PropPanelAssociation());
            _panels.put(ru.novosoft.uml.behavior.collaborations.MAssociationRoleImpl.class, new PropPanelAssociationRole());
            _panels.put(ru.novosoft.uml.foundation.core.MAttributeImpl.class, new PropPanelAttribute());
            _panels.put(ru.novosoft.uml.behavior.collaborations.MCollaborationImpl.class, new PropPanelCollaboration());
            _panels.put(ru.novosoft.uml.behavior.collaborations.MClassifierRoleImpl.class, new PropPanelClassifierRole());
            _panels.put(ru.novosoft.uml.foundation.core.MDependencyImpl.class, new PropPanelDependency());
            _panels.put(ru.novosoft.uml.behavior.use_cases.MExtendImpl.class, new PropPanelExtend());
            _panels.put(ru.novosoft.uml.behavior.use_cases.MExtensionPointImpl.class, new PropPanelExtensionPoint());
            _panels.put(ru.novosoft.uml.foundation.core.MGeneralizationImpl.class, new PropPanelGeneralization());
            _panels.put(ru.novosoft.uml.behavior.use_cases.MIncludeImpl.class, new PropPanelInclude());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MInstanceImpl.class, new PropPanelInstance());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MComponentInstanceImpl.class, new PropPanelComponentInstance());
            _panels.put(ru.novosoft.uml.foundation.core.MComponentImpl.class, new PropPanelComponent());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MNodeInstanceImpl.class, new PropPanelNodeInstance());
            _panels.put(ru.novosoft.uml.foundation.core.MNodeImpl.class, new PropPanelNode());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MObjectImpl.class, new PropPanelObject());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MInstanceImpl.class, new PropPanelInstance());
            _panels.put(ru.novosoft.uml.foundation.core.MInterfaceImpl.class, new PropPanelInterface());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MLinkImpl.class, new PropPanelLink());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MStimulusImpl.class, new PropPanelStimulus());
            _panels.put(ru.novosoft.uml.behavior.collaborations.MMessageImpl.class, new PropPanelMessage());
            _panels.put(ru.novosoft.uml.foundation.core.MOperationImpl.class, new PropPanelOperation());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MPseudostateImpl.class, new PropPanelPseudostate());
            _panels.put(org.argouml.uml.diagram.state.ui.UMLStateDiagram.class, new PropPanelUMLStateDiagram());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MStateImpl.class, new PropPanelSimpleState());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MCompositeStateImpl.class, new PropPanelCompositeState());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MFinalStateImpl.class, new PropPanelFinalState());
            _panels.put(java.lang.String.class, new PropPanelString());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MTransitionImpl.class, new PropPanelTransition());
            _panels.put(ru.novosoft.uml.foundation.core.MAssociationEndImpl.class, new PropPanelAssociationEnd());
            _panels.put(ru.novosoft.uml.behavior.collaborations.MAssociationEndRoleImpl.class, new PropPanelAssociationEndRole());
            _panels.put(ru.novosoft.uml.foundation.core.MParameterImpl.class, new PropPanelParameter());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MSignalImpl.class, new PropPanelSignal());
            _panels.put(ru.novosoft.uml.foundation.extension_mechanisms.MStereotypeImpl.class, new PropPanelStereotype());
            _panels.put(ru.novosoft.uml.foundation.core.MDataTypeImpl.class, new PropPanelDataType());
            _panels.put(ru.novosoft.uml.foundation.core.MAbstractionImpl.class, new PropPanelAbstraction());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MGuardImpl.class, new PropPanelGuard());
            _panels.put(ru.novosoft.uml.behavior.state_machines.MCallEventImpl.class, new PropPanelCallEvent());
            _panels.put(ru.novosoft.uml.behavior.common_behavior.MCallActionImpl.class, new PropPanelCallAction());
        }
        catch(Exception exception)
        {
            System.out.println(exception.toString() + " in InitPanelsLater.run()");
            exception.printStackTrace();
        }
        for(Iterator iterator = _panels.values().iterator(); iterator.hasNext();)
        {
            Object obj = iterator.next();
            if(obj instanceof UMLUserInterfaceContainer)
                ((UMLUserInterfaceContainer)obj).addNavigationListener(_tabProps);
        }

        Argo.log.info("done preloading Property Panels");
    }

    private Hashtable _panels;
    private TabProps _tabProps;
    private Orientation _orientation;
}
