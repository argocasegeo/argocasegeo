/*
 * DBFFile.java
 *
 * Created on 28 de Julho de 2004, 09:55
 */

package org.argouml.mga.shape.dbffile;

import java.util.Vector;
import java.util.Date;
import java.io.*;
import java.text.*;

/**
 *
 * @author  Valerio
 */
public class DBFFile {

    private int numRecords;
    private short numBytesHeader;
    private short numBytesRecord;
    private Vector fields;
    private File DBArq;
    private RandomAccessFile DBrw;
    private String fileName;
    private boolean active;
    private byte [] dlUpdate;

    /** Creates a new instance of DBFFile */
    public DBFFile() {
    }

    public DBFFile( String fileName_ ) {
        fileName = new String( fileName_ );
        DBArq = new File( fileName );

        numRecords = 0;
        fields = new Vector();
        active = false;
        numBytesHeader = 0;
        numBytesRecord = 1;
        dlUpdate = new byte[3];
    }

    public Vector getFields() {
        return fields;
    }

    public boolean load() {
        DBField field;
        int offset;

        if( !active )
            active = true;
        try {
            DBrw = new RandomAccessFile( DBArq, "rw" );
            offset = 1;

            DBrw.seek( 4 );
            // colocar na posicao de leitura
            numRecords = invByteOrder( DBrw.readInt() );
            numBytesHeader = invByteOrder( DBrw.readShort() );
            numBytesRecord = invByteOrder( DBrw.readShort() );

            for( int campo = 1; campo <= ( numBytesHeader - 32 ) / 32; campo++ ) {
                field = new DBField();
                DBrw.seek( 32 * campo );                // deslocamento p/ posicao de leitura
                field.setName( getStr( 11 ) );          // nome do campo

                DBrw.seek( 32 * campo + 11 );           // deslocamento p/ posicao de leitura
                field.setFType( (char)DBrw.readByte() );      // tipo do campo

                DBrw.seek( 32 * campo + 16 );           // deslocamento p/ posicao de leitura
                field.setFLength( DBrw.readByte() );    // tamanho do campo
                field.setFDec( DBrw.readByte() );       // Num de casas decimais

                field.setOffset( offset );
                offset += field.getFLength();

                fields.add( field );
            }
            return true;
        } catch( FileNotFoundException fnf ) {
            return false;
        } catch( IOException ioe ) {
            return false;
        }
    }

    public boolean createNew() {
        byte [] buffer = new byte[32];

        // inicializações
        numBytesHeader = 33;        // numero de bytes do header com nenhum campo

        for( int i = 0; i < 32; i++ )
            buffer[i] = 0;

        DLU();                        // calcular DLU

        if( active )
            close();

        try {
            // criar arquivo
            if( DBArq.exists() )
                DBArq.delete();
            DBArq.createNewFile();
            DBrw = new RandomAccessFile( DBArq, "rw" );
            active = true;

            // Write Header
            byte b = 3;
            putStr( Character.toString( (char)b ), 1 );
            DBrw.write( dlUpdate );
            DBrw.writeInt( invByteOrder( numRecords ) );
            DBrw.writeShort( invByteOrder( numBytesHeader ) );
            DBrw.writeShort( invByteOrder( numBytesRecord ) );
            DBrw.write( buffer, 0, 20 );
            // remarcar o fim de arquivo ---- nao eh necessario
            //b = 0; //13;
            //putStr( Character.toString( (char)b ), 1 );

            return true;
        } catch( FileNotFoundException fnf ) {
            return false;
        } catch( IOException ioe ) {
            return false;
        }
    }

    public boolean close() {
        try {
            if( active ) {
                DBrw.close();
                active = false;
            }
        } catch( IOException ioe ) {
        }
        return active;
    }

    public boolean open() {
        try {
            if( !active ) {
                DBrw = new RandomAccessFile( DBArq, "rw" );
                active = true;
            }
        } catch( FileNotFoundException fnf ) {
            fnf.printStackTrace();
            active = false;
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            active = false;
        }
        return active;
    }

