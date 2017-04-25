// Copyright (c) 1996-2002 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.ui;

import org.argouml.application.api.Argo;
import org.argouml.kernel.*;
import org.argouml.ui.*;
import org.argouml.util.*;
import org.argouml.util.osdep.*;
// Acrescentado por Valério Moysés Vilela - TerraUFV
import org.argouml.mga.shape.shapefile.*;
import org.argouml.mga.shape.dbffile.*;
// fim do acrescimo
import org.tigris.gef.ocl.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class ActionGenerateShape extends ActionSaveXMI {
    
    ////////////////////////////////////////////////////////////////
    // static variables
    
    public static ActionGenerateShape SINGLETON = new ActionGenerateShape();
    
    public static final String separator = "/"; //System.getProperty("file.separator");
    
    ////////////////////////////////////////////////////////////////
    // private members
    
    // por Valério Moysés Vilela - TerraUFV
    private NodeList geoObjects, associations, packages,
    nonGeoObjects, netObjects; //, dataTypes, geoFields;
    
    private DocumentBuilder builder;
    private Document doc;
    private DocumentBuilderFactory factory;
    ////////////////////////////////////////////////////////////////
    // constructors
    
    protected ActionGenerateShape() {
        super("Generate Shapefile...", NO_ICON);
    }
    
    
    ////////////////////////////////////////////////////////////////
    // main methods
    
    public void actionPerformed(ActionEvent e) {
        trySave(false);
    }
    
    /**
     * Funcao para buscar um elemento no documento pelo seu id
     * @param e o elemento sendo analisado atualmente
     * @param id string que contem o id do elemento sendo procurado
     * @return o elemento sendo pesquisado ou null, caso ele nao exista
     */
    private Node getElementById( Node e, String id ) {
        if( e.hasAttributes() ) {
            NamedNodeMap attrs = e.getAttributes();
            for( int a = 0; a < attrs.getLength(); a++ ) {
                Node n = attrs.item( a );
                if( n.getNodeName().equalsIgnoreCase( "xmi.id" ) &&
                n.getNodeValue().equalsIgnoreCase( id ) )
                    return e;
            }
        }
        Node ret = null;
        if( e.hasChildNodes() ) {
            NodeList childs = e.getChildNodes();
            int i = 0;
            while( ret == null && i < childs.getLength() ) {
                ret = getElementById( childs.item( i ), id );
                i++;
            }
        }
        return ret;
    }
    
    public boolean getMultiplicity( Element m ) {
        String lower, upper;
        NodeList l = m.getElementsByTagName( "Foundation.Data_Types.MultiplicityRange.lower" );
        NodeList u = m.getElementsByTagName( "Foundation.Data_Types.MultiplicityRange.upper" );
        
        if( l.getLength() == 0 ) {
            Node ref = m.getElementsByTagName( "Foundation.Data_Types.Multiplicity" ).item(0);
            NamedNodeMap attrs = ref.getAttributes();
            for( int a = 0; a < attrs.getLength(); a++ ) {
                Node n = attrs.item( a );
                if( n.getNodeName().equalsIgnoreCase( "xmi.idref" ) ) 
                    return getMultiplicity( (Element)( getElementById( doc.getDocumentElement(), n.getNodeValue() ) ) );
            }
        }

        String up = ( (Text)( u.item(0).getFirstChild() ) ).getData().trim();
        if( up.equals( "-1" ) )
            return true;
        else 
            return false;
    }
    
    public void makeAssocMN( String obj1, String obj2, File dir ) {
        Element classe1 = (Element)getElementById( doc.getDocumentElement(), obj1 );
        Element classe2 = (Element)getElementById( doc.getDocumentElement(), obj2 );

        NodeList nomeElem1 = classe1.getElementsByTagName( "Foundation.Core.ModelElement.name" );
        NodeList nomeElem2 = classe2.getElementsByTagName( "Foundation.Core.ModelElement.name" );
        String nome1 = nomeElem1.item(0).getFirstChild().getNodeValue();
        String nome2 = nomeElem2.item(0).getFirstChild().getNodeValue();
        DBFFile arq = new DBFFile( dir.toString() + "/" + nome1 + nome2 + ".dbf" );
        boolean teste = arq.createNew();

        NodeList attrs1 = classe1.getElementsByTagName( "Foundation.Core.Attribute" );
        for( int i = 0; i < attrs1.getLength(); i++ ) {
            Element attr = (Element)( attrs1.item(i) );
            NodeList tags = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.tag" );
            NodeList tagValues = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.value" );
            for( int j = 2; j < tags.getLength(); j++ ) {
                String tag = ( (Text)( tags.item(j).getFirstChild() ) ).getData().trim();
                String tagValue = ( (Text)( tagValues.item(j).getFirstChild() ) ).getData().trim();

                if( tag.equalsIgnoreCase( "primary key" ) && tagValue.equalsIgnoreCase( "true" ) ) {
                    NodeList attrChilds = attrs1.item( i ).getChildNodes();
                    String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
                    Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
                    String ref = no.getAttributes().item(0).getNodeValue();
                    Node dataType = getElementById( doc.getDocumentElement(), ref );
                    NodeList dataTypeChilds = dataType.getChildNodes();
                    String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
                    if( attrType.equalsIgnoreCase( "int" ) )
                        arq.newField( attrNome, (byte)'N', (byte)4, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "double" ) ||
                    attrType.equalsIgnoreCase( "float" ) )
                        arq.newField( attrNome, (byte)'O', (byte)8, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "boolean" ) ||
                    attrType.equalsIgnoreCase( "bool" ) )
                        arq.newField( attrNome, (byte)'L', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "char" ) )
                        arq.newField( attrNome, (byte)'C', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "String" ) )
                        arq.newField( attrNome, (byte)'C', (byte)40, (byte)0 );
                }
            }
        }
        NodeList attrs2 = classe2.getElementsByTagName( "Foundation.Core.Attribute" );
        for( int i = 0; i < attrs2.getLength(); i++ ) {
            Element attr = (Element)( attrs2.item(i) );
            NodeList tags = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.tag" );
            NodeList tagValues = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.value" );
            for( int j = 2; j < tags.getLength(); j++ ) {
                String tag = ( (Text)( tags.item(j).getFirstChild() ) ).getData().trim();
                String tagValue = ( (Text)( tagValues.item(j).getFirstChild() ) ).getData().trim();

                if( tag.equalsIgnoreCase( "primary key" ) && tagValue.equalsIgnoreCase( "true" ) ) {
                    NodeList attrChilds = attrs2.item( i ).getChildNodes();
                    String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
                    Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
                    String ref = no.getAttributes().item(0).getNodeValue();
                    Node dataType = getElementById( doc.getDocumentElement(), ref );
                    NodeList dataTypeChilds = dataType.getChildNodes();
                    String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
                    if( attrType.equalsIgnoreCase( "int" ) )
                        arq.newField( attrNome, (byte)'N', (byte)4, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "double" ) ||
                    attrType.equalsIgnoreCase( "float" ) )
                        arq.newField( attrNome, (byte)'O', (byte)8, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "boolean" ) ||
                    attrType.equalsIgnoreCase( "bool" ) )
                        arq.newField( attrNome, (byte)'L', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "char" ) )
                        arq.newField( attrNome, (byte)'C', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "String" ) )
                        arq.newField( attrNome, (byte)'C', (byte)40, (byte)0 );
                }
            }
        }
        arq.insert();
        arq.close();
    }
    
    public void makeAssociation( ShapeFile s, String obj ) {
        Element classe = (Element)getElementById( doc.getDocumentElement(), obj );
        
        NodeList attrs = classe.getElementsByTagName( "Foundation.Core.Attribute" );
        for( int i = 0; i < attrs.getLength(); i++ ) {
            Element attr = (Element)( attrs.item(i) );
            NodeList tags = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.tag" );
            NodeList tagValues = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.value" );
            for( int j = 2; j < tags.getLength(); j++ ) {
                String tag = ( (Text)( tags.item(j).getFirstChild() ) ).getData().trim();
                String tagValue = ( (Text)( tagValues.item(j).getFirstChild() ) ).getData().trim();

                if( tag.equalsIgnoreCase( "primary key" ) && tagValue.equalsIgnoreCase( "true" ) ) {
                    NodeList attrChilds = attrs.item( i ).getChildNodes();
                    String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
                    Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
                    String ref = no.getAttributes().item(0).getNodeValue();
                    Node dataType = getElementById( doc.getDocumentElement(), ref );
                    NodeList dataTypeChilds = dataType.getChildNodes();
                    String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
                    if( attrType.equalsIgnoreCase( "int" ) )
                        s.newField( attrNome, 'N', (byte)4, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "double" ) ||
                    attrType.equalsIgnoreCase( "float" ) )
                        s.newField( attrNome, 'O', (byte)8, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "boolean" ) ||
                    attrType.equalsIgnoreCase( "bool" ) )
                        s.newField( attrNome, 'L', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "char" ) )
                        s.newField( attrNome, 'C', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "String" ) )
                        s.newField( attrNome, 'C', (byte)40, (byte)0 );
                }
            }
        }
    }

    public void makeAssociation( DBFFile s, String obj ) {
        Element classe = (Element)getElementById( doc.getDocumentElement(), obj );
        
        NodeList attrs = classe.getElementsByTagName( "Foundation.Core.Attribute" );
        for( int i = 0; i < attrs.getLength(); i++ ) {
            Element attr = (Element)( attrs.item(i) );
            NodeList tags = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.tag" );
            NodeList tagValues = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue.value" );
            for( int j = 2; j < tags.getLength(); j++ ) {
                String tag = ( (Text)( tags.item(j).getFirstChild() ) ).getData().trim();
                String tagValue = ( (Text)( tagValues.item(j).getFirstChild() ) ).getData().trim();

                if( tag.equalsIgnoreCase( "primary key" ) && tagValue.equalsIgnoreCase( "true" ) ) {
                    NodeList attrChilds = attrs.item( i ).getChildNodes();
                    String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
                    Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
                    String ref = no.getAttributes().item(0).getNodeValue();
                    Node dataType = getElementById( doc.getDocumentElement(), ref );
                    NodeList dataTypeChilds = dataType.getChildNodes();
                    String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
                    if( attrType.equalsIgnoreCase( "int" ) )
                        s.newField( attrNome, (byte)'N', (byte)4, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "double" ) ||
                    attrType.equalsIgnoreCase( "float" ) )
                        s.newField( attrNome, (byte)'O', (byte)8, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "boolean" ) ||
                    attrType.equalsIgnoreCase( "bool" ) )
                        s.newField( attrNome, (byte)'L', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "char" ) )
                        s.newField( attrNome, (byte)'C', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "String" ) )
                        s.newField( attrNome, (byte)'C', (byte)40, (byte)0 );
                }
            }
        }
    }

    public void genNonGeoObj( Node n, File d ) {
        // Obtem o ID do objeto sendo analisado para procurar por ele nos relacionamentos
        String objId = n.getAttributes().item(0).getNodeValue();
        // Obtem a lista de atributos declarados no objeto sendo analisado
        NodeList attrs = ((Element)n).getElementsByTagName( "Foundation.Core.Attribute" );
        // Obtem o nome do objeto sendo analisado (nome do arquivo a ser gerado)
        NodeList nomeElem = ((Element)n).getElementsByTagName( "Foundation.Core.ModelElement.name" );
        String nome = nomeElem.item(0).getFirstChild().getNodeValue();

        // Jah tenho o nome da classe 
        // Criar os arquivos dbf e pesquisar os atributos da classe

        DBFFile arq = new DBFFile( d.toString() + "/" + nome + ".DBF" );
        arq.createNew();
        
        // Buscar os atributos do objeto
        for( int v = 0; v < attrs.getLength(); v++ ) {
            NodeList attrChilds = attrs.item( v ).getChildNodes();
            String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
            Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
            String ref = no.getAttributes().item(0).getNodeValue();
            Node dataType = getElementById( doc.getDocumentElement(), ref );
            NodeList dataTypeChilds = dataType.getChildNodes();
            String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
            if( attrType.equalsIgnoreCase( "int" ) )
                arq.newField( attrNome, (byte)'N', (byte)4, (byte)0 );
            else if( attrType.equalsIgnoreCase( "double" ) ||
            attrType.equalsIgnoreCase( "float" ) )
                arq.newField( attrNome, (byte)'O', (byte)8, (byte)0 );
            else if( attrType.equalsIgnoreCase( "boolean" ) ||
            attrType.equalsIgnoreCase( "bool" ) )
                arq.newField( attrNome, (byte)'L', (byte)1, (byte)0 );
            else if( attrType.equalsIgnoreCase( "char" ) )
                arq.newField( attrNome, (byte)'C', (byte)1, (byte)0 );
            else if( attrType.equalsIgnoreCase( "String" ) )
                arq.newField( attrNome, (byte)'C', (byte)40, (byte)0 );
        }

        // Procura por possiveis associacoes desse objeto
        for( int z = 0; z < associations.getLength(); z++ ) {
            Element assoc = (Element)associations.item( z );
            if( assoc.hasAttribute( "xmi.id" ) ) {
                NodeList ends = assoc.getElementsByTagName( "Foundation.Core.AssociationEnd.type" );
                NodeList multiplicities = assoc.getElementsByTagName( "Foundation.Core.AssociationEnd.multiplicity" );

                String end1 = ends.item(0).getChildNodes().item(1).getAttributes().item(0).getNodeValue();
                boolean mult1 = getMultiplicity( (Element)multiplicities.item(0) );
                String end2 = ends.item(1).getChildNodes().item(1).getAttributes().item(0).getNodeValue();
                boolean mult2 = getMultiplicity( (Element)multiplicities.item(1) );

                if( end2.equals( objId ) && !mult1 ) 
                    makeAssociation( arq, end1 );
                else if( end1.equals( objId ) && !mult2 && mult1 )
                    makeAssociation( arq, end2 );
                else if( end2.equals( objId ) && mult1 && mult2 )
                    makeAssocMN( end1, end2, d );
            }
        }
        arq.insert();
        arq.close();
    }

    public void genGeoObj( Node n, File d ) {
        // Obtem o ID do objeto sendo analisado para procurar por ele nos relacionamentos
        String objId = n.getAttributes().item(0).getNodeValue();
        // Obtem a lista de atributos declarados no objeto sendo analisado
        NodeList attrs = ((Element)n).getElementsByTagName( "Foundation.Core.Attribute" );
        // Obtem o nome do objeto sendo analisado (nome do arquivo a ser gerado)
        NodeList nomeElem = ((Element)n).getElementsByTagName( "Foundation.Core.ModelElement.name" );
        String nome = nomeElem.item(0).getFirstChild().getNodeValue();
        boolean [] shapeType = new boolean[4];
        int p = 0;

        NodeList childs = n.getChildNodes();
        for( int i = 13; i < 20; i = i + 2 ) {
            NamedNodeMap values = childs.item( i ).getAttributes();
            if( values.item( 0 ).getNodeValue().equals( "true" ) )
                shapeType[p] = true;
            else
                shapeType[p] = false;
            p++;
        }
        // Jah tenho o nome da classe e o tipo de representaçao espacial
        // Criar os arquivos shape e pesquisar os atributos da classe
        int contRep = 0;
        for( int j = 0; j < 3; j++ )
            if( shapeType[j] )
                contRep++;

        if( contRep == 0 && shapeType[3] == false ) {
            System.out.println( "Objetos geográficos devem ter pelo menos um tipo " +
            "de representaçao espacial especificado" );
            return;
        }
        ShapeFile shape;
        int tipo = 1;
        for( int k = 0; k < 3; k++ ) {
            if( shapeType[k] ) {
                if( contRep > 1 )
                    shape = new ShapeFile( tipo, nome + Integer.toString( tipo ), d.toString() + "/" );
                else
                    shape = new ShapeFile( tipo, nome, d.toString() + "/" );

                // Buscar os atributos do objeto
                for( int v = 0; v < attrs.getLength(); v++ ) {
                    NodeList attrChilds = attrs.item( v ).getChildNodes();
                    String attrNome = attrChilds.item( 1 ).getFirstChild().getNodeValue();
                    Node no = attrChilds.item( 15 ).getFirstChild().getNextSibling();
                    String ref = no.getAttributes().item(0).getNodeValue();
                    Node dataType = getElementById( doc.getDocumentElement(), ref );
                    NodeList dataTypeChilds = dataType.getChildNodes();
                    String attrType = dataTypeChilds.item( 1 ).getFirstChild().getNodeValue();
                    if( attrType.equalsIgnoreCase( "int" ) )
                        shape.newField( attrNome, 'N', (byte)4, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "double" ) ||
                    attrType.equalsIgnoreCase( "float" ) )
                        shape.newField( attrNome, 'O', (byte)8, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "boolean" ) ||
                    attrType.equalsIgnoreCase( "bool" ) )
                        shape.newField( attrNome, 'L', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "char" ) )
                        shape.newField( attrNome, 'C', (byte)1, (byte)0 );
                    else if( attrType.equalsIgnoreCase( "String" ) )
                        shape.newField( attrNome, 'C', (byte)40, (byte)0 );
                }

                // Procura por possiveis associacoes desse objeto
                for( int z = 0; z < associations.getLength(); z++ ) {
                    Element assoc = (Element)associations.item( z );
                    if( assoc.hasAttribute( "xmi.id" ) ) {
                        NodeList ends = assoc.getElementsByTagName( "Foundation.Core.AssociationEnd.type" );
                        NodeList multiplicities = assoc.getElementsByTagName( "Foundation.Core.AssociationEnd.multiplicity" );

                        String end1 = ends.item(0).getChildNodes().item(1).getAttributes().item(0).getNodeValue();
                        boolean mult1 = getMultiplicity( (Element)multiplicities.item(0) );
                        String end2 = ends.item(1).getChildNodes().item(1).getAttributes().item(0).getNodeValue();
                        boolean mult2 = getMultiplicity( (Element)multiplicities.item(1) );

                        if( end2.equals( objId ) && !mult1 ) 
                            makeAssociation( shape, end1 );
                        else if( end1.equals( objId ) && !mult2 && mult1 )
                            makeAssociation( shape, end2 );
                        else if( end2.equals( objId ) && mult1 && mult2 )
                            makeAssocMN( end1, end2, d );
                    }
                }
                shape.save();
            }
            tipo = tipo + 2;
        }
        // Verifica se o objeto eh do tipo complexo
        if( shapeType[3] ) {
            System.out.println( "Nao eh possivel fazer o mapeamento automatico de " +
            "objetos complexos" );
            //return;
        }
    }

    public boolean trySave( boolean overwrite, File f ) {
        File dir = getNewFile();
        if( dir == null )
            return false;
        boolean success = true;
        
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse( f );
            
            Element raiz = doc.getDocumentElement();
            //dataTypes = raiz.getElementsByTagName( "Foundation.Core.DataType" );
            packages = raiz.getElementsByTagName( "Model_Management.Package" );
            geoObjects = raiz.getElementsByTagName( "Foundation.Core.GeographicObject" );
            nonGeoObjects = raiz.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
            netObjects = raiz.getElementsByTagName( "Foundation.Core.NetworkObject" );
            //geoFields = raiz.getElementsByTagName( "Foundation.Core.GeographicFields" );
            associations = raiz.getElementsByTagName( "Foundation.Core.Association" );
            
            for( int i = 0; i < geoObjects.getLength(); i++ ) {
                genGeoObj( geoObjects.item( i ), dir );
            }
            for( int j = 0; j < nonGeoObjects.getLength(); j++ ) {
                genNonGeoObj( nonGeoObjects.item( j ), dir );
            }
            
        } catch( ParserConfigurationException pce ) {
            pce.printStackTrace();
            success = false;
        } catch( SAXException sax ) {
            sax.printStackTrace();
            success = false;
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            success = false;
        }
        return success;
    }
    
    public boolean trySave(boolean overwrite) {
        ProjectBrowser pb = ProjectBrowser.TheInstance;
        Project p = pb.getProject();
        
        boolean success = false;
        File temp = null;
        try {
            temp = temp.createTempFile( "temp", ".xmi" );
            
            p.saveXMI( true, temp);
            success = trySave( true, temp );
            
        } catch( IOException e ) {
        } catch( Exception exc ) {
        }
        
        if (success) {
            ProjectBrowser.TheInstance.updateTitle();
        }
        temp.delete();          // apaga o arquivo .xmi temporario
        return success;
    }
    
    protected File getNewFile() {
        ProjectBrowser pb = ProjectBrowser.TheInstance;
        Project p = pb.getProject();
        
        JFileChooser chooser = null;
        URL url = p.getURL();
/*        if ((url != null) && (url.getFile().length()>0)) {
            chooser  = OsUtil.getFileChooser (url.getFile());
        }*/
        if (chooser == null) {
            chooser  = OsUtil.getFileChooser();
        }
        
        if (url != null) {
            chooser.setSelectedFile(new File(url.getFile()));
        }
        
        String sChooserTitle =
        Argo.localize("Actions",
        "text.save_as_project.chooser_title");
        chooser.setDialogTitle( "Select the destination directory" );
        //chooser.setFileFilter(FileFilters.ShapefileFilter);
        chooser.setFileFilter(new
        javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
            public String getDescription() {
                return "Directories";
            }
        });
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        
        int retval = chooser.showSaveDialog(pb);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                String name = file.getName();
            }
            return file;
        } else {
            return null;
        }
    }
    
    public boolean shouldBeEnabled() {
        return true;
    }
} /* end class ActionGenerateShape*/