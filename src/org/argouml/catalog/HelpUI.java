package org.argouml.catalog;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class HelpUI extends JDialog {
	
	private JTextArea helpArea;
	
	public HelpUI(JFrame jf) {
		super(jf, "Help");
		
		  setSize( 410, 130 );
        
		  System.out.println("here I am");
	      Container contentPane = getContentPane();
	      contentPane.setLayout(new FlowLayout());
	      
	      helpArea = new JTextArea(400, 600);
	      StringBuffer s = new StringBuffer("-New Analysis Pattern\n\n");
	      s.append("To create a new analysis pattern, it is necessary to\n" +
	      		"have an UML-GeoFrame diagram drawn in your model. Otherwise,\n" +
	      		"an exception will be thrown. After clicking in new, fill in\n" +
	      		"the required fields (in red) and click on finish\n");
	      
	      
	      
	      helpArea.setText(s.toString());
	      contentPane.add(helpArea);
	      
	      helpArea.setVisible(true);
	      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					
					dispose();
				}
			});
	
	}

}
