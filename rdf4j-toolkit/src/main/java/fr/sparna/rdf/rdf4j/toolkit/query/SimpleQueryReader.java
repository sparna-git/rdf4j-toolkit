package fr.sparna.rdf.rdf4j.toolkit.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Supplies a SPARQL query from a variety of sources :
 * <ul>
 * 	<li>A String</li>
 *  <li>An InputStream</li>
 *  <li>A File</li>
 *  <li>A resource in the classpath</li>
 * </ul>
 * 
 * @author Thomas Francart
 *
 */
public class SimpleQueryReader implements Supplier<String> {

	protected String sparql;
	
	public static String fromResource(String resource) {
		return PrefixPrepender.prependPrefixes(
				new SimpleQueryReader(SimpleQueryReader.class.getResourceAsStream(resource)).get()
		);
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a String holding the query
	 * 
	 * @param sparql The String holding the query.
	 */
	public SimpleQueryReader(String sparql) {
		super();
		this.sparql = sparql;
	}
	
	/**
	 * Construct an SPARQLQueryBuilderBase by reading from the stream with the default encoding.
	 * 
	 * @param stream the stream to read from
	 */
	public SimpleQueryReader(InputStream stream) {
		this(stream, Charset.defaultCharset().name());
	}
	
	/**
	 * Construct an SPARQLQueryBuilderBase by reading from the stream with the specified charset
	 * 
	 * @param stream the stream to read from
	 */
	public SimpleQueryReader(InputStream stream, String charset) {
		super();
		// read from the stream
		try {
			// read from the stream
			this.sparql = IOUtils.toString(stream, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a reference to a File to read from and a charset
	 * 
	 * @param file 		The file to read from
	 * @param charset	Charset to use to read the file
	 */
	public SimpleQueryReader(File file, String charset) {
		super();
		
		try {
			this.sparql = FileUtils.readFileToString(file, charset);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("SPARQL file not found : "+file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new IllegalArgumentException("File cannot be read from : "+file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a reference to a File to read from in the default charset
	 * 
	 * @param file 		The file to read from
	 */
	public SimpleQueryReader(File file) {
		this(file, Charset.defaultCharset().name());
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a resource from the classpath
	 * 
	 * @param owner 	The class used to load the resource
	 * @param resource	The resource reference on the classpath relative to the owner
	 */
    public SimpleQueryReader(Class<?> owner, String resource) {
    	if (owner == null) {
            throw new IllegalArgumentException("owner");
        }
    	
    	// Load SPARQL query definition
        InputStream src = owner.getResourceAsStream(resource);
        if (src == null) {
            throw new RuntimeException(new FileNotFoundException(resource));
        }
        
		try {
			// read from the stream
			// note we use the method variant with a String and not a Charset to be able to work
			// with older versions of commons.IO
			this.sparql = IOUtils.toString(src, Charset.defaultCharset().name());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	
    /**
	 * Construct a SPARQLQueryBuilderBase with a resource from the classpath
	 * 
	 * @param owner 	The resource owner used to load the resource
	 * @param resource	The resource reference on the classpath relative to the owner
	 */
    public SimpleQueryReader(Object owner, String resource) {
        this((Class<?>)owner.getClass(), resource);
    }
    
    public SimpleQueryReader(Class<?> owner) {
        this(owner, owner.getSimpleName()+".rq");
    }
    
    public SimpleQueryReader(Object owner) {
        this((Class<?>)owner.getClass(), owner.getClass().getSimpleName()+".rq");
    }
	
	@Override
	public String get() {
		return sparql;
	}

}
