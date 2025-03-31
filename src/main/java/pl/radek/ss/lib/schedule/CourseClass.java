package pl.radek.ss.lib.schedule;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CourseClass
{
	// --------------------------------------------------------------------------------
	public static int courseClassId = 0;
	private int id;
	private	Professor professor;											
	private Course course;													
	private List<Group> groups = new ArrayList<Group>();	
	private int numberOfSeats;												
	private boolean requiresLab;											
	private int duration;													
	// --------------------------------------------------------------------------------
	public CourseClass(boolean requiresLab, int duration) {
		this.id = courseClassId++;
		this.requiresLab = requiresLab;
		this.duration = duration;
	}
	
	public CourseClass(Professor professor, Course course, List<Group> groups, boolean requiresLab, int duration) {
		this.id = courseClassId++;
		this.professor = professor;
		this.course = course;
		this.numberOfSeats = 0;
		this.requiresLab = requiresLab;
		this.duration = duration;
		 
		professor.AddCourseClass(this);

		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group studentsGroup = iterator.next();
			// TODO unnecessary?
            // studentsGroup.AddClass(this);
			this.groups.add(studentsGroup);
			numberOfSeats += studentsGroup.getNumberOfStudents();
		}
	}
	
	public CourseClass(Professor professor, Course course, int duration) {
		this.professor = professor;
		this.course = course;
		this.duration = duration;
	}
	
	public boolean groupsOverlap(CourseClass courseClass) {
		for (Iterator<Group> it1 = groups.iterator(); it1.hasNext();) {
			Group studentsGroupIt1 = (Group) it1.next();
			for (Iterator<Group> it2 = courseClass.getGroups().iterator(); it2.hasNext();) {
				Group studentsGroupIt2 = (Group) it2.next();
				if( studentsGroupIt1.equals(studentsGroupIt2) )
                    return true;
			}
		}
		return false;
	}

	public boolean professorOverlaps(CourseClass courseClass) 
	{ 
		return professor.equals(courseClass.professor); 
	}

	// --------------------------------------------------------------------------------
	public Professor getProfessor() 
	{ 
		return professor;
	}
	
	public void setProfessor(Professor professor)
	{
		this.professor = professor;
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
		return "CourseClass [" + professor.getName() + ", " + course.getName() + ", " + numberOfSeats + ", " + duration + "]";
	}
}
