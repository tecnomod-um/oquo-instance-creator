package es.um.dis.tecnomod.oquo.service;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;


public class RDFValidator {
	private static final String OQUO_ONTOLOGY = "https://purl.org/oquo";
	//private static final String OQUO_ONTOLOGY = "/home/fabad/ontologies/oquo/ontology/oquo.owl";
	
	private static final String OQUO_SHAPES = "https://raw.githubusercontent.com/tecnomod-um/oquo/main/shacl_eval.ttl";
	//private static final String OQUO_SHAPES =  "/home/fabad/ontologies/oquo/shacl_eval.ttl";
	
	/**
	 * Validate a jena model according to the oquo model
	 * @param instances Jena model with the instances to be validated
	 * @return ValidationReport
	 */
	public static ValidationReport validate(Model instances) {
		OntModel ontology = ModelFactory.createOntologyModel();
		ontology.setDerivationLogging(false);
		ontology.read(OQUO_ONTOLOGY, Lang.RDFXML.getName());
		ontology.add(instances);
		///////////
//		Resource evaluationResult = ontology.getResource("http://purl.org/net/EvaluationResult#");
//		Resource measurement = ontology.getResource("http://ecoinformatics.org/oboe/oboe.1.2/oboe-core.owl#Measurement");
//		Resource qualityMeasure = ontology.getResource("http://purl.org/net/QualityModel#QualityMeasure");
//		Statement statementToTest = ontology.asStatement(new Triple(evaluationResult.asNode(), RDF.type.asNode(), qualityMeasure.asNode()));
//		RDFSRuleInfGraph g = (RDFSRuleInfGraph) ontology.getGraph();
//
//		if (g.contains(statementToTest.asTriple())) {
//			PrintWriter out = new PrintWriter(System.out);
//		    System.out.println("Statement is " + statementToTest);
//		    for (Iterator<Derivation> id = ontology.getDerivation(statementToTest); id.hasNext(); ) {
//		        Derivation deriv = (Derivation) id.next();
//		        deriv.printTrace(out, true);
//		        out.flush();
//		    }
//			
//			
//		}
//		g.getRules().forEach(rule -> {
//			System.out.println(rule);
//		});
		//ontology.write(System.out, "TURTLE");
		/////////////////////////
		Shapes shapes = Shapes.parse(OQUO_SHAPES);
		ValidationReport validationReport = ShaclValidator.get().validate(shapes, ontology.getGraph());
		return validationReport;
	}
}
