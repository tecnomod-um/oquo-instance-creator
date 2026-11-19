package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationsInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

/**
 * Tests of InstanceCreator.createObservationsAndApplyModel, which creates the observations,
 * adds the OQUO module that describes them (with its imports) and applies its scale conversions.
 *
 * These tests read the OQUO modules from their PURLs, so they need network access.
 */
class InstanceCreatorModelTest {

	private static final String OQUO_HURON_IRI = "https://purl.org/oquo-huron";
	private static final String OQUO_OQUARE_QM_IRI = "https://purl.org/oquo-oquare-qm";
	private static final String OQUO_CORE_IRI = "https://purl.org/oquo-core";
	private static final String DATA_NAMESPACE = "https://example.org/oquo-test-data/";

	private static final String HURON_NS = "https://purl.org/oquo-huron#";
	private static final String OQUARE_QM_NS = "https://purl.org/oquo-oquare-qm#";
	private static final String NAMES_PER_CLASS_METRIC = HURON_NS + "NamesPerClassMetric";
	private static final String NAMES_PER_CLASS_METRIC_SCALE = HURON_NS + "NamesPerClassMetricScale";
	private static final String DESCRIPTIONS_PER_CLASS_METRIC = HURON_NS + "DescriptionsPerClassMetric";
	private static final String DESCRIPTIONS_PER_CLASS_METRIC_SCALE = HURON_NS + "DescriptionsPerClassMetricScale";
	private static final String HURON_AGENT = HURON_NS + "huron-software-agent";
	private static final String OQUARE_SCALE = OQUARE_QM_NS + "OquareFiveCategoriesScale";

	private static final String IS_ABOUT = "http://purl.obolibrary.org/obo/IAO_0000136";

	@Test
	void testOntologyIsIncluded() {
		Model model = createModel(observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 0.5));

		// The module and its imports are included
		Set<String> ontologies = model.listSubjectsWithProperty(RDF.type, OWL.Ontology).toList().stream()
				.map(Resource::getURI)
				.collect(Collectors.toSet());
		assertTrue(ontologies.contains(OQUO_HURON_IRI));
		assertTrue(ontologies.contains(OQUO_OQUARE_QM_IRI));
		assertTrue(ontologies.contains(OQUO_CORE_IRI));

		// The imports are not kept, since their content is already in the model
		assertFalse(model.contains(null, OWL.imports, (RDFNode) null));

