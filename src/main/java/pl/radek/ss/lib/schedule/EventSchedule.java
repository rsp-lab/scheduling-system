package pl.radek.ss.lib.schedule;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EventSchedule
{
	// --------------------------------------------------------------------------------
	public static int eventScheduleId = 0;
	private int id;
	private	Promoter promoter;											
	private Course course;													
	private List<Group> groups = new ArrayList<Group>();	
	private int numberOfSeats;												
	private boolean requiresLab;											
	private int duration;													
	// --------------------------------------------------------------------------------
	public EventSchedule(boolean requiresLab, int duration) {
		this.id = eventScheduleId++;
		this.requiresLab = requiresLab;
		this.duration = duration;
	}
	
	public EventSchedule(Promoter promoter, Course course, List<Group> groups, boolean requiresLab, int duration) {
		this.id = eventScheduleId++;
		this.promoter = promoter;
		this.course = course;
		this.numberOfSeats = 0;
		this.requiresLab = requiresLab;
		this.duration = duration;
		 
		promoter.AddEventSchedule(this);

		// Assigns a student group to a class
		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group studentsGroup = iterator.next();
			studentsGroup.AddEventSchedule(this);
			this.groups.add(studentsGroup);
			numberOfSeats += studentsGroup.getNumberOfStudents();
		}
	}
	
	public EventSchedule(Promoter promoter, Course course, int duration) {
		this.promoter = promoter;
		this.course = course;
		this.duration = duration;
	}
	
	public boolean groupsOverlap(EventSchedule eventSchedule) {
		for (Iterator<Group> it1 = groups.iterator(); it1.hasNext();) {
			Group studentsGroupIt1 = it1.next();
			for (Iterator<Group> it2 = eventSchedule.getGroups().iterator(); it2.hasNext();) {
				Group studentsGroupIt2 = it2.next();
				if( studentsGroupIt1.equals(studentsGroupIt2) )
                    return true;
			}
		}
		return false;
	}

	public boolean promoterOverlaps(EventSchedule eventSchedule) {
		return promoter.equals(eventSchedule.promoter); 
	}

	// --------------------------------------------------------------------------------
	public Promoter getPromoter() 
	{ 
		return promoter;
	}
	
	public void setPromoter(Promoter promoter)
	{
		this.promoter = promoter;
	}
	
	public Course getCourse()
	{ 
		return course;
	}

	public void setCourse(Course course)
	{
		this.course = course;
	}
	
	public List<Group> getGroups() 
	{ 
		return groups;
	}
	
	public void setGroups(List<Group> groups)
	{
		this.groups = groups;
	}

	public int getNumberOfSeats()
	{
		return numberOfSeats;
	}
	
	public void setNumberOfSeats(int numberOfSeats)
	{
		this.numberOfSeats = numberOfSeats;
	}

	public boolean isLabRequired() 
	{
		return requiresLab; 
	}

	public int getDuration()
	{ 
		return duration;
	}
	
	public void setDuration(int duration)
	{
		this.duration = duration;
	}
	
	public int getId()
	{ 
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "EventSchedule [" + promoter.getName() + ", " + course.getName() + ", " + numberOfSeats + ", " + duration + "]";
	}
}
