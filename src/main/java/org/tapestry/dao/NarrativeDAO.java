package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Narrative;

/**
 * Defines DAO operations for the Narrative model.
 * 
 * @author lxie *
 */
public interface NarrativeDAO {
	public List<Narrative> getAllNarrativesByUser(int volunteerId);
//	public List<Narrative> getAllNarrativesByUser(int volunteerId, int patientId);
	public List<Narrative> getNarrativesByVolunteer(int volunteerId, int patientId, int appointmentId);
	public Narrative getNarrativeById(int narrativeId);
	public void addNarrative(Narrative narrative);
	public void updateNarrative(Narrative narrative);
	public void deleteNarrativeById(int narrativeId);
}
