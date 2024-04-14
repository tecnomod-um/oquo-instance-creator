package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import es.um.dis.tecnomod.oquo.dto.ConfigurationInfoDTO;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.utils.IssueTypes;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

class InstanceCreatorTest {

	@Test
	void testCreateObservation() {
		// Create jena model to store the rdf
		Model model = ModelFactory.createDefaultModel();
		assertTrue(model.isEmpty());

		// Create an observation
		ObservationInfoDTO observation = new ObservationInfoDTO();

		// Fill observation data
		/* Ontology version IRI if available; ontology IRI if not */
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl"); // Gene Ontology version 2021-02-01

		/*
		 * Feature of interest - the IRI of any entity of the ontology (class,
		 * properties...) or the IRI of the ontology itself
		 */
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150"); // The class GO_0008150

		/*
		 * The type of the feature of interest (owl:Class, owl:ObjectProperty,
		 * owl:AnnotationProperty, owl:Ontology...)
		 */
		observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI()); // "owl:Class"

		/* IRI of the instrument used to perform the observation - NOT USED */
		observation.setInstrumentIRI(null);

		/*
		 * IRI of the metric used to measure the feature of interest. Normally taken
		 * from oquo ontology
		 * (https://raw.githubusercontent.com/tecnomod-um/oquo/main/ontology/oquo.owl)
		 */
		observation.setMetricUsedIRI("https://purl.archive.org/oquo#NamesPerClassMetric"); // Names per class

		/*
		 * IRI of the property being measured (names, synonyms, descriptions...) - NOT
		 * USED
		 */
		observation.setObservablePropertyIRI(null);

		/*
		 * IRI of the ranking function, which can be
		 * <http://purl.org/net/QualityModel#HigherBest> or
		 * <http://purl.org/net/QualityModel#LowerBest>
		 */
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");

		/*
		 * The IRI of the scale used by the measure. - I was using the same as the
		 * metric iri plus the word "Scale", so that every measure with the same metric
		 * is related to the same scale instance.
		 */
		observation.setScaleIRI("https://purl.archive.org/oquo#namesPerClassMetricScale");

		/*
		 * The type of the scale used by the measure - I was using
		 * <https://purl.archive.org/oquo#RawScale> to identify the scale is the raw
		 * value of the metric.
		 */
		observation.setScaleTypeIRI("https://purl.archive.org/oquo#RawScale");

		/* The timestamp */
		observation.setTimestamp(Calendar.getInstance());

		/* The IRI of the unit of measure - NOT USED */
		observation.setUnitOfMeasureIRI(null);

		/*
		 * The value of the observation. This is an object, so you can pass an integer,
		 * a decimal, a boolean, etc...
		 */
		observation.setValue(Integer.valueOf(1));
		
		
		/*
		 * The configuration used in the evaluation. We use the class ConfigurationInfoDTO:
		 */
		ConfigurationInfoDTO configurationInfoDto = new ConfigurationInfoDTO();
		
		/*
		 * We set the IRI to use when creating the configuration instance.
		 * This allows to reuse the same configuration instance when you have
		 * many observations with a single configuration. Just make sure that
		 * the IRI you use is the same for all evaluations sharing the configuration.
		 */
		configurationInfoDto.setConfigurationIRI(Namespaces.OQUO_NS + "myconfig");
		
		/*
		 * We set the configuration as a string with a serialization of
		 * the configuration. This is plain text, you can use xml, json, yaml
		 * or whatever.
		 */
		configurationInfoDto.setSerializedConfiguration("tokenizer_strategy: blank_space\nthreshold: 0.3");
		
		/*
		 * We add the configuration into the observation object. The observation
		 * has a list of configurations, so you can have more than one configuration
		 * attached to an evaluation
		 */
		List<ConfigurationInfoDTO> configList = Arrays.asList(configurationInfoDto);
		observation.setConfigurationDataList(configList);
		
		
		
		/*
		 * The issues discovered in the observation. We use the class IssueInfoDTO
		 */
		IssueInfoDTO issueInfoDTO = new IssueInfoDTO();
		
		/*
		 * We set the message associated with the issue
		 */
		issueInfoDTO.setMessage("The class GO:0008150 does not have any name");
		
		/*
		 * We set the type of the issue by assigning its severity
		 */
		issueInfoDTO.setIssueType(IssueTypes.MAJOR_ISSUE);
		
		/*
		 * We add the issueInfoDTO object into the observation object.
		 * The observation object has a list of issues, so it is possible to
		 * link several issues to the same observation
		 */
		observation.setIssues(Arrays.asList(issueInfoDTO));
		
		
		// Add the observation into the jena model
		InstanceCreator.createObservation(model, observation);
		assertFalse(model.isEmpty());
		
		// Optionally you can check if the rdf data conforms the ontology
		ValidationReport report = RDFValidator.validate(model);
		ShLib.printReport(report);
		assertTrue(report.conforms());
		
		// Optionally, you can include a map with the prefixes to use. The Namespaces can provide this map.
		model.setNsPrefixes(Namespaces.getPrefixMap());
		
		// See the rdf
		// You can use "TURTLE", "NTRIPLES", "TRIG", "RDFXML"...
		// You can use a FileOutputStream instead of System.out to write to a file.
		model.write(System.out, "TURTLE");
	}
	
	@Test
	void testCreateObservation2() {
		Model model = ModelFactory.createDefaultModel();
		assertTrue(model.isEmpty());
		
		ObservationInfoDTO observation1 = createObservation1();
		ObservationInfoDTO observation2 = createObservation2();
		ObservationInfoDTO observation3 = createObservation3();
		
		InstanceCreator.createObservation(model, observation1);
		InstanceCreator.createObservation(model, observation2);
		InstanceCreator.createObservation(model, observation3);
		assertFalse(model.isEmpty());
		
		ValidationReport report = RDFValidator.validate(model);
		ShLib.printReport(report);
		assertTrue(report.conforms());
		
		Resource evaluationClass = model.createResource(Namespaces.RES_NS + "Evaluation");
		assertEquals(3, model.listSubjectsWithProperty(RDF.type, evaluationClass).toList().size());
		model.setNsPrefixes(Namespaces.getPrefixMap());
		model.write(System.out, "TURTLE");
		
	}
	
	@Test
	void testGetEvaluationIRI() {
		ObservationInfoDTO observation1 = createObservation1();
		ObservationInfoDTO observation2 = createObservation2();
		ObservationInfoDTO observation3 = createObservation3();
		System.out.println(InstanceCreator.getEvaluationIRI(observation1));
		System.out.println(InstanceCreator.getEvaluationIRI(observation2));
		System.out.println(InstanceCreator.getEvaluationIRI(observation3));
		assertEquals(InstanceCreator.getEvaluationIRI(observation1), InstanceCreator.getEvaluationIRI(observation2));
		assertNotEquals(InstanceCreator.getEvaluationIRI(observation1), InstanceCreator.getEvaluationIRI(observation3));
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
		observation.setScaleIRI("https://purl.archive.org/oquo#namesPerClassMetricScale");
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
		observation.setScaleIRI("https://purl.archive.org/oquo#descriptionsPerClassMetricScale");
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
		observation.setScaleIRI("https://purl.archive.org/oquo#descriptionsPerClassMetricScale");
		observation.setScaleTypeIRI("https://purl.archive.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setUnitOfMeasureIRI(null);
		observation.setValue(Double.valueOf(0.85));
		return observation;
	}

}
