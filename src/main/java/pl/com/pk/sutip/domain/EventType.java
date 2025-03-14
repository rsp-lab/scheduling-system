package pl.com.pk.sutip.domain;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@XStreamAlias("eventType")
public class EventType
{
	public EventType() { }
	
	public EventType(String name, Event event)
	{
		this.name = name;
		this.event = event;
	}

	@Id
	@GeneratedValue(generator = "type_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_id", sequenceName = "type_id_seq", initialValue = 1)
	Integer id;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	@Basic
	@NotNull
	@Size(min = 4, max = 20)
	String name;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	@ManyToOne(targetEntity = Event.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", nullable = true)
	Event event;
	public Event getEvent()
	{
		return event;
	}
	public void setEvent(Event event)
	{
		this.event = event;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof EventType)
		{
			return ((EventType) obj).getName().equals(this.getName());
		}
		return false;
	}

	@Override
	public String toString() 
	{
		return "EventType: " + this.name;
	}
}
