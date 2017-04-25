/*
 * testeDBF.java
 *
 * Created on 29 de Julho de 2004, 14:23
 */

package org.argouml.mga.shape.dbffile;

import java.util.Vector;
/**
 *
 * @author  Valerio
 */

public class testeDBF {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBFFile arq = new DBFFile( "ROADS.DBF" );
        DBFFile arq2 = new DBFFile( "benga.dbf" );
        boolean teste2 = arq2.createNew();
        boolean teste = arq.load();
        Vector campos;
        DBField field;
        
        if( !teste ) {
            System.out.println( "ERRO AO ABRIR O ARQUIVO" );
            return;
        }
        
        campos = new Vector( arq.getFields() );
        //System.out.println( campos.size() );
        for( int i = 0; i < campos.size(); i++ ) {
            field = (DBField)campos.get( i );
            System.out.println( "Nome: " + field.getName() +
                                "\nTipo: " + field.getFType() +
                                "\nTamanho: " + field.getFLength() + 
                                "\nOffset: " + field.getOffset() +
                                "\nFDec: " + field.getFDec() );
            arq2.newField( field.getName(), (byte)field.getFType(), 
                           (byte)field.getFLength(), (byte)field.getFDec() ) ;
        }

        /*field = (DBField)campos.get(0);
        field.setName( "BENGA" );
        field = null;
        DBField field2 = (DBField)campos.get(0);
        System.out.println( "Novo nome: " + field2.getName() );*/
        
        arq2.insert();
        //arq.newField( "BENGA", (byte)'C', (byte)20, (byte)0 );
        arq.delete( 321 );
        arq.close();
        arq2.close();
    }
    
}
