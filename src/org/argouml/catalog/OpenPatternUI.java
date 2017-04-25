/*
 *  OpenPatternUI.java
 *  
 *  Alexandre Gazola 
 *  Criado em 06/08/2005
 *  �ltima atualiza��o em 19/01/2006
 * 
 *  Projeto Final de Curso
 *  Desenvolvimento do Cat�logo de Padr�es de An�lise da
 *  Ferramenta CASE ArgoCASEGEO
 *  
*/


package org.argouml.catalog;

import java.awt.*;
import javax.swing.*;
import org.argouml.ui.*;
import org.argouml.uml.ui.*;
import org.argouml.kernel.*;
import org.argouml.util.*;
import org.tigris.gef.base.*;
import org.tigris.gef.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.event.*;

import javax.swing.event.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.*;


/**
 * Esta classe � respons�vel pela interface gr�fica  da fun��o
 * de Abrir/Reutilizar padr�es.
 * 
 * @author Alexandre Gazola
 *
 */
/**
 * @author Gazolla
 *
 */
public class OpenPatternUI extends JDialog {

	protected StatusBar _statusBar = new StatusBar();

	public static final String separator = "/";

	private Container c;

	private JList myPatterns;

	private JTextArea docArea;

	private JButton openBtn;

	private JButton reuseBtn;

	private JButton cancelBtn;

	//private JButton helpBtn;

	private Icon imageDiagram;

	private JLabel imageLabel;

	private JPanel panel1;

	private JTextField searchFld;

	private JTextArea resultArea;

	private File figDirFile;

	private Normalizer normalizer;
	
	private DefaultListModel model;
	
	private JLabel numberOfResultsLbl;
	
	private JScrollPane scroller2;

