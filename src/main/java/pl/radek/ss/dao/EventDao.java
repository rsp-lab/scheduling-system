package pl.radek.ss.dao;

import pl.radek.ss.domain.Event;
import pl.radek.ss.domain.EventType;
import pl.radek.ss.domain.Message;
import pl.radek.ss.exception.EventNotFoundException;

import java.util.List;

public interface EventDao
{
	// -----------------------------------------------------------------------------------------------------
	Integer createEvent(Event event);
	void updateEvent(Event event);
	void updateMessage(Message message);
	void deleteEvent(Integer id) throws EventNotFoundException;
	EventType findEventType(Integer id) throws EventNotFoundException;
	void updateEventType(EventType type);
	Event findEvent(Integer id) throws EventNotFoundException;
	Event findEventByLink(String link) throws EventNotFoundException;
	List<Event> findEvent(String name, String eventType, String username);
	List<Event> getEvents(Integer limit, String orderBy, String orderType, String eventType, String username);
	// -----------------------------------------------------------------------------------------------------
} 