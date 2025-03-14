package pl.com.pk.sutip.dao;

import pl.com.pk.sutip.domain.Participation;
import pl.com.pk.sutip.exception.ParticipationNotFoundException;

public interface ParticipationDao
{	
	// -----------------------------------------------------------------------------------------------------
	public Participation findParticipation(Integer id) throws ParticipationNotFoundException;
	public Integer updateParticipation(Participation accountEvent);
	// -----------------------------------------------------------------------------------------------------
}
