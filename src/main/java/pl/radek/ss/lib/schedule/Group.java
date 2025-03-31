package pl.radek.ss.lib.schedule;
import java.util.ArrayList;
import java.util.List;

public class Group
{
	// --------------------------------------------------------------------------------
	private int id;
	private String name;
	private int numberOfStudents;
	private List<EventSchedule> eventsSchedule = new ArrayList<>();
	// --------------------------------------------------------------------------------
	public Group(int id, String name, int numberOfStudents) {
		this.id = id;
		this.name = name;
		this.numberOfStudents = numberOfStudents;
	}
	
	public Group(int id, String name)
	{
		this(id, name, 0);
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
	
	public int getNumberOfStudents()
	{ 
		return numberOfStudents;
	}
	
	public void setNumberOfStudents(int numberOfStudents)
	{
		this.numberOfStudents = numberOfStudents;
	}

	public List<EventSchedule> getEventSchedule()
	{ 
		return eventsSchedule;
	}

	public void setEventSchedule(List<EventSchedule> eventsSchedule)
	{
		this.eventsSchedule = eventsSchedule;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Group)
            return ((Group) obj).getId() == this.getId();
        
		return false;
	}
	
	@Override
	public String toString() {
		return "Group [" + id + "]" + name;
	}
}
