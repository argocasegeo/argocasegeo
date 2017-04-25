/*
 * ShapeFile.java
 *
 * Created on 14 de Julho de 2004, 10:47
 *
 * Shape Record Reader
 */

package org.argouml.shapefile;

import java.util.Vector;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author  Valerio
 */
public class ShapeFile {
    
    private int shapeType;
    private boolean eof;
    private BoundingBox box;            // área de abrangência
    private String fileName, filePath;
    
    //private long numRecs;               // numero de registros
    //private int numFields;              // Numero de colunas do banco de dados
    
    //private DBFFile DBFFile_;
    
    private Vector records;
    private Vector fields;
    
    private File SHPFile, SHXFile;
    private DataInputStream SHPReader, SHXReader;
    private DataOutputStream SHPWriter, SHXWriter;
    
    // variaveis a serem atualizadas no momento de gravação
    // armazenam o tamanho dos arquivos
    private int SHPFL, SHXFL;
    
    
    /** Cria uma nova instancia de ShapeFile */
    public ShapeFile() {
    }
    
    public ShapeFile( int shapeType_, String name, String path ) {
        shapeType = shapeType_;
        records = new Vector();
        fields = new Vector();
        box = new BoundingBox( 0, 0, 0, 0 );
        //DBFFile_ = new DBFFile( path + name + ".dbf" );
        fileName = new String( name );
        filePath = new String( path );
        //numRecs = 0;
        //numFields = 0;
        
    }
    
    // Abre o arquivo SHP armazenado em disco. Devolve o sucesso da operação
    public boolean open( String path, String name ) {
        DBFField DBFField_;         // Unit Shapes
        //DBField DBField_;           // Unit DBFUnit
        
        records = new Vector();
        fields = new Vector();
        box = new BoundingBox();
        //DBFFile_ = new DBFFile( path + name + ".dbf" );
        filePath = new String( path );
        fileName = new String( name );
        
        // Abrir o arquivo
        try {
            SHPFile = new File( filePath + fileName + ".shp" );
            SHPReader = new DataInputStream( new FileInputStream( SHPFile ) );
            
            SHPReader.skip( 32 );
            // Leitura do cabecalho e atribuições
            shapeType = BRi( "SHP" );           // Shape Type
            box.setXMin( BRr( "SHP" ) );        // Coord. Box.XMin
            box.setYMin( BRr( "SHP" ) );        // Coord. Box.YMin
            box.setXMax( BRr( "SHP" ) );        // Coord. Box.XMax
            box.setYMax( BRr( "SHP" ) );        // Coord. Box.YMax
            
            // Carregar estrutura do DBF
            /*if( DBFFile_.load() ) {
                for( int nf = 1; nf <= DBFFile_.getNumFields(); nf++ ) {
                    DBFField_ = new DBFField();
                    fields.add( DBFField_ );
                    //DBField  := DBFFile.Fields.Items[nf-1];
                    DBFField_.setNome( DBField_.getNome() );
                    DBFField_.setFType( DBField_.getFType() );
                    DBFField_.setFLength( DBField_.getFLength() );
                    DBFField_.setFDec( DBField_.getFDec() );
                }
            }*/
           
            SHPReader.close();
            
            return true;
        } catch( IOException ioe ) {
            return false;
        }
    }
    
    // Inverte a BYTE Order (BIG->Little e VV) de um inteiro
    public static int invByteOrder( int num ) {
        byte [] i = new byte[8];
        i[0] = (byte)( num );
        i[1] = (byte)( num >> 8 );
        i[2] = (byte)( num >> 16 );
        i[3] = (byte)( num >> 24 );
        
        return (int)( ( i[0] & 0xff ) << 24 | ( i[1] & 0xff ) << 16 |
        ( i[2] & 0xff ) << 8  | ( i[3] & 0xff ) );
    }
    
