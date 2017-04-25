package org.argouml.oraclespatial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class OracleDatabase {
	
	// nome do banco de dados
	private String m_dbName;
	// diretório utilizado
	private String m_dirName;
	//Arquivo que guardara os scripts
	private FileWriter m_saida;
	//armazena true se arquivo que guarda os scripts for criado
	private boolean m_criacaoOK;
	
	public OracleDatabase(String name, String directory)// throws IOException
	{
		m_dbName = name;
		m_dirName = directory;
		
		String v_CaminhoArquivoSaida = m_dirName + "\\" + m_dbName + ".txt";
		
		File teste = new File(v_CaminhoArquivoSaida); 
		int resposta = 0;
		
		if(teste.exists())
			resposta = JOptionPane.showConfirmDialog(null, "The file already exists!\n Wishes overwrite?", "ATENTION" , JOptionPane.YES_NO_OPTION);
		
		//se resposta for zero, ou arquivo nao existe ou usuario deseja sobrescrever o existente
		if(resposta == 0)
		{
			try 
			{
				m_saida = new FileWriter(v_CaminhoArquivoSaida);
				m_criacaoOK = true;
				JOptionPane.showMessageDialog(null, "File created!");
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(null, "It was not possible to create this file.", "ERROR" , JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}       
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Rename this file and try again.");
		}
	}
	
	public boolean isOk()
    {
      return m_criacaoOK;       
    }
	
	public void adicionarTabela(ObjetoGeoFrame p_objGeoFrame) throws IOException
	{
		m_saida.write(p_objGeoFrame.gerarStringCorrespondente());
	}
	
	public void adicionarRelacionamento(Relacionamento p_relacionamento) throws IOException
	{
		m_saida.write(p_relacionamento.gerarStringCorrespondente());
	}
	
	public void finalizar() throws IOException
	{
		m_saida.close(); //ATENCAO - FECHAR ARQUIVO SOH NO FINAL
	}
}
