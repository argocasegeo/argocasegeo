/*
 * GeneraWindow.java
 *
 * Alexandre Gazola
 * Created on 5 de Novembro de 2004, 12:14
 */

package org.argouml.terralib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author  agazola
 */
public class GeneraWindow extends JDialog {
    
    /** Creates a new instance of GeneraWindow */
    /** Creates a new instance of Dbms */
    public GeneraWindow(JFrame m, String parent) {     
       
        super(m, "Specialization of " + parent, true);
      
        setSize( 500, 100 );
             
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        
        label = new JLabel( "Select an option for generalization: ");
        contentPane.add( label );
        
        
        aRadio = new JRadioButton("Option A", true);
        contentPane.add(aRadio);
        bRadio = new JRadioButton("Option B", false);
        contentPane.add(bRadio);
        cRadio = new JRadioButton("Option C", false);
        contentPane.add(cRadio);
        
        final ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(aRadio);
        radioGroup.add(bRadio);
        radioGroup.add(cRadio);
       
              
        okBtn = new JButton("OK");
        okBtn.addActionListener(
            new ActionListener()
            {
                  public void actionPerformed( ActionEvent event)
                  {      
                      if(aRadio.isSelected())
                          op = 'A';
                      if(bRadio.isSelected())
                          op = 'B';
                      if(cRadio.isSelected())
                          op = 'C';                     
                     dispose();
                  }           
                 
           }
        );
               
        
        contentPane.add( okBtn );          
        
        setLocation( 300, 270 );
        setVisible(true);
        this.setResizable(false);
        this.setModal(true);
      
                
        
    }
    public char getOp()
    {
        return op;
    }
    
     

        private JRadioButton aRadio, bRadio, cRadio;
        private JLabel label;
        private char op;
        private JButton okBtn;
     
            
      
        

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame m = new JFrame();
        GeneraWindow app = new GeneraWindow(m, "Casa");
        System.exit(0);
       
        
    }
    
}
