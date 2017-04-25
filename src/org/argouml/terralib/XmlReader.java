/** "Porque Deus amou o mundo de tal maneira que deu o seu filho unig�nito para
 que todo aquele que nele cr� n�o pere�a, mas tenha a vida eterna". Jo. 3:16*/

// @author Alexandre Gazola
// �ltima atualiza�ao em 05/11/2004

package org.argouml.terralib;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*; 
import javax.swing.*;

import javax.swing.JOptionPane;
import java.util.LinkedList;


public class XmlReader {

  // caminho (path) do arquivo XML
  private String xmlPathname;
  // objeto que manipular� o BD
  private TerraDatabase terraDB;
  // diret�rio de gera�ao do BD
  private String directory;
  // janela de dados �teis
  private Dbms window;
  // se tudo ok
  private boolean isOk;
  private char op = 'D'; //armazena o tipo escolhido para geracao de generalizacao

  // construtor que seta o caminho do XML
  public XmlReader(String path) {
      JFrame m = new JFrame();
      window = new Dbms(m);
      directory = window.getDir();;
      if( directory.equals(""))
      {
          directory = "./";
          return;
      }
          
    xmlPathname = path; 
        
  }
 
  // le o XML carregando os dados dos usu�rios em um Vector.
  // retorna o vector contendo os usu�rios cadastrados no XML.
  public void lerXMI() throws Exception { 
  
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    
   Document doc = null;
   
    try{
    doc = db.parse( xmlPathname );  
    }
    catch( Exception e)
    { System.out.println("exce�ao aqui!" + e);}
   
    
    // pega a raiz do documento XML
    Element elem = doc.getDocumentElement();
    
    // pega todos os Modelos do XML, os quais representar�o bancos de dados diferentes
    NodeList nl = elem.getElementsByTagName( "Model_Management.Model" );
    
    // Assumindo apenas 1 banco de dados
    Element model = (Element)nl.item(0); // colocar �ndice gen�rico********
    
    String database = getChildTagValue(model, "Foundation.Core.ModelElement.name");
       
    // Escolhe o banco de dados que ser� utilizado
    int opcao = window.getBD();
 

    // cria o banco de dados  
     terraDB = new TerraDatabase(database, directory, opcao);
     if (!terraDB.isOk())
         return;
    
        
    // packages cont�m todos os pacotes do modelo
    // pacotes ser�o mapeados em vistas num banco TerraLib
    NodeList packages = model.getElementsByTagName( "Model_Management.Package" );
    
     // armazena nome da classe e sua chave prim�ria(nessa ordem)
    List auxiliary = new ArrayList(); // voltar isso para dentro do for do pacote qualquer coisa
    
    
    
    for( int npack = 0; npack < packages.getLength(); npack++ )
    {
    Element pack = (Element)packages.item(npack); // **********colocar �ndice gen�rico
    String view = getChildTagValue(pack, "Foundation.Core.ModelElement.name");
    
    
    // guarda os objetos geogr�ficos deste pacote
    NodeList geoobjs = pack.getElementsByTagName( "Foundation.Core.GeographicObject" );
    // guarda os objetos n�o-geogr�ficos deste pacote
    NodeList ngeoobjs = pack.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
    // guarda os campos geogr�ficos
    NodeList geofields = pack.getElementsByTagName( "Foundation.Core.GeographicField" );
    
    NodeList netobjs = pack.getElementsByTagName( "Foundation.Core.NetworkObject" );
//TEMP{
/*    // guarda as associa��es
    NodeList associations = pack.getElementsByTagName( "Foundation.Core.Association" );
    // guarda as generaliza��es
    NodeList generalizations = pack.getElementsByTagName( "Foundation.Core.Generalization" );

 
    NodeList stereotype = model.getElementsByTagName("Foundation.Extension_Mechanisms.Stereotype");
    //ACHO QUE PODE TIRAR ESTE COMANDO DO FOR - TESTAR DEPOIS */
//TEMP}   
    
    // armazena as classes que possuem especializa�oes
    List specialization = new ArrayList();   
    
    
 
    //********* manipula�ao dos objetos geogr�ficos*************
    String[] themes = new String[geoobjs.getLength()];
    for( int i = 0; i < geoobjs.getLength(); i++ )
    {
        Element geoobj = (Element)geoobjs.item(i);
        String nome = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
//TEMP{
        String nomeTabelaVersoes = nome+"_VERSOES";
//TEMP}        
        
        NodeList list = geoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( list.getLength() > 0)      
            specialization.add(nome);    
        
        NodeList genera = geoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
        NodeList especia = geoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
 
//TEMP// SE CLASSE PARTICIPA DE UMA GENERALIZACAO OU ESPECIALIZACAO NAO CRIA TABELA AGORA 
        if( (genera.getLength() > 0) || (especia.getLength() > 0) )  
        {            
            themes[i] = "";
            continue;
        }
              
          // insere este layer no banco de dados
//          terraDB.addLayer(nome);
//          themes[i] = nome;       
          
        // verifica as representa�oes do objeto
       boolean isPoint, isLine, isPolygon, isComplexObj,isNode,isArc;
       isPoint = checkRepresentation( geoobj, "isPoint");
       isLine = checkRepresentation( geoobj, "isLine");
       isPolygon = checkRepresentation( geoobj, "isPolygon");
       isComplexObj = checkRepresentation( geoobj, "isComplexObj" );
       
       
//TEMP{
       
       //deve verificar se eh instante ou intervalo para todas classes - ver se este codigo esta no lugar certo
       boolean isInstant, isInterval; //FAZER ISSO PARA GRANULARIDADE TAMBEM
       isInstant = checkRepresentation( geoobj, "isInstant");
       isInterval = checkRepresentation( geoobj, "isInterval");
       
       // inser�ao das representa�oes dos objetos geogr�ficos no banco de dados
       String[] representation = new String[4];
       representation[0] = representation[1]= representation[2]= representation[3] = "";
       
       if (isPoint) representation[0] = "Ponto";
       if (isLine)  representation[1] = "Linha";
       if (isPolygon) representation[2] = "Pol";
       if (isComplexObj) representation[3] = "Complex";
       
       // insere este layer no banco de dados
       if(isInterval) { 
           terraDB.addLayer(nomeTabelaVersoes);
           themes[i] = nomeTabelaVersoes;
           terraDB.addLayerRep( nomeTabelaVersoes, representation);
       }
       else { //objeto sera armazenado de forma convencional
          terraDB.addLayer(nome);
          themes[i] = nome;
          terraDB.addLayerRep( nome, representation);
       } 
//TEMP}       
       
       // inser�ao das representa�oes dos objetos geogr�ficos no banco de dados
/*  
       String[] representation = new String[4];
       representation[0] = representation[1]= representation[2]= representation[3] = "";
       
       if (isPoint) representation[0] = "Ponto";
       if (isLine)  representation[1] = "Linha";
       if (isPolygon) representation[2] = "Pol";
       if (isComplexObj) representation[3] = "Complex";
       
       terraDB.addLayerRep( nome, representation);      
*/        
        
       // atributos
       NodeList attributes = geoobj.getElementsByTagName( "Foundation.Core.Attribute" );
//TEMP//       String[] attList = new String[2 * (attributes.getLength())];
//TEMP//       String primaryKey = "";
       
//TEMP{
       String[] attList;
       if (isInstant) //armazena espa�o para o atributo instante que ser� adicionado aqui
         attList = new String[2 * (attributes.getLength()) +2];
       else      
         attList = new String[2 * (attributes.getLength())];

       ArrayList PKs = new ArrayList(); //armazena atributos que fazem parte da chave prim�ria
//TEMP}
       
       int controle = 0;       
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
           
           attList[controle] = attributeName;
                     
           NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
           Element structType = (Element)structFields.item(0);
        
           NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
           Element classify = (Element)classifiers.item(0);
           // tipo do atributo 
           String attrID = classify.getAttribute("xmi.idref");
           String attributeType = getType( model, attrID );    
           
           attList[controle + 1] = attributeType;
           
           // chave prim�ria ou n�o
           boolean pKey = false;
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ )
           {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               if (teste.equals("primary key")&& teste2.equals("true") )
               {
                   pKey = true;
               }
           }
                    
           
           // trecho necess�rio para os relacionamentos
           if( pKey )
           {
               auxiliary.add(nome); 
               auxiliary.add(attributeName);
               auxiliary.add(attributeType); //TEMP  //GUARDAR TIPO DA CHAVE PRIM�RIA TAMB�M
               PKs.add(attributeName); //TEMP //GUARDA CHAVE PRIM�RIA
               
           }
                     
           controle += 2;
                   
           
       }
       
//TEMP{
       String[] chavesPrimar = (String[]) PKs.toArray(new String[0]);
       
       //acrescentar atributos ao banco de dados
       if (isInterval) {
           //quarto par�metro � true porque est� tratanto objeto geogr�fico
           terraDB.addTableTemporal( nome, attList, chavesPrimar, true );  
       }
       else if (isInstant) {
           attList[controle] = "instante";
           attList[controle +1] = "Date";  //TROCAR POR DATA DE ACORDO COM GRANULARIDADE
           terraDB.addAttr2( nome, attList, chavesPrimar );
       }
       
       else { //fazer para instante tambem  
           terraDB.addAttr2( nome, attList, chavesPrimar );
//TEMP}
          //acrescenta atributos ao banco de dados
         //terraDB.addAttr( nome, attList, primaryKey );          
       }
                                      
       
    }    
    
    // cria a view adicionando os temas( pr�prios layers )
    if(geoobjs.getLength()> 0)
       terraDB.addViewTheme(view, themes );
     
     //************* fim da manipula�ao dos objetos geogr�ficos*************
      
     // *********** manipula�ao dos objetos n�o-geogr�ficos *****************//
     // Neste caso, s� teremos tabelas de atributos     
     
    
    for( int i = 0; i < ngeoobjs.getLength(); i++ )
    {
        Element ngeoobj = (Element)ngeoobjs.item(i);
        String nome = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );


        String nomeTabelaVersoes = nome+"_VERSOES";
               
        
        NodeList list = ngeoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( list.getLength() > 0)      
            specialization.add(nome);           
        
        NodeList AAA = ngeoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
        NodeList especia = ngeoobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( (AAA.getLength() > 0) || (especia.getLength() > 0) )  
        {            
       //     themes[i] = ""; ATENCAO - ESTAVA ERRADO
            continue;
        }
      
       //deve verificar se eh instante ou intervalo para todas classes
       boolean isInstant, isInterval; //FAZER ISSO PARA GRANULARIDADE TAMBEM
       isInstant = checkRepresentation( ngeoobj, "isInstant");
       isInterval = checkRepresentation( ngeoobj, "isInterval");
               
        
         // atributos
       NodeList attributes = ngeoobj.getElementsByTagName( "Foundation.Core.Attribute" );
      
       String[] attList;
       if (isInstant) //armazena espa�o para o atributo instante que ser� adicionado aqui
         attList = new String[2 * (attributes.getLength()) +2];
       else      
         attList = new String[2 * (attributes.getLength())];
       
       ArrayList PKs = new ArrayList(); //armazena atributos que fazem parte da chave prim�ria 
       
       int controle = 0;       
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
           
           attList[controle] = attributeName;
                     
           NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
           Element structType = (Element)structFields.item(0);
        
           NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
           Element classify = (Element)classifiers.item(0);
           // tipo do atributo 
           String attrID = classify.getAttribute("xmi.idref");
           String attributeType = getType( model, attrID );    
           
           attList[controle + 1] = attributeType;
           
