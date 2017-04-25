package org.argouml.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.argouml.application.api.Argo;


public class HelpContentsBox extends JDialog {
////////////////////////////////////////////////////////////////
	  // instance variables

	  JTabbedPane _tabs = new JTabbedPane();

	
	  ////////////////////////////////////////////////////////////////
	  // constructor
	  public HelpContentsBox() {
	    this((Frame)null,false);
	  }

	  public HelpContentsBox(Frame owner) {
	    this(owner,false);
	  }

	  public HelpContentsBox(Frame owner, boolean modal) {
	    super(owner,modal);
	    this.setTitle("About ArgoCASEGEO");
	
	    // tamanho anterior 512x512 (ficava mto pequena a tela)
	    // int imgWidth = 512, imgHeight = 512;
	    int imgWidth = 639, imgHeight = 350;

	    
	    Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(scrSize.width/2 - imgWidth/2,
			       scrSize.height/2 - imgHeight/2);
	    getContentPane().setLayout(new BorderLayout(0, 0));

	    
	    Font ctrlFont = MetalLookAndFeel.getControlTextFont();
/*
	    StringBuffer versionBuf = new StringBuffer();
	    versionBuf.append("\n--- Generated version information: ---\n");
	    versionBuf.append(org.argouml.util.Tools.getVersionInfo());
	    versionBuf.append(
			     "\n"+
			     "Intended for use with:\n"+
			     "  JDK 1.2 or higher plus\n"+
			     "    GEF Graph Editing Framework (gef.tigris.org)\n"+
			     "      including GIF generation code from www.acme.com\n"+
			     "    A JAXP 1.0.1 compatible parser\n" +
	                     "       [Xerces-J 1.2.2 or later recommended, (xml.apache.org), it's just great!]\n"+
			     "    Novosoft's NSUML 0.4.19 or higher (nsuml.sourceforge.net)\n"+
			     "    Frank Finger's (TU-Dresden) OCL-Compiler (dresden-ocl.sourceforge.net)\n"+
			     "    ANTLR (www.antlr.org) version 2.7\n"+
			     "\n");

		versionBuf.append("\n");
		versionBuf.append("The ArgoUML developers would like to thank all those broad-minded people\n");
		versionBuf.append("who spend their valuable time in contributing to the projects ArgoUML\n");
		versionBuf.append("depends on! We wouldn't be here without your work!\n");
		versionBuf.append("\n");*/

	    InputStreamReader isr = null;

	 //   _tabs.addTab("Splash", _splashPanel);

	/*    try {
	        JTextArea a = new JTextArea();
	        a.setEditable(false);
	        a.read(new StringReader(versionBuf.toString()), null);
	        _tabs.addTab("Version", new JScrollPane(a));
	    }
	    catch (Exception e) {
	        Argo.log.error("Unable to read version information", e);
	    }*/

	    try {	    	
	        JTextArea apc = new JTextArea();        
	        apc.read(new InputStreamReader(
	            getClass().getResourceAsStream (Argo.RESOURCEDIR + 
	            		                        "argocasegeo.about")), null);
	        JTextArea nap = new JTextArea();        
	        nap.read(new InputStreamReader(
	            getClass().getResourceAsStream (Argo.RESOURCEDIR + 
	            		                        "newanalysispattern.about")), null);
	        
	        JTextArea orap = new JTextArea();        
	        orap.read(new InputStreamReader(
	            getClass().getResourceAsStream (Argo.RESOURCEDIR + 
	            		                        "openreuseanalysispattern.about")), null);
	        
	        JTextArea eap = new JTextArea();        
	        eap.read(new InputStreamReader(
	            getClass().getResourceAsStream (Argo.RESOURCEDIR + 
	            		                        "editanalysispattern.about")), null);
	        
	        //apc.setEditable(false);
	        nap.setEditable(false);
	        orap.setEditable(false);
	        eap.setEditable(false);
	        //_tabs.addTab("Analysis Patterns Catalog", new JScrollPane(apc));
	        _tabs.addTab("ANALYSIS PATTERNS CATALOG", new JScrollPane(apc));
	        _tabs.addTab("New Analysis Pattern", new JScrollPane(nap));
	        _tabs.addTab("Open/Reuse Analysis Pattern", new JScrollPane(orap));
	        _tabs.addTab("Edit Analysis Pattern", new JScrollPane(eap));
	    }
	    
	    
	    catch (Exception e) {
	        Argo.log.error("Unable to read.", e);
	    }

	    getContentPane().setLayout(new BorderLayout(0, 0));
	    getContentPane().add(_tabs, BorderLayout.CENTER);

	    // TODO: 10 and 120 were found by trial and error.  Calculate them.
	    setSize(imgWidth + 10, imgHeight + 120);
	    //pack();
	  }

	} /* end class AboutBox */
