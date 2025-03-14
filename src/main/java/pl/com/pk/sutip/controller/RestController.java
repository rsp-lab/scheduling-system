package pl.com.pk.sutip.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import pl.com.pk.sutip.dao.AccountDao;
import pl.com.pk.sutip.dao.EventDao;
import pl.com.pk.sutip.dao.IntervalDao;
import pl.com.pk.sutip.dao.MessageDao;
import pl.com.pk.sutip.dao.ParticipationDao;
import pl.com.pk.sutip.domain.Event;
import pl.com.pk.sutip.domain.EventType;
import pl.com.pk.sutip.domain.Interval;
import pl.com.pk.sutip.domain.Message;
import pl.com.pk.sutip.domain.Participation;
import pl.com.pk.sutip.exception.EventNotFoundException;
import pl.com.pk.sutip.lib.IntervalProcessor;
import pl.com.pk.sutip.lib.schedule.Algorithm;
import pl.com.pk.sutip.lib.schedule.Configuration;
import pl.com.pk.sutip.lib.schedule.Course;
import pl.com.pk.sutip.lib.schedule.EventSchedule;
import pl.com.pk.sutip.lib.schedule.Group;
import pl.com.pk.sutip.lib.schedule.Promoter;
import pl.com.pk.sutip.lib.schedule.Room;
import pl.com.pk.sutip.wrapper.SearchFilter;
import pl.com.pk.sutip.wrapper.UploadFile;

@Controller
public class RestController
{
	// -----------------------------------------------------------------------------------------------------
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	EventDao eventDao;
	
	@Autowired
	ParticipationDao participationDao;

	@Autowired
	IntervalDao intervalDao;
	
