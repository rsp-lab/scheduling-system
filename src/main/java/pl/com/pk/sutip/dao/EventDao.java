package pl.com.pk.sutip.dao;

import java.util.List;

import pl.com.pk.sutip.domain.Event;
import pl.com.pk.sutip.domain.EventType;
import pl.com.pk.sutip.domain.Message;
import pl.com.pk.sutip.exception.EventNotFoundException;

public interface EventDao
{
	// -----------------------------------------------------------------------------------------------------
	public Integer createEvent(Event event);
	public void updateEvent(Event event);
	public void updateMessage(Message message);
	public void deleteEvent(Integer id) throws EventNotFoundException;
	public EventType findEventType(Integer id);
	public void updateEventType(EventType type);
	public Event findEvent(Integer id) throws EventNotFoundException;
	public Event findEventByLink(String link) throws EventNotFoundException;
	public List<Event> findEvent(String name, String eventType, String username);
	public List<Event> getEvents(Integer limit, String orderBy, String orderType, String eventType, String username);
	// -----------------------------------------------------------------------------------------------------
} 