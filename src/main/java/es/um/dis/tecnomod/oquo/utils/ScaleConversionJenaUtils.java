package es.um.dis.tecnomod.oquo.utils;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;


public class ScaleConversionJenaUtils {

	private static final String ORDINAL_TO_ORDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "OrdinalToOrdinalScaleConversion";
	private static final String ORDINAL_TO_NOMINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "OrdinalToNominalScaleConversion";
	private static final String ORDINAL_TO_CARDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "OrdinalToCardinalScaleConversion";
	private static final String NOMINAL_TO_ORDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "NominalToOrdinalScaleConversion";
	private static final String NOMINAL_TO_NOMINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "NominalToNominalScaleConversion";
	private static final String NOMINAL_TO_CARDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "NominalToCardinalScaleConversion";
	private static final String CARDINAL_TO_ORDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "CardinalToOrdinalScaleConversion";
	private static final String CARDINAL_TO_NOMINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "CardinalToNominalScaleConversion";
	private static final String CARDINAL_TO_CARDINAL_SCALE_CONVERSION = Namespaces.OQUO_NS + "CardinalToCardinalScaleConversion";
	private static final String HAS_SCALE_POINT_MAPPING = Namespaces.OQUO_NS + "hasScalePointMapping";
	private static final String HAS_OUTPUT_SCALE = Namespaces.OQUO_NS + "hasOutputScale";
	private static final String HAS_INPUT_CARDINAL_INTERVAL = Namespaces.OQUO_NS + "hasInputCardinalInterval";
	private static final String HAS_INPUT_SCALE_CATEGORY = Namespaces.OQUO_NS + "hasInputScaleCategory";
	private static final String HAS_OUTPUT_SCALE_CATEGORY = Namespaces.OQUO_NS + "hasOutputScaleCategory";
	private static final String HAS_OUTPUT_CARDINAL_VALUE = Namespaces.OQUO_NS + "hasOutputCardinalValue";
	private static final String HAS_SLOPE = Namespaces.OQUO_NS + "hasSlope";
	private static final String HAS_INTERCEPT = Namespaces.OQUO_NS + "hasIntercept";
	private static final String HAS_INTERVAL_LOWER_VALUE = Namespaces.OQUO_NS + "hasIntervalLowerValue";
	private static final String HAS_INTERVAL_UPPER_VALUE = Namespaces.OQUO_NS + "hasIntervalUpperValue";
	private static final String HAS_INTERVAL_LOWER_BOUND_TYPE = Namespaces.OQUO_NS + "hasIntervalLowerBoundType";
	private static final String HAS_INTERVAL_UPPER_BOUND_TYPE = Namespaces.OQUO_NS + "hasIntervalUpperBoundType";
	private static final String OPEN_INTERVAL_BOUND = Namespaces.OQUO_NS + "OpenIntervalBound";

	private static final String HAS_LITERAL_VALUE = Namespaces.EVAL_NS + "hasLiteralValue";
	private static final String IS_MEASURED_ON_SCALE = Namespaces.EVAL_NS + "isMeasuredOnScale";
	private static final String FOR_MEASURE = Namespaces.EVAL_NS + "forMeasure";
	private static final String OBTAINED_FROM = Namespaces.EVAL_NS + "obtainedFrom";

	private static final String PROV_GENERATED = Namespaces.PROV_NS + "generated";
	private static final String PROV_WAS_GENERATED_BY = Namespaces.PROV_NS + "wasGeneratedBy";
	private static final String PROV_WAS_ATTRIBUTED_TO = Namespaces.PROV_NS + "wasAttributedTo";
	private static final String PROV_WAS_DERIVED_FROM = Namespaces.PROV_NS + "wasDerivedFrom";

	private static final String IS_ABOUT = Namespaces.OBO_NS + "IAO_0000136";
	private static final String OM_RANK = "http://www.wurvoc.org/vocabularies/om-1.8/rank";

	/** Value of a converted quality value: its literal and, for ordinal and nominal scales, its category. */
	private static final class ConvertedValue {
		final Literal literal;
		final Resource category;

		ConvertedValue(Literal literal, Resource category) {
			this.literal = literal;
			this.category = category;
		}
	}

