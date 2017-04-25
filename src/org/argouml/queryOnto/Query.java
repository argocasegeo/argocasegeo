/*
 * Query.java
 *
 * Created on 26 de Novembro de 2007, 13:57
 */

package org.argouml.queryOnto;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.argouml.queryOnto.utilities.ItemGenerated;


/**
 *
 * @author  Gabriel
 */
public class Query extends javax.swing.JFrame {

	/** Creates new form Query */
	public Query() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButtonExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textFieldIntegDirectory = new javax.swing.JTextField();
        textFieldIntegOnto = new javax.swing.JTextField();
        textFieldOnto = new javax.swing.JTextField();
        jButtonXmlDirectory = new javax.swing.JButton();
        jButtonIntegOnto = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableQuery = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textFieldContext = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        textFieldRigid = new javax.swing.JTextArea();
        textFieldAP = new javax.swing.JTextField();
        desktopPaneSolution = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
        jLabel1.setForeground(new java.awt.Color(0, 51, 255));
        jLabel1.setText("Integration Query");

        jButtonExit.setText("Sair");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 294, Short.MAX_VALUE)
                .add(jButtonExit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jButtonExit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setText("Integration (XML) Files Directory:");

        jLabel3.setText("Integrated Ontology:");

        jLabel4.setText("Ontology:");

        jButtonXmlDirectory.setText("...");
        jButtonXmlDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonXmlDirectoryActionPerformed(evt);
            }
        });

        jButtonIntegOnto.setText("...");
        jButtonIntegOnto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIntegOntoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(textFieldIntegDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .add(textFieldIntegOnto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .add(textFieldOnto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonXmlDirectory, 0, 0, Short.MAX_VALUE)
                    .add(jButtonIntegOnto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(textFieldIntegDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonXmlDirectory))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(textFieldIntegOnto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonIntegOnto))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(textFieldOnto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel5.setText("Select an Analysis Pattern (with DoubleClick):");

        tableQuery.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Analysis Pattern", "Solution"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableQuery);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 599, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(16, 16, 16)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel6.setText("Analysis Pattern:");

        jLabel7.setText("Context:");

        jLabel8.setText("Rigid Classes (Domain for Reusable):");

        jLabel9.setText("Solution:");

        textFieldContext.setColumns(20);
        textFieldContext.setRows(5);
        jScrollPane2.setViewportView(textFieldContext);

        textFieldRigid.setColumns(20);
        textFieldRigid.setRows(5);
        jScrollPane3.setViewportView(textFieldRigid);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jLabel7)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, textFieldAP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel8))
                .add(21, 21, 21)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(desktopPaneSolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 318, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(textFieldAP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(desktopPaneSolution, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, 0, 616, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void jButtonIntegOntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIntegOntoActionPerformed
	
		if(!(textFieldIntegDirectory.getText().length() > 0) ){
			JOptionPane.showMessageDialog(this, "Choose the directory above first.", "Directory error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JFileChooser navegadorDiretorios2 = new JFileChooser();
		navegadorDiretorios2.setDialogTitle("Select the Ontology OWL file");
		navegadorDiretorios2.setFileSelectionMode(JFileChooser.FILES_ONLY);
		navegadorDiretorios2.setFileFilter(new 
				javax.swing.filechooser.FileFilter()
		{
			public boolean accept(File f)
			{  
				return f.isDirectory()
				|| f.getName().toLowerCase().endsWith(".owl");
			}
			public String getDescription()
			{  
				return "OWL files";
			}
		});


		int result = navegadorDiretorios2.showOpenDialog(null);
		if (result == JFileChooser.CANCEL_OPTION) {	return;	}

		File nomeArquivo = navegadorDiretorios2.getSelectedFile(); 

		if (result == JFileChooser.APPROVE_OPTION) {
			textFieldIntegOnto.setText(nomeArquivo.getAbsolutePath());
		}

		textFieldOnto.setText(nomeArquivo.getName());

		//Leitura dos arquivos XML do diretorio escolhido
		
		ArrayList<ItemGenerated> lista = new ArrayList<ItemGenerated>();
		
		File auxiliar = new File(textFieldIntegDirectory.getText());
		File[] array = auxiliar.listFiles(new FileFilter(){
			public boolean accept(File f)
			{  
				return f.getName().toLowerCase().endsWith(".xml");
			}
			public String getDescription()
			{  
				return "XML files";
			}
		}  );
		
		FileReader reader;
		BufferedReader buffer;
		String linha = null;
		
		String padraoAnalise = "";
		String classePadrao = "";
		String ontologia = "";
		String classeOntologia = "";
		
		for(File i : array){
			try {
				reader = new FileReader(i);
				buffer = new BufferedReader(reader);

				while(buffer.ready()){
					linha = buffer.readLine();
					//aqui come�a a leitura do xml no bra�o
					if(linha.contains("<padrao_analise>")){
						padraoAnalise = linha.substring( linha.indexOf(">") + 1, linha.indexOf("</"));	
					}else
					if(linha.contains("<classe_padrao>")){
						classePadrao = linha.substring( linha.indexOf(">") + 1, linha.indexOf("</"));	
					}else
					if(linha.contains("<ontologia>")){
						ontologia = linha.substring( linha.indexOf(">") + 1, linha.indexOf("</"));	
					}else
					if(linha.contains("<classe_ontologia>")){
						classeOntologia = linha.substring( linha.indexOf(">") + 1, linha.indexOf("</"));	
					}	
				}//fim do while
				
				lista.add( new ItemGenerated(padraoAnalise ,classePadrao, ontologia, classeOntologia) );
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}//fim do catch
			catch (IOException e) {
				e.printStackTrace();
			}

		}//fim do for

		//Constru��o da JTable
		
		int contador = 0;
		String ultimoPA = "";
		TableModel modelo = tableQuery.getModel();

		for( ItemGenerated i : lista ){
			if( i.getOntologia().equals( textFieldOnto.getText() ) && !i.getPadrao().equals(ultimoPA) ) {
				
				//adiciona linha a tabela caso for necess�rio
            	if (contador >= tableQuery.getRowCount()) {
            		((DefaultTableModel) modelo).addRow(new Object[]{ null, null });
            	}
				
				modelo.setValueAt(i.getPadrao().substring(0, i.getPadrao().indexOf("_")), contador, 0);
				modelo.setValueAt(i.getPadrao(), contador, 1);
				ultimoPA = i.getPadrao();
				contador++;
			
			}//fim do if
			
		}//fim do for
		
		//C�digo relativo a constru��o das partes de baixo
		
		ListSelectionModel selectionModel = tableQuery.getSelectionModel();
		selectionModel.addListSelectionListener( 
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						atualizaCampos(tableQuery.getSelectedRow());
					}//fim do valueChanged
					
				}
		);

	}//GEN-LAST:event_jButtonIntegOntoActionPerformed

	protected void atualizaCampos( int linha ) {
		String padrao = (String)tableQuery.getModel().getValueAt(linha, 0);
		String contexto = "";
		String classesRigidas = "";

		
		textFieldAP.setText(padrao);
		
		if (primeiraVezQueClica) {
			JFileChooser navegadorDiretorios = new JFileChooser();
			navegadorDiretorios.setDialogTitle("Select the Analysis Pattern Catalog directory");
			navegadorDiretorios.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
			int result = navegadorDiretorios.showOpenDialog(null);
			if (result == JFileChooser.CANCEL_OPTION) {	return;	}
			File nomeArquivo = navegadorDiretorios.getSelectedFile(); 
		
			if (result == JFileChooser.APPROVE_OPTION) {
				diretorioCPA = nomeArquivo.getAbsolutePath();
			}
			
			primeiraVezQueClica = false;
		}
		int index = diretorioCPA.lastIndexOf('\\');
		
		File file = new File(diretorioCPA  + "\\" + diretorioCPA.substring(index + 1,diretorioCPA.length())+".xml");
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(reader);
			String linhaXML = "";
			boolean ehPrimeiro = true;
			boolean ehPrimeiroRigid = true;
			
			while( buffer.ready() ){
				linhaXML = buffer.readLine();
				
				//caso em que <context> e </context> est�o na mesma linha
				if( linhaXML.contains("<context>") ){
					if( linhaXML.contains("</context>") ){
						contexto = linhaXML.substring(linhaXML.indexOf(">") + 1, linhaXML.indexOf("</"));
						return;
					}
					while(!linhaXML.contains("</context>")){
						if(ehPrimeiro){
							contexto = linhaXML.substring(linhaXML.indexOf(">") + 1);
							linhaXML = buffer.readLine();
							ehPrimeiro = false;
						}else{
							contexto = contexto.concat(linhaXML);
							linhaXML = buffer.readLine();
						}
					}
				}//fim do if do context
				else if( linhaXML.contains("<rigid_classes>") ){
					if( linhaXML.contains("</rigid_classes>") ){
						classesRigidas = linhaXML.substring(linhaXML.indexOf(">") + 1, linhaXML.indexOf("</"));
						return;
					}
					while(!linhaXML.contains("</rigid_classes>")){
						if(ehPrimeiroRigid){
							classesRigidas = linhaXML.substring(linhaXML.indexOf(">") + 1);
							linhaXML = buffer.readLine();
							ehPrimeiroRigid = false;
						}else{
							classesRigidas = classesRigidas.concat(linhaXML);
							linhaXML = buffer.readLine();
						}
					}
				}//fim do if do rigid_classes
				
				
				
				
			}//fim do while
			ehPraDesenhar = true;

			textFieldContext.setLineWrap(true);
			textFieldContext.setWrapStyleWord(true);
			textFieldContext.setText(contexto);
			
			textFieldRigid.setLineWrap(true);
			textFieldRigid.setWrapStyleWord(true);
			textFieldRigid.setText(classesRigidas);
			

			if( ehPraDesenhar ){
				Image image = new ImageIcon(diretorioCPA  + "\\" + diretorioCPA.substring(index + 1,diretorioCPA.length())+".gif").getImage().getScaledInstance(desktopPaneSolution.getWidth(), desktopPaneSolution.getHeight(), Image.SCALE_DEFAULT );
				ImageIcon aux = new ImageIcon(image);
				desktopPaneSolution.getGraphics().drawImage( aux.getImage(),  
						0,0,desktopPaneSolution.getBackground(), desktopPaneSolution);
			}//fim do if
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "There is no XML files on\n" + diretorioCPA + "\\\n", "File error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}//fim do atualizaCampos
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
    	 dispose(); 
    }//GEN-LAST:event_jButtonCancelActionPerformed
	
	
	private void jButtonXmlDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonXmlDirectoryActionPerformed
		/*
		 * Abre o navegador de diretorios para que o usuario escolha
		 * o caminho da pasta dos Padroes de Analise
		 * */
		JFileChooser navegadorDiretorios = new JFileChooser();
		navegadorDiretorios.setDialogTitle("Select the (XML) Integration File Directory");
		navegadorDiretorios.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = navegadorDiretorios.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {	return; }
		File nomeDiretorio = navegadorDiretorios.getSelectedFile();

		if (result == JFileChooser.APPROVE_OPTION) { 
			textFieldIntegDirectory.setText(nomeDiretorio.getAbsolutePath());
		}

		//Verifica se existe arquivo .xml dentro da pasta selecionada

		File auxiliar = new File(nomeDiretorio.getAbsolutePath());
		File[] array = auxiliar.listFiles();

		boolean temArquivoXML = false;

		for(File i : array){
			if(i.getName().toLowerCase().endsWith(".xml")){
				temArquivoXML = true;
				break;
			}//fim do if	
		}//fim do for

		if( !temArquivoXML ){
			JOptionPane.showMessageDialog(this, "This directory don't contains a XML file!\nTry another.", "Directory error", JOptionPane.INFORMATION_MESSAGE);
			textFieldIntegDirectory.setText("");
		}//fim do if

	}//GEN-LAST:event_jButtonXmlDirectoryActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Query().setVisible(true);
			}
		});
	}
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonIntegOnto;
    private javax.swing.JButton jButtonXmlDirectory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tableQuery;
    private javax.swing.JTextField textFieldAP;
    private javax.swing.JTextArea textFieldContext;
    private javax.swing.JTextField textFieldIntegDirectory;
    private javax.swing.JTextField textFieldIntegOnto;
    private javax.swing.JTextField textFieldOnto;
    private javax.swing.JTextArea textFieldRigid;
    private javax.swing.JDesktopPane desktopPaneSolution;
    private boolean primeiraVezQueClica = true;
	private String diretorioCPA;
	private boolean ehPraDesenhar;
    // End of variables declaration//GEN-END:variables

}
