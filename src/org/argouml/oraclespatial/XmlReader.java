package org.argouml.oraclespatial;

import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.*;

import org.w3c.dom.*;

public class XmlReader {

    // caminho (path) do arquivo XML
    private String m_caminhoArquivoXML;
    // objeto que manipular� o arquivo com os scripts do BD
    private OracleDatabase m_oracleDB;
    // diret�rio de gera�ao do BD
    private String m_diretorioGeracao;
    // janela para pegar o caminho onde o arquivo sera gerado
    private JanelaGeracao m_janelaGeracao;
    // guarda todos os objetos geoframe
    private ArrayList<ObjetoGeoFrame> m_objetosGeoFrame;
    // variavel utilizada para no tratamento de generalizacoes
    private char m_OpcaoGeneralizacao = 'D';
    private ArrayList<ObjetoGeoFrame> m_participaGeneralizacao;
    private boolean arc = false,  no = false,  rede = false;
    private boolean redearco = false,  redeno = false;

    // construtor que seta o caminho do XML
    public XmlReader(String p_path) {
        m_janelaGeracao = new JanelaGeracao();
        m_diretorioGeracao = m_janelaGeracao.getDir();
        if (m_diretorioGeracao.equals("")) {
            m_diretorioGeracao = "./";
            return;
        }

        m_caminhoArquivoXML = p_path;

        m_objetosGeoFrame = new ArrayList<ObjetoGeoFrame>();
        m_participaGeneralizacao = new ArrayList<ObjetoGeoFrame>();
    }

    public void lerXMI() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = null;

        try {
            doc = db.parse(m_caminhoArquivoXML);
        } catch (Exception e) {
            System.out.println("exce�ao aqui!" + e);
        }

        // pega a raiz do documento XML
        Element elem = doc.getDocumentElement();

        // pega todos os Modelos do XML, os quais representar�o bancos de dados diferentes
        NodeList nl = elem.getElementsByTagName("Model_Management.Model");

        // Assumindo apenas 1 banco de dados
        Element v_esquema = (Element) nl.item(0); // colocar �ndice gen�rico********

        String v_nomeBD = getChildTagValue(v_esquema, "Foundation.Core.ModelElement.name");

        if (testaEsquema(v_esquema)) {
            return; //h� classe sem chave prim�ria (mas se for subclasse eh permitido nao ter chave primaria)
        }
        // cria o arquivo com os scripts do banco de dados
        m_oracleDB = new OracleDatabase(v_nomeBD, m_diretorioGeracao);

        if (!m_oracleDB.isOk()) {
            return;
        }

        NodeList v_geoobjs = v_esquema.getElementsByTagName("Foundation.Core.GeographicObject");
        NodeList v_netobjs = v_esquema.getElementsByTagName("Foundation.Core.NetworkObject");
        NodeList v_ngeoobjs = v_esquema.getElementsByTagName("Foundation.Core.NonGeographicObject");
        NodeList v_geofields = v_esquema.getElementsByTagName("Foundation.Core.GeographicField");

        gerarObjetos(v_geoobjs, v_esquema, true);
        gerarObjetos(v_ngeoobjs, v_esquema, false);
        gerarObjetos(v_geofields, v_esquema, true);
        gerarObjetosNet(v_netobjs, v_esquema, true);

        //NAO ENTRAR AQUI SE NAO TIVER GENERALIZACOES
        if (m_participaGeneralizacao.size() > 0) {
            tratarGeneralizacoes(v_esquema);
        }

        gerarAssociacoes(v_esquema);

