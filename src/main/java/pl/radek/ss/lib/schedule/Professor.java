package pl.radek.ss.lib.schedule;
import java.util.ArrayList;
import java.util.List;

public class Professor
{
	// --------------------------------------------------------------------------------
	private int id;
	private String name;
	private List<CourseClass> courseClasses = new ArrayList<>();
	// --------------------------------------------------------------------------------
	public Professor(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Professor(String name)
	{
		this(0, name);
	}

	public void AddCourseClass(CourseClass courseClass)
	{
		this.courseClasses.add(courseClass);
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
	
	public List<CourseClass> getCourseClasses()
	{ 
		return courseClasses;
	}

	public void setCourseClasses(List<CourseClass> courseClasses)
	{
		this.courseClasses = courseClasses;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Professor)
            return ((Professor) obj).getId() == this.getId();
        
		return false;
	}
	
	@Override
	public String toString() {
		return "Professor [" + id + "]" + name;
	}
}