    // Cria um novo campo no arquivo dbf
    public boolean newField( String name, byte tipo, byte length, byte dec ) {
        byte [] buffer = new byte[32];
        DBField field;
        int offset;

        if( !active )
            return false;

        for( int i = 0; i < 32; i++ )
            buffer[i] = 0;

        DLU();                      // calcular DLU

        try {
            // posicionar o ponteiro de gravação na penúltima posição do arquivo
            DBrw.seek( DBrw.length() );

            putStr( name, 11 );
            DBrw.writeByte( tipo );
            DBrw.write( buffer, 0, 4 );
            DBrw.writeByte( length );
            DBrw.writeByte( dec );
            DBrw.write( buffer, 0, 14 );

            // Regravar marca de fim de arquivo --- Nao eh necessario
            //byte b = 13;
            //putStr( Character.toString( (char)b ), 1 );

            // Atualizar o Cabeçalho
            DBrw.seek( 1 );
            numBytesHeader += 32;
            numBytesRecord += length;

            DBrw.write( dlUpdate );
            DBrw.writeInt( invByteOrder( numRecords ) );
            DBrw.writeShort( invByteOrder( numBytesHeader ) );
            DBrw.writeShort( invByteOrder( numBytesRecord ) );

            // Gravar os dados do campo em memória...
            if( fields.size() != 0 ) {
                field = (DBField)fields.lastElement();
                offset = field.getOffset() + field.getFLength();
            }
            else
                offset = 1;

            field = new DBField();
            field.setName( name );
            field.setFType( (char)tipo );
            field.setFLength( length );
            field.setFDec( dec );
            field.setOffset( offset );

            fields.add( field );

            return true;
        } catch( IOException ioe ) {
            return false;
        }
    }

    // Faz a leitura de n caracteres do arquivo, e devolve como string
    public String getStr( int n ) {
        String s;
        char c;

        if( !active )
            return null;

        s = new String();

        // acumular caracteres, descartando nulos
        int i = 0;
        while( i < n ) {
            try {
                c = (char)DBrw.readByte();
                byte b = 0;
                if( c != (char)b )
                    s += c;
                else
                    i = n;
                i++;
            } catch( IOException ioe ) {
                i = n;
            } 
        }

        // eliminar espaços no final do string
        i = s.length();
        if( i > 0 )
            while( i > 0 && s.charAt( i - 1 ) == ' ' )
                i--;

        return s.substring( 0, i );
    }

