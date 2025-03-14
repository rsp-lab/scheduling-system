package pl.com.pk.sutip.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.dao.SaltSource;
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
import pl.com.pk.sutip.domain.Account;
import pl.com.pk.sutip.domain.Event;
import pl.com.pk.sutip.domain.Role;
import pl.com.pk.sutip.exception.AccountNotFoundException;
import pl.com.pk.sutip.exception.UserExistsException;
import pl.com.pk.sutip.wrapper.SearchFilter;

@Controller
public class AccountController
{
	// -----------------------------------------------------------------------------------------------------
	@Autowired
	AccountDao accountDao;

	@Autowired
	EventDao eventDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired 
	SaltSource saltSource;
	
	@Autowired  
	private MessageSource messageSource;
	 
	private static Logger logger = Logger.getLogger(EventController.class);
	// -----------------------------------------------------------------------------------------------------
	/*
	 *  Przygotowuje obiekt konta (Account) do utworzenia przez administratora
	 */
	@RequestMapping(value = "/account/add", method = RequestMethod.GET)
	public ModelAndView add()
	{
		logger.info("returning /account/add");
		ModelAndView mav = new ModelAndView("/account/add");
		mav.addObject("account", new Account());
		return mav;
	}
	
	/*
	 *  Tworzy obiekt konta
	 */
	@RequestMapping(value = "/account/add", method = RequestMethod.POST)
	public ModelAndView add(@Valid Account account, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("account/add");
		 
		if (!account.getPassword().equals(account.getRepeatedPassword())) 
		{
			result.addError(new FieldError("account", "repeatedPassword", messageSource.getMessage("addUser.equalPassword", null, Locale.getDefault())));
		}
		
		if (result.hasErrors()) 
		{
			mav.addObject("account", account);
			return mav;
		}
		 
		try
		{
			accountDao.registerAccount(account);
		} 
		catch (UserExistsException e)
		{
			logger.info("throwing UserExistsException");
			result.reject("user.exists");
			result.addError(new FieldError("account", "username", messageSource.getMessage("addUser.userExists", null, Locale.getDefault())));
			mav.addObject("account", account);
			return mav;
		}
		 
		logger.info("redirecting /account/add.html");
        mav.setViewName("redirect:/account/add.html");
        return mav;
	}
	
	/*
	 *  Przygotowuje obiekt konta (Account) do rejestracji
	 */
	@RequestMapping(value = "/registerAccount", method = RequestMethod.GET)
	public ModelAndView register()
	{
		logger.info("returning /account/registerAccount");
		ModelAndView mav = new ModelAndView("/account/registerAccount");
		mav.addObject("account", new Account());
		return mav;
	}
	 
	/*
	 *  Rejestruje użytkownika w systemie
	 */
	@RequestMapping(value = "/registerAccount", method = RequestMethod.POST)
	public ModelAndView register(@Valid Account account, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("account/registerAccount");
		 
		if (!account.getPassword().equals(account.getRepeatedPassword())) 
		{
			result.addError(new FieldError("account", "repeatedPassword", messageSource.getMessage("addUser.equalPassword", null, Locale.getDefault())));
		}
		
		if (result.hasErrors()) 
		{
			mav.addObject("account", account);
			return mav;
		}
		
		ArrayList<Role> roles = new ArrayList<Role>();
		roles.add(new Role("ROLE_USER"));
		account.setRoles(roles);
		
		try
		{
			accountDao.registerAccount(account);
		} 
		catch (UserExistsException e)
		{
			logger.info("throwing UserExistsException");
			result.reject("user.exists");
			result.addError(new FieldError("account", "username", messageSource.getMessage("addUser.userExists", null, Locale.getDefault())));
			mav.addObject("account", account);
			return mav;
		}
		 
		logger.info("redirecting /index.html");
        mav.setViewName("redirect:/index.html");
        return mav;
	}
	
