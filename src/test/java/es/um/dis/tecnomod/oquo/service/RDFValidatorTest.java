package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.vocabulary.OWL;
import org.junit.jupiter.api.Test;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

class RDFValidatorTest {

	@Test
	void testValidate1() {
		Model model = ModelFactory.createDefaultModel();
		assertTrue(model.isEmpty());
		
		InstanceCreator.createObservation(model, createObservation1());
		InstanceCreator.createObservation(model, createObservation2());
		InstanceCreator.createObservation(model, createObservation3());
		assertFalse(model.isEmpty());
		
		ValidationReport report = RDFValidator.validate(model);
		assertTrue(report.conforms());
		//ShLib.printReport(report);
	}
	
	@Test
	void testValidate2() {
		Model model = this.generateNonCompliantModel();
		
		ValidationReport report = RDFValidator.validate(model);
		assertFalse(report.conforms());
		//ShLib.printReport(report);
		
	}

	
	private ObservationInfoDTO createObservation1() {
		ObservationInfoDTO observation = new ObservationInfoDTO();
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150");
		observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI());
		observation.setInstrumentIRI(null);
		observation.setMetricUsedIRI("https://purl.archive.org/oquo#NamesPerClassMetric");
		observation.setObservablePropertyIRI(null);
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");
		observation.setScaleIRI("https://purl.archive.org/oquo#NamesPerClassMetricScale");
		observation.setScaleTypeIRI("https://purl.archive.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setUnitOfMeasureIRI(null);
		observation.setValue(Integer.valueOf(1));
		return observation;
	}
	
	private ObservationInfoDTO createObservation2() {
		ObservationInfoDTO observation = new ObservationInfoDTO();
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150");
		observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI());
		observation.setInstrumentIRI(null);
		observation.setMetricUsedIRI("https://purl.archive.org/oquo#DescriptionsPerClassMetric");
		observation.setObservablePropertyIRI(null);
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");
		observation.setScaleIRI("https://purl.archive.org/oquo#DescriptionsPerClassMetricScale");
		observation.setScaleTypeIRI("https://purl.archive.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setUnitOfMeasureIRI(null);
		observation.setValue(Integer.valueOf(1));
		return observation;
	}
	
	private ObservationInfoDTO createObservation3() {
		ObservationInfoDTO observation = new ObservationInfoDTO();
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestTypeIRI(OWL.Ontology.getURI());
		observation.setInstrumentIRI(null);
		observation.setMetricUsedIRI("https://purl.archive.org/oquo#DescriptionsPerClassMetric");
		observation.setObservablePropertyIRI(null);
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");
		observation.setScaleIRI("https://purl.archive.org/oquo#DescriptionsPerClassMetricScale");
		observation.setScaleTypeIRI("https://purl.archive.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setUnitOfMeasureIRI(null);
		observation.setValue(Double.valueOf(0.85));
		return observation;
	}
	
	private Model generateNonCompliantModel() {
		Model model = ModelFactory.createDefaultModel();
		Resource evaluation = model.createResource(Namespaces.OQUO_NS + "evaluationIns", model.createResource(Namespaces.RES_NS + "Evaluation"));
		Property evaluatedSubject = model.createProperty(Namespaces.RES_NS + "evaluatedSubject");
		Resource evaluationSubject = model.createResource("http://purl.obolibrary.org/obo/GO_0008150",
				model.createResource(Namespaces.RES_NS + "EvaluationSubject"));
		model.add(evaluation, evaluatedSubject, evaluationSubject);
		
		return model;
	}
}