	public OpenPatternUI() {
		setTitle("Open/Reuse Analysis Pattern");

		c = getContentPane();
		c.setLayout(null);

		clean();
		
		File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		figDirFile = new File(path + "\\temp.gif");
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"SOLUTION"), BorderFactory
				.createLineBorder(Color.black)));
		panel1.setBackground(new Color(253, 254, 235)); // LIGHT_GRAY � bom

		imageLabel = new JLabel();
		// adiciona imagem ao painel1
		panel1.add(imageLabel);

		// adiciona os botoes ao painel1
		openBtn = new JButton("Open");
		openBtn.setToolTipText("Open the selected Analysis Pattern");
		openBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				ProjectBrowser pb = ProjectBrowser.TheInstance;
				Project myProject = pb.getProject();

				String patternName = (String) myPatterns.getSelectedValue();
				File fileDirAux = new File("");
				String path = fileDirAux.getAbsolutePath();
				path += "\\" + "Analysis Pattern Catalog" + "\\" + patternName
						+ "\\" + patternName + ".zargo";

				File theFile = new File(path);

				try {
					URL url = theFile.toURL();
					myProject = Project.loadProject(url);
					pb.setProject(myProject);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dispose();
			}
		});

		c.add(openBtn);
		openBtn.setBounds(180, 659, 90, 25);

		reuseBtn = new JButton("Reuse");
		reuseBtn.setToolTipText("Reuse the selected Analysis Pattern");
		reuseBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String patternName = (String) myPatterns.getSelectedValue();
				reusePattern(patternName);
				dispose();
			}
		});

		c.add(reuseBtn);
		reuseBtn.setBounds(278, 659, 90, 25);

		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				clean();
				dispose();
			}
		});

		c.add(cancelBtn);
		cancelBtn.setBounds(378, 659, 90, 25);

		/*helpBtn = new JButton("Help");
		helpBtn
				.setToolTipText("Help about how to create a new Analysis Pattern");
		c.add(helpBtn);
		helpBtn.setBounds(478, 659, 90, 25);*/

		JScrollPane scroller = new JScrollPane(panel1);
		scroller.setBounds(10, 150, 700, 500);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		panel2.setBounds(710, 10, 280, 680);

		panel2
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder(BorderFactory.createEtchedBorder(),
								"SEARCH"), BorderFactory
						.createLineBorder(Color.black)));

		JLabel avlLbl = new JLabel("AVAILABLE PATTERNS");
		panel2.add(avlLbl);

	
		 model = new DefaultListModel();
		 myPatterns = new JList(model);
		 myPatterns.setPreferredSize(new Dimension(250, 200));
		
	
		 
		 
		 // myPatterns = new JList(getAvailablePatterns());
        String availables[] = getAvailablePatterns();
		 for (int i = 0; i < availables.length; i++) {
			 model.addElement((String)availables[i]);
			 
		 }
		 
		 if (model.getSize() == 0) {
			   	reuseBtn.setEnabled(false);
		    	openBtn.setEnabled(false);
		    } else {
		    	reuseBtn.setEnabled(true);
		    	openBtn.setEnabled(true);
		    }
		
		
		
		
		myPatterns.setVisibleRowCount(15);
		myPatterns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrl = new JScrollPane(myPatterns);
		
		
	
		panel2.add(scrl);

		myPatterns.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String s = (String) myPatterns.getSelectedValue();
				updateIF(s);
			}
		});
		
		myPatterns.addKeyListener(new KeyListener() {
		
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
		
			}
		
			// se o usu�rio pressionou a tecla delete
			public void keyPressed(KeyEvent arg0) {
				if ((arg0.getKeyText(arg0.getKeyCode()))
						.equalsIgnoreCase("delete")) {

					int option = JOptionPane.showConfirmDialog(
							OpenPatternUI.this,
							"Are you sure you want to delete "
									+ myPatterns.getSelectedValue().toString()
									+ "?", "Delete Analysis Pattern",
							JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION) {

						File fileDirAux = new File("");
						String path = fileDirAux.getAbsolutePath();
						path += "\\Analysis Pattern Catalog";
						File theFile = new File(path);
						File[] files = theFile.listFiles();
						for (int i = 0; i < files.length; i++) {
							if (files[i].getName().equals(
									(String) myPatterns.getSelectedValue())) {
								File[] innerFiles = files[i].listFiles();
								for (int j = 0; j < innerFiles.length; j++) {
									innerFiles[j].delete();
								}

								files[i].delete();
								break;
							}

						}
						int index = myPatterns.getSelectedIndex();
						model.removeElementAt(index);

						myPatterns.setSelectedIndex(0);

						if (model.getSize() == 0) {
							docArea.setText("");
							openBtn.setEnabled(false);
							reuseBtn.setEnabled(false);
						}

					}

				}
			}
		
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
		
			}
		
		});
		

		JLabel searchLbl = new JLabel("SEARCH BY KEYWORD");
		panel2.add(searchLbl);

		searchFld = new JTextField(17);
		panel2.add(searchFld);

		JButton searchBtn = new JButton("OK");
		panel2.add(searchBtn);
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				performSearch(searchFld.getText());
			}
		});

		
		numberOfResultsLbl = new JLabel("                             ");
		panel2.add(numberOfResultsLbl);
		
		resultArea = new JTextArea(16, 22);
		resultArea.setEditable(false);
		panel2.add(new JScrollPane(resultArea));
		
		
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout());
		
		
	
		
		panel3.setBounds(9, 6, 700, 140);

		docArea = new JTextArea(150, 20);
		docArea.setEditable(false);
		
		
		scroller2 = new JScrollPane(docArea);
	   
		
		
		//scroller2.setBounds(10, 150, 700, 500);
		
		
		panel3.add(scroller2, BorderLayout.CENTER);

		panel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"DOCUMENTATION"), BorderFactory
				.createLineBorder(Color.black)));

		// adiciona os dois pain�is ao JFrame

		c.add(scroller, BorderLayout.CENTER);
		c.add(panel2, BorderLayout.EAST);
		c.add(panel3, BorderLayout.NORTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				figDirFile.delete();
				dispose();
			}
		});

		setResizable(false);
		setSize(1000, 725);

		setModal(true);
		setVisible(true);
	}

	/**
	 * Limpa os arquivos criados no diret�rio dos Padr�es de
	 * An�lise
	 */
	public void clean() {

		File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		path += "\\Analysis Pattern Catalog";

		File directory = new File(path);

		File[] files = directory.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()
					&& !(files[i].getName().equals("AnalysisPatterns.txt"))
					&& !(files[i].getName().equals("ReuseDone.zargo"))) {

				files[i].delete();
			}
		}
	}

	/**
	 * Realiza uma busca no Cat�logo de Padr�es de An�lise
	 * @param keywords Palavras-chave utilizadas na busca.
	 */
	public void performSearch(String keywords) {

		resultArea.setText("");

		String[] tokens = getTokens(keywords);

		File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		path += "\\Analysis Pattern Catalog";

		File directory = new File(path);

		File[] patterns = directory.listFiles();

		String searchInXml = null;
		
		// n�mero de padr�es encontrados na busca
		int contador = 0;
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i].isDirectory()) {
				searchInXml = patterns[i].getName() + ".xml";
				File fileToRead = new File(patterns[i] + "\\" + searchInXml);

				try {
					FileReader reader = new FileReader(fileToRead);
					BufferedReader leitor = new BufferedReader(reader);

					String line = null;

					line = leitor.readLine();

					while (line != null) {

						for (int j = 0; j < tokens.length; j++) {
							if (line.contains(tokens[j])
									&& !(resultArea.getText()
											.contains(patterns[i].getName()))) {
								contador++;
								resultArea.append(patterns[i].getName());
								resultArea.append("\n\n");
								break;
							}
						}
						line = leitor.readLine();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (resultArea.getText().equals("")) {
			resultArea.setText("The search didn�t find any match.");
			numberOfResultsLbl.setText("                        ");
		} else {
			numberOfResultsLbl.setText("Number of patterns found: " + contador +
					"                              ");
		}
	}

	/**
	 * Retorna as palavras da String keywords
	 * @param keywords String com as palavras-chave
	 * @return Array de palavras da String
	 */
	public String[] getTokens(String keywords) {

		StringTokenizer tokens = new StringTokenizer(keywords);
		String[] result = new String[tokens.countTokens()];

		for (int i = 0; tokens.hasMoreTokens(); i++) {
			result[i] = tokens.nextToken();
		}
		return result;
	}

	/**
	 * Principal fun��o do Cat�logo de Padr�es de An�lise.
	 * Permite que esquemas de bancos de dados  geogr�ficos
	 * sejam reutilizados no projeto de novos esquemas
	 * @param patternName Padr�o de An�lise a ser reutilizado
	 */
	public void reusePattern(String patternName) {

		// Projeto atual que o usuario estah criando... 
		// salva como tempGazola.zargo
		ProjectBrowser pb = ProjectBrowser.TheInstance;
		File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		path += "\\" + "Analysis Pattern Catalog";
		File myFile = new File(path + "\\tempGazola.zargo");

		ActionSaveProjectAs.SINGLETON.trySave(true, myFile);
		extractFile(path + "\\tempGazola.zargo");

		// PARA DELETAR ISSO TENHO QUE FAZER COM Q A APLICACAO
		// NAO PRECISE MAIS DELE
		myFile.delete();

		File myDir = new File(path);

		File[] theFiles;
		theFiles = myDir.listFiles();

		normalizer = new Normalizer();

		// estes arquivos representam a aplicacao em questao
		// (que importara o padrao de analise)
		String xmiFile1 = findXmi(theFiles);
		String pgmlFile1 = findPgml(theFiles);

		// estes arquivos representam o padrao de analise a
		// ser utilizado
		String xmiFile2 = findXmi(patternName, path);
		String pgmlFile2 = findPgml(patternName, path);

		// prepara xmi para reutilizar
		String goodXmi = myDir.getAbsolutePath() + "\\goodXmi.xmi";
		// gera o novo xmi (a ser usado na intercalacao)
		normalizer.replaceXmi(xmiFile2, goodXmi);

		// prepara pgml para reutilizar
		String goodPgml = myDir.getAbsolutePath() + "\\goodPgml.pgml";
		// gera o novo pgml (a ser usado na intercalacao)
		normalizer.replacePgml(pgmlFile2, goodPgml);

		// gera xmi final
		String juntadoXmi = myDir.getAbsolutePath() + "\\juntadoXmi.xmi";
        try{
            normalizer.mergeXmi("file:///" + xmiFile1, "file:///" + goodXmi,juntadoXmi);
        }catch(Exception e){
				Project myProject = pb.getProject();
				path +=  "\\" + patternName
						+ "\\" + patternName + ".zargo";

				File theFile = new File(path);

				try {
					URL url = theFile.toURL();
					myProject = Project.loadProject(url);
					pb.setProject(myProject);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dispose();
                return;
        }
		// gera pgml final
		String juntadoPgml = myDir.getAbsolutePath() + "\\juntadoPgml.pgml";
		takeOffDtdDeclaration(pgmlFile1, myDir, 1);
		takeOffDtdDeclaration(goodPgml, myDir, 2);
		normalizer.mergePgml("file:///" + myDir + "\\temp1.pgml", "file:///"
				+ myDir + "\\temp2.pgml", juntadoPgml);

		String argoFile = findArgo(theFiles);
		System.out.println("veja o argo: " + argoFile);

		zipFiles(myDir + "\\juntadoPgml.pgml", myDir + "\\juntadoXmi.xmi",
				argoFile, myDir + "\\reuseDone.zargo");

		Project myProject;
		myProject = Project.makeEmptyProject();
		pb.setProject(myProject);
		String title = pb.TheInstance.getTitle();

		try {
			URL url = (new File(myDir + "\\reuseDone.zargo")).toURL();
			myProject = Project.loadProject(url);
			pb.setProject(myProject);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ProjectBrowser.TheInstance.setTitle(title);
		
		clean();

	}

	/**
	 * Desloca o arquivo de nome pgml2 em rela��o ao arquivo
	 * pgml1
	 * @param pgml1 Arquivo base.
	 * @param pgml2 Arquivo a ser deslocado.
	 */
	public void shiftPgml(String pgml1, String pgml2) {

		DocumentBuilderFactory dbf = null;		    
		DocumentBuilder db = null;		    
		Document doc = null;
		DocumentBuilder db2 = null;
		Document docPattern = null;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
			doc = db.parse(pgml1);
			
			db2 = dbf.newDocumentBuilder();
			docPattern = db.parse(pgml2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        Element elem = doc.getDocumentElement();
        NodeList nl = elem.getElementsByTagName("group");
	   	    
	    // obtem elemento que cont�m o que o modelo possui
	    Element group = (Element)nl.item(0); 
	  	 
	    
        // obt�m raiz do documento XML (padr�o)
	    Element elemPattern = docPattern.getDocumentElement();
	    // obt�m pacotes do padr�o
	    NodeList nlPattern = elemPattern.getElementsByTagName("group");
	
	    
	   String attribute2 = group.getAttribute("description");
	   if (attribute2.contains("FigPackage")) {
		   String veja = attribute2.substring(54);
		 
		   
		   
		   
		   System.out.println("aqui legal noivos!");
		   System.out.println(veja);
		   
		   
		   
		   
		   
	   }
	    
	    
	    
	    String attribute1 = group.getAttribute("description");
	   if (attribute1.contains("FigPackage")) {
		   
		   
		   
		   
		   
		   
	   }
	    
	    
	    // acrescenta os pacotes do padr�o ao modelo atual
	  /*  for (int i = 0; i < nlPattern.getLength(); i++) {
	       	model.appendChild(doc.importNode(nlPattern.item(i), true));
	    }*/
	}		

	/**
	 * Compacta os arquivos .pgml, .xmi e .argo em um arquivo
	 * .zargo
	 * @param filePgml Arquivo .pgml a ser compactado
	 * @param fileXmi Arquivo .xmi a ser compactado
	 * @param fileArgo Arquivo .argo a ser compactado
	 * @param arqSaida Arquivo .zargo gerado
	 */
	public void zipFiles(String filePgml, String fileXmi, String fileArgo,
			String arqSaida) {

		final int TAMANHO_BUFFER = 2048;
		int cont;
		byte[] dados = new byte[TAMANHO_BUFFER];
		String[] arquivos = { filePgml, fileXmi, fileArgo };

		BufferedInputStream origem = null;
		FileInputStream streamDeEntrada = null;
		FileOutputStream destino = null;
		ZipOutputStream saida = null;
		ZipEntry entry = null;

		try {
			destino = new FileOutputStream(arqSaida);
			saida = new ZipOutputStream(new BufferedOutputStream(destino));

			for (int i = 0; i < arquivos.length; i++) {
				File arquivo = new File(arquivos[i]);

				if (arquivo.isFile() && !(arquivo.getName()).equals(arqSaida)) {
					System.out.println("Compactando: " + arquivos[i]);

					streamDeEntrada = new FileInputStream(arquivo);
					origem = new BufferedInputStream(streamDeEntrada,
							TAMANHO_BUFFER);
					entry = new ZipEntry(arquivos[i]);
					saida.putNextEntry(entry);

					while ((cont = origem.read(dados, 0, TAMANHO_BUFFER)) != -1) {
						saida.write(dados, 0, cont);
					}
					origem.close();
				}
			}
			saida.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove a declara�ao do arquivo .pgml
	 * @param s
	 * @param myDir
	 * @param t
	 */
	public void takeOffDtdDeclaration(String s, File myDir, int t) {

		File fIn = new File(s);

		File fOut = new File(myDir + "\\temp" + t + ".pgml");

		try {
			FileReader reader = new FileReader(fIn);
			BufferedReader leitor = new BufferedReader(reader);

			FileWriter writer = new FileWriter(fOut);
			PrintWriter saida = new PrintWriter(writer, true);

			String line = null;

			line = leitor.readLine();
			saida.println(line);
			line = leitor.readLine();
			line = leitor.readLine();

			while (line != null) {

				saida.println(line);
				line = leitor.readLine();

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Encontra o arquivo .pgml correspondente ao padr�o 
	 * patterNName
	 * @param patternName Nome do padr�o de an�lise
	 * @param path Caminho do padr�o
	 * @return Arquivo .pgml procurado.
	 */
	public String findPgml(String patternName, String path) {

		File otherFile = new File(path + "\\" + patternName);
		String s = null;
		for (File f : otherFile.listFiles()) {
			if (f.isFile()) {

				s = f.getName();
				if (s.contains("UMLGeoFrame")) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * Encontra o arquivo .xmi correspondente ao padr�o 
	 * patterNName
	 * @param patternName Nome do padr�o de an�lise
	 * @param path Caminho do padr�o
	 * @return Arquivo .xmi procurado.
	 */
	public String findXmi(String patternName, String path) {
		File otherFile = new File(path + "\\" + patternName);
		String s = null;
		for (File f : otherFile.listFiles()) {
			if (f.isFile()) {

				s = f.getName();
				if (s.contains("xmi")) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * Encontra o arquivo .pgml presente num determinado
	 * diret�rio
	 * @param theFiles Arquivos pertentes ao diret�rio
	 * @return Arquivo .pgml
	 */
	public String findPgml(File[] theFiles) {
		String s = null;
		for (File f : theFiles) {

			if (f.isFile()) {

				s = f.getName();
				if (s.contains("UMLGeoFrame")) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * Encontra o arquivo .argo presente num determinado
	 * diret�rio
	 * @param theFiles Arquivos pertentes ao diret�rio
	 * @return Arquivo .argo
	 */
	public String findArgo(File[] theFiles) {
		String s = null;
		for (File f : theFiles) {
			if (f.isFile()) {

				s = f.getName();
				if (s.contains(".argo")) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * Encontra o arquivo .xmi presente num determinado
	 * diret�rio
	 * @param theFiles Arquivos pertentes ao diret�rio
	 * @return Arquivo .xmi
	 */
	public String findXmi(File[] theFiles) {
		String s = null;
		for (File f : theFiles) {
			if (f.isFile()) {
				s = f.getName();
				if (s.contains("xmi")) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
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

	/**
	 * Atualiza a interface gr�fica para exibir o padr�o
	 * correspondente
	 * @param patternName Nome do padr�o de an�lise
	 */
	public void updateIF(String patternName) {

		File fileDirAux = new File("");
		String path = fileDirAux.getAbsolutePath();
		path += "\\" + "Analysis Pattern Catalog" + "\\" + patternName;
		imageDiagram = new ImageIcon(path + "\\" + patternName + ".gif");
		imageLabel.setIcon(imageDiagram);

		if (patternName == null) {
			System.out.println("No patterns in the catalog.");
			return;
		}
		
		// exhibits the documentation related to the pattern
		String documentation = "";

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

		documentation += "Name: " + patternName + "\n";

		String xmlPathname = ("file:///" + path + "\\" + patternName + ".xml");

		try {

			doc = db.parse(xmlPathname);

			// pega a raiz do documento XML
			Element elem = doc.getDocumentElement();

			String value = "";

			//obtem tema
			value = elem.getAttribute("theme");
			if (!value.equals("")) {
				documentation += "Theme: " + value + "\n";
			}

			//obtem problema
			value = Normalizer.getChildTagValue(elem, "problem");
			documentation += "Problem: " + value + "\n";

			// obtem contexto
			value = Normalizer.getChildTagValue(elem, "context");
			documentation += "Context: " + value.trim() + "\n";

			//			obtem for�as
			value = Normalizer.getChildTagValue(elem, "forces");
			documentation += "Forces: " + value + "\n";

			//			obtem participantes
			value = Normalizer.getChildTagValue(elem, "participants");
			if (!value.contains(" ")) {
				documentation += "Participants: " + value + "\n";
			}

			//			obtem padr�es relacionados
			value = Normalizer.getChildTagValue(elem, "related_patterns");
			if (!value.contains(" ")) {
				documentation += "Related Patterns: " + value + "\n";
			}

			//			obtem exemplo
			value = Normalizer.getChildTagValue(elem, "example");
			if (!value.contains(" ")) {
				documentation += "Example: " + value + "\n";
			}
			docArea.setText(documentation.trim());
			 JScrollBar vertical = scroller2.getVerticalScrollBar();
			    JScrollBar horizontal = scroller2.getHorizontalScrollBar();
			     //Para rolar tudo para cima
			     vertical.setValue(vertical.getMinimum());
			     horizontal.setValue(horizontal.getMinimum());
			     

		} catch (SAXException e) {
			System.out.println("exce��o com arquivo xml!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OpenPatternUI.this.repaint();

	}



	/**
	 *  Gera a imagem do modelo
	 */
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
	 * Retorna os padr�es de an�lise dispon�veis
	 * @return Padr�es de Analise dispon�veis
	 */
	public String[] getAvailablePatterns() {

		ArrayList<String> patterns = new ArrayList<String>();
		File workDir = new File(figDirFile.getParent()
				+ "\\Analysis Pattern Catalog");
		File[] lista = workDir.listFiles();

		for (File f : lista) {
			if (f.isDirectory()) {
				patterns.add(f.getName());
			}
		}

		Object[] obs = patterns.toArray();
		String[] result = new String[obs.length];
		for (int i = 0; i < obs.length; i++) {
			result[i] = (String) (obs[i]);
		}

		return (result);
	}
}