package fr.sparna.rdf.rdf4j.toolkit.jsonld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class JsonLDContextGenerator {

	public static void main(String...args) throws Exception {
		Model shapes = Rio.parse(new FileInputStream(new File(args[0])), Rio.getWriterFormatForFileName(args[0]).orElse(RDFFormat.RDFXML), (Resource)null);
		
		JsonLDContextGenerator me = new JsonLDContextGenerator();
		String context = me.createContext(shapes);
		System.out.println(context);
		
		File outputFile = new File(args[1]);
		FileOutputStream fos = new FileOutputStream(outputFile);
		IOUtils.write(context, fos);
		fos.close();
	}
	
	public void run() {
		
	}
	
	public String createContext(Model shapes) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("{"+"\n");
		sb.append("  \"@context\" : {"+"\n");
		addKey(sb, "type", "@type");
		addKey(sb, "id", "@id");
		addKey(sb, "graph", "@graph");
		
		
		sb.append("\n");
		sb.append("\n");
		// iterate on all prefixes
		shapes.getNamespaces().stream().forEach(ns -> {
			addKey(sb, ns.getPrefix(), ns.getName());
		});
		
		sb.append("\n");
		sb.append("\n");

		List<Resource> nodeShapes = shapes.filter(null, RDF.TYPE, SHACL.NODE_SHAPE, (Resource)null).subjects().stream().filter(s -> s.isIRI()).collect(Collectors.toList());
		nodeShapes.sort((ns1,ns2) -> ((IRI)ns1).getLocalName().compareToIgnoreCase(((IRI)ns2).getLocalName()));
		
		
		// then map each class
		for (Resource ns : nodeShapes) {
			addKey(sb, ((IRI)ns).getLocalName(), ((IRI)ns).stringValue());
		}
		
		// then map each property
		sb.append("\n");
		sb.append("\n");
		
		Set<Value> propertyShapes = shapes
				.filter(null, SHACL.PROPERTY, null, (Resource)null).objects();
		
		Set<Value> paths = new HashSet<>();
		propertyShapes.stream()
			.map(ps -> shapes.filter((Resource)ps, SHACL.PATH, null, (Resource)null).objects())
			.forEach(list -> {
				list.stream().filter(v -> v.isIRI()).forEach(value -> paths.add(value));
			});
		
		List<Value> sortedPath = new ArrayList<Value>(paths);
		sortedPath.sort((v1, v2) -> ((IRI)v1).getLocalName().compareToIgnoreCase(((IRI)v2).getLocalName()));
		
		for (Value value : sortedPath) {
			if(value.isIRI()) {
				String type = null;
				Set<Resource> propertyShapesWithThisPath = shapes.filter(null, SHACL.PATH, value, (Resource)null).subjects();
				for (Resource ps : propertyShapesWithThisPath) {
					Set<Value> datatypes = shapes.filter(ps, SHACL.DATATYPE, null, (Resource)null).objects(); 
					if(datatypes.size() > 0) {
						Value theDatatype = datatypes.iterator().next();
						type = theDatatype.stringValue();
						if(type.startsWith(XMLSchema.NAMESPACE)) {
							type = "xsd:"+((IRI)theDatatype).getLocalName();
						}
					}
					Set<Value> classes = shapes.filter(ps, SHACL.CLASS, null, (Resource)null).objects();
					Set<Value> nodes = shapes.filter(ps, SHACL.NODE, null, (Resource)null).objects();
					Set<Value> nodeKindIris = shapes.filter(ps, SHACL.NODE_KIND, SHACL.IRI, (Resource)null).objects();
					
					if(classes.size() > 0 || nodes.size() > 0 || nodeKindIris.size() > 0) {
						type = "@id";
					}
				}
				addPropertyMapping(
						sb,
						((IRI)value).getLocalName(),
						((IRI)value).stringValue(),
						type
				);
			}
		}
				
		// remove last comma
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("  }"+"\n");
		sb.append("}"+"\n");
		
		return sb.toString();
	}
	
	public void addKey(StringBuffer sb, String key, String value) {
		sb.append("    \""+key+"\" : \""+value+"\""+",\n");
	}
	
	public void addPropertyMapping(StringBuffer sb, String key, String uri, String type) {
		sb.append("    \""+key+"\" : ");
		sb.append("{ \"@id\" : \""+uri+"\""+((type != null)?", \"@type\": \""+type+"\"":"")+"}");
		sb.append(",\n");
	}
	
}
