package es.um.dis.tecnomod.oquo.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class ConfigurationInfoDTO.
 */
public class ConfigurationInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1717024778372707164L;

	/** The serialized configuration in a plain text format. */
	private String serializedConfiguration;
	
	/** The configuration IRI. */
	private String configurationIRI;

	/**
	 * Instantiates a new configuration info DTO.
	 */
	public ConfigurationInfoDTO() {
		super();
	}

	/**
	 * Instantiates a new configuration info DTO.
	 *
	 * @param serializedConfiguration the serialized configuration
	 * @param configurationIRI the configuration IRI
	 */
	public ConfigurationInfoDTO(String serializedConfiguration, String configurationIRI) {
		super();
		this.serializedConfiguration = serializedConfiguration;
		this.configurationIRI = configurationIRI;
	}

	/**
	 * Gets the serialized configuration.
	 *
	 * @return the serialized configuration
	 */
	public String getSerializedConfiguration() {
		return serializedConfiguration;
	}

	/**
	 * Sets the serialized configuration.
	 *
	 * @param serializedConfiguration the new serialized configuration
	 */
	public void setSerializedConfiguration(String serializedConfiguration) {
		this.serializedConfiguration = serializedConfiguration;
	}

	/**
	 * Gets the configuration IRI.
	 *
	 * @return the configuration IRI
	 */
	public String getConfigurationIRI() {
		return configurationIRI;
	}

	/**
	 * Sets the configuration IRI.
	 *
	 * @param configurationIRI the new configuration IRI
	 */
	public void setConfigurationIRI(String configurationIRI) {
		this.configurationIRI = configurationIRI;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(configurationIRI, serializedConfiguration);
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
		ConfigurationInfoDTO other = (ConfigurationInfoDTO) obj;
		return Objects.equals(configurationIRI, other.configurationIRI)
				&& Objects.equals(serializedConfiguration, other.serializedConfiguration);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationInfoDTO [serializedConfiguration=");
		builder.append(serializedConfiguration);
		builder.append(", configurationIRI=");
		builder.append(configurationIRI);
		builder.append("]");
		return builder.toString();
	}

	
}
