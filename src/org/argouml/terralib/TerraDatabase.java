/*
 * TerraDatabase.java
 *
 * Alexandre Gazola
 * Created on 27 de Agosto de 2004, 15:35
 * Classe para fazer as manipulações no Banco de Dados TerraLib.
 */

package org.argouml.terralib;

import javax.swing.*;

/**
 *
 * @author  agazola
 *
 */
public final class TerraDatabase {
    
    private String dbName; // nome do banco de dados
    private int dbOp; // SGBD utilizado
    private String dirName; // diretório utilizado
    public boolean ok;
  
    
    // métodos implementados em C++( fazem uso da TerraLib)
    // retornam verdadeiro em caso de sucesso, falso caso contrário
    
    // cria o banco de dados de nome "name" no modelo conceitual do TerraLib
    // op: 0 - Access, 1 - SQL Server, 2 - MySQL, 3 - Oracle, 4: PostGreSQL
    private native String createDatabase(String name, String directory, int op); 
    private native void addViewTheme( String dbName, String directory, String viewName, String[] themes, int op  );
    private native void addLayer( String dbName, String directory, String layerName, int op  );
    private native void addLayerRep( String dbName, String directory,String layerName, String[] rep, int op );
    private native void addAttr( String dbName, String directory, String layerName, String[] attr, String pKey, int op );
    private native void addTable( String dnName, String directory, String tableName, String[] attr, String pKey, int op );
    private native void createRelation( String dbName, String directory, String field, String table1, String table2, String pKey, int op );
    private native void createTable( String dbName, String directory,String table1, String table2, String pKey1, String pKey2, int op );
    private native void createEspA( String dbName, String directory, String parentClass, String primaryKey, String[] desc, String[] representation, String attrs[], String view, int op );  
    private native void createEspB( String dbName, String directory,String parentClass, String primaryKey, String[] parentAttrs, String[] desc, String pkeys[], String[] representation, String attrs[][], String view, int op );  
    private native void createEspC( String dbName, String directory,String parentClass, String primaryKey, String[] parentAttrs, String[] desc, String pkeys[], String[] representation, String attrs[][], String view, int op );  
//TEMP{
    private native void comandoSQL(String dbName, String dirName, String comando, int op);
    private native void addTable2 ( String dnName, String directory, String tableName, String[] attr, String[] pKey, int op );  //para permitir mais de um atributo como chave primaria
    private native void addTableTemporal ( String dnName, String directory, String tableName, String[] attr, String[] pKey, int op, boolean isGeo );
    private native boolean createRelation2( String dbName, String directory, String table1, String table2, String[] pKey, int op );
    private native boolean createTable2( String dbName, String directory,String table1, String table2, String[] pKey1, String[] pKey2, int op, boolean isTemporal );
    private native void addAttr2( String dbName, String directory, String layerName, String[] attr, String[] pKey, int op );
    private native void createEspA2( String dbName, String directory, String parentClass, String[] primaryKey, String[] desc, String[] representation, String attrs[], String view, int op, boolean isGeo, String estereotipoTemporal );
    private native void createEspB2( String dbName, String directory,String parentClass, String[] parentPK, String[] parentAttrs, String[] desc, String pkeys[][], String[][] representation, String attrs[][], String view, int op, boolean isGeo, String estereotipoTemporal );
    private native void createEspC2( String dbName, String directory,String parentClass, String[] primaryKey, String[] parentAttrs, String[] desc, String pkeys[], String[][] representation, String attrs[][], String view, int op, boolean isGeo, String estereotipoTemporal );  
//TEMP}    
    //**********************************************************************************************
    // métodos públicos
    
    // Cria layer
    public void addLayer( String name )
    {
        addLayer( dbName,dirName, name, dbOp );
      
    }
    
    public void addViewTheme( String viewName, String[] themes  )
    {
        
        addViewTheme( dbName,dirName, viewName, themes, dbOp);        
    }  
   
    
    // insere as representaçoes do layer
    public void addLayerRep( String layerName, String[] rep )
    {//...
        addLayerRep( dbName,dirName, layerName, rep, dbOp );
    }
    
    // cria tabela de atributos associadas ao layer
    // formato: nome[0], tipo[1], nome[2], tipo[3], nome[4], ... 
    public void addAttr( String layerName, String[] attr, String pKey )
    {
        addAttr( dbName, dirName,layerName, attr, pKey, dbOp);
        
    }
    
    // cria uma tabela de atributos solta no banco de dados
    
    public void addTable( String tableName, String[] attr, String pKey )
    {
      addTable(dbName,dirName, tableName, attr, pKey,  dbOp );
    } 
    
    public void createRelation( String field, String table1, String table2, String pKey )
    {
        createRelation(dbName,dirName,field, table1, table2, pKey, dbOp );     
    }
    
    public void createTable( String table1, String table2, String pKey1, String pKey2 )
    {
       createTable( dbName,dirName, table1, table2, pKey1,  pKey2, dbOp );
    }
    
    public void createEspA( String parentClass, String primaryKey, String[] desc, String[] representation, String attrs[], String view )
    {
        createEspA( dbName,dirName, parentClass, primaryKey, desc, representation, attrs, view, dbOp );         
    }
    
    public void createEspB( String parentClass, String primaryKey, String parentAttrs[], String[] desc, String[] pkeys, String[] representation, String attrs[][], String view )
    {
        createEspB( dbName,dirName, parentClass, primaryKey, parentAttrs, desc, pkeys, representation, attrs, view, dbOp );         
    }
    
    public void createEspC( String parentClass, String primaryKey, String parentAttrs[], String[] desc, String[] pkeys, String[] representation, String attrs[][], String view )
    {
        createEspC( dbName,dirName, parentClass, primaryKey, parentAttrs, desc, pkeys, representation, attrs, view, dbOp );         
    }
    
    //*******************************************************************************
    // Construtor
    /** Creates a new instance of TerraDatabase */
    public TerraDatabase(String name, String directory, int op) {
        dbName = name;
        dbOp = op;
        dirName = directory;
        String result = createDatabase( dbName, directory, dbOp);
         if( result.equals(name))
            JOptionPane.showMessageDialog(null, "This database already exists.\n Please rename your model and try again.", "ERROR" , JOptionPane.ERROR_MESSAGE);
        else
        {
            ok = true;
            JOptionPane.showMessageDialog(null, "The Automatic Generation was executed successfully!");
        }
    }
    
    public boolean isOk()
    {
      return ok;       
    }
    
//TEMP{
    public void comandoSQL(String comando) {
        comandoSQL(dbName, dirName, comando, dbOp);
    }
    
    //este método é igual ao addTable só que aceita mais de um atributo como chave primária
    public void addTable2( String tableName, String[] attr, String[] pKey )
    {
      addTable2 (dbName,dirName, tableName, attr, pKey,  dbOp );
    }
    //parâmetro isGeo deve ser falso para objetos não geográficos
    public void addTableTemporal( String tableName, String[] attr, String[] pKey , boolean isGeo)
    {
      addTableTemporal (dbName,dirName, tableName, attr, pKey,  dbOp, isGeo );
    }
    public boolean createRelation2(String table1, String table2, String[] pKey )
    {
      boolean isOK = createRelation2(dbName,dirName, table1, table2, pKey, dbOp );
      return isOK;
    }
    public boolean createTable2( String table1, String table2, String[] pKey1, String[] pKey2, boolean isTemporal)
    {
      boolean isOK = createTable2( dbName,dirName, table1, table2, pKey1,  pKey2, dbOp, isTemporal);
      return isOK;
    }
    public void addAttr2( String layerName, String[] attr, String[] pKey )
    {
        addAttr2( dbName, dirName,layerName, attr, pKey, dbOp);
        
    }
    public void createEspA2( String parentClass, String[] primaryKey, String[] desc, String[] representation, String attrs[], String view, boolean isGeo, String estereotipoTemporal )
    {
        createEspA2( dbName,dirName, parentClass, primaryKey, desc, representation, attrs, view, dbOp, isGeo, estereotipoTemporal );         
    }
    public void createEspB2( String parentClass, String[] parentPK, String parentAttrs[], String[] desc, String[][] pkeys, String[][] representation, String attrs[][], String view, boolean isGeo, String estereotipoTemporal )
    {
        createEspB2( dbName,dirName, parentClass, parentPK, parentAttrs, desc, pkeys, representation, attrs, view, dbOp, isGeo, estereotipoTemporal );         
    }
    public void createEspC2( String parentClass, String[] primaryKey, String parentAttrs[], String[] desc, String[] pkeys, String[][] representation, String attrs[][], String view, boolean isGeo, String estereotipoTemporal )
    {
        createEspC2( dbName,dirName, parentClass, primaryKey, parentAttrs, desc, pkeys, representation, attrs, view, dbOp, isGeo, estereotipoTemporal );         
    }
//TEMP}    
    
 
 
    static
    {
           System.loadLibrary( "testeDLL" );
    }
    
    public static void main( String args[] )
    {
        
      TerraDatabase db = new TerraDatabase( "gazola", "C:\\Documents and Settings\\Usuario\\Meus documentos", 0 );
        
    }
    
}
