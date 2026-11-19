package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationsInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

/**
 * Tests of chained scale conversions in InstanceCreator.createObservationsAndApplyModel, using the
 * test ontology src/test/resources/scale-conversions-chain-test.ttl (no network access needed).
 */
class InstanceCreatorChainedConversionsTest {

	private static final String TEST_ONTOLOGY = "/test_data/scale-conversions-chain-test.ttl";
	private static final String DATA_NAMESPACE = "https://example.org/oquo-test-data/";
	private static final String EX = "https://example.org/oquo-conversions-chain-test#";

	private static final String IS_ABOUT = "http://purl.obolibrary.org/obo/IAO_0000136";

	@Test
	void testChain() {
		// 0.5 -> five categories 3 -> 50 % -> pass
		Model model = createModel(observation("ChainRawScale", 0.5));
		Resource original = getOriginalQualityValue(model);

		Resource five = getSingleValueOnScale(model, "ChainFiveScale");
		assertEquals(original, derivedFrom(five));
		assertEquals(EX + "Five3", five.getPropertyResourceValue(model.getProperty(IS_ABOUT)).getURI());
		assertEquals(3, literal(five).getInt());

		Resource percentage = getSingleValueOnScale(model, "ChainPercentageScale");
		assertEquals(five, derivedFrom(percentage));
		assertEquals(50.0, literal(percentage).getDouble(), 1e-9);

		Resource passFail = getSingleValueOnScale(model, "ChainPassFailScale");
		assertEquals(percentage, derivedFrom(passFail));
		assertEquals(EX + "Pass", passFail.getPropertyResourceValue(model.getProperty(IS_ABOUT)).getURI());

		assertEquals(4, countQualityValues(model));
	}

	@Test
	void testChainWithAnotherValue() {
		// 0.1 -> five categories 1 -> 0 % -> fail
		Model model = createModel(observation("ChainRawScale", 0.1));

		assertEquals(EX + "Five1", getSingleValueOnScale(model, "ChainFiveScale")
				.getPropertyResourceValue(model.getProperty(IS_ABOUT)).getURI());
		assertEquals(0.0, literal(getSingleValueOnScale(model, "ChainPercentageScale")).getDouble(), 1e-9);
		assertEquals(EX + "Fail", getSingleValueOnScale(model, "ChainPassFailScale")
				.getPropertyResourceValue(model.getProperty(IS_ABOUT)).getURI());
	}

	@Test
	void testCycleIsCut() {
		// ChainPercentageScale -> ChainFiveScale closes a cycle: ChainFiveScale was already reached,
		// so no second value is created on it (getSingleValueOnScale checks there is only one)
		Model model = createModel(observation("ChainRawScale", 0.5));
		getSingleValueOnScale(model, "ChainFiveScale");
		assertEquals(4, countQualityValues(model));
	}

	@Test
	void testDiamond() {
		// raw 1 -> A: 2 and B: 3 -> target: only one value, either 2 + 1 or 3 + 1
		Model model = createModel(observation("DiamondRawScale", 1.0));
		Resource original = getOriginalQualityValue(model);

		Resource a = getSingleValueOnScale(model, "DiamondAScale");
		Resource b = getSingleValueOnScale(model, "DiamondBScale");
		assertEquals(original, derivedFrom(a));
		assertEquals(original, derivedFrom(b));
		assertEquals(2.0, literal(a).getDouble(), 1e-9);
		assertEquals(3.0, literal(b).getDouble(), 1e-9);

		Resource target = getSingleValueOnScale(model, "DiamondTargetScale");
		Resource source = derivedFrom(target);
		assertTrue(source.equals(a) || source.equals(b));
		assertEquals(literal(source).getDouble() + 1, literal(target).getDouble(), 1e-9);

		assertEquals(4, countQualityValues(model));
	}

	@Test
	void testSelfLoop() {
		// A conversion from a scale to itself is never applied
		Model model = createModel(observation("LoopScale", 1.0));
		assertEquals(1, countQualityValues(model));
	}

	@Test
	void testChainLinks() {
		Model model = createModel(observation("ChainRawScale", 0.5));
		Resource original = getOriginalQualityValue(model);
		Resource evaluation = original.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "obtainedFrom"));
		Resource measure = original.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "forMeasure"));

		// Every value of the chain belongs to the same evaluation and measure
		List<Resource> qualityValues = qualityValues(model);
		for (Resource qualityValue : qualityValues) {
			assertEquals(evaluation,
					qualityValue.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "obtainedFrom")));
			assertEquals(evaluation,
					qualityValue.getPropertyResourceValue(model.getProperty(Namespaces.PROV_NS + "wasGeneratedBy")));
			assertEquals(measure,
					qualityValue.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "forMeasure")));
			assertTrue(evaluation.hasProperty(model.getProperty(Namespaces.PROV_NS + "generated"), qualityValue));
		}

		// ... but the evaluation only produced the original one
		assertEquals(1, evaluation.listProperties(model.getProperty(Namespaces.EVAL_NS + "producedQualityValue"))
				.toList().size());
	}

	// ---------------------------------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------------------------------

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

	private Resource getOriginalQualityValue(Model model) {
		List<Resource> evaluations = model
				.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "Evaluation")).toList();
		assertEquals(1, evaluations.size());
		Resource original = evaluations.get(0)
				.getPropertyResourceValue(model.getProperty(Namespaces.EVAL_NS + "producedQualityValue"));
		assertNotNull(original);
		return original;
	}

	private Resource getSingleValueOnScale(Model model, String scale) {
		List<Resource> values = model.listSubjectsWithProperty(
				model.getProperty(Namespaces.EVAL_NS + "isMeasuredOnScale"), model.getResource(EX + scale)).toList();
		assertEquals(1, values.size(), "Quality values on " + scale);
		return values.get(0);
	}

	private Resource derivedFrom(Resource qualityValue) {
		Resource source = qualityValue
				.getPropertyResourceValue(qualityValue.getModel().getProperty(Namespaces.PROV_NS + "wasDerivedFrom"));
		assertNotNull(source, qualityValue + " is not derived from any quality value");
		return source;
	}

	private org.apache.jena.rdf.model.Literal literal(Resource qualityValue) {
		return qualityValue.getProperty(qualityValue.getModel().getProperty(Namespaces.EVAL_NS + "hasLiteralValue"))
				.getLiteral();
	}

	private List<Resource> qualityValues(Model model) {
		return model.listSubjectsWithProperty(RDF.type, model.getResource(Namespaces.EVAL_NS + "QualityValue"))
				.toList();
	}

	private int countQualityValues(Model model) {
		return qualityValues(model).size();
	}
}