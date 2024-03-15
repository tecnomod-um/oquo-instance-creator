package es.um.dis.tecnomod.oquo.service;

import java.util.Calendar;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

public class InstanceCreator {

	private static final String HAS_RANKIG_FUNCTION = Namespaces.QM_NS + "hasRankigFunction";
	private static final String RANKING_FUNCTION = Namespaces.QM_NS + "RankingFunction";
	private static final String IS_MEASURED_ON_SCALE = Namespaces.EVALUATION_RESULT_NS + "isMeasuredOnScale";
	private static final String FOR_MEASURE = Namespaces.EVALUATION_RESULT_NS + "forMeasure";
	private static final String QUALITY_MEASURE = Namespaces.QM_NS + "QualityMeasure";
	private static final String PRODUCED_QUALITY_VALUE = Namespaces.EVALUATION_RESULT_NS + "producedQualityValue";
	private static final String OBTAINED_FROM = Namespaces.EVALUATION_RESULT_NS + "obtainedFrom";
	private static final String HAS_LITERAL_VALUE = Namespaces.EVALUATION_RESULT_NS + "hasLiteralValue";
	private static final String QUALITY_VALUE = Namespaces.EVALUATION_RESULT_NS + "QualityValue";
	private static final String INPUT_DATA = Namespaces.EVALUATION_RESULT_NS + "inputData";
	private static final String EVALUATION_DATA = Namespaces.EVALUATION_RESULT_NS + "EvaluationData";
	private static final String PERFORMED_ON = Namespaces.EVALUATION_RESULT_NS + "performedOn";
	private static final String IN_XSD_DATE_TIME = Namespaces.TIME_NS + "inXSDDateTime";
	private static final String INSTANT = Namespaces.TIME_NS + "Instant";
	private static final String EVALUATED_SUBJECT = Namespaces.EVALUATION_RESULT_NS + "evaluatedSubject";
	private static final String EVALUATION = Namespaces.EVALUATION_RESULT_NS + "Evaluation";
	private static final String EVALUATION_SUBJECT = Namespaces.EVALUATION_RESULT_NS + "EvaluationSubject";

	/**
	 * Include the triples to represent the observation information into the rdf
	 * model passed as arguments. The model follows the schema of Evaluation Result
	 * Ontology (http://vocab.linkeddata.es/eval/index.html).
	 * 
	 * @param rdfModel        The jena model in which the triples will be included.
	 * @param observationInfo The information about the observation.
	 * @see http://vocab.linkeddata.es/eval/index.html
	 */
	public static void createObservation(Model rdfModel, ObservationInfoDTO observationInfo) {
		String featureOfInterestIRI = observationInfo.getFeatureOfInterestIRI();
		String featureOfInterestTypeIRI = observationInfo.getFeatureOfInterestTypeIRI();
		String sourceDocumentIRI = observationInfo.getSourceDocumentIRI();
		String metricUsedIRI = observationInfo.getMetricUsedIRI();
		String scaleIRI = observationInfo.getScaleIRI();
		String scaleTypeIRI = observationInfo.getScaleTypeIRI();
		String rankingFunctionIRI = observationInfo.getRankingFunctionIRI();
		Object value = observationInfo.getValue();
		Calendar timestamp = observationInfo.getTimestamp();

		/* Evaluation and evaluationSubject */
		String evaluationIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource evaluationSubject = rdfModel.createResource(featureOfInterestIRI,
				rdfModel.createResource(EVALUATION_SUBJECT));
		Resource evaluation = rdfModel.createResource(evaluationIRI, rdfModel.createResource(EVALUATION));
		Property evaluatedSubject = rdfModel.createProperty(EVALUATED_SUBJECT);
		rdfModel.add(evaluation, evaluatedSubject, evaluationSubject);
		Resource featureOfInterestType = rdfModel.createResource(featureOfInterestTypeIRI);
		rdfModel.add(evaluationSubject, RDF.type, featureOfInterestType);

		/* Instant */
		String instantIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource instant = rdfModel.createResource(instantIRI, rdfModel.createResource(INSTANT));
		Property inTime = rdfModel.createProperty(IN_XSD_DATE_TIME);
		rdfModel.add(instant, inTime, rdfModel.createTypedLiteral(timestamp));

		/* Evaluation and instant */
		Property performedOn = rdfModel.createProperty(PERFORMED_ON);
		rdfModel.add(evaluation, performedOn, instant);

		/*
		 * Evaluation and EvaluationData. We used this to store the source ontology IRI
		 */
		if (sourceDocumentIRI != null) {
			Resource evaluationData = rdfModel.createResource(sourceDocumentIRI, OWL.Ontology);
			rdfModel.add(evaluationData, RDF.type, rdfModel.createResource(EVALUATION_DATA));
			Property inputData = rdfModel.createProperty(INPUT_DATA);
			rdfModel.add(evaluation, inputData, evaluationData);
		}

		/* Quality value */
		String qualityValueIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource qualityValue = rdfModel.createResource(qualityValueIRI, rdfModel.createResource(QUALITY_VALUE));
		Property hasLiteralValue = rdfModel.createProperty(HAS_LITERAL_VALUE);
		rdfModel.add(qualityValue, hasLiteralValue, rdfModel.createTypedLiteral(value));

		/* Quality value and evaluation */
		Property obtainedFrom = rdfModel.createProperty(OBTAINED_FROM);
		Property producedQualityValue = rdfModel.createProperty(PRODUCED_QUALITY_VALUE);
		rdfModel.add(qualityValue, obtainedFrom, evaluation);
		rdfModel.add(evaluation, producedQualityValue, qualityValue);

		/* Quality value and quality measure */
		Resource qualityMeasure = rdfModel.createResource(metricUsedIRI, rdfModel.createResource(QUALITY_MEASURE));
		Property forMeasure = rdfModel.createProperty(FOR_MEASURE);
		rdfModel.add(qualityValue, forMeasure, qualityMeasure);

		/* Measurement scale */
		Resource measurementScale = rdfModel.createResource(scaleIRI, rdfModel.createResource(scaleTypeIRI));

		/* Measurement scale and quality value */
		Property isMeasuredOnScale = rdfModel.createProperty(IS_MEASURED_ON_SCALE);
		rdfModel.add(qualityValue, isMeasuredOnScale, measurementScale);

		/* Ranking function */
		Resource rankingFunction = rdfModel.createResource(rankingFunctionIRI,
				rdfModel.createResource(RANKING_FUNCTION));

		/* Measurement scale and ranking function */
		Property hasRankingFunction = rdfModel.createProperty(HAS_RANKIG_FUNCTION);
		rdfModel.add(measurementScale, hasRankingFunction, rankingFunction);

	}

}
