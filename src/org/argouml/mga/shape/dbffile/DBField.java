/*
 * DBField.java
 *
 * Created on 27 de Julho de 2004, 15:42
 */

package org.argouml.mga.shape.dbffile;

/**
 *
 * @author  Valerio
 */
public class DBField {
    
    private String name;
    private char fType;
    private byte fLength;
    private byte fDec;
    private int offset;
    
    /** Creates a new instance of DBField */
    public DBField() {
        name = new String();
        fType = ' ';
        fLength = 0;
        fDec = 0;
        offset = 0;
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
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /** Getter for property offset.
     * @return Value of property offset.
     *
     */
    public int getOffset() {
        return offset;
    }
    
    /** Setter for property offset.
     * @param offset New value of property offset.
     *
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
}