           // chave prim�ria ou n�o
           boolean pKey = false;
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ )
           {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               if (teste.equals("primary key")&& teste2.equals("true") )
               {
                   pKey = true;
               }
           }
           
           
            // trecho necess�ria para os relacionamentos
           if( pKey )
           {
               auxiliary.add(nome);
               auxiliary.add(attributeName);
               auxiliary.add(attributeType); //GUARDAR TIPO DA CHAVE PRIM�RIA TAMB�M
               PKs.add(attributeName); //GUARDA CHAVE PRIM�RIA               
           }
          
           
           controle += 2;
       }
       
       String[] chavesPrimar = (String[]) PKs.toArray(new String[0]);
       

       
       //acrescentar atributos ao banco de dados
       if (isInterval) 
           //quarto par�metro � false porque esta classe n�o possui representa��o geogr�fica
           terraDB.addTableTemporal( nome, attList, chavesPrimar, false );  
       else if (isInstant) {
           attList[controle] = "instante";
           attList[controle +1] = "Date";
           terraDB.addTable2( nome, attList, chavesPrimar );
       }
       
       else  
           terraDB.addTable2( nome, attList, chavesPrimar );         
       
       
    }
     //************* fim da manipula�ao dos objetos n�o-geogr�ficos*************  
     
     // *********** manipula�ao dos campos geogr�ficos *****************//
    
    themes = new String[geofields.getLength()];   
    for( int i = 0; i < geofields.getLength(); i++ )
    {
        Element geofieldobj = (Element)geofields.item(i);
        String nome = getChildTagValue( geofieldobj, "Foundation.Core.ModelElement.name" );
        
//TEMP{
        String nomeTabelaVersoes = nome+"_VERSOES";
//TEMP}               
        
         NodeList list = geofieldobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( list.getLength() > 0)      
            specialization.add(nome);    
           
        NodeList genera = geofieldobj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
        NodeList especia = geofieldobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( (genera.getLength() > 0) || (especia.getLength() > 0) )  
        {            
            themes[i] = "";
            continue;
        }
        
          // insere este layer no banco de dados
          //terraDB.addLayer(nome);
          //themes[i] = nome;       
          
        // verifica as representa�oes do objeto
       boolean isGridOfCels, isAdjPolygons, isIsolines, isGridOfPoints, isTIN, isIrregularPoints;
       isGridOfCels = checkRepresentation( geofieldobj, "isGridOfCels");
       isAdjPolygons = checkRepresentation( geofieldobj, "isAdjPolygons");
       isIsolines = checkRepresentation( geofieldobj, "isIsolines");
       isGridOfPoints = checkRepresentation( geofieldobj, "isGridOfPoints" );
       isTIN = checkRepresentation( geofieldobj, "isTIN");
       isIrregularPoints = checkRepresentation( geofieldobj, "isIrregularPoints");
       
       // inser�ao das representa�oes dos objetos geogr�ficos no banco de dados
  
//TEMP{
       
       //deve verificar se eh instante ou intervalo para todas classes - ver se este codigo esta no lugar certo
       boolean isInstant, isInterval; //FAZER ISSO PARA GRANULARIDADE TAMBEM
       isInstant = checkRepresentation( geofieldobj, "isInstant");
       isInterval = checkRepresentation( geofieldobj, "isInterval");
//TEMP}              
       
       String[] representation = new String[6];
       representation[0] = representation[1]= representation[2]= representation[3] = "";
       representation[4] = representation[5] = "";
       
       if (isGridOfCels) representation[0] = "Celula";
       if (isAdjPolygons)  representation[1] = "Pol";
       if (isIsolines) representation[2] = "Linha";
       if (isGridOfPoints) representation[3] = "Ponto";
       if (isTIN) representation[2] = "Pol";
       if (isIrregularPoints) representation[3] = "Ponto";
      
//TEMP{       
      // insere este layer no banco de dados
       if(isInterval) { 
           terraDB.addLayer(nomeTabelaVersoes);
           themes[i] = nomeTabelaVersoes;
           terraDB.addLayerRep( nomeTabelaVersoes, representation);
       }
       else { //objeto sera armazenado de forma convencional
          terraDB.addLayer(nome);
          themes[i] = nome;
          terraDB.addLayerRep( nome, representation);
       } 
//TEMP}              
       //terraDB.addLayerRep( nome, representation);      
        
        
         // atributos
       NodeList attributes = geofieldobj.getElementsByTagName( "Foundation.Core.Attribute" );
//TEMP//       String[] attList = new String[(2 * (attributes.getLength())) + 2] ;
//TEMP//       String primaryKey = "";

//TEMP{
       String[] attList;
       if (isInstant) //armazena espa�o para o atributo instante que ser� adicionado aqui
         attList = new String[2 * (attributes.getLength()) +4];
       else      
         attList = new String[2 * (attributes.getLength()) +2];

       ArrayList PKs = new ArrayList(); //armazena atributos que fazem parte da chave prim�ria
//TEMP}
       
       int controle = 0;        
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
           
           attList[controle] = attributeName;
                     
           NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
           Element structType = (Element)structFields.item(0);
        
           NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
           Element classify = (Element)classifiers.item(0);
           // tipo do atributo 
           String attrID = classify.getAttribute("xmi.idref");
           String attributeType = getType( model, attrID );    
           System.out.println( attributeType );
           attList[controle + 1] = attributeType;
           
           // chave prim�ria ou n�o
           boolean pKey = false;
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ )
           {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               if (teste.equals("primary key")&& teste2.equals("true") )
               {
                   pKey = true;
               }
           }
                   
       
           if( pKey )
           {
               auxiliary.add(nome); 
               auxiliary.add(attributeName);
               auxiliary.add(attributeType); //TEMP  //GUARDAR TIPO DA CHAVE PRIM�RIA TAMB�M
               PKs.add(attributeName); //TEMP //GUARDA CHAVE PRIM�RIA
               
           }       
           
           controle += 2;
       }
       
       attList[ (2 * (attributes.getLength())) ] = "limit";
       attList[ (2 * (attributes.getLength()))  + 1 ] = "String";
       
//TEMP{
       String[] chavesPrimar = (String[]) PKs.toArray(new String[0]);
       
       //acrescentar atributos ao banco de dados
       if (isInterval) {
           //quarto par�metro � true porque est� tratanto campo geogr�fico
           terraDB.addTableTemporal( nome, attList, chavesPrimar, true );  
       }
       else if (isInstant) {
           attList[(2 * (attributes.getLength())) +2] = "instante";
           attList[(2 * (attributes.getLength())) +3] = "Date";  //TROCAR POR DATA DE ACORDO COM GRANULARIDADE
           terraDB.addAttr2( nome, attList, chavesPrimar );
       }
       
       else  //fazer para instante tambem  
           terraDB.addAttr2( nome, attList, chavesPrimar );
//TEMP}
       
       
       
       //acrescenta atributos ao banco de dados
       //  terraDB.addAttr( nome, attList, primaryKey );
     
//TEMP{         
     // String sql1 = "ALTER TABLE " + nome + " ADD PRIMARY KEY (" + primaryKey +")";
     // terraDB.comandoSQL(sql1);
//TEMP}     
    }    
     
     // cria a view adicionando os temas( pr�prios layers )
    if( geofields.getLength() > 0 )
        terraDB.addViewTheme(view, themes );
    //************* fim da manipula�ao dos campos geogr�ficos*************//  
    
    //********* manipula�ao dos objetos de rede*************
    themes = new String[netobjs.getLength()];
    for( int i = 0; i < netobjs.getLength(); i++ )
    {
        Element netobj = (Element)netobjs.item(i);
        String nome = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
//TEMP{
        String nomeTabelaVersoes = nome+"_VERSOES";
//TEMP}        
        
        NodeList list = netobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
        if( list.getLength() > 0)      
            specialization.add(nome);    
        
        NodeList genera = netobj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
        NodeList especia = netobj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
 
//TEMP// SE CLASSE PARTICIPA DE UMA GENERALIZACAO OU ESPECIALIZACAO NAO CRIA TABELA AGORA 
        if( (genera.getLength() > 0) || (especia.getLength() > 0) )  
        {            
            themes[i] = "";
            continue;
        }
              
          // insere este layer no banco de dados
//          terraDB.addLayer(nome);
//          themes[i] = nome;       
          
        // verifica as representa�oes do objeto
       boolean isNode, isArc;
       isNode = checkRepresentation( netobj, "isNode");
       isArc = checkRepresentation( netobj, "isArc");
       
//TEMP{
       
       //deve verificar se eh instante ou intervalo para todas classes - ver se este codigo esta no lugar certo
       boolean isInstant, isInterval; //FAZER ISSO PARA GRANULARIDADE TAMBEM
       isInstant = checkRepresentation( netobj, "isInstant");
       isInterval = checkRepresentation( netobj, "isInterval");
       
       // inser�ao das representa�oes dos objetos geogr�ficos no banco de dados
       String[] representation = new String[4];
       representation[0] = representation[1]= representation[2]= representation[3] = "";
       
       if (isNode) representation[0] = "Nodo";
       if (isArc)  representation[1] = "Arco";
       
       // insere este layer no banco de dados
       if(isInterval) { 
           terraDB.addLayer(nomeTabelaVersoes);
           themes[i] = nomeTabelaVersoes;
           terraDB.addLayerRep( nomeTabelaVersoes, representation);
       }
       else { //objeto sera armazenado de forma convencional
          terraDB.addLayer(nome);
          themes[i] = nome;
          terraDB.addLayerRep( nome, representation);
       } 
     
        
       // atributos
       NodeList attributes = netobj.getElementsByTagName( "Foundation.Core.Attribute" );
//TEMP//       String[] attList = new String[2 * (attributes.getLength())];
//TEMP//       String primaryKey = "";
       
//TEMP{
       String[] attList;
       if (isInstant) //armazena espa�o para o atributo instante que ser� adicionado aqui
         attList = new String[2 * (attributes.getLength()) +2];
       else      
         attList = new String[2 * (attributes.getLength())];

       ArrayList PKs = new ArrayList(); //armazena atributos que fazem parte da chave prim�ria
//TEMP}
       
       int controle = 0;       
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
           
           attList[controle] = attributeName;
                     
           NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
           Element structType = (Element)structFields.item(0);
        
           NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
           Element classify = (Element)classifiers.item(0);
           // tipo do atributo 
           String attrID = classify.getAttribute("xmi.idref");
           String attributeType = getType( model, attrID );    
           
           attList[controle + 1] = attributeType;
           
           // chave prim�ria ou n�o
           boolean pKey = false;
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ )
           {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               if (teste.equals("primary key")&& teste2.equals("true") )
               {
                   pKey = true;
               }
           }
                    
           
           // trecho necess�rio para os relacionamentos
           if( pKey )
           {
               auxiliary.add(nome); 
               auxiliary.add(attributeName);
               auxiliary.add(attributeType); //TEMP  //GUARDAR TIPO DA CHAVE PRIM�RIA TAMB�M
               PKs.add(attributeName); //TEMP //GUARDA CHAVE PRIM�RIA
               
           }
                     
           controle += 2;
                   
           
       }
       
//TEMP{
       String[] chavesPrimar = (String[]) PKs.toArray(new String[0]);
       
       //acrescentar atributos ao banco de dados
       if (isInterval) {
           //quarto par�metro � true porque est� tratanto objeto geogr�fico
           terraDB.addTableTemporal( nome, attList, chavesPrimar, true );  
       }
       else if (isInstant) {
           attList[controle] = "instante";
           attList[controle +1] = "Date";  //TROCAR POR DATA DE ACORDO COM GRANULARIDADE
           terraDB.addAttr2( nome, attList, chavesPrimar );
       }
       
       else { //fazer para instante tambem  
           terraDB.addAttr2( nome, attList, chavesPrimar );
//TEMP}
          //acrescenta atributos ao banco de dados
         //terraDB.addAttr( nome, attList, primaryKey );          
       }
                                      
       
    }    
    
    // cria a view adicionando os temas( pr�prios layers )
    if(netobjs.getLength()> 0)
       terraDB.addViewTheme(view, themes );
     
     //************* fim da manipula�ao dos objetos de rede*************
  
    //*************************** RELACIONAMENTOS ************************//
    //AQUI FAZIA OS RELACIONAMENTOS POR PACOTE - FOI TIRADO

