package pl.radek.ss.lib.schedule;

public class Course
{
	// --------------------------------------------------------------------------------
	private int id;
	private String name;
	// --------------------------------------------------------------------------------
	public Course(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Course(String name)
	{
		this(0, name);
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Course)
            return ((Course) obj).getId() == this.getId();
        
		return false;
	}
	
	@Override
	public String toString()
	{
		return "Course [" + id + "]" + name;
	}
}
