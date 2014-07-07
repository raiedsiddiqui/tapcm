package org.tapestry.myoscar.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.net.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
//import org.junit.Test;
import org.oscarehr.myoscar.client.ws_manager.AccountManager;
import org.oscarehr.myoscar.client.ws_manager.GroupManager;
//import org.oscarehr.myoscar_server.ws.transfer_objects.PeopleGroupTransfer;
import org.oscarehr.myoscar_server.ws.PeopleGroupTransfer;
import org.oscarehr.myoscar.client.ws_manager.MedicalDataManager;
import org.oscarehr.myoscar.commons.MedicalDataType;
import org.oscarehr.myoscar_server.ws.LoginResultTransfer3;
import org.oscarehr.myoscar_server.ws.MedicalDataTransfer4;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.myoscar.client.utils.*;
import org.oscarehr.myoscar.client.ws_manager.GroupManager;;


public class MyOscarHelper {
	
	public void example() throws Exception
	{		
		//--- this allows self signed https certificates ---
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
		
		//--- this will log in and instantiate a credential object for subsequent calls ---
//		String serverUrl="https://127.0.0.1:8091/myoscar_server";
//		String user="admin";
//		String password="admin";
		
		String serverUrl="https://maple.myoscar.org:8443/myoscar_server";		
		String user="linda.tapestry";
		String password="linda2006";
		Long groupId = new Long(16);
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);		
	
		List<Long> patientIds = GroupManager.getMembersByPeopleGroupId(credentials, groupId, 0, 100);
		List<PersonTransfer3> patients = new ArrayList<PersonTransfer3>();
		PersonTransfer3 person;
		if (patientIds != null)
		{
			 for(Long id: patientIds)
			 {
				 person = new PersonTransfer3();
				 person = AccountManager.getPerson(credentials, id);
				 
				 patients.add(person);
				 
				 System.out.println("patient's name is === " + person.getFirstName() + " " + person.getLastName() );
				 System.out.println("patient's phone number is === " + person.getPhone1() );
			 }
		}
		else
			System.out.println("size of pList is === 00000000000000000" );
	
		
		
		
		//--- this will retrieve an account and print some of the patients demographic information ---
//		String patientsUserName="carolchou.test";
//		PersonTransfer3 patient=AccountManager.getPerson(credentials, patientsUserName);	
//		Calendar birthday = patient.getBirthDate();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");	
//		System.out.println("Patient's DOB is ===" + sdf.format(birthday.getTime()));
//		
//		List<PersonTransfer3> patients = AccountManager.getPeopleByRole(credentials,"patient",true,0,3);
//		
//		if (patients != null)
//			System.out.println("size of pa === "+ patients.size());
//		System.out.println("samplePatient, city,  phone, email  = "+patient.getCity()+", "+patient.getPhone1()+", "+patient.getEmailAddress());
	

//		System.err.println("Medical Data begin ===================");
//
//		//--- this will get a list of the patients height weight measurements and print them ---
//		String medicalDataType=MedicalDataType.HEIGHT_AND_WEIGHT.name();
//		List<MedicalDataTransfer4> medicalDataList=MedicalDataManager.getMedicalData(credentials, patient.getId(), medicalDataType, true, 0, 99);
//		
//		
//		for (MedicalDataTransfer4 medicalData : medicalDataList)
//		{			
//			System.err.println("sample data = "+medicalData.getId()+", "+medicalData.getDateOfData()+", "+medicalData.getData());
//			System.out.println("sample data = "+medicalData.getId()+", "+medicalData.getDateOfData()+", "+medicalData.getData());
//		}
//		
//		System.err.println("Medical Data done ===================");
	}	
}