    // Inverte a BYTE Order (BIG->Little e VV) de um double
    public double invByteOrder( double num ) {
        long bits = Double.doubleToLongBits( num );
        String bitString = new String( Long.toBinaryString( bits ) );
        
        while( bitString.length() < 64 )
            bitString = "0" + bitString;
        
        StringBuffer result = new StringBuffer(64);
        result.append( bitString.substring( 56, 64 ) + bitString.substring( 48, 56 ) +
        bitString.substring( 40, 48 ) + bitString.substring( 32, 40 ) +
        bitString.substring( 24, 32 ) + bitString.substring( 16, 24 ) +
        bitString.substring(  8, 16 ) + bitString.substring(  0,  8 ) );
        
        bits = Long.parseLong( result.substring(1), 2 );
        
        double ret = Double.longBitsToDouble( bits );
        if( result.charAt( 0 ) == '1' )
            ret *= -1;
        
        return ret;
    }
    
    // OutPut nº inteiro
    private void BWi( int Ni, String fileType ) {
        // Variavel criada para passagem de parametros do BlockWrite
        int nInt = Ni;
        
        try {
            // OutPut em arquivo UnTyped
            if( fileType.equalsIgnoreCase( "SHP" ) )
                SHPWriter.writeInt( nInt );
            else if( fileType.equalsIgnoreCase( "SHX" ) )
                SHXWriter.writeInt( nInt );
        } catch( IOException ioe ) {}
    }
    
    // OutPut nº real
    private void BWr( double Nr, String fileType ) {
        // Variavel criada para passagem de parametros do BlockWrite
        double nReal = Nr;
        
        try {
            // OutPut em arquivo UnTyped
            if( fileType.equalsIgnoreCase( "SHP" ) )
                SHPWriter.writeDouble( nReal );
            else if( fileType.equalsIgnoreCase( "SHX" ) )
                SHXWriter.writeDouble( nReal );
        } catch( IOException ioe ) {}
    }
    
    //Input nº Real
    private double BRr( String fileType ) {
        double nReal = 0;
        
        try {
            // InPut em arquivo UnTyped
            if( fileType.equalsIgnoreCase( "SHP" ) )
                nReal = SHPReader.readDouble();
            else if( fileType.equalsIgnoreCase( "SHX" ) )
                nReal = SHXReader.readDouble();
            return nReal;
        } catch( IOException ioe ) {
            return Double.NaN;
        }
    }
    
    //Input nº Inteiro
    private int BRi( String fileType ) {
        int nInt = 0;
        
        try {
            // InPut em arquivo UnTyped
            if( fileType.equalsIgnoreCase( "SHP" ) )
                nInt = SHPReader.readInt();
            else if( fileType.equalsIgnoreCase( "SHX" ) )
                nInt = SHXReader.readInt();
            return nInt;
        } catch( IOException ioe ) {
            return -1;
        }
    }
    
    // Escreve o cabeçalho
    private void writeHeader( String fileType ) {
        
        BWi( 9994, fileType );      // file code
        BWi( 0, fileType );         // unused
        BWi( 0, fileType );         // unused
        BWi( 0, fileType );         // unused
        BWi( 0, fileType );         // unused
        BWi( 0, fileType );         // unused
        
        if( fileType.equalsIgnoreCase( "SHP" ) )
            BWi( SHPFL, fileType );      // file length
        if( fileType.equalsIgnoreCase( "SHX" ) )
            BWi( SHXFL, fileType );      // file length
        
        if( box.getXMin() == box.getXMax() && box.getYMin() == box.getYMax() ) {
            box.setXMin( box.getXMin() - 1 );
            box.setYMin( box.getYMin() - 1 );
            box.setXMax( box.getXMax() + 1 );
            box.setYMax( box.getYMax() + 1 );
        }
        
        BWi( invByteOrder( 1000 ), fileType );              // Versao
        BWi( invByteOrder( shapeType ), fileType );         // Shape type
        BWr( invByteOrder( box.getXMin() ), fileType );     // Xmin
        BWr( invByteOrder( box.getYMin() ), fileType );     // Ymin
        BWr( invByteOrder( box.getXMax() ), fileType );     // Xmax
        BWr( invByteOrder( box.getYMax() ), fileType );     // Ymax
        BWr( invByteOrder( 0.0 ), fileType );               // Unused
        BWr( invByteOrder( 0.0 ), fileType );               // Unused
        BWr( invByteOrder( 0.0 ), fileType );               // Unused
        BWr( invByteOrder( 0.0 ), fileType );               // Unused
    }
    
