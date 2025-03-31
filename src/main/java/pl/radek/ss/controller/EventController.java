package pl.radek.ss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.radek.ss.dao.*;
import pl.radek.ss.domain.*;
import pl.radek.ss.exception.EventNotFoundException;
import pl.radek.ss.lib.DateManagement;
import pl.radek.ss.lib.IntervalProcessor;
import pl.radek.ss.dtos.SearchFilter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class EventController
{
	// -----------------------------------------------------------------------------------------------------
	private final AccountDao accountDao;
    private final EventDao eventDao;
    private final ParticipationDao participationDao;
    private final IntervalDao intervalDao;
    private final MessageDao messageDao;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    
    public EventController(AccountDao accountDao, EventDao eventDao, ParticipationDao participationDao, IntervalDao intervalDao, MessageDao messageDao, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.accountDao = accountDao;
        this.eventDao = eventDao;
        this.participationDao = participationDao;
        this.intervalDao = intervalDao;
        this.messageDao = messageDao;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }
    
    private static final Integer MAX_MESSAGES = 50;
	// -----------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/createEvent", method = RequestMethod.GET)
	public ModelAndView createEvent(Principal principal)
	{
		Event event = new Event(DateManagement.getCurrentDate(), DateManagement.getCurrentTime(), DateManagement.getFutureDate(), DateManagement.getFutureTime());
		if(principal != null)
            event.setUsername(principal.getName());
		
		// Default map location
		event.setLatitude("50.064");
        event.setLongitude("19.939");
		
		ModelAndView mav = new ModelAndView("/event/createEvent");
		mav.addObject("event", event);
        mav.addObject("latitude", event.getLatitude());
        mav.addObject("longitude", event.getLongitude());
		return mav;
	}

	@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
	public ModelAndView createEvent(Principal principal, @Valid Event event, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("/event/createEvent");
		
        if (!result.hasErrors())
		    validateDate(event, result);
        
        if (result.hasErrors()) {
			mav.addObject("event", event);
            mav.addObject("latitude", event.getLatitude());
            mav.addObject("longitude", event.getLongitude());
			return mav;
		}
		
        // TODO security
        /*
		EventType type = null;
		if(principal == null || event.getType().equals("Public"))
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
				// logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
			
			if(event.getType().equals("Extended"))
			{
				type = new EventType("EXTENDED", event);
			}
			if(event.getType().equals("Private"))
			{
				type = new EventType("PRIVATE", event);
			}
		}
		*/
        EventType type = null;
        if(event.getType().equals("Public"))
            type = new EventType("PUBLIC", event);
        if(event.getType().equals("Extended"))
            type = new EventType("EXTENDED", event);
        if(event.getType().equals("Private"))
            type = new EventType("PRIVATE", event);
        
		ArrayList<EventType> types = new ArrayList<>();
		types.add(type);
		
		event.setEventTypes(types);
		event.setCreationDate(new Date());
		event.setParticipantCounter(0);
		
		int id = eventDao.createEvent(event);
		
		String link = passwordEncoder.encode("" + id);
		event.setLink(link);
  
		eventDao.updateEvent(event);
		
        mav.setViewName("redirect:/manageEvent?linkID=" + link);
        return mav;
	}
	
	@RequestMapping(value = "/manageEvent", method = RequestMethod.GET)
	public ModelAndView manageEvent(@RequestParam(value = "linkID") String link)
	{
		ModelAndView mav = new ModelAndView("/event/manageEvent");

		try {
			Event event = eventDao.findEventByLink(link);
			event.setType(event.getEventTypes().get(0).getName());
			
			if(event.getParticipations().size() > 0 && (event.getType().equals("EXTENDED") || event.getType().equals("PRIVATE")))
			{
				Interval interval = IntervalProcessor.findOptimalInterval(event.getParticipations(), event.getDuration());
				System.out.println("Interval: " + interval);
				mav.addObject("interval", interval);
			}
			
			mav.addObject("event", event);
			mav.addObject("latitude", event.getLatitude());
			mav.addObject("longitude", event.getLongitude());
			mav.addObject("eventType", event.getType());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value = "/manageEvent", method = RequestMethod.POST)
	public ModelAndView manageEvent(@Valid Event event, BindingResult result, Principal principal) throws EventNotFoundException
	{
		ModelAndView mav = new ModelAndView("/event/manageEvent");
        
        if (!result.hasErrors())
            validateDate(event, result);
		
		if (result.hasErrors()) {
			mav.addObject("event", event);
            mav.addObject("latitude", event.getLatitude());
            mav.addObject("longitude", event.getLongitude());
			mav.addObject("eventType", event.getType());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
			return mav;
		}
        
        // TODO security
        /*
		String type = "";
		if(principal == null || event.getType().equals("Public"))
		{
			type = "PUBLIC";
		}
		if(principal != null)
		{
			if(event.getType().equals("Extended"))
			{
				type = "EXTENDED";
			}
			if(event.getType().equals("Private"))
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
		*/
        String type = "";
        if(event.getType().equals("Public"))
            type = "PUBLIC";
        if(event.getType().equals("Extended"))
            type = "EXTENDED";
        if(event.getType().equals("Private"))
            type = "PRIVATE";
        
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
		
		mav.addObject("event", eventDB);
        mav.addObject("latitude", event.getLatitude());
        mav.addObject("longitude", event.getLongitude());
		mav.addObject("participants", eventDB.getParticipations());
		mav.addObject("eventType", eventDB.getType());
		mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, eventDB.getId(), "date", "DESC"));
		return mav;
	}
	
	@RequestMapping(value = "/deleteEvent", method = RequestMethod.GET)
	public ModelAndView deleteEvent(@RequestParam(value = "id") Integer id)
	{
		try {
			eventDao.deleteEvent(id);
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findEvent?order=creationDate:DESC&user=false");
		return mav;
	}
	
	@RequestMapping(value = "/findEvent", method = RequestMethod.GET)
	public ModelAndView findEvent(@RequestParam(value = "order") String order, @RequestParam(value = "user") String user, Principal principal)
	{
		String[] orderDetails = order.split(":");
		
		List<Event> events = null;
		SearchFilter searchFilter = new SearchFilter();
        
        // TODO security
        /*
		if(principal == null)
            events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1],
                    "'PUBLIC'", null);
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
				// logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
		}
		*/
        events = eventDao.getEvents(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1], "'PUBLIC','EXTENDED','PRIVATE'", null);
        
		ModelAndView mav = new ModelAndView("/event/findEvent");
		mav.addObject("events", events);
		mav.addObject("orderBy", orderDetails[0]);
		mav.addObject("orderType", orderDetails[1]);
		mav.addObject("user", user);
		mav.addObject("searchFilter", searchFilter);
		return mav;
	}
	
	@RequestMapping(value = "/findEvent", method = RequestMethod.POST)
	public ModelAndView findEvent(SearchFilter searchFilter, BindingResult result, Principal principal)
	{
		List<Event> events = null;
        
        // TODO security
        /*
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
				// logger.info("throwing AccountNotFoundException");
				e.printStackTrace();
			}
		}
		*/
        events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED','PRIVATE'", null);
        
		ModelAndView mav = new ModelAndView("/event/findEvent");
		mav.addObject("events", events);
		mav.addObject("user", searchFilter.isUser());
		mav.addObject("searchFilter", searchFilter);
		return mav;
	}
	
	@RequestMapping(value = "/detailsEvent", method = RequestMethod.GET)
	public ModelAndView detailsEvent(@RequestParam(value = "id") Integer id)
	{
		ModelAndView mav = new ModelAndView("/event/detailsEvent");

		try {
			Event event = eventDao.findEvent(id);
			mav.addObject("event", event);
            mav.addObject("latitude", event.getLatitude());
            mav.addObject("longitude", event.getLongitude());
			mav.addObject("eventType", event.getEventTypes().get(0).getName());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, event.getId(), "date", "DESC"));
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/detailsEvent", method = RequestMethod.POST)
	public ModelAndView detailsEvent(Event eventPopup, BindingResult result, @RequestParam(value = "usernamePopup") String usernamePopup, @RequestParam(value = "contactPopup") String contactPopup, @RequestParam(value = "intervals") String intervals)
	{
		List<Interval> intervalList = new ArrayList<>();
		String[] intervalTab = intervals.split("#");
		
		if(!intervals.equals(""))
            for (int i = 0; i < intervalTab.length; i += 4) {
                Interval interval = new Interval(intervalTab[i], intervalTab[i + 1], intervalTab[i + 2], intervalTab[i + 3]);
                intervalList.add(interval);
            }
		
		try {
			Event event = eventDao.findEvent(eventPopup.getId());
			event.setParticipantCounter(event.getParticipantCounter()+1);
			eventDao.updateEvent(event);
			
			Participation participation = new Participation(usernamePopup, contactPopup, event);
			if(!intervals.isEmpty())
                participation.addIntervals(intervalList);
	
			participationDao.updateParticipation(participation);
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findEvent?order=creationDate:DESC&user=false");
        return mav;
	}
	
	@RequestMapping(value = "/saveMessage", method = RequestMethod.GET)
	public ModelAndView saveMessage(@RequestParam(value = "messageUsername") String messageUsername, @RequestParam(value = "messageText") String messageText, @RequestParam(value = "id") Integer id, @RequestParam(value = "page") String page)
	{
		ModelAndView mav = null;
		if(page.equals("details"))
            mav = new ModelAndView("/event/detailsEvent");
		if(page.equals("manage"))
            mav = new ModelAndView("/event/manageEvent");
		
		try	{
			Event event = eventDao.findEvent(id);
			Message message = new Message(messageUsername, messageText, new Date(), event);
			eventDao.updateMessage(message);
			
			mav.addObject("event", event);
			mav.addObject("eventType", event.getEventTypes().get(0).getName());
			mav.addObject("participants", event.getParticipations());
			mav.addObject("messages", messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC"));
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}

		return mav;
	}
	
	@RequestMapping(value = "/getMessageQuery", method = RequestMethod.GET)
	public ResponseEntity<String> getMessageQuery(HttpServletResponse response, @RequestParam(value = "id") Integer id)
	{
		StringBuilder builder = new StringBuilder();
		List<Message> messages = messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC");
		
		for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();)
		{
			Message msg = iterator.next();
			builder.append(msg.getDate()).append("/separator/").append(msg.getAuthor()).append("/separator/").append(msg.getText());
			if(iterator.hasNext())
                builder.append("/#/");
		}
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	    return new ResponseEntity<>(builder.toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/saveMessageQuery", method = RequestMethod.GET)
	public ResponseEntity<String> saveMessageQuery(@RequestParam(value = "messageUsername") String messageUsername, @RequestParam(value = "messageText") String messageText, @RequestParam(value = "id") Integer id)
	{
		StringBuilder builder = new StringBuilder();
		try {
			Event event = eventDao.findEvent(id);
			Message message = new Message(messageUsername, messageText, new Date(), event);
			eventDao.updateMessage(message);
			
			List<Message> messages = messageDao.getMessages(MAX_MESSAGES, id, "date", "DESC");
			
			for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();)
			{
				Message msg = iterator.next();
				builder.append(msg.getDate()).append("/separator/").append(msg.getAuthor()).append("/separator/").append(msg.getText());
				if(iterator.hasNext())
                    builder.append("/#/");
			}
		}
		catch (EventNotFoundException e) {
			e.printStackTrace();
		}
		
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	    return new ResponseEntity<>(builder.toString(), responseHeaders, HttpStatus.CREATED);
	}

	private void validateDate(Event event, BindingResult result)
	{
		String startDate = event.getStartDate();
		String startTime = event.getStartTimestamp();
        startTime = startTime.contains(":") ? startTime : startTime + ":00";
		String startTimestamp = startDate + "T" + startTime;
		
		String endDate = event.getEndDate();
		String endTime = event.getEndTimestamp();
        endTime = endTime.contains(":") ? endTime : endTime + ":00";
		String endTimestamp = endDate + "T" + endTime;
  
		String dateRegex = "^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$";
		String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
  
		// Checking if startdate is in the future
		if(startDate.matches(dateRegex))
		{
			if(DateManagement.compareTimestamps(startTimestamp, DateManagement.getCurrentTimestamp()) == 1)
                result.addError(new FieldError("event", "startDate", messageSource.getMessage("error.startDateIsPast", null, Locale.getDefault())));
		}
		
		// Checking if enddate is in the past
		if(endDate.matches(dateRegex))
		{
			if(DateManagement.compareTimestamps(endTimestamp, DateManagement.getCurrentTimestamp()) == 1)
                result.addError(new FieldError("event", "endDate", messageSource.getMessage("error.endDateIsPast", null, Locale.getDefault())));
		}
		
        // Checking if startdate is earlier than enddate
		if(startDate.matches(dateRegex) && endDate.matches(dateRegex) && startTime.matches(timeRegex) && endTime.matches(timeRegex))
		{
			if(DateManagement.compareTimestamps(startTimestamp, endTimestamp) == -1) {
				result.addError(new FieldError("event", "startTimestamp", messageSource.getMessage("error.startAfterEnd", null, Locale.getDefault())));
				// result.addError(new FieldError("event", "endTimestamp", messageSource.getMessage("error.startAfterEnd", null, Locale.getDefault())));
			}
		}
	}
	
	@ModelAttribute("types")
	public ArrayList<String> getList()
	{
	    ArrayList<String> itemList = new ArrayList<>();
        // TODO allow only extended event types
	    // itemList.add("Public");
	    itemList.add("Extended");
	    // itemList.add("Private");
	    return itemList;
	}
	// -----------------------------------------------------------------------------------------------------
}
