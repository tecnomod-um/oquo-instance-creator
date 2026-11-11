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

	private String agentIRI;

	private String agentTypeIRI;

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

	public String getSourceDocumentIRI() {
		return sourceDocumentIRI;
	}

	public void setSourceDocumentIRI(String sourceDocumentIRI) {
		this.sourceDocumentIRI = sourceDocumentIRI;
	}

	public String getFeatureOfInterestIRI() {
		return featureOfInterestIRI;
	}

	public void setFeatureOfInterestIRI(String featureOfInterestIRI) {
		this.featureOfInterestIRI = featureOfInterestIRI;
	}

	public String getFeatureOfInterestTypeIRI() {
		return featureOfInterestTypeIRI;
	}

	public void setFeatureOfInterestTypeIRI(String featureOfInterestTypeIRI) {
		this.featureOfInterestTypeIRI = featureOfInterestTypeIRI;
	}

	public String getAgentIRI() {
		return agentIRI;
	}

	public void setAgentIRI(String agentIRI) {
		this.agentIRI = agentIRI;
	}

	public String getAgentTypeIRI() {
		return agentTypeIRI;
	}

	public void setAgentTypeIRI(String agentTypeIRI) {
		this.agentTypeIRI = agentTypeIRI;
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	public String getMetricUsedIRI() {
		return metricUsedIRI;
	}

	public void setMetricUsedIRI(String metricUsedIRI) {
		this.metricUsedIRI = metricUsedIRI;
	}

	public String getScaleIRI() {
		return scaleIRI;
	}

	public void setScaleIRI(String scaleIRI) {
		this.scaleIRI = scaleIRI;
	}

	public String getScaleTypeIRI() {
		return scaleTypeIRI;
	}

	public void setScaleTypeIRI(String scaleTypeIRI) {
		this.scaleTypeIRI = scaleTypeIRI;
	}

	public String getRankingFunctionIRI() {
		return rankingFunctionIRI;
	}

	public void setRankingFunctionIRI(String rankingFunctionIRI) {
		this.rankingFunctionIRI = rankingFunctionIRI;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public List<IssueInfoDTO> getIssues() {
		return issues;
	}

	public void setIssues(List<IssueInfoDTO> issues) {
		this.issues = issues;
	}

	public List<ConfigurationInfoDTO> getConfigurationDataList() {
		return configurationDataList;
	}

	public void setConfigurationDataList(List<ConfigurationInfoDTO> configurationDataList) {
		this.configurationDataList = configurationDataList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agentIRI, agentTypeIRI, agentVersion, configurationDataList, details, featureOfInterestIRI,
				featureOfInterestTypeIRI, issues, metricUsedIRI, rankingFunctionIRI, scaleIRI, scaleTypeIRI,
				sourceDocumentIRI, timestamp, value);
	}

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
