package pl.radek.ss.lib.schedule;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleParser
{
	private List<Group> groupMap = new ArrayList<>();
	
	private List<EventSchedule> listEvents = new ArrayList<>();
	private List<Room> listRoom = new ArrayList<>();
	private List<Integer> listTimeId = new ArrayList<>();
	private List<Integer> listTimeDay = new ArrayList<>();
	
	private Map<Integer, List<EventSchedule>> eventMap = new HashMap<>();
	private Map<Integer, List<Room>> roomMap = new HashMap<>();
	private Map<Integer, List<Integer>> timeIdMap = new HashMap<>();
	private Map<Integer, List<Integer>> timeDayMap = new HashMap<>();
	
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
			
			public void startElement(String nsURI, String strippedName, String tagName, Attributes attr) {
				if (tagName.equalsIgnoreCase("group"))
                    bGroup = true;
				
				if (tagName.equalsIgnoreCase("id"))
                    bId = true;
				
				if (tagName.equalsIgnoreCase("name"))
                    bName = true;
				
				if (tagName.equalsIgnoreCase("id"))
                    bId = true;
				
				if (tagName.equalsIgnoreCase("event"))
                    bEvent = true;
				
				if (tagName.equalsIgnoreCase("timeId"))
                    bTimeId = true;
				
				if (tagName.equalsIgnoreCase("timeDay"))
                    bTimeDay = true;
				
				if (tagName.equalsIgnoreCase("time"))
                    bTime = true;
				
				if (tagName.equalsIgnoreCase("room"))
                    bRoom = true;
				
				if (tagName.equalsIgnoreCase("course"))
                    bCourse = true;
				
				if (tagName.equalsIgnoreCase("prof"))
                    bProf = true;
				
				if (tagName.equalsIgnoreCase("duration"))
                    bDuration = true;
			}
			
			public void characters(char[] ch, int start, int length) {
				if (bGroup && bId && !bRoom) {
					List<EventSchedule> eventScheduleList = new ArrayList<>();
					List<Room> roomList = new ArrayList<>();
					List<Integer> timeIdList = new ArrayList<>();
					List<Integer> timeDayList = new ArrayList<>();
					
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
				}
				
				if (bGroup && bName && !bRoom && !bCourse) {
					groupName = new String(ch, start, length);
					bName = false;
					groupMap.add(new Group(groupId, groupName));
				}
				
				if( bGroup && bEvent && bTimeId) {
					timeId = Integer.parseInt(new String(ch, start, length));
					bTimeId = false;
				}
				
				if( bGroup && bEvent && bTimeDay) {
					timeDay = Integer.parseInt(new String(ch, start, length));
					bTimeDay = false;
				}
				
				if( bGroup && bEvent && bTime) {
					time = new String(ch, start, length);
					bTime = false;
				}
				
				if( bGroup && bEvent && bRoom && bId) {
					roomId = Integer.parseInt(new String(ch, start, length));
					bId = false;
				}
				
				if( bGroup && bEvent && bRoom && bName) {
					roomName = new String(ch, start, length);
					bName = false;
					bRoom = false;
				}
				
				if( bGroup && bEvent && bCourse && bProf) {
					prof = new String(ch, start, length);
					bProf = false;
				}
				
				if( bGroup && bEvent && bCourse && bName) {
					courseName = new String(ch, start, length);
					bName = false;
				}
				
				if( bGroup && bEvent && bCourse && bDuration) {
					duration = Integer.parseInt(new String(ch, start, length));
					bDuration = false;
					bCourse = false;
					bEvent = false;
     
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
		
		List<EventSchedule> eventScheduleList = new ArrayList<>();
		List<Room> roomList = new ArrayList<>();
		List<Integer> timeIdList = new ArrayList<>();
		List<Integer> timeDayList = new ArrayList<>();
		
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
