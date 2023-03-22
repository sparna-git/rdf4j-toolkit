package fr.sparna.rdf.rdf4j.toolkit.jsonld;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.github.jsonldjava.core.DocumentLoader;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;

// voir https://stackoverflow.com/questions/43638342/how-to-convert-rdf-to-pretty-nested-json-using-java-rdf4j
// Jackson JSON-LD annotations to generate @context : https://www.baeldung.com/json-linked-data
public class JsonLDParse {

	public static void main(String...args) throws Exception {
		// Open a valid json(-ld) input file
		InputStream inputStream = new FileInputStream(args[0]);
		
		// Read the file into an Object (The type of this object will be a List, Map, String, Boolean,
		// Number or null depending on the root object in the file).
		Object jsonObject = JsonUtils.fromInputStream(inputStream);
		
		System.out.println(JsonUtils.toPrettyString(jsonObject));
		
		// Create a context JSON map containing prefixes and definitions
		// Map context = new HashMap();
		Object context = JsonUtils.fromInputStream(new FileInputStream(args[1]));
		// Customise context...
		
		System.out.println(JsonUtils.toPrettyString(context));
		
		// Create an instance of JsonLdOptions with the standard JSON-LD options
		JsonLdOptions options = new JsonLdOptions();
		options.setProcessingMode(JsonLdOptions.JSON_LD_1_1);
		// Customise options...
		DocumentLoader dl = new DocumentLoader();
		dl.addInjectedDoc("http://lexisnexis.fr/jsonldcontext.json",  JsonUtils.toPrettyString(context));
		options.setDocumentLoader(dl);
		
		// Call whichever JSONLD function you want! (e.g. compact)
		// Object compact = JsonLdProcessor.compact(jsonObject, context, options);
		
		
		// ((Map) jsonObject).put("@context", context);
		
		// first flatten
		Object compact = JsonLdProcessor.compact(jsonObject, context, options);
		
		System.out.println(JsonUtils.toPrettyString(compact));
		
		// then parse
		StatementCollector collector = new StatementCollector();
		JsonLdProcessor.toRDF(compact, new RDF4JTripleCallback(collector));
		
		// Print out the result (or don't, it's your call!)
		// System.out.println(JsonUtils.toPrettyString(compact));
		
		Repository repository = new SailRepository(new MemoryStore());
		try(RepositoryConnection c = repository.getConnection()) {
			c.add(collector.getStatements(), (Resource)null);
			RepositoryWriter writer = new RepositoryWriter(c);
			writer.writeToStream(System.out, RDFFormat.TURTLE);
		}
		
	}
	
}
