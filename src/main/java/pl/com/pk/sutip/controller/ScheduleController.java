package pl.com.pk.sutip.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import pl.com.pk.sutip.lib.schedule.Algorithm;
import pl.com.pk.sutip.lib.schedule.Configuration;
import pl.com.pk.sutip.lib.schedule.Course;
import pl.com.pk.sutip.lib.schedule.EventSchedule;
import pl.com.pk.sutip.lib.schedule.Group;
import pl.com.pk.sutip.lib.schedule.Promoter;
import pl.com.pk.sutip.lib.schedule.Room;
import pl.com.pk.sutip.lib.schedule.Schedule;
import pl.com.pk.sutip.lib.schedule.ScheduleParser;
import pl.com.pk.sutip.wrapper.UploadFile;

@Controller
public class ScheduleController
{
	// -----------------------------------------------------------------------------------------------------
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static Logger logger = Logger.getLogger(EventController.class);
	public static final String SCHEDULE_PATH = "D:/";
	// -----------------------------------------------------------------------------------------------------
	/*
	 *  Przekierowuje na odpowiednią stronę
	 */
	@RequestMapping(value = "/user/schedule", method = RequestMethod.GET)
	public ModelAndView schedule()
	{
		logger.info("returning /user/schedule");
		ModelAndView mav = new ModelAndView("/user/schedule");
		mav.addObject("uploadItem", new UploadFile());
		return mav;
	}
	 
	/*
	 *  Pobiera plik przesłany przez internet od użytkownika
	 */
	@RequestMapping(value = "/user/schedule", method = RequestMethod.POST)
	public ModelAndView schedule(UploadFile uploadFile, BindingResult result) throws UnsupportedEncodingException, IOException
	{
		ModelAndView mav = new ModelAndView("user/schedule");

		// Pobranie pliku przesłanego przez użytkownika
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(uploadFile.getFileData().getInputStream(), "UTF-8");
		try
		{
			int read;
			do
			{
			    read = in.read(buffer, 0, buffer.length);
			    if (read>0)
			    {
			      out.append(buffer, 0, read);
			    }
			} 
			while (read>=0);
		} 
		finally
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{
				logger.info("throwing in.close() Exception");
				e.printStackTrace();
			}
		}
		if(!out.toString().equals(""))
		{
			String xml = out.toString().substring(1);
			
			// Wykonanie algorytmu
			String schedule = null;
			ScheduleParser parser = null;
			try
			{
				Configuration.getInstance().fromXML(xml);
				schedule = Algorithm.getInstance().start();
				
				// zapisuje na dysk plan zajec w formacie xml
				String link = passwordEncoder.encodePassword(schedule, null);
				PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
				writer.println(schedule);
				writer.flush();
				
				uploadFile.setSchedule(schedule);
				uploadFile.setLink(link);
				
				parser = new ScheduleParser();
				
				parser.parseSchedule(schedule);
			}
			catch (ParserConfigurationException e)
			{
				logger.info("throwing ParserConfigurationException");
				e.printStackTrace();
			}
			catch (SAXException e)
			{
				logger.info("throwing SAXException");
				e.printStackTrace();
			}
			
			mav.addObject("mapGroup", parser.getGroupMap());
			mav.addObject("mapEvent", parser.getEventMap());
			mav.addObject("mapRoom", parser.getRoomMap());
			mav.addObject("mapTimeId", parser.getTimeIdMap());
			mav.addObject("mapTimeDay", parser.getTimeDayMap());
			mav.addObject("min_hour", Schedule.MIN_HOUR);
			mav.addObject("hours", Schedule.HOURS);
		}
        
