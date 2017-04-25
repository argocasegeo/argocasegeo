/*
 * Dbms.java
 *Alexandre Gazola
 *
 * Created on 25 de Outubro de 2004, 11:48
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
public class Dbms extends JDialog{    
   
    
    /** Creates a new instance of Dbms */
    public Dbms(JFrame m) {     
       
        super(m, "Database Management System", true);
      
        setSize( 410, 130 );
             
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        
        label = new JLabel( "Select a DBMS: ");
        contentPane.add( label );
        
        dbmsCombo = new JComboBox();
        dbmsCombo.setEditable(false);
        dbmsCombo.addItem("Microsoft Access (*.mdb)");
        dbmsCombo.addItem("Microsoft SQL Server");
        dbmsCombo.addItem("Oracle");
        dbmsCombo.addItem("MySQL");
        dbmsCombo.addItem("PostgreSQL");
        contentPane.add(dbmsCombo);
                
        textField = new JTextField(30);
        contentPane.add(textField);
        dirBtn = new JButton("Select");
        dirBtn.addActionListener( 
           new ActionListener()
        {
            public void actionPerformed( ActionEvent event)
            {
                openFile();
                
            }
        }
        );
        contentPane.add(dirBtn);
        
        okBtn = new JButton("OK");
        okBtn.addActionListener(
            new ActionListener()
            {
                  public void actionPerformed( ActionEvent event)
                  {                    
                    dispose();
                  }           
                 
           }
        );
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(
            new ActionListener()
            {
                  public void actionPerformed( ActionEvent event)
                  {
                      fileName = "";
                      dispose();
                  }           
                 
           }
        );
        
        
        contentPane.add( okBtn );
        contentPane.add( cancelBtn );
        
        
        Toolkit tk = Toolkit.getDefaultToolkit(); 
        Dimension screenSize = tk.getScreenSize(); 
        //  Centralizando 
        setLocation((screenSize.width -  getSize().width) / 2, 
         (screenSize.height - getSize().height) / 2); 
        setVisible(true);
        this.setResizable(false);
        this.setModal(true);
      
                
        
    }
    public String getDir()
    {
        return fileName;
    }
    
    public int getBD()
    {
        String s = (String)dbmsCombo.getSelectedItem();
        if( s.equals("Microsoft Access (*.mdb)"))
            return 0;
        else if (s.equals("Microsoft SQL Server"))
            return 1;
        else if (s.equals("Oracle"))
            return 2;
        
        else if (s.equals("MySQL"))
        {
            JOptionPane.showMessageDialog(null, "Unavailable option at the moment.\n Wait for future releases.\nDefault selected.");
            return 3;
        }
        else if (s.equals("PostgreSQL"))
        {   
            JOptionPane.showMessageDialog(null, "Unavailable option at the moment\n. Wait for future releases.\nDefault selected.");
            return 4;
        }
        return -1;
            
    }
       
    private void openFile()
    {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setDialogTitle("Select the destination directory");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = dirChooser.showOpenDialog(null);
        if( result == JFileChooser.CANCEL_OPTION)
            return;
        
        File dirName = dirChooser.getSelectedFile(); 
        fileName = dirName.getAbsolutePath();
        textField.setText(fileName);
        fileName = dirName.getAbsolutePath();
        
        
    }  

        private JComboBox dbmsCombo;
        private JLabel label;
        private String op;
        private JButton okBtn;
        private JButton cancelBtn;
        private JButton dirBtn;
        private JTextField textField;
        private String fileName;
        

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame m = new JFrame();
        Dbms app = new Dbms(m);
       
        
    }
    
}
