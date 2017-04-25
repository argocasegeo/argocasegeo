package org.argouml.oraclespatial;

import javax.swing.*;
import java.io.*;
//import java.awt.*;
//import java.awt.event.*;

public class JanelaGeracao
{   
	private String m_fileName;
	
	public JanelaGeracao() 
	{     		
		JFileChooser v_navegadorDiretorios = new JFileChooser();
		v_navegadorDiretorios.setDialogTitle("Select the destination directory");
		v_navegadorDiretorios.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int v_result = v_navegadorDiretorios.showOpenDialog(null);
		if( v_result == JFileChooser.CANCEL_OPTION)
		{	
			return;
		}
			
		File v_nomeDiretorio = v_navegadorDiretorios.getSelectedFile(); 
		m_fileName = v_nomeDiretorio.getAbsolutePath();
	}
	
	public String getDir()
	{
		return m_fileName;
	}
}