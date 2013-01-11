package fr.sparna.rdf.toolkit.infer;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription="Apply inference on RDF data using a set of SPARQL queries defined in a directory")
public class ArgumentsInferSPARQL extends ArgumentsInferBase {

	@Parameter(
			names = { "-r", "--rules" },
			description = "Directory containing SPARQL queries",
			converter = FileConverter.class,
			required = true
	)
	private File queryDirectory;

	public File getQueryDirectory() {
		return queryDirectory;
	}

	public void setQueryDirectory(File queryDirectory) {
		this.queryDirectory = queryDirectory;
	}

}
