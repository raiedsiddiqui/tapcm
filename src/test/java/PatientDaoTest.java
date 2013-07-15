package org.tapestry.tests;

import org.tapestry.dao.PatientDao;
import org.tapestry.objects.Patient;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
* This class tests the PatientDao to see if an object can be created,
* edited, saved, and retrieved.
*/
public class PatientDaoTest{

	private final String DB = "jdbc:mysql://localhost";
	private final String UN = "root";
	private final String PW = "root";

	@Test
	public void testGetById(){
		PatientDao dao = new PatientDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		Patient p = dao.getPatientById(1);
		assertNotNull("Patient is null", p);
		System.out.println("Patient with ID 1 is: " + p.getFirstName() + " " + p.getLastName());
	}

	@Test
	public void testGetAllPatients(){
		PatientDao dao = new PatientDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<Patient> patients = dao.getAllPatients();
		assertNotNull("No patients returned", patients);
		System.out.println("Patients:");
		for (Patient p : patients){
			System.out.println("| " + p.getPatientId() + " | "  + p.getFirstName() + " | " + p.getLastName() + " |");
		}
	}

}
