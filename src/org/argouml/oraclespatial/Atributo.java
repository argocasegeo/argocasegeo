package org.argouml.oraclespatial;

public class Atributo {
	
	private String m_nome;
	private String m_tipo;
	
	public Atributo(String p_nome, String p_tipo)
	{
		m_nome = p_nome;
		m_tipo = p_tipo;
	}
	
	public String getNome()
	{
		return m_nome;
	}
	
	public String getTipo()
	{
		return m_tipo;
	}
	
	public static String ConverterParaTipoValido(String p_valor)
	{
		if(p_valor.equalsIgnoreCase("int"))
			return Constantes.INTEIRO;
		
		else if(p_valor.equalsIgnoreCase("double"))
			return Constantes.DOUBLE;
		
		else if(p_valor.equalsIgnoreCase("float"))
			return Constantes.FLOAT;
		
		else if(p_valor.equalsIgnoreCase("boolean"))
			return Constantes.BOOL;
		
		else if(p_valor.equalsIgnoreCase("date"))
			return Constantes.DATA;
		
		else if(p_valor.equalsIgnoreCase("char"))
			return Constantes.CHAR;
		
		else if(p_valor.equalsIgnoreCase("geometria"))
			return Constantes.GEOMETRIA;
		
		else
			return Constantes.STRING;	
	}
};
