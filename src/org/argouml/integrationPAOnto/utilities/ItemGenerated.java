package org.argouml.integrationPAOnto.utilities;

public class ItemGenerated {
	
	private String padrao;
	private String classeDoPadrao;
	private String ontologia;
	private String classeDaOntologia;
	
	public ItemGenerated(String padrao, String classeDoPadrao, String ontologia, String classeDaOntologia) {
		this.padrao = padrao;
		this.classeDoPadrao = classeDoPadrao;
		this.ontologia = ontologia;
		this.classeDaOntologia = classeDaOntologia;
	}

	public String getPadrao() {
		return padrao;
	}

	public String getClasseDoPadrao() {
		return classeDoPadrao;
	}

	public String getOntologia() {
		return ontologia;
	}

	public String getClasseDaOntologia() {
		return classeDaOntologia;
	}
	
	public String toString(){
		return "padrao: " + padrao + "; classe do padrao: " + classeDoPadrao +
					 ";\nontologia: " + ontologia + "; classe da ontologia: " + classeDaOntologia +"\n";
	}
	
}//fim do ItemGenerated
