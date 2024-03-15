package es.um.dis.tecnomod.oquo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.junit.jupiter.api.Test;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;

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
		
		
		// Add the observation into the jena model
		InstanceCreator.createObservation(model, observation);
		assertFalse(model.isEmpty());
		
		// See the rdf
		// You can use "TURTLE", "NTRIPLES", "TRIG", "RDFXML"...
		// You can use a FileOutputStream instead of System.out to write to a file.
		model.write(System.out, "TURTLE");
	}

}
