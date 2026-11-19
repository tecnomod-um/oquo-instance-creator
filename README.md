# oquo-instance-creator

A Java library that turns the results of quality evaluations into RDF that follows the
[Ontology QUality Ontology (OQUO)](https://github.com/tecnomod-um/oquo).

You describe what you measured (which entity, with which metric, on which scale, what value you
got and when) with plain Java objects, and the library creates the corresponding OQUO instances in
an [Apache Jena](https://jena.apache.org/) `Model`. It can also add the OQUO module that describes
your metrics and convert the values to the scale of its quality model, so that results from
different frameworks can be compared.

If your application is not written in Java, use
[oquo-instance-creator-endpoint](https://github.com/tecnomod-um/oquo-instance-creator-endpoint),
a REST service that exposes this library.

## What it generates

A single measurement in OQUO is spread across several linked resources. For every observation, the
library creates:

| Resource created              | OQUO / reused class                          | What it represents                                   |
|-------------------------------|----------------------------------------------|------------------------------------------------------|
| `<ns>observation-<uuid>`      | `oboe:Observation` (and `ipo:Symptom` if it detected issues) | The act of observing one entity        |
| `<ns>metric-<uuid>`           | `oboe:Measurement` + your metric class       | The measurement taken with that metric               |
| `<ns>qualityValue-<uuid>`     | `eval:QualityValue`, `oboe:MeasuredValue`, `prov:Entity` | The value obtained, on the given scale   |
| `<ns>evaluation-<uuid>`       | `eval:Evaluation`, `prov:Activity`           | The evaluation that produced the value               |
| `<ns>instant-<uuid>`          | `time:Instant`                               | When the evaluation happened                         |
| `<ns>issue-<uuid>`            | your issue class                             | (Optional) problems detected by the observation      |
| `<ns>agent-<uuid>`            | the type of the agent (e.g. `prov:SoftwareAgent`) | (Optional) the specific version of the software agent that ran the evaluation, when a version is given |

`<ns>` is the **data namespace**, by default `https://purl.org/oquo-data/` (also used when the
namespace given is `null` or blank). Use your own namespace to publish your results.

The results also record their **provenance** with [PROV-O](https://www.w3.org/TR/prov-o/): the
evaluation generated the quality value (`prov:generated`, `prov:wasGeneratedBy`) and, if you give
an agent, was run by it (`prov:wasAssociatedWith`, `prov:wasAttributedTo`). See the
[Provenance section of the OQUO README](https://github.com/tecnomod-um/oquo#provenance).

## Requirements

- Java 8 or later
- Maven 3 (to build it)

## Installation

The library is not published in Maven Central. Install it in your local Maven repository:

```bash
git clone https://github.com/tecnomod-um/oquo-instance-creator
cd oquo-instance-creator
mvn install -DskipTests
```

This builds the library for Java 8 (profile `java8`, active by default). To build it for Java 17,
add `-Pjava17`. Besides the library, `target/` contains a jar with all its dependencies
(`oquo-instance-creator-0.0.8-jar-with-dependencies.jar`).

Then add it to the `pom.xml` of your project:

```xml
<dependency>
    <groupId>es.um.dis.tecnomod</groupId>
    <artifactId>oquo-instance-creator</artifactId>
    <version>0.0.8</version>
</dependency>
```

## Usage

### Describing an observation

Each observation is an `ObservationInfoDTO`:

```java
ObservationInfoDTO observation = new ObservationInfoDTO();

// The ontology that was evaluated (its version IRI if available)
observation.setSourceDocumentIRI("http://purl.obolibrary.org/obo/go/releases/2021-02-01/go.owl");

// The entity that was evaluated, and its type
observation.setFeatureOfInterestIRI("http://purl.obolibrary.org/obo/GO_0008150");
observation.setFeatureOfInterestTypeIRI(OWL.Class.getURI());

// The metric and its scale, as defined in an OQUO module
observation.setMetricUsedIRI("https://purl.org/oquo-huron#NamesPerClassMetric");
observation.setScaleIRI("https://purl.org/oquo-huron#NamesPerClassMetricScale");
observation.setScaleTypeIRI("https://purl.org/oquo#RawScale");
observation.setRankingFunctionIRI("http://purl.org/net/QualityModel#HigherBest");

// The value and when it was obtained
observation.setValue(Double.valueOf(0.85));
observation.setTimestamp(Calendar.getInstance());

// Optional: the agent that ran the evaluation
observation.setAgentIRI("https://purl.org/oquo-huron#huron-software-agent");
observation.setAgentTypeIRI("http://www.w3.org/ns/prov#SoftwareAgent");
```

| Field                      | Required | Description |
|----------------------------|:--------:|-------------|
| `featureOfInterestIRI`     | yes | IRI of the entity that was evaluated (e.g. a class of an ontology, or the ontology itself). |
| `featureOfInterestTypeIRI` | yes | IRI of the type of that entity (e.g. `owl:Class`, `owl:Ontology`). |
| `metricUsedIRI`            | yes | IRI of the metric, as defined in an OQUO module. |
| `scaleIRI`                 | yes | IRI of the scale the value is measured on. |
| `scaleTypeIRI`             | yes | IRI of the type of scale, e.g. `https://purl.org/oquo#RawScale`. |
| `rankingFunctionIRI`       | yes | `http://purl.org/net/QualityModel#HigherBest` or `http://purl.org/net/QualityModel#LowerBest`. |
| `value`                    | yes | The measured value. Numbers, booleans and strings are stored as typed literals. For ordinal and nominal scales, give the rank, the label or the IRI of the category. |
| `timestamp`                | yes | When the evaluation took place. |
| `sourceDocumentIRI`        | no  | IRI of the document that was evaluated. Stored as the evaluation's input data. |
| `details`                  | no  | Free-text notes, stored as `rdfs:comment` on the quality value. |
| `issues`                   | no  | Problems detected (`IssueInfoDTO`, see below). |
| `configurationDataList`    | no  | Configuration used in the evaluation (`ConfigurationInfoDTO`, see below). |
| `agentIRI`                 | no  | IRI of the agent that ran the evaluation: a tool, a person or an organization. |
| `agentTypeIRI`             | no  | Type of the agent: `prov:SoftwareAgent`, `prov:Person` or `prov:Organization`. If not given, the agent is typed as `prov:Agent`. |
| `agentVersion`             | no  | Version of the software that ran the evaluation. Only for software agents. |

If `agentVersion` is given, the library creates an agent for that version in the data namespace
(`schema:softwareVersion`, `dcterms:isVersionOf` the agent), which is the one associated with the
evaluation. Its IRI is derived from the IRI of the agent and the version, so all the evaluations run
with the same version share it. The agent fields are not validated: giving `agentVersion` only for
software agents is up to the caller.

An **issue** (`IssueInfoDTO`) has:

| Field            | Required | Description |
|------------------|:--------:|-------------|
| `issueType`      | yes | IRI of the issue class, as defined in an OQUO module, or one of the severities of the OQUO core (`IssueTypes.CRITICAL_ISSUE`, `MAJOR_ISSUE`, `MINOR_ISSUE`, `INFO_ISSUE`). |
| `message`        | no  | Human-readable description, stored as `rdfs:comment`. |
| `hostAsset`      | no  | IRI of the entity where the issue is located, usually the feature of interest. |
| `causativeAsset` | no  | IRI of the entity that causes the issue, if different from the host. |

A **configuration** (`ConfigurationInfoDTO`) has a `configurationIRI`, which identifies it (use the
same IRI for all the observations that share a configuration), and a `serializedConfiguration`, the
configuration as plain text.

### Creating the RDF

To add an observation to a Jena model:

```java
Model model = ModelFactory.createDefaultModel();
InstanceCreator.createObservation(model, observation, "https://example.org/my-evaluations/");
```

To create a model with the RDF of several observations:

```java
ObservationsInfoDTO observations = new ObservationsInfoDTO();
observations.setObservationsInfoDTO(Arrays.asList(observation1, observation2));

Model model = InstanceCreator.createObservations(observations, "https://example.org/my-evaluations/");
```

Both methods have a version without the data namespace, which uses the default one. Every call
creates new resources, with random UUIDs, even for the same input.

### Adding the OQUO module and converting the values

`createObservationsAndApplyModel` also receives the IRI of the OQUO module that describes the
metrics (e.g. `https://purl.org/oquo-huron`):

```java
Model model = InstanceCreator.createObservationsAndApplyModel(
        observations, "https://purl.org/oquo-huron", "https://example.org/my-evaluations/");
```

The resulting model contains, besides the observations:

- **The module and all the ontologies it imports**, read from their IRIs, so that the result is
  self-contained. The `owl:imports` statements are removed, since their content is already in the
  model. The ontologies can be in any RDF syntax supported by Jena (RDF/XML, Turtle, JSON-LD…),
  whatever their extension. They are cached, so each one is downloaded only once while the
  application runs; `JenaUtils.clearCache()` empties the cache, e.g. to read a new version of a
  module.
- **The quality values obtained by applying the scale conversions defined in the module.** Each
  quality value whose scale is the input scale of a conversion is converted to its output scale,
  typically the scale of the quality model. The new quality value shares the measure, evaluation
  and agent of the original one, and is linked to it with `prov:wasDerivedFrom`. On ordinal and
  nominal scales, its literal is the code of the category (its rank or label), and it is linked to
  the category with `obo:IAO_0000136` (*is about*).

The nine types of conversion of OQUO are supported (cardinal, ordinal or nominal to cardinal,
ordinal or nominal), and conversions are chained: a converted value is converted again if there is
a conversion from its scale. From each original value, at most one value is created on each scale,
which also prevents infinite loops when the conversions form a cycle. Values that match no point
mapping of a conversion are not converted.

`InstanceCreator.applyModel(model, ontologyIRI, dataNamespace)` does the same on a model you have
already created.

### Validating the result

`RDFValidator` checks the result against the
[SHACL shapes of OQUO](https://github.com/tecnomod-um/oquo/tree/main/SHACL), version 2.2.2, together
with the OQUO module that describes the metrics and its imports. The module must be in RDF/XML, and
it is read on every call, without the cache:

```java
ValidationReport report = RDFValidator.validate(model, "https://purl.org/oquo-huron");
if (!report.conforms()) {
    ShLib.printReport(report);
}
```

### Writing the result

```java
model.setNsPrefixes(Namespaces.getPrefixMap());   // optional: readable prefixes
model.write(System.out, "TURTLE");                // or "RDF/XML", "NTRIPLES", "JSON-LD"...
```

## Where do the metric and scale IRIs come from?

The IRIs of metrics, scales, issue types and software agents are defined in the OQUO modules that
integrate each evaluation framework, such as [oquo-huron](https://purl.org/oquo-huron),
[oquo-oquare](https://purl.org/oquo-oquare) and [oquo-ontoenrich](https://purl.org/oquo-ontoenrich).
See [How to use OQUO](https://github.com/tecnomod-um/oquo#how-to-use-oquo) to define your own.

## Tests

```bash
mvn test
```

Some tests read the OQUO modules from their PURLs, so they need network access. The tests of the
scale conversions use local test ontologies (`src/test/resources/test_data`).

## Related projects

| Project | What it does |
|---------|--------------|
| [oquo](https://github.com/tecnomod-um/oquo) | The ontology itself, its modules and examples. |
| [oquo-instance-creator-endpoint](https://github.com/tecnomod-um/oquo-instance-creator-endpoint) | REST service that exposes this library to any language. |
| [oquo-scale-conversion-creator](https://github.com/tecnomod-um/oquo-scale-conversion-creator) | Command-line tool to add scale conversions to OQUO-based modules. |

## License

Apache License 2.0. See [LICENSE](LICENSE).