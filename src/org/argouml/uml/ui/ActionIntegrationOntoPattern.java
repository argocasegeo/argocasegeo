package org.argouml.uml.ui;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFileChooser;

import org.argouml.application.api.Argo;
import org.argouml.kernel.Project;
import org.argouml.terralib.XmlReader;
import org.argouml.ui.ProjectBrowser;
import org.argouml.util.FileFilters;
import org.argouml.util.osdep.OsUtil;

import org.argouml.catalog.*;
import org.argouml.queryOnto.*;
import org.argouml.integrationPAOnto.*;

public class ActionIntegrationOntoPattern extends UMLAction {

	public static ActionIntegrationOntoPattern SINGLETON = new ActionIntegrationOntoPattern();

    public static final String separator = "/"; //System.getProperty("file.separator");

    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionIntegrationOntoPattern() {
	super("Integration OntoPattern", NO_ICON);
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent e) {
    	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Casamento().setVisible(true);
            }
        });
    }

    public boolean shouldBeEnabled() {
	return true;
    }
} 

