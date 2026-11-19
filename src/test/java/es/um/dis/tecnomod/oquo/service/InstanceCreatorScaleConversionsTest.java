package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationsInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

/**
 * Tests of the nine types of scale conversion applied by
 * InstanceCreator.createObservationsAndApplyModel, using the test ontology
 * src/test/resources/scale-conversions-test.ttl (no network access needed).
 *
 * The test ontology defines one input scale per type of conversion:
 * - cardinal input values are numbers;
 * - ordinal input values are given by the rank (om:rank), the label or the IRI of the category;
 * - nominal input values are given by the label or the IRI of the category.
 */
class InstanceCreatorScaleConversionsTest {

	private static final String TEST_ONTOLOGY = "/test_data/scale-conversions-test.ttl";
	private static final String DATA_NAMESPACE = "https://example.org/oquo-test-data/";
	private static final String EX = "https://example.org/oquo-conversions-test#";

	private static final String IS_ABOUT = "http://purl.obolibrary.org/obo/IAO_0000136";

	// ---------------------------------------------------------------------------------------------
	// Cardinal input
	// ---------------------------------------------------------------------------------------------

	@Test
	void testCardinalToCardinal() {
		// [0, 1] -> 100 * x
		assertCardinal(convert("CardinalForCardinalScale", 0.25), "PercentageScale", 25.0);
		assertCardinal(convert("CardinalForCardinalScale", 0.0), "PercentageScale", 0.0);
		assertCardinal(convert("CardinalForCardinalScale", 1.0), "PercentageScale", 100.0);  // closed upper bound
		// (1, INF) -> 100
		assertCardinal(convert("CardinalForCardinalScale", 3.0), "PercentageScale", 100.0);
		// outside every interval
		assertNull(convert("CardinalForCardinalScale", -1.0));
	}

	@Test
	void testCardinalToNominal() {
		assertCategory(convert("CardinalForNominalScale", 0.3), "PassFailScale", "Fail", "fail");
		assertCategory(convert("CardinalForNominalScale", 0.5), "PassFailScale", "Pass", "pass");  // closed lower bound
		assertCategory(convert("CardinalForNominalScale", 7.0), "PassFailScale", "Pass", "pass");
	}

	// ---------------------------------------------------------------------------------------------
	// Ordinal input
	// ---------------------------------------------------------------------------------------------

	@Test
	void testOrdinalToCardinal() {
		assertCardinal(convert("OrdinalForCardinalScale", 1), "PercentageScale", 0.0);
		assertCardinal(convert("OrdinalForCardinalScale", 2), "PercentageScale", 50.0);
		assertCardinal(convert("OrdinalForCardinalScale", 3), "PercentageScale", 100.0);
		// the input category given by its label or its IRI
		assertCardinal(convert("OrdinalForCardinalScale", "medium"), "PercentageScale", 50.0);
		assertCardinal(convert("OrdinalForCardinalScale", EX + "High"), "PercentageScale", 100.0);
		// no such category
		assertNull(convert("OrdinalForCardinalScale", 4));
	}

	@Test
	void testOrdinalToOrdinal() {
		assertCategory(convert("OrdinalForOrdinalScale", 1), "FiveScale", "Five1", 1);
		assertCategory(convert("OrdinalForOrdinalScale", 2), "FiveScale", "Five3", 3);
		assertCategory(convert("OrdinalForOrdinalScale", 3), "FiveScale", "Five5", 5);
	}

	@Test
	void testOrdinalToNominal() {
		assertCategory(convert("OrdinalForNominalScale", 1), "PassFailScale", "Fail", "fail");
		assertCategory(convert("OrdinalForNominalScale", 2), "PassFailScale", "Pass", "pass");
		assertCategory(convert("OrdinalForNominalScale", 3), "PassFailScale", "Pass", "pass");
	}

	// ---------------------------------------------------------------------------------------------
	// Nominal input
	// ---------------------------------------------------------------------------------------------

	@Test
	void testNominalToCardinal() {
		assertCardinal(convert("NominalForCardinalScale", "no"), "PercentageScale", 0.0);
		assertCardinal(convert("NominalForCardinalScale", "yes"), "PercentageScale", 100.0);
		assertCardinal(convert("NominalForCardinalScale", EX + "Yes"), "PercentageScale", 100.0);
		// no such category
		assertNull(convert("NominalForCardinalScale", "maybe"));
	}