	/**
	 * Applies scaleConversion to qualityValue: creates a new quality value, measured on the output
	 * scale of the conversion, with the value given by the scale point mapping that matches the
	 * value of qualityValue. Nothing is created if no mapping matches.
	 * @param rdfModel model with the observations and the ontology that defines the conversion
	 * @param qualityValue quality value measured on the input scale of the conversion
	 * @param scaleConversion conversion to apply
	 * @param oquoDataNamespace namespace of the new quality value
	 */
	public static void applyScaleConversion(Model rdfModel, Resource qualityValue, Resource scaleConversion,
			String oquoDataNamespace) {
		Resource outputScale = scaleConversion.getPropertyResourceValue(rdfModel.getProperty(HAS_OUTPUT_SCALE));
		List<Resource> scalePointMappings = scaleConversion
				.listProperties(rdfModel.getProperty(HAS_SCALE_POINT_MAPPING))
				.mapWith(Statement::getResource)
				.toList();

		ConvertedValue convertedValue;
		if (JenaUtils.isA(scaleConversion, CARDINAL_TO_CARDINAL_SCALE_CONVERSION)) {
			convertedValue = convertCardinalToCardinal(rdfModel, qualityValue, scalePointMappings);
		} else if (JenaUtils.isA(scaleConversion, CARDINAL_TO_NOMINAL_SCALE_CONVERSION)
				|| JenaUtils.isA(scaleConversion, CARDINAL_TO_ORDINAL_SCALE_CONVERSION)) {
			convertedValue = convertCardinalToCategory(rdfModel, qualityValue, scalePointMappings);
		} else if (JenaUtils.isA(scaleConversion, NOMINAL_TO_CARDINAL_SCALE_CONVERSION)
				|| JenaUtils.isA(scaleConversion, ORDINAL_TO_CARDINAL_SCALE_CONVERSION)) {
			convertedValue = convertCategoryToCardinal(rdfModel, qualityValue, scalePointMappings);
		} else if (JenaUtils.isA(scaleConversion, NOMINAL_TO_NOMINAL_SCALE_CONVERSION)
				|| JenaUtils.isA(scaleConversion, NOMINAL_TO_ORDINAL_SCALE_CONVERSION)
				|| JenaUtils.isA(scaleConversion, ORDINAL_TO_NOMINAL_SCALE_CONVERSION)
				|| JenaUtils.isA(scaleConversion, ORDINAL_TO_ORDINAL_SCALE_CONVERSION)) {
			convertedValue = convertCategoryToCategory(rdfModel, qualityValue, scalePointMappings);
		} else {
			throw new IllegalArgumentException("Unknown type of scale conversion: " + scaleConversion);
		}

		if (convertedValue != null) {
			createConvertedQualityValue(rdfModel, qualityValue, outputScale, convertedValue, oquoDataNamespace);
		}
	}

	/**
	 * Cardinal to cardinal: the mapping whose input interval contains the value gives the output
	 * value as slope * value + intercept. A mapping without input interval applies to any value.
	 */
	private static ConvertedValue convertCardinalToCardinal(Model rdfModel, Resource qualityValue,
			List<Resource> scalePointMappings) {
		Double value = getCardinalValue(rdfModel, qualityValue);
		if (value == null) {
			return null;
		}
		Resource mapping = findMappingByInterval(rdfModel, scalePointMappings, value);
		if (mapping == null) {
			return null;
		}
		double slope = getDouble(mapping.getProperty(rdfModel.getProperty(HAS_SLOPE)), 1.0);
		double intercept = getDouble(mapping.getProperty(rdfModel.getProperty(HAS_INTERCEPT)), 0.0);
		return new ConvertedValue(rdfModel.createTypedLiteral(slope * value + intercept), null);
	}

	/**
	 * Cardinal to ordinal or nominal: the mapping whose input interval contains the value gives the
	 * output category.
	 */
	private static ConvertedValue convertCardinalToCategory(Model rdfModel, Resource qualityValue,
			List<Resource> scalePointMappings) {
		Double value = getCardinalValue(rdfModel, qualityValue);
		if (value == null) {
			return null;
		}
		Resource mapping = findMappingByInterval(rdfModel, scalePointMappings, value);
		if (mapping == null) {
			return null;
		}
		return categoryValue(rdfModel,
				mapping.getPropertyResourceValue(rdfModel.getProperty(HAS_OUTPUT_SCALE_CATEGORY)));
	}

