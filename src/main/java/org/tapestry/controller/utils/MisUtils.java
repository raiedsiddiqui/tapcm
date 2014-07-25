package org.tapestry.controller.utils;

import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.controller.Utils;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Patient;
import org.tapestry.dao.PatientDao;

public class MisUtils {

	public static String getMyOscarAuthenticationInfo(){
		String info = "MyOscar offers you the opportunity to communicate electronically with your healthy care team, as well as store personal"
				+ "health information online. Thansmitting private health information poses risk of which you should be aware. You should not"
				+ " agree to use MyOscar to communicate with your health care team without understanding and accepting these risk. The terms "
				+ "and conditions for using MyOscar include, but are not limited to the following: /n"
				+ "1. MyOscar makes every reasonable attempt to protect your privacy and security Messages sent through MyOscar are far more"
				+ "secure than regular email. ";
		
		return info;
	}
	
	//all patient's info are from tapestry DB + myoscar DB
	public static List<Patient> getAllPatientsWithFullInfos(PatientDao patientDao, SecurityContextHolderAwareRequestWrapper request){
				
		List<Patient> patients = patientDao.getAllPatients();
		HttpSession session = request.getSession();
		
		if (session.getAttribute("allPatientWithFullInfos") != null)
			patients = (List<Patient>)session.getAttribute("allPatientWithFullInfos");
		else
		{
			int age;				
			
			try {			
				List<PersonTransfer3> patientsInMyOscar = ClientManager.getClients();
				
				for(PersonTransfer3 person: patientsInMyOscar)
				{	
					age = Utils.getAgeByBirthDate(person.getBirthDate());
					
					for(Patient p: patients)
					{
						if (person.getUserName().equals(p.getUserName()))
						{
							Calendar birthDate = person.getBirthDate();						
							if (birthDate != null)
								p.setBod(Utils.getDateByCalendar(birthDate));
							
							p.setAge(age);
							p.setCity(person.getCity());					
							p.setHomePhone(person.getPhone1());		
							if (person.getStreetAddress1() != null)
								p.setAddress(person.getStreetAddress1());
							else if(person.getStreetAddress2() != null)
								p.setAddress(person.getStreetAddress2());
							
							break;
						}
					}
				}
				
				
			} catch (Exception e) {
				System.out.println("something wrong when calling myoscar server...");			
				e.printStackTrace();
			}
			
			session.setAttribute("allPatientWithFullInfos", patients);
		}
		
		
		return patients;
	}
}