    /*
     * Escreve os diferentes tipos de registros
     * devolvem o número de bytes escritos (16 bit word)
     * RecordNumber -> Numero sequencial do registro
     * OffSet -> Numero de bytes ja gravados, ou seja, deslocamento do registro atual
     * RC -> Entidade a ser escrita.
     */
    private int writePoint( int recordNumber_, int offSet, RCPoint RC ) {
        if( RC.getShapeType() != shapeType )
            return 0;
        
        // Shape
        BWi( recordNumber_, "SHP" );        // numero do registro
        BWi( 10, "SHP" );                   // content length
        
        BWi( invByteOrder( RC.getShapeType() ), "SHP" );    // Shape Type
        Point p = new Point( RC.getPoint() );
        BWr( invByteOrder( p.getX() ), "SHP" );             // Coord. X do ponto
        BWr( invByteOrder( p.getY() ), "SHP" );             // Coord. Y do ponto
        
        // Atualizar BOX
        box.updateP( recordNumber_, p.getX(), p.getY() );
        
        // SHX
        BWi( offSet, "SHX" );               // OffSet deste registro no shape
        BWi( 10, "SHX" );                   // content length
        
        return 14;
    }
    
    private int writePolygon( int recordNumber_, int offSet, RCPolygon RC ) {
        int numParts, numPoints, contentLength;
        Part part;
        Point P1, Ponto;
        
        // somente gravar entidades do mesmo tipo
        if( RC.getShapeType() != shapeType )
            return 0;
        
        BWi( recordNumber_, "SHP" );        // numero do registro
        
        // Verificar o fechamento de cada uma das partes do polígono
        Vector parts = new Vector( RC.getParts() );
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            Vector listPoints = new Vector( part.getListPoints() );
            P1 = (Point)listPoints.firstElement();
            Ponto = (Point)listPoints.lastElement();
            if( P1.getX() != Ponto.getX() || P1.getY() != Ponto.getY() )
                RC.addPoint( nPart + 1, P1.getX(), P1.getY(), Ponto.getSeq() + 1 );
        }
        
