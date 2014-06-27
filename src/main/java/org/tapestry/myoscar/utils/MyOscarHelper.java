package org.tapestry.myoscar.utils;

import java.util.List;
import java.util.Locale;
import java.net.*;
//import org.junit.Test;
import org.oscarehr.myoscar.client.ws_manager.AccountManager;
import org.oscarehr.myoscar.client.ws_manager.MedicalDataManager;
import org.oscarehr.myoscar.commons.MedicalDataType;
import org.oscarehr.myoscar_server.ws.LoginResultTransfer3;
import org.oscarehr.myoscar_server.ws.MedicalDataTransfer4;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.oscarehr.util.MiscUtils;


public class MyOscarHelper {
	
	public void example() throws Exception
	{
		System.out.println("starting in helper class... before calling MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates()");
		//--- this allows self signed https certificates ---
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
		System.out.println("...after calling MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates()");		
		//--- this will log in and instantiate a credential object for subsequent calls ---
		String serverUrl="https://127.0.0.1:8091/myoscar_server";
		String user="admin";
		String password="admin";
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);
		PersonTransfer3 p3 = loginResultTransfer.getPerson();
		System.out.println("login user first name  is  === " + p3.getFirstName());
		System.out.println("login user's Birthday is  === " + p3.getBirthDate());
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);
		
		
		//--- this will retrieve an account and print some of the patients demographic information ---
		String patientsUserName="patient";
		PersonTransfer3 samplePatient=AccountManager.getPerson(credentials, patientsUserName);
		
		
		System.out.println("Patient's first name  is  === " + samplePatient.getFirstName());
		System.out.println("Patient's Birthday is  === " + samplePatient.getBirthDate());
		
		
		System.err.println("samplePatient = "+samplePatient.getId()+", "+samplePatient.getUserName()+", "+samplePatient.getFirstName()+", "+samplePatient.getEmailAddress());
		

		//--- this will get a list of the patients height weight measurements and print them ---
		String medicalDataType=MedicalDataType.HEIGHT_AND_WEIGHT.name();
		List<MedicalDataTransfer4> medicalDataList=MedicalDataManager.getMedicalData(credentials, samplePatient.getId(), medicalDataType, true, 0, 99);
		
		for (MedicalDataTransfer4 medicalData : medicalDataList)
		{
			System.err.println("sample data = "+medicalData.getId()+", "+medicalData.getDateOfData()+", "+medicalData.getData());
		}
		
		
	}
	
	public static boolean exists(String URLName){
		try 
		{
			HttpURLConnection.setFollowRedirects(false);
		    // note : you may also need
			//HttpURLConnection.setInstanceFollowRedirects(false);
		    HttpURLConnection con =
		       (HttpURLConnection) new URL(URLName).openConnection();
		    con.setRequestMethod("HEAD");
		    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		    }
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/*
	public static boolean existsByOtherWay(String URLName){
		  try {

		    Properties systemSettings = System.getProperties();
		    systemSettings.put("proxySet", "true");
		    systemSettings.put("http.proxyHost","proxy.mycompany.local") ;
		    systemSettings.put("http.proxyPort", "80") ;

		    URL u = new URL(URLName);
		    HttpURLConnection con = (HttpURLConnection) u.openConnection();
		    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		    String encodedUserPwd =
		         encoder.encode("domain\\username:password".getBytes());
		    con.setRequestProperty
		         ("Proxy-Authorization", "Basic " + encodedUserPwd);
		    con.setRequestMethod("HEAD");
		    System.out.println
		         (con.getResponseCode() + " : " + con.getResponseMessage());
		    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		    }
		  catch (Exception e) {
		       e.printStackTrace();
		       return false;
		       }
		  }
*/
}
