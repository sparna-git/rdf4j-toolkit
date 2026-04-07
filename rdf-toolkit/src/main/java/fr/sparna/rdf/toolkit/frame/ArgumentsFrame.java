package fr.sparna.rdf.toolkit.frame;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Frame an RDF data file using a provided JSON-LD framing specification file")
public class ArgumentsFrame {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, or Spring config",
			required = true,
			variableArity = true
	) 
	private List<String> input;

	@Parameter(
			names = { "-o", "--output" },
			description = "Output JSON-LD file",
			required = true
	) 
	private File output;
	
	@Parameter(
			names = { "-d", "--debug" },
			description = "Debug raw JSON-LD in a file"
	) 
	private File debug;
	
	@Parameter(
			names = { "-f", "--frame" },
			description = "JSON-LD 1.1 framing specification",
			required = true
	)
	private File frame;
	
	@Parameter(
			names = { "-s", "--sparql" },
			description = "SPARQL query",
			required = false
	)
	private File sparql;

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public File getFrame() {
		return frame;
	}

	public void setFrame(File frame) {
		this.frame = frame;
	}

	public File getDebug() {
		return debug;
	}

	public void setDebug(File debug) {
		this.debug = debug;
	}

	public File getSparql() {
		return sparql;
	}

	public void setSparql(File sparql) {
		this.sparql = sparql;
	}

}
