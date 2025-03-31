package pl.radek.ss.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.radek.ss.domain.Participation;
import pl.radek.ss.exception.ParticipationNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Transactional
@Repository
public class ParticipationDaoImpl implements ParticipationDao
{	
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	EntityManager entityManager;
	// -----------------------------------------------------------------------------------------------------
	public void createParticipation(Participation participation)
	{
		entityManager.persist(participation);
	}
	
	public Participation findParticipation(Integer id) throws ParticipationNotFoundException {
		try	{
			return entityManager.find(Participation.class, id);
		}
		catch(NoResultException nre) {
			throw new ParticipationNotFoundException();
		}
	}

	public Integer updateParticipation(Participation participation) {
		entityManager.merge(participation);
		return participation.getId();
	}

	public Participation findParticipationByEventId(Integer id) throws ParticipationNotFoundException {
		try {
			Query query = entityManager.createQuery(QueryDB.FIND_PARTICIPATION);
			query.setParameter("id", id);
			return (Participation) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new ParticipationNotFoundException();
		}
	}

	public List<Participation> getParticipants(Integer limit, String orderBy, String orderType) {
		String queryDB = QueryDB.GET_PARTICIPANTS;
		queryDB = queryDB.replace("#orderBy", orderBy);
		queryDB = queryDB.replace("#orderType", orderType);
		Query query = entityManager.createQuery(queryDB);
		return query.setMaxResults(limit).getResultList();
	}
	// -----------------------------------------------------------------------------------------------------
}
