package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Patient;

/**
 * Defines DAO operations for the Patient model.
 * 
 * @author lxie 
*/

public interface PatientDAO {
	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getPatientByID(int id);
	
	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getNewestPatient();
	
	/**
	* List all the patients in the database
	* @return An ArrayList of Patient objects
	*/
    public List<Patient> getAllPatients();
    
    /**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The ID of the volunteer
	* @return An ArrayList of Patient objects
	*/
    public List<Patient> getPatientsForVolunteer(int volunteer);
    
    /**
	 * 
	 * get all patients with partialName in firstname or lastname
	 * @param partialName
	 * @return
	 */
	public List<Patient> getPatientssByPartialName(String partialName);
	
	/**
	* Saves a patient in the database
	* @param p The Patient object to save
	*/
    public void createPatient(Patient p);
    
    /**
     * Changes a patient in the database
     * @param p The Patient object containing the new data (should have the same ID as the patient to replace)
     */
    public void updatePatient(Patient p);
    
    /**
	* delete the specified patient from the database
	* @param id The ID of the patient to remove
	*/
	public void deletePatientWithId(int id);
}
