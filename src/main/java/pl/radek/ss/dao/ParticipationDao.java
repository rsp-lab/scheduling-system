package pl.radek.ss.dao;

import pl.radek.ss.domain.Participation;
import pl.radek.ss.exception.ParticipationNotFoundException;

public interface ParticipationDao
{	
	// -----------------------------------------------------------------------------------------------------
	Participation findParticipation(Integer id) throws ParticipationNotFoundException;
	Integer updateParticipation(Participation accountEvent);
	// -----------------------------------------------------------------------------------------------------
}
