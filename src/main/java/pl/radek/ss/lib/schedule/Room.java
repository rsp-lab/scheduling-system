package pl.radek.ss.lib.schedule;

public class Room
{
	// --------------------------------------------------------------------------------
	private static int nextRoomId = 0; 				// pole id ustawiane automatycznie
	private int id;
	private String name;
	private boolean lab;
	private int numberOfSeats;
	// --------------------------------------------------------------------------------
	public Room(String name, boolean lab, int numberOfSeats) {
		this.id = nextRoomId++;
		this.name = name;
		this.lab = lab;
		this.numberOfSeats = numberOfSeats;
	}
	
	public Room(String name) {
		this(name, false, 0);
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
	
	public boolean isLab()
	{ 
		return lab;
	}
	
	public int getNumberOfSeats()
	{ 
		return numberOfSeats;
	}
	
	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}
	
	public static void restartIDs()
	{
		nextRoomId = 0;
	}
	
	@Override
	public String toString() {
		return "Room [" + id + "]" + name;
	}
}