        // calcular content length
        numParts = parts.size();
        numPoints = 0;
        
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            part.setOffset( numPoints );
            numPoints += part.getNumPoints();
            Vector listPoints = new Vector( part.getListPoints() );
            for( int nPoint = 0; nPoint < listPoints.size(); nPoint++ ) {
                Ponto = (Point)listPoints.get( nPoint );
                RC.updateBox( nPoint + 1, Ponto.getX(), Ponto.getY() );
            }
        }
        
        contentLength = 26 + numParts * 2 + numPoints * 8;
        
        BWi( contentLength - 4, "SHP" );                    // content length
        BoundingBox b = new BoundingBox( RC.getBox() );
        BWi( invByteOrder( RC.getShapeType() ), "SHP" );    // Shape Type
        BWr( invByteOrder( b.getXMin() ), "SHP" );          // Coord. Box.XMin
        BWr( invByteOrder( b.getYMin() ), "SHP" );          // Coord. Box.YMin
        BWr( invByteOrder( b.getXMax() ), "SHP" );          // Coord. Box.XMax
        BWr( invByteOrder( b.getYMax() ), "SHP" );          // Coord. Box.YMax
        
        BWi( invByteOrder( numParts ), "SHP" );             // NumParts
        BWi( invByteOrder( numPoints ), "SHP" );            // NumPoints
        
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            BWi( invByteOrder( part.getOffset() ), "SHP" );
        }
        
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            Vector listPoints = new Vector( part.getListPoints() );
            for( int nPoint = 0; nPoint < listPoints.size(); nPoint++ ) {
                Ponto = (Point)listPoints.get( nPoint );
                BWr( invByteOrder( Ponto.getX() ), "SHP" );
                BWr( invByteOrder( Ponto.getY() ), "SHP" );
            }
        }
        
        // SHX
        BWi( offSet, "SHX" );               // OffSet deste registro no shape
        BWi( contentLength - 4, "SHX" );    // content length
        
        return contentLength ;
    }
    
    private int writePolyLine( int recordNumber_, int offSet, RCPolyLine RC ) {
        int numParts, numPoints, contentLength;
        Part part;
        Point ponto;
        
        if( RC.getShapeType() != shapeType )
            return 0;
        
        BWi( recordNumber_, "SHP" );            // numero do registro
        
        Vector parts = new Vector( RC.getParts() );
        // calcular content length
        numParts = parts.size();
        numPoints = 0;
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            part.setOffset( numPoints );
            numPoints += part.getNumPoints();
            // atualiza o Bounding Box da Polyline
            for( int nPoint = 0; nPoint < part.getNumPoints(); nPoint++ ) {
                Vector listPoints = new Vector( part.getListPoints() );
                ponto = (Point)listPoints.get( nPoint );
                RC.updateBox( nPoint + 1, ponto.getX(), ponto.getY() );
            }
        }
        
        contentLength = 26 + numParts * 2 + numPoints * 8;
        
        BWi( contentLength - 4, "SHP" );                    // content length
        BoundingBox b = new BoundingBox( RC.getBox() );
        BWi( invByteOrder( RC.getShapeType() ), "SHP" );    // Shape Type
        BWr( invByteOrder( b.getXMin() ), "SHP" );          // Coord box.XMin
        BWr( invByteOrder( b.getYMin() ), "SHP" );          // Coord box.YMin
        BWr( invByteOrder( b.getXMax() ), "SHP" );          // Coord box.XMax
        BWr( invByteOrder( b.getYMax() ), "SHP" );          // Coord box.YMax
        
        BWi( invByteOrder( numParts ), "SHP" );             // numParts
        BWi( invByteOrder( numPoints ), "SHP" );            // numPoints
        
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            BWi( invByteOrder( part.getOffset() ), "SHP" );
        }
        
        for( int nPart = 0; nPart < parts.size(); nPart++ ) {
            part = (Part)parts.get( nPart );
            Vector listPoints = new Vector( part.getListPoints() );
            for( int nPoint = 0; nPoint < listPoints.size(); nPoint++ ) {
                ponto = (Point)listPoints.get( nPoint );
                BWr( invByteOrder( ponto.getX() ), "SHP" );
                BWr( invByteOrder( ponto.getY() ), "SHP" );
            }
        }
        
        // SHX
        BWi( offSet, "SHX" );               // Offset deste registro no shape
        BWi( contentLength - 4, "SHX" );    // content length
        
        return contentLength;
    }
    
    private int writeMultiPoint( int recordNumber_, int offSet, RCMultiPoint RC ) {
        int numPoints, contentLength;
        Point ponto;
        
        if( RC.getShapeType() != shapeType )
            return 0;
        
        BWi( recordNumber_, "SHP" );        // numero do registro
        
        // calcular content length
        Vector listPoints = new Vector( RC.getListPoint() );
        numPoints = listPoints.size();
        
        // Atualiza Box do Polígono
        for( int nPoint = 0; nPoint < listPoints.size(); nPoint++ ) {
            ponto = (Point)listPoints.get( nPoint );
            RC.updateBox( nPoint + 1, ponto.getX(), ponto.getY() );
        }
        
        contentLength = 24 + numPoints * 8;
        
        BWi( contentLength - 4, "SHP" );                    // content length
        BoundingBox b = new BoundingBox( RC.getBox() );
        BWi( invByteOrder( RC.getShapeType() ), "SHP" );    // Shape type
        BWr( invByteOrder( b.getXMin() ), "SHP" );          // Coord box.XMin
        BWr( invByteOrder( b.getYMax() ), "SHP" );          // Coord box.YMax
        BWr( invByteOrder( b.getXMin() ), "SHP" );          // Coord box.XMin
        BWr( invByteOrder( b.getYMax() ), "SHP" );          // Coord box.YMax
        
        BWi( invByteOrder( numPoints ), "SHP" );            // numPoints
        
        for( int nPoint = 0; nPoint < listPoints.size(); nPoint++ ) {
            ponto = (Point)listPoints.get( nPoint );
            BWr( invByteOrder( ponto.getX() ), "SHP" );
            BWr( invByteOrder( ponto.getY() ), "SHP" );
        }
        
        // SHX
        BWi( offSet, "SHX" );               // Offset deste registro no shape
        BWi( contentLength - 4, "SHX" );    // content length
        
        return contentLength;
    }
    
    // procedimentos de leitura dos diferentes tipos de registros
    private void addReadPoint() {
        int nRegistro;
        RCPoint RCP;

        int nReg = BRi( "SHP" );            // numero do registro
        int cLen = BRi( "SHP" );            // content length

        // Criar o ponto
        RCP = (RCPoint)newRecord();
        nRegistro = RCP.getRecordNumber();
        
        // Recuperar os valores do registro do BD
        for( int i = 0; i < RCP.getNumFields(); i++ );
            //RCP.setValue( i + 1, DBFFile.getField( nRegistro, i ) );

        int shape = invByteOrder( BRi( "SHP" ) );       // shape type
        double x = invByteOrder( BRr( "SHP" ) );        // coord X do ponto
        double y = invByteOrder( BRr( "SHP" ) );        // coord Y do ponto
        RCP.setPoint( x, y );
    }
    
    private void addReadPolygon() {
        int nRegistro, numParts, np;
        RCPolygon RCP;
        double pX, pY;

        // Atualizar p/ leitura de mais de uma parte...
        int nReg = BRi( "SHP" );            // numero do registro
        int cLen = BRi( "SHP" );            // content length

        // Criar o polígono
        RCP = (RCPolygon)newRecord();
        nRegistro = RCP.getRecordNumber();

        //Recuperar os valores do registro do BD
        for( int i = 0; i < RCP.getNumFields(); i++ );
            //RCP.setValue( i + 1, DBFFile.getField( nRgistro, i ) );
        
        int shape = invByteOrder( BRi( "SHP" ) );       // shape type
        double xMin = invByteOrder( BRr( "SHP" ) );     // coord box.xmin
        double yMin = invByteOrder( BRr( "SHP" ) );     // coord box.ymin
        double xMax = invByteOrder( BRr( "SHP" ) );     // coord box.xmax
        double yMax = invByteOrder( BRr( "SHP" ) );     // coord box.ymax
        RCP.setBox( new BoundingBox( xMin, xMax, yMin, yMax ) );
        
        numParts = invByteOrder( BRi( "SHP" ) );        // numParts
        np = invByteOrder( BRi( "SHP" ) );              // numPoints

        for( int i = 0; i < numParts; i++ ) {
            int unUsed = BRi( "SHP" );          // Jah que nao serah usado
                                                // o procedimento de inversao da 
                                                // ordem dos bytes nao foi usado
        }
        RCP.addPart();
        
        for( int i = 0; i < np; i++ ) {
            pX = invByteOrder( BRr( "SHP" ) );      // ponto coord X
            pY = invByteOrder( BRr( "SHP" ) );      // ponto coord Y
            // insere o ponto atual
            RCP.addPoint( 1, pX, pY, i + 1 );
        }
    }

    private void addReadPolyline() {
        int nRegistro, numParts, np;
        RCPolyLine RCP;
        double pX, pY;

        // Atualizar p/ leitura de mais de uma parte....
        int nReg = BRi( "SHP" );
        int cLen = BRi( "SHP" );

        // Criar a polyline
        RCP = (RCPolyLine)newRecord();

        nRegistro = RCP.getRecordNumber();

        //Recuperar os valores do registro do BD
        for( int i = 0; i < RCP.getNumFields(); i++ );
            //RCP.setValue( i + 1, DBFFile.getField( nRegistro, i ) );
        
        int shape = invByteOrder( BRi( "SHP" ) );       // shape type
        double xMin = invByteOrder( BRr( "SHP" ) );     // coord box.xmin
        double yMin = invByteOrder( BRr( "SHP" ) );     // coord box.ymin
        double xMax = invByteOrder( BRr( "SHP" ) );     // coord box.xmax
        double yMax = invByteOrder( BRr( "SHP" ) );     // coord box.ymax
        RCP.setBox( new BoundingBox( xMin, xMax, yMin, yMax ) );

        numParts = invByteOrder( BRi( "SHP" ) );        // numParts
        np = invByteOrder( BRi( "SHP" ) );              // numPoints

        for( int i = 0; i < numParts; i++ ) {
            int unUsed = BRi( "SHP" );          // Jah que nao serah usado
                                                // o procedimento de inversao da 
                                                // ordem dos bytes nao foi usado
        }

        RCP.addPart();
        
        for( int i = 0; i < np; i++ ) {
            pX = invByteOrder( BRr( "SHP" ) );      // ponto coord X
            pY = invByteOrder( BRr( "SHP" ) );      // ponto coord Y
            // insere o ponto atual
            RCP.addPoint( 1, pX, pY, i + 1 );
        }
    }
    
    // Cria e adiciona um registro
    public RecordContents newRecord() {
        RCPoint RCPt;
        RCMultiPoint RCPm;
        RCPolyLine RCPl;
        RCPolygon RCPg;
        
        switch( shapeType ) {
            case ShapeTypes.SHP_POINT:
                RCPt = new RCPoint( fields );
                records.add( RCPt );
                RCPt.setRecordNumber( records.size() );
                return RCPt;
                
            case ShapeTypes.SHP_POLYLINE:
                RCPl = new RCPolyLine( fields, shapeType );
                records.add( RCPl );
                RCPl.setRecordNumber( records.size() );
                return RCPl;
                
            case ShapeTypes.SHP_POLYGON:
                RCPg = new RCPolygon( fields );
                records.add( RCPg );
                RCPg.setRecordNumber( records.size() );
                return RCPg;
                
            case ShapeTypes.SHP_MULTIPOINT:
                RCPm = new RCMultiPoint( fields );
                records.add( RCPm );
                RCPm.setRecordNumber( records.size() );
                return RCPm;
                
            default:
                System.out.println( "NAO DEVIA ENTRAR AQUI!!" );
                return null;
        }
    }
    
    public void deleteRecord( int recordNumber ) {
        if( recordNumber > 0 && recordNumber <= records.size() )
            records.remove( recordNumber - 1 );
    }
    
    // Grava o arquivo armazenado na memória para o disco. Devolve o sucesso da operação
    public boolean save() {
        int writtenBytes;
        
        RCPoint RCPoint_;
        RCPolygon RCPolygon_;
        RCPolyLine RCPolyline_;
        RCMultiPoint RCMultiPoint_;
        RecordContents RC;
        
        DBFField DBFField_;
        
        // Criar os arquivos de saída
        // criar arquivo DBF em disco...
        //DBFFile_ = new DBFFile( filePath + fileName + ".DBF" );
        //DBFFile.New ;
        
        // gravar a estrutura
        for( int fIndex = 0; fIndex < fields.size(); fIndex++ ) {
            DBFField_ = (DBFField)fields.get( fIndex );
            //DBFFile_.newField( DBFField_.getNome(), DBFField_.getFType(),
            //DBFField_.getFLength(), DBFField_.getFDec() );
        }
        
        // Passo 1 - Ler estrutura e contar tamanho do arquivo
        // Passo 2 - Reescrever arquivo
        
        // inicializar variaveis
        SHPFL = 50;    // tamanho dos arquivos,
        SHXFL = 50;    // em palavras de 16 bits
        
        try {
            SHPFile = new File( filePath + fileName + ".SHP" );
            SHPWriter = new DataOutputStream( new FileOutputStream( SHPFile ) );
            SHXFile = new File( filePath + fileName + ".SHX" );
            SHXWriter = new DataOutputStream( new FileOutputStream( SHXFile ) );
            
            for( int passo = 1; passo <= 2; passo++ ) {
                // escrever cabecalho  (reservando o espaco fisico no arquivo)
                writeHeader( "SHP" );
                writeHeader( "SHX" );
                
                // Re-inicializar variaveis
                SHPFL = 50;
                SHXFL = 50;
                writtenBytes = 0;
                
                // escrever registro por registro
                for( int rcIndex = 0; rcIndex < records.size(); rcIndex++ ) {
                    RC = (RecordContents)records.get( rcIndex );
                    switch( shapeType ) {
                        case ShapeTypes.SHP_POINT:
                            RCPoint_ = (RCPoint)RC;
                            writtenBytes = writePoint( rcIndex + 1, SHPFL, RCPoint_ );
                            break;
                            
                        case ShapeTypes.SHP_POLYGON:
                            RCPolygon_ = (RCPolygon)RC;
                            writtenBytes = writePolygon( rcIndex + 1, SHPFL, RCPolygon_ );
                            break;
                            
                        case ShapeTypes.SHP_POLYLINE:
                            RCPolyline_ = (RCPolyLine)RC;
                            writtenBytes = writePolyLine( rcIndex + 1, SHPFL, RCPolyline_ );
                            box.updateB( rcIndex + 1, RCPolyline_.getBox() );
                            break;
                            
                        case ShapeTypes.SHP_MULTIPOINT:
                            RCMultiPoint_ = (RCMultiPoint)RC;
                            writtenBytes = writeMultiPoint( rcIndex + 1, SHPFL, RCMultiPoint_ );
                            box.updateB( rcIndex + 1, RCMultiPoint_.getBox() );
                            break;
                        default:
                            System.out.println( "NAO DEVIA ENTRAR AQUI!!!" );
                            break;
                    }
                    
                    SHPFL += writtenBytes;
                    SHXFL += 4;
                    
                    // Escrever dados do arquivo DBF do registro...
                    if( passo == 2 ) {
                        //DBFFile_.insert();
                        for( int fIndex = 0; fIndex < RC.getNumFields(); fIndex++ ) ;
                        //DBFFile_.putFString( RC.getValue( fIndex + 1), rcIndex, fIndex );
                    }
                }
            }
            
            // fechar arquivos
            SHPWriter.close();
            SHXWriter.close();
            //DBFFile_.close();
        } catch( FileNotFoundException fnf ) {
            return false;
        } catch( IOException ioe ) {
            return false;
        }
        return true;
    }
    
    // Carrega o arquivo SHP armazenado em disco. Devolve o sucesso da operação
    public boolean load() {
        try {
        // Abrir o arquivo
        SHPFile = new File( filePath + fileName + ".SHP" );
        SHPReader = new DataInputStream( new FileInputStream( SHPFile ) );
        
        eof = false;
        
        // leitura do cabecalho
        // nenhum dado importante ... Deslocando 100 Bytes à frente
        SHPReader.skip( 100 );

        // ler registro por registro
        while( !eof ) {
            switch( shapeType ) {
                case ShapeTypes.SHP_POINT:
                    addReadPoint();
                    break;
                case ShapeTypes.SHP_POLYGON:
                    addReadPolygon();
                    break;
                case ShapeTypes.SHP_POLYLINE:
                    addReadPolyline();
                    break;
                case ShapeTypes.SHP_MULTIPOINT:
                    break;
                default:
                    System.out.println( "NAO DEVIA ENTRAR AQUI!!" );
                    break;
            }
        }

        JOptionPane.showMessageDialog(null, "Geração automática executada com sucesso!");
        // fechar arquivos
        SHPReader.close();

        // se chegou neste ponto, leitura OK
        return true;
        } catch( FileNotFoundException fnf ) {
            return false;
        } catch( IOException ioe ) {
            return false;
        }
    }
    
    // Retorna o registro correspondente ao identificador do usuário
    public RecordContents getUserRecord( long recordNumber ) {
        int index;
        RecordContents rc;
        
        index = indexByName( "UserID" );
        if( index > 0 ) {
            for( int nrc = 0; nrc < records.size(); nrc++ ) {
                rc = (RecordContents)records.get( nrc );
                if( rc.getFloatValue( index ) == recordNumber )
                    return rc;
            }
        }
        return null;
    }
    
    // Retorna o registro correspondente ao numero sequencial
    public RecordContents getSeqRecord( int recordNumber ) {
        if( recordNumber > 0 && recordNumber <= records.size() )
            return (RecordContents)records.get( recordNumber -1 );
        else
            return null;
    }
    
    // Retorna o indice do campo de nome "FieldName", 0 caso não exista
    public int indexByName( String fieldName ) {
        DBFField DBFField_;
        
        for( int i = 1; i <= fields.size(); i++ ) {
            DBFField_ = (DBFField)fields.get( i );
            if( fieldName.equalsIgnoreCase( DBFField_.getNome() ) )
                return i;
        }
        return 0;
    }
    
    // Cria um campo no arquivo DBF e em todos os registros do Shape
    public boolean newField( String name, char tipo, byte length, byte dec ) {
        RecordContents rc;
        DBFField DBFField_;
        
        DBFField_ = new DBFField();
        DBFField_.setNome( name );
        DBFField_.setFType( tipo );
        DBFField_.setFLength( length );
        DBFField_.setFDec( dec );
        
        fields.add( DBFField_ );
        
        for( int i = 0; i < records.size(); i++ ) {
            rc = (RecordContents)records.get( i );
            rc.createNewField();
        }
        
        return true;
    }
}
