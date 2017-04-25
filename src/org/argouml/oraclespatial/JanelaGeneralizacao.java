package org.argouml.oraclespatial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JanelaGeneralizacao extends JDialog {
	
	private JRadioButton aRadio, bRadio, cRadio;
	private JLabel label;
	private JTextArea textarea;
	private char op;
	private JButton okBtn;
	private String descricao;
	
	public JanelaGeneralizacao(JFrame m, String p_nomeJanela) {     
		
		super(m, p_nomeJanela, true);
		
		setSize( 370, 235 );
		this.setResizable(false);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout());
		
		label = new JLabel( "Choose the option that will handle the generalization: " );
		contentPane.add( label );
		
		aRadio = new JRadioButton("Option A", true);
		contentPane.add(aRadio);
		bRadio = new JRadioButton("Option B", false);
		contentPane.add(bRadio);
		cRadio = new JRadioButton("Option C", false);
		contentPane.add(cRadio);
		
		textarea = new JTextArea(6,8);
		textarea.setForeground(Color.red);
		textarea.setOpaque(false);
		textarea.setFont(new Font("Arial", Font.ITALIC, 12));
		
		contentPane.add (textarea);
		/*
		 * descricao = "\nOpcao A: agrupa todas as classes num 'tabelao' \n" +
				"Opcao B: cada subclasse vira uma classe e a classe mãe some \n" +
				"Opcao C: cada classe vira uma tabela\n";
		 * */
		descricao = "Option A: \n " +
				" Group all the classes in a unique table \n" +
				"Option B: \n" +
				" Each subclass becomes a class and the superclass disappears \n" +
				"Option C: \n" +
				" Each class becomes a table \n";
		textarea.append(descricao);	
		/*
		 * Opcao A: agrupa todas as classes num 'tabelao'
		 * Opcao B: cada subclasse vira uma classe e a classe mãe some
		 * Opcao C: cada classe vira uma tabela
		 * 
		 * */
		
		final ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(aRadio);
		radioGroup.add(bRadio);
		radioGroup.add(cRadio);
			
		
		okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event)
			{      
				if(aRadio.isSelected())
					op = 'A';
				if(bRadio.isSelected())
					op = 'B';
				if(cRadio.isSelected())
					op = 'C';                     
				dispose();
			}           
		});
		
		contentPane.add( okBtn );
		
		setLocation( 300, 270 );
		setVisible(true);
		this.setModal(true);  

	}
	
	public char getOp()
	{
		return op;
	}
	
	
	
	public static void main(String[] args) {
		// TODO code application logic here
		JFrame m = new JFrame();
		JanelaGeneralizacao app = new JanelaGeneralizacao(m, "Casa");
		System.exit(0);             
	}
}