    // faz a gravação da String S para o arquivo,
    // preenchendo com #0 até completar n caracteres
    public void putStr( String s, int n ) {
        byte [] buffer = new byte[255];

        if( !active )
            return;
        for( int i = 0; i < s.length(); i++ )
            buffer[i] = (byte)s.charAt(i);
        for( int i = s.length(); i < n; i++ )
            buffer[i] = 0;
        try {
            DBrw.write( buffer, 0, n );
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    // faz a gravação da String S para o arquivo,
    // preenchendo com #32 até completar n caracteres
    public void putFStr( String s, int n ) {
        byte [] buffer = new byte[255];

        if( !active )
            return;

        for( int i = 0; i < s.length(); i++ )
            buffer[i] = (byte)s.charAt(i);
        for( int i = s.length(); i < n; i++ )
            buffer[i] = (byte)(' ');
        try {
            DBrw.write( buffer, 0, n );
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    // devolve o nome do campo de indice FNumber (primeiro campo -> FNumber==0)
    public String getFieldName( int fNumber ) {
        DBField field;

        try {
            field = (DBField)fields.get( fNumber );
            return field.getName();
        } catch( ArrayIndexOutOfBoundsException oob ) {
            System.out.println( "Indice fora dos limites validos" );
            return null;
        }
    }

    // devolve o indice do campo do campo de nome "FieldName"
    public int getFNumber( String fName ) {
        DBField field;
        int fNumber = -1;
        int i = 0;

        while( i < fields.size() ) {
            field = (DBField)fields.get(i);
            if( fName.equalsIgnoreCase( field.getName() ) ) {
                fNumber = i;
                i = fields.size();
            }
            else
                i++;
        }
        return fNumber;
    }

    // devolve o campo corresponde no registro "nregistro"
    public String getFieldByName( int nRegistro, String fName ) {
        DBField field;
        int fNumber = -1;
        int i = 0;

        while( i < fields.size() ) {
            field = (DBField)fields.get( i );
            if( fName.equalsIgnoreCase( field.getName() ) ) {
                fNumber = i;
                i = fields.size();
            }
            else
                i++;
        }

        if( fNumber > -1 )
            return getField( nRegistro, fNumber );
        else {
            System.out.println( "DBFUnit - Nome de campo inexistente." );
            return null;
        }
    }

    // devolve o n-ésimo campo do registro "nregistro"
    public String getField( int nRegistro, int fNumber ) {
        DBField field;
        int pos;

        try {
            if( fNumber >= 0 && fNumber < fields.size() ) {
                field = (DBField)fields.get( fNumber );
                pos = numBytesHeader + numBytesRecord * ( nRegistro - 1 ) + field.getOffset();
                DBrw.seek( pos );
                return getStr( field.getFLength() );
            }
            else if( fNumber == -1 ) {
                pos = numBytesHeader + numBytesRecord * ( nRegistro - 1 );
                DBrw.seek( pos );
                return getStr( 1 );
            }
            else {
                System.out.println( "DBFUnit - Indice fora dos limites validos." );
                return null;
            }
        } catch( EOFException eof ) {
            eof.printStackTrace();
            System.out.println( "DBFUnit - Numero de registro invalido." );
            return null;
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            return null;
        }
    }

    // grava as informações de um determinado campo em um determinado registro
    public void putFString( String s, int nRegistro, int fNumber ) {
        DBField field;
        int pos;

        if( fNumber >= 0 && fNumber < fields.size() ) {
            try {
                field = (DBField)fields.get( fNumber );
                
                pos = numBytesHeader + numBytesRecord * ( nRegistro - 1 ) + field.getOffset();
                DBrw.seek( pos );
                if( field.getFType() == 'C' )
                    putFStr( s, field.getFLength() );
                else
                    putStr( s, field.getFLength() );
            } catch( EOFException eof ) {
                eof.printStackTrace();
                System.out.println( "DBFUnit - Numero de registro invalido." );
                return;
            } catch( IOException ioe ) {
                ioe.printStackTrace();
                return;
            }
        }
        else if( fNumber == -1 ) {
            try {
                pos = numBytesHeader + numBytesRecord * ( nRegistro - 1 );
                DBrw.seek( pos );
                putFStr( s, 1 );
            } catch( EOFException eof ) {
                eof.printStackTrace();
                System.out.println( "DBFUnit - Numero de registro invalido." );
                return;
            } catch( IOException ioe ) {
                ioe.printStackTrace();
                return;
            }
        }
        else {
            System.out.println( "DBFUnit - Indice fora dos limites validos." );
            return;
        }
    }

    // grava as informações de um determinado campo em um determinado registro
    public void putFNumber( double n, int nRegistro, int fNumber ) {
        DBField field;
        String s;

        if( fNumber >= 0 && fNumber <= fields.size() ) {
            try {
                field = (DBField)fields.get( fNumber );

                ///// formataçao do numero a ser armazenado
                DecimalFormatSymbols symbol = new DecimalFormatSymbols();
                symbol.setDecimalSeparator( '.' );
                symbol.setGroupingSeparator( ',' );
                DecimalFormat formatter = new DecimalFormat();
                formatter.setDecimalFormatSymbols( symbol );
                formatter.setGroupingUsed( false );
                formatter.setMinimumFractionDigits( field.getFDec() );
                formatter.setMaximumFractionDigits( field.getFDec() );
                ///// fim da definiçao do formato

                s = new String( formatter.format( n ) );
                for( int i = s.length(); i < field.getFLength(); i++ )
                    s = " " + s;

                int pos = numBytesHeader + numBytesRecord * ( nRegistro - 1 ) + field.getOffset();
                DBrw.seek( pos );
                putStr( s, field.getFLength() );
            } catch( EOFException eof ) {
                eof.printStackTrace();
                System.out.println( "DBFUnit - Numero de registro invalido." );
                return;
            } catch( IOException ioe ) {
                ioe.printStackTrace();
                return;
            }
        }
        else {
            System.out.println( "DBFUnit - Indice fora dos limites validos." );
            return;
        }
    }

    // retorna o numero de campos de cada registro
    public int numFields() {
        return fields.size();
    }

    // Insere um registro em branco no final do arquivo
    public void insert() {
        DBField field;
        String s;

        try {
            // posiciona o ponteiro no último byte do arquivo
            DBrw.seek( DBrw.length() );
            if( numRecords == 0 ) {
                byte b = 13;
                putStr( Character.toString( (char)b ), 1 );
            }

            // Gravar registro em branco
            putStr( " ", 1 );                          // marca de registro válido
            s = new String();
            for( int i = 0; i < fields.size(); i++ ) {
                field = (DBField)fields.get( i );
                if( field.getFType() == 'C' )
                    putFStr( s, field.getFLength() );
                else
                    putStr( s, field.getFLength() );
            }

            // Regravar marca de fim de arquivo --- nao eh necessario
            //byte b = 13;
            //putStr( Character.toString( (char)b ), 1 );

            // Atualizar o numero de registros
            DBrw.seek( 4 );
            //numRecords++;
            DBrw.writeInt( invByteOrder( numRecords ) );
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            return;
        }
    }

    // Apaga um registro
    public void delete( int nRegistro ) {
        byte [] buffer = new byte[4096];
        int pos;

        try {
            // Swap dos registros
            for( int i = nRegistro; i < numRecords; i++ ) {
                pos = numBytesHeader + numBytesRecord * i;
                DBrw.seek( pos );
                DBrw.read( buffer, 0, numBytesRecord );
                DBrw.seek( pos - numBytesRecord );
                DBrw.write( buffer, 0, numBytesRecord );
            }

            // Regravar marca de fim de arquivo --- nao eh necessario
            //byte b = 13;
            //putStr( Character.toString( (char)b ), 1 );

            long fimArq = DBrw.getFilePointer();
            // Apagar ultimo registro
            DBrw.setLength( fimArq );

            // Atualizar o numero de registros
            DBrw.seek( 4 );
            numRecords--;
            DBrw.writeInt( invByteOrder( numRecords ) );
        } catch( IOException ioe ) {
            ioe.printStackTrace();
            return;
        }
    }

    public int getNumRecords() {
        return numRecords;
    }
    
    // Devolve um string de tres posicões com a data atual, no formato YMD
    private void DLU(){
        // cria o formatador
        SimpleDateFormat formatador = new SimpleDateFormat("yyyyMMdd");
        // cria a string com a data atual
        String dataHoje = formatador.format( new Date() );

        dlUpdate[0] = (byte)( Integer.parseInt( dataHoje.substring( 0, 4 ) ) - 1900 );
        dlUpdate[1] = (byte)( Integer.parseInt( dataHoje.substring( 4, 6 ) ) );
        dlUpdate[2] = (byte)( Integer.parseInt( dataHoje.substring( 6, 8 ) ) );
    }

    private int invByteOrder( int num ) {
        byte [] i = new byte[4];
        i[0] = (byte)( num );
        i[1] = (byte)( num >> 8 );
        i[2] = (byte)( num >> 16 );
        i[3] = (byte)( num >> 24 );
        
        return (int)( ( i[0] & 0xff ) << 24 | ( i[1] & 0xff ) << 16 |
        ( i[2] & 0xff ) << 8  | ( i[3] & 0xff ) );
    }

    private short invByteOrder( short num ) {
        byte [] i = new byte[2];
        i[0] = (byte)( num );
        i[1] = (byte)( num >> 8 );
        
        return (short)( ( i[0] & 0xff ) << 8 | ( i[1] & 0xff ) );
    }

}
