package org.argouml.integrationPAOnto.utilities;

import com.hp.hpl.jena.ontology.OntClass;

public class OntClassAlterado {
	
	private OntClass objeto;
	
	public OntClassAlterado( OntClass obj ){
		this.objeto = obj;
	}
	
	public String toString(){
		return objeto.getLocalName();
	}
	
	public OntClass getOntClass(){
		return this.objeto;
	}
	
}//fim do OntClassAlterado
