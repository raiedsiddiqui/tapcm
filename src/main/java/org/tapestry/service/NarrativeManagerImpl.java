package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tapestry.objects.Narrative;
import org.tapestry.dao.NarrativeDAO;
/**
 * Implementation for service NarrativeManager
 * @author lxie *
 */
@Service
public class NarrativeManagerImpl implements NarrativeManager {
	@Autowired
	private NarrativeDAO narrativeDao;
	
	@Override
	public List<Narrative> getAllNarrativesByUser(int volunteerId) {
		return narrativeDao.getAllNarrativesByUser(volunteerId);
	}

	@Override
	public List<Narrative> getNarrativesByVolunteer(int volunteerId,int patientId, int appointmentId) {
		return narrativeDao.getNarrativesByVolunteer(volunteerId, patientId, appointmentId);
	}

	@Override
	public Narrative getNarrativeById(int narrativeId) {
		return narrativeDao.getNarrativeById(narrativeId);
	}

	@Override
	public void addNarrative(Narrative narrative) {
		narrativeDao.addNarrative(narrative);
	}

	@Override
	public void updateNarrative(Narrative narrative) {
		narrativeDao.updateNarrative(narrative);
	}

	@Override
	public void deleteNarrativeById(int narrativeId) {
		narrativeDao.deleteNarrativeById(narrativeId);
	}

}