	@Test
	void testNominalToOrdinal() {
		assertCategory(convert("NominalForOrdinalScale", "no"), "FiveScale", "Five1", 1);
		assertCategory(convert("NominalForOrdinalScale", "yes"), "FiveScale", "Five5", 5);
	}

	@Test
	void testNominalToNominal() {
		assertCategory(convert("NominalForNominalScale", "no"), "PassFailScale", "Fail", "fail");
		assertCategory(convert("NominalForNominalScale", "yes"), "PassFailScale", "Pass", "pass");
	}

	// ---------------------------------------------------------------------------------------------
	// Several observations at once
	// ---------------------------------------------------------------------------------------------

	@Test
	void testSeveralConversionTypesAtOnce() {
		Model model = createModel(
				observation("CardinalForCardinalScale", 0.25),
				observation("OrdinalForOrdinalScale", 2),
				observation("NominalForNominalScale", "yes"));

		assertEquals(3, model.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "Evaluation"))
				.toList().size());
		// one original and one converted quality value per observation
		assertEquals(6, model.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "QualityValue"))
				.toList().size());
		assertEquals(3, model.listSubjectsWithProperty(model.getProperty(Namespaces.PROV_NS + "wasDerivedFrom"))
				.toList().size());
	}

	// ---------------------------------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------------------------------

	/**
	 * Creates an observation with value on the input scale ex:inputScale, applies the test ontology
	 * and returns the quality value converted from it, or null if none was created.
	 */
	private Resource convert(String inputScale, Object value) {
		Model model = createModel(observation(inputScale, value));

		List<Resource> evaluations = model
				.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "Evaluation")).toList();
		assertEquals(1, evaluations.size());
		Resource original = evaluations.get(0)
				.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "producedQualityValue"));
		assertNotNull(original);

		List<Resource> converted = model
				.listSubjectsWithProperty(model.getProperty(Namespaces.PROV_NS + "wasDerivedFrom"), original).toList();
		assertTrue(converted.size() <= 1, "More than one converted quality value: " + converted);
		return converted.isEmpty() ? null : converted.get(0);
	}

	private Model createModel(ObservationInfoDTO... observations) {
		ObservationsInfoDTO observationsInfo = new ObservationsInfoDTO();
		observationsInfo.setObservationsInfoDTO(Arrays.asList(observations));
		String ontology = getClass().getResource(TEST_ONTOLOGY).toString();
		return InstanceCreator.createObservationsAndApplyModel(observationsInfo, ontology, DATA_NAMESPACE);
	}

	private ObservationInfoDTO observation(String inputScale, Object value) {
		ObservationInfoDTO observation = new ObservationInfoDTO();
		observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");
		observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150");
		observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI());
		observation.setMetricUsedIRI(EX + "TestMetric");
		observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");
		observation.setScaleIRI(EX + inputScale);
		observation.setScaleTypeIRI("https://purl.org/oquo#RawScale");
		observation.setTimestamp(Calendar.getInstance());
		observation.setValue(value);
		return observation;
	}

	/** The converted value is the number expected, on the output scale, without category. */
	private void assertCardinal(Resource converted, String outputScale, double expected) {
		assertNotNull(converted, "No converted quality value");
		Model model = converted.getModel();
		assertEquals(EX + outputScale,
				converted.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "isMeasuredOnScale")).getURI());
		assertEquals(expected,
				converted.getProperty(model.getProperty(Namespaces.EVAL_NS + "hasLiteralValue")).getDouble(), 1e-9);
		assertNull(converted.getProperty(model.getProperty(IS_ABOUT)));
	}

	/**
	 * The converted value is the category expected, on the output scale, with its code as literal:
	 * its rank (an Integer) for ordinal scales, or its label (a String) for nominal scales.
	 */
	private void assertCategory(Resource converted, String outputScale, String category, Object code) {
		assertNotNull(converted, "No converted quality value");
		Model model = converted.getModel();
		assertEquals(EX + outputScale,
				converted.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "isMeasuredOnScale")).getURI());
		assertEquals(EX + category, converted.getPropertyResourceValue(model.getProperty(IS_ABOUT)).getURI());

		Statement literal = converted.getProperty(model.getProperty(Namespaces.EVAL_NS + "hasLiteralValue"));
		if (code instanceof Integer) {
			assertEquals(((Integer) code).intValue(), literal.getInt());
		} else {
			assertEquals(code, literal.getString());
		}
	}
}