//////////////////////////////////////////generalizacao    // Generaliza��o e Especializa��o    
    GeneraWindow  gWindow;
    int contadores = specialization.size();
     
    if (contadores-- > 0)
    {       
       
        JFrame fr = new JFrame();
        String parentClass = view;      
         gWindow = new GeneraWindow(fr, parentClass);
          op = gWindow.getOp(); // identifica a o tipo de transforma�ao que o usu�rio deseja aplicar
          System.out.println("@@@@@@@@@@@@@@@@@@opppppppppppppp"+" "+op);
    }    
     
        
    else
        op = 'D';
     
    String parentClass;
    //a string classe armazena as classe que participam de generalizacao/especializacao => pode nao ser a classe pai
    String classe; //ATENCAO = VER SE EH NECESSARIO ESTA VARIAVEL
    
           if(op == 'A')
           {
               //String primaryKey = "";
               List parentPrimaryKey = new ArrayList();
               String estereotipoTemporal = "";
               
        //       String[] rep;
               Iterator it = specialization.iterator();           
                              
               while( it.hasNext() )
               { 
                   classe = (String)it.next();
                   parentClass = getParent(classe, model);
                   System.out.println("Processando especializacoes da classe pai: "+parentClass);
        
                   // chave da classe base
                   parentPrimaryKey = getPK(parentClass, model);
                   String[] parentPK = new String[parentPrimaryKey.size()];
                   parentPK = (String[]) parentPrimaryKey.toArray(new String[0]);

                   estereotipoTemporal = getEstereotipoTemporal(parentClass, pack);
                   
                   //primaryKey = getPrimaryKey( parentClass, pack );                
                   List descendants = getDescendant( parentClass, pack ); //pega o estere�tipo temporal da classe pai
                    
                   int size = descendants.size();                
                   String[] desc = new String[descendants.size()];
                   desc = (String[])descendants.toArray(new String[0]);
        
                   String[] rep;
                   boolean isGeografico = isGeo(parentClass, pack); //deve receber valor true se classe possui representacao geografica (ou seja, deve continuar com o valor false se classe for do tipo obj nao geografico)      
                   
                   rep = decideRepresentation2(parentClass, desc, pack ); //pega a representacao de todas as classes (porque opcao A gera uma tabela s�)
                                      
                   
                   List attrs = new ArrayList();
                   //attrs = getAttributeList(parentClass, pack, true);
                   attrs = getAttributeList2(parentClass, model, true);
                 
                   for( int k = 0; k < desc.length; k++ )
                   {
                       //attrs.addAll(getAttributeList(desc[k], pack, false));
                       attrs.addAll(getAttributeList2(desc[k], model, false));
                   }
              
                   
                   String z[] = new String[attrs.size()];
                   z = (String[])attrs.toArray(new String[0]);
         
                   terraDB.createEspA2( parentClass, parentPK, desc, rep, z, view, isGeografico, estereotipoTemporal );                 
                   
               }
               
           }
    
           else if( op == 'B' )
           {               

               List parentPrimaryKey = new ArrayList();
               String estereotipoTemporal = "";
               
 //              String[] rep;
               String[] pkeys;
               Iterator it = specialization.iterator();              
               
               while( it.hasNext() )
               { 
                   classe = (String)it.next();
                   parentClass = getParent(classe, model); //ATENCAO - ACHO QUE NAO PRECISA DISSO
                   System.out.println("Processando especializacoes da classe pai: "+parentClass);      
                   
                   // chave da classe base
                   parentPrimaryKey = getPK(parentClass, model);
                   String[] parentPK = new String[parentPrimaryKey.size()];
                   parentPK = (String[]) parentPrimaryKey.toArray(new String[0]);
                 
                   estereotipoTemporal = getEstereotipoTemporal(parentClass, pack);
                   
                   List descendants = getDescendant( parentClass, pack );
                    
                   int size = descendants.size();                
                   String[] desc = new String[size]; 
                   desc = (String[])descendants.toArray(new String[0]);
                                                           
                   //rep = decideRepresentation(parentClass, desc, pack ); 
                   //rep = decideRepresentation2(parentClass, desc, pack ); 
                   String[][] rep = new String[size][]; //armazena a representacao geometrica de tocas as subclasses
                   
                   boolean isGeografico = isGeo(parentClass, pack); //deve receber valor true se classe possui representacao geografica (ou seja, deve continuar com o valor false se classe for do tipo obj nao geografico)
                   
                   if(isGeografico) {
                        boolean[] parentRep = getRep(parentClass, pack);
                        boolean[] subRep;
                        for (int i=0; i<size; ++i) {
                            subRep = getRep(desc[i], pack);
                            rep[i] = uniao(subRep, parentRep);
                        }
                   }
                                                        
                   
                   //armazena a chave primaria das classes filhas
                   String[][] descpkeys = new String[desc.length][];  //ATENCAO -- TIRAR ESTE PARAMETRO DA FUNCAO createEspB2 DEPOIS
                   List descPK = new ArrayList();
                   
                   /*for( int i=0; i<desc.length; ++i) {
                       descPK = getPK(desc[i], model);
                       descpkeys[i] = new String[descPK.size()];
                       descpkeys[i] = (String[]) descPK.toArray(new String[0]);
                   }*/
                                        
                   
                   // armazena atributos das classe filhas
                   String[][] myAttrs = new String[desc.length][];

                   List attrs = new ArrayList();
                   attrs = getAttributeList2(parentClass, model, true); 
                               
                   String parentAttributes[];
                   
                   // armazena atributos da classe base
                   parentAttributes = new String[attrs.size()];
                   parentAttributes = (String[]) attrs.toArray(new String[0]);

                                     
                   for( int k = 0; k < desc.length; k++ )  
                   {
                       //attrs = getAttributeList(desc[k], pack, true);
                       attrs = getAttributeList2(desc[k], model, false);//temp-----------
                       myAttrs[k] = new String[attrs.size()];
                       myAttrs[k] = (String[])(attrs.toArray(new String[0]));
                   }  
                                                
                   terraDB.createEspB2( parentClass, parentPK, parentAttributes, desc, descpkeys, rep, myAttrs, view, isGeografico, estereotipoTemporal );
               } 
                             
           }
    
           else if( op == 'C' )
           {
               
               String estereotipoTemporal = "";
               List parentPrimaryKey = new ArrayList();
               
               //String[] rep;
               String[] pkeys;
               Iterator it = specialization.iterator();
               while( it.hasNext() )
               { 
                   parentClass = (String)it.next();
                   // chave da classe base
                   //primaryKey = getPrimaryKey( parentClass, pack );
                   parentPrimaryKey = getPK(parentClass, model);
                   String[] parentPK = new String[parentPrimaryKey.size()];
                   parentPK = (String[]) parentPrimaryKey.toArray(new String[0]);
                   
                   estereotipoTemporal = getEstereotipoTemporal(parentClass, pack);
                   
                   List descendants = getDescendant( parentClass, pack );
                    
                   int size = descendants.size();                
                   String[] desc = new String[descendants.size()];
                   desc = (String[])descendants.toArray(new String[0]);
                             
                   //rep = decideRepresentation(parentClass, desc, pack );
                   String[][] rep = new String[size+1][]; //armazena a representacao geometrica de tocas as subclasses e da classe pai (que eh o primeiro vetor => rep[0][])
                   
                   boolean isGeografico = isGeo(parentClass, pack); //deve receber valor true se classe possui representacao geografica (ou seja, deve continuar com o valor false se classe for do tipo obj nao geografico)
                   
                   if(isGeografico) {
                        boolean[] parentRep = getRep(parentClass, pack);
                        rep[0] = uniao(parentRep, parentRep);//funcao "uniao" aqui foi usada somente para converter o vetor de boolean para String 
                        
                        boolean[] subRep;
                        for (int i=0; i<size; ++i) {
                            subRep = getRep(desc[i], pack);
                            rep[i+1] = uniao(subRep, parentRep);
                        }
                   }
                   
                   
                   // armazena chaves das classes filhas
                   String[] descpkeys = new String[desc.length];
                   for( int k = 0; k < descpkeys.length; k++ )
                       descpkeys[k] = getPrimaryKey( desc[k], pack );           
                   
                   
                   // armazena atributos das classe filhas
                   String[][] myAttrs = new String[desc.length][];
                   
                   List attrs = new ArrayList();
                   //attrs = getAttributeList(parentClass, pack, true);
                   attrs = getAttributeList2(parentClass, model, true);
                
                   String parentAttributes[];
                   
                   // armazena atributos da classe base
                   parentAttributes = new String[attrs.size()];
                   parentAttributes = (String[]) attrs.toArray(new String[0]); 
                  
                                     
                   for( int k = 0; k < desc.length; k++ )  
                   {
                       //attrs = getAttributeList(desc[k], pack, true);
                       attrs = getAttributeList2(desc[k], model, false);
                       myAttrs[k] = new String[attrs.size()];
                       myAttrs[k] = (String[])(attrs.toArray(new String[0]));
                   }  
                 
              
                    //terraDB.createEspC( parentClass, primaryKey, parentAttributes, desc, descpkeys, rep, myAttrs, view );             
                    terraDB.createEspC2( parentClass, parentPK, parentAttributes, desc, descpkeys, rep, myAttrs, view, isGeografico, estereotipoTemporal );             
                         
               
               } 
           }    
  //////////////////////////////////////////generalizacao  */    
    
    
    }
    
        
    // TRATAMENTO DAS ASSOCIA��ES INTER-PACOTES
    // Associa�ao, Agrega��o e Composi��o    
  /*
    NodeList interAssociations = model.getElementsByTagName( "Foundation.Core.Association" );
     
    for( int i = 0; i < interAssociations.getLength(); i++ )
    {
        Element interAssociation = (Element)interAssociations.item(i);
              
       if( interAssociation.hasAttribute("xmi.id"))
       {     
         
            // ID da classe, many indica se a multiplicidade � de muitos
            // informa��es da primeira classe
            String className1; boolean many1;
            // Informa�oes da segunda classe
            String className2; boolean many2;
            
            // multiplicidades
            NodeList multiplicities = interAssociation.getElementsByTagName( "Foundation.Core.AssociationEnd.multiplicity");
            Element multiplicity = (Element)multiplicities.item(0); // pega primeira multiplicidade
            many1 = getMultiplicity( multiplicity, model );
            multiplicity = (Element)multiplicities.item(1); // pega a segunda multiplicidade
            many2 = getMultiplicity( multiplicity, model );
               
            // nomedas das classes envolvidas no relacionamento
            NodeList relations = interAssociation.getElementsByTagName( "Foundation.Core.AssociationEnd.type");
                   
            Element relation = (Element)relations.item(0); // pega primeira classe
            className1 = getClassName1( relation, model );
            relation = (Element)relations.item(1); // pega a segunda classe
            className2 = getClassName1( relation, model );           
             
            
            String pKey = "";
            // Obten�ao das chaves prim�rias das tabelas envolvidas
             Iterator it = auxiliary.iterator();
             while( it.hasNext())
             {   
                 if( className1.equals(it.next()))
                     pKey = (String)it.next();
                 else
                     it.next();
                               
             }              
             
                    
            // tratamento do relacionamento 1 para 1, e 1 para N
            if( ((!many1) && (!many2)) || ((!many1) && (many2))  ) 
            { 
                // Neste caso, a tabela da classe className1 exporta o campo para a classe className2
                terraDB.createRelation(className1, className1, className2, pKey);           
            }
             
            // tratamento do restante do relacionamento 1 para N
            else if(many1 && (!many2))
            {
                // Neste caso, a tabela da classe className2 exporta o campo para a classe className1
                terraDB.createRelation(className2, className2, className1, pKey);                
            }
             
            // tratamento do relacionamento M para N
            else
            {
                String pKey2 = "";
                it = auxiliary.iterator();
                while( it.hasNext())
                {   
                   if( className2.equals(it.next()))
                       pKey2 = (String)it.next();
                   else
                      it.next();
                }
                if( pKey.equals(""))
                    pKey = "object_id_";
                if( pKey2.equals(""))
                    pKey2 = "object_id_";
                          
                terraDB.createTable(className1, className2, pKey, pKey2 );
                
            }
            
        
       }
    }*/
  
