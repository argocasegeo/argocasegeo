/*
 * testeShape.java
 *
 * Created on 2 de Agosto de 2004, 17:36
 */

package org.argouml.mga.shape.shapefile;

/**
 *
 * @author  Valério
 */
public class testeShape {
    
    /** Creates a new instance of testeShape */
    public testeShape() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ShapeFile shape = new ShapeFile( 1, "benga", "./" );
        shape.newField( "Nome", 'C', (byte)25, (byte)0 );
        shape.newField( "Tipo", 'C', (byte)30, (byte)0 );
        shape.newField( "id", 'N', (byte)4, (byte)0 );
        shape.newField( "Desc", 'C', (byte)50, (byte)0 );
        
        boolean teste = shape.save();
        if( !teste )
            System.out.println( "ERRO AO GRAVAR O ARQUIVO." );
        
        //boolean teste = shape.open( "./", "ROADS" );

        /*if( !teste ) {
            System.out.println( "ERRO AO ABRIR O ARQUIVO." );
            return;
        }*/
        
        /*boolean lido = shape.load();
        if( !lido ) {
            System.out.println( "ERRO AO LER O ARQUIVO." );
            return;
        }*/
    }
    
}
