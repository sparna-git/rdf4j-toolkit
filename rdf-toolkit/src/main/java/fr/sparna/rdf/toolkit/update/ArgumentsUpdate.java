package fr.sparna.rdf.toolkit.update;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription="Apply inference on RDF data using a set of SPARQL queries defined in a directory")
public class ArgumentsUpdate {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true,
			variableArity = true
	) 
	private List<String> input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output RDF file to send result to",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-og", "--outputGraphs" },
			description = "Named graphs IRI to output",
			variableArity = true
	)
	private List<String> outputGraphs;
	
	@Parameter(
			names = { "-u", "--updates" },
			description = "Directory containing SPARQL updates",
			converter = FileConverter.class,
			required = true
	)
	private File updateDirectory;

	public List<IRI> getOutputGraphsIRIs() throws URISyntaxException {
		if(outputGraphs == null) {
			return null;
		}
		
		List<IRI> uris = new ArrayList<IRI>();
		for (String aString : this.outputGraphs) {
			uris.add(SimpleValueFactory.getInstance().createIRI(aString));
		}
		return uris;
	}

	public File getUpdateDirectory() {
		return updateDirectory;
	}

	public void setUpdateDirectory(File updateDirectory) {
		this.updateDirectory = updateDirectory;
	}

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

	public List<String> getOutputGraphs() {
		return outputGraphs;
	}

	public void setOutputGraphs(List<String> outputGraphs) {
		this.outputGraphs = outputGraphs;
	}

}