	/**
	 * Ordinal or nominal to cardinal: the mapping of the input category gives the output value.
	 */
	private static ConvertedValue convertCategoryToCardinal(Model rdfModel, Resource qualityValue,
			List<Resource> scalePointMappings) {
		Resource mapping = findMappingByCategory(rdfModel, qualityValue, scalePointMappings);
		if (mapping == null) {
			return null;
		}
		Statement output = mapping.getProperty(rdfModel.getProperty(HAS_OUTPUT_CARDINAL_VALUE));
		if (output == null) {
			return null;
		}
		return new ConvertedValue(rdfModel.createTypedLiteral(toDouble(output.getLiteral())), null);
	}

	/**
	 * Ordinal or nominal to ordinal or nominal: the mapping of the input category gives the output
	 * category.
	 */
	private static ConvertedValue convertCategoryToCategory(Model rdfModel, Resource qualityValue,
			List<Resource> scalePointMappings) {
		Resource mapping = findMappingByCategory(rdfModel, qualityValue, scalePointMappings);
		if (mapping == null) {
			return null;
		}
		return categoryValue(rdfModel,
				mapping.getPropertyResourceValue(rdfModel.getProperty(HAS_OUTPUT_SCALE_CATEGORY)));
	}

	/**
	 * Creates the converted quality value, measured on outputScale, and links it with the same
	 * measure, evaluation and agents as the quality value it is derived from.
	 */
	private static Resource createConvertedQualityValue(Model rdfModel, Resource qualityValue,
			Resource outputScale, ConvertedValue convertedValue, String oquoDataNamespace) {
		Resource converted = rdfModel.createResource(oquoDataNamespace + "qualityValue-" + UUID.randomUUID());

		// Same types as the original quality value (eval:QualityValue, oboe:MeasuredValue, prov:Entity)
		JenaUtils.copyProperty(rdfModel, qualityValue, converted, RDF.type.getURI());

		converted.addProperty(rdfModel.getProperty(IS_MEASURED_ON_SCALE), outputScale);
		converted.addProperty(rdfModel.getProperty(HAS_LITERAL_VALUE), convertedValue.literal);
		if (convertedValue.category != null) {
			converted.addProperty(rdfModel.getProperty(IS_ABOUT), convertedValue.category);
		}

		// Measure, evaluation and agents
		JenaUtils.copyProperty(rdfModel, qualityValue, converted, FOR_MEASURE);
		JenaUtils.copyProperty(rdfModel, qualityValue, converted, OBTAINED_FROM);
		JenaUtils.copyProperty(rdfModel, qualityValue, converted, PROV_WAS_GENERATED_BY);
		JenaUtils.copyProperty(rdfModel, qualityValue, converted, PROV_WAS_ATTRIBUTED_TO);
		for (Resource evaluation : converted.listProperties(rdfModel.getProperty(PROV_WAS_GENERATED_BY))
				.mapWith(Statement::getResource).toList()) {
			evaluation.addProperty(rdfModel.getProperty(PROV_GENERATED), converted);
		}

		// The value it was converted from
		converted.addProperty(rdfModel.getProperty(PROV_WAS_DERIVED_FROM), qualityValue);
		return converted;
	}

