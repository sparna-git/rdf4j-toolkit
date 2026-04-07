package fr.sparna.rdf.rdf4j.toolkit.jsonld;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import java.io.ByteArrayInputStream;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.JsonLdVersion;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

public class JsonLdFrame {

	public Document framingSpecification;
	
	public JsonLdFrame(Document framingSpecification ) {
		this.framingSpecification = framingSpecification;
	}
	
	public JsonLdFrame(InputStream framingSpecificationInput ) throws Exception {
		Document framingSpecification = JsonDocument.of(framingSpecificationInput);
		this.framingSpecification = framingSpecification;
	}
	
	public JsonObject frame(InputStream input) throws Exception {
		Document jsonldDocument = JsonDocument.of(input);
		return JsonLd.frame(jsonldDocument, this.framingSpecification).get();
	}
	
	public void frame(InputStream input, OutputStream out) throws Exception {
		JsonObject result = this.frame(input);
		JsonLdFrame.write(result, out);
	}
	
	public static void main(String...args) throws Exception {
		/*
		InputStream docInput = new FileInputStream(args[0]);
		Document jsonldDocument = JsonDocument.of(docInput);
		
		InputStream frameInput = new FileInputStream(args[1]);
		Document framingSpecification = JsonDocument.of(frameInput);
		
		JsonObject result = JsonLd.frame(jsonldDocument, framingSpecification).get();
		
		write(result, System.out);
		*/

			/*
		InputStream docInput = new FileInputStream(args[0]);
		Document jsonldDocument = JsonDocument.of(docInput);

		InputStream frameInput = new FileInputStream(args[1]);
		Document framingSpecification = JsonDocument.of(frameInput);

		JsonObject result = JsonLd.frame(jsonldDocument, framingSpecification).get();
		*/
		
		
		String TEST = "" +
		"<http://example.org/Thomas> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Person> . \n" +
		"<http://example.org/Thomas> <http://example.org/p> \"3\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" +
		"<http://example.org/Thomas> <http://example.org/description> \"Une description\"@fr .\n"
		;

		ByteArrayInputStream bais = new ByteArrayInputStream(TEST.getBytes());
		RdfDocument rdfDocument = RdfDocument.of(bais);

		JsonLdOptions options = new JsonLdOptions();
		options.setUseNativeTypes(true);

		JsonObject result = JsonLd.fromRdf(rdfDocument).options(options).get().getJsonObject(0);

		

		write(result, System.out);
	}
	
	public static void write(JsonObject o, OutputStream out) {
        // Create a JsonWriter with pretty printing (indentation) configuration
        JsonWriter jsonWriter = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
                .createWriter(out);
        // Write the JSON object with indentation to the StringWriter
        jsonWriter.writeObject(o);
        // Close the JsonWriter to release resources
        jsonWriter.close();
	}
	
}
