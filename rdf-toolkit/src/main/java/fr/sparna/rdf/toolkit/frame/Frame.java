package fr.sparna.rdf.toolkit.frame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.jsonld.JsonLdFrame;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;

public class Frame implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsFrame args = (ArgumentsFrame)o;

		// lire le RDF d'input
		log.debug("Reading input RDF...");
		Repository r = RepositoryBuilderFactory.fromStringList(args.getInput()).get();
		log.debug("Done !");
				
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// serialize in JSON-LD (plain)
		log.debug("Serializing input in plain JSON-LD...");		
		
		try(RepositoryConnection connection = r.getConnection()) {
			RepositoryWriter repoWriter;
			if(args.getSparql() != null) {
				log.debug("Serializing wusing a query... (query from "+args.getSparql()+")");	
				String sparqlString = FileUtils.readFileToString(args.getSparql(), Charset.defaultCharset());
				repoWriter = new RepositoryWriter(connection, sparqlString);				
			} else {
				repoWriter = new RepositoryWriter(connection);
			}
			
			repoWriter.writeToStream(baos, RDFFormat.JSONLD);
		}
		log.debug("Done !");
		

		if(args.getDebug() != null) {
			log.debug("Debugging... (to : "+args.getDebug().getAbsolutePath()+")");
			// read back as Json
			ByteArrayInputStream baisDebug = new ByteArrayInputStream(baos.toByteArray());
			FileOutputStream fos = new FileOutputStream(args.getDebug());
			fos.write(baisDebug.readAllBytes());
			fos.flush();
			fos.close();
			log.debug("Done !");
		}
		
		// init framer
		JsonLdFrame framer = new JsonLdFrame(new FileInputStream(args.getFrame()));
		
		// apply framing
		log.debug("Framing... (to "+args.getOutput().getAbsolutePath()+")");
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		framer.frame(bais, new FileOutputStream(args.getOutput()));
		log.debug("Done !");
	}
	
}
