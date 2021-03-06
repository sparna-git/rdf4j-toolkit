package fr.sparna.rdf.toolkit.construct;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription = "Applies a set of CONSTRUCT queries and sends result to output file")
public class ArgumentsConstruct {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true,
			variableArity = true
	)
	private List<String> input;
	
	@Parameter(
			names = { "-q", "--queries" },
			description = "SPARQL query file, or directory containing SPARQL queries",
			converter = FileConverter.class,
			required = true
	)
	private File queryDirectoryOrFile; 
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output RDF file to send result to",
			converter = FileConverter.class,
			required = true
	)
	private File output; 

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public File getQueryDirectoryOrFile() {
		return queryDirectoryOrFile;
	}

	public void setQueryDirectoryOrFile(File queryDirectoryOrFile) {
		this.queryDirectoryOrFile = queryDirectoryOrFile;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}
	
}
