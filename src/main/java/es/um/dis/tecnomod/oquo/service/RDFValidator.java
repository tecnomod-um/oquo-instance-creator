package es.um.dis.tecnomod.oquo.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;


public class RDFValidator {
	private static final String OQUO_ONTOLOGY = "https://purl.archive.org/oquo";
	private static final String OQUO_SHAPES = "https://raw.githubusercontent.com/tecnomod-um/oquo/main/shacl_eval.ttl";
	
	/**
	 * Validate a jena model according to the oquo model
	 * @param instances Jena model with the instances to be validated
	 * @return ValidationReport
	 */
	public static ValidationReport validate(Model instances) {
		Model ontology = RDFDataMgr.loadModel(OQUO_ONTOLOGY, Lang.RDFXML);
		ontology.add(instances);
		Shapes shapes = Shapes.parse(OQUO_SHAPES);
		ValidationReport validationReport = ShaclValidator.get().validate(shapes, ontology.getGraph());
		return validationReport;
	}
}
