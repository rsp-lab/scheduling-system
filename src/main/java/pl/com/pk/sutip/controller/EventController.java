package pl.com.pk.sutip.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pl.com.pk.sutip.dao.AccountDao;
import pl.com.pk.sutip.dao.EventDao;
import pl.com.pk.sutip.dao.IntervalDao;
import pl.com.pk.sutip.dao.MessageDao;
import pl.com.pk.sutip.dao.ParticipationDao;
import pl.com.pk.sutip.domain.Account;
import pl.com.pk.sutip.domain.Event;
import pl.com.pk.sutip.domain.EventType;
import pl.com.pk.sutip.domain.Interval;
import pl.com.pk.sutip.domain.Message;
import pl.com.pk.sutip.domain.Participation;
import pl.com.pk.sutip.exception.AccountNotFoundException;
import pl.com.pk.sutip.exception.EventNotFoundException;
import pl.com.pk.sutip.lib.DateManagement;
import pl.com.pk.sutip.lib.IntervalProcessor;
import pl.com.pk.sutip.wrapper.SearchFilter;

@Controller
public class EventController
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
	
	@Autowired
	private MessageSource messageSource;
	
	private static Integer MAX_MESSAGES = 10;
	private static Logger logger = Logger.getLogger(EventController.class);
	// -----------------------------------------------------------------------------------------------------
	/*
	 *  Ustawia pola formatki na domyślne
	 */
	@RequestMapping(value = "/createEvent", method = RequestMethod.GET)
	public ModelAndView createEvent(Principal principal)
	{
		// Pobiera aktualny czas (data i godzina)
		String currentDate = DateManagement.getCurrentDate();
		String currentTime = DateManagement.getCurrentTime();

		Event event = new Event(currentDate, currentTime, currentDate, currentTime);
		if(principal != null)
		{
			event.setUsername(principal.getName());
		}
		
		// Domyślna pozycja spotkania
		event.setLatitude(50.0648490706829);
		event.setLongitude(19.939654296875);
		
		logger.info("returning /event/createEvent");
		ModelAndView mav = new ModelAndView("/event/createEvent");
		mav.addObject("event", event);
		return mav;
	}
	
	/*
	 *  Tworzy spotkanie (Event), tworzy unikalny link do jego zarządzania, przekierowuje metodę niżej
	 */
	@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
	public ModelAndView createEvent(Principal principal, @Valid Event event, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("/event/createEvent");
		
		validateDate(event, result);
		
		if (result.hasErrors()) 
		{
			logger.info("returning /event/createEvent with errors");
			mav.addObject("event", event);
			return mav;
		}
		
		// Tworzenie obiektu EventType
		EventType type = null;
		if(principal == null || event.getType().equals("Publiczny"))
		{
			type = new EventType("PUBLIC", event);
		}
		if(principal != null)
		{
			try
			{
				Account account = accountDao.findUsername(principal.getName());
				account.setCreatedCounter(account.getCreatedCounter()+1);
				accountDao.update(account);
				event.setAccount(account);
			}
			catch (AccountNotFoundException e)
			{
				logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
			
			if(event.getType().equals("Rozszerzony"))
			{
				type = new EventType("EXTENDED", event);
			}
			if(event.getType().equals("Prywatny"))
			{
				type = new EventType("PRIVATE", event);
			}
		}
		ArrayList<EventType> types = new ArrayList<EventType>();
		types.add(type);
		
		event.setEventTypes(types);
		event.setCreationDate(new Date());
		event.setParticipantCounter(0);
		
		int id = eventDao.createEvent(event);
		
		// Tworzenie unikalnego linka do zarządzania spotkaniem
		String link = passwordEncoder.encodePassword("" + id, null);
		
		// Ustawianie linka i aktualizacja encji
		event.setLink(link);
		eventDao.updateEvent(event);
		
		// Przekierowanie (wywołanie poniższego kontrolera)
		logger.info("redirecting /event/manageEvent with link: " + link);
        mav.setViewName("redirect:/manageEvent.html?linkID=" + link);
        return mav;
	}
	
	/*
	 *  Pobiera wszystkich uczestników spotkania
	 */
	@RequestMapping(value = "/manageEvent", method = RequestMethod.GET)
	public ModelAndView manageEvent(@RequestParam(value = "linkID") String link)
	{
		logger.info("returning /event/manageEvent");
		ModelAndView mav = new ModelAndView("/event/manageEvent");

		try
		{
			Event event = eventDao.findEventByLink(link);
			event.setType(event.getEventTypes().get(0).getName());
			
			if(event.getParticipations().size() > 0 && (event.getType().equals("EXTENDED") || event.getType().equals("PRIVATE")))
			{
				Interval interval = IntervalProcessor.findOptimalInterval(event.getParticipations(), event.getDuration());
				System.out.println("Interval: " + interval);
				mav.addObject("interval", interval);
			}
			
			mav.addObject("event", event);
			mav.addObject("eventType", event.getType());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		return mav;
	}
	
	/*
	 *  Aktualizuje dane dotyczące spotkania
	 */
	@RequestMapping(value = "/manageEvent", method = RequestMethod.POST)
	public ModelAndView manageEvent(@Valid Event event, BindingResult result, Principal principal) throws EventNotFoundException
	{
		ModelAndView mav = new ModelAndView("/event/manageEvent");
		
		validateDate(event, result);
		
		if (result.hasErrors()) 
		{
			logger.info("returning /event/manageEvent with errors");
			mav.addObject("event", event);
			mav.addObject("eventType", event.getType());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
			return mav;
		}
		
		// Aktualizacja typu spotkania (EventType)
		String type = "";
		if(principal == null || event.getType().equals("Publiczny"))
		{
			type = "PUBLIC";
		}
		if(principal != null)
		{
			if(event.getType().equals("Rozszerzony"))
			{
				type = "EXTENDED";
			}
			if(event.getType().equals("Prywatny"))
			{
				type = "PRIVATE";
			}
//			try
//			{
//				Account account = accountDao.findUsername(principal.getName());
//				event.setId(account.getId());
//			}
//			catch (AccountNotFoundException e)
//			{
//				e.printStackTrace();
//			}
		}
		
		Event eventDB = eventDao.findEvent(event.getId());
		eventDB.setType(type);
		
		EventType eventType = eventDao.findEventType(eventDB.getId());
		eventType.setName(type);
		
		eventDB.setLatitude(event.getLatitude());
		eventDB.setLongitude(event.getLongitude());
		eventDB.setParticipantCounter(event.getParticipantCounter());
		eventDB.setName(event.getName());
		eventDB.setDescription(event.getDescription());
		eventDB.setLocation(event.getLocation());
		eventDB.setDuration(event.getDuration());
		eventDB.setStartDate(event.getStartDate());
		eventDB.setStartTimestamp(event.getStartTimestamp());
		eventDB.setEndDate(event.getEndDate());
		eventDB.setEndTimestamp(event.getEndTimestamp());	
		eventDB.setUsername(event.getUsername());
		eventDB.setContact(event.getContact());
		
		eventDao.updateEventType(eventType);
		eventDao.updateEvent(eventDB);
		
		logger.info("returning /event/manageEvent");
		mav.addObject("event", eventDB);
		mav.addObject("participants", eventDB.getParticipations());
		mav.addObject("eventType", eventDB.getType());
		mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, eventDB.getId(), "date", "DESC"));
		return mav;
	}
	
	/*
	 *  Usuwa spotkanie
	 */
	@RequestMapping(value = "/deleteEvent", method = RequestMethod.GET)
	public ModelAndView deleteEvent(@RequestParam(value = "id") Integer id)
	{
		try
		{
			eventDao.deleteEvent(id);
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning /findEvent");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findEvent.html?order=creationDate:DESC&user=false");
		return mav;
	}
	
	/*
	 *  Pobiera spotkanie spełniające odpowiednie kryteria
	 */
	@RequestMapping(value = "/findEvent", method = RequestMethod.GET)
	public ModelAndView findEvent(@RequestParam(value = "order") String order, @RequestParam(value = "user") String user, Principal principal)
	{
		// filter[0] zawiera orderBy (według czego), filter[1] zawiera orderType (rosnąco lub malejąco)
		String[] orderDetails = order.split(":");
		
		List<Event> events = null;
		SearchFilter searchFilter = new SearchFilter();
		if(principal == null)
		{
			events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1], "'PUBLIC'", null);
		}
		if(principal != null)
		{
			try
			{
				String role = accountDao.findUsername(principal.getName()).getRoles().get(0).getName();
				if(role.equals("ROLE_USER") && user.equals("true"))
				{
					events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1], "'PUBLIC','EXTENDED','PRIVATE'", principal.getName());
					searchFilter.setUser(true);
				}
				if(role.equals("ROLE_USER") && user.equals("false"))
				{
					events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1], "'PUBLIC','EXTENDED'", null);
				}
				if(role.equals("ROLE_ADMIN"))
				{
					events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1], "'PUBLIC','EXTENDED','PRIVATE'", null);
				}
			}
			catch (AccountNotFoundException e)
			{
				logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
		}
		
		logger.info("returning event/findEvent");
		ModelAndView mav = new ModelAndView("/event/findEvent");
		mav.addObject("events", events);
		mav.addObject("orderBy", orderDetails[0]);
		mav.addObject("orderType", orderDetails[1]);
		mav.addObject("user", user);
		mav.addObject("searchFilter", searchFilter);
		return mav;
	}
	 
	/*
	 *  Pobiera spotkania, które pasują do przekazanej nazwy (ignoruje wielkość wprowadzonych znaków)
	 */
	@RequestMapping(value = "/findEvent", method = RequestMethod.POST)
	public ModelAndView findEvent(SearchFilter searchFilter, BindingResult result, Principal principal)
	{
		List<Event> events = null;
		if(principal == null)
		{
			events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC'", null);
		}
		if(principal != null)
		{
			try
			{
				String role = accountDao.findUsername(principal.getName()).getRoles().get(0).getName();
				if(role.equals("ROLE_USER") && searchFilter.isUser())
				{
					events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED','PRIVATE'", principal.getName());
				}
				else
				{
					events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED'", null);
				}
				if(role.equals("ROLE_ADMIN"))
				{
					events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED','PRIVATE'", null);
				}
			}
			catch (AccountNotFoundException e)
			{
				logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
		}
		
		logger.info("returning event/findEvent");
		ModelAndView mav = new ModelAndView("/event/findEvent");
		mav.addObject("events", events);
		mav.addObject("user", searchFilter.isUser());
		mav.addObject("searchFilter", searchFilter);
		return mav;	
	}
	
	/*
	 *  Przekazuje dane dotyczące spotkania
	 */
	@RequestMapping(value = "/detailsEvent", method = RequestMethod.GET)
	public ModelAndView detailsEvent(@RequestParam(value = "id") Integer id)
	{
		ModelAndView mav = new ModelAndView("/event/detailsEvent");

		try
		{
			Event event = eventDao.findEvent(id);
			mav.addObject("event", event);
			mav.addObject("eventType", event.getEventTypes().get(0).getName());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning event/detailsEvent");
		return mav;	
	}
	
	/*
	 *  Zapisuje użytkownika na spotkanie (pobiera parametry username i contact)
	 */
	@RequestMapping(value = "/detailsEvent", method = RequestMethod.POST)
	public ModelAndView detailsEvent(Event eventPopup, BindingResult result, @RequestParam(value = "usernamePopup") String usernamePopup, @RequestParam(value = "contactPopup") String contactPopup, @RequestParam(value = "intervals") String intervals)
	{
		// Tworzenie obiektów przedziałów czasu (Interval)
		List<Interval> intervalList = new ArrayList<Interval>();
		String[] intervalTab = intervals.split("#");
		
		if(!intervals.equals(""))
		{
			for (int i = 0; i < intervalTab.length; i+=4)
			{
				Interval interval = new Interval(intervalTab[i], intervalTab[i+1], intervalTab[i+2], intervalTab[i+3]);
				intervalList.add(interval);
			}
		}
		
		try
		{
			// Ze względów bezpieczestwa wyszukuje obiekt event zamiast odrazu go utrwalić
			// Wpływ użytkownika na wyłączone pola formatki nie ma znaczenia
			Event event = eventDao.findEvent(eventPopup.getId());
			event.setParticipantCounter(event.getParticipantCounter()+1);
			eventDao.updateEvent(event);
			
			Participation participation = new Participation(usernamePopup, contactPopup, event);
			if(!intervals.equals(""))
			{
				participation.addIntervals(intervalList);
			}
			participationDao.updateParticipation(participation);
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning /event/detailsEvent");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findEvent.html?order=creationDate:DESC&user=false");
        return mav;
	}
	
	/*
	 *  Zapisuje wiadomość przesłaną prez użytkownika (chat)
	 */
	@RequestMapping(value = "/saveMessage", method = RequestMethod.GET)
	public ModelAndView saveMessage(@RequestParam(value = "messageUsername") String messageUsername, @RequestParam(value = "messageText") String messageText, @RequestParam(value = "id") Integer id, @RequestParam(value = "page") String page)
	{
		ModelAndView mav = null;
		if(page.equals("details"))
		{
			logger.info("returning /event/detailsEvent");
			mav = new ModelAndView("/event/detailsEvent");
		}
		if(page.equals("manage"))
		{
			logger.info("returning /event/manageEvent");
			mav = new ModelAndView("/event/manageEvent");
		}
		
		try
		{
			Event event = eventDao.findEvent(id);
			Message message = new Message(messageUsername, messageText, new Date(), event);
			eventDao.updateMessage(message);
			
			mav.addObject("event", event);
			mav.addObject("eventType", event.getEventTypes().get(0).getName());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC"));
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}

		logger.info("returning /event/manageEvent");
		return mav;
	}
	
	/*
	 *  Zwraca najnowsze wiadomości (chat) przy wykorzystaniu żądania jquery/ajax
	 */
	@RequestMapping(value = "/getMessageQuery", method = RequestMethod.GET)
	public ResponseEntity<String> getMessageQuery(HttpServletResponse response, @RequestParam(value = "id") Integer id)
	{
		StringBuilder builder = new StringBuilder();
		List<Message> messages = messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC");
		
		for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();)
		{
			Message msg = (Message) iterator.next();
			builder.append(msg.getDate()).append("/separator/").append(msg.getAuthor()).append("/separator/").append(msg.getText());
			if(iterator.hasNext())
			{
				builder.append("/#/");
			}
		}
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	    return new ResponseEntity<String>(builder.toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	/*
	 *  Zapisuje wiadomośći zwraca najnowsze (chat) przy wykorzystaniu żądania jquery/ajax
	 */
	@RequestMapping(value = "/saveMessageQuery", method = RequestMethod.GET)
	public ResponseEntity<String> saveMessageQuery(@RequestParam(value = "messageUsername") String messageUsername, @RequestParam(value = "messageText") String messageText, @RequestParam(value = "id") Integer id)
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			Event event = eventDao.findEvent(id);
			Message message = new Message(messageUsername, messageText, new Date(), event);
			eventDao.updateMessage(message);
			
			List<Message> messages = messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC");
			
			for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();)
			{
				Message msg = (Message) iterator.next();
				builder.append(msg.getDate()).append("/separator/").append(msg.getAuthor()).append("/separator/").append(msg.getText());
				if(iterator.hasNext())
				{
					builder.append("/#/");
				}
			}
		}
		catch (EventNotFoundException e)
		{
			logger.info("throwing EventNotFoundException");
			e.printStackTrace();
		}
		
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	    return new ResponseEntity<String>(builder.toString(), responseHeaders, HttpStatus.CREATED);
	}

	
	/*
	 *  Weryfikuje pola związane z czasem
	 */
	private void validateDate (Event event, BindingResult result)
	{
		String startDate = event.getStartDate();
		String startTime = event.getStartTimestamp();
		String startTimestamp = startDate + "T" + startTime;
		
		String endDate = event.getEndDate();
		String endTime = event.getEndTimestamp();
		String endTimestamp = endDate + "T" + endTime;
		
		String dateRegex = "^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$";
		String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		
		// Sprawdzenie czy data rozpoczęcia jest w przyszłości
		if(startDate.matches(dateRegex))
		{
			if(DateManagement.compareTimestamps(startTimestamp, DateManagement.getCurrentTimestamp()) == 1)
			{
				result.addError(new FieldError("event", "startDate", messageSource.getMessage("error.startDateIsPast", null, Locale.getDefault())));
			}
		}
		
		// Sprawdzenie czy data zakończenia jest w przyszłości
		if(endDate.matches(dateRegex))
		{
			if(DateManagement.compareTimestamps(startTimestamp, DateManagement.getCurrentTimestamp()) == 1)
			{
				result.addError(new FieldError("event", "endDate", messageSource.getMessage("error.endDateIsPast", null, Locale.getDefault())));
			}
		}
		
		// Sprawdzenie czy data rozpoczęcia jest wcześniej niż data zakończenia
		if(startDate.matches(dateRegex) && endDate.matches(dateRegex) && startTime.matches(timeRegex) && endTime.matches(timeRegex))
		{
			if(DateManagement.compareTimestamps(startTimestamp, endTimestamp) == -1)
			{
				result.addError(new FieldError("event", "startTimestamp", messageSource.getMessage("error.startAfterEnd", null, Locale.getDefault())));
				result.addError(new FieldError("event", "endTimestamp", messageSource.getMessage("error.startAfterEnd", null, Locale.getDefault())));
			}
		}
	}
	
	/*
	 *  Lista typów dla kontrolki select dla eventForm.jsp
	 */
	@ModelAttribute("types")
	public ArrayList<String> getList()
	{
	    ArrayList<String> itemList = new ArrayList<String>();
	    itemList.add("Publiczny");
	    itemList.add("Rozszerzony");
	    itemList.add("Prywatny");
	    return itemList;
	}
	// -----------------------------------------------------------------------------------------------------
}