//TRATAMENTO DE TODAS ASSOCIA��ES - INCLUINDO (INTER-PACOTES)
//TEMP{ PEGAR O ESTEREOTIPO TEMP E TRATAR CASO EM QUE NAO HA ESTE ESTEREOTIPO  
    
    // guarda as associa��es
    NodeList associations = model.getElementsByTagName( "Foundation.Core.Association" );
    // guarda as generaliza��es
    NodeList generalizations = model.getElementsByTagName( "Foundation.Core.Generalization" );
    
    NodeList stereotype = model.getElementsByTagName("Foundation.Extension_Mechanisms.Stereotype");
    
  //guarda todos os identificadores das associa��es temporais (de acordo com o arquivo xmi)  
  ArrayList assocTemporaisID = new ArrayList(); 
 
  //h� algum estere�tipo - n�o garante que � temporal
  for (int i=0; i < stereotype.getLength(); ++i) { 
    
    //ESTEREOTIPOS DIFERENTES NAO ESTAO NO MESMO NODO
    Element stereo = (Element)stereotype.item(i); //TENTAR PEGAR APENAS O ESTEREOTIPO temp  
   
    String nomeStereo = getChildTagValue(stereo, "Foundation.Core.ModelElement.name");
    
   if ( nomeStereo.equals("temp") ) { //PEGAR APENAS O ESTEREOTIPO temp  
    String abcd = getChildTagValue(stereo, "Foundation.Core.ModelElement.name"); 
    System.out.println(abcd);
    System.out.println(abcd);
    System.out.println(abcd);
    
    NodeList children = stereo.getElementsByTagName( "Foundation.Extension_Mechanisms.Stereotype.extendedElement" );
    if( children.getLength() == 0 )
        continue; //ACHO QUE PODE TROCAR POR BREAK
        
    
    //ACHO QUE PODE USAR ESTE COMANDO stereo.getFirstChild() PARA NAO TER QUE CRIAR NodeList children
    
    Element child = (Element) children.item(0);
    //if( child == null ) return null;
     
    NodeList assocTemporais = child.getElementsByTagName( "Foundation.Core.ModelElement" );
    
    for (int j=0; j < assocTemporais.getLength(); ++j) {    
      Element assocTemp = (Element) assocTemporais.item(j);  //fazer comando de repeticao aqui para pegar todos relacionamentos temporais
      String idAssocTemp = assocTemp.getAttribute("xmi.idref");
      
      assocTemporaisID.add(idAssocTemp);//arnazena todos os identificadores das associa��es temporais
      
      System.out.println(idAssocTemp);
      System.out.println(idAssocTemp);
      System.out.println(idAssocTemp);
    }
   } 
  }   
//TEMP}    
        
    // Associa�ao, Agrega��o e Composi��o    
  
    for( int i = 0; i < associations.getLength(); i++ )
    {
        Element association = (Element)associations.item(i);
              
       if( association.hasAttribute("xmi.id"))
       {     
         
            // ID da classe, many indica se a multiplicidade � de muitos
            // informa��es da primeira classe
            String className1; boolean many1;
            // Informa�oes da segunda classe
            String className2; boolean many2;
            
            // multiplicidades
            NodeList multiplicities = association.getElementsByTagName( "Foundation.Core.AssociationEnd.multiplicity");
            Element multiplicity = (Element)multiplicities.item(0); // pega primeira multiplicidade
            many1 = getMultiplicity( multiplicity, model );
            multiplicity = (Element)multiplicities.item(1); // pega a segunda multiplicidade
            many2 = getMultiplicity( multiplicity, model );
           
//TEMP{
       String assocID = association.getAttribute("xmi.id");
       boolean isAssocTemporal = false;
        
       if(assocTemporaisID.contains(assocID)) {
           many1 = true;  //para chegar ate a condicao N..N, onde a associacao temporal eh tratada
           many2 = true;
           isAssocTemporal = true;
       }
//TEMP}            
            
            // nome das das classes envolvidas no relacionamento
            NodeList relations = association.getElementsByTagName( "Foundation.Core.AssociationEnd.type");
                   
            Element relation = (Element)relations.item(0); // pega primeira classe
            className1 = getClassName1( relation, model );
            relation = (Element)relations.item(1); // pega a segunda classe
            className2 = getClassName1( relation, model );           
             
            
            String pKey = "";
            
//TEMP{     
            String tipoPK = ""; // PASSAR O TIPO DO ATRIBUTO COMO PAR�METRO DO RELACIONAMENTO
            ArrayList PKsClasse1 = new ArrayList(); //chave primaria da classe1
            ArrayList PKsClasse2 = new ArrayList(); //chave primaria da classe2
            String[] chavesPrimarClasse1;
            String[] chavesPrimarClasse2;
//TEMP}            
            // Obten�ao das chaves prim�rias das tabelas envolvidas
            /*Iterator it = auxiliary.iterator();
             while( it.hasNext())
             {   
                String classe = (String)it.next();
                System.out.println(classe+"@@@@@@@@@");

                 if( className1.equals(classe)) {
                     pKey = (String)it.next();
                     tipoPK = (String)it.next(); //TEMP
                     PKsClasse1.add(pKey);
                     PKsClasse1.add(tipoPK); //TEMP//  PEGAR O TIPO DE PK TAMBEM
                 }    
                 else {
                     it.next();
                     it.next(); //TEMP// pula tipo do atributo
                 }    
             }              

//TEMP{     
            Iterator it2 = auxiliary.iterator();
             while( it2.hasNext())
             {   
                 if( className2.equals(it2.next())) {
                     pKey = (String)it2.next();
                     tipoPK = (String)it2.next(); //TEMP
                     PKsClasse2.add(pKey); //TEMP
                     PKsClasse2.add(tipoPK); //TEMP//  PEGAR O TIPO DE PK TAMBEM
                     
                 }    
                 else {
                     it2.next();
                     it2.next(); //TEMP// pula tipo do atributo
                 }              
             }*/                             
             
//TEMP}      
/*            PKsClasse1 = (ArrayList) getPK(className1,model);
            PKsClasse2 = (ArrayList) getPK(className2,model);
            
            
             //Dependendo de como o generalizacao eh tratada, haveria erro se subclasse nao tivesse chave primaria             
            String paiClasse1 = getParent(className1, model);
            String paiClasse2 = getParent(className2, model);
            
            if( op != 'C' && !paiClasse1.equals("") && !paiClasse1.equals(className1)) { 
               //classe1 eh uma subclasse e nao escolheu opcao C
               System.out.println("----");
               System.out.println(PKsClasse1);
               System.out.println(className1 + " eh subclasse e ainda nao possui chave primaria");
               List parentPK = new ArrayList(); 
               parentPK = getPK(paiClasse1,model);
               System.out.println(parentPK);
               System.out.println(op);
               if(op == 'A') {
                  System.out.println("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                  PKsClasse1.clear();
                  PKsClasse1.addAll(parentPK);
                  
               }   
               else {
                   //fazer este comando primeiro e nao PKsClasse1.addAll(parentPK); para os atributo que farao parte da chave estrangeira aparecerem na mesma ordem (senao haveria erro)
                   parentPK.addAll(PKsClasse1); 
                   PKsClasse1 = (ArrayList)parentPK;
               }
               System.out.println("Chave primaria atual: " + PKsClasse1);
               System.out.println("----");
             }
             
            if(PKsClasse2.isEmpty()) { //ATENCAO REFAZER -- IGUAL PARA CLASSE1
               System.out.println("----");
               System.out.println(className2 + " eh subclasse e ainda nao possui chave primaria (pegar chave de sua superclasse)"); 
               String parent = getParent(className2,model);
               List parentPK = new ArrayList(); 
               parentPK = getPK(parent,model);
               PKsClasse2.addAll(parentPK);
               System.out.println("Chave primaria da superclasse: " + PKsClasse2);
               System.out.println("----");
             }
*/               
            PKsClasse1 = (ArrayList) getPKreal(className1,model);
            PKsClasse2 = (ArrayList) getPKreal(className2,model);
            
             chavesPrimarClasse1 = (String[]) PKsClasse1.toArray(new String[0]); //TEMP
             chavesPrimarClasse2 = (String[]) PKsClasse2.toArray(new String[0]); //TEMP
                              
            // tratamento do relacionamento 1 para 1, e 1 para N
            if( ((!many1) && (!many2)) || ((!many1) && (many2))  ) 
            {  System.out.println("passei aqui!");
                // Neste caso, a tabela da classe className1 exporta o campo para a classe className2
                //terraDB.createRelation(className1, className1, className2, pKey);
                
               System.out.println("CLASSE 1:  " + className1);
               System.out.println("CLASSE 2:  " + className2);
               
         /*      List teste = new ArrayList();
               teste = getPK(className1,model);*/
               
                if (!terraDB.createRelation2(className1, className2, chavesPrimarClasse1) ) {
                   //tratar aqui caso em que em que classes que se relacionavam com outras desapareceram na transformacao de generalizacoes
                   List descendants = getDescendant( className2, model ); ///troquei pack por model ===========                
                   
                   if(descendants.isEmpty()) { //restante dos relacionamentos 1..1 -- nestes casos eh a className1 que desaparece devido trasnformacao da generalizacao
                      System.out.println("#################################");
                      
                      descendants = getDescendant( className1, model );
                      Iterator desc = descendants.iterator();
                                                         
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className1)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);                           
                           //terraDB.createRelation2( className2, parent, chavesPrimarClasse2);
                            terraDB.createRelation2( parent, className2, chavesPrimarClasse1);
                           System.out.println("++++++++++++++++++++++++++++");
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento de "+ className2 + " para " + subClasse);
                         //terraDB.createRelation2( className2, subClasse, chavesPrimarClasse2);
                         //terraDB.createRelation2( subClasse, className2, chavesPrimarClasse1);
                         List pkSubClasse = new ArrayList();
                         pkSubClasse = getPKreal(subClasse, model);
                         String[] chavesPrimarSubClasse = (String[]) pkSubClasse.toArray(new String[0]);
                         terraDB.createRelation2( subClasse, className2, chavesPrimarSubClasse);
                     }
                   }
                      
                   else {
                   
                   Iterator desc = descendants.iterator();
                                                           
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className2)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);
                           terraDB.createRelation2( className1, parent, chavesPrimarClasse1);
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento de "+ className2 + " para " + subClasse);
                         terraDB.createRelation2( className1, subClasse, chavesPrimarClasse1);
                     }
                   }
                }
                    
            }
             
            // tratamento do restante do relacionamento 1 para N
            else if(many1 && (!many2))
            {  System.out.println("passei neste!"); // Neste caso, a tabela da classe className2 exporta o campo para a classe className1
               
               if (!terraDB.createRelation2(className2, className1, chavesPrimarClasse2)) {
                   //tratar aqui caso em que em que classes que se relacionavam com outras desapareceram na transformacao de generalizacoes
                   List descendants = getDescendant( className1, model );                
                   
                   if(descendants.isEmpty()) { //restante dos relacionamentos -- nestes casos eh a className1 que desaparece devido trasnformacao da generalizacao
                      System.out.println("#################################");
                      
                      descendants = getDescendant( className2, model );
                      Iterator desc = descendants.iterator();
                                                         
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className2)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);                           
                           terraDB.createRelation2( parent, className1, chavesPrimarClasse2);
                           System.out.println("++++++++++++++++++++++++++++");
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento da superclasse para " + subClasse);
                         terraDB.createRelation2( subClasse, className1, chavesPrimarClasse2);
                     }
                   }
                      
                   else {
                   
                   Iterator desc = descendants.iterator();
                                                           
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className1)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);
                           terraDB.createRelation2( className2, parent, chavesPrimarClasse2);
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento de "+ className1 + " para " + subClasse);
                         terraDB.createRelation2( className2, subClasse, chavesPrimarClasse2);
                     }
                   }
               }
               
                                                                        
            }
             
            // tratamento do relacionamento M para N
            else
            { //o �ltimo par�metro deve ser true se relacionamento for temporal
                System.out.println("Relacionamento N..N");  
                if (!terraDB.createTable2(className1, className2, chavesPrimarClasse1, chavesPrimarClasse2, isAssocTemporal) ) {
                   //tratar aqui caso em que em que classes que se relacionavam com outras desapareceram na transformacao de generalizacoes
                   List descendants = getDescendant( className1, model );                
                   
                   if(descendants.isEmpty()) { //restante dos relacionamentos -- nestes casos eh a className1 que desaparece devido trasnformacao da generalizacao
                      System.out.println("#################################");
                      
                      descendants = getDescendant( className2, model );
                      Iterator desc = descendants.iterator();
                                                         
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className2)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);                           
                           terraDB.createTable2( parent, className1, chavesPrimarClasse2, chavesPrimarClasse1, isAssocTemporal);
                           System.out.println("++++++++++++++++++++++++++++");
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento da superclasse para " + subClasse);
                         terraDB.createTable2( className1, subClasse, chavesPrimarClasse1, chavesPrimarClasse2, isAssocTemporal);
                     }
                   }
                      
                   else {
                   
                   Iterator desc = descendants.iterator();
                                                           
                     while (desc.hasNext()) {
                         String subClasse = (String)desc.next();
                         
                         if(subClasse.equals(className1)) {//Relacionamento com subclasse, que eh transferido para superclasse (opcao A)
                           System.out.println("----");
                           System.out.println("Tratando generalizacao");
                           System.out.println("Subclasse: " + subClasse);
                           String parent = getParent(subClasse, model);
                           System.out.println("Superclasse: " + parent);
                           terraDB.createTable2( parent, className2, chavesPrimarClasse1, chavesPrimarClasse2, isAssocTemporal);
                           break;
                         }
                         
                         //Relacionamento com superclasse, que eh transferido para subclasses (opcao B)
                         System.out.println("----");
                         System.out.println("Tratando generalizacao");
                         System.out.println("Transferindo relacionamento de "+ className1 + " para " + subClasse);
                         terraDB.createTable2( subClasse, className2, chavesPrimarClasse1, chavesPrimarClasse2, isAssocTemporal);
                     }
                   }
                }
            }
