/**
   @version 1.0 2001-09-01
   @author Cay Horstmann
*/

package org.argouml.mga.shape.shapefile.testes;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
   This program displays an XML document as a tree.
*/
public class DOMTreeTest
{ 
   public static void main(String[] args)
   {  
      JFrame frame = new DOMTreeFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.show();
   }
}

/**
   This frame contains a tree that displays the contents of
   an XML document.
*/
class DOMTreeFrame extends JFrame
{  
   public DOMTreeFrame()
   {  
      setTitle("DOMTreeTest");
      setSize(WIDTH, HEIGHT);

      JMenu fileMenu = new JMenu("File");
      JMenuItem openItem = new JMenuItem("Open");
      openItem.addActionListener(new
         ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               openFile();
            }
         });
      fileMenu.add(openItem);

      JMenuItem exitItem = new JMenuItem("Exit");
      exitItem.addActionListener(new
         ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               System.exit(0);
            }
         });
      fileMenu.add(exitItem);

      JMenuBar menuBar = new JMenuBar();
      menuBar.add(fileMenu);
      setJMenuBar(menuBar);
   }

   /**
      Open a file and load the document.
   */
   public void openFile()
   {  
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));

      chooser.setFileFilter(new
         javax.swing.filechooser.FileFilter()
         {  
            public boolean accept(File f)
            {  
               return f.isDirectory()
                  || f.getName().toLowerCase().endsWith(".xml");
            }
            public String getDescription()
            {  
               return "XML files";
            }
         });
      int r = chooser.showOpenDialog(this);
      if (r != JFileChooser.APPROVE_OPTION) return;
      File f = chooser.getSelectedFile();
      try
      {
         if (builder == null)
         {
            DocumentBuilderFactory factory 
               = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
         }

         Document doc = builder.parse(f);
         JTree tree = new JTree(new DOMTreeModel(doc));
         tree.setCellRenderer(new DOMTreeCellRenderer());

         setContentPane(new JScrollPane(tree));
         validate();
      }
      catch (IOException exception)
      {
         JOptionPane.showMessageDialog(this, exception);
      }
      catch (ParserConfigurationException exception)
      {
         JOptionPane.showMessageDialog(this, exception);
      }
      catch (SAXException exception)
      {
         JOptionPane.showMessageDialog(this, exception);
      }
   }

   private DocumentBuilder builder;
   private static final int WIDTH = 400;
   private static final int HEIGHT = 400;
}

/**
   This tree model describes the tree structure of a Java
   object. Children are the objects that are stored in instance
   variables.
*/
class DOMTreeModel implements TreeModel
{ 
   /**
      Constructs an empty tree.
   */
   public DOMTreeModel(Document aDoc)
   {  
      doc = aDoc;
   }

   public Object getRoot()
   {  
      return doc.getDocumentElement();
   }

   public int getChildCount(Object parent)
   {  
      Node node = (Node)parent;
      NodeList list = node.getChildNodes();
      return list.getLength();
   }

   public Object getChild(Object parent, int index)
   {  
      Node node = (Node)parent;
      NodeList list = node.getChildNodes();
      return list.item(index);
   }

   public int getIndexOfChild(Object parent, Object child)
   {  
      Node node = (Node)parent;
      NodeList list = node.getChildNodes();
      for (int i = 0; i < list.getLength(); i++)
         if (getChild(node, i) == child)
            return i;
      return -1;
   }

   public boolean isLeaf(Object node)
   {  
      return getChildCount(node) == 0;
   }

   public void valueForPathChanged(TreePath path, 
      Object newValue) {}

   public void addTreeModelListener(TreeModelListener l) {}

   public void removeTreeModelListener(TreeModelListener l) {}

   private Document doc;
}

/**
   This class renders a class name either in plain or italic.
   Abstract classes are italic.
*/
class DOMTreeCellRenderer extends DefaultTreeCellRenderer
{  
   public Component getTreeCellRendererComponent(JTree tree,
      Object value, boolean selected, boolean expanded,
      boolean leaf, int row, boolean hasFocus)
   {  
      super.getTreeCellRendererComponent(tree, value,
         selected, expanded, leaf, row, hasFocus);
      Node node = (Node)value;
      if (node instanceof Element)
         setText(elementString((Element)node));
      else if (node instanceof CharacterData)
         setText(characterString((CharacterData)node));
      else
         setText(node.getClass() + ": " + node.toString());
      return this;
   }

   public static String elementString(Element e)
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("Element: <");
      buffer.append(e.getTagName());
      NamedNodeMap attributes = e.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
         buffer.append(' ');
         Node attributeNode = attributes.item(i);
         buffer.append(attributeNode.getNodeName());
         System.out.println(e.getTagName() + ": " + attributeNode.getNodeName() + " = " + attributeNode.getNodeValue());
         buffer.append('=');
         buffer.append('"');
         buffer.append(attributeNode.getNodeValue()); 
         buffer.append('"');
      }
      buffer.append('>');
      return buffer.toString();
   }
      
   public static String characterString(CharacterData node)
   {
      StringBuffer buffer = new StringBuffer(node.getData());
      for (int i = 0; i < buffer.length(); i++)
      {
         if (buffer.charAt(i) == '\r') 
         {
            buffer.replace(i, i + 1, "\\r");
            i++;
         }
         else if (buffer.charAt(i) == '\n') 
         {
            buffer.replace(i, i + 1, "\\n");
            i++;
         }
         else if (buffer.charAt(i) == '\t') 
         {
            buffer.replace(i, i + 1, "\\t");
            i++;
         }
      }
      if (node instanceof Text)
         buffer.insert(0, "Text: ");
      else if (node instanceof Comment)
         buffer.insert(0, "Comment: ");
      else if (node instanceof CDATASection)
         buffer.insert(0, "CDATASection: ");
      
      return buffer.toString();
   }
}
