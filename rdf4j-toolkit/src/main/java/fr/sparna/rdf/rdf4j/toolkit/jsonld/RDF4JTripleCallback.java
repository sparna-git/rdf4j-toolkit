package fr.sparna.rdf.rdf4j.toolkit.jsonld;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;

import com.github.jsonldjava.core.JsonLdConsts;
import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Quad;

public class RDF4JTripleCallback implements JsonLdTripleCallback {
	
	protected RDFHandler handler;
	protected ValueFactory vf;
	private final Function<String, Resource> namedBNodeCreator;
	private final Supplier<Resource> anonymousBNodeCreator;
	
	public RDF4JTripleCallback(RDFHandler handler) {
		super();
		this.handler = handler;
		this.vf = SimpleValueFactory.getInstance();
		this.namedBNodeCreator = nodeID -> SimpleValueFactory.getInstance().createBNode(nodeID);
		this.anonymousBNodeCreator = () -> SimpleValueFactory.getInstance().createBNode();
	}


	@Override
	public Object call(final RDFDataset dataset) {
		if (handler != null) {
			try {
				handler.startRDF();
				for (final Entry<String, String> nextNamespace : dataset.getNamespaces().entrySet()) {
					handler.handleNamespace(nextNamespace.getKey(), nextNamespace.getValue());
				}
			} catch (final RDFHandlerException e) {
				throw new RuntimeException("Could not handle start of RDF", e);
			}
		}
		for (String graphName : dataset.keySet()) {
			final List<RDFDataset.Quad> quads = dataset.getQuads(graphName);
			if (JsonLdConsts.DEFAULT.equals(graphName)) {
				graphName = null;
			}
			for (final RDFDataset.Quad quad : quads) {
				if (quad.getObject().isLiteral()) {
					triple(quad.getSubject().getValue(), quad.getPredicate().getValue(), quad.getObject().getValue(),
							quad.getObject().getDatatype(), quad.getObject().getLanguage(), graphName);
				} else {
					triple(quad.getSubject().getValue(), quad.getPredicate().getValue(), quad.getObject().getValue(),
							graphName);
				}
			}
		}
		if (handler != null) {
			try {
				handler.endRDF();
			} catch (final RDFHandlerException e) {
				throw new RuntimeException("Could not handle end of RDF", e);
			}
		}

		return handler;
	}
	

	private Resource createResource(String resource) {
		// Blank node without any given identifier
		if (resource.equals(JsonLdConsts.BLANK_NODE_PREFIX)) {
			return anonymousBNodeCreator.get();
		} else if (resource.startsWith(JsonLdConsts.BLANK_NODE_PREFIX)) {
			return namedBNodeCreator.apply(resource.substring(2));
		} else {
			return vf.createIRI(resource);
		}
	}
	

	private void triple(String s, String p, String value, String datatype, String language, String graph) {

		if (s == null || p == null || value == null) {
			// TODO: i don't know what to do here!!!!
			return;
		}

		final Resource subject = createResource(s);

		final IRI predicate = vf.createIRI(p);
		final IRI datatypeURI = datatype == null ? null : vf.createIRI(datatype);

		Value object;
		try {
			// object = RDFParserHelper.createLiteral(value, language, datatypeURI, getParserConfig(),
			// 		getParserErrorListener(), getValueFactory());
			if(datatype == null) {
				if(language == null) {
					object = vf.createLiteral(value);
				} else {
					object = vf.createLiteral(value, language);
				}
			} else {
				object = vf.createLiteral(value, datatypeURI);
			}
		} catch (final RDFParseException e) {
			throw new RuntimeException(e);
		}

		Statement result;
		if (graph == null) {
			result = vf.createStatement(subject, predicate, object);
		} else {
			result = vf.createStatement(subject, predicate, object, createResource(graph));
		}

		if (handler != null) {
			try {
				handler.handleStatement(result);
			} catch (final RDFHandlerException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void triple(String s, String p, String o, String graph) {
		if (s == null || p == null || o == null) {
			// TODO: i don't know what to do here!!!!
			return;
		}

		Statement result;
		// This method is always called with three Resources as subject
		// predicate and
		// object
		if (graph == null) {
			result = vf.createStatement(createResource(s), vf.createIRI(p), createResource(o));
		} else {
			result = vf.createStatement(createResource(s), vf.createIRI(p), createResource(o), createResource(graph));
		}

		if (handler != null) {
			try {
				handler.handleStatement(result);
			} catch (final RDFHandlerException e) {
				throw new RuntimeException(e);
			}
		}
	}
}