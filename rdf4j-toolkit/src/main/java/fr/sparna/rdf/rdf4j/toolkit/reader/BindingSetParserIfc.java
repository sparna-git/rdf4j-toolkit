package fr.sparna.rdf.rdf4j.toolkit.reader;

import org.eclipse.rdf4j.query.BindingSet;

/**
 * An abstraction for objects capable of reading a key and its associated value from a SPARQL
 * query result binding.
 * @author Thomas Francart
 *
 * @param <Key>	the class of keys
 * @param <Value> the class of values
 */
public interface BindingSetParserIfc<Key, Value> {

	/**
	 * Reads the key from the binding set
	 * @param binding the bindings to parse
	 * @return
	 */
	public Key parseKey(BindingSet binding);
	
	/**
	 * Reads the value from the binding set
	 * @param binding the bindings to parse
	 * @return
	 */
	public Value parseValue(BindingSet binding);
	
}
