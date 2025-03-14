package pl.com.pk.sutip.lib.schedule;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ScheduleParser
{
	private List<Group> groupMap = new ArrayList<Group>();
	
	private List<EventSchedule> listEvents = new ArrayList<EventSchedule>();
	private List<Room> listRoom = new ArrayList<Room>();
	private List<Integer> listTimeId = new ArrayList<Integer>();
	private List<Integer> listTimeDay = new ArrayList<Integer>();
	
	private Map<Integer, List<EventSchedule>> eventMap = new HashMap<Integer, List<EventSchedule>>();
	private Map<Integer, List<Room>> roomMap = new HashMap<Integer, List<Room>>();
	private Map<Integer, List<Integer>> timeIdMap = new HashMap<Integer, List<Integer>>();
	private Map<Integer, List<Integer>> timeDayMap = new HashMap<Integer, List<Integer>>();
	
	private int groupId = 0;
	
	public void parseSchedule(String schedule) throws ParserConfigurationException, SAXException, IOException
	{
		DefaultHandler mHandler = new DefaultHandler()
		{
			private String groupName;
			private int timeId;
			private int timeDay;
			private String time;
			private int roomId;
			private String roomName;
			private String prof;
			private String courseName;
			private int duration;
			private boolean bGroup = false;
			private boolean bId = false;
			private boolean bName = false;
			private boolean bEvent = false;
			private boolean bTimeId = false;
			private boolean bTimeDay = false;
			private boolean bTime = false;
			private boolean bRoom = false;
			private boolean bCourse = false;
			private boolean bProf = false;
			private boolean bDuration = false;
			
			public void startElement(String nsURI, String strippedName, String tagName, Attributes attr) throws SAXException
			{
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
				
				if (tagName.equalsIgnoreCase("id"))
				{
					bId = true;
				}
				
				if (tagName.equalsIgnoreCase("event"))
				{
					bEvent = true;
				}
				
				if (tagName.equalsIgnoreCase("timeId"))
				{
					bTimeId = true;
				}
				
				if (tagName.equalsIgnoreCase("timeDay"))
				{
					bTimeDay = true;
				}
				
				if (tagName.equalsIgnoreCase("time"))
				{
					bTime = true;
				}
				
				if (tagName.equalsIgnoreCase("room"))
				{
					bRoom = true;
				}
				
				if (tagName.equalsIgnoreCase("course"))
				{
					bCourse = true;
				}
				
				if (tagName.equalsIgnoreCase("prof"))
				{
					bProf = true;
				}
				
				if (tagName.equalsIgnoreCase("duration"))
				{
					bDuration = true;
				}
			}
			
			public void characters(char[] ch, int start, int length)
			{
				if (bGroup && bId && !bRoom)
				{
					List<EventSchedule> eventScheduleList = new ArrayList<EventSchedule>();
					List<Room> roomList = new ArrayList<Room>();
					List<Integer> timeIdList = new ArrayList<Integer>();
					List<Integer> timeDayList = new ArrayList<Integer>();
					
					eventScheduleList.addAll(listEvents);
					roomList.addAll(listRoom);
					timeIdList.addAll(listTimeId);
					timeDayList.addAll(listTimeDay);
					
					eventMap.put(groupId, eventScheduleList);
					roomMap.put(groupId, roomList);
					timeIdMap.put(groupId, timeIdList);
					timeDayMap.put(groupId, timeDayList);
					
					listEvents.clear();
					listRoom.clear();
					listTimeId.clear();
					listTimeDay.clear();
					
					groupId = Integer.parseInt(new String(ch, start, length));
					bId = false;
					// System.out.println(" ------> group ID: " + groupId);
				}
				
				if (bGroup && bName && !bRoom && !bCourse)
				{
					groupName = new String(ch, start, length);
					bName = false;
					// System.out.println(" ------> group Name: " + groupName);
					// ----------------------------------------------------------
					groupMap.add(new Group(groupId, groupName));
				}
				
				if( bGroup && bEvent && bTimeId)
				{
					timeId = Integer.parseInt(new String(ch, start, length));
					bTimeId = false;
					// System.out.println("time ID: " + timeId);
				}
				
				if( bGroup && bEvent && bTimeDay)
				{
					timeDay = Integer.parseInt(new String(ch, start, length));
					bTimeDay = false;
					// System.out.println("time Day: " + timeDay);
				}
				
				if( bGroup && bEvent && bTime)
				{
					time = new String(ch, start, length);
					bTime = false;
					// System.out.println("time: " + time);
				}
				
				if( bGroup && bEvent && bRoom && bId)
				{
					roomId = Integer.parseInt(new String(ch, start, length));
					bId = false;
					// System.out.println("room ID: " + roomId);
				}
				
				if( bGroup && bEvent && bRoom && bName)
				{
					roomName = new String(ch, start, length);
					bName = false;
					bRoom = false;
					// System.out.println("room Name: " + roomName);
				}
				
				if( bGroup && bEvent && bCourse && bProf)
				{
					prof = new String(ch, start, length);
					bProf = false;
					// System.out.println("prof: " + prof);
				}
				
				if( bGroup && bEvent && bCourse && bName)
				{
					courseName = new String(ch, start, length);
					bName = false;
					// System.out.println("courseName: " + courseName);
				}
				
				if( bGroup && bEvent && bCourse && bDuration)
				{
					duration = Integer.parseInt(new String(ch, start, length));
					bDuration = false;
					bCourse = false;
					bEvent = false;
					// System.out.println("duration: " + duration);
					// -----------------------------------------------
					EventSchedule eventSchedule = new EventSchedule(new Promoter(prof), new Course(courseName), duration);
					listEvents.add(eventSchedule);
					Room room = new Room(roomName);
					listRoom.add(room);
					listTimeId.add(timeId);
					listTimeDay.add(timeDay);
					
				}
			}
		};
		
		SAXParserFactory SAXfactory = SAXParserFactory.newInstance();
		SAXfactory.setValidating(true);

		SAXParser parser = SAXfactory.newSAXParser(); 
		StringReader sr = new StringReader(schedule);
		InputSource is = new InputSource(sr);
		parser.parse(is, mHandler);
		
		List<EventSchedule> eventScheduleList = new ArrayList<EventSchedule>();
		List<Room> roomList = new ArrayList<Room>();
		List<Integer> timeIdList = new ArrayList<Integer>();
		List<Integer> timeDayList = new ArrayList<Integer>();
		
		eventScheduleList.addAll(listEvents);
		roomList.addAll(listRoom);
		timeIdList.addAll(listTimeId);
		timeDayList.addAll(listTimeDay);
		
		eventMap.put(groupId, eventScheduleList);
		roomMap.put(groupId, roomList);
		timeIdMap.put(groupId, timeIdList);
		timeDayMap.put(groupId, timeDayList);
		
		sr.close();
	}

	public List<Group> getGroupMap()
	{
		return groupMap;
	}

	public void setGroupMap(List<Group> groupMap)
	{
		this.groupMap = groupMap;
	}

	public Map<Integer, List<EventSchedule>> getEventMap()
	{
		return eventMap;
	}

	public void setEventMap(Map<Integer, List<EventSchedule>> classMap)
	{
		this.eventMap = classMap;
	}

	public Map<Integer, List<Room>> getRoomMap()
	{
		return roomMap;
	}

	public void setRoomMap(Map<Integer, List<Room>> roomMap)
	{
		this.roomMap = roomMap;
	}

	public Map<Integer, List<Integer>> getTimeIdMap()
	{
		return timeIdMap;
	}

	public void setTimeIdMap(Map<Integer, List<Integer>> timeIdMap)
	{
		this.timeIdMap = timeIdMap;
	}

	public Map<Integer, List<Integer>> getTimeDayMap()
	{
		return timeDayMap;
	}

	public void setTimeDayMap(Map<Integer, List<Integer>> timeDayMap)
	{
		this.timeDayMap = timeDayMap;
	}
}
