package fr.sparna.rdf.rdf4j.toolkit.handler;

import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a query result consisting of a single line with a single value (Literal or Resource).
 * 
 * @author Thomas Francart
 */
public class ReadSingleValueHandler extends AbstractTupleQueryResultHandler implements TupleQueryResultHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Value result = null;
	protected String bindingName = null;
	
	@Override
	public void startQueryResult(List<String> bindingNames)
	throws TupleQueryResultHandlerException {	
		// if more than 1 binding name, we have a problem
		if(bindingNames.size() > 1) {
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single binding.");
		}
		
		// keep track of binding name
		this.bindingName = bindingNames.get(0);
		
		// re-init result
		this.result = null;
	}
	
	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		if(this.result != null) {
			// oups, 2 result lines, this is unexpected
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single result.");
		}
		this.result = bs.getValue(this.bindingName);
		log.trace("Read single value :\n"+this.result);
	}

	/**
	 * Returns the single result from this query
	 * 
	 * @return the single value for this result
	 */
	public Value getResult() {
		return result;
	}

}
