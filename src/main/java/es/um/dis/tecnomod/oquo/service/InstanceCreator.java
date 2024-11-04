package es.um.dis.tecnomod.oquo.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import es.um.dis.tecnomod.oquo.dto.ConfigurationInfoDTO;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

public class InstanceCreator {

	/* Quality model and evaluation result entities */
	private static final String HAS_RANKIG_FUNCTION = Namespaces.QM_NS + "hasRankigFunction";
	private static final String RANKING_FUNCTION = Namespaces.QM_NS + "RankingFunction";
	private static final String IS_MEASURED_ON_SCALE = Namespaces.RES_NS + "isMeasuredOnScale";
	private static final String FOR_MEASURE = Namespaces.RES_NS + "forMeasure";
	//private static final String QUALITY_MEASURE = Namespaces.QM_NS + "QualityMeasure";
	private static final String PRODUCED_QUALITY_VALUE = Namespaces.RES_NS + "producedQualityValue";
	private static final String OBTAINED_FROM = Namespaces.RES_NS + "obtainedFrom";
	private static final String HAS_LITERAL_VALUE = Namespaces.RES_NS + "hasLiteralValue";
	private static final String QUALITY_VALUE = Namespaces.RES_NS + "QualityValue";
	private static final String INPUT_DATA = Namespaces.RES_NS + "inputData";
	private static final String EVALUATION_DATA = Namespaces.RES_NS + "EvaluationData";
	private static final String IN_XSD_DATE_TIME = Namespaces.TIME_NS + "inXSDDateTime";
	private static final String INSTANT = Namespaces.TIME_NS + "Instant";
	private static final String EVALUATED_SUBJECT = Namespaces.RES_NS + "evaluatedSubject";
	private static final String EVALUATION = Namespaces.RES_NS + "Evaluation";
	private static final String EVALUATION_SUBJECT = Namespaces.RES_NS + "EvaluationSubject";
	
	/* OBOE entities */
	private static final String ENTITY = Namespaces.OBOE_NS + "Entity";
	private static final String OBSERVATION = Namespaces.OBOE_NS + "Observation";
	private static final String MEASUREMENT = Namespaces.OBOE_NS + "Measurement";
	private static final String MEASUREMENT_VALUE = Namespaces.OBOE_NS + "MeasuredValue";
	
	private static final String HAS_MEASUREMENT = Namespaces.OBOE_NS + "hasMeasurement";
	private static final String HAS_VALUE = Namespaces.OBOE_NS + "hasValue";
	private static final String MEASUREMENT_FOR = Namespaces.OBOE_NS + "measurementFor";
	private static final String OF_ENTITY = Namespaces.OBOE_NS + "ofEntity";
	
	/* IPO entities */
	private static final String ASSET = Namespaces.IPO_NS + "Asset";
	private static final String SYMPTOM = Namespaces.IPO_NS + "Symptom";
	
	private static final String HAS_HOST_ASSET = Namespaces.IPO_NS + "hasHostAsset";
	private static final String HOST_ASSET_OF = Namespaces.IPO_NS + "hostAssetOf";
	private static final String HAS_CAUSATIVE_ASSET = Namespaces.IPO_NS + "hasCausativeAsset";
	private static final String CAUSATIVE_ASSET_OF = Namespaces.IPO_NS + "causativeAssetOf";
	private static final String INDICATES = Namespaces.IPO_NS + "indicates";
	private static final String INDICATED_BY = Namespaces.IPO_NS + "indicatedBy";
	
	/* OQUO entities */
	private static final String HAS_DETECTED_ISSUE = Namespaces.OQUO_NS + "hasDetectedIssue";
	private static final String EVALUATION_INPUT_DATA = Namespaces.OQUO_NS + "EvaluationInputData";
	private static final String EVALUATION_CONFIGURATION_DATA = Namespaces.OQUO_NS + "EvaluationConfigurationData";
	private static final String HAS_OBSERVATION = Namespaces.OQUO_NS + "hasObservation";
	
	/* Commons Text Datatype Ontology entities */
	private static final String HAS_TEXT_VALUE = Namespaces.CMNSTXT_NS + "hasTextValue";
	
	/* Time ontology */
	private static final String HAS_TIME = Namespaces.TIME_NS + "hasTime";
	

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
		List<String> details = observationInfo.getDetails();
		Object value = observationInfo.getValue();
		Calendar timestamp = observationInfo.getTimestamp();
		
		/* Evaluation result triples */

		/* Evaluation and evaluationSubject */
		String evaluationIRI = Namespaces.OQUO_NS + "evaluation-" + UUID.randomUUID().toString();
		Resource evaluationSubject = rdfModel.createResource(featureOfInterestIRI,
				rdfModel.createResource(EVALUATION_SUBJECT));
		Resource evaluation = rdfModel.createResource(evaluationIRI, rdfModel.createResource(EVALUATION));
		Property evaluatedSubject = rdfModel.createProperty(EVALUATED_SUBJECT);
		rdfModel.add(evaluation, evaluatedSubject, evaluationSubject);
		Resource featureOfInterestType = rdfModel.createResource(featureOfInterestTypeIRI);
		rdfModel.add(evaluationSubject, RDF.type, featureOfInterestType);

		
		
		/* Instant */
		String instantIRI = Namespaces.OQUO_NS + "instant-" + UUID.randomUUID().toString();
		Resource instant = rdfModel.createResource(instantIRI, rdfModel.createResource(INSTANT));
		Property inXSDDateTime = rdfModel.createProperty(IN_XSD_DATE_TIME);
		rdfModel.add(instant, inXSDDateTime, rdfModel.createTypedLiteral(timestamp));
		
		/* Evaluation and instant */
		Property performedOn = rdfModel.createProperty(HAS_TIME);
		if (!rdfModel.contains(evaluation, performedOn)) {
			rdfModel.add(evaluation, performedOn, instant);
		}
		

		/*
		 * Evaluation and EvaluationInputData. We used this to store the source ontology IRI
		 */
		Property inputData = rdfModel.createProperty(INPUT_DATA);
		if (sourceDocumentIRI != null) {
			Resource evaluationInputData = rdfModel.createResource(sourceDocumentIRI, OWL.Ontology);
			rdfModel.add(evaluationInputData, RDF.type, rdfModel.createResource(EVALUATION_DATA));
			rdfModel.add(evaluationInputData, RDF.type, rdfModel.createResource(EVALUATION_INPUT_DATA));
			rdfModel.add(evaluation, inputData, evaluationInputData);
		}
		
		/*
		 * Evaluation and EvaluationConfigurationData. We used this to store the configuration used in this evaluation
		 */
		if (observationInfo.getConfigurationDataList() != null) {
			for (ConfigurationInfoDTO configurationInfoDTO : observationInfo.getConfigurationDataList()) {
				Resource evaluationConfigurationData = rdfModel.createResource(configurationInfoDTO.getConfigurationIRI(), rdfModel.createResource(EVALUATION_DATA));
				rdfModel.add(evaluationConfigurationData, RDF.type, rdfModel.createResource(EVALUATION_CONFIGURATION_DATA));
				rdfModel.addLiteral(evaluationConfigurationData, rdfModel.createProperty(HAS_TEXT_VALUE), rdfModel.createLiteral(configurationInfoDTO.getSerializedConfiguration()));
				rdfModel.add(evaluation, inputData,evaluationConfigurationData );
			}
		}
		

		/* Quality value */
		String qualityValueIRI = Namespaces.OQUO_NS + "qualityValue-" + UUID.randomUUID().toString();
		Resource qualityValue = rdfModel.createResource(qualityValueIRI, rdfModel.createResource(QUALITY_VALUE));
		Property hasLiteralValue = rdfModel.createProperty(HAS_LITERAL_VALUE);
		rdfModel.add(qualityValue, hasLiteralValue, rdfModel.createTypedLiteral(value));
		
		/* Quality value and comments (details) */
		if (details != null && !details.isEmpty()) {
			for (String detail : details) {
				rdfModel.add(qualityValue, RDFS.comment, rdfModel.createTypedLiteral(detail));
			}
		}

		/* Quality value and evaluation */
		Property obtainedFrom = rdfModel.createProperty(OBTAINED_FROM);
		Property producedQualityValue = rdfModel.createProperty(PRODUCED_QUALITY_VALUE);
		rdfModel.add(qualityValue, obtainedFrom, evaluation);
		rdfModel.add(evaluation, producedQualityValue, qualityValue);

		/* Quality value and quality measure */
		String qualityMeasureIRI = Namespaces.OQUO_NS + "metric-" + UUID.randomUUID().toString();
		Resource qualityMeasure = rdfModel.createResource(qualityMeasureIRI, rdfModel.createResource(metricUsedIRI));
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
		
		/* QualityMeasure and measurement scale */
		Property hasScale = rdfModel.createProperty(Namespaces.QM_NS + "hasScale");
		rdfModel.add(qualityMeasure, hasScale, measurementScale);
		
		/* OBOE triples */
		/* Entity */
		rdfModel.add(evaluationSubject, RDF.type, rdfModel.createResource(ENTITY));
		
		/* Measurement value */
		rdfModel.add(qualityValue, RDF.type, rdfModel.createResource(MEASUREMENT_VALUE));
		
		/* Observation */
		String observationIRI = Namespaces.OQUO_NS + "observation-" + UUID.randomUUID().toString();
		Resource observation = rdfModel.createResource(observationIRI, rdfModel.createResource(OBSERVATION));
		rdfModel.add(observation, rdfModel.createProperty(OF_ENTITY), evaluationSubject);
		rdfModel.add(evaluation, rdfModel.createProperty(HAS_OBSERVATION), observation);
		rdfModel.add(observation, performedOn, instant);
		
		/* Measurement */
		rdfModel.add(qualityMeasure, RDF.type, rdfModel.createResource(MEASUREMENT));
		rdfModel.add(qualityMeasure, rdfModel.createProperty(MEASUREMENT_FOR), observation);
		rdfModel.add(observation, rdfModel.createProperty(HAS_MEASUREMENT), qualityMeasure);
		rdfModel.add(qualityMeasure, rdfModel.createProperty(HAS_VALUE), qualityValue);
		
		
		/* IPO triples (issues)*/
		
		List<IssueInfoDTO> issuesInfoDTO = observationInfo.getIssues();
		if (issuesInfoDTO != null && !issuesInfoDTO.isEmpty()) {
			rdfModel.add(observation, RDF.type, rdfModel.createResource(SYMPTOM));
			for (IssueInfoDTO issueInfoDTO : issuesInfoDTO) {
				Resource hostAsset = rdfModel.createResource(issueInfoDTO.getHostAsset(), rdfModel.createResource(ASSET));
				/* Issue */
				String issueIRI = Namespaces.OQUO_NS + "issue-" + UUID.randomUUID().toString();
				String message = issueInfoDTO.getMessage();
				Resource issue = rdfModel.createResource(issueIRI, rdfModel.createResource(issueInfoDTO.getIssueType()));
				
				/* Issue rdfs:comment message*/
				rdfModel.add(issue, RDFS.comment, message);
				
				/* Issue hasHostAsset Asset */
				rdfModel.add(issue, rdfModel.createProperty(HAS_HOST_ASSET), hostAsset);
				
				/* Asset hostAssetOf Issue */
				rdfModel.add(hostAsset, rdfModel.createProperty(HOST_ASSET_OF), issue);
				
				/* Evaluation hasDetectedIssue Issue */
				rdfModel.add(evaluation, rdfModel.createProperty(HAS_DETECTED_ISSUE), issue);
				
				/* observation indicates Issue */
				rdfModel.add(observation, rdfModel.createProperty(INDICATES), issue);
				
				/* issue indicated by observation */
				rdfModel.add(issue, rdfModel.createProperty(INDICATED_BY), observation);
				
				/* If there is causative asset... */
				if (issueInfoDTO.getCausativeAsset() != null && !issueInfoDTO.getCausativeAsset().isEmpty()) {
					Resource causativeAsset = rdfModel.createResource(issueInfoDTO.getCausativeAsset(), rdfModel.createResource(ASSET));
					
					/* Issue hasCausativeAsset Asset */
					rdfModel.add(issue, rdfModel.createProperty(HAS_CAUSATIVE_ASSET), causativeAsset);
					
					/* Asset causativeAssetOf Issue */
					rdfModel.add(causativeAsset, rdfModel.createProperty(CAUSATIVE_ASSET_OF), issue);
				}
			}
		}

	}
	

	
	/**
	 * Generates the IRI for an evaluation instance. This IRI depends on the feature of interest and the ontology version IRI
	 * @param observationInfo The observation info dto object.
	 * @return The IRI to use for the evaluation instance.
	 */
	public static String getEvaluationIRI(ObservationInfoDTO observationInfo) {
		String str = observationInfo.getFeatureOfInterestIRI() + observationInfo.getSourceDocumentIRI();
		UUID id = UUID.nameUUIDFromBytes(str.getBytes());
		String evaluationIRI = Namespaces.OQUO_NS + "evaluation-" + id.toString();
		return evaluationIRI;
	}

}