//TEMP}                        
             
       }
    }        
    
    
 
    
  }
  
  
  
  // este m�todo l� e retorna o conte�do (texto) de uma tag (elemento)
  // filho da tag informada como par�metro. A tag filho a ser pesquisada
  // � a tag informada pelo nome (string)
  public static String getChildTagValue( Element elem, String tagName ) {//throws Exception { // voltar para private depois
    NodeList children = elem.getElementsByTagName( tagName );
    if( children == null ) return null;
    Element child = (Element) children.item(0);
    if( child == null ) return null;
    return child.getFirstChild().getNodeValue();
  }
  
  private boolean checkRepresentation( final Element e, String isRep )
  {
        NodeList isReps = e.getElementsByTagName( "Foundation.Core.GeneralizableElement." + isRep );
        Element isR = (Element)isReps.item(0);
        String resposta = isR.getAttribute("xmi.value");
        if( resposta.equals("true") )// objeto possui representa�ao de pol�gono
            return true;
        else 
            return false;             
  }
  
  private String getType( Element model, String id )
  {
      NodeList dataTypes = model.getElementsByTagName( "Foundation.Core.DataType" );
      
      for( int i = 0; i < dataTypes.getLength(); i++ )
      {
          Element dataType = (Element)dataTypes.item(i);
          String resultID = dataType.getAttribute("xmi.id");
          try{
          if( resultID.equals(id))
          {
            String s = getChildTagValue(dataType, "Foundation.Core.ModelElement.name" );
            if (s != null)
               return s;
      
          }
          }
          catch( Exception e )
          {
              System.out.println("Exce�ao ao se tentar achar o tipo!");
          }
     }
      
      dataTypes = model.getElementsByTagName( "Foundation.Core.Class" );
      
      for( int i = 0; i < dataTypes.getLength(); i++ )
      {
          Element dataType = (Element)dataTypes.item(i);
          String resultID = dataType.getAttribute("xmi.id");
          try{
          if( resultID.equals(id))
          {
            String s = getChildTagValue(dataType, "Foundation.Core.ModelElement.name" );
            if (s != null)
               return s;
      
          }
          }
          catch( Exception e )
          {
              System.out.println("Exce�ao ao se tentar achar o tipo!");
          }
     }
      return null;
      
  }
  
  public String[] decideRepresentation(String parentClass, String[] desc, Element pack ) throws Exception
  {      
      
       boolean isPoint = false, isLine = false, isPolygon = false, isComplexObj = false;
     
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      String representation[] = new String[4];
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
                Element geoobj = (Element)geoobjs.item(i); 
                String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
                 if (parentClass.equals(name))
                 { 
                            
                            isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
                            
                  }
                  for( int b = 0; b < desc.length; b++ )
                    {
                        if (desc[b].equals(name))
                        {   
                            
                             isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
                            
                            
                        }
                    }     
              
            
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
                Element ngeoobj = (Element)ngeoobjs.item(i); 
                String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );

                        
                
                
                 if (parentClass.equals(name))
                 {
                            
                            isPoint = (isPoint || checkRepresentation( ngeoobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( ngeoobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( ngeoobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( ngeoobj, "isComplexObj" ));
                            
                            
                  }
                  for( int b = 0; b < desc.length; b++ )
                    {
                        if (desc[b].equals(name))
                        {
                            
                             isPoint = (isPoint || checkRepresentation( ngeoobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( ngeoobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( ngeoobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( ngeoobj, "isComplexObj" ));
                            
                            
                        }
                    }     
              
            
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
                Element geofield = (Element)geofields.item(i); 
                String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );

                        
                
                
                 if (parentClass.equals(name))
                 {
                            
                             isPoint = (isPoint || checkRepresentation( geofield, "isPoint"));
                            isLine = (isLine || checkRepresentation( geofield, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( geofield, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( geofield, "isComplexObj" ));
                            
                            
                  }
                  for( int b = 0; b < desc.length; b++ )
                    {
                        if (desc[b].equals(name))
                        {
                            
                             isPoint = (isPoint || checkRepresentation( geofield, "isPoint"));
                            isLine = (isLine || checkRepresentation( geofield, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( geofield, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( geofield, "isComplexObj" ));
                            
                            
                        }
                    }                  
            
      }
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
                Element netobj = (Element)netobjs.item(i); 
                String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );

                        
                
                
                 if (parentClass.equals(name))
                 {
                            
                             isPoint = (isPoint || checkRepresentation( netobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( netobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( netobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( netobj, "isComplexObj" ));
                            
                            
                  }
                  for( int b = 0; b < desc.length; b++ )
                    {
                        if (desc[b].equals(name))
                        {
                            
                             isPoint = (isPoint || checkRepresentation( netobj, "isPoint"));
                            isLine = (isLine || checkRepresentation( netobj, "isLine"));
                            isPolygon = (isPolygon || checkRepresentation( netobj, "isPolygon"));
                            isComplexObj = (isComplexObj || checkRepresentation( netobj, "isComplexObj" ));
                            
                            
                        }
                    }                  
            
      }
        
      
      representation[0] = representation[1]= representation[2]= representation[3] = "";
             
       if (isPoint) representation[0] = "Ponto";
       if (isLine)  representation[1] = "Linha";
       if (isPolygon) representation[2] = "Pol";
       if (isComplexObj) representation[3] = "Complex";
      
      
      return representation; 
      
      
  }
  
   public String[] decideRepresentation3(String parentClass, String[] desc, Element pack ) throws Exception
  {
        String representation[] = new String[5];
      for (int i=0; i<5; ++i)
          representation[i] = "";
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
//      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
    //  NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
    
      boolean isPoint = false, isLine = false, isPolygon = false, isComplexObj = false;
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {         
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (parentClass.equals(name)) { 
                isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
            }
            
            for( int b = 0; b < desc.length; b++ ) {
                if (desc[b].equals(name)) {   
                    isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                    isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                    isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                    isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
                }
            }                
            
      }
     boolean isNode = false, isArc = false;
      for( int i = 0; i < netobjs.getLength(); i++ )
      {       
            Element netobj = (Element)netobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
                                                       
             if (parentClass.equals(name)) {     //ATENCAO - VERIFICAR SE PRECISA DO "OU" ||                       
                 isNode = (isNode || checkRepresentation( netobj, "isNode"));
                     isArc = (isArc || checkRepresentation( netobj, "isArc"));
             }
             
             for( int b = 0; b < desc.length; b++ ) {
                 if (desc[b].equals(name)) {
                     isNode = (isNode || checkRepresentation( netobj, "isNode"));
                     isArc = (isArc || checkRepresentation( netobj, "isArc"));
                 }
              }                              
      }
  
      
      if (isPoint || isNode) representation[0] = "Ponto";
      if (isLine || isArc)  representation[1] = "Linha";
      
      //ATENCAO TERMINAR OBJ NAO GEO
      return representation;
   }

  //dado a classe pai e as classes filhas, retorna um vetor com a representacao de todas as classes reunidas
  //so funciona para a opacao A (que mapeia a generalizacao em uma tabela apenas)
  public String[] decideRepresentation2(String parentClass, String[] desc, Element pack ) throws Exception
  {
      String representation[] = new String[5];
      for (int i=0; i<5; ++i)
          representation[i] = "";
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
//      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
    
      boolean isPoint = false, isLine = false, isPolygon = false, isComplexObj = false;
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {         
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (parentClass.equals(name)) { 
                isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
            }
            
            for( int b = 0; b < desc.length; b++ ) {
                if (desc[b].equals(name)) {   
                    isPoint = (isPoint || checkRepresentation( geoobj, "isPoint"));
                    isLine = (isLine || checkRepresentation( geoobj, "isLine"));
                    isPolygon = (isPolygon || checkRepresentation( geoobj, "isPolygon"));
                    isComplexObj = (isComplexObj || checkRepresentation( geoobj, "isComplexObj" ));
                }
            }                
            
      }
      
      
      boolean isGridOfCels = false, isAdjPolygons = false, isIsolines = false, isGridOfPoints = false, isTIN = false, isIrregularPoints = false;
      for( int i = 0; i < geofields.getLength(); i++ )
      {       
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
                                                       
             if (parentClass.equals(name)) {     //ATENCAO - VERIFICAR SE PRECISA DO "OU" ||                       
                 isGridOfCels = (isGridOfCels || checkRepresentation( geofield, "isGridOfCels"));
                     isAdjPolygons = (isAdjPolygons || checkRepresentation( geofield, "isAdjPolygons"));
                     isIsolines = (isIsolines || checkRepresentation( geofield, "isIsolines"));
                     isGridOfPoints = (isGridOfPoints || checkRepresentation( geofield, "isGridOfPoints" ));
                     isTIN = (isTIN || checkRepresentation( geofield, "isTIN"));
                     isIrregularPoints = (isIrregularPoints || checkRepresentation( geofield, "isIrregularPoints" ));
             }
             
             for( int b = 0; b < desc.length; b++ ) {
                 if (desc[b].equals(name)) {
                     isGridOfCels = (isGridOfCels || checkRepresentation( geofield, "isGridOfCels"));
                     isAdjPolygons = (isAdjPolygons || checkRepresentation( geofield, "isAdjPolygons"));
                     isIsolines = (isIsolines || checkRepresentation( geofield, "isIsolines"));
                     isGridOfPoints = (isGridOfPoints || checkRepresentation( geofield, "isGridOfPoints" ));
                     isTIN = (isTIN || checkRepresentation( geofield, "isTIN"));
                     isIrregularPoints = (isIrregularPoints || checkRepresentation( geofield, "isIrregularPoints" ));
                 }
              }                              
      }
      
      
      if (isPoint || isGridOfPoints || isIrregularPoints) representation[0] = "Ponto";
      if (isLine || isIsolines)  representation[1] = "Linha";
      if (isPolygon || isAdjPolygons || isTIN) representation[2] = "Pol";
      if (isComplexObj) representation[3] = "Complex";
      if (isGridOfCels) representation[4] = "Celula";
      
      //ATENCAO TERMINAR OBJ NAO GEO
      return representation;
      
  }
  
  
  public boolean[] getRep(String parentClass, Element pack ) 
  {
      boolean representation[] = new boolean[5];
      for (int i=0; i<5; ++i)
          representation[i] = false;
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
    
      boolean isPoint = false, isLine = false, isPolygon = false, isComplexObj = false;
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {         
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (parentClass.equals(name)) { 
                isPoint = checkRepresentation( geoobj, "isPoint");
                isLine = checkRepresentation( geoobj, "isLine");
                isPolygon = checkRepresentation( geoobj, "isPolygon");
                isComplexObj = checkRepresentation( geoobj, "isComplexObj" );
            }
                                       
      }
           
      boolean isGridOfCels = false, isAdjPolygons = false, isIsolines = false, isGridOfPoints = false, isTIN = false, isIrregularPoints = false;
      for( int i = 0; i < geofields.getLength(); i++ )
      {       
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
                                                       
             if (parentClass.equals(name)) {                            
                 isGridOfCels = checkRepresentation( geofield, "isGridOfCels");
                 isAdjPolygons = checkRepresentation( geofield, "isAdjPolygons");
                 isIsolines = checkRepresentation( geofield, "isIsolines");
                 isGridOfPoints = checkRepresentation( geofield, "isGridOfPoints" );
                 isTIN = checkRepresentation( geofield, "isTIN");
                 isIrregularPoints = checkRepresentation( geofield, "isIrregularPoints");
             }
                              
      }
      
      representation[0] = (isPoint || isGridOfPoints || isIrregularPoints); //"Ponto";
      representation[1] = (isLine || isIsolines); //"Linha";
      representation[2] = (isPolygon || isAdjPolygons || isTIN); //"Pol";
      representation[3] = (isComplexObj); //"Complex";
      representation[4] = (isGridOfCels); //"Celula";
      
      //ATENCAO TERMINAR OBJ NAO GEO
      return representation;
      
  }
  
  public String[] uniao(boolean[] subRep, boolean[] parentRep) {
      boolean[] resposta = new boolean[subRep.length];
      String[] representation = new String[subRep.length];
      
      for(int i=0; i<subRep.length; ++i)
          resposta[i] = subRep[i] || parentRep[i];          
      
      for(int i=0; i<subRep.length; ++i)
          representation[i] = "";
      
      if (resposta[0]) representation[0] = "Ponto";
      if (resposta[1]) representation[1] = "Linha";
      if (resposta[2]) representation[2] = "Pol";
      if (resposta[3]) representation[3] = "Complex";
      if (resposta[4]) representation[4] = "Celula";
      
      return representation;
          
  }
  
  
  
  //retorna true se a classe pai de uma generalizacao possuir representacao geometrica, o que 
  //implica que as classes filhas tambem possuem representacao geometrica
  public boolean isGeo(String parentClass, Element pack ) {
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
     NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {         
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (parentClass.equals(name))
                return true;
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {       
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
                                                       
             if (parentClass.equals(name))
                 return true;
      }
      for( int i = 0; i < netobjs.getLength(); i++ )
      {       
            Element netobj = (Element)netobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
                                                       
             if (parentClass.equals(name))
                 return true;
      }
      return false;
  }
  
  public String getEstereotipoTemporal(String className, Element pack ) {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
       NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      boolean isInstant = false, isInterval = false;
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (className.equals(name)) { 
                isInstant = checkRepresentation( geoobj, "isInstant");
                isInterval = checkRepresentation( geoobj, "isInterval");
                
                if(isInstant) 
                    return "instante";
                else if (isInterval)
                    return "intervalo";
                else
                    return "";
            }
      }
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );                
                
            if (className.equals(name)) { 
                isInstant = checkRepresentation( geoobj, "isInstant");
                isInterval = checkRepresentation( geoobj, "isInterval");
                
                if(isInstant) 
                    return "instante";
                else if (isInterval)
                    return "intervalo";
                else
                    return "";
            }
      }
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
            Element netobj = (Element)netobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );                
            
            if (className.equals(name)) { 
                isInstant = checkRepresentation( netobj, "isInstant");
                isInterval = checkRepresentation( netobj, "isInterval");
                
                if(isInstant) 
                    return "instante";
                else if (isInterval)
                    return "intervalo";
                else
                    return "";
            }
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );                
            
            if (className.equals(name)) { 
                isInstant = checkRepresentation( geofield, "isInstant");
                isInterval = checkRepresentation( geofield, "isInterval");
                
                if(isInstant) 
                    return "instante";
                else if (isInterval)
                    return "intervalo";
                else
                    return "";
            }
      }
      
      return "";      
  }
  
  
  // dado o nome de uma classe, retorna sua chave prim�ria
  public String getPrimaryKey( String parentClass, Element pack ) throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
       NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
            
            if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = geoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = geoobj.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) 
                       {
                           primaryKey = attributeName;
                           return primaryKey;
                       }
                   }
                   
                   return primaryKey;
            }
           
           
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
            Element ngeoobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = ngeoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = ngeoobj.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) primaryKey = attributeName;
                   }
                   
                   return primaryKey;
            }
           
           
      }
       
       for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
            Element netobj = (Element)netobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = netobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = netobj.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) primaryKey = attributeName;
                   }
                   
                   return primaryKey;
            }
           
           
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = geofield.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = geofield.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) primaryKey = attributeName;
                   }
                   
                   return primaryKey;
            }
                      
      } 
      
      String vazia = "";
      return vazia;
      
  }
  
  public List getAttributeList( String parentClass, Element pack, boolean p ) throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
       NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      List myAttributes = new ArrayList(); 
   
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
            
            if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = geoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                    
                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {  
                               pKey = true;
                           }
                       }

                       if( p )
                       {
                           
                           if( pKey )
                           {  
                               pKey = false;
                               continue;
                           }
                       }
                        myAttributes.add( attributeName );

                   }
                   
                
                 return myAttributes;
            }
           
           
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
            Element ngeoobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = ngeoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                      

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = ngeoobj.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                           if( p )
                       {
                           
                           if( pKey ) continue;
                       }
                        myAttributes.add( attributeName );
                   }
                   
                  return myAttributes;
            }
           
           
      }
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
            Element netobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = netobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                      

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = netobj.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                           if( p )
                       {
                           
                           if( pKey ) continue;
                       }
                        myAttributes.add( attributeName );
                   }
                   
                  return myAttributes;
            }
           
           
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {

                   // atributos
                   NodeList attributes = geofield.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                     

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = geofield.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                           if( p )
                       {
                           
                           if( pKey ) continue;
                       }
                        myAttributes.add( attributeName );
                   }
                   
                  return myAttributes;
            }
                      
      } 
      
     throw new Exception("erro legal");
      
  }
  
  
  // retorna lista de descendentes de uma determinada classe
  public List getDescendant( String parentClass, Element pack ) throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      List result = new ArrayList();
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
          Element geoobj = (Element) geoobjs.item(i);
          
          String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
          if( parentClass.equals(name))
          {
              NodeList list = geoobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++)
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String child = getChild(idRef, pack );
                  String childClass = getClassName2(child, pack);
                  result.add(childClass);                  
              }   
              
          }
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
          Element ngeoobj = (Element) ngeoobjs.item(i);
          
          String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
          if( parentClass.equals(name))
          {
              NodeList list = ngeoobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++)
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String child = getChild(idRef, pack );
                  String childClass = getClassName2(child, pack);
                  result.add(childClass);                  
              }   
              
          }
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
          Element geofield = (Element) geofields.item(i);
          
          String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
          if( parentClass.equals(name))
          {
              NodeList list = geofield.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++)
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String child = getChild(idRef, pack );
                  String childClass = getClassName2(child, pack);
                  result.add(childClass);                  
              }   
              
          }
      }
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
          Element netobj = (Element) netobjs.item(i);
          
          String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
          if( parentClass.equals(name))
          {
              NodeList list = netobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++)
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String child = getChild(idRef, pack );
                  String childClass = getClassName2(child, pack);
                  result.add(childClass);                  
              }   
              
          }
      }
      
      return result;    
       
  }
  
  
  // retorna a classe pai de uma determinada subclasse
  public String getParent( String descendantClass, Element pack ) throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      //List result = new ArrayList();
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
          Element geoobj = (Element) geoobjs.item(i);
          
          String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
          if( descendantClass.equals(name))
          {
              NodeList list = geoobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
              {
                  Element gen  = (Element) list.item(j);                  
                  String idRef = gen.getAttribute("xmi.idref");
                  String parent = getSuper(idRef, pack );
                  String parentClass = getClassName2(parent, pack);
                  //result.add(parentClass);
                  return parentClass;
              }
              return "";
              
          }
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
          Element ngeoobj = (Element) ngeoobjs.item(i);
          
          String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
          if( descendantClass.equals(name))
          {
              NodeList list = ngeoobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String parent = getSuper(idRef, pack );
                  String parentClass = getClassName2(parent, pack);
                  return parentClass;                  
              }   
              return "";
          }
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
          Element geofield = (Element) geofields.item(i);
          
          String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
          if( descendantClass.equals(name))
          {
              NodeList list = geofield.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++)
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String parent = getSuper(idRef, pack );
                  String parentClass = getClassName2(parent, pack);
                  return parentClass;
              }   
              return "";
          }
      }
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
          Element netobj = (Element) netobjs.item(i);
          
          String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
          if( descendantClass.equals(name))
          {
              NodeList list = netobj.getElementsByTagName("Foundation.Core.Generalization");
              for( int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
              {
                  Element gen  = (Element) list.item(j);
                  String idRef = gen.getAttribute("xmi.idref");
                  String parent = getSuper(idRef, pack );
                  String parentClass = getClassName2(parent, pack);
                  return parentClass;                  
              }   
              return "";
          }
      }
      //return result;
      
      return "erro!!!!!!!!!!!!!11"; 
  }
  
  
  public String getSuper( String idRef, Element pack )
  {
      NodeList generalizations = pack.getElementsByTagName("Foundation.Core.Generalization");
      for( int i = 0; i < generalizations.getLength(); i++ )
      {
          Element generalization = (Element)generalizations.item(i);
          String attr = generalization.getAttribute("xmi.id");
          if( attr.equals(idRef))
          {
                NodeList children = generalization.getElementsByTagName("Foundation.Core.GeneralizableElement");
                Element child = (Element) children.item(1); //troquei 0 por 1
                String id = child.getAttribute("xmi.idref");
                return id;             
          }
          else
              continue;
      }
      
      return "erro!!!";
  }
  
  public char getOp() {
      return op;
  }
  
  
  
  //funcao parecida com a getPK com a diferenca de que testa se a classe participa de uma generalizacao,
  //sendo que se o resultado for verdadeiro, atualiza sua chave primaria, pegando a PK da classe pai
  public List getPKreal( String classe, Element model ) throws Exception {
        List PKsClasse = new ArrayList();
        PKsClasse = getPK(classe,model);
        //Dependendo de como o generalizacao eh tratada, haveria erro se subclasse nao tivesse chave primaria             
        String paiClasse = getParent(classe, model);
        char op = getOp();
             
        //if( op != 'C' && !paiClasse.equals("") && !paiClasse.equals(classe)) { 
        if( !paiClasse.equals("") && !paiClasse.equals(classe)) {
               //classe eh uma subclasse
               System.out.println("----");
               System.out.println(PKsClasse);
               System.out.println(classe + " eh subclasse e ainda nao possui chave primaria");
               List parentPK = new ArrayList(); 
               parentPK = getPK(paiClasse,model);
               System.out.println(parentPK);
               System.out.println(op);
               //if(op == 'A') {
                  System.out.println("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                  PKsClasse.clear();
                  PKsClasse.addAll(parentPK);
                  
               //}   
               /*else {
                   //fazer este comando primeiro e nao PKsClasse1.addAll(parentPK); para os atributo que farao parte da chave estrangeira aparecerem na mesma ordem (senao haveria erro)
                   parentPK.addAll(PKsClasse); 
                   PKsClasse = parentPK;
               }*/
               System.out.println("Chave primaria atual: " + PKsClasse);
               System.out.println("----");
        }
        
        return PKsClasse;
  }
  
  
  
  
 //dado o nome de uma classe, retorna  uma lista com 
 //todos os atributos que fazem parte de sua chave prim�ria, incluindo o tipo.
 //O primeiro elemento da lista retornada eh o nome do atributo, o segundo eh seu tipo, o terceiro o nome de outro atributo... 
 //A funcao getPrimaryKey devera ser apagada depois, pois nao retorna a chave completa
  public List getPK( String classe, Element model ) throws Exception
  {
      
      NodeList geoobjs = model.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = model.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = model.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = model.getElementsByTagName("Foundation.Core.NetworkObject");
      
      List primaryKey = new ArrayList();
      
      String attrID = "";
      String attributeType = "";
      
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
            
            if( !(classe.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = geoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                     
                 for( int j = 0; j < attributes.getLength(); j++ )
                 {
                         
                     
                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);                                                                                
                       
                       
                       // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );  //DEVE PEGAR ATTR E NAO GEOOBJ (VERIFICAR SE ESTE ERRO EXISTE EM OUTRAS PARTES DO CODIGO)
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           //System.out.println("Atributo "+attributeName+"Loop "+k+" "+teste+" "+teste2);
                           
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) 
                       {
                           // tipo do atributo 
                       attrID = classify.getAttribute("xmi.idref");
                       attributeType = getType( model, attrID ); 
                           primaryKey.add(attributeName);
                           primaryKey.add(attributeType);
                           pKey = false;
                           
                       }
                   }
                   
                   return primaryKey;
            }
           
           
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
            Element ngeoobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
            
              if( !(classe.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = ngeoobj.getElementsByTagName( "Foundation.Core.Attribute" );    


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) 
                       {
                           // tipo do atributo 
                       attrID = classify.getAttribute("xmi.idref");
                       attributeType = getType( model, attrID ); 
                           primaryKey.add(attributeName);
                           primaryKey.add(attributeType);
                           pKey = false;
                           
                       }
                   }
                   
                   return primaryKey;
            }
           
           
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
            
              if( !(classe.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = geofield.getElementsByTagName( "Foundation.Core.Attribute" );     

                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) {
                           // tipo do atributo 
                           attrID = classify.getAttribute("xmi.idref");
                           attributeType = getType( model, attrID ); 
                           primaryKey.add(attributeName);
                           primaryKey.add(attributeType);
                           pKey = false;
                           
                       }
                   }
                   
                   return primaryKey;
            }
                      
      } 
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
            Element netobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
            
              if( !(classe.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = netobj.getElementsByTagName( "Foundation.Core.Attribute" );    


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );


                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( pKey ) 
                       {
                           // tipo do atributo 
                       attrID = classify.getAttribute("xmi.idref");
                       attributeType = getType( model, attrID ); 
                           primaryKey.add(attributeName);
                           primaryKey.add(attributeType);
                           pKey = false;
                           
                       }
                   }
                   
                   return primaryKey;
            }
           
           
      }
      
      //String vazia = "";
      return null;
      
  } 
  
      
      
  public String getChild( String idRef, Element pack )
  {
      NodeList generalizations = pack.getElementsByTagName("Foundation.Core.Generalization");
      for( int i = 0; i < generalizations.getLength(); i++ )
      {
          Element generalization = (Element)generalizations.item(i);
          String attr = generalization.getAttribute("xmi.id");
          if( attr.equals(idRef))
          {
                NodeList children = generalization.getElementsByTagName("Foundation.Core.GeneralizableElement");
                Element child = (Element) children.item(0);
                String id = child.getAttribute("xmi.idref");
                return id;             
          }
          else
              continue;
      }
      
      return "erro!!!";
  }
      
  // obt�m a multiplicidade de determinado elemento
  // true para muitos e false caso contr�rio
  public boolean getMultiplicity( Element multiplicity, Element elem )
  {     
          
     String inferior = "", superior = "";
     try
     {
       inferior = getChildTagValue( multiplicity, "Foundation.Data_Types.MultiplicityRange.lower");
       superior = getChildTagValue( multiplicity, "Foundation.Data_Types.MultiplicityRange.upper");
     }
     catch( Exception e )
     {
         System.out.println("Multiplicidade com problemas!");
     }
  
      if( inferior == null)
      {
          NodeList l = multiplicity.getElementsByTagName("Foundation.Data_Types.Multiplicity");
          Element e = (Element)l.item(0);
          String ref = e.getAttribute("xmi.idref");       
          return searchID(ref, elem);         
      }    
      else
      {
          if ( superior.equals("-1"))
              return true;
          else
              return false; 
          
      }    
      
  }

  // Dado o ID, retorna o nome da classe
  public String getClassName2( String idRef, Element pack ) throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
     
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {
          Element geoobj = (Element) geoobjs.item(i);
          String attr = geoobj.getAttribute("xmi.id");
       
          if( idRef.equals(attr))
          {
              String className = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
              return className;
          }         
      }
      
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          Element ngeoobj = (Element) ngeoobjs.item(i);
          String attr = ngeoobj.getAttribute("xmi.id"); System.out.println("ids que encontro: " + attr );
          if( idRef.equals(attr))
          { 
              String className = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
              return className;
          }         
      }
      
      for( int i = 0; i < geofields.getLength(); i++ )
      {
          Element geofield = (Element) geofields.item(i);
          String attr = geofield.getAttribute("xmi.id"); System.out.println("ids que encontro: " + attr );
          if( idRef.equals(attr))
          { 
              String className = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
              return className;
          }         
      }    
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          Element netobj = (Element) netobjs.item(i);
          String attr = netobj.getAttribute("xmi.id"); System.out.println("ids que encontro: " + attr );
          if( idRef.equals(attr))
          { 
              String className = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
              return className;
          }         
      }
      
      return "erro";
  }
  
  
  public String getClassName1( Element relation, Element pack ) throws Exception
  {
       NodeList classifiers = relation.getElementsByTagName("Foundation.Core.Classifier");
       Element classifier = (Element)classifiers.item(0);
       String idref = classifier.getAttribute("xmi.idref");
       
       //pesquisa nas classes de objetos geogr�ficos
       NodeList geoobjs = pack.getElementsByTagName( "Foundation.Core.GeographicObject" );
       //pesquisa nas classes de objetos n�o-geogr�ficos
       NodeList ngeoobjs = pack.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
       // pesquisa nas classes de campos geogr�ficos
       NodeList geofields = pack.getElementsByTagName( "Foundation.Core.GeographicField" );
       
       NodeList netobjs = pack.getElementsByTagName( "Foundation.Core.NetworkObjects" );
       
       if( geoobjs.getLength() > 0)
       {
          
           for( int i = 0; i < geoobjs.getLength(); i++ )
           {
                Element geoobj = (Element) geoobjs.item(i);
                String attribute = geoobj.getAttribute("xmi.id");
                if( attribute.equals(idref))
                {
                    String className = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
                    return className;
                                
                }
           }
       }
       
       if( ngeoobjs.getLength() > 0)
       {
          
           for( int i = 0; i < ngeoobjs.getLength(); i++ )
           {
                Element ngeoobj = (Element) ngeoobjs.item(i);
                String attribute = ngeoobj.getAttribute("xmi.id");
                if( attribute.equals(idref))
                {
                    String className = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
                    return className;
                                
                }
           }
       }
       
       if( geofields.getLength() > 0)
       {
          
           for( int i = 0; i < geofields.getLength(); i++ )
           {
                Element geofield = (Element) geofields.item(i);
                String attribute = geofield.getAttribute("xmi.id");
                if( attribute.equals(idref))
                {
                    String className = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
                    return className;
                                
                }
           }
       }
       if( netobjs.getLength() > 0)
       {
          
           for( int i = 0; i < netobjs.getLength(); i++ )
           {
                Element netobj = (Element) netobjs.item(i);
                String attribute = netobj.getAttribute("xmi.id");
                if( attribute.equals(idref))
                {
                    String className = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
                    return className;
                                
                }
           }
       }
       
       throw new Exception("Erro!!!!!!!!!!");
       
           
      
      
  }
  
  public boolean searchID( String ref, Element elem )
  {
      
      NodeList list = elem.getElementsByTagName("Foundation.Data_Types.Multiplicity");
      String value = "";
      
      try
          {
          for( int i = 0; i < list.getLength(); i++ )
          {

              Element e = (Element)list.item(i);
              String attr = e.getAttribute("xmi.id");
              if( ref.equals(attr))
              {
                  value = getChildTagValue(e, "Foundation.Data_Types.MultiplicityRange.upper");

              }

          }
      }
      
      catch(Exception e)
      {
          System.out.println("Deu erro!");
          
          
      }
      
      return (value.equals("-1"));     
      
  }
  
  
