package org.argouml.oraclespatial;

import java.util.ArrayList;

public class ObjetoGeoFrame {

	private String m_nomeClasse;
	private ArrayList<Atributo> m_PK;
	private ArrayList<Atributo> m_atributos;
	private boolean m_superClasse = false;
	private boolean m_subClasse = false;
	
	public ObjetoGeoFrame(String p_nomeClasse)
	{
		m_nomeClasse = p_nomeClasse;
		m_PK = new ArrayList<Atributo>();
		m_atributos = new ArrayList<Atributo>();
	}
	
	public ObjetoGeoFrame(String p_nomeClasse, ArrayList<Atributo> p_PK)
	{
		m_nomeClasse = p_nomeClasse;
		m_PK = p_PK;
		m_atributos = new ArrayList<Atributo>();
	}
	
	public void adicionarAtributoChavePrimaria(Atributo p_atributo)
	{
		m_PK.add(p_atributo);
	}
	
	public void adicionarAtributo(Atributo p_atributo)
	{
		m_atributos.add(p_atributo);
	}
	
	public void adicionarRepresentacaoEspacial()
	{
		m_atributos.add( new Atributo("geometria", Constantes.GEOMETRIA));
	}
	
	public String getNome()
	{
		return m_nomeClasse;
	}
	
	public ArrayList<Atributo> getPK()
	{
		return m_PK;
	}
	
	public String gerarStringCorrespondente()
	{
		String v_resultado = "CREATE TABLE " + m_nomeClasse + " \r\n(\r\n";
		Atributo v_atributo;
		
		for(int c_indiceAtributo = 0; c_indiceAtributo < m_PK.size(); ++c_indiceAtributo)
		{
			v_atributo = m_PK.get(c_indiceAtributo);
			v_resultado += v_atributo.getNome() + " " + v_atributo.getTipo();
			v_resultado += ",\r\n";
		}
		
		for(int c_indiceAtributo = 0; c_indiceAtributo < m_atributos.size(); ++c_indiceAtributo)
		{
			v_atributo = m_atributos.get(c_indiceAtributo);
			v_resultado += v_atributo.getNome() + " " + v_atributo.getTipo();
			v_resultado += ",\r\n";
		}	
		
		v_resultado += "PRIMARY KEY " + getStringPK() + "\r\n);\r\n\r\n";
		return v_resultado;
	}
	
	//esta funcao retorna a chave primaria com os atributos separados por virgurla. Ex.: (atributo1, atributo2)
	public String getStringPK()
	{
		String v_resultado = "(";
		Atributo v_atributoPK;
		
		for(int c_indicePK = 0; c_indicePK < m_PK.size(); ++c_indicePK)
		{
			v_atributoPK = m_PK.get(c_indicePK);
			v_resultado += v_atributoPK.getNome();
			
			if(c_indicePK != m_PK.size()-1)
			{
				v_resultado += ", ";
			}
		}
		
		return v_resultado + ")";
	}
	
	//gera em uma String os atributos que serao exportados para outra tabela em um relacionamento
	public String gerarChaveEstrangeira()
	{
		String v_resultado = "(";
		Atributo v_atributoPK;
		
		for(int c_indicePK = 0; c_indicePK < m_PK.size(); ++c_indicePK)
		{
			v_atributoPK = m_PK.get(c_indicePK);
			v_resultado += v_atributoPK.getNome() + "_FK";
			
			if(c_indicePK != m_PK.size()-1)
			{
				v_resultado += ", ";
			}
		}
		
		return v_resultado + ")";
	}
	
	public void setSuperClasse(boolean p_superClasse)
	{
		m_superClasse = p_superClasse;
	}
	
	public boolean ehSuperClasse()
	{
		return m_superClasse;
	}
	
	public void setSubClasse(boolean p_subClasse)
	{
		m_subClasse = p_subClasse;
	}
	
	public boolean ehSubClasse()
	{
		return m_superClasse;
	}
	
	public void adicionarAtributos(ArrayList<Atributo> p_atributos)
	{
		m_atributos.addAll(p_atributos);
	}
	
	public void adicionarAtributosPK(ArrayList<Atributo> p_atributosPK)
	{
		m_PK.addAll(p_atributosPK);
	}
	
	public ArrayList<Atributo> getAtributosNaoGeograficos(boolean p_comPK)
	{
		ArrayList<Atributo> v_resposta = new ArrayList<Atributo>();
		
		if(p_comPK)
		{
			v_resposta.addAll(m_PK);
		}	
		
		for(int i = 0; i < m_atributos.size(); ++i)
		{
			Atributo v_atributo = m_atributos.get(i);
			if(!(v_atributo.getTipo()).equals(Constantes.GEOMETRIA))
			{	
				v_resposta.add(v_atributo);
			}
		}
	
		return v_resposta;
	}
	
	public void limpaPK()
	{
		m_atributos.addAll(0, m_PK);
		m_PK.clear();
	}
	/*public static void main(String[] args)
	{
		ObjetoGeoFrame teste = new ObjetoGeoFrame("tabela_teste");
		Atributo attPK = new Atributo("attPK", "INTEGER");
		teste.adicionarAtributoChavePrimaria(attPK);
		Atributo att1 = new Atributo("att1", "DOUBLE");
		teste.adicionarAtributo(att1);
		Atributo att2 = new Atributo("att2", "VARCHAR2");
		teste.adicionarAtributo(att2);
		teste.adicionarRepresentacaoEspacial();
		
		System.out.println(teste.gerarStringCorrespondente());
	}*/
}
