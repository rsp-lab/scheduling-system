package pl.radek.ss.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.radek.ss.domain.Interval;
import pl.radek.ss.exception.IntervalNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Transactional
@Repository
public class IntervalDaoImpl implements IntervalDao
{	
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	private EntityManager entityManager;
    // -----------------------------------------------------------------------------------------------------
	public Interval findInterval(Integer id) throws IntervalNotFoundException {
		try	{
			return entityManager.find(Interval.class, id);
		}
		catch(NoResultException nre) {
			throw new IntervalNotFoundException();
		}
	}

	public void updateInterval(Interval interval)
	{
		entityManager.merge(interval);
	}

	public Interval findIntervalByAccountEventId(Integer id) throws IntervalNotFoundException {
		try	{
			Query query = entityManager.createQuery(QueryDB.FIND_INTERVAL);
			query.setParameter("id", id);
			return (Interval) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new IntervalNotFoundException();
		}
	}
	// -----------------------------------------------------------------------------------------------------
}
