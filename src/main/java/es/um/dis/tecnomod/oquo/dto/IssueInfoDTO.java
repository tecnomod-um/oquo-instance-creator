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
	
	/** The causative asset. */
	private String causativeAsset;
	
	/** The host asset. */
	private String hostAsset;
	
	/**
	 * Instantiates a new issue info DTO.
	 */
	public IssueInfoDTO(){}
	
	/**
	 * Instantiates a new issue info DTO.
	 *
	 * @param issueType the issue type
	 * @param message the message
	 */
	public IssueInfoDTO(String issueType, String message) {
		this(issueType, null, null, message);
	}

	
	/**
	 * Instantiates a new issue info DTO.
	 *
	 * @param issueType the issue type
	 * @param message the message
	 * @param hostAsset the host asset
	 */
	public IssueInfoDTO(String issueType, String hostAsset, String message) {
		this(issueType, hostAsset, null, message);
	}

	/**
	 * Instantiates a new issue info DTO.
	 *
	 * @param issueType the issue type
	 * @param message the message
	 * @param causativeAsset the causative asset
	 * @param hostAsset the host asset
	 */
	public IssueInfoDTO(String issueType, String hostAsset, String causativeAsset, String message) {
		super();
		this.issueType = issueType;
		this.message = message;
		this.causativeAsset = causativeAsset;
		this.hostAsset = hostAsset;
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

	/**
	 * Gets the causative asset.
	 *
	 * @return the causative asset
	 */
	public String getCausativeAsset() {
		return causativeAsset;
	}

	/**
	 * Sets the causative asset.
	 *
	 * @param causativeAsset the new causative asset
	 */
	public void setCausativeAsset(String causativeAsset) {
		this.causativeAsset = causativeAsset;
	}

	/**
	 * Gets the host asset.
	 *
	 * @return the host asset
	 */
	public String getHostAsset() {
		return hostAsset;
	}

	/**
	 * Sets the host asset.
	 *
	 * @param hostAsset the new host asset
	 */
	public void setHostAsset(String hostAsset) {
		this.hostAsset = hostAsset;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(causativeAsset, hostAsset, issueType, message);
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
		IssueInfoDTO other = (IssueInfoDTO) obj;
		return Objects.equals(causativeAsset, other.causativeAsset) && Objects.equals(hostAsset, other.hostAsset)
				&& Objects.equals(issueType, other.issueType) && Objects.equals(message, other.message);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IssueInfoDTO [issueType=");
		builder.append(issueType);
		builder.append(", message=");
		builder.append(message);
		builder.append(", causativeAsset=");
		builder.append(causativeAsset);
		builder.append(", hostAsset=");
		builder.append(hostAsset);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
