/*
 * RecordContents.java
 *
 * Created on 14 de Julho de 2004, 09:39
 *
 * Shape Record Contents
 */

package org.argouml.shapefile;

import java.util.Vector;

/**
 *
 * @author  Valerio
 */
public class RecordContents {
    
    private int shapeType;          // Tipo de entidade do registro
    private int offset;            // Deslocamento do inicio do registro (16bits)
    private int recordNumber;      // Numero sequencial do registro
    private int contentLength;     // Tamanho do registro (16bits)
    private Vector Fields;          // link para declarações da estrutura de dados do DBF
    private Vector Values;          // Lista de valores dos campos presentes no DBF
    
    /** Cria uma nova instancia de RecordContents */
    public RecordContents() {
    }
    
    public RecordContents( int shapeType_, Vector fList ) {
        shapeType = shapeType_;
        offset = 0;                 // Offset deve ser atualizado no metodo de gravação
        Values = new Vector();
        
        //  A lista de valores para o registro deve ter o mesmo número de registros
        // especificado pelo ShapeFile (FList)
        
        Fields = new Vector( fList );
        for( int f = 0; f < Fields.size(); f++ )
            Values.add( new DBFStringValue() );
    }
    
    //// Manipulação dos valores do DBF ////
    
    // Devolve o string referente ao campo de indice "Index"
    public String getValue( int index ) {
        DBFStringValue value = new DBFStringValue();
        if( index > 0 && index <= Fields.size() ) {
            value = (DBFStringValue)Values.get( index - 1 );
        }
        return value.getValue();
    }
    
    // Atribuir o string "S" ao campo de indice "Index"
    public void setValue( int index, String s ) {
        DBFStringValue value = new DBFStringValue();
        if( index > 0 && index <= Fields.size() ) {
            value = (DBFStringValue)Values.get( index - 1 );
            value.setValue( s );
        }
    }
    
    // Devolve o valor numérico referente ao campo de indice "Index"
    public double getFloatValue( int index ) { //: Extended ;
        DBFStringValue value = new DBFStringValue();
        DBFField Field = new DBFField();

        if( index > 0 && index <= Fields.size() ) {
            value = (DBFStringValue)Values.get( index - 1 );
            Field = (DBFField)Fields.get( index - 1 );
            if( Field.getFType() == 'N' || Field.getFType() == ' ' )
                return Double.parseDouble( value.getValue() );
            else
                return 0.0;
        }
        else
            return 0.0;
    }
    
    // Atribuir o valor numérico "V" ao campo de indice "Index"
    public void setFloatValue( int index, double v ) {
        DBFStringValue value = new DBFStringValue();
        DBFField Field = new DBFField();
        
        if( index > 0 && index <= Fields.size() ) {
            value = (DBFStringValue)Values.get( index - 1 );
            Field = (DBFField)Fields.get( index - 1 );
            if( Field.getFType() == 'N' || Field.getFType() == 'F' )
                value.setValue( Double.toString( v ) ); 
                //FloatToStrF(V,ffGeneral,DBFField.FLength,DBFField.FDec);
        }
    }
    
    // Cria um novo campo para o registro
    public void createNewField() {
        Values.add( new DBFStringValue() );
    }
    
    // Remove um campo do registro
    public void removeField( int index ) {
        if( index > 0 && index <= Fields.size() )
            Values.remove( index - 1 );
    }
    
    /** Getter for property recordNumber.
     * @return Value of property recordNumber.
     *
     */
    public int getRecordNumber() {
        return recordNumber;
    }
    
    /** Setter for property recordNumber.
     * @param recordNumber New value of property recordNumber.
     *
     */
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }
    
    /** Getter for property shapeType.
     * @return Value of property shapeType.
     *
     */
    public int getShapeType() {
        return shapeType;
    }
    
    /** Setter for property shapeType.
     * @param shapeType New value of property shapeType.
     *
     */
    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }
    
    public int getNumFields() {
        return Fields.size();
    }
}
