package org.argouml.oraclespatial;

import java.util.ArrayList;

public class Relacionamento {

	private ObjetoGeoFrame m_objeto1;
	private ObjetoGeoFrame m_objeto2;
	private int m_tipoRelacionamento;
    private String FK1;
	
	public Relacionamento(ObjetoGeoFrame p_objeto1, ObjetoGeoFrame p_objeto2, int p_tipoRelacionamento)
	{
		m_objeto1 = p_objeto1;
		m_objeto2 = p_objeto2;
		m_tipoRelacionamento = p_tipoRelacionamento;
	}

    public Relacionamento(ObjetoGeoFrame p_objeto1, ObjetoGeoFrame p_objeto2,String F1)
	{
		m_objeto1 = p_objeto1;
		m_objeto2 = p_objeto2;
        FK1 = F1;
		m_tipoRelacionamento = 3;
	}
	
	public String gerarStringCorrespondente()
	{
		String v_resultado;
		
		if(m_tipoRelacionamento == Constantes.RELACIONAMENTO_1_N)
		{
			v_resultado = gerarRelacionamento_1_N();
		}
		else if(m_tipoRelacionamento == Constantes.RELACIONAMENTO_N_N)
		{
			v_resultado = gerarRelacionamento_N_N();
		}
		else if(m_tipoRelacionamento == Constantes.RELACIONAMENTO_GENERALIZACAO)
		{
			v_resultado = gerarRelacionamentoGeneralizacao();
		}
        else if(m_tipoRelacionamento == Constantes.RELACIONAMENTO_NET)
		{
			v_resultado = gerarRelacionamentoNet();
		}
		else
		{
			v_resultado =  "--ERRO AO CRIAR RELACIONAMENTO: TIPO DE RELACIONAMENTO INVALIDO!"; 
		}
		
		return v_resultado;
	}
	
	private String gerarRelacionamento_1_N()
	{
		String v_resultado = "";
		String v_nomeObjeto1 = m_objeto1.getNome();
		String v_nomeObjeto2 = m_objeto2.getNome();
		ArrayList<Atributo> v_objeto1PK = m_objeto1.getPK();
		String v_nomeAtributoPK;
		Atributo v_atributoPK;
		
		for(int c_indicePK = 0; c_indicePK < v_objeto1PK.size(); ++c_indicePK)
		{
			v_atributoPK = v_objeto1PK.get(c_indicePK);			
			v_nomeAtributoPK = v_atributoPK.getNome();
			
			v_resultado += "ALTER TABLE " + v_nomeObjeto2 + " ADD " + v_nomeAtributoPK + "_FK " + v_atributoPK.getTipo() + ";\r\n";
		}
		
		String v_nomeRestricao = "fk_" + v_nomeObjeto1 + "_" + v_nomeObjeto2;
		v_resultado += "ALTER TABLE " + v_nomeObjeto2 + 
					   " ADD CONSTRAINT " + v_nomeRestricao + 
					   " FOREIGN KEY " + m_objeto1.gerarChaveEstrangeira() + 
					   " REFERENCES " + v_nomeObjeto1 + m_objeto1.getStringPK() + ";\r\n\r\n";
		
		return v_resultado;
	}

    private String gerarRelacionamentoNet()
	{
		String v_resultado = "";
		String v_nomeObjeto1 = m_objeto1.getNome();
		String v_nomeObjeto2 = m_objeto2.getNome();
        String num = "";
        if(FK1.endsWith("1"))
            num = "1";
        else if(FK1.endsWith("2"))
            num =  "2";
		String v_nomeRestricao = "fk_" + v_nomeObjeto1 + "_" + v_nomeObjeto2;
		v_resultado += "ALTER TABLE " + v_nomeObjeto2 +
					   " ADD CONSTRAINT " + v_nomeRestricao+num +
					   " FOREIGN KEY " +  "("+FK1 +"_FK"+")" +
					   " REFERENCES " + v_nomeObjeto1 + m_objeto1.getStringPK() + ";\r\n\r\n";

		return v_resultado;
	}
	
	private String gerarRelacionamento_N_N()
	{
		String v_nomeObjeto1 = m_objeto1.getNome();
		String v_nomeObjeto2 = m_objeto2.getNome();
		String v_nomeTabelaRelacionamento = v_nomeObjeto1 + "_" + v_nomeObjeto2;
		
		ArrayList<Atributo> v_PKTabelaRelacionamento = new ArrayList<Atributo>();
		v_PKTabelaRelacionamento.addAll(m_objeto1.getPK());
		v_PKTabelaRelacionamento.addAll(m_objeto2.getPK());
		
		ObjetoGeoFrame v_tabelaRelacionamento = new ObjetoGeoFrame(v_nomeTabelaRelacionamento, v_PKTabelaRelacionamento);
		String v_resultado = v_tabelaRelacionamento.gerarStringCorrespondente();
		
		String v_nomeRestricao1 = "fk_" + v_nomeObjeto1 + "_" + v_nomeTabelaRelacionamento;
		v_resultado += "ALTER TABLE " + v_nomeTabelaRelacionamento + 
		   " ADD CONSTRAINT " + v_nomeRestricao1 + 
		   " FOREIGN KEY " + m_objeto1.getStringPK() + 
		   " REFERENCES " + v_nomeObjeto1 + m_objeto1.getStringPK() + ";\r\n";
		
		String v_nomeRestricao2 = "fk_" + v_nomeObjeto2 + "_" + v_nomeTabelaRelacionamento;
		v_resultado += "ALTER TABLE " + v_nomeTabelaRelacionamento + 
		   " ADD CONSTRAINT " + v_nomeRestricao2 + 
		   " FOREIGN KEY " + m_objeto2.getStringPK() + 
		   " REFERENCES " + v_nomeObjeto2 + m_objeto2.getStringPK() + ";\r\n\r\n";
		
		return v_resultado;
	}
	
	private String gerarRelacionamentoGeneralizacao()
	{
		String v_resultado = "";
		String v_nomeObjeto1 = m_objeto1.getNome();
		String v_nomeObjeto2 = m_objeto2.getNome();
		
		String v_nomeRestricao = "fk_" + v_nomeObjeto1 + "_" + v_nomeObjeto2;
		v_resultado += "ALTER TABLE " + v_nomeObjeto2 + 
					   " ADD CONSTRAINT " + v_nomeRestricao + 
					   " FOREIGN KEY " + m_objeto1.getStringPK() + 
					   " REFERENCES " + v_nomeObjeto1 + m_objeto1.getStringPK() + ";\r\n\r\n";
		
		return v_resultado;
	}
}
