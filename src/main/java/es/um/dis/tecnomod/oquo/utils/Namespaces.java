package es.um.dis.tecnomod.oquo.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Namespaces {
	public static final String OQUO_NS = "https://purl.org/oquo#";
	public static final String PROV_NS = "http://www.w3.org/ns/prov#";
	public static final String QM_NS = "http://purl.org/net/QualityModel#";
	public static final String RES_NS = "http://purl.org/net/EvaluationResult#";
	public static final String TIME_NS = "http://www.w3.org/2006/time#";
	public static final String OBOE_NS = "http://ecoinformatics.org/oboe/oboe.1.2/oboe-core.owl#";
	public static final String BTL2_NS = "http://purl.org/biotop/btl2.owl#";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	public static final String IPO_NS = "http://purl.org/ipo/core#";
	public static final String CMNSTXT_NS = "https://www.omg.org/spec/Commons/TextDatatype/";
	
	public static Map<String, String> getPrefixMap() {
		Map<String, String> prefixMap = new HashMap<>();
		for (Field field : Namespaces.class.getDeclaredFields()) {
			String prefix = field.getName().split("_")[0].toLowerCase();
			try {
				String iri = (String) field.get(null);
				prefixMap.put(prefix, iri);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return prefixMap;
	}
}
