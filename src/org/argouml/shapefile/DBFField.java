/*
 * DBFField.java
 *
 * Created on 14 de Julho de 2004, 16:17
 */

package org.argouml.shapefile;

/**
 *
 * @author  Valerio
 */
public class DBFField {
    
    private String nome;          // nome do campo
    private char fType;           // tipo do campo (padrao DBF III)
    private byte fLength;         // tamanho do campo
    private byte fDec;            // numero de casas decimais para campos numericos

    /** Creates a new instance of DBFField */
    public DBFField() {
        nome = new String();
    }
    
    /** Getter for property fDec.
     * @return Value of property fDec.
     *
     */
    public byte getFDec() {
        return fDec;
    }
    
    /** Setter for property fDec.
     * @param fDec New value of property fDec.
     *
     */
    public void setFDec(byte fDec) {
        this.fDec = fDec;
    }
    
    /** Getter for property fLength.
     * @return Value of property fLength.
     *
     */
    public byte getFLength() {
        return fLength;
    }
    
    /** Setter for property fLength.
     * @param fLength New value of property fLength.
     *
     */
    public void setFLength(byte fLength) {
        this.fLength = fLength;
    }
    
    /** Getter for property fType.
     * @return Value of property fType.
     *
     */
    public char getFType() {
        return fType;
    }
    
    /** Setter for property fType.
     * @param fType New value of property fType.
     *
     */
    public void setFType(char fType) {
        this.fType = fType;
    }
    
    /** Getter for property nome.
     * @return Value of property nome.
     *
     */
    public java.lang.String getNome() {
        return nome;
    }
    
    /** Setter for property nome.
     * @param nome New value of property nome.
     *
     */
    public void setNome(java.lang.String nome) {
        this.nome = nome;
    }
    
}
