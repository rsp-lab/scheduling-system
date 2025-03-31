package pl.radek.ss.domain;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Message
{
	public Message() { }
	
	public Message(String author, String text, Event event) {
		this.author = author;
		this.text = text;
		this.event = event;
	}
	
	public Message(String author, String text, Date date, Event event) {
		this(author, text, event);
		this.date = date;
	}

	@Id
	@GeneratedValue(generator = "message_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "message_id", sequenceName = "message_id_seq", allocationSize = 1)
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
    @Size(min = 1, max = 20)
	String author;
	public String getAuthor()
	{
		return author;
	}
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	@Basic
    @Size(max = 256)
	String text;
	public String getText()
	{
		return text;
	}
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Basic
	@DateTimeFormat(iso = ISO.DATE_TIME)
	Date date;
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
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
		if(obj instanceof Message)
            return ((Message) obj).getText().equals(this.getText());
        
		return false;
	}

	@Override
	public String toString() 
	{
		return "Message: " + this.text;
	}
}
