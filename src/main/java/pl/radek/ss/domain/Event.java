package pl.radek.ss.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@XStreamAlias("event")
public class Event
{
	public Event() { }
	
	public Event(String startDate, String startTimestamp, String endDate, String endTimestamp) {
		this.setStartDate(startDate);
		this.setStartTimestamp(startTimestamp);
		this.setEndDate(endDate);
		this.setEndTimestamp(endTimestamp);
	}
	
	public Event(String name, String username, String contact, String link, String startDate, String startTimestamp, String endDate, String endTimestamp, Account account) {
		this(startDate, startTimestamp, endDate, endTimestamp);
		this.setName(name);
		this.setUsername(username);
		this.setContact(contact);
		this.setLink(link);
		this.setAccount(account);
	}

	@Id
	@GeneratedValue(generator = "event_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "event_id", sequenceName = "event_id_seq", allocationSize = 1)
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
    @Size(min = 1, max = 48, message="The event name length must be between 1 and 48 characters!")
	String name;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Basic
    @Size(max = 256, message="The event description length must be a maximum of 256 characters!")
	String description;
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Basic
    @Size(min = 1, max = 64, message="Location must be between 1 and 64 characters!")
	String location;
	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	@Basic
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	@Size(max = 4, message="Duration length must be between 1 and 4 characters!")
	String duration;
	public String getDuration()
	{
		return duration;
	}
	public void setDuration(String duration)
	{
		this.duration = duration;
	}
	
	@Basic
	@Column(name="creation_date")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	Date creationDate = new Date();
	public Date getCreationDate()
	{
		return creationDate;
	}
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	@Basic
    @Size(max = 64)
	String link;
	public String getLink()
	{
		return link;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	
	@Basic
	String latitude;
	public String getLatitude()
	{
		return latitude;
	}
	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}
	
	@Basic
	String longitude;
	public String getLongitude()
	{
		return longitude;
	}
	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
	
	@Basic
	@Column(name="participant_counter")
	Integer participantCounter = 0;
	public Integer getParticipantCounter()
	{
		return participantCounter;
	}
	public void setParticipantCounter(Integer participantCounter) {
		this.participantCounter = participantCounter;
	}
	
	@Basic
	@Pattern(regexp="^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$")
	// @Size(max = 10)
	String startDate;
	public String getStartDate()
	{
		return startDate;
	}
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	
	@Basic
	@Pattern(regexp="([01]?[0-9]|2[0-3]):[0-5][0-9]")
	// @Size(max = 5)
	String startTimestamp;
	public String getStartTimestamp()
	{
		return startTimestamp;
	}
	public void setStartTimestamp(String startTimestamp)
	{
		this.startTimestamp = startTimestamp;
	}
	
	@Basic
	@Pattern(regexp="^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$")
	// @Size(max = 10)
	String endDate;
	public String getEndDate()
	{
		return endDate;
	}
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	
	@Basic
	@Pattern(regexp="([01]?[0-9]|2[0-3]):[0-5][0-9]")
	// @Size(max = 5)
	String endTimestamp;
	public String getEndTimestamp()
	{
		return endTimestamp;
	}
	public void setEndTimestamp(String endTimestamp)
	{
		this.endTimestamp = endTimestamp;
	}
	
	@Basic
	@Size(min = 1, max = 20, message="Username length must be between 1 and 20 characters!")
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
	@Size(max = 64, message="The email length must be a maximum of 64 characters!")
	String contact;
	public String getContact()
	{
		return contact;
	}
	public void setContact(String contact)
	{
		this.contact = contact;
	}
	
	@Transient
	String type;
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
	@ManyToOne(targetEntity = Account.class)
	@JoinColumn(name = "account_id", nullable = true)
	Account account;
	public Account getAccount()
	{
		return account;
	}
	public void setAccount(Account account)
	{
		this.account = account;
	}
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "event", targetEntity = EventType.class, fetch = FetchType.LAZY)
	List<EventType> types = new ArrayList<EventType>();
	public List<EventType> getEventTypes()
	{
		return types;
	}
	public void setEventTypes(List<EventType> types) 
	{
		this.types = types;
	}
	public void addEventType(EventType type)
	{
		this.types.add(type);
	}
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "event", targetEntity = Message.class, fetch = FetchType.LAZY)
	List<Message> messages = new ArrayList<Message>();
	public List<Message> getMessages()
	{
		return messages;
	}
	public void setMessages(List<Message> messages) 
	{
		this.messages = messages;
	}
	public void addMessage(Message message)
	{
		this.messages.add(message);
	}
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "event", targetEntity = Participation.class, fetch = FetchType.LAZY)
	List<Participation> participations = new ArrayList<Participation>();
	public List<Participation> getParticipations()
	{
		return participations;
	}
	public void setParticipations(List<Participation> participations) 
	{
		this.participations = participations;
	}
	public void addParticipations(List<Participation> participations)
	{
		for (Participation par : participations)
            this.participations.add(par);
	}
	public void addParticipation(Participation participation)
	{
		this.participations.add(participation);
	}
}
