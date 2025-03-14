package pl.com.pk.sutip.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.com.pk.sutip.domain.Event;
import pl.com.pk.sutip.domain.EventType;
import pl.com.pk.sutip.domain.Message;
import pl.com.pk.sutip.exception.EventNotFoundException;

@Transactional
@Repository
public class EventDaoImpl implements EventDao
{
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	EntityManager entityManager;
	// -----------------------------------------------------------------------------------------------------
	public Integer createEvent(Event event)
	{
		entityManager.persist(event);
		return event.getId();
	}
	
	public void updateEvent(Event event)
	{
		entityManager.merge(event);
	}

	public void deleteEvent(Integer id) throws EventNotFoundException
	{
		Event event = findEvent(id);
		entityManager.remove(event);
	}

	public Event findEvent(Integer id) throws EventNotFoundException
	{
		try
		{
			return entityManager.find(Event.class, id);
		}
		catch(NoResultException nre)
		{
			throw new EventNotFoundException();
		}
	}
	
	public List<Event> findEvent(String name, String eventType, String username)
	{
		Query query = null;
		if(username == null)
		{
			String queryDB = QueryDB.FIND_EVENT;
			queryDB = queryDB.replaceAll("#eventType", eventType);
			queryDB = queryDB.replaceAll("#name", "'%" + name.toUpperCase() + "%'");
			query = entityManager.createQuery(queryDB);
		}
		if(username != null)
		{
			String queryDB = QueryDB.FIND_EVENT_WITH_USERNAME;
			queryDB = queryDB.replaceAll("#eventType", eventType);
			queryDB = queryDB.replaceAll("#name", "'%" + name.toUpperCase() + "%'");
			queryDB = queryDB.replaceAll("#username", "'%" + username + "%'");
			query = entityManager.createQuery(queryDB);
		}
		return query.getResultList();
	}
	
	public Event findEventByLink(String link) throws EventNotFoundException
	{
		try
		{
			String queryDB = QueryDB.FIND_EVENT_BY_LINK;
			queryDB = queryDB.replaceAll("#link",  "'" + link + "'");
			Query query = entityManager.createQuery(queryDB);
			return (Event) query.getSingleResult();
		}
		catch(NoResultException nre)
		{
			throw new EventNotFoundException();
		}
	}
	public List<Event> getEvents(Integer limit, String orderBy, String orderType, String eventType, String username)
	{
		Query query = null;
		if(username == null)
		{
			String queryDB = QueryDB.GET_EVENTS;
			queryDB = queryDB.replaceAll("#eventType", eventType);
			queryDB = queryDB.replaceAll("#orderBy", orderBy);
			queryDB = queryDB.replaceAll("#orderType", orderType);
			query = entityManager.createQuery(queryDB);
		}
		if(username != null)
		{
			String queryDB = QueryDB.GET_EVENTS_WITH_USERNAME;
			queryDB = queryDB.replaceAll("#eventType", eventType);
			queryDB = queryDB.replaceAll("#orderBy", orderBy);
			queryDB = queryDB.replaceAll("#orderType", orderType);
			queryDB = queryDB.replaceAll("#username", "'%" + username + "%'");
			query = entityManager.createQuery(queryDB);
		}
		return query.setMaxResults(limit).getResultList();
	}

	public EventType findEventType(Integer id)
	{
//		Event event = findEvent(id);
//		List<EventType> types = findEventTypes(event.getId());
//		for (EventType eventType : types)
//		{
//			entityManager.remove(eventType);
//		}
		Query query = entityManager.createQuery(QueryDB.FIND_TYPE);
		query.setParameter("id", id);
		return (EventType) query.getSingleResult();
	}
	
	public void updateEventType(EventType type)
	{
		entityManager.merge(type);
	}

	public void updateMessage(Message message)
	{
		entityManager.merge(message);
	}
	// -----------------------------------------------------------------------------------------------------
}
