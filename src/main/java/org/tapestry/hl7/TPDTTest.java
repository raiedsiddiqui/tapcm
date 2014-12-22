package org.tapestry.hl7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tapestry.objects.Appointment;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.message.ADT_A04;
import ca.uhn.hl7v2.model.v23.segment.EVN;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.TX;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.VAR;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class TPDTTest {
	private String[] patientData;	
	private Appointment appointment;	
	private String message;	
	private String fileName;
	
	public void setPatientData(Patient patient)
	{
		this.patientData = new String[5];
		
		this.patientData[0] = patient.getFirstName();
		this.patientData[1] = patient.getLastName();
		this.patientData[2] = patient.getBod();
		this.patientData[3] = patient.getMrp();
		this.patientData[4] = patient.getAddress();
	}
	
	public void setAppointment(Appointment a){
		this.appointment = a;
	}
	
	public String getAdtA04DataMessage() throws HL7Exception {
		if (this.message == null)
			this.generateA04MessageTapestryReport();
			
		return this.message;
	}
	
	public String getORUDataMessage() throws Exception {
		if (this.message == null)
			this.populateORU1Message();
			
		return this.message;
	}

	public void generateA04MessageTapestryReport() throws HL7Exception {		
		
		// get current timestamp
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddkkmmss.SSSZ");
	    String currentTimestamp = formatter.format(new Date());
	    
	      
	    // get current date/time
	    formatter = new SimpleDateFormat("yyyyMMddkkmmss");
	    String currentDateTime = formatter.format(new Date());
	   	   
	    TPDT_A04 tpdt_A04 = new TPDT_A04();
	    // MSH Segment
	    MSH mshSegment = tpdt_A04.getMSH();
	
	    mshSegment.getFieldSeparator().setValue("|");
	    mshSegment.getEncodingCharacters().setValue("^~\\&");
	  
	    mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDateTime);
	    mshSegment.getMessageControlID().setValue(currentTimestamp.toString());
	    mshSegment.getProcessingID().getProcessingID().setValue("T");
	    mshSegment.getMessageType().getMessageType().setValue("ADT");
	    mshSegment.getMessageType().getTriggerEvent().setValue("A04");
	    mshSegment.getVersionID().setValue("2.3");
	    
	    // EVN Segment
	
	    EVN evn = tpdt_A04.getEVN();
	    evn.getEventTypeCode().setValue("A04");
	    evn.getRecordedDateTime().getTimeOfAnEvent().setValue(currentDateTime);
	    
	    // PID Segment
	
	    PID pid = tpdt_A04.getPID(); 
	    pid.getPid1_SetIDPatientID().setValue("1");  
	
	    pid.getPatientIDInternalID(0).getID().setValue("R_"+patientData[0]);     
	    pid.getPatientName().getFamilyName().setValue(patientData[0]);
	    pid.getPatientName().getGivenName().setValue(patientData[1]);
	    pid.getPatientName().getMiddleInitialOrName().setValue(" ");
	    pid.getDateOfBirth().getTimeOfAnEvent().setValue(patientData[2]);
	    pid.getPatientAddress(0).getCity().setValue("Hamilton");
	    pid.getPatientAddress(0).getStreetAddress().setValue("175 Longwood S");
	    pid.getPatientAddress(0).getCountry().setValue("Canada");
	    pid.getPatientAddress(0).getStateOrProvince().setValue("Ontario");
	    pid.getPatientAddress(0).getZipOrPostalCode().setValue("L7B 3C2");
	    
	    // PV1 segment	
	    PV1 pv1 = tpdt_A04.getPV1();	
	    // Make 'null' values a space
	    String nullValue = " ";
	    
	    pv1.getPv110_HospitalService().setValue(nullValue);
	    pv1.getPv111_TemporaryLocation().getPl1_PointOfCare().setValue(nullValue);
	    pv1.getPv112_PreadmitTestIndicator().setValue(nullValue);
	    pv1.getPv113_ReadmissionIndicator().setValue(nullValue);
	    pv1.getPv114_AdmitSource().setValue(nullValue);
	    pv1.getPv115_AmbulatoryStatus(0).setValue(nullValue);
	    pv1.getPv116_VIPIndicator().setValue(nullValue);
	    pv1.getPv117_AdmittingDoctor(0).getXcn1_IDNumber().setValue(nullValue);
	    pv1.getPv118_PatientType().setValue(nullValue);
	    pv1.getPv119_VisitNumber().getCx1_ID().setValue(nullValue);
	    pv1.getPv11_SetIDPatientVisit().setValue(nullValue);
	    pv1.getPv120_FinancialClass(0).getFc1_FinancialClass().setValue(nullValue);
	    pv1.getPv121_ChargePriceIndicator().setValue(nullValue);
	    pv1.getPv122_CourtesyCode().setValue(nullValue);
	    pv1.getPv123_CreditRating().setValue(nullValue);
	    pv1.getPv124_ContractCode(0).setValue(nullValue);
	    pv1.getPv125_ContractEffectiveDate(0).setValue(nullValue);
	    pv1.getPv126_ContractAmount(0).setValue(nullValue);
	    pv1.getPv127_ContractPeriod(0).setValue(nullValue);
	    pv1.getPv128_InterestCode().setValue(nullValue);
	    pv1.getPv129_TransferToBadDebtCode().setValue(nullValue);
	    pv1.getPv12_PatientClass().setValue(nullValue);
	    pv1.getPv130_TransferToBadDebtDate().setValue(nullValue);
	    pv1.getPv131_BadDebtAgencyCode().setValue(nullValue);
	    pv1.getPv132_BadDebtTransferAmount().setValue(nullValue);
	    pv1.getPv133_BadDebtRecoveryAmount().setValue(nullValue);
	    pv1.getPv134_DeleteAccountIndicator().setValue(nullValue);
	    pv1.getPv135_DeleteAccountDate().setValue(nullValue);
	    pv1.getPv136_DischargeDisposition().setValue(nullValue);
	    pv1.getPv137_DischargedToLocation().getCm_dld1_DischargeLocation().setValue(nullValue);
	    pv1.getPv138_DietType().setValue(nullValue);
	    pv1.getPv139_ServicingFacility().setValue(nullValue);
	    pv1.getPv140_BedStatus().setValue(nullValue);
	    pv1.getPv13_AssignedPatientLocation().getPl1_PointOfCare().setValue(nullValue);
	    pv1.getPv141_AccountStatus().setValue(nullValue);
	    pv1.getPv142_PendingLocation().getPl1_PointOfCare().setValue(nullValue);
	    pv1.getPv143_PriorTemporaryLocation().getPl1_PointOfCare().setValue(nullValue);
	    pv1.getPv144_AdmitDateTime().getTs1_TimeOfAnEvent().setValue(nullValue);
	    pv1.getPv145_DischargeDateTime().getTs1_TimeOfAnEvent().setValue(nullValue);
	    pv1.getPv146_CurrentPatientBalance().setValue(nullValue);
	    pv1.getPv147_TotalCharges().setValue(nullValue);
	    pv1.getPv148_TotalAdjustments().setValue(nullValue);
	    pv1.getPv149_TotalPayments().setValue(nullValue);
	    pv1.getPv14_AdmissionType().setValue(nullValue);
	    pv1.getPv150_AlternateVisitID().getCx1_ID().setValue(nullValue);
	    pv1.getPv151_VisitIndicator().setValue(nullValue);
	    pv1.getPv152_OtherHealthcareProvider(0).getXcn1_IDNumber().setValue(nullValue);
	    pv1.getPv15_PreadmitNumber().getCx1_ID().setValue(nullValue);
	    pv1.getPv16_PriorPatientLocation().getPl1_PointOfCare().setValue(nullValue);
	    pv1.getPv17_AttendingDoctor(0).getXcn1_IDNumber().setValue(nullValue);
	    pv1.getPv18_ReferringDoctor(0).getXcn1_IDNumber().setValue(nullValue);
	    pv1.getPv19_ConsultingDoctor(0).getXcn1_IDNumber().setValue(nullValue);			
	    pv1.getSetIDPatientVisit().setValue("1");
	    pv1.getPatientClass().setValue("U");	    	
	    pv1.getAttendingDoctor(0).getIDNumber().setValue("mrp "+ patientData[3]);		// mrp number			
	    
		String visitDate = appointment.getDate() + " " + appointment.getTime();
		pv1.getAdmitDateTime().getTimeOfAnEvent().setValue("visit date ==" + visitDate);
		
	//	pv1.getAdmissionType().setValue(appointment.getType());
		pv1.getAdmissionType().setValue("First Visit");
		
		
		TPR tPR = tpdt_A04.getTPR();
		
    	tPR.getHealthGoals().setValue("Health goal: Loss 10 lbs in 6 months");
    	tPR.getLifeGoals().setValue("Eat healthy...");
    	tPR.getSocialContext().setValue("Suni is an 81-year-old male. He lives alone and very independent.");
    	tPR.getAlerts().setValue("Nutrition score high risk); weight changed in the past 6 months without trying");
    	tPR.getPlan1().setValue("Follow-up visit to complete addition al Tapestry screening tolls");
    	tPR.getPlan2().setValue("MRP requested to administer General Depression Scale - Score=5(Normal");
		
	
	    Parser parser = new PipeParser();
	    this.message = parser.encode(tpdt_A04);
	}
	
	 public boolean save() throws HL7Exception {
			if (this.message == null)
				this.generateA04MessageTapestryReport();	 
	        this.fileName = "hl7Report";
	 
	        String saveDir = "webapps/hl7/";
	        
	        // create HL7 A04 file
	        try {
	        	File directory = new File(saveDir);
	        	if (!directory.exists())
	        		directory.mkdir();
	        	
	        	FileWriter fw = new FileWriter(saveDir + this.fileName, true);
	        	BufferedWriter out = new BufferedWriter(fw);
	        	out.write(this.message);
	        	out.close();
	        } catch (IOException e) {
	        	System.out.println("exception:====  " +e.getMessage());
				return false;
	        }
	        
	        
	        return true;
		}
	 
	 public void populateMessageSample() throws Exception
	 {
		 ADT_A04 adt = new ADT_A04();
		 adt.initQuickstart("ADT", "A04", "P");
		    
		 // Populate the MSH Segment
		 MSH mshSegment = adt.getMSH();
		 mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
		 mshSegment.getSequenceNumber().setValue("123");
		          
		 // Populate the PID Segment
		 PID pid = adt.getPID(); 
		 pid.getPatientName().getFamilyName().setValue("Doe");
		 pid.getPatientName().getGivenName().setValue("John");
		 pid.getPatientIDExternalID().getID().setValue("123456");

		 Parser parser = new PipeParser();
		 this.message = parser.encode(adt);
	 }
	 
	 public void populateORU1Message() throws HL7Exception, Exception{
		 ORU_R01 oMessage = new ORU_R01();
		 oMessage.initQuickstart("ORU", "R01", "T");
		 		 
		// Populate the MSH Segment
		 ca.uhn.hl7v2.model.v25.segment.MSH mshSegment = oMessage.getMSH();
		 mshSegment.getSendingApplication().getNamespaceID().setValue("Tapestry App");
		 mshSegment.getSequenceNumber().setValue("12399999");		 
		 mshSegment.getFieldSeparator().setValue("|");
		 mshSegment.getEncodingCharacters().setValue("^~\\&");
		
		 mshSegment.getVersionID().getVersionID().setValue("2.5");	
				 
		 // PID Segment
		 ca.uhn.hl7v2.model.v25.segment.PID pid = oMessage.getPATIENT_RESULT().getPATIENT().getPID();
		 pid.getAlternatePatientIDPID(0).getIDNumber().setValue("1");
		 
		 pid.getAdministrativeSex().setValue("F");
				   
		 pid.getPatientID().getIDNumber().setValue("patientId--199999");
			//	 pid.getPatientName(0).getFamilyName().getSurname().setValue(patientData[0]);
		 pid.getPatientName(0).getFamilyName().getSurname().setValue("Patient Tobin");
			//	 pid.getPatientName(0).getGivenName().setValue(patientData[1]);
		 pid.getPatientName(0).getGivenName().setValue("Steve");
		 pid.getDateTimeOfBirth().getTime().setValue("19331212");
		 
		 pid.getPatientAddress(0).getStreetAddress().getStreetOrMailingAddress().setValue("175");
		 pid.getPatientAddress(0).getStreetAddress().getStreetName().setValue("Longwood S");
		 pid.getPatientAddress(0).getCity().setValue("Hamilton");		 
		 pid.getPatientAddress(0).getCountry().setValue("Canada");
		 pid.getPatientAddress(0).getStateOrProvince().setValue("Ontario");
		 pid.getPatientAddress(0).getZipOrPostalCode().setValue("L7B 3C2");
				 
		 //PV1 Segment
		 ca.uhn.hl7v2.model.v25.segment.PV1 pv = oMessage.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
		 pv.getPatientClass().setValue("U");
		 pv.getAdmissionType().setValue("Follow up");
		 pv.getAttendingDoctor(0).getIDNumber().setValue("MRP #"); //mrp
		 pv.getAdmitDateTime().getTime().setValue("20141215090000 AM");
		 
		 /*
		  * The OBR segment is contained within a group called ORDER_OBSERVATION, 
		  * which is itself in a group called PATIENT_RESULT. These groups are
		  * reached using named accessors.
		  */
		 ORU_R01_ORDER_OBSERVATION orderObservation = oMessage.getPATIENT_RESULT(0).getORDER_OBSERVATION();
		 fillOBXAndOBRField(100, "eat healthy, have good relationship with family", orderObservation, oMessage,  "TPLG");
      
		 orderObservation = oMessage.getPATIENT_RESULT(1).getORDER_OBSERVATION(); 
         fillOBXAndOBRField(101, "live healthy, lose weight and practive more", orderObservation, oMessage,  "TPHG"); 
 
         orderObservation = oMessage.getPATIENT_RESULT(2).getORDER_OBSERVATION(); 
         String[] alerts = new String[]{"Edmonton Frail Scale socores indicates high risk","High Nutritional Risk",
        		 "Minor Manifest Limitation in Walking 0.5 km"};
         fillOBXAndOBRField(102, alerts, orderObservation, oMessage, "TPCR", 3); 
         
		 Parser parser = new PipeParser();
		 String str = parser.encode(oMessage);
		 
		 this.message = parser.encode(oMessage);
		 System.out.println("Message is ===" + this.message);
		 
	 }
	 
	 public void fillOBXAndOBRField(int index, String str, ORU_R01_ORDER_OBSERVATION orderObservation, 
			 ORU_R01 message, String tagName) throws HL7Exception
	{
		 //populate OBR
		 fillOBR(index, orderObservation, tagName);
		 
		 //Populate OBX
		 ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(0);		 
		 OBX obx = observation.getOBX();
		 obx.getSetIDOBX().setValue("1");
		 TX tx = new TX(message);
         tx.setValue(str);
         
         Varies value = obx.getObservationValue(0);
         value.setData(tx);
	 }
	 
	 public void fillOBXAndOBRField(int index, String[] str, ORU_R01_ORDER_OBSERVATION orderObservation, 
			 ORU_R01 message, String tagName, int numberOfOBX) throws HL7Exception
	{
		//populate OBR
		 fillOBR(index, orderObservation, tagName);
		 
		//Populate OBX
		 ORU_R01_OBSERVATION observation;
		 OBX obx;
		 for (int i = 1; i< numberOfOBX +1; i++)
		 {
			 observation = orderObservation.getOBSERVATION(i-1);		 
			 obx = observation.getOBX();
			 obx.getSetIDOBX().setValue(String.valueOf(i));
			 TX tx = new TX(message);
	         tx.setValue(str[i-1]);
	         
	         Varies value = obx.getObservationValue(0);
	         value.setData(tx);
		 }
	}
	 
	 public void fillOBR(int index, ORU_R01_ORDER_OBSERVATION orderObservation,String tagName) throws HL7Exception
	 {
		//populate OBR
		 OBR obr = orderObservation.getOBR();
		 obr.getSetIDOBR().setValue(String.valueOf(index));
		 obr.getFillerOrderNumber().getEntityIdentifier().setValue("patientId");//patient id
		 obr.getFillerOrderNumber().getNamespaceID().setValue("Tapestry report");
		 obr.getUniversalServiceIdentifier().getIdentifier().setValue(tagName);		 //life goal
		 //end of OBR
	 }

	 
	 public void populateORUMessage() throws HL7Exception, Exception{
		 // First, a message object is constructed
		 TPR_ORU_R01 tor = new TPR_ORU_R01();
		 /*
		  * The initQuickstart method populates all of the mandatory fields in the
		  * MSH segment of the message, including the message type, the timestamp,
		  * and the control ID.
		  */
		 tor.initQuickstart("ORU", "R01", "T");
		 
		// Populate the MSH Segment
		 ca.uhn.hl7v2.model.v25.segment.MSH mshSegment = tor.getMSH();
		 mshSegment.getSendingApplication().getNamespaceID().setValue("Tapestry App");
		 mshSegment.getSequenceNumber().setValue("123");		 
		 mshSegment.getFieldSeparator().setValue("|");
		 mshSegment.getEncodingCharacters().setValue("^~\\&");
		
		 mshSegment.getVersionID().getVersionID().setValue("2.5");	
		 
		 // PID Segment
		 ca.uhn.hl7v2.model.v25.segment.PID pid = tor.getPATIENT_RESULT().getPATIENT().getPID();
		 pid.getAlternatePatientIDPID(0).getIDNumber().setValue("1");
		 
		 pid.getAdministrativeSex().setValue("F");
		   
		 pid.getPatientID().getIDNumber().setValue("patientId--199999");
	//	 pid.getPatientName(0).getFamilyName().getSurname().setValue(patientData[0]);
		 pid.getPatientName(0).getFamilyName().getSurname().setValue("Patient Tobin");
	//	 pid.getPatientName(0).getGivenName().setValue(patientData[1]);
		 pid.getPatientName(0).getGivenName().setValue("Steve");
		 pid.getDateTimeOfBirth().getTime().setValue("1933-12-12");
		 
		 pid.getPatientAddress(0).getStreetAddress().getStreetOrMailingAddress().setValue("175");
		 pid.getPatientAddress(0).getStreetAddress().getStreetName().setValue("Longwood S");
		 pid.getPatientAddress(0).getCity().setValue("Hamilton");		 
		 pid.getPatientAddress(0).getCountry().setValue("Canada");
		 pid.getPatientAddress(0).getStateOrProvince().setValue("Ontario");
		 pid.getPatientAddress(0).getZipOrPostalCode().setValue("L7B 3C2");
		 
		 //PV1 Segment
		 ca.uhn.hl7v2.model.v25.segment.PV1 pv = tor.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
		 pv.getPatientClass().setValue("U");
		 pv.getAdmissionType().setValue("Follow up");
		 pv.getAttendingDoctor(0).getIDNumber().setValue("MRP #"); //mrp
		 pv.getAdmitDateTime().getTime().setValue("2014/^Dec/^15/^09:00:00 AM");
	
		 
//		 pv1.getAdmissionType().setValue("First Visit");
			
		 TPR tPR = tor.getTPR();
			
		 tPR.getHealthGoals().setValue("Health goal: Loss 10 lbs in 6 months");
		 tPR.getLifeGoals().setValue("Eat healthy...");
		 tPR.getSocialContext().setValue("Suni is an 81-year-old male. He lives alone and very independent.");
		 tPR.getAlerts().setValue("Nutrition score high risk); weight changed in the past 6 months without trying");
		 tPR.getPlan1().setValue("Follow-up visit to complete addition al Tapestry screening tolls");
		 tPR.getPlan2().setValue("MRP requested to administer General Depression Scale - Score=5(Normal");
			
		 Parser parser = new PipeParser();
		 this.message = parser.encode(tor);
		 
	 }
	
	
}
