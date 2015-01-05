package org.tapestry.hl7;

import org.apache.log4j.Logger;
import org.tapestry.hl7.TPR;

import java.util.Arrays;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;

/**
 * Class TPR_ORU_R01 for Tapestry Report
 */
public class TPR_ORU_R01 extends ORU_R01 {
	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(TPR_ORU_R01.class);
	
	 /**
     * Constructor
     */
	
	public TPR_ORU_R01() throws HL7Exception{
		this(new DefaultModelClassFactory());
	}	
	
	public TPR_ORU_R01(ModelClassFactory factory) throws HL7Exception {
		super(factory);
		
		// Now, let's add the TPR segment at the right spot
		String[] segmentNames = getNames();
		
		int indexOfPv1 = Arrays.asList(segmentNames).indexOf("PV1");
		
		// Put the ZPI segment right after the PID segment
		int index = indexOfPv1 + 1;
		
		Class<TPR> type = TPR.class;
		boolean required = true;
		boolean repeating = false;
	
		this.add(type, required, repeating, index);
		
	}

	/**
	 * Add an accessor for the TPR segment
	 */
	public TPR getTPR() throws HL7Exception {
		return getTyped("TPR", TPR.class);
	}
}
