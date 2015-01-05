package org.tapestry.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.model.v23.datatype.NM;
//import ca.uhn.hl7v2.model.v23.datatype.NM;
import ca.uhn.hl7v2.model.v23.datatype.ST;

/**
* TRP represents an HL7 custom segment (Tapestry Report)
* This segment has the following fields:</p>
 * <ul>
 	* <li>TPR-1: Life Goals  (LG) <b>optional </b>
 	* <li>TPR-2: Health Goals  (HG) <b>optional </b>
 	* <li>TPR-3: Alerts - Case Review (CR) <b>optional </b>
 	* <li>TPR-4: Social Context  (SC) <b>optional </b>
 	* <li>TPR-5: Plan 1 (PL1) <b>optional </b>
 	* <li>TPR-6: Plan 2 (PL2) <b>optional </b>
 	* <li>TPR-7: Plan 3 (PL3) <b>optional </b>
 	* <li>TPR-8: Plan 4 (PL4) <b>optional </b>
 	* <li>TPR-9: Plan 5 (PL5) <b>optional </b>
 	* <li>TPR-10: Addition Information 1 (AI1) <b>optional </b>
 	* <li>TPR-11: Addition Information 2 (AI2) <b>optional </b>
 	* <li>TPR-12: Addition Information 3 (AI3) <b>optional </b>
 	* <li>TPR-13: Addition Information 4 (AI4) <b>optional </b>
 	* <li>TPR-14: Addition Information 5 (AI5) <b>optional </b>
 	* <li>TPR-15: Function status 1  (FS1) <b>optional </b>
 	* <li>TPR-16: Function status 2  (FS2) <b>optional </b>
 	* <li>TPR-17: Function status 3  (FS3) <b>optional </b>
 	* <li>TPR-18: Nutritional status   (NS1) <b>optional </b> 	* 
 	* <li>TPR-19: Social Support 1 (SS1) <b>optional </b>
 	* <li>TPR-20: Social Support 2 (SS2) <b>optional </b>
 	* <li>TPR-21: Mobility 1 (MB1) <b>optional </b>
 	* <li>TPR-22: Mobility 2 (MB2) <b>optional </b>
 	* <li>TPR-23: Mobility 3 (MB3) <b>optional </b>
 	* <li>TPR-24: Physical Activity 1 (PA1) <b>optional </b>
 	* <li>TPR-25: Physical Activity 2 (PA2) <b>optional </b>
 	* <li>TPR-26: Tapestry Question 1 (TP1) <b>optional </b>
 	* <li>TPR-27: Tapestry Question 2 (TP2) <b>optional </b>
 	* <li>TPR-28: Tapestry Question 3 (TP3) <b>optional </b>
 	* <li>TPR-29: Tapestry Question 4 (TP4) <b>optional </b>
 	* <li>TPR-30: Tapestry Question 5 (TP5) <b>optional </b>
 	* <li>TPR-31: Tapestry Question 6 (TP6) <b>optional </b>
 	* <li>TPR-32: Volunteer Information 1 (VI1)<b>optional </b>
 	* <li>TPR-33: Volunteer Information 2 (VI2)<b>optional </b>
 	* <li>TPR-34: Volunteer Information 3 (VI3)<b>optional </b>
* 
*/
public class TPR extends AbstractSegment {
	/**
	 * Adding a serial UID is always a good idea, but optional
	*/
	private static final long serialVersionUID = 1;
	
	public TPR(Group parent, ModelClassFactory factory) {
		super(parent, factory);
		// By convention, an init() method is created which adds
		// the specific fields to this segment class
		init(factory);
	}
	private void init(ModelClassFactory factory) 
	{
		try 
		{/*
		 * For each field in the custom segment, the add() method is
		 * called once. In this example, there are two fields in the Z-TPR segment.
		 */ 		 
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Life goals");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "healthy goals");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Alerts - Case Review");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "social context");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Plan 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Plan 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Plan 3");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Plan 4");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Plan 5");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Addition Information 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Addition Information 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Addition Information 3");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Addition Information 4");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Addition Information 5");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Function status 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Function status 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Function status 3");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Nutritional status");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Social Support 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Social Support 2 ");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Mobility 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Mobility 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Mobility 3 ");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Physical Activity 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Physical Activity 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 3");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 4");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 5");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Tapestry Question 6");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Volunteer Information 1");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Volunteer Information 2");
			add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Volunteer Information 3");
			
			add(NM.class, true, 1, 4, new Object[]{ getMessage() }, "weight"); 
			

		} catch (HL7Exception e) {
			log.error("Unexpected error creating TPR - this is probably a bug in the source code generator.", e);
		}
	}
	/**
	 * This method must be overridden. The easiest way is just to return null.
	 */
	@Override
	protected Type createNewTypeWithoutReflection(int field) {
		return null;
	}
			 
	/**
	 * Create an accessor for each field
	 */

	public ST getLifeGoals() throws HL7Exception {
		ST retVal = this.getTypedField(1, 0);
		return retVal;	
	}
	
	
	public ST getHealthGoals() throws HL7Exception {
		ST retVal = this.getTypedField(2, 0);
		return retVal;	
	}
	
	public ST getAlerts() throws HL7Exception {
		ST retVal = this.getTypedField(3, 0);
		return retVal;	
	}
	
	public ST getSocialContext() throws HL7Exception {
		ST retVal = this.getTypedField(4, 0);
		return retVal;	
	}
	
	public ST getPlan1() throws HL7Exception {
		ST retVal = this.getTypedField(5, 0);
		return retVal;	
	}
	
	public ST getPlan2() throws HL7Exception {
		ST retVal = this.getTypedField(6, 0);
		return retVal;	
	}
	public ST getPlan3() throws HL7Exception {
		ST retVal = this.getTypedField(7, 0);
		return retVal;	
	}
	public ST getPlan4() throws HL7Exception {
		ST retVal = this.getTypedField(8, 0);
		return retVal;	
	}
	public ST getPlan5() throws HL7Exception {
		ST retVal = this.getTypedField(9, 0);
		return retVal;	
	}
	public NM getWeight() throws HL7Exception {
		return getTypedField(10, 0);
	}
	
}