		// The metric is described in the model
		assertTrue(model.contains(model.getResource(NAMES_PER_CLASS_METRIC), RDF.type, OWL.Class));
	}

	@ParameterizedTest(name = "value {0} -> OQuaRE category {1}")
	@CsvSource({
		"0.0,  1",
		"0.5,  1",
		"0.75, 2",  // closed lower bound of [0.75, 0.9)
		"0.9,  3",
		"0.97, 4",
		"1.0,  5",
		"3.0,  5"   // [1.0, INF)
	})
	void testCardinalToOrdinalConversion(double value, int expectedCategory) {
		Model model = createModel(observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, value));

		Resource original = getOriginalQualityValue(model);
		Resource converted = getSingleConvertedQualityValue(model, original);

		assertEquals(OQUARE_SCALE, getResource(converted, Namespaces.EVAL_NS + "isMeasuredOnScale").getURI());
		assertEquals(expectedCategory,
				converted.getProperty(property(model, Namespaces.EVAL_NS + "hasLiteralValue")).getInt());
		assertEquals(OQUARE_QM_NS + "OquareCategory" + expectedCategory, getResource(converted, IS_ABOUT).getURI());
	}

	@Test
	void testConvertedQualityValueLinks() {
		ObservationInfoDTO observation = observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 0.5);
		observation.setAgentIRI(HURON_AGENT);
		observation.setAgentTypeIRI(Namespaces.PROV_NS + "SoftwareAgent");
		Model model = createModel(observation);

		Resource original = getOriginalQualityValue(model);
		Resource converted = getSingleConvertedQualityValue(model, original);

		// Same types, measure, evaluation and agent as the original quality value
		assertTrue(converted.hasProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "QualityValue")));
		for (String link : Arrays.asList(Namespaces.EVAL_NS + "forMeasure", Namespaces.EVAL_NS + "obtainedFrom",
				Namespaces.PROV_NS + "wasGeneratedBy", Namespaces.PROV_NS + "wasAttributedTo")) {
			assertEquals(getResource(original, link), getResource(converted, link), link);
		}
		assertEquals(HURON_AGENT, getResource(converted, Namespaces.PROV_NS + "wasAttributedTo").getURI());

		// The evaluation generated both values, but only the original one is its produced quality value
		Resource evaluation = getResource(original, Namespaces.EVAL_NS + "obtainedFrom");
		assertTrue(evaluation.hasProperty(property(model, Namespaces.PROV_NS + "generated"), original));
		assertTrue(evaluation.hasProperty(property(model, Namespaces.PROV_NS + "generated"), converted));
		List<Statement> produced = evaluation
				.listProperties(property(model, Namespaces.EVAL_NS + "producedQualityValue")).toList();
		assertEquals(1, produced.size());
		assertEquals(original, produced.get(0).getResource());

		// The converted value is not a value of the measurement
		Resource measurement = getResource(original, Namespaces.EVAL_NS + "forMeasure");
		assertFalse(measurement.hasProperty(property(model, Namespaces.OBOE_NS + "hasValue"), converted));
	}

	@Test
	void testDataNamespace() {
		Model model = createModel(observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 0.5));

		Resource original = getOriginalQualityValue(model);
		Resource converted = getSingleConvertedQualityValue(model, original);
		assertTrue(original.getURI().startsWith(DATA_NAMESPACE));
		assertTrue(converted.getURI().startsWith(DATA_NAMESPACE));
	}

	@Test
	void testNoConversionWhenNoMappingMatches() {
		// No point mapping of the conversion of NamesPerClassMetricScale covers negative values
		Model model = createModel(observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, -1.0));

		Resource original = getOriginalQualityValue(model);
		assertTrue(getConvertedQualityValues(model, original).isEmpty());
		assertEquals(1, countQualityValues(model));
	}

	@Test
	void testSeveralObservations() {
		Model model = createModel(
				observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 0.5),
				observation(DESCRIPTIONS_PER_CLASS_METRIC, DESCRIPTIONS_PER_CLASS_METRIC_SCALE, 0.85),
				observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 1.0));

		// One original and one converted quality value per observation
		assertEquals(6, countQualityValues(model));
		assertEquals(3, model.listSubjectsWithProperty(property(model, Namespaces.PROV_NS + "wasDerivedFrom"))
				.toList().size());
		assertEquals(3, model.listSubjectsWithProperty(
				property(model, Namespaces.EVAL_NS + "isMeasuredOnScale"), model.getResource(OQUARE_SCALE))
				.toList().size());
	}

	@Test
	void testShaclValidation() {
		ObservationInfoDTO observation = observation(NAMES_PER_CLASS_METRIC, NAMES_PER_CLASS_METRIC_SCALE, 0.0);
		observation.setAgentIRI(HURON_AGENT);
		observation.setAgentTypeIRI(Namespaces.PROV_NS + "SoftwareAgent");

		IssueInfoDTO issue = new IssueInfoDTO();
		issue.setIssueType(HURON_NS + "ClassWithNoNameIssue");
		issue.setMessage("The class does not have any name");
		issue.setHostAsset(observation.getFeatureOfInterestIRI());
		observation.setIssues(Arrays.asList(issue));

		Model model = createModel(observation);

		ValidationReport report = RDFValidator.validate(model, OQUO_HURON_IRI);
		if (!report.conforms()) {
			ShLib.printReport(report);
		}
		assertTrue(report.conforms());
	}

	// ---------------------------------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------------------------------

	private Model createModel(ObservationInfoDTO... observations) {
		ObservationsInfoDTO observationsInfo = new ObservationsInfoDTO();
		observationsInfo.setObservationsInfoDTO(Arrays.asList(observations));
		Model model = InstanceCreator.createObservationsAndApplyModel(observationsInfo, OQUO_HURON_IRI, DATA_NAMESPACE);
		assertNotNull(model);
		assertFalse(model.isEmpty());
		return model;
	}

	private ObservationInfoDTO observation(String metricIRI, String scaleIRI, double value) {
		ObservationInfoDTO observation = new ObservationInfoDTO();
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150");
		observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI());
		observation.setMetricUsedIRI(metricIRI);
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");
		observation.setScaleIRI(scaleIRI);
		observation.setScaleTypeIRI("https://purl.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setValue(Double.valueOf(value));
		return observation;
	}

	private Property property(Model model, String iri) {
		return model.getProperty(iri);
	}

	private Resource getResource(Resource subject, String propertyIRI) {
		Statement statement = subject.getProperty(subject.getModel().getProperty(propertyIRI));
		assertNotNull(statement, subject + " has no " + propertyIRI);
		return statement.getResource();
	}

	/** The quality value produced by the only evaluation of the model. */
	private Resource getOriginalQualityValue(Model model) {
		List<Resource> evaluations = model
				.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "Evaluation")).toList();
		assertEquals(1, evaluations.size());
		return getResource(evaluations.get(0), Namespaces.EVAL_NS + "producedQualityValue");
	}

	private List<Resource> getConvertedQualityValues(Model model, Resource original) {
		return model.listSubjectsWithProperty(property(model, Namespaces.PROV_NS + "wasDerivedFrom"), original)
				.toList();
	}

	private Resource getSingleConvertedQualityValue(Model model, Resource original) {
		List<Resource> converted = getConvertedQualityValues(model, original);
		assertEquals(1, converted.size());
		return converted.get(0);
	}

	private int countQualityValues(Model model) {
		return model.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "QualityValue"))
				.toList().size();
	}
}