	/** Value of the quality value as a number, or null if it has none or it is not numeric. */
	private static Double getCardinalValue(Model rdfModel, Resource qualityValue) {
		Statement value = qualityValue.getProperty(rdfModel.getProperty(HAS_LITERAL_VALUE));
		if (value == null || !value.getObject().isLiteral()) {
			return null;
		}
		try {
			return toDouble(value.getLiteral());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** First mapping whose input interval contains value; a mapping without interval contains any value. */
	private static Resource findMappingByInterval(Model rdfModel, List<Resource> scalePointMappings, double value) {
		for (Resource mapping : scalePointMappings) {
			Resource interval = mapping.getPropertyResourceValue(rdfModel.getProperty(HAS_INPUT_CARDINAL_INTERVAL));
			if (interval == null || intervalContains(rdfModel, interval, value)) {
				return mapping;
			}
		}
		return null;
	}

	/**
	 * Whether the interval contains value. A missing lower or upper value means -INF or +INF, and a
	 * missing bound type means a closed bound.
	 */
	private static boolean intervalContains(Model rdfModel, Resource interval, double value) {
		double lower = getDouble(interval.getProperty(rdfModel.getProperty(HAS_INTERVAL_LOWER_VALUE)),
				Double.NEGATIVE_INFINITY);
		double upper = getDouble(interval.getProperty(rdfModel.getProperty(HAS_INTERVAL_UPPER_VALUE)),
				Double.POSITIVE_INFINITY);
		Resource open = rdfModel.getResource(OPEN_INTERVAL_BOUND);
		boolean lowerOpen = interval.hasProperty(rdfModel.getProperty(HAS_INTERVAL_LOWER_BOUND_TYPE), open);
		boolean upperOpen = interval.hasProperty(rdfModel.getProperty(HAS_INTERVAL_UPPER_BOUND_TYPE), open);
		boolean aboveLower = lowerOpen ? value > lower : value >= lower;
		boolean belowUpper = upperOpen ? value < upper : value <= upper;
		return aboveLower && belowUpper;
	}

	/**
	 * Mapping whose input category is the category of the quality value. The category is the one
	 * the quality value represents (btl2:represents) or, if it has none, the one whose IRI, rank or
	 * label is its literal value.
	 */
	private static Resource findMappingByCategory(Model rdfModel, Resource qualityValue,
			List<Resource> scalePointMappings) {
		Resource represented = qualityValue.getPropertyResourceValue(rdfModel.getProperty(IS_ABOUT));
		Statement literal = qualityValue.getProperty(rdfModel.getProperty(HAS_LITERAL_VALUE));
		for (Resource mapping : scalePointMappings) {
			Resource category = mapping.getPropertyResourceValue(rdfModel.getProperty(HAS_INPUT_SCALE_CATEGORY));
			if (category == null) {
				continue;
			}
			if (represented != null) {
				if (represented.equals(category)) {
					return mapping;
				}
			} else if (literal != null && literal.getObject().isLiteral()
					&& categoryMatches(rdfModel, category, literal.getLiteral())) {
				return mapping;
			}
		}
		return null;
	}

	/** Whether the literal is the IRI, the rank (om:rank) or the label (rdfs:label) of the category. */
	private static boolean categoryMatches(Model rdfModel, Resource category, Literal literal) {
		String lexical = literal.getLexicalForm().trim();
		if (category.isURIResource() && lexical.equals(category.getURI())) {
			return true;
		}
		Statement rank = category.getProperty(rdfModel.getProperty(OM_RANK));
		if (rank != null) {
			try {
				if (toDouble(rank.getLiteral()) == toDouble(literal)) {
					return true;
				}
			} catch (NumberFormatException e) {
				// not numeric: try the label
			}
		}
		return category.listProperties(RDFS.label).toList().stream()
				.anyMatch(label -> label.getLiteral().getLexicalForm().trim().equalsIgnoreCase(lexical));
	}

	/**
	 * Value of an ordinal or nominal output: the category and, as literal, its rank (om:rank) or,
	 * if it has none, its label or IRI.
	 */
	private static ConvertedValue categoryValue(Model rdfModel, Resource category) {
		if (category == null) {
			return null;
		}
		Statement rank = category.getProperty(rdfModel.getProperty(OM_RANK));
		if (rank != null && rank.getObject().isLiteral()) {
			return new ConvertedValue(rank.getLiteral(), category);
		}
		Statement label = category.getProperty(RDFS.label);
		Literal literal = label != null && label.getObject().isLiteral()
				? rdfModel.createLiteral(label.getLiteral().getLexicalForm())
				: rdfModel.createLiteral(category.getURI());
		return new ConvertedValue(literal, category);
	}

	/** Numeric value of the statement's literal, or defaultValue if there is no statement. */
	private static double getDouble(Statement statement, double defaultValue) {
		return statement == null ? defaultValue : toDouble(statement.getLiteral());
	}

	/**
	 * Numeric value of a literal, accepting the xsd:double special values INF, -INF and NaN in any
	 * case (some tools write them as inf or Infinity).
	 */
	private static double toDouble(Literal literal) {
		String lexical = literal.getLexicalForm().trim().toUpperCase(Locale.ROOT);
		switch (lexical) {
			case "INF":
			case "+INF":
			case "INFINITY":
			case "+INFINITY":
				return Double.POSITIVE_INFINITY;
			case "-INF":
			case "-INFINITY":
				return Double.NEGATIVE_INFINITY;
			case "NAN":
				return Double.NaN;
			default:
				return Double.parseDouble(lexical);
		}
	}
}


