package org.argouml.uml.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.argouml.application.api.Argo;
import org.argouml.kernel.Project;
import org.argouml.terralib.XmlReader;
import org.argouml.ui.ProjectBrowser;
import org.argouml.util.FileFilters;
import org.argouml.util.osdep.OsUtil;

import org.argouml.catalog.*;
import org.w3c.dom.*; 
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ActionCreatePattern extends ActionSaveXMI {

	public static ActionCreatePattern SINGLETON = new ActionCreatePattern();

	
    public static final String separator = "/"; //System.getProperty("file.separator");

    ////////////////////////////////////////////////////////////////
    // constructors

    protected ActionCreatePattern() {
	super("New Analysis Pattern", NO_ICON);
    }

    public static String getChildTagValue (Element elem, String tagName) {
        NodeList children = elem.getElementsByTagName( tagName );
        if( children == null ) return null;
        Element child = (Element) children.item(0);
        if( child == null ) return null;
        return child.getFirstChild().getNodeValue();
    }
    
    
    ////////////////////////////////////////////////////////////////
    // main methods

    public void actionPerformed(ActionEvent e){
        //dbOp = JOptionPane.showInputDialog(this, "Enter database option")
   	
    	CreatePatternUI app = null;
	
    	/*
    	 * Parte de codigo um pouco gambiarrado, sujeito a futuras alteracoes,
    	 * mas que por enquanto faz o programa funcionar bem...
    	 */
    	File temp = null;
        try {
            temp = File.createTempFile( "temp", ".xmi" );
        } catch( IOException exc ) {}
        
        trySave(true, temp);
        String s = temp.toString();
        
        String xmlPathname = ("file:///" + s.replace('/', '\\'));
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		}
		
        Document doc = null;
        
        try {
			doc = db.parse( xmlPathname );
		} catch (SAXException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		// pega a raiz do documento XML
	    Element elem = doc.getDocumentElement();
	    
	    // pega todos os Modelos do XML, os quais representarão bancos de dados diferentes
	    NodeList nl = elem.getElementsByTagName( "Model_Management.Model" );
	    
	    // Assumindo apenas 1 banco de dados
	    Element model = (Element)nl.item(0); // colocar índice genérico********
	    
	    NodeList packages = model.getElementsByTagName( "Model_Management.Package" );

	    
	    /*
	     * Se existir algum pacote no documento, ele entra no try e executa
	     * o new CreatePatternUI()... senao, emite a mensagem de erro para
	     * o usuario e sai da funcao, sem executar os dois ultimos comandos
	     */
		if (packages.getLength() != 0) {
	    	try {
	    		app = new CreatePatternUI();
	    	} catch (Exception e1) {
	    		e1.printStackTrace(); 
	    		System.out.println("lancou excecao de Exception");
	    	} 
	    }
	    else {
	        JOptionPane.showMessageDialog(app, 
    		    "Error!! To be able to use this menu, it is \n" + 
    			"MANDATORY to have at least one elemen in the \n" +
    			"model (a package, a Geographic Object, etc.), \n " +
    			"i.e., the model MUST NOT BE EMPTY! \n\n", 
    			"ArgoCASEGEO", JOptionPane.ERROR_MESSAGE);
    	    return;
	    }
	    
	    /*
	     * Esses dois comandos rodam apenas se a excecao nao for lançada,
	     * ou seja, se existir ao menos um pacote dentro do documento
	     */
		app.generateModelImage();
	//	System.exit(1);
		
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

    // habilita ou nao o menu
    public boolean shouldBeEnabled() {
	    return true;
    }
} 