        m_oracleDB.finalizar();
    }

    private void gerarAssociacoes(Element p_esquema) throws Exception {
        // guarda as associa��es
        NodeList associations = p_esquema.getElementsByTagName("Foundation.Core.Association");

        for (int i = 0; i < associations.getLength(); i++) {
            Element association = (Element) associations.item(i);

            if (association.hasAttribute("xmi.id")) {
                // ID da classe, many indica se a multiplicidade � de muitos
                // informa��es da primeira classe
                String className1;
                boolean many1;
                // Informa�oes da segunda classe
                String className2;
                boolean many2;

                // multiplicidades
                NodeList multiplicities = association.getElementsByTagName("Foundation.Core.AssociationEnd.multiplicity");
                Element multiplicity = (Element) multiplicities.item(0); // pega primeira multiplicidade
                many1 = getMultiplicity(multiplicity, p_esquema);
                multiplicity = (Element) multiplicities.item(1); // pega a segunda multiplicidade
                many2 = getMultiplicity(multiplicity, p_esquema);

                // nome das das classes envolvidas no relacionamento
                NodeList relations = association.getElementsByTagName("Foundation.Core.AssociationEnd.type");

                Element relation = (Element) relations.item(0); // pega primeira classe
                className1 = getClassName1(relation, p_esquema);
                relation = (Element) relations.item(1); // pega a segunda classe
                className2 = getClassName1(relation, p_esquema);

                //se a opcao for 'C' vai dar certo pq em m_objetosGeoFrame a classe ja estara guardando a nova PK
                ObjetoGeoFrame v_objetoGeoFrame1 = getObjeto(className1, m_objetosGeoFrame);
                ObjetoGeoFrame v_objetoGeoFrame2 = getObjeto(className2, m_objetosGeoFrame);

                String isAgreg = "";
                for (int k = 0; k < 5; k++) {
                    Node item = association.getElementsByTagName("Foundation.Core.AssociationEnd.aggregation").item(k);
                    if (item != null) {
                        isAgreg = item.getAttributes().getNamedItem("xmi.value").getNodeValue();
                    }
                    if (isAgreg.equals("aggregate") || isAgreg.equals("composite")) {
                        break;
                    }
                }
                if (isAgreg.equals("aggregate") || isAgreg.equals("composite")) {
                    //pesquisa nas classes de objetos de rede
                    boolean criachave = false;
                    NodeList netobjs = p_esquema.getElementsByTagName("Foundation.Core.NetworkObject");
                    for (int j = 0; j < netobjs.getLength(); j++) {
                        Element v_obj = (Element) netobjs.item(j);
                        String nomeClasse = getChildTagValue(v_obj, "Foundation.Core.ModelElement.name");
                        if (nomeClasse.equals(className1) || nomeClasse.equals(className2)) {
                            String isarc = "";
                            String isdirectarc = "";
                            isarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
                            isdirectarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isDirectArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();

                            if (isarc.equals("true") || isdirectarc.equals("true")) {
                                redearco = true;
                                criachave = true;
                            }
                            String isno = "";

                            isno = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isNode").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
                            if (isno.equals("true")) {
                                redeno = true;
                                criachave = true;
                            }
                        } else {
                            continue;
                        }
                    }
                    if (criachave) {
                        ObjetoGeoFrame classeRede = new ObjetoGeoFrame(className2);

                        ObjetoGeoFrame REDE = new ObjetoGeoFrame("REDE");
                        Atributo at = new Atributo("IdRede", Atributo.ConverterParaTipoValido("INT"));
                        REDE.adicionarAtributoChavePrimaria(at);
                        at = new Atributo("Tipo", Atributo.ConverterParaTipoValido("STRING"));
                        REDE.adicionarAtributo(at);

                        Relacionamento rel = new Relacionamento(REDE, classeRede, Constantes.RELACIONAMENTO_1_N);
                        m_oracleDB.adicionarRelacionamento(rel);

                    }

                }

                if (m_OpcaoGeneralizacao == 'A') {
                    if (v_objetoGeoFrame1 == null)//entao eh subclasse
                    {
                        String v_superClasse = getParent(className1, p_esquema);
                        v_objetoGeoFrame1 = getObjeto(v_superClasse, m_objetosGeoFrame);
                    }
                    if (v_objetoGeoFrame2 == null)//entao eh subclasse
                    {
                        String v_superClasse = getParent(className2, p_esquema);
                        v_objetoGeoFrame2 = getObjeto(v_superClasse, m_objetosGeoFrame);
                    }
                } else if (m_OpcaoGeneralizacao == 'B')//soh funciona se apenas um dos dois objetos forem superclasse
                {
                    if (v_objetoGeoFrame1 == null)//entao eh superclasse
                    {
                        List v_filhos = getDescendant(className1, p_esquema);

                        for (int k = 0; k < v_filhos.size(); ++k) {
                            v_objetoGeoFrame1 = getObjeto((String) v_filhos.get(k), m_objetosGeoFrame);

                            // tratamento do relacionamento 1 para 1, e 1 para N
                            if (((!many1) && (!many2)) || ((!many1) && (many2))) {
                                // Neste caso, a tabela da classe className1 exporta o campo para a classe className2
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_1_N));
                            } // tratamento do restante do relacionamento 1 para N
                            else if (many1 && (!many2)) {
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame2, v_objetoGeoFrame1, Constantes.RELACIONAMENTO_1_N));
                            } // tratamento do relacionamento M para N
                            else {
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_N_N));
                            }
                        }
                    }
                    if (v_objetoGeoFrame2 == null)//entao eh superclasse
                    {
                        List v_filhos = getDescendant(className2, p_esquema);

                        for (int k = 0; k < v_filhos.size(); ++k) {
                            v_objetoGeoFrame2 = getObjeto((String) v_filhos.get(k), m_objetosGeoFrame);

                            // tratamento do relacionamento 1 para 1, e 1 para N
                            if (((!many1) && (!many2)) || ((!many1) && (many2))) {
                                // Neste caso, a tabela da classe className1 exporta o campo para a classe className2
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_1_N));
                            } // tratamento do restante do relacionamento 1 para N
                            else if (many1 && (!many2)) {
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame2, v_objetoGeoFrame1, Constantes.RELACIONAMENTO_1_N));
                            } // tratamento do relacionamento M para N
                            else {
                                m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_N_N));
                            }
                        }
                    }
                    continue;
                }

                // tratamento do relacionamento 1 para 1, e 1 para N
                if (((!many1) && (!many2)) || ((!many1) && (many2))) {
                    // Neste caso, a tabela da classe className1 exporta o campo para a classe className2
                    m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_1_N));
                } // tratamento do restante do relacionamento 1 para N
                else if (many1 && (!many2)) {
                    m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame2, v_objetoGeoFrame1, Constantes.RELACIONAMENTO_1_N));
                } // tratamento do relacionamento M para N
                else {
                    m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objetoGeoFrame1, v_objetoGeoFrame2, Constantes.RELACIONAMENTO_N_N));
                }

            }
        }
        ObjetoGeoFrame REDE = new ObjetoGeoFrame("net_table");
        Atributo at = new Atributo("IdRede", Atributo.ConverterParaTipoValido("INT"));
        REDE.adicionarAtributoChavePrimaria(at);
        at = new Atributo("Tipo", Atributo.ConverterParaTipoValido("STRING"));
        REDE.adicionarAtributo(at);

        ObjetoGeoFrame NO = new ObjetoGeoFrame("node_table");
        at = new Atributo("IdNo", Atributo.ConverterParaTipoValido("INT"));
        NO.adicionarAtributoChavePrimaria(at);

        ObjetoGeoFrame ARCO = new ObjetoGeoFrame("arc_table");
        at = new Atributo("IdArc", Atributo.ConverterParaTipoValido("INT"));
        ARCO.adicionarAtributoChavePrimaria(at);
        at = new Atributo("IdNo1_FK", Atributo.ConverterParaTipoValido("INT"));
        ARCO.adicionarAtributo(at);
        at = new Atributo("IdNo2_FK", Atributo.ConverterParaTipoValido("INT"));
        ARCO.adicionarAtributo(at);

        if (redearco) {
            ObjetoGeoFrame REDEARCO = new ObjetoGeoFrame("net_arc_table");
            at = new Atributo("IdRede_FK", Atributo.ConverterParaTipoValido("INT"));
            REDEARCO.adicionarAtributoChavePrimaria(at);
            at = new Atributo("IdArc_FK", Atributo.ConverterParaTipoValido("INT"));
            REDEARCO.adicionarAtributoChavePrimaria(at);
            m_objetosGeoFrame.add(REDEARCO);
            m_oracleDB.adicionarTabela(REDEARCO);

            Relacionamento rel = new Relacionamento(REDE, REDEARCO, "IdRede");
            m_oracleDB.adicionarRelacionamento(rel);
            rel = new Relacionamento(ARCO, REDEARCO, "IdArc");
            m_oracleDB.adicionarRelacionamento(rel);
        }
        if (redeno) {
            ObjetoGeoFrame REDENO = new ObjetoGeoFrame("net_node_table");
            at = new Atributo("IdRede_FK", Atributo.ConverterParaTipoValido("INT"));
            REDENO.adicionarAtributoChavePrimaria(at);
            at = new Atributo("IdNo_FK", Atributo.ConverterParaTipoValido("INT"));
            REDENO.adicionarAtributoChavePrimaria(at);
            m_objetosGeoFrame.add(REDENO);
            m_oracleDB.adicionarTabela(REDENO);

            Relacionamento rel = new Relacionamento(REDE, REDENO, "IdRede");
            m_oracleDB.adicionarRelacionamento(rel);
            rel = new Relacionamento(NO, REDENO, "IdNo");
            m_oracleDB.adicionarRelacionamento(rel);

        }
    }

    private ObjetoGeoFrame getObjeto(String p_nomeClasse, ArrayList<ObjetoGeoFrame> p_objetosGeoFrame) {
        ObjetoGeoFrame v_objetoGeoFrame;

        for (int c_indiceObjeto = 0; c_indiceObjeto < p_objetosGeoFrame.size(); ++c_indiceObjeto) {
            v_objetoGeoFrame = p_objetosGeoFrame.get(c_indiceObjeto);
            if (v_objetoGeoFrame.getNome().equals(p_nomeClasse)) {
                return v_objetoGeoFrame;
            }
        }

        return null;
    }

    private void gerarObjetos(NodeList p_objetosGeoFrame, Element p_esquema, boolean p_temRepresentacaoEspacial) throws Exception {
        for (int c_indiceObjGeo = 0; c_indiceObjGeo < p_objetosGeoFrame.getLength(); ++c_indiceObjGeo) {
            Element v_obj = (Element) p_objetosGeoFrame.item(c_indiceObjGeo);
            String v_nomeClasse = getChildTagValue(v_obj, "Foundation.Core.ModelElement.name");

            ObjetoGeoFrame v_objGeoFrame = new ObjetoGeoFrame(v_nomeClasse);

            NodeList attributes = v_obj.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {

                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                Element structType = (Element) structFields.item(0);

                NodeList classifiers = structType.getElementsByTagName("Foundation.Core.Classifier");
                Element classify = (Element) classifiers.item(0);

                // tipo do atributo
                String attrID = classify.getAttribute("xmi.idref");
                String attributeType = getType(p_esquema, attrID);

                Atributo v_atributo = new Atributo(attributeName, Atributo.ConverterParaTipoValido(attributeType));

                // chave prim�ria ou n�o
                boolean pKey = false;

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                    if (teste.equals("primary key") && teste2.equals("true")) {
                        pKey = true;
                    }
                }

                if (pKey) {
                    v_objGeoFrame.adicionarAtributoChavePrimaria(v_atributo);
                } else {
                    v_objGeoFrame.adicionarAtributo(v_atributo);
                }

            }
            if (p_temRepresentacaoEspacial) {
                v_objGeoFrame.adicionarRepresentacaoEspacial();
            }


            //SE CLASSE PARTICIPA DE UMA GENERALIZACAO OU ESPECIALIZACAO NAO CRIA TABELA AGORA
            NodeList v_genera = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
            NodeList v_especia = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
            if ((v_genera.getLength() > 0) || (v_especia.getLength() > 0)) {
                if (v_especia.getLength() > 0) {
                    v_objGeoFrame.setSuperClasse(true);
                } else {
                    v_objGeoFrame.setSubClasse(true);
                }
                m_participaGeneralizacao.add(v_objGeoFrame);
                continue;
            }

            m_objetosGeoFrame.add(v_objGeoFrame);
            m_oracleDB.adicionarTabela(v_objGeoFrame);
        }

    }

    private void gerarObjetosNet(NodeList p_objetosGeoFrame, Element p_esquema, boolean p_temRepresentacaoEspacial) throws Exception {
        for (int c_indiceObjGeo = 0; c_indiceObjGeo < p_objetosGeoFrame.getLength(); ++c_indiceObjGeo) {
            Element v_obj = (Element) p_objetosGeoFrame.item(c_indiceObjGeo);
            String v_nomeClasse = getChildTagValue(v_obj, "Foundation.Core.ModelElement.name");

            ObjetoGeoFrame v_objGeoFrame = new ObjetoGeoFrame(v_nomeClasse);

            NodeList attributes = v_obj.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {

                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList structFields = attr.getElementsByTagName("Foundation.Core.StructuralFeature.type");
                Element structType = (Element) structFields.item(0);

                NodeList classifiers = structType.getElementsByTagName("Foundation.Core.Classifier");
                Element classify = (Element) classifiers.item(0);

                // tipo do atributo
                String attrID = classify.getAttribute("xmi.idref");
                String attributeType = getType(p_esquema, attrID);

                Atributo v_atributo = new Atributo(attributeName, Atributo.ConverterParaTipoValido(attributeType));

                // chave prim�ria ou n�o
                boolean pKey = false;

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");
                    if (teste.equals("primary key") && teste2.equals("true")) {
                        pKey = true;
                    }
                }

                if (pKey) {
                    v_objGeoFrame.adicionarAtributoChavePrimaria(v_atributo);
                } else {
                    v_objGeoFrame.adicionarAtributo(v_atributo);
                }

            }
            boolean geo = false;//TODO
            String ispt = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isPoint").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isli = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isLine").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String ispo = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isPolygon").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isco = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isComplexObj").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();

            if (ispt.equals("true") || isli.equals("true") || ispo.equals("true") || isco.equals("true")) {
                geo = true;
            }

            String isarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isno = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isNode").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isdarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isDirectArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();

            if (isarc.equals("true") || isdarc.equals("true")) {
                arc = true;
                rede = true;
            }
            if (isno.equals("true")) {
                no = true;
                rede = true;
            }

            if (p_temRepresentacaoEspacial && geo) {
                v_objGeoFrame.adicionarRepresentacaoEspacial();
            }


            //SE CLASSE PARTICIPA DE UMA GENERALIZACAO OU ESPECIALIZACAO NAO CRIA TABELA AGORA
            NodeList v_genera = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.generalization");
            NodeList v_especia = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.specialization");
            if ((v_genera.getLength() > 0) || (v_especia.getLength() > 0)) {
                if (v_especia.getLength() > 0) {
                    v_objGeoFrame.setSuperClasse(true);
                } else {
                    v_objGeoFrame.setSubClasse(true);
                }
                m_participaGeneralizacao.add(v_objGeoFrame);
                continue;
            }

            m_objetosGeoFrame.add(v_objGeoFrame);
            m_oracleDB.adicionarTabela(v_objGeoFrame);
        }
        ObjetoGeoFrame ARCO = new ObjetoGeoFrame("arc_table");
        ObjetoGeoFrame NO = new ObjetoGeoFrame("node_table");
        ObjetoGeoFrame REDE = new ObjetoGeoFrame("net_table");
        ObjetoGeoFrame ARCOA = new ObjetoGeoFrame("arc_arc_table");
        if (no) {

            Atributo at = new Atributo("IdNo", Atributo.ConverterParaTipoValido("INT"));
            NO.adicionarAtributoChavePrimaria(at);
            m_objetosGeoFrame.add(NO);
            m_oracleDB.adicionarTabela(NO);

        }
        if (arc) {

            Atributo at = new Atributo("IdArc", Atributo.ConverterParaTipoValido("INT"));
            ARCO.adicionarAtributoChavePrimaria(at);
            at = new Atributo("IdNo1_FK", Atributo.ConverterParaTipoValido("INT"));
            ARCO.adicionarAtributo(at);
            at = new Atributo("IdNo2_FK", Atributo.ConverterParaTipoValido("INT"));
            ARCO.adicionarAtributo(at);
            m_objetosGeoFrame.add(ARCO);
            m_oracleDB.adicionarTabela(ARCO);

            Relacionamento rel = new Relacionamento(NO, ARCO, "IdNo1");
            m_oracleDB.adicionarRelacionamento(rel);
            rel = new Relacionamento(NO, ARCO, "IdNo2");
            m_oracleDB.adicionarRelacionamento(rel);

            at = new Atributo("IdArc1_FK", Atributo.ConverterParaTipoValido("INT"));
            ARCOA.adicionarAtributoChavePrimaria(at);
            at = new Atributo("IdArc2_FK", Atributo.ConverterParaTipoValido("INT"));
            ARCOA.adicionarAtributoChavePrimaria(at);
            m_objetosGeoFrame.add(ARCOA);
            m_oracleDB.adicionarTabela(ARCOA);

            rel = new Relacionamento(ARCO, ARCOA, "IdArc1");
            m_oracleDB.adicionarRelacionamento(rel);
            rel = new Relacionamento(ARCO, ARCOA, "IdArc2");
            m_oracleDB.adicionarRelacionamento(rel);
        }
        if (rede) {

            Atributo at = new Atributo("IdRede", Atributo.ConverterParaTipoValido("INT"));
            REDE.adicionarAtributoChavePrimaria(at);
            at = new Atributo("Tipo", Atributo.ConverterParaTipoValido("STRING"));
            REDE.adicionarAtributo(at);
            m_objetosGeoFrame.add(REDE);
            m_oracleDB.adicionarTabela(REDE);

        }
        for (int c_indiceObjGeo = 0; c_indiceObjGeo < p_objetosGeoFrame.getLength(); ++c_indiceObjGeo) {
            Element v_obj = (Element) p_objetosGeoFrame.item(c_indiceObjGeo);
            String v_nomeClasse = getChildTagValue(v_obj, "Foundation.Core.ModelElement.name");

            ObjetoGeoFrame v_objGeoFrame = new ObjetoGeoFrame(v_nomeClasse);

            NodeList attributes = v_obj.getElementsByTagName("Foundation.Core.Attribute");
            String isarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isno = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isNode").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();
            String isdarc = v_obj.getElementsByTagName("Foundation.Core.GeneralizableElement.isDirectArc").item(0).getAttributes().getNamedItem("xmi.value").getNodeValue();

            if (isarc.equals("true") || isdarc.equals("true")) {
                Relacionamento rel = new Relacionamento(ARCO, v_objGeoFrame, Constantes.RELACIONAMENTO_1_N);
                m_oracleDB.adicionarRelacionamento(rel);

            }
            if (isno.equals("true")) {
                m_oracleDB.adicionarRelacionamento(new Relacionamento(NO, v_objGeoFrame, Constantes.RELACIONAMENTO_1_N));
            }

        }
    }

    private void tratarGeneralizacoes(Element p_esquema) throws Exception {
        JFrame fr = new JFrame();
        JanelaGeneralizacao v_Window = new JanelaGeneralizacao(fr, "Generalization founded");
        m_OpcaoGeneralizacao = v_Window.getOp();

        for (int c_indiceObjGeo = 0; c_indiceObjGeo < m_participaGeneralizacao.size(); ++c_indiceObjGeo) {
            ObjetoGeoFrame v_objGeoFrame = m_participaGeneralizacao.get(c_indiceObjGeo);

            if (v_objGeoFrame.ehSuperClasse() && m_OpcaoGeneralizacao == 'A') {
                List v_filhos = getDescendant(v_objGeoFrame.getNome(), p_esquema);

                for (int i = 0; i < v_filhos.size(); ++i) {
                    ObjetoGeoFrame v_filho = getObjeto((String) v_filhos.get(i), m_participaGeneralizacao);
                    v_objGeoFrame.adicionarAtributos(v_filho.getAtributosNaoGeograficos(true));
                }

                m_objetosGeoFrame.add(v_objGeoFrame);
                m_oracleDB.adicionarTabela(v_objGeoFrame);
            } else if (v_objGeoFrame.ehSuperClasse() && m_OpcaoGeneralizacao == 'B') {
                List v_filhos = getDescendant(v_objGeoFrame.getNome(), p_esquema);

                for (int i = 0; i < v_filhos.size(); ++i) {
                    ObjetoGeoFrame v_filho = getObjeto((String) v_filhos.get(i), m_participaGeneralizacao);
                    v_filho.limpaPK();
                    v_filho.adicionarAtributosPK(v_objGeoFrame.getPK());
                    v_filho.adicionarAtributos(v_objGeoFrame.getAtributosNaoGeograficos(false));

                    m_objetosGeoFrame.add(v_filho);
                    m_oracleDB.adicionarTabela(v_filho);
                }
            } else if (v_objGeoFrame.ehSuperClasse() && m_OpcaoGeneralizacao == 'C') {
                List v_filhos = getDescendant(v_objGeoFrame.getNome(), p_esquema);

                m_objetosGeoFrame.add(v_objGeoFrame);
                m_oracleDB.adicionarTabela(v_objGeoFrame);

                for (int i = 0; i < v_filhos.size(); ++i) {
                    ObjetoGeoFrame v_filho = getObjeto((String) v_filhos.get(i), m_participaGeneralizacao);
                    v_filho.limpaPK();
                    v_filho.adicionarAtributosPK(v_objGeoFrame.getPK());

                    m_objetosGeoFrame.add(v_filho);
                    m_oracleDB.adicionarTabela(v_filho);
                    m_oracleDB.adicionarRelacionamento(new Relacionamento(v_objGeoFrame, v_filho, Constantes.RELACIONAMENTO_GENERALIZACAO));
                }
            }
        }
    }

