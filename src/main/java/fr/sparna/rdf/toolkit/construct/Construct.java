package fr.sparna.rdf.toolkit.construct;

import java.io.File;
import java.util.List;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.query.builder.FileSPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Construct implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsConstruct args = (ArgumentsConstruct)o;
		
		// TODO configure logging
		
		// lire le RDF d'input
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();
		
		// preparer le RDF d'output
		Repository outputRepository = new LocalMemoryRepositoryFactory().createNewRepository();
		
		// executer les SPARQL
		List<File> sparqls = FileUtil.listFilesRecursive(args.getQueryDirectoryOrFile());
		for (File file : sparqls) {
			log.debug("Applying rule "+file.getAbsolutePath()+"...");
			SesameSPARQLExecuter.newExecuter(inputRepository).executeConstruct(
					new ConstructSPARQLHelper(
							new FileSPARQLQueryBuilder(file),
							new CopyStatementRDFHandler(outputRepository)
					)
			);
		}
		
		// write output
		RepositoryWriter.writeToFile(args.getOutput(), outputRepository);
		
		// shutdown repos
		inputRepository.shutDown();
		outputRepository.shutDown();
	}

}