	/*
	 *  Pobiera użytkowników spełniając odpowiednie kryteria
	 */
	@RequestMapping(value = "/findAccount", method = RequestMethod.GET)
	public ModelAndView findAccount(@RequestParam(value = "order") String order)
	{
		// filter[0] zawiera orderBy (według czego), filter[1] zawiera orderType (rosnąco lub malejąco)
		String[] orderDetails = order.split(":");
		List<Account> accounts = accountDao.getAccounts(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1]);
		
		logger.info("returning /account/findAccount");
		ModelAndView mav = new ModelAndView("/account/findAccount");
		mav.addObject("accounts", accounts);
		mav.addObject("orderBy", orderDetails[0]);
		mav.addObject("orderType", orderDetails[1]);
		mav.addObject("searchFilter", new SearchFilter());
		return mav;
	}
	 
	/*
	 *  Pobiera użytkowników, którzy pasują do przekazanej nazwy (ignoruje wielkość wprowadzonych znaków)
	 */
	@RequestMapping(value = "/findAccount", method = RequestMethod.POST)
	public ModelAndView findAccount(SearchFilter searchFilter, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("/account/findAccount");
		try
		{
			List<Account> accounts = accountDao.findUsers(searchFilter.getName());
			mav.addObject("accounts", accounts);
		}
		catch (AccountNotFoundException e)
		{
			logger.info("throwing AccountNotFoundException");
	        mav.setViewName("redirect:/index.html");
	        return mav;
		}
		
		logger.info("returning account/findAccount");
		mav.addObject("searchFilter", searchFilter);
		return mav;	
	}
	
	/*
	 *  Usuwa użytkownika
	 */
	@RequestMapping(value = "/deleteAccount", method = RequestMethod.GET)
	public ModelAndView deleteAccount(@RequestParam(value = "id") Integer id)
	{
		try
		{
			accountDao.delete(id);
		}
		catch (AccountNotFoundException e)
		{
			logger.info("throwing AccountNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning /findAccount");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findAccount.html?order=registerDate:DESC");
		return mav;
	}
	 
	/*
	 *  Przekazuje dane dotyczące użytkownika
	 */
	@RequestMapping(value = "/detailsAccount", method = RequestMethod.GET)
	public ModelAndView detailsAccount(Principal principal, @RequestParam(value = "id") Integer id)
	{
		ModelAndView mav = new ModelAndView("/account/detailsAccount");
		
		try
		{
			Account account = accountDao.find(id);
			mav.addObject("events", account.getEvents());
			mav.addObject("account", account);
			mav.addObject("searchFilter", new SearchFilter());
		}
		catch (AccountNotFoundException e)
		{
			logger.info("throwing AccountNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning /account/detailsAccount");
		return mav;	
	}
	
	/*
	 *  Przeszukuje spotkania użytkownika
	 */
	@RequestMapping(value = "/detailsAccount", method = RequestMethod.POST)
	public ModelAndView detailsAccount(SearchFilter searchFilter, BindingResult result, @RequestParam(value = "username") String username)
	{
		Account account = null;
		List<Event> events = null;
		try
		{
			account = accountDao.findUsername(username);
			events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED','PRIVATE'", account.getUsername());
		}
		catch (AccountNotFoundException e)
		{
			logger.info("throwing AccountNotFoundException");
			e.printStackTrace();
		}
		
		logger.info("returning account/detailsAccount");
		ModelAndView mav = new ModelAndView("/account/detailsAccount");
		mav.addObject("account", account);
		mav.addObject("events", events);
		mav.addObject("searchFilter", searchFilter);
		return mav;	
	}
	
	/*
	 *  Lista ról dla kontrolki select w administrator/add.jsp
	 */
	@ModelAttribute("roles")
	public ArrayList<String> getList()
	{
	    ArrayList<String> itemList = new ArrayList<String>();
	    itemList.add("ROLE_ADMIN");
	    itemList.add("ROLE_USER");
	    return itemList;
	}
	// -----------------------------------------------------------------------------------------------------
}