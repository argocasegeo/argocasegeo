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

public class ActionOpenPattern extends ActionSaveXMI {

	public static ActionOpenPattern SINGLETON = new ActionOpenPattern();

    public static final String separator = "/"; //System.getProperty("file.separator");

    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionOpenPattern() {
	super("Open/Reuse Analysis Pattern", NO_ICON);
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent e) {
    	 //dbOp = JOptionPane.showInputDialog(this, "Enter database option")
      	OpenPatternUI app = new OpenPatternUI();
      	//app.generateModelImage();
      //	System.exit(1);
    	
    	
    	//trySave(false);
        //System.exit(1);
    }

    public boolean trySave(boolean overwrite) {
	//File f = getNewFile();
	//if (f == null)
	  //return false;
        File temp = null;
        try {
            temp = temp.createTempFile( "temp", ".xmi" );
        } catch( IOException e ) {}
	boolean success = trySave(true, temp);
        
             
        // Inserir o codigo para gerar o arquivo Shape 
        // (chamadas de funcoes, eh claro)
	if (success) {
	    ProjectBrowser.TheInstance.updateTitle();
	}
 
        
          // geraçao do modelo Terralib *********************************
      String s = temp.toString();
   
      XmlReader app = new XmlReader ("file:///" + s.replace('/', '\\'));     
      try
      {
      app.lerXMI();
   
      }
      catch( Exception e )
      {
          System.err.println("It wasn´t possible to read the xmi file!!!!!!");
          System.exit(1);
      } 
       //****************************************************************   
    
  
      
        temp.delete();      // apaga o arquivo .xmi temporario
    
    System.out.println("foi um sucesso!");
	return success;
    }

    protected File getNewFile() {
	ProjectBrowser pb = ProjectBrowser.TheInstance;
	Project p = pb.getProject();

        JFileChooser chooser = null;
	URL url = p.getURL();
        if ((url != null) && (url.getFile().length()>0)) {
	    chooser  = OsUtil.getFileChooser (url.getFile());
        }
        if (chooser == null) {
	    chooser  = OsUtil.getFileChooser ();
        }

	if (url != null) {
	    chooser.setSelectedFile(new File(url.getFile()));
	}

	String sChooserTitle =
	    Argo.localize("Actions",
			       "text.save_as_project.chooser_title");
	chooser.setDialogTitle(sChooserTitle + p.getName());
	chooser.setFileFilter(FileFilters.ShapefileFilter);

	int retval = chooser.showSaveDialog(pb);
	if (retval == JFileChooser.APPROVE_OPTION) {
	    File file = chooser.getSelectedFile();
	    if (file != null) {
		String name = file.getName();
		if (! name.endsWith(Project.SHAPE_FILE_EXT)) {
		    file = new File(file.getParent(),
				    name + Project.SHAPE_FILE_EXT);
		}
	    }
	    return file;
	} else {
	    return null;
	}
    }

    public boolean shouldBeEnabled() {
	return true;
    }
} 