        logger.info("returning /user/schedule");
        mav.addObject("uploadItem", uploadFile);
        return mav;
	}
	
	/*
	 *  Zwraca użytkownikowi plan zajęć w formacie XML
	 */
	@RequestMapping(value = "/downloadSchedule", method = RequestMethod.GET)
	public void downloadSchedule(@RequestParam(value = "linkID") String linkID, HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("returning /user/schedule");
		
		File file = new File(SCHEDULE_PATH + linkID);
		int length;
		
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=schedule.xml");
        
        byte[] bbuf = new byte[1024000];
        DataInputStream in;
		try
		{
			in = new DataInputStream(new FileInputStream(file));
			ServletOutputStream sot = response.getOutputStream();
			while ((length = in.read(bbuf)) > 0) sot.write(bbuf, 0, length);
			in.close();
			sot.flush();
			sot.close();
		}
		catch (FileNotFoundException e)
		{
			logger.info("throwing FileNotFoundException");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			logger.info("throwing IOException");
			e.printStackTrace();
		}
	}
	
	/*
	 *  Pobiera dane przesłane przez użytkownika
	 */
	@RequestMapping(value = "/saveSchedule", method = RequestMethod.GET)
	public ModelAndView saveSchedule(@RequestParam(value = "promoters") String promoters,
									 @RequestParam(value = "courses") String courses,
									 @RequestParam(value = "rooms") String rooms,
									 @RequestParam(value = "groups") String groups,
									 @RequestParam(value = "events") String events)
	{
		ModelAndView mav = new ModelAndView("user/schedule");

		// pobieranie organizatorów spotkania
		List<Promoter> promotersList = new ArrayList<Promoter>();
		String[] promotersTab = promoters.split("#");
		
		for (int i = 0; i < promotersTab.length; i+=2)
		{
			Promoter professor = new Promoter(Integer.parseInt(promotersTab[i]), promotersTab[i+1]);
			promotersList.add(professor);
		}
		
		// pobieranie zdarzeń
		List<Course> eventsList = new ArrayList<Course>();
		String[] eventsTab = courses.split("#");
		
		for (int i = 0; i < eventsTab.length; i+=2)
		{
			Course course = new Course(Integer.parseInt(eventsTab[i]), eventsTab[i+1]);
			eventsList.add(course);
		}
		
		// pobieranie pokoi
		List<Room> roomsList = new ArrayList<Room>();
		String[] roomsTab = rooms.split("#");
		
		for (int i = 0; i < roomsTab.length; i+=3)
		{
			Room room = new Room(roomsTab[i], Boolean.parseBoolean(roomsTab[i+1]), Integer.parseInt(roomsTab[i+2]));
			roomsList.add(room);
		}
		
		// pobieranie grup studenckich
		List<Group> groupsList = new ArrayList<Group>();
		String[] groupsTab = groups.split("#");
		
		for (int i = 0; i < groupsTab.length; i+=3)
		{
			Group group = new Group(Integer.parseInt(groupsTab[i]), groupsTab[i+1], Integer.parseInt(groupsTab[i+2]));
			groupsList.add(group);
		}
		
		// pobieranie kursów
		List<EventSchedule> classesList = new ArrayList<EventSchedule>();
		String[] classesTab = events.split("#");
		
		for (int i = 0; i < classesTab.length; i+=5)
		{
			Promoter promoter = null;
			Course course = null;
			
			// znajduje profesora
			for (Iterator<Promoter> iterator = promotersList.iterator(); iterator.hasNext();)
			{
				Promoter p = (Promoter) iterator.next();
				if(p.getId() == Integer.parseInt(classesTab[i]))
				{
					promoter = p;
				}
			}
			
			// znajduje kurs
			for (Iterator<Course> iterator = eventsList.iterator(); iterator.hasNext();)
			{
				Course c = (Course) iterator.next();
				if(c.getId() == Integer.parseInt(classesTab[i+1]))
				{
					course = c;
				}
			}
			
			// znajduje grupy
			String[] groupsPerClass = classesTab[i+2].split(",");
			List<Group> group = new ArrayList<Group>();
			for(int k = 0; k < groupsPerClass.length; k++)
			{
				for (Iterator<Group> iterator = groupsList.iterator(); iterator.hasNext();)
				{
					Group g = (Group) iterator.next();
					if(g.getId() == Integer.parseInt(groupsPerClass[k]))
					{
						group.add(g);
					}
				}
			}
			
			EventSchedule es = new EventSchedule(promoter, course, group, Boolean.parseBoolean(classesTab[i+3]), Integer.parseInt(classesTab[i+4]));
			classesList.add(es);
		}
		
        // Wykonanie algorytmu
		String schedule = null;
		ScheduleParser parser = null;
		UploadFile uploadFile = new UploadFile();
        try
		{
			for (Iterator<Promoter> iterator = promotersList.iterator(); iterator.hasNext();)
			{
				Promoter p = (Promoter) iterator.next();
				Configuration.getInstance().addPromoter(p);
			}
			for (Iterator<Course> iterator = eventsList.iterator(); iterator.hasNext();)
			{
				Course c = (Course) iterator.next();
				Configuration.getInstance().addCourse(c);
			}
			for (Iterator<Room> iterator = roomsList.iterator(); iterator.hasNext();)
			{
				Room r = (Room) iterator.next();
				Configuration.getInstance().addRoom(r);
			}
			for (Iterator<Group> iterator = groupsList.iterator(); iterator.hasNext();)
			{
				Group g = (Group) iterator.next();
				Configuration.getInstance().addGroup(g);
			}
			for (Iterator<EventSchedule> iterator = classesList.iterator(); iterator.hasNext();)
			{
				EventSchedule es = (EventSchedule) iterator.next();
				Configuration.getInstance().addCourseClass(es);
			}
			
			schedule = Algorithm.getInstance().start();
			
			// zapisuje na dysk plan zajec w formacie xml
			String link = passwordEncoder.encodePassword(schedule, null);
			PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
			writer.println(schedule);
			writer.flush();
			
			uploadFile.setSchedule(schedule);
			uploadFile.setLink(link);
			
			parser = new ScheduleParser();
			parser.parseSchedule(schedule);
		}
		catch (FileNotFoundException e)
		{
			logger.info("throwing FileNotFoundException");
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			logger.info("throwing ParserConfigurationException");
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			logger.info("throwing SAXException");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			logger.info("throwing IOException");
			e.printStackTrace();
		}
        
        logger.info("returning /user/schedule");
        mav.addObject("uploadItem", uploadFile);
		mav.addObject("mapGroup", parser.getGroupMap());
		mav.addObject("mapEvent", parser.getEventMap());
		mav.addObject("mapRoom", parser.getRoomMap());
		mav.addObject("mapTimeId", parser.getTimeIdMap());
		mav.addObject("mapTimeDay", parser.getTimeDayMap());
		mav.addObject("min_hour", Schedule.MIN_HOUR);
		mav.addObject("hours", Schedule.HOURS);
        return mav;
	}
	// -----------------------------------------------------------------------------------------------------
}