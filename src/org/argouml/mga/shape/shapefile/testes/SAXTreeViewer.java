

package org.argouml.mga.shape.shapefile.testes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

// This is an XML book - no need for explicit Swing imports
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class SAXTreeViewer extends JFrame {
    /** Default parser to use */
    private String vendorParserClass = "org.apache.xerces.parsers.SAXParser";
    /** The base tree to render */
    private JTree jTree;
    /** Tree model to use */
    DefaultTreeModel defaultTreeModel;
    
    public SAXTreeViewer( ) {
        // Handle Swing setup
        super("SAX Tree Viewer");
        setSize(600, 450);
    }
    
    public void init(String xmlURI) throws IOException, SAXException {
        DefaultMutableTreeNode base =
        new DefaultMutableTreeNode("XML Document: " +
        xmlURI);
        // Build the tree model
        defaultTreeModel = new DefaultTreeModel(base);
        jTree = new JTree(defaultTreeModel);
        // Construct the tree hierarchy
        buildTree(defaultTreeModel, base, xmlURI);
        // Display the results
        getContentPane( ).add(new JScrollPane(jTree),
        BorderLayout.CENTER);
    }
    
    public void buildTree(DefaultTreeModel treeModel,
    DefaultMutableTreeNode base, String xmlURI)
    throws IOException, SAXException {
        // Create instances needed for parsing
        XMLReader reader = XMLReaderFactory.createXMLReader(vendorParserClass);
        // Register content handler
        // Register error handler
        // Parse
        InputSource inputSource = new InputSource(xmlURI);
        reader.parse(inputSource);
    }
    
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println(
                "Usage: java javaxml2.SAXTreeViewer " +
                "[XML Document URI]");
                System.exit(0);
            }
            SAXTreeViewer viewer = new SAXTreeViewer( );
            viewer.init(args[0]);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace( );
        }
    }
}