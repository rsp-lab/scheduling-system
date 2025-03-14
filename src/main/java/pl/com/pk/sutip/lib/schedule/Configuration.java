package pl.com.pk.sutip.lib.schedule;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Configuration
{
	// --------------------------------------------------------------------------------
	private static Configuration instance = null;
	public Map<Integer, Promoter> promoters = new HashMap<Integer, Promoter>();
	public Map<Integer, Group> groups = new HashMap<Integer, Group>();
	public Map<Integer, Course> courses = new HashMap<Integer, Course>();
	public Map<Integer, Room> rooms = new HashMap<Integer, Room>();
	public List<EventSchedule> eventsSchedule = new ArrayList<EventSchedule>();
	// --------------------------------------------------------------------------------
	public static Configuration getInstance() 
	{ 
		if( instance == null )
		{
			instance = new Configuration();
		}
		return instance;
	}

	// ustawia konfiguracj� na podstawie danych xml
	public void fromXML(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		Room.restartIDs();
		clearAll();
		
		DefaultHandler mHandler = new DefaultHandler()
		{
			private int professorId;
			private String professorName;
			private int courseId;
			private String courseName;
			private boolean roomLab;
			private String roomName;
			private int roomNrSeats;
			private int groupId;
			private List<Integer> groupIds = new ArrayList<Integer>();
			private String groupName;
			private int groupNrStudents;
			private boolean requireLab;
			private int duration;
			private boolean bProfessor = false;
			private boolean bGroup = false;
			private boolean bRoom = false;
			private boolean bCourse = false;
			private boolean bEvent = false;
			private boolean bId = false;
			private boolean bName = false;
			private boolean bLab = false;
			private boolean bNrSeats = false;
			private boolean bNrStudents = false;
			private boolean bProfId = false;
			private boolean bCourseId = false;
			private boolean bRequireLab = false;
			private boolean bDuration = false;
			private boolean bGroups = false;
			private boolean bGroupId = false;
			
			public void startElement(String nsURI, String strippedName, String tagName, Attributes attr) throws SAXException
			{
				if (tagName.equalsIgnoreCase("promoter"))
				{
					bProfessor = true;
				}
				
				if (tagName.equalsIgnoreCase("course"))
				{
					bCourse = true;
				}
				
				if (tagName.equalsIgnoreCase("room"))
				{
					bRoom = true;
				}
				
				if (tagName.equalsIgnoreCase("group"))
				{
					bGroup = true;
				}
				
				if (tagName.equalsIgnoreCase("id"))
				{
					bId = true;
				}
				
				if (tagName.equalsIgnoreCase("name"))
				{
					bName = true;
				}
				
				if (tagName.equalsIgnoreCase("lab"))
				{
					bLab = true;
				}
				
				if (tagName.equalsIgnoreCase("nrSeats"))
				{
					bNrSeats = true;
				}
				
				if (tagName.equalsIgnoreCase("nrStudents"))
				{
					bNrStudents = true;
				}
				
				if (tagName.equalsIgnoreCase("event"))
				{
					bEvent = true;
				}
				
				if (tagName.equalsIgnoreCase("profId"))
				{
					bProfId = true;
				}
				
				if (tagName.equalsIgnoreCase("courseId"))
				{
					bCourseId = true;
				}
				
				if (tagName.equalsIgnoreCase("requireLab"))
				{
					bRequireLab = true;
				}
				
				if (tagName.equalsIgnoreCase("duration"))
				{
					bDuration = true;
				}
				
				if (tagName.equalsIgnoreCase("groups"))
				{
					bGroups = true;
				}
				
				if (tagName.equalsIgnoreCase("groupId"))
				{
					bGroupId = true;
				}
			}
			
			public void characters(char[] ch, int start, int length)
			{
				
				if (bProfessor && bId)
				{
					professorId = Integer.parseInt(new String(ch, start, length));
					bId = false;
					// System.out.println("prof ID: " + professorId);
				}
				
				if (bProfessor && bName)
				{
					professorName = new String(ch, start, length);
					bName = false;
					// System.out.println("prof Name: " + professorName);
					// -----------------------------------------------
					bProfessor = false;
					promoters.put(professorId, new Promoter(professorId, professorName));
				}
				
				if (bCourse && bId)
				{
					courseId = Integer.parseInt(new String(ch, start, length));
					bId = false;
					// System.out.println("course ID: " + courseId);
				}
				
				if (bCourse && bName)
				{
					courseName = new String(ch, start, length);
					bName = false;
					// System.out.println("course Name: " + courseName);
					// -----------------------------------------------
					bCourse = false;
					courses.put(courseId, new Course(courseId, courseName));
				}
				
				if (bRoom && bName)
				{
					roomName = new String(ch, start, length);
					bName = false;
					// System.out.println("room Name: " + roomName);
				}
				
				if (bRoom && bLab)
				{
					roomLab = Boolean.parseBoolean(new String(ch, start, length));
					bLab = false;
					// System.out.println("room Lab: " + roomLab);
				}
				
				if (bRoom && bNrSeats)
				{
					roomNrSeats = Integer.parseInt(new String(ch, start, length));
					bNrSeats = false;
					// System.out.println("room NrSeats: " + roomNrSeats);
					// -----------------------------------------------
					bRoom = false;
					Room room = new Room(roomName, roomLab, roomNrSeats);
					rooms.put(room.getId(), room);
				}
				
				if (bGroup && bId)
				{
					groupId = Integer.parseInt(new String(ch, start, length));
					bId = false;
					// System.out.println("group Id: " + groupId);
				}
				
				if (bGroup && bName)
				{
					groupName = new String(ch, start, length);
					bName = false;
					// System.out.println("group Name: " + groupName);
				}
				
				if (bGroup && bNrStudents)
				{
					groupNrStudents = Integer.parseInt(new String(ch, start, length));
					bNrStudents = false;
					// System.out.println("group NrStudents: " + groupNrStudents);
					// -----------------------------------------------
					bGroup = false;
					groups.put(groupId, new Group(groupId, groupName, groupNrStudents));
				}
				
				if (bEvent && bProfId)
				{
					professorId = Integer.parseInt(new String(ch, start, length));
					bProfId = false;
					// System.out.println("class profID: " + professorId);
				}
				
				if (bEvent && bCourseId)
				{
					courseId = Integer.parseInt(new String(ch, start, length));
					bCourseId = false;
					// System.out.println("class courseID: " + courseId);
				}
				
				if (bEvent && bGroups && bGroupId)
				{
					groupId = Integer.parseInt(new String(ch, start, length));
					bGroupId = false;
					// System.out.println("class groupID: " + groupId);
					groupIds.add(groupId);
				}
				
				if (bEvent && bRequireLab)
				{
					requireLab = Boolean.parseBoolean(new String(ch, start, length));
					bRequireLab = false;
					// System.out.println("class requireLab: " + requireLab);
				}
				
				if (bEvent && bDuration)
				{
					duration = Integer.parseInt(new String(ch, start, length));
					bDuration = false;
					// System.out.println("class duration: " + duration);
					// -----------------------------------------------
					bEvent = false;
					Promoter promoter = null;
					Course course = null;
					List<Group> groupList = new ArrayList<Group>();
					for (Iterator<Integer> iterator = promoters.keySet().iterator(); iterator.hasNext();)
					{
						Integer profId = (Integer) iterator.next();
						if(profId == professorId)
						{
							promoter = promoters.get(profId);
						}
						
					}
					for (Iterator<Integer> iterator = courses.keySet().iterator(); iterator.hasNext();)
					{
						Integer courId = (Integer) iterator.next();
						if(courId == courseId)
						{
							course = courses.get(courId);
						}
						
					}
					for (Iterator<Integer> iterator = groups.keySet().iterator(); iterator.hasNext();)
					{
						Integer sgId = (Integer) iterator.next();
						for (Iterator<Integer> it = groupIds.iterator(); it.hasNext();)
						{
							Integer id = (Integer) it.next();
							if(sgId == id)
							{
								groupList.add(groups.get(id));
							}
						}
					}
					eventsSchedule.add(new EventSchedule(promoter, course, groupList, requireLab, duration));
					groupIds.clear();
				}
			}
		};
		
		SAXParserFactory SAXfactory = SAXParserFactory.newInstance();
		SAXfactory.setValidating(true);

		SAXParser parser = SAXfactory.newSAXParser(); 
		parser.parse( new InputSource( new StringReader( xml ) ), mHandler);
	}
	
	// zwraca aktualn� konfiguracj� w formacie xml
	public String toXML()
	{
		StringBuilder builder = new StringBuilder();
		String tab = "\t";
		builder.append("<configuration>").append("\n");
		
		// Promoters
		Set<Integer> professorsSet = promoters.keySet();
		builder.append(tab).append("<promoters>").append("\n");
		for (Iterator<Integer> iterator = professorsSet.iterator(); iterator.hasNext();)
		{
			Integer key = (Integer) iterator.next();
			Promoter value = promoters.get(key);
			builder.append(tab).append(tab).append("<promoter>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<id>").append(key).append("</id>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(value.getName()).append("</name>").append("\n");
			builder.append(tab).append(tab).append("</promoter>").append("\n");
		}
		builder.append(tab).append("</promoters>").append("\n");
		
		// Courses
		Set<Integer> coursesSet = courses.keySet();
		builder.append(tab).append("<courses>").append("\n");
		for (Iterator<Integer> iterator = coursesSet.iterator(); iterator.hasNext();)
		{
			Integer key = (Integer) iterator.next();
			Course value = courses.get(key);
			builder.append(tab).append(tab).append("<course>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<id>").append(key).append("</id>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(value.getName()).append("</name>").append("\n");
			builder.append(tab).append(tab).append("</course>").append("\n");
		}
		builder.append(tab).append("</courses>").append("\n");
		
		// Rooms
		Set<Integer> roomsSet = rooms.keySet();
		builder.append(tab).append("<rooms>").append("\n");
		for (Iterator<Integer> iterator = roomsSet.iterator(); iterator.hasNext();)
		{
			Integer key = (Integer) iterator.next();
			Room value = rooms.get(key);
			builder.append(tab).append(tab).append("<room>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(value.getName()).append("</name>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<lab>").append(value.isLab()).append("</lab>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<nrSeats>").append(value.getNumberOfSeats()).append("</nrSeats>").append("\n");
			builder.append(tab).append(tab).append("</room>").append("\n");
		}
		builder.append(tab).append("</rooms>").append("\n");
		
		// StudentGroups
		Set<Integer> studentGroupsSet = groups.keySet();
		builder.append(tab).append("<groups>").append("\n");
		for (Iterator<Integer> iterator = studentGroupsSet.iterator(); iterator.hasNext();)
		{
			Integer key = (Integer) iterator.next();
			Group value = groups.get(key);
			builder.append(tab).append(tab).append("<group>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<id>").append(value.getId()).append("</id>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(value.getName()).append("</name>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<nrStudents>").append(value.getNumberOfStudents()).append("</nrStudents>").append("\n");
			builder.append(tab).append(tab).append("</group>").append("\n");
		}
		builder.append(tab).append("</groups>").append("\n");
		
		// EventsSchedule
		builder.append(tab).append("<events>").append("\n");
		for (Iterator<EventSchedule> iterator = eventsSchedule.iterator(); iterator.hasNext();)
		{
			EventSchedule cc = (EventSchedule) iterator.next();
			builder.append(tab).append(tab).append("<event>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<profId>").append(cc.getPromoter().getId()).append("</profId>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<courseId>").append(cc.getCourse().getId()).append("</courseId>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<groups>").append("\n");
			for (Iterator<Group> it = cc.getGroups().iterator(); it.hasNext();)
			{
				Group sg = (Group) it.next();
				builder.append(tab).append(tab).append(tab).append(tab).append("<groupId>").append(sg.getId()).append("</groupId>").append("\n");
			}
			builder.append(tab).append(tab).append(tab).append("</groups>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<requireLab>").append(cc.isLabRequired()).append("</requireLab>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<duration>").append(cc.getDuration()).append("</duration>").append("\n");
			builder.append(tab).append(tab).append("</event>").append("\n");
		}
		
		builder.append(tab).append("</events>").append("\n");
		builder.append("</configuration>");
		return builder.toString();
	}
	
	public void clearAll()
	{
		promoters.clear();
		groups.clear();
		courses.clear();
		rooms.clear();
		eventsSchedule.clear();
	}
	// --------------------------------------------------------------------------------
	public void addPromoter(Promoter promoter)
	{
		this.promoters.put(promoter.getId(), promoter);
	}
	
	public Promoter getPromoterById(int id)
	{ 
		if(promoters.containsKey(id))
		{
			return promoters.get(id);
		}
		else
		{
			return null;
		}
	}

	public int getNumberOfPromoters()
	{ 
		return (int) promoters.size();
	}

	public void addGroup(Group group)
	{
		this.groups.put(group.getId(), group);
	}
	
	public Group getGroupById(int id)
	{ 
		if(groups.containsKey(id))
		{
			return groups.get(id);
		}
		else
		{
			return null;
		}
	}

	public int getNumberOfGroups()
	{ 
		return (int) groups.size(); 
	}

	public void addCourse(Course course)
	{
		this.courses.put(course.getId(), course);
	}
	
	public Course getCourseById(int id)
	{ 
		if(courses.containsKey(id))
		{
			return courses.get(id);
		}
		else
		{
			return null;
		}
	}

	public int getNumberOfCourses()
	{ 
		return (int) courses.size();
	}

	public void addRoom(Room room)
	{
		this.rooms.put(room.getId(), room);
	}
	
	public Room getRoomById(int id)
	{ 
		if(rooms.containsKey(id))
		{
			return rooms.get(id);
		}
		else
		{
			return null;
		}
	}

	public int getNumberOfRooms()
	{ 
		return (int) rooms.size();
	}

	public List<EventSchedule> getEventSchedule()
	{ 
		return eventsSchedule;
	}
	
	public void setCourseClasses(List<EventSchedule> eventsSchedule)
	{
		this.eventsSchedule = eventsSchedule;
	}
	
	public void addCourseClass(EventSchedule eventSchedule)
	{
		this.eventsSchedule.add(eventSchedule);
	}

	public int getNumberOfEvents()
	{ 
		return (int) eventsSchedule.size();
	}
}