	@Autowired
	MessageDao messageDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static Logger logger = Logger.getLogger(EventController.class);
	public static final String VIEW_KEY = "xmlView";
	public static final String SCHEDULE_PATH = "D:/";
	// -----------------------------------------------------------------------------------------------------
	/*
	 *  Zwraca spotkania (events) spełniające odpowiednie kryteria
	 */
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public ModelAndView getEvents(@RequestParam(value = "limit") String limit,
								  @RequestParam(value = "order") String order,
								  @RequestParam(value = "type") String type,
								  @RequestParam(value = "username") String username,
								  Principal principal) 
	{
		if(limit.equals(null))
		{
			limit = "" + SearchFilter.MAX_RESULTS;
		}
		String[] orderDetails = order.split(":");
		
		List<Event> events = new ArrayList<Event>();
		events = eventDao.getEvents(Integer.parseInt(limit), orderDetails[0], orderDetails[1], type, username);
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "events", events);
		return mav;
	}
	
	/*
	 *  Zwraca spotkanie (event) na podstawie id
	 */
	@RequestMapping(value = "/events/{id}", method = RequestMethod.GET)
	public ModelAndView getEventById(@PathVariable("id") String id) 
	{
		Event event = null;
		try
		{
			event = eventDao.findEvent(Integer.parseInt(id));
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			event = new Event();
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			event = new Event();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "event", event);
		return mav;
	}
	
	/*
	 *  Zwraca spotkanie (event) na podstawie linku
	 */
	@RequestMapping(value = "/events/{link}", method = RequestMethod.GET)
	public ModelAndView getEventByLink(@PathVariable("link") String link) 
	{
		Event event = null;
		try
		{
			event = eventDao.findEventByLink(link);
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			event = new Event();
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			event = new Event();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "event", event);
		return mav;
	}
	
	/*
	 *  Usuwa spotkanie (event) na podstawie id
	 */
	@RequestMapping(value = "/events/{id}", method = RequestMethod.DELETE)
	public void deleteEvent(@PathVariable("id") String id) 
	{
		try
		{
			eventDao.deleteEvent(Integer.parseInt(id));
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
	}
	
	/*
	 *  Tworzy spotkanie (event) 
	 */
	@RequestMapping(value = "/events/new/{type}", method = RequestMethod.POST)
	public ModelAndView createEvent(@RequestBody Event event, @PathVariable("type") String type) 
	{
		EventType eventType = new EventType(type, event);
		ArrayList<EventType> types = new ArrayList<EventType>();
		types.add(eventType);
		
		event.setEventTypes(types);
		event.setCreationDate(new Date());
		event.setParticipantCounter(0);
		
		int id = eventDao.createEvent(event);
		
		String link = passwordEncoder.encodePassword("" + id, null);
		
		event.setLink(link);
		eventDao.updateEvent(event);
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "event", event);
		return mav;
	}
	
	/*
	 *  Aktualizuje spotkanie (event) 
	 */
	@RequestMapping(value = "/events/update/{type}", method = RequestMethod.PUT)
	public ModelAndView updateEvent(@RequestBody Event event, @PathVariable("type") String type) 
	{
		EventType eventType = new EventType(type, event);
		ArrayList<EventType> types = new ArrayList<EventType>();
		types.add(eventType);
		
		eventDao.updateEvent(event);
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "event", event);
		return mav;
	}
	
	/*
	 *  Pobiera uczestników (participation) spotkania (event) na podstawie id
	 */
	@RequestMapping(value = "/participations/{id}", method = RequestMethod.GET)
	public ModelAndView getParticipations(@PathVariable("id") String id) 
	{
		List<Participation> participations = null;
		try
		{
			participations = eventDao.findEvent(Integer.parseInt(id)).getParticipations();
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			participations = new ArrayList<Participation>();
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			participations = new ArrayList<Participation>();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "participations", participations);
		return mav;
	}
	
	/*
	 *  Zapisuje uczestnictwo (participation) na spotkanie (event) na podstawie id
	 */
	@RequestMapping(value = "/participations/new/{id}", method = RequestMethod.POST)
	public ModelAndView saveParticipation(@RequestParam(value = "intervals") String intervals, @PathVariable("id") String id, @RequestParam(value = "username") String username, @RequestParam(value = "contact") String contact) 
	{
		// Tworzenie obiektów przedziałów czasu (Interval)
		List<Interval> intervalList = new ArrayList<Interval>();
		String[] intervalTab = intervals.split("#");
		
		for (int i = 0; i < intervalTab.length; i+=4)
		{
			Interval interval = new Interval(intervalTab[i], intervalTab[i+1], intervalTab[i+2], intervalTab[i+3]);
			intervalList.add(interval);
		}
		
		Participation participation = null;
		try
		{
			Event event = eventDao.findEvent(Integer.parseInt(id));
			event.setParticipantCounter(event.getParticipantCounter()+1);
			eventDao.updateEvent(event);
			
			participation = new Participation(username, contact, event);
			participation.addIntervals(intervalList);
			participationDao.updateParticipation(participation);
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			participation = new Participation();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "participation", participation);
		return mav;
	}
	
	/*
	 *  Zwraca widomości (messages) na podstawie limitu oraz id spotkania (event)
	 */
	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public ModelAndView getMessages(@RequestParam(value = "limit") String limit, @RequestParam(value = "id") String id) 
	{
		if(limit.equals(null))
		{
			limit = "" + SearchFilter.MAX_RESULTS;
		}
		
		List<Message> messages = new ArrayList<Message>();
		messages = messageDao.getMessages(Integer.parseInt(limit), Integer.parseInt(id), "date", "DESC");
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "messages", messages);
		return mav;
	}
	
	/*
	 *  Zapisuje wiadomość (message) na podstawie id spotkania (event)
	 */
	@RequestMapping(value = "/messages/new/{id}", method = RequestMethod.POST)
	public ModelAndView saveMessage(@PathVariable("id") String id, @RequestParam(value = "username") String username, @RequestParam(value = "text") String text) 
	{
		Event event = null;
		Message message = null;
		try
		{
			event = eventDao.findEvent(Integer.parseInt(id));
			message = new Message(username, text, new Date(), event);
			eventDao.updateMessage(message);
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "message", message);
		return mav;
	}
	
	/*
	 *  Zwraca optymalny termin spotkania (interval) na podstawie id 
	 */
	@RequestMapping(value = "/algorithm/interval/{id}", method = RequestMethod.GET)
	public ModelAndView ModelAndView(@PathVariable("id") String id) 
	{
		Event event = null;
		Interval interval = null;
		try
		{
			event = eventDao.findEvent(Integer.parseInt(id));
			if(event.getParticipations().size() > 0)
			{
				interval = IntervalProcessor.findOptimalInterval(event.getParticipations(), event.getDuration());
			}
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			interval = new Interval();
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			interval = new Interval();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "interval", interval);
		return mav;
	}
	
	/*
	 *  Zwraca optymalny termin spotkania (interval) na podstawie link 
	 */
	@RequestMapping(value = "/algorithm/interval/{link}", method = RequestMethod.GET)
	public ModelAndView findOptimalIntervalByEventLink(@PathVariable("link") String link) 
	{
		Event event = null;
		Interval interval = null;
		try
		{
			event = eventDao.findEventByLink(link);
			if(event.getParticipations().size() > 0)
			{
				interval = IntervalProcessor.findOptimalInterval(event.getParticipations(), event.getDuration());
			}
		}
		catch (NumberFormatException e)
		{
			logger.info("throwing NumberFormatException");
			interval = new Interval();
			e.printStackTrace();
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			interval = new Interval();
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "interval", interval);
		return mav;
	}
	
	/*
	 *  Zwraca optymalny termin spotkania (interval) na podstawie innych terminów
	 */
	@RequestMapping(value = "/algorithm/interval", method = RequestMethod.GET)
	public ModelAndView findOptimalInterval(@RequestParam(value = "intervals") String intervals, @RequestParam(value = "duration") String duration) 
	{
		// Tworzenie obiektów przedziałów czasu (Interval)
		List<Interval> intervalList = new ArrayList<Interval>();
		String[] intervalTab = intervals.split("#");
		
		for (int i = 0; i < intervalTab.length; i+=4)
		{
			Interval interval = new Interval(intervalTab[i], intervalTab[i+1], intervalTab[i+2], intervalTab[i+3]);
			intervalList.add(interval);
		}
		
		Interval interval = IntervalProcessor.processIntervals(intervalList, duration);
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "interval", interval);
		return mav;
	}
	
	/*
	 *  Zwraca plan zajęć (schedule) na podstawie pliku xml
	 */
	@RequestMapping(value = "/algorithm/schedule/{xml}", method = RequestMethod.GET)
	public ModelAndView getSchedule(@PathVariable("xml") String xml)
	{
		// Wykonanie algorytmu
		String schedule = null;
        try
		{
			Configuration.getInstance().fromXML(xml);
			schedule = Algorithm.getInstance().start();
			
			// zapisuje na dysk plan zajec w formacie xml
			String link = passwordEncoder.encodePassword(schedule, null);
			PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
			writer.println(schedule);
			writer.flush();
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
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "schedule", schedule);
		return mav;
	}
	
	/*
	 *  Zwraca plan zajęć (schedule) na podstawie konfiguracji GET
	 */
	@RequestMapping(value = "/algorithm/schedule", method = RequestMethod.GET)
	public ModelAndView getSchedule(@RequestParam(value = "professors") String promoters,
									 @RequestParam(value = "courses") String courses,
									 @RequestParam(value = "rooms") String rooms,
									 @RequestParam(value = "groups") String groups,
									 @RequestParam(value = "classes") String events)
	{
		// pobieranie organizatorów
		List<Promoter> professorsList = new ArrayList<Promoter>();
		String[] professorsTab = promoters.split("#");
		
		for (int i = 0; i < professorsTab.length; i+=2)
		{
			Promoter professor = new Promoter(Integer.parseInt(professorsTab[i]), professorsTab[i+1]);
			professorsList.add(professor);
		}
		
		// pobieranie kursów
		List<Course> coursesList = new ArrayList<Course>();
		String[] coursesTab = courses.split("#");
		
		for (int i = 0; i < coursesTab.length; i+=2)
		{
			Course course = new Course(Integer.parseInt(coursesTab[i]), coursesTab[i+1]);
			coursesList.add(course);
		}
		
		// pobieranie pokoi
		List<Room> roomsList = new ArrayList<Room>();
		String[] roomsTab = rooms.split("#");
		
		for (int i = 0; i < roomsTab.length; i+=3)
		{
			Room room = new Room(roomsTab[i], Boolean.parseBoolean(roomsTab[i+1]), Integer.parseInt(roomsTab[i+2]));
			roomsList.add(room);
		}
		
		// pobieranie grup uczestników
		List<Group> groupsList = new ArrayList<Group>();
		String[] groupsTab = groups.split("#");
		
		for (int i = 0; i < groupsTab.length; i+=3)
		{
			Group group = new Group(Integer.parseInt(groupsTab[i]), groupsTab[i+1], Integer.parseInt(groupsTab[i+2]));
			groupsList.add(group);
		}
		
		// pobieranie spotkań
		List<EventSchedule> classesList = new ArrayList<EventSchedule>();
		String[] classesTab = events.split("#");
		
		for (int i = 0; i < classesTab.length; i+=5)
		{
			Promoter professor = null;
			Course course = null;
			
			// znajduje profesora
			for (Iterator<Promoter> iterator = professorsList.iterator(); iterator.hasNext();)
			{
				Promoter p = (Promoter) iterator.next();
				if(p.getId() == Integer.parseInt(classesTab[i]))
				{
					professor = p;
				}
			}
			
			// znajduje kurs
			for (Iterator<Course> iterator = coursesList.iterator(); iterator.hasNext();)
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
			
			EventSchedule cc = new EventSchedule(professor, course, group, Boolean.parseBoolean(classesTab[i+3]), Integer.parseInt(classesTab[i+4]));
			classesList.add(cc);
		}
		
	    // Wykonanie algorytmu
		String schedule = null;
		UploadFile uploadFile = new UploadFile();
	    try
		{
			for (Iterator<Promoter> iterator = professorsList.iterator(); iterator.hasNext();)
			{
				Promoter p = (Promoter) iterator.next();
				Configuration.getInstance().addPromoter(p);
			}
			for (Iterator<Course> iterator = coursesList.iterator(); iterator.hasNext();)
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
				EventSchedule cc = (EventSchedule) iterator.next();
				Configuration.getInstance().addCourseClass(cc);
			}
			
			schedule = Algorithm.getInstance().start();
			
			// zapisuje na dysk plan zajec w formacie xml
			String link = passwordEncoder.encodePassword(schedule, null);
			PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
			writer.println(schedule);
			writer.flush();
			
			uploadFile.setSchedule(schedule);
			uploadFile.setLink(link);
		}
		catch (FileNotFoundException e)
		{
			logger.info("throwing FileNotFoundException");
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("xmlView", BindingResult.MODEL_KEY_PREFIX + "schedule", schedule);
		return mav;
	}
	// -----------------------------------------------------------------------------------------------------
}