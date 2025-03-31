package pl.radek.ss.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Participation
{
	public Participation() { }
	
	public Participation(String username, String contact) {
		this.username = username;
		this.contact = contact;
	}
	
	public Participation(String username, String contact, Event event) {
		this(username, contact);
		this.event = event;
	}
	
	public Participation(String username, String contact, Event event, List<Interval> intervals) {
		this(username, contact);
		this.event = event;
		this.intervals = intervals;
	}
	
	@Id
	@GeneratedValue(generator = "participation_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "participation_id", sequenceName = "participation_id_seq", allocationSize = 1)
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
    @Size(min = 1, max = 20)
	String username;
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	@Basic
    @Size(max = 64)
	String contact;
	public String getContact()
	{
		return contact;
	}
	public void setContact(String contact)
	{
		this.contact = contact;
	}
	
	@ManyToOne(targetEntity = Event.class, cascade = CascadeType.ALL)
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
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "participation", targetEntity = Interval.class, fetch = FetchType.LAZY)
	List<Interval> intervals = new ArrayList<Interval>();
	public List<Interval> getIntervals()
	{
		return intervals;
	}
	public void setIntervals(List<Interval> intervals) 
	{
		this.intervals = intervals;
	}
	public void addIntervals(List<Interval> intervals) {
		for (Interval interval : intervals) {
			interval.setParticipation(this);
			this.intervals.add(interval);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Participation)
            return ((Participation) obj).getId().equals(this.getId());
        
		return false;
	}

	@Override
	public String toString() 
	{
		return "Participation: " + this.id;
	}
}
