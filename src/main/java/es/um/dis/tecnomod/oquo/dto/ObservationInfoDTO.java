package es.um.dis.tecnomod.oquo.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * The Class ObservationInfoDTO.
 */
public class ObservationInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -168082823892871235L;

	/** The source document IRI. */
	private String sourceDocumentIRI;

	/** The feature of interest IRI. */
	private String featureOfInterestIRI;

	/** The feature of interest type IRI. */
	private String featureOfInterestTypeIRI;

	/** The agent IRI. */
	private String agentIRI;

	/** The agent type IRI. */
	private String agentTypeIRI;

	/** The agent version. */
	private String agentVersion;

	/** The metric used IRI. */
	private String metricUsedIRI;

	/** The scale IRI. */
	private String scaleIRI;

	/** The scale type IRI. */
	private String scaleTypeIRI;

	/** The ranking function IRI. */
	private String rankingFunctionIRI;

	/** The value. */
	private Object value;

	/** The timestamp. */
	private Calendar timestamp;

	/** The details. */
	private List<String> details;

	/**  Optional issues to attach to the observation. */
	private List<IssueInfoDTO> issues;

	/**  Optional configuration parameters to store together with the evaluation. */
	private List<ConfigurationInfoDTO> configurationDataList;

	/**
	 * Gets the source document IRI.
	 *
	 * @return the source document IRI
	 */
	public String getSourceDocumentIRI() {
		return sourceDocumentIRI;
	}

	/**
	 * Sets the source document IRI.
	 *
	 * @param sourceDocumentIRI the new source document IRI
	 */
	public void setSourceDocumentIRI(String sourceDocumentIRI) {
		this.sourceDocumentIRI = sourceDocumentIRI;
	}

	/**
	 * Gets the feature of interest IRI.
	 *
	 * @return the feature of interest IRI
	 */
	public String getFeatureOfInterestIRI() {
		return featureOfInterestIRI;
	}

	/**
	 * Sets the feature of interest IRI.
	 *
	 * @param featureOfInterestIRI the new feature of interest IRI
	 */
	public void setFeatureOfInterestIRI(String featureOfInterestIRI) {
		this.featureOfInterestIRI = featureOfInterestIRI;
	}

	/**
	 * Gets the feature of interest type IRI.
	 *
	 * @return the feature of interest type IRI
	 */
	public String getFeatureOfInterestTypeIRI() {
		return featureOfInterestTypeIRI;
	}

	/**
	 * Sets the feature of interest type IRI.
	 *
	 * @param featureOfInterestTypeIRI the new feature of interest type IRI
	 */
	public void setFeatureOfInterestTypeIRI(String featureOfInterestTypeIRI) {
		this.featureOfInterestTypeIRI = featureOfInterestTypeIRI;
	}

	/**
	 * Gets the agent IRI.
	 *
	 * @return the agent IRI
	 */
	public String getAgentIRI() {
		return agentIRI;
	}

	/**
	 * Sets the agent IRI.
	 *
	 * @param agentIRI the new agent IRI
	 */
	public void setAgentIRI(String agentIRI) {
		this.agentIRI = agentIRI;
	}

	/**
	 * Gets the agent type IRI.
	 *
	 * @return the agent type IRI
	 */
	public String getAgentTypeIRI() {
		return agentTypeIRI;
	}

	/**
	 * Sets the agent type IRI.
	 *
	 * @param agentTypeIRI the new agent type IRI
	 */
	public void setAgentTypeIRI(String agentTypeIRI) {
		this.agentTypeIRI = agentTypeIRI;
	}

	/**
	 * Gets the agent version.
	 *
	 * @return the agent version
	 */
	public String getAgentVersion() {
		return agentVersion;
	}

	/**
	 * Sets the agent version.
	 *
	 * @param agentVersion the new agent version
	 */
	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	/**
	 * Gets the metric used IRI.
	 *
	 * @return the metric used IRI
	 */
	public String getMetricUsedIRI() {
		return metricUsedIRI;
	}

	/**
	 * Sets the metric used IRI.
	 *
	 * @param metricUsedIRI the new metric used IRI
	 */
	public void setMetricUsedIRI(String metricUsedIRI) {
		this.metricUsedIRI = metricUsedIRI;
	}

	/**
	 * Gets the scale IRI.
	 *
	 * @return the scale IRI
	 */
	public String getScaleIRI() {
		return scaleIRI;
	}

	/**
	 * Sets the scale IRI.
	 *
	 * @param scaleIRI the new scale IRI
	 */
	public void setScaleIRI(String scaleIRI) {
		this.scaleIRI = scaleIRI;
	}

	/**
	 * Gets the scale type IRI.
	 *
	 * @return the scale type IRI
	 */
	public String getScaleTypeIRI() {
		return scaleTypeIRI;
	}

	/**
	 * Sets the scale type IRI.
	 *
	 * @param scaleTypeIRI the new scale type IRI
	 */
	public void setScaleTypeIRI(String scaleTypeIRI) {
		this.scaleTypeIRI = scaleTypeIRI;
	}

	/**
	 * Gets the ranking function IRI.
	 *
	 * @return the ranking function IRI
	 */
	public String getRankingFunctionIRI() {
		return rankingFunctionIRI;
	}

	/**
	 * Sets the ranking function IRI.
	 *
	 * @param rankingFunctionIRI the new ranking function IRI
	 */
	public void setRankingFunctionIRI(String rankingFunctionIRI) {
		this.rankingFunctionIRI = rankingFunctionIRI;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public Calendar getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public List<String> getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(List<String> details) {
		this.details = details;
	}

	/**
	 * Gets the issues.
	 *
	 * @return the issues
	 */
	public List<IssueInfoDTO> getIssues() {
		return issues;
	}

	/**
	 * Sets the issues.
	 *
	 * @param issues the new issues
	 */
	public void setIssues(List<IssueInfoDTO> issues) {
		this.issues = issues;
	}

	/**
	 * Gets the configuration data list.
	 *
	 * @return the configuration data list
	 */
	public List<ConfigurationInfoDTO> getConfigurationDataList() {
		return configurationDataList;
	}

	/**
	 * Sets the configuration data list.
	 *
	 * @param configurationDataList the new configuration data list
	 */
	public void setConfigurationDataList(List<ConfigurationInfoDTO> configurationDataList) {
		this.configurationDataList = configurationDataList;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(agentIRI, agentTypeIRI, agentVersion, configurationDataList, details, featureOfInterestIRI,
				featureOfInterestTypeIRI, issues, metricUsedIRI, rankingFunctionIRI, scaleIRI, scaleTypeIRI,
				sourceDocumentIRI, timestamp, value);
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObservationInfoDTO other = (ObservationInfoDTO) obj;
		return Objects.equals(agentIRI, other.agentIRI) && Objects.equals(agentTypeIRI, other.agentTypeIRI)
				&& Objects.equals(agentVersion, other.agentVersion)
				&& Objects.equals(configurationDataList, other.configurationDataList)
				&& Objects.equals(details, other.details)
				&& Objects.equals(featureOfInterestIRI, other.featureOfInterestIRI)
				&& Objects.equals(featureOfInterestTypeIRI, other.featureOfInterestTypeIRI)
				&& Objects.equals(issues, other.issues) && Objects.equals(metricUsedIRI, other.metricUsedIRI)
				&& Objects.equals(rankingFunctionIRI, other.rankingFunctionIRI)
				&& Objects.equals(scaleIRI, other.scaleIRI) && Objects.equals(scaleTypeIRI, other.scaleTypeIRI)
				&& Objects.equals(sourceDocumentIRI, other.sourceDocumentIRI)
				&& Objects.equals(timestamp, other.timestamp) && Objects.equals(value, other.value);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObservationInfoDTO [sourceDocumentIRI=");
		builder.append(sourceDocumentIRI);
		builder.append(", featureOfInterestIRI=");
		builder.append(featureOfInterestIRI);
		builder.append(", featureOfInterestTypeIRI=");
		builder.append(featureOfInterestTypeIRI);
		builder.append(", agentIRI=");
		builder.append(agentIRI);
		builder.append(", agentTypeIRI=");
		builder.append(agentTypeIRI);
		builder.append(", agentVersion=");
		builder.append(agentVersion);
		builder.append(", metricUsedIRI=");
		builder.append(metricUsedIRI);
		builder.append(", scaleIRI=");
		builder.append(scaleIRI);
		builder.append(", scaleTypeIRI=");
		builder.append(scaleTypeIRI);
		builder.append(", rankingFunctionIRI=");
		builder.append(rankingFunctionIRI);
		builder.append(", value=");
		builder.append(value);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", details=");
		builder.append(details);
		builder.append(", issues=");
		builder.append(issues);
		builder.append(", configurationDataList=");
		builder.append(configurationDataList);
		builder.append("]");
		return builder.toString();
	}

}
