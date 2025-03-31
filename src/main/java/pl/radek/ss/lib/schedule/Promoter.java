package pl.radek.ss.lib.schedule;
import java.util.ArrayList;
import java.util.List;

public class Promoter
{
	// --------------------------------------------------------------------------------
	private int id;
	private String name;
	private List<EventSchedule> eventsSchedule = new ArrayList<>();
	// --------------------------------------------------------------------------------
	public Promoter(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Promoter(String name)
	{
		this(0, name);
	}

	public void AddEventSchedule(EventSchedule eventSchedule)
	{
		this.eventsSchedule.add(eventSchedule);
	}
	// --------------------------------------------------------------------------------
	public int getId()
	{ 
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getName() 
	{ 
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public List<EventSchedule> getEventsSchedule()
	{ 
		return eventsSchedule;
	}

	public void setEventsSchedule(List<EventSchedule> eventsSchedule)
	{
		this.eventsSchedule = eventsSchedule;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Promoter)
            return ((Promoter) obj).getId() == this.getId();
        
		return false;
	}
	
	@Override
	public String toString() {
		return "Promoter [" + id + "]" + name;
	}
}
