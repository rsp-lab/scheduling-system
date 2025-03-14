package pl.com.pk.sutip.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.com.pk.sutip.domain.Interval;
import pl.com.pk.sutip.exception.IntervalNotFoundException;

@Transactional
@Repository
public class IntervalDaoImpl implements IntervalDao
{	
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	EntityManager entityManager;
	// -----------------------------------------------------------------------------------------------------
	public Interval findInterval(Integer id) throws IntervalNotFoundException
	{
		try
		{
			return entityManager.find(Interval.class, id);
		}
		catch(NoResultException nre)
		{
			throw new IntervalNotFoundException();
		}
	}

	public void updateInterval(Interval interval)
	{
		entityManager.merge(interval);
	}

	public Interval findIntervalByAccountEventId(Integer id) throws IntervalNotFoundException
	{
		try
		{
			Query query = entityManager.createQuery(QueryDB.FIND_INTERVAL);
			query.setParameter("id", id);
			return (Interval) query.getSingleResult();
		}
		catch(NoResultException nre)
		{
			throw new IntervalNotFoundException();
		}
	}
	// -----------------------------------------------------------------------------------------------------
}
