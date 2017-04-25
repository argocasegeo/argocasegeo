/*
 * testeXMI.java
 *
 * Created on 4 de Agosto de 2004, 09:01
 */

package org.argouml.mga.shape;

import org.apache.xerces.parsers.DOMParser;
//import org.apache.xpath.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.argouml.mga.shape.shapefile.*;
import org.argouml.mga.shape.dbffile.*;

/**
 *
 * @author  Valério
 */
public class testeXMI {
    
    private static Document doc;

    /** Creates a new instance of testeXMI */
    public testeXMI() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments( true );

            builder = factory.newDocumentBuilder();
            doc = builder.parse( new File( "C:/users/valerio/Projeto - ArgoUML/Codigo/hidro.xmi" ) );
            
            Element raiz = doc.getDocumentElement();
            NodeList geoObjects = raiz.getElementsByTagName( "Foundation.Core.GeographicObject" );
            NodeList nonGeoObjects = raiz.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
            NodeList dataTypes = raiz.getElementsByTagName( "Foundation.Core.DataType" );
            NodeList associations = raiz.getElementsByTagName( "Foundation.Core.Association" );
            NodeList packages = raiz.getElementsByTagName( "Model_Management.Package" );

            for( int i = 0; i < geoObjects.getLength(); i++ ) {
                genShape( geoObjects.item( i ) );
                //analyseElement( geoObjects.item( i ) );
                //System.out.println();
            }

            /*System.out.println( "Aeeeeeeeeeeeeeeeeeee" );
            Node s = getElementById( raiz, "xmi.7" );
            if( s != null )
                analyseElement( s );*/
            
            //genShape( geoObjects.item( 0 ) );

        } catch( IOException ioe ) {
            ioe.printStackTrace();
        } catch( ParserConfigurationException pce ) {
            pce.printStackTrace();
        } catch( SAXException se ) {
            se.printStackTrace();
        }
    }
    
    public static void genShape( Node n ) {
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
                    shape = new ShapeFile( tipo, nome + Integer.toString( tipo ), "./" );
                else
                    shape = new ShapeFile( tipo, nome, "./" );

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
                //for( int z = 0; z < associations.getLength(); z++ ) {
                    
                //}
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
    
    public static void analyseElement( Node n ) {
        switch( n.getNodeType() ) {
            case Node.TEXT_NODE:
                String data = ((Text)n).getData().trim();
                if( data.length() != 0 ) {
                    System.out.print( ((Text)n).getNodeName() + ": " );
                    System.out.println( data );
                }
                break;
            case Node.ELEMENT_NODE:
                System.out.println( n.getNodeName() );
                NamedNodeMap atributos = n.getAttributes();
                if( atributos.getLength() != 0 ) {
                    for( int j = 0; j < atributos.getLength(); j++ ) {
                        Node atributo = atributos.item( j );
                        System.out.println( atributo.getNodeName() + " = " + atributo.getNodeValue() );
                    }
                }
                NodeList childs = n.getChildNodes();
                if( childs.getLength() != 0 )
                    for( int i = 0; i < childs.getLength(); i++ )
                        analyseElement( childs.item( i ) );
                break;
            default:
                System.out.println( n.getNodeType() );
        }
    }
    
    /**
     * Funcao para buscar um elemento no documento pelo seu id
     * @param e o elemento sendo analisado atualmente
     * @param id string que contem o id do elemento sendo procurado
     * @return o elemento sendo pesquisado ou null, caso ele nao exista
     */
    public static Node getElementById( Node e, String id ) {
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
}
