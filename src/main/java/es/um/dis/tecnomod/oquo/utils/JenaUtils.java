package es.um.dis.tecnomod.oquo.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.stream.StreamManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class JenaUtils {
	/** Syntaxes tried, in this order, after the one suggested by the content type or the extension. */
    private static final List<Lang> SYNTAXES = Arrays.asList(
            Lang.RDFXML, Lang.TURTLE, Lang.JSONLD, Lang.NTRIPLES,
            Lang.TRIG, Lang.NQUADS, Lang.RDFJSON, Lang.TRIX);

    /**
     * Reads the RDF document at location (a URL, a file: IRI or a local path), trying every
     * RDF syntax supported by Jena until one of them parses the whole document.
     * @param location location of the document
     * @return a model with the triples of the document
     * @throws IllegalArgumentException if the document cannot be retrieved or parsed with any syntax
     */
    public static Model readModel(String location) {
        byte[] content;
        Lang hint = null;
        try (TypedInputStream in = StreamManager.get().open(location)) {
            if (in == null) {
                throw new IllegalArgumentException("Cannot retrieve " + location);
            }
            content = IO.readWholeFile(in);
            if (in.getContentType() != null) {
                hint = RDFLanguages.contentTypeToLang(in.getContentType());
            }
        }
        if (hint == null) {
            hint = RDFLanguages.filenameToLang(location);
        }

        Set<Lang> syntaxes = new LinkedHashSet<>();
        if (hint != null) {
            syntaxes.add(hint);
        }
        syntaxes.addAll(SYNTAXES);

        List<String> errors = new ArrayList<>();
        for (Lang syntax : syntaxes) {
            Model model = ModelFactory.createDefaultModel();
            try {
                RDFDataMgr.read(model, new ByteArrayInputStream(content), location, syntax);
                return model;
            } catch (RuntimeException e) {
                errors.add(syntax.getName() + ": " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("Cannot parse " + location + " with any RDF syntax:\n  "
                + String.join("\n  ", errors));
    }

	/**
	 * Reads the ontology at ontologyIRI and, recursively, all the ontologies it imports.
	 * @param ontologyIRI IRI (or version IRI) of the ontology
	 * @return a model with the triples of the ontology and of its imports closure
	 */
	public static Model readOntologyWithImports(String ontologyIRI) {
	    Model result = ModelFactory.createDefaultModel();
	    Set<String> visited = new HashSet<>();
	    Deque<String> pending = new ArrayDeque<>();
	    pending.push(ontologyIRI);

	    while (!pending.isEmpty()) {
	        String iri = pending.pop();
	        if (!visited.add(iri)) {
	            continue;
	        }
	        Model ontology = readModel(iri);
	        ontology.listObjectsOfProperty(OWL.imports)
	                .mapWith(RDFNode::asResource)
	                .forEachRemaining(imported -> pending.push(imported.getURI()));
	        result.add(ontology);
	    }
	    result.removeAll(null, OWL.imports, null);

	    return result;
	}

	public static boolean isA(Resource resource, String typeIRI) {
		return resource.hasProperty(RDF.type, resource.getModel().getResource(typeIRI));
	}


	/** Copies all the values of the property propertyIRI of from to to. */
	public static void copyProperty(Model rdfModel, Resource from, Resource to, String propertyIRI) {
		Property property = rdfModel.getProperty(propertyIRI);
		for (RDFNode value : from.listProperties(property).mapWith(Statement::getObject).toList()) {
			to.addProperty(property, value);
		}
	}
}
