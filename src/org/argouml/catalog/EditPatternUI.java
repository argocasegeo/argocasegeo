package org.argouml.catalog;

import java.awt.*;

import javax.swing.*;

import org.argouml.ui.*;
import org.argouml.uml.ui.*;

import org.argouml.kernel.*;
import org.argouml.util.*;
import org.argouml.util.osdep.*;
import org.tigris.gef.base.*;
import org.tigris.gef.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.event.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class EditPatternUI extends JFrame {
	
	protected StatusBar _statusBar = new StatusBar();
	
	public static final String separator = "/";
	
	private Container c;
	
	private JLabel imageLabel;
	
	private JLabel nameLbl;
	private JTextField nameFld;
	
	private JLabel themeLbl;
	private JTextField themeFld;
	
	private JLabel problemLbl;
	private JTextArea problemArea;
	
	private JLabel contextLbl;
	private JTextArea contextArea;
	
	private JLabel forcesLbl;
	private JTextArea forcesArea;
	
	private JLabel participantsLbl;
	private JTextArea participantsArea;
	
	private JLabel relatedLbl;
	private JTextArea relatedArea;
	
	private JLabel exampleLbl;
	private JTextArea exampleArea;
	
	private JButton finishBtn;
	private JButton cancelBtn;
	//private JButton helpBtn;
	private JButton updateBtn;
	
	private String path;
	private File figDirFile;
	
	private Icon imageDiagram;
	
	private int counterFig;
	

	
	JPanel panel1;
	
	public EditPatternUI() {
		
		super("Edit Analysis Pattern");
		
		c = getContentPane();	
	    c.setLayout(null);
	  
	    File fileDirAux = new File("");
		path = fileDirAux.getAbsolutePath();
		figDirFile = new File(path + "\\temp.gif");
		 
		generateModelImage();
	
		System.out.println("veja o path: " + path);
		imageDiagram = new ImageIcon( path + "\\temp.gif");
		
		
		imageLabel = new JLabel(imageDiagram);
		imageLabel.setToolTipText("Class diagram displaying the proposed solution");
					
		panel1 = new JPanel();
		JLabel whatisLbl = new JLabel("EDIT AN ANALYSIS PATTERN");
		whatisLbl.setFont(new Font("Arial",Font.BOLD,11));
		c.add(whatisLbl);
		whatisLbl.setBounds(10, 10 , 250 , 20);
	
		panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"SOLUTION"), BorderFactory
				.createLineBorder(Color.black)));
		panel1.setBackground(new Color(253, 254, 235)); // LIGHT_GRAY É bom
		
	   // adiciona imagem ao painel1		
	    panel1.add(imageLabel);
	    	    
	    // adiciona os botoes ao painel1
	    finishBtn = new JButton("Finish");
	    finishBtn.setToolTipText("Finish the creation of the Analysis Pattern");
	    finishBtn.addActionListener(new ActionListener() {
	    	        	
			public void actionPerformed(ActionEvent e) {
				
				if (    nameFld.getText().equals("") ||
						problemArea.getText().equals("") ||
						contextArea.getText().equals("")||
						forcesArea.getText().equals("")
					) {
					
					JOptionPane.showMessageDialog(EditPatternUI.this, 
							"Error!! Please fill in all required fields.", "ArgoCASEGEO", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				String name = nameFld.getText();
			
				File diretorio = new File(".\\Analysis Pattern Catalog\\" + name);
				if  (! diretorio.exists() ) {
				  diretorio.mkdir();
				}
				
				
				String fileName = name + ".zargo";
				File fileDirAux = new File("");
				String path = fileDirAux.getAbsolutePath();
				File fileDir = new File(path + "\\Analysis Pattern Catalog\\" + 
						name + "\\" + fileName);
			    
				
				if (savePattern(true, fileDir)) { // estava trySave...
				
					JOptionPane.showMessageDialog(null, 
							"The Analysis Pattern \"" + nameFld.getText() +
							"\" was updated successfully.", "ArgoCASEGEO", 
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, 
							"An error has ocurred during the creation of the " +
							"Analysis Pattern!", "ArgoCASEGEO", 
							JOptionPane.ERROR_MESSAGE);

				}
				fileName = name + ".xml";
				fileDir = new File(path + "\\Analysis Pattern Catalog\\" + name
						+ "\\" + fileName);
				saveDocumentation(fileDir);
								
				
				File dir = new File(path + "\\Analysis Pattern Catalog\\" + name);
				saveImage(dir);
				
				
				System.out.println("este é o fileDir: " + figDirFile.getAbsolutePath());
				System.out.println("este é o fileDir: " + fileDir.getAbsolutePath());
				extractFile(path + "\\Analysis Pattern Catalog\\" + name + "\\" + name + ".zargo");
				
				
				
				EditPatternUI.this.dispose();

			}
		});

		c.add(finishBtn);
		finishBtn.setBounds(290, 620, 90, 25);
	    
	    
	    updateBtn = new JButton("Update Diagram");
	    updateBtn.setToolTipText("Edit your diagram an click here" +
	    		" to update."); 
	    
	    updateBtn.addActionListener(new ActionListener() {        	
			public void actionPerformed(ActionEvent e) {
				
				figDirFile.delete();
				counterFig++;
				
				File fileDirAux = new File("");
				path = fileDirAux.getAbsolutePath();
				figDirFile = new File(path + "\\" +
						counterFig + "temp.gif");
				
				generateModelImage();				
						
			    System.out.println(
			    		"veja isso: " + figDirFile.getPath());
			    
		
				ImageIcon ig = new ImageIcon((figDirFile.getPath()));
				
								
				imageDiagram = ig;
				imageLabel.setIcon(imageDiagram);

				imageLabel = new JLabel(imageDiagram);

				EditPatternUI.this.repaint();
				
				updateBtn.setEnabled(false);

			}
		});
	    c.add(updateBtn);
	    updateBtn.setBounds(310, 580, 150, 25);
	    
	  
	    
	    cancelBtn = new JButton("Cancel");
	    cancelBtn.addActionListener(new ActionListener() {        	
			public void actionPerformed(ActionEvent e) {
				
				figDirFile.delete();
				dispose();
				
			}
	    }
	    );
	    
	    
	    c.add(cancelBtn);
	    cancelBtn.setBounds(388, 620, 90, 25);
	    
	    /*
	    helpBtn = new JButton("Help");
	    helpBtn.setToolTipText("Help about how to create a new Analysis Pattern");
	    helpBtn.addActionListener(new ActionListener() {
       	
			public void actionPerformed(ActionEvent e) {
				
				HelpUI helpUI = new HelpUI(EditPatternUI.this);
				helpUI.setVisible(true);
				helpUI.setModal(true);
			}
	    });
	    
	    
	    c.add(helpBtn);
	    helpBtn.setBounds(434, 620, 60, 25); 
	    */
		
		JScrollPane scroller = new JScrollPane(panel1);
		scroller.setBounds(10, 60, 700, 500);
		
		JPanel panel2 = new JPanel();
				
		panel2.setLayout(null);
		panel2.setBounds(710, 10, 250, 680);
		
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"DOCUMENTATION"), BorderFactory
				.createLineBorder(Color.black)));	
		
		
		/*
		 * nome
		 */
		
		nameLbl = new JLabel("Name: ");
		nameLbl.setForeground(Color.RED);
		
		nameLbl.setToolTipText("Name of the Analysis Pattern.");
		nameFld = new JTextField(20);
			
		panel2.add(nameLbl);
		nameLbl.setBounds(17,28, 35,20);
		
		panel2.add(nameFld);
		nameFld.setBounds(53, 28, 180, 20);
		
		/*
		 * tema
		 */
		
		themeLbl = new JLabel("Theme: ");				
		themeLbl.setToolTipText("Theme of the Analysis Pattern.");
		themeFld = new JTextField(20);
		
		panel2.add(themeLbl);
		themeLbl.setBounds(17, 55, 60, 20);
		
		panel2.add(themeFld);
		themeFld.setBounds(53, 55, 181, 20);
	
		
		/* 
		 * problema
		 */
		
		problemLbl = new JLabel("Problem: ");
		problemLbl.setToolTipText("Brief declaration of the problem that needs \n" +
				"to be solved by this Analysis Pattern.");
		problemLbl.setForeground(Color.RED);
		panel2.add(problemLbl);
		problemLbl.setBounds(17, 75, 60, 20);
					
		Box b1 = Box.createHorizontalBox();
		panel2.add(b1);	
		problemArea = new JTextArea(20, 20);
		JScrollPane myScroller1 = new JScrollPane(problemArea);
		problemArea.setLineWrap(true);
		myScroller1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b1.add(new JScrollPane(myScroller1));
		b1.setBounds(17, 95, 218, 72);
		
		/* 
		 * contexto
		 */
		
		contextLbl = new JLabel("Context: ");
		contextLbl.setToolTipText("Scenery in which the problem was identified \n" +
				"and its proposed solution.");
		contextLbl.setForeground(Color.RED);
		panel2.add(contextLbl);
		contextLbl.setBounds(17, 172, 60, 20);
				
		Box b2 = Box.createHorizontalBox();
		panel2.add(b2);
		contextArea = new JTextArea(20, 20);
		JScrollPane myScroller2 = new JScrollPane(contextArea);
		contextArea.setLineWrap(true);
		myScroller2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b2.add(new JScrollPane(myScroller2));
		b2.setBounds(17, 192, 218, 72);
		
		/* 
		 * forças
		 */
		
		forcesLbl = new JLabel("Forces: ");
		forcesLbl.setToolTipText("Set of restrictions considered when solving the problem");
		forcesLbl.setForeground(Color.RED);
		panel2.add(forcesLbl);
		forcesLbl.setBounds(17, 272, 60, 20);
				
		Box b3 = Box.createHorizontalBox();
		panel2.add(b3);
		forcesArea = new JTextArea(20, 20);
		JScrollPane myScroller3 = new JScrollPane(forcesArea);
		forcesArea.setLineWrap(true);
		myScroller3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b3.add(new JScrollPane(myScroller3));
		b3.setBounds(17, 292, 218, 72);
		
		/* 
		 * participantes
		 */
		
		participantsLbl = new JLabel("Participants: ");
		participantsLbl.setToolTipText("Description of the elements comprising the Analisys Pattern.");
			panel2.add(participantsLbl);
			participantsLbl.setBounds(17, 372, 85, 20);
				
		Box b4 = Box.createHorizontalBox();
		panel2.add(b4);
		participantsArea = new JTextArea(20, 20);
		JScrollPane myScroller4 = new JScrollPane(participantsArea);
		participantsArea.setLineWrap(true);
		myScroller4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b4.add(new JScrollPane(myScroller4));
		b4.setBounds(17, 392, 218, 72);
		
		/* 
		 * padroes relacionados
		 */
		
		relatedLbl = new JLabel("Related Patterns: ");
		relatedLbl.setToolTipText("Information about Related Patterns.");
		panel2.add(relatedLbl);
		relatedLbl.setBounds(17, 475, 100, 20);
				
		Box b5 = Box.createHorizontalBox();
		panel2.add(b5);
		relatedArea = new JTextArea(20, 20);
		JScrollPane myScroller5 = new JScrollPane(relatedArea);
		relatedArea.setLineWrap(true);
		myScroller5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b5.add(new JScrollPane(myScroller5));
		b5.setBounds(17, 495, 218, 72);
		
		/* 
		 * Exemplo de uso
		 */
		
		exampleLbl = new JLabel("Example of Use: ");
		exampleLbl.setToolTipText("Example of how this Analysis Pattern can be used.");
		panel2.add(exampleLbl);
		exampleLbl.setBounds(17, 575, 100, 20);
		
		Box b6 = Box.createHorizontalBox();
		panel2.add(b6);
		exampleArea = new JTextArea(20, 20);
		JScrollPane myScroller6 = new JScrollPane(exampleArea);
		exampleArea.setLineWrap(true);
		myScroller6.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b6.add(new JScrollPane(myScroller6));
		b6.setBounds(17, 595, 218, 72);
					
		// adiciona os dois painéis ao JFrame
		
		c.add(scroller, BorderLayout.CENTER);
		c.add(panel2, BorderLayout.EAST);
						
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				figDirFile.delete();
				dispose();
			}
		});
		
		// preenche os campos de documentação
		fillFields();
				
		setResizable(false);
		setSize(1000, 725);
		//pack();
		setVisible(true);
	}
	
    protected void paintComponent(Graphics g) {
        
        if (imageDiagram != null) {
          int x = getWidth()/2 - imageDiagram.getIconWidth()/2;
          int y = getHeight()/2 - imageDiagram.getIconHeight()/2;

          if (y < 0) {
              y = 0;
          }

          if (x < 5) {
              x = 5;
          }
          imageDiagram.paintIcon(this, g, x, y);
      }
  } 
    

	public void fillFields() {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    Document doc = null;
	    
	    ProjectBrowser pb = ProjectBrowser.TheInstance;
		Project p = pb.getProject();
		String patternName = "";
		char[] c = p.getName().toCharArray();
		for (int i = 0; i < c.length; i++) {
		
			if (c[i] != '.') {
				patternName += c[i];
			} else {
				break;
			}
			
		}
		
		nameFld.setText(patternName);
	    
	
	    String xmlPathname = ("file:///" +	figDirFile.getParent() + "\\" +
	    		"Analysis Pattern Catalog" +
	    		"\\" + patternName + "\\" + 
	    		patternName + ".xml");
	    	    
	    try {
	    	
			doc = db.parse( xmlPathname );  
			 
			
			// pega a raiz do documento XML
			Element elem = doc.getDocumentElement();
			
   
			
			//NodeList nl = elem.getElementsByTagName( "documentation" );
			//System.out.println("Amo A sAmira: " + nl.getLength());
		    //Element documentation = (Element)nl.item(0); 
			
			
			String value = "";
			
			//obtem tema
			value = elem.getAttribute("theme");
			themeFld.setText(value);
			
			//obtem problema
			value = getChildTagValue(elem, "problem");
			problemArea.setText(value);
			
			// obtem contexto
			value = getChildTagValue(elem, "context");
			contextArea.setText(value);
			
			//obtem forças
			value = getChildTagValue(elem, "forces");
			forcesArea.setText(value);
			
			//obtem participantes
			value = getChildTagValue(elem, "participants");
			participantsArea.setText(value);
			
			//obtem padrões relacionados
			value = getChildTagValue(elem, "related_patterns");
			relatedArea.setText(value);
			
			//obtem exemplo
			value = getChildTagValue(elem, "example");
			exampleArea.setText(value);
			
		} catch (SAXException e) {
			System.out.println("exceção com arquivo xml!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		
		
		
	}
	
	  // este método lê e retorna o conteúdo (texto) de uma tag (elemento)
	  // filho da tag informada como parâmetro. A tag filho a ser pesquisada
	  // é a tag informada pelo nome (string)
	  public static String getChildTagValue( Element elem, String tagName ) throws Exception { // voltar para private depois
	    NodeList children = elem.getElementsByTagName( tagName );
	    if( children == null ) return null;
	    Element child = (Element) children.item(0);
	    if( child == null ) return null;
	    return child.getFirstChild().getNodeValue();
	  }
	
	
	
	public void generateModelImage() {
		boolean overwrite = true;

		Object target = ProjectBrowser.TheInstance.getActiveDiagram();
		if (target instanceof Diagram) {
			String defaultName = ((Diagram) target).getName();
			defaultName = Util.stripJunk(defaultName);

			ProjectBrowser pb = ProjectBrowser.TheInstance;
		
			Project p = pb.getProject();
		
					
							   
			File theFile = figDirFile;
			if (theFile != null) {
				String path = theFile.getParent();
				String name = theFile.getName();
				String extension = SuffixFilter.getExtension(theFile);

				if (extension == null
						|| !((extension.equals(FileFilters.PSFilter._suffix))
								|| (extension
										.equals(FileFilters.EPSFilter._suffix))
								|| (extension
										.equals(FileFilters.GIFFilter._suffix)) || (extension
								.equals(FileFilters.SVGFilter._suffix)))) {
				
					extension = ".gif";// FileFilters.getSuffix(filter);
					theFile = new File(theFile.getParentFile(), theFile
							.getName()
							+ "." + extension);
				}
				
				CmdSaveGraphics cmd = null;
				if (FileFilters.PSFilter._suffix.equals(extension))
					cmd = new CmdSavePS();
				else if (FileFilters.EPSFilter._suffix.equals(extension))
					cmd = new CmdSaveEPS();
				else if (FileFilters.GIFFilter._suffix.equals(extension))
					cmd = new CmdSaveGIF();
				else if (FileFilters.SVGFilter._suffix.equals(extension))
					cmd = new CmdSaveSVG();
				else {
					pb.showStatus("Unknown graphics file type withextension "
							+ extension);
					// return false;
				}

				if (!path.endsWith(separator))
					path += separator;
				pb.showStatus("Writing " + path + name + "...");
				if (theFile.exists() && !overwrite) {
					System.out.println("Are you sure you want to overwrite "
							+ name + "?");
					String t = "Overwrite " + path + name;
					int response = JOptionPane.showConfirmDialog(pb, t, t,
							JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.NO_OPTION)
						;// return false;
				}
				try {
					FileOutputStream fo = new FileOutputStream(theFile);
					cmd.setStream(fo);
					cmd.doIt();
					fo.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pb.showStatus("Wrote " + path + name);
				// return true;
			}

		}

	}
	
	/**
	 * Descompacta um arquivo .zargo (compactado)
	 * @param zipfilename Arquivo a ser descompactado
	 */
	public void extractFile(String zipfilename) {
		try {
			File f = new File(zipfilename);
			String dir = f.getParent();

			FileInputStream fis = new FileInputStream(zipfilename);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry ze;
			ZipFile zf = new ZipFile(zipfilename);
			while ((ze = zis.getNextEntry()) != null) {
				InputStream ins = zf.getInputStream(ze);

				File outFile = new File(dir + "\\" + ze.getName());
				FileOutputStream fos = new FileOutputStream(outFile);

				int bread;
				byte[] bin = new byte[4096];
				while ((bread = ins.read(bin, 0, 4096)) > -1) {
					fos.write(bin, 0, bread);

				}
				ins.close();
				fos.close();
			}

			zis.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void generateModelImage2() {
		boolean overwrite = true;

		Object target = ProjectBrowser.TheInstance.getActiveDiagram();
		if (target instanceof Diagram) {
			String defaultName = ((Diagram) target).getName();
			defaultName = Util.stripJunk(defaultName);

			ProjectBrowser pb = ProjectBrowser.TheInstance;
		
			Project p = pb.getProject();
		
				
							   
			File theFile = new File("C:\\Arquivos de programas\\Cópia de ArgoUML\\ArgoCASEGEO\\temp2.gif");
			if (theFile != null) {
				String path = theFile.getParent();
				String name = theFile.getName();
				String extension = SuffixFilter.getExtension(theFile);

				if (extension == null
						|| !((extension.equals(FileFilters.PSFilter._suffix))
								|| (extension
										.equals(FileFilters.EPSFilter._suffix))
								|| (extension
										.equals(FileFilters.GIFFilter._suffix)) || (extension
								.equals(FileFilters.SVGFilter._suffix)))) {
				
					extension = ".gif";// FileFilters.getSuffix(filter);
					theFile = new File(theFile.getParentFile(), theFile
							.getName()
							+ "." + extension);
				}
				
				CmdSaveGraphics cmd = null;
				if (FileFilters.PSFilter._suffix.equals(extension))
					cmd = new CmdSavePS();
				else if (FileFilters.EPSFilter._suffix.equals(extension))
					cmd = new CmdSaveEPS();
				else if (FileFilters.GIFFilter._suffix.equals(extension))
					cmd = new CmdSaveGIF();
				else if (FileFilters.SVGFilter._suffix.equals(extension))
					cmd = new CmdSaveSVG();
				else {
					pb.showStatus("Unknown graphics file type withextension "
							+ extension);
					// return false;
				}

				if (!path.endsWith(separator))
					path += separator;
				pb.showStatus("Writing " + path + name + "...");
				if (theFile.exists() && !overwrite) {
					System.out.println("Are you sure you want to overwrite "
							+ name + "?");
					String t = "Overwrite " + path + name;
					int response = JOptionPane.showConfirmDialog(pb, t, t,
							JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.NO_OPTION)
						;// return false;
				}
				try {
					FileOutputStream fo = new FileOutputStream(theFile);
					cmd.setStream(fo);
					cmd.doIt();
					fo.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pb.showStatus("Wrote " + path + name);
				// return true;
			}

		}

	}
	
	public void saveImage(File dir) {
	  File fOut = new File(dir.getAbsolutePath() + "\\" + nameFld.getText() + ".gif");
	  fOut.delete();
	  figDirFile.renameTo(new File(dir.getAbsolutePath() + "\\" + nameFld.getText() + ".gif"));
	}
	
	public boolean savePattern(boolean overwrite, File file) {
	    ProjectBrowser pb = ProjectBrowser.TheInstance;
	    Project p = pb.getProject();

	    try {

	      p.save(overwrite, file);
	      return true;
	    }
	    catch (FileNotFoundException fnfe) {
	      fnfe.printStackTrace();
	    }
	    catch (IOException ioe) {
	      ioe.printStackTrace();
	    }
	    catch (Exception ex) {
	    }
	    return false;
	  }
	
	public void saveDocumentation(File fileDir) {
		
		try {
			FileWriter writer = new FileWriter(fileDir);
			PrintWriter saida = new PrintWriter(writer, true);
			
			// grava no arquivo
			saida.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
			saida.println("<documentation name=\"" + nameFld.getText()+
					"\" theme=\"" + themeFld.getText() + "\">");
			saida.println("    <problem>" + problemArea.getText() +
					"</problem>");
			saida.println("    <context>" + contextArea.getText());
			saida.println();
			saida.println("\n    </context>");
			saida.println("    <forces>" + forcesArea.getText() +
			"\n    </forces>");
			saida.println("    <participants>" + 
					participantsArea.getText() +
			"\n    </participants>");
			saida.println("    <related_patterns>" + 
					relatedArea.getText() +
			"\n    </related_patterns>");
			saida.println("    <example>" + exampleArea.getText() +
			"\n    </example>");
			saida.println("</documentation>");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EditPatternUI app = new EditPatternUI();
		app.fillFields();
		
		System.out.println("prontim");
		System.exit(0);
	}

}
