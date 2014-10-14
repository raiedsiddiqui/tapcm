package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Narrative;
/**
 * service for Model Narrative
 * @author lxie *
 */
@Service
public interface NarrativeManager {
	@Transactional
	public List<Narrative> getAllNarrativesByUser(int volunteerId);
	@Transactional
	public List<Narrative> getNarrativesByVolunteer(int volunteerId, int patientId, int appointmentId);
	@Transactional
	public Narrative getNarrativeById(int narrativeId);
	@Transactional
	public void addNarrative(Narrative narrative);
	@Transactional
	public void updateNarrative(Narrative narrative);
	@Transactional
	public void deleteNarrativeById(int narrativeId);

}
