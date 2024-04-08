package es.um.dis.tecnomod.oquo.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class IssueInfoDTO.
 */
public class IssueInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2248814567027808703L;
	
	/** The issue type. */
	private String issueType;
	
	/** The message. */
	private String message;
	
	/**
	 * Instantiates a new issue info DTO.
	 */
	public IssueInfoDTO(){}
	
	/**
	 * Instantiates a new issue info DTO.
	 *
	 * @param observation the observation
	 * @param issueType the issue type
	 * @param message the message
	 */
	public IssueInfoDTO(String issueType, String message) {
		super();
		this.issueType = issueType;
		this.message = message;
	}


	/**
	 * Gets the issue type.
	 *
	 * @return the issue type
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * Sets the issue type.
	 *
	 * @param issueType the new issue type
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		return Objects.hash(issueType, message);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IssueInfoDTO other = (IssueInfoDTO) obj;
		return Objects.equals(issueType, other.issueType) && Objects.equals(message, other.message);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IssueInfoDTO [issueType=");
		builder.append(issueType);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}

	

}