//*********************************************
//*********************************************
//*********************************************
    //REFAZER O CODIGO ABAIXO DESTE PONTO **************************
//	 retorna lista de descendentes de uma determinada classe
    public List getDescendant(String parentClass, Element pack) throws Exception {

        NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
        NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
        NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
        NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");

        List result = new ArrayList();

        for (int i = 0; i < geoobjs.getLength(); i++) {

            Element geoobj = (Element) geoobjs.item(i);

            String name = getChildTagValue(geoobj, "Foundation.Core.ModelElement.name");
            if (parentClass.equals(name)) {
                NodeList list = geoobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String child = getChild(idRef, pack);
                    String childClass = getClassName2(child, pack);
                    result.add(childClass);
                }

            }
        }

        for (int i = 0; i < netobjs.getLength(); i++) {

            Element netobj = (Element) netobjs.item(i);

            String name = getChildTagValue(netobj, "Foundation.Core.ModelElement.name");
            if (parentClass.equals(name)) {
                NodeList list = netobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String child = getChild(idRef, pack);
                    String childClass = getClassName2(child, pack);
                    result.add(childClass);
                }

            }
        }

        for (int i = 0; i < ngeoobjs.getLength(); i++) {

            Element ngeoobj = (Element) ngeoobjs.item(i);

            String name = getChildTagValue(ngeoobj, "Foundation.Core.ModelElement.name");
            if (parentClass.equals(name)) {
                NodeList list = ngeoobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String child = getChild(idRef, pack);
                    String childClass = getClassName2(child, pack);
                    result.add(childClass);
                }

            }
        }

        for (int i = 0; i < geofields.getLength(); i++) {

            Element geofield = (Element) geofields.item(i);

            String name = getChildTagValue(geofield, "Foundation.Core.ModelElement.name");
            if (parentClass.equals(name)) {
                NodeList list = geofield.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String child = getChild(idRef, pack);
                    String childClass = getClassName2(child, pack);
                    result.add(childClass);
                }

            }
        }

        return result;

    }

    public String getChild(String idRef, Element pack) {
        NodeList generalizations = pack.getElementsByTagName("Foundation.Core.Generalization");
        for (int i = 0; i < generalizations.getLength(); i++) {
            Element generalization = (Element) generalizations.item(i);
            String attr = generalization.getAttribute("xmi.id");
            if (attr.equals(idRef)) {
                NodeList children = generalization.getElementsByTagName("Foundation.Core.GeneralizableElement");
                Element child = (Element) children.item(0);
                String id = child.getAttribute("xmi.idref");
                return id;
            } else {
                continue;
            }
        }

        return "erro!!!";
    }

    // fun��o que testa se as classes do esquema possuem chave prim�ria
    // se existe classe sem chave prim�ria, retorna true e imprime o nome da classe
    public boolean testaEsquema(Element model) {
        //PEGANDO ELEMENTOS DO MODELO E NAO DO PACOTE, ELE VERIFICA AS CLASSES QUE FORAM DELETADAS DO ESQUEMA MAS QUE CONTINUAM DISPON�VEIS NO PAINEL DO CANTO ESQUERDO DA FERRAMENTA
        NodeList geoobjs = model.getElementsByTagName("Foundation.Core.GeographicObject");
        String classesSemPK = "";
        boolean temErro = false;

        for (int i = 0; i < geoobjs.getLength(); i++) {
            boolean temPK = false;
            Element geo = (Element) geoobjs.item(i);
            String nome = getChildTagValue(geo, "Foundation.Core.ModelElement.name");

            String pai = getParent(nome, model);
            if (!pai.equals(nome) && !pai.equals("")) {
                continue; //se for subclasse, eh permitido que ela nao tenha chave primaria
            }

            NodeList attributes = geo.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {
                //child.getFirstChild().getNodeValue(); ATENCAO TESTE
                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");

                    if (teste.equals("primary key") && teste2.equals("true")) {
                        temPK = true;
                    }
                }
            }

            if (!temPK) {
                classesSemPK += (nome + '\n');
                temErro = true;
            }
        }

        NodeList netobjs = model.getElementsByTagName("Foundation.Core.NetworkObject");

        for (int i = 0; i < netobjs.getLength(); i++) {
            boolean temPK = false;
            Element net = (Element) netobjs.item(i);
            String nome = getChildTagValue(net, "Foundation.Core.ModelElement.name");

            String pai = getParent(nome, model);
            if (!pai.equals(nome) && !pai.equals("")) {
                continue; //se for subclasse, eh permitido que ela nao tenha chave primaria
            }

            NodeList attributes = net.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {
                //child.getFirstChild().getNodeValue(); ATENCAO TESTE
                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");

                    if (teste.equals("primary key") && teste2.equals("true")) {
                        temPK = true;
                    }
                }
            }

            if (!temPK) {
                classesSemPK += (nome + '\n');
                temErro = true;
            }
        }

        NodeList ngeoobjs = model.getElementsByTagName("Foundation.Core.NonGeographicObject");

        for (int i = 0; i < ngeoobjs.getLength(); i++) {
            boolean temPK = false;
            Element ngeo = (Element) ngeoobjs.item(i);
            String nome = getChildTagValue(ngeo, "Foundation.Core.ModelElement.name");

            String pai = getParent(nome, model);
            if (!pai.equals(nome) && !pai.equals("")) {
                continue; //se for subclasse, eh permitido que ela nao tenha chave primaria
            }

            NodeList attributes = ngeo.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {
                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");

                    if (teste.equals("primary key") && teste2.equals("true")) {
                        temPK = true;
                    }
                }
            }

            if (!temPK) {
                classesSemPK += (nome + '\n');
                temErro = true;
            }

        }

        NodeList geofields = model.getElementsByTagName("Foundation.Core.GeographicField");

        for (int i = 0; i < geofields.getLength(); i++) {
            boolean temPK = false;
            Element geofieldobj = (Element) geofields.item(i);
            String nome = getChildTagValue(geofieldobj, "Foundation.Core.ModelElement.name");

            String pai = getParent(nome, model);
            if (!pai.equals(nome) && !pai.equals("")) {
                continue; //se for subclasse, eh permitido que ela nao tenha chave primaria
            }

            NodeList attributes = geofieldobj.getElementsByTagName("Foundation.Core.Attribute");

            for (int j = 0; j < attributes.getLength(); j++) {
                Element attr = (Element) attributes.item(j);
                // nome do atributo
                String attributeName = getChildTagValue(attr, "Foundation.Core.ModelElement.name");

                NodeList pKeys = attr.getElementsByTagName("Foundation.Extension_Mechanisms.TaggedValue");
                for (int k = 0; k < pKeys.getLength(); k++) {
                    Element temp = (Element) pKeys.item(k);
                    String teste = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.tag");
                    String teste2 = getChildTagValue(temp, "Foundation.Extension_Mechanisms.TaggedValue.value");

                    if (teste.equals("primary key") && teste2.equals("true")) {
                        temPK = true;
                    }
                }
            }

            if (!temPK) {
                classesSemPK += (nome + '\n');
                temErro = true;
            }
        }

        if (temErro) {
            JOptionPane.showMessageDialog(null, "Add a Primary Key in these classes: \n" + classesSemPK, "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return temErro;
    }

    // retorna a classe pai de uma determinada subclasse
    public String getParent(String descendantClass, Element pack) {
        NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
        NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
        NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
        NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");

        //List result = new ArrayList();
        for (int i = 0; i < geoobjs.getLength(); i++) {
            Element geoobj = (Element) geoobjs.item(i);

            String name = getChildTagValue(geoobj, "Foundation.Core.ModelElement.name");
            if (descendantClass.equals(name)) {
                NodeList list = geoobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
                {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String parent = getSuper(idRef, pack);
                    String parentClass = getClassName2(parent, pack);
                    //result.add(parentClass);
                    return parentClass;
                }
                return "";
            }
        }

        for (int i = 0; i < netobjs.getLength(); i++) {
            Element netobj = (Element) netobjs.item(i);

            String name = getChildTagValue(netobj, "Foundation.Core.ModelElement.name");
            if (descendantClass.equals(name)) {
                NodeList list = netobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
                {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String parent = getSuper(idRef, pack);
                    String parentClass = getClassName2(parent, pack);
                    //result.add(parentClass);
                    return parentClass;
                }
                return "";
            }
        }

        for (int i = 0; i < ngeoobjs.getLength(); i++) {

            Element ngeoobj = (Element) ngeoobjs.item(i);

            String name = getChildTagValue(ngeoobj, "Foundation.Core.ModelElement.name");
            if (descendantClass.equals(name)) {
                NodeList list = ngeoobj.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) //ATENCAO - VERIFICAR SE PRECISA DESSE FOR
                {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String parent = getSuper(idRef, pack);
                    String parentClass = getClassName2(parent, pack);
                    return parentClass;
                }
                return "";
            }
        }

        for (int i = 0; i < geofields.getLength(); i++) {

            Element geofield = (Element) geofields.item(i);

            String name = getChildTagValue(geofield, "Foundation.Core.ModelElement.name");
            if (descendantClass.equals(name)) {
                NodeList list = geofield.getElementsByTagName("Foundation.Core.Generalization");
                for (int j = 0; j < list.getLength(); j++) {
                    Element gen = (Element) list.item(j);
                    String idRef = gen.getAttribute("xmi.idref");
                    String parent = getSuper(idRef, pack);
                    String parentClass = getClassName2(parent, pack);
                    return parentClass;
                }
                return "";
            }
        }

        //return result;
        return "erro!!!!!!!!!!!!!11";
    }

    public String getSuper(String idRef, Element pack) {
        NodeList generalizations = pack.getElementsByTagName("Foundation.Core.Generalization");
        for (int i = 0; i < generalizations.getLength(); i++) {
            Element generalization = (Element) generalizations.item(i);
            String attr = generalization.getAttribute("xmi.id");
            if (attr.equals(idRef)) {
                NodeList children = generalization.getElementsByTagName("Foundation.Core.GeneralizableElement");
                Element child = (Element) children.item(1); //troquei 0 por 1
                String id = child.getAttribute("xmi.idref");
                return id;
            } else {
                continue;
            }
        }

        return "erro!!!";
    }

    // Dado o ID, retorna o nome da classe
    public String getClassName2(String idRef, Element pack) {
        NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
        NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
        NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
        NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");

        for (int i = 0; i < geoobjs.getLength(); i++) {
            Element geoobj = (Element) geoobjs.item(i);
            String attr = geoobj.getAttribute("xmi.id");

            if (idRef.equals(attr)) {
                String className = getChildTagValue(geoobj, "Foundation.Core.ModelElement.name");
                return className;
            }
        }

        for (int i = 0; i < netobjs.getLength(); i++) {
            Element netobj = (Element) netobjs.item(i);
            String attr = netobj.getAttribute("xmi.id");

            if (idRef.equals(attr)) {
                String className = getChildTagValue(netobj, "Foundation.Core.ModelElement.name");
                return className;
            }
        }

        for (int i = 0; i < ngeoobjs.getLength(); i++) {
            Element ngeoobj = (Element) ngeoobjs.item(i);
            String attr = ngeoobj.getAttribute("xmi.id");
            System.out.println("ids que encontro: " + attr);
            if (idRef.equals(attr)) {
                String className = getChildTagValue(ngeoobj, "Foundation.Core.ModelElement.name");
                return className;
            }
        }

        for (int i = 0; i < geofields.getLength(); i++) {
            Element geofield = (Element) geofields.item(i);
            String attr = geofield.getAttribute("xmi.id");
            System.out.println("ids que encontro: " + attr);
            if (idRef.equals(attr)) {
                String className = getChildTagValue(geofield, "Foundation.Core.ModelElement.name");
                return className;
            }
        }

        return "erro";
    }

    //	 este m�todo l� e retorna o conte�do (texto) de uma tag (elemento)
    // filho da tag informada como par�metro. A tag filho a ser pesquisada
    // � a tag informada pelo nome (string)
    public static String getChildTagValue(Element elem, String tagName) {//throws Exception { // voltar para private depois
        NodeList children = elem.getElementsByTagName(tagName);
        if (children == null) {
            return null;
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            return null;
        }
        return child.getFirstChild().getNodeValue();
    }

    private String getType(Element model, String id) {
        NodeList dataTypes = model.getElementsByTagName("Foundation.Core.DataType");

        for (int i = 0; i < dataTypes.getLength(); i++) {
            Element dataType = (Element) dataTypes.item(i);
            String resultID = dataType.getAttribute("xmi.id");
            try {
                if (resultID.equals(id)) {
                    String s = getChildTagValue(dataType, "Foundation.Core.ModelElement.name");
                    if (s != null) {
                        return s;
                    }

                }
            } catch (Exception e) {
                System.out.println("Exce�ao ao se tentar achar o tipo!");
            }
        }

        dataTypes = model.getElementsByTagName("Foundation.Core.Class");

        for (int i = 0; i < dataTypes.getLength(); i++) {
            Element dataType = (Element) dataTypes.item(i);
            String resultID = dataType.getAttribute("xmi.id");
            try {
                if (resultID.equals(id)) {
                    String s = getChildTagValue(dataType, "Foundation.Core.ModelElement.name");
                    if (s != null) {
                        return s;
                    }

                }
            } catch (Exception e) {
                System.out.println("Exce�ao ao se tentar achar o tipo!");
            }
        }
        return null;

    }

    // obt�m a multiplicidade de determinado elemento
    // true para muitos e false caso contr�rio
    public boolean getMultiplicity(Element multiplicity, Element elem) {

        String inferior = "", superior = "";
        try {
            inferior = getChildTagValue(multiplicity, "Foundation.Data_Types.MultiplicityRange.lower");
            superior = getChildTagValue(multiplicity, "Foundation.Data_Types.MultiplicityRange.upper");
        } catch (Exception e) {
            System.out.println("Multiplicidade com problemas!");
        }

        if (inferior == null) {
            NodeList l = multiplicity.getElementsByTagName("Foundation.Data_Types.Multiplicity");
            Element e = (Element) l.item(0);
            String ref = e.getAttribute("xmi.idref");
            return searchID(ref, elem);
        } else {
            if (superior.equals("-1")) {
                return true;
            } else {
                return false;
            }

        }

    }

    public boolean searchID(String ref, Element elem) {

        NodeList list = elem.getElementsByTagName("Foundation.Data_Types.Multiplicity");
        String value = "";

        try {
            for (int i = 0; i < list.getLength(); i++) {

                Element e = (Element) list.item(i);
                String attr = e.getAttribute("xmi.id");
                if (ref.equals(attr)) {
                    value = getChildTagValue(e, "Foundation.Data_Types.MultiplicityRange.upper");

                }

            }
        } catch (Exception e) {
            System.out.println("Deu erro!");


        }

        return (value.equals("-1"));
    }

    public String getClassName1(Element relation, Element pack) throws Exception {
        NodeList classifiers = relation.getElementsByTagName("Foundation.Core.Classifier");
        Element classifier = (Element) classifiers.item(0);
        String idref = classifier.getAttribute("xmi.idref");

        //pesquisa nas classes de objetos geogr�ficos
        NodeList geoobjs = pack.getElementsByTagName("Foundation.Core.GeographicObject");
        //pesquisa nas classes de objetos de rede
        NodeList netobjs = pack.getElementsByTagName("Foundation.Core.NetworkObject");
        //pesquisa nas classes de objetos n�o-geogr�ficos
        NodeList ngeoobjs = pack.getElementsByTagName("Foundation.Core.NonGeographicObject");
        // pesquisa nas classes de campos geogr�ficos
        NodeList geofields = pack.getElementsByTagName("Foundation.Core.GeographicField");

        if (geoobjs.getLength() > 0) {

            for (int i = 0; i < geoobjs.getLength(); i++) {
                Element geoobj = (Element) geoobjs.item(i);
                String attribute = geoobj.getAttribute("xmi.id");
                if (attribute.equals(idref)) {
                    String className = getChildTagValue(geoobj, "Foundation.Core.ModelElement.name");
                    return className;

                }
            }
        }

        if (netobjs.getLength() > 0) {

            for (int i = 0; i < netobjs.getLength(); i++) {
                Element netobj = (Element) netobjs.item(i);
                String attribute = netobj.getAttribute("xmi.id");
                if (attribute.equals(idref)) {
                    String className = getChildTagValue(netobj, "Foundation.Core.ModelElement.name");
                    return className;

                }
            }
        }

        if (ngeoobjs.getLength() > 0) {

            for (int i = 0; i < ngeoobjs.getLength(); i++) {
                Element ngeoobj = (Element) ngeoobjs.item(i);
                String attribute = ngeoobj.getAttribute("xmi.id");
                if (attribute.equals(idref)) {
                    String className = getChildTagValue(ngeoobj, "Foundation.Core.ModelElement.name");
                    return className;

                }
            }
        }

        if (geofields.getLength() > 0) {

            for (int i = 0; i < geofields.getLength(); i++) {
                Element geofield = (Element) geofields.item(i);
                String attribute = geofield.getAttribute("xmi.id");
                if (attribute.equals(idref)) {
                    String className = getChildTagValue(geofield, "Foundation.Core.ModelElement.name");
                    return className;

                }
            }
        }

        throw new Exception("Erro!!!!!");
    }
}
