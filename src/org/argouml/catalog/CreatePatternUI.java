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
import java.awt.event.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;



public class CreatePatternUI extends JDialog {
	
	protected StatusBar _statusBar = new StatusBar();
	
	public static final String separator = "/";
	
	private Container c;
	
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
	
	private File figDirFile;
	
	public CreatePatternUI() throws Exception {
    	
		
		
		setTitle("New Analysis Pattern");
		
		c = getContentPane();	
	    c.setLayout(null);
	  
	    File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		figDirFile = new File(path + "\\temp.gif");
		this.generateModelImage();
		
				
		
		System.out.println("veja o path: " + path);
		Icon imageDiagram = new ImageIcon( path + "\\temp.gif");
		
		
		JLabel imageLabel = new JLabel(imageDiagram);
		imageLabel.setToolTipText("Class diagram displaying the proposed solution");
					
		JPanel panel1 = new JPanel();
		JLabel whatisLbl = new JLabel("CREATE A NEW ANALYSIS PATTERN");
		whatisLbl.setFont(new Font("Arial",Font.BOLD,11));
		c.add(whatisLbl);
		whatisLbl.setBounds(10, 10 , 250 , 20);
	
		panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"SOLUTION"), BorderFactory
				.createLineBorder(Color.black)));
		panel1.setBackground(new Color(253, 254, 235)); // LIGHT_GRAY � bom
		
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
					
					JOptionPane.showMessageDialog(CreatePatternUI.this, 
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
			    System.out.println("gustavo: " + fileDir.getAbsolutePath());
				
				if (savePattern(true, fileDir)) { // estava trySave...
				
					JOptionPane.showMessageDialog(null, 
							"The Analysis Pattern \"" + nameFld.getText() +
							"\" was created successfully.", "ArgoCASEGEO", 
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
				saveImage(diretorio);		
				
				figDirFile.delete();

				dispose();

			}
		});
	    
	    
	    
	    c.add(finishBtn);
	    finishBtn.setBounds(242, 620, 90, 25);
	    
	    cancelBtn = new JButton("Cancel");
	    cancelBtn.addActionListener(new ActionListener() {        	
			public void actionPerformed(ActionEvent e) {
				
				figDirFile.delete();
				dispose();
				
			}
	    }
	    );
	    
	    
	    c.add(cancelBtn);
	    cancelBtn.setBounds(338, 620, 90, 25);
	    
	   // helpBtn = new JButton("Help");
	   // helpBtn.setToolTipText("Help about how to create a new Analysis Pattern");
	    //c.add(helpBtn);
	   // helpBtn.setBounds(434, 620, 60, 25);
		
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
		nameFld = new JTextField(8);
			
		panel2.add(nameLbl);
		nameLbl.setBounds(17,28, 40,20);
		
		panel2.add(nameFld);
		nameFld.setBounds(57, 28, 176, 20);
		
		/*
		 * tema
		 */
		
		themeLbl = new JLabel("Theme: ");				
		themeLbl.setToolTipText("Theme of the Analysis Pattern.");
		themeFld = new JTextField(20);
		
		panel2.add(themeLbl);
		themeLbl.setBounds(17, 55, 64, 20);
		
		panel2.add(themeFld);
		themeFld.setBounds(62, 55, 172, 20);
	
		
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
		 * for�as
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
		relatedLbl.setBounds(17, 475, 115, 20);
				
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
					
		// adiciona os dois pain�is ao JFrame
		
		c.add(scroller, BorderLayout.CENTER);
		c.add(panel2, BorderLayout.EAST);
						
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				figDirFile.delete();
				dispose();
			}
		});
		
		setResizable(false);
		setSize(1000, 725);
		//pack();
                
        setModal(true);
		setVisible(true);
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
					//TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
				pb.showStatus("Wrote " + path + name);
				// return true;
			}

		}

	}
	
	public void saveImage(File dir) {
	  System.out.println("entrei aqui!!!!!!!!!!!!!");
		figDirFile.renameTo(new File(dir + "\\" + nameFld.getText() + ".gif"));
		
	}
	
	public void extractFile(String zipfilename) {
		try {
			
			File f = new File(zipfilename);
			String dir = f.getParent();
			
			 FileInputStream fis = new FileInputStream(zipfilename); 
			 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis)); 
			 ZipEntry ze; 
			 ZipFile zf = new ZipFile(zipfilename);
			 while ( (ze = zis.getNextEntry()) != null) {
				 InputStream ins = zf.getInputStream(ze);
				
					File outFile = new File(dir+ "\\"+ze.getName());
					 FileOutputStream fos = new FileOutputStream(outFile); 
					 
					 int bread;
					 byte[] bin = new byte[4096]; 
					 while ( (bread = ins.read(bin, 0, 4096)) > -1) { 
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
	
	public boolean savePattern(boolean overwrite, File file) {
	    ProjectBrowser pb = ProjectBrowser.TheInstance;
	    Project p = pb.getProject();

	    

	    try {

	   /*
	      String sStatus = MessageFormat.format (
	          Argo.localize ("Actions", "template.save_project.status_writing"),
	          new Object[] {file}
	        );
	      pb.showStatus (sStatus);
		*/	
		  
	      p.save(overwrite, file);
	      extractFile(file.getAbsolutePath());
	      	
/*
	      sStatus = MessageFormat.format (
	          Argo.localize ("Actions", "template.save_project.status_wrote"),
	          new Object[] {p.getURL()}
	        );
	      pb.showStatus (sStatus);
	      Argo.log.debug ("setting most recent project file to " +
	                      file.getCanonicalPath());
	      Configuration.setString(Argo.KEY_MOST_RECENT_PROJECT_FILE, file.getCanonicalPath());
	      */
	      return true;
	    }
	    catch (FileNotFoundException fnfe) {
	     /* String sMessage = MessageFormat.format (
	          Argo.localize ("Actions", "template.save_project.file_not_found"),
	          new Object[] {fnfe.getMessage()}
	        );
	      
	      JOptionPane.showMessageDialog (
	          pb,
	          sMessage,
	          Argo.localize ("Actions", "text.save_project.file_not_found_title"),
	          JOptionPane.ERROR_MESSAGE
	        );*/
	      
	      fnfe.printStackTrace();
	    }
	    catch (IOException ioe) {
	      /*String sMessage = MessageFormat.format (
	          Argo.localize ("Actions", "template.save_project.io_exception"),
	          new Object[] {ioe.getMessage()}
	        );
	      
	      JOptionPane.showMessageDialog (
	          pb,
	          sMessage,
	          Argo.localize ("Actions", "text.save_project.io_exception_title"),
	          JOptionPane.ERROR_MESSAGE
	        );
	      */
	      ioe.printStackTrace();
	    }
	    catch (Exception ex) {
	    /*	String sMessage = MessageFormat.format (
	          Argo.localize ("Actions", "template.save_project.general_exception"),
	          new Object[] {ex.getMessage()}
	        );
	      
	      JOptionPane.showMessageDialog (
	          pb,
	          sMessage,
	          Argo.localize ("Actions", "text.save_project.general_exception_title"),
	          JOptionPane.ERROR_MESSAGE
	        );
	      */
	      ex.printStackTrace();
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
	/*
	 * Funcao main apenas para teste... retir�-la depois...
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		CreatePatternUI app = null;
		try {
			app = new CreatePatternUI();
		} catch (Exception e) {
			System.out.println("lancou");
			JOptionPane.showMessageDialog(app, 
					"Error!! Please fill in all required fields.", "ArgoCASEGEO", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		/*if (    nameFld.getText().equals("") ||
				problemArea.getText().equals("") ||
				contextArea.getText().equals("")||
				forcesArea.getText().equals("")
			) {
			
			JOptionPane.showMessageDialog(CreatePatternUI.this, 
					"Error!! Please fill in all required fields.", "ArgoCASEGEO", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		//System.out.println("prontim");
		//System.exit(1);
	}*/

}

