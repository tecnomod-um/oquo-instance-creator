package es.um.dis.tecnomod.oquo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * The Class ObservationsInfoDTO.
 */
public class ObservationsInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9198897305792648856L;

	/** The observations info DTO. */
	private List<ObservationInfoDTO> observationsInfoDTO;

	/**
	 * Gets the observations info DTO.
	 *
	 * @return the observations info DTO
	 */
	public List<ObservationInfoDTO> getObservationsInfoDTO() {
		return observationsInfoDTO;
	}

	/**
	 * Sets the observations info DTO.
	 *
	 * @param observationsInfoDTO the new observations info DTO
	 */
	public void setObservationsInfoDTO(List<ObservationInfoDTO> observationsInfoDTO) {
		this.observationsInfoDTO = observationsInfoDTO;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(observationsInfoDTO);
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
		ObservationsInfoDTO other = (ObservationsInfoDTO) obj;
		return Objects.equals(observationsInfoDTO, other.observationsInfoDTO);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObservationsInfoDTO [observationsInfoDTO=");
		builder.append(observationsInfoDTO);
		builder.append("]");
		return builder.toString();
	}


}