//TEMP{
  //fun��o que testa se as classes do esquema possuem chave prim�ria
  //se existe classe sem chave prim�ria, retorna true e imprime o nome da classe 
  //TERMINAR == TESTAR CLASSES CAMPOGEOGRAFICO E OBJNAOGEOGRAFICO
  public boolean testaEsquema (Element model) {

    //PEGANDO ELEMENTOS DO MODELO E NAO DO PACOTE, ELE VERIFICA AS CLASSES QUE FORAM DELETADAS DO ESQUEMA MAS QUE CONTINUAM DISPON�VEIS NO PAINEL DO CANTO ESQUERDO DA FERRAMENTA  
    NodeList geoobjs = model.getElementsByTagName( "Foundation.Core.GeographicObject" );
    String classesSemPK = "";
    boolean temErro = false;
    
    try {
    for( int i = 0; i < geoobjs.getLength(); i++ )
    {
        boolean temPK = false;
        Element geo = (Element)geoobjs.item(i);
        String nome = getChildTagValue( geo, "Foundation.Core.ModelElement.name" );
        
     
     

    /*    if (nome.indexOf(' ') != -1) {
          StringTokenizer st = new StringTokenizer(nome);
          
          String outra = nome;
          outra = outra.replace(' ', '_');
          System.out.println(outra);
          
          nome = "";  
          
          while (st.hasMoreTokens()) 
              nome += st.nextToken();                

          System.out.println(nome);   
        }  
     */           
        //ATENCAO = FAZER O MESMO PARA CAMPO E NGEO
        String pai = getParent(nome, model);
        if( !pai.equals(nome) && !pai.equals("") ) {
            continue; //se for subclasse, eh permitido que ela nao tenha chave primaria
        }
        
   /*     if (nome.indexOf(' ') != -1) {
            //o nome da classe possui espaco em branco, o que gera erro se nao for substituido
            NodeList teste = geo.getElementsByTagName("Foundation.Core.ModelElement.name");
            Node el = teste.item(0);
            Node nomeClasse = el.getFirstChild();
            
            String novoNome = nomeClasse.getNodeValue();
            novoNome = novoNome.replace(' ', '_');
            
            nomeClasse.setNodeValue(novoNome); //ATENCAO = REVER
        }
    */    
        NodeList attributes = geo.getElementsByTagName( "Foundation.Core.Attribute" );
 
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           //child.getFirstChild().getNodeValue(); ATENCAO TESTE
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                  
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ ) {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               
               if (teste.equals("primary key")&& teste2.equals("true") )
                   temPK = true;
           }
                             
       }
        
        
           if (!temPK) {
             classesSemPK += (nome + '\n');
             temErro = true;
           }
               
    }
    
   }     
    
    catch (Exception e) {
    System.out.println("Erro em testaEsquema(Element) !!!!!!!!!!");
    }
    
    
    NodeList ngeoobjs = model.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
    
    try {
    for( int i = 0; i < ngeoobjs.getLength(); i++ )
    {
        boolean temPK = false;
        Element ngeo = (Element)ngeoobjs.item(i);
        String nome = getChildTagValue( ngeo, "Foundation.Core.ModelElement.name" );
        
        NodeList attributes = ngeo.getElementsByTagName( "Foundation.Core.Attribute" );
        
 
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                  
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ ) {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               
               if (teste.equals("primary key")&& teste2.equals("true") )
                   temPK = true;
           }
                             
       }
        
        
           if (!temPK) {
             classesSemPK += (nome + '\n');
             temErro = true;
           }
               
    }
    

   }     
    
    catch (Exception e) {
    System.out.println("Erro em testaEsquema(Element) !!!!!!!!!!");
    }
    
    
    NodeList geofields = model.getElementsByTagName( "Foundation.Core.GeographicField" );
    
    try {
    for( int i = 0; i < geofields.getLength(); i++ )
    {
        boolean temPK = false;
        Element geofieldobj = (Element)geofields.item(i);
        String nome = getChildTagValue( geofieldobj, "Foundation.Core.ModelElement.name" );
        
        NodeList attributes = geofieldobj.getElementsByTagName( "Foundation.Core.Attribute" );
        
 
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                  
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ ) {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               
               if (teste.equals("primary key")&& teste2.equals("true") )
                   temPK = true;
           }
                             
       }
        
        
           if (!temPK) {
             classesSemPK += (nome + '\n');
             temErro = true;
           }
               
    }
    
    if(temErro) {
      JOptionPane.showMessageDialog(null, "Existe classe sem chave primaria: \n" + classesSemPK, "ERROR" , JOptionPane.ERROR_MESSAGE);
    
      /*JOptionPane painel = new JOptionPane();

      int opcao = painel.showOptionDialog(null, "Nao pode haver espacos em branco no nome das classes!\nClique em OK para troca-los por _ ou em CANCEL para retornar e renomear as classes" , "Warning", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, null, null);     

      if(opcao == (painel.CANCEL_OPTION) || opcao == (painel.CLOSED_OPTION))
        System.out.println("NAO DEIXE CRIAR BD");  */
    }
    
   }         
    
    
    
    catch (Exception e) {
    System.out.println("Erro em testaEsquema(Element) !!!!!!!!!!");
    } 
    
    
    
    NodeList netobjs = model.getElementsByTagName( "Foundation.Core.NonGeographicObject" );
    
    try {
    for( int i = 0; i < netobjs.getLength(); i++ )
    {
        boolean temPK = false;
        Element net = (Element)netobjs.item(i);
        String nome = getChildTagValue( net, "Foundation.Core.ModelElement.name" );
        
        NodeList attributes = net.getElementsByTagName( "Foundation.Core.Attribute" );
        
 
       for( int j = 0; j < attributes.getLength(); j++ )
       {
           
           Element attr = (Element)attributes.item(j);
           // nome do atributo
           String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                  
           NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
           for( int k = 0; k < pKeys.getLength(); k++ ) {
               Element temp = (Element)pKeys.item(k);
               String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
               String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
               
               if (teste.equals("primary key")&& teste2.equals("true") )
                   temPK = true;
           }
                             
       }
        
        
           if (!temPK) {
             classesSemPK += (nome + '\n');
             temErro = true;
           }
               
    }
    

   }     
    
    catch (Exception e) {
    System.out.println("Erro em testaEsquema(Element) !!!!!!!!!!");
    }
    
  
        
  return temErro;
  }
  
  
  //Esta funcao tem como parametro o esquema criado pelo usuario
  //Verifica se alguma classe possui espaco em braco no nome e se possui, entao troca por '_'
  public Element tiraEspacos(Element model) {
    
    NodeList geoobjs = model.getElementsByTagName( "Foundation.Core.GeographicObject" );   

    for( int i = 0; i < geoobjs.getLength(); i++ )
    {
        Element geo = (Element)geoobjs.item(i);
        String nome = getChildTagValue( geo, "Foundation.Core.ModelElement.name" );
        
        if (nome.indexOf(' ') != -1) {
            //o nome da classe possui espaco em branco, o que gera erro se nao for substituido
            NodeList teste = geo.getElementsByTagName("Foundation.Core.ModelElement.name");
            Node el = teste.item(0);
            Node nomeClasse = el.getFirstChild();
            
            String novoNome = nomeClasse.getNodeValue();
            novoNome = novoNome.replace(' ', '_');
            
            nomeClasse.setNodeValue(novoNome); //ATENCAO = REVER
        }
 
    }
    
    return model; //ATENCAO = FAZER PRA NGEO E CAMPO
  }
  
  
  
  
  
  //DEPOIS DE TERMINAR ESTE METODO, APAGAR O getAttributeList
  //dado o nome de uma classe, retorna  uma lista com 
  //todos os atributos da classe, incluindo o tipo.
  //O primeiro elemento da lista retornada eh o nome do atributo, o segundo eh seu tipo, o terceiro o nome de outro atributo... 
  //Se o ultimo parametro for false, entao retorna os atributos, incluindo chave primaria, senao retorna somente os atributos que nao fazem parte da chave primaria
  public List getAttributeList2( String parentClass, Element pack, boolean p ) //throws Exception
  {
      
      NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
      NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
      NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");
      NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
      
      
      List myAttributes = new ArrayList(); 
      
      String attrID = ""; 
      String attributeType = "";
    
      for( int i = 0; i < geoobjs.getLength(); i++ )
      {       
            Element geoobj = (Element)geoobjs.item(i); 
            String name = getChildTagValue( geoobj, "Foundation.Core.ModelElement.name" );
            
            if( !(parentClass.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = geoobj.getElementsByTagName( "Foundation.Core.Attribute" );
//                   String primaryKey = "";      

                 for( int j = 0; j < attributes.getLength(); j++ )
                 {
                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );                   
                       
                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);
                       
                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true"))
                           {  
                               pKey = true;
                           }
                       }

                       if( p ) //ATENCAO - FAZER MAIS EFICIENTE
                       {
                           
                           if( pKey )
                           {  
                               pKey = false;
                               continue;
                           }
                       }
                        myAttributes.add( attributeName );
                        // tipo do atributo 
                        attrID = classify.getAttribute("xmi.idref");
                        attributeType = getType( pack, attrID );
                        
                        myAttributes.add(attributeType);  //adiciona o tipo do atributo tambem
                   }
                 //System.out.println(myAttributes);                  
                 //return myAttributes;
            }
           
           
      }
     
      for( int i = 0; i < ngeoobjs.getLength(); i++ )
      {
          
            Element ngeoobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( ngeoobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = ngeoobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                      

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;

                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );  
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                        if( p ) //ATENCAO - FAZER MAIS EFICIENTE
                       {
                           
                           if( pKey )
                           {  
                               pKey = false;
                               continue;
                           }
                       }
                        myAttributes.add( attributeName );
                        
                        // tipo do atributo 
                        attrID = classify.getAttribute("xmi.idref");
                        attributeType = getType( pack, attrID );
                        
                        myAttributes.add(attributeType);  //adiciona o tipo do atributo tambem
                   }
                   
                  //return myAttributes;
            }
           
           
      }

      for( int i = 0; i < geofields.getLength(); i++ )
      {
          
            Element geofield = (Element)geofields.item(i); 
            String name = getChildTagValue( geofield, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {
                 // atributos
                 NodeList attributes = geofield.getElementsByTagName( "Foundation.Core.Attribute" );
     
                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                     

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;
                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                       if( p )
                       {                                                      
                           if( pKey )
                           {  
                               pKey = false;
                               continue;
                           }
                       }
                        myAttributes.add( attributeName );
                        // tipo do atributo 
                        attrID = classify.getAttribute("xmi.idref");
                        attributeType = getType( pack, attrID );
                        
                        myAttributes.add(attributeType);  //adiciona o tipo do atributo tambem
                   }
                   
                  System.out.println(myAttributes);
                  //return myAttributes;
            }     
                      
      } 
      
      for( int i = 0; i < netobjs.getLength(); i++ )
      {
          
            Element netobj = (Element)ngeoobjs.item(i); 
            String name = getChildTagValue( netobj, "Foundation.Core.ModelElement.name" );
            
              if( !(parentClass.equals(name)) )
                continue;
            else
            {
                   // atributos
                   NodeList attributes = netobj.getElementsByTagName( "Foundation.Core.Attribute" );
                   String primaryKey = "";      


                 for( int j = 0; j < attributes.getLength(); j++ )
                 {

                       Element attr = (Element)attributes.item(j);
                       // nome do atributo
                       String attributeName = getChildTagValue( attr, "Foundation.Core.ModelElement.name" );
                      

                       NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                       Element structType = (Element)structFields.item(0);

                       NodeList classifiers = structType.getElementsByTagName( "Foundation.Core.Classifier" );
                       Element classify = (Element)classifiers.item(0);

                      // chave prim�ria ou n�o
                       boolean pKey = false;

                       NodeList pKeys = attr.getElementsByTagName( "Foundation.Extension_Mechanisms.TaggedValue" );  
                       for( int k = 0; k < pKeys.getLength(); k++ )
                       {       

                           Element temp = (Element)pKeys.item(k);
                           String teste = getChildTagValue( temp, "Foundation.Extension_Mechanisms.TaggedValue.tag" );
                           String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                           if (teste.equals("primary key")&& teste2.equals("true") )
                           {
                               pKey = true;
                           }
                       }

                        if( p ) //ATENCAO - FAZER MAIS EFICIENTE
                       {
                           
                           if( pKey )
                           {  
                               pKey = false;
                               continue;
                           }
                       }
                        myAttributes.add( attributeName );
                        
                        // tipo do atributo 
                        attrID = classify.getAttribute("xmi.idref");
                        attributeType = getType( pack, attrID );
                        
                        myAttributes.add(attributeType);  //adiciona o tipo do atributo tambem
                   }
                   
                  //return myAttributes;
            }
           
           
      }
      
      
     return myAttributes;
     //throw new Exception("erro em getAttributeList2");
      
  }
  
//TEMP}  

  
  public static void main( String args[] )
  {
      String s = "file:///C:\\Documents and Settings\\Usuario\\Desktop\\genera.xmi"; //relacao.xmi";  //repolho.xmi"; //assoc.xmi";
      XmlReader app = new XmlReader (s);
      try
      {
         app.lerXMI();
   
      }
      catch( Exception e )
      {
          System.err.println("Error reading xmi file!");
          System.exit(1);
      }
  }
 
}

// classe para armazenar tipos de dados e classes criadas no diagrama
class DataType
{
    private String id;
    private String name;
    
    public DataType( String id, String name )
    {
        this.id = id;
        this.name = name;
    }
    
    public void setId( String id )
    {
        this.id = id;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String toString()
    {
        String s = "Name " + name + "\n" + "ID: " + id;
        return s;
    }
}