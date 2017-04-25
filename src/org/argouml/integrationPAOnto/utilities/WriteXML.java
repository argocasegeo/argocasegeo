package org.argouml.integrationPAOnto.utilities;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class WriteXML {
	
	public static void gravaXML( ArrayList<ItemGenerated> lista ){
		
		try {
			PrintStream arquivoSaida = new PrintStream("output\\saida.xml");
			
			arquivoSaida.println("<?xml version=\"1.0\"?>");
			arquivoSaida.println("<integracao>");
			
			for(int i = 0; i < lista.size(); i++){
				ItemGenerated it = lista.get(i);
				arquivoSaida.println("\t<padrao_analise>" + it.getPadrao() + "</padrao_analise>");
				arquivoSaida.println("\t<classe_padrao>" + it.getClasseDoPadrao() + "</classe_padrao>");
				arquivoSaida.println("\t<ontologia>" + it.getOntologia() + "</ontologia>");
				arquivoSaida.println("\t<classe_ontologia>" + it.getClasseDaOntologia() + "</classe_ontologia>");
				if(i != lista.size() - 1)
					arquivoSaida.println();
			}//fim do for
			
			arquivoSaida.println("</integracao>");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}//fim do gravaXML
	
	
}//fim do WriteXML
