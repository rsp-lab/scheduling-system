package pl.radek.ss.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.radek.ss.dao.AccountDao;
import pl.radek.ss.dao.EventDao;
import pl.radek.ss.domain.Account;
import pl.radek.ss.domain.Event;
import pl.radek.ss.domain.Role;
import pl.radek.ss.exception.AccountNotFoundException;
import pl.radek.ss.exception.UserExistsException;
import pl.radek.ss.dtos.SearchFilter;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class AccountController
{
	// -----------------------------------------------------------------------------------------------------
	private final AccountDao accountDao;
    private final EventDao eventDao;
    private final MessageSource messageSource;
    
    public AccountController(AccountDao accountDao, EventDao eventDao, MessageSource messageSource) {
        this.accountDao = accountDao;
        this.eventDao = eventDao;
        this.messageSource = messageSource;
    }
    
    // -----------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/account/add", method = RequestMethod.GET)
	public ModelAndView add()
	{
		ModelAndView mav = new ModelAndView("/account/add");
		mav.addObject("account", new Account());
		return mav;
	}
	
	@RequestMapping(value = "/account/add", method = RequestMethod.POST)
	public ModelAndView add(@Valid Account account, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("account/add");
		 
		if (!account.getPassword().equals(account.getRepeatedPassword()))
            result.addError(new FieldError("account", "repeatedPassword",
                    messageSource.getMessage("addUser.equalPassword", null, Locale.getDefault())));
		
		if (result.hasErrors()) 
		{
			mav.addObject("account", account);
			return mav;
		}
		 
		try {
			accountDao.registerAccount(account);
		} 
		catch (UserExistsException e) {
			result.reject("user.exists");
			result.addError(new FieldError("account", "username", messageSource.getMessage("addUser.userExists", null, Locale.getDefault())));
			mav.addObject("account", account);
			return mav;
		}
		 
        mav.setViewName("redirect:/account/add");
        return mav;
	}
	
	@RequestMapping(value = "/registerAccount", method = RequestMethod.GET)
	public ModelAndView register()
	{
		ModelAndView mav = new ModelAndView("/account/registerAccount");
		mav.addObject("account", new Account());
		return mav;
	}
	 
	@RequestMapping(value = "/registerAccount", method = RequestMethod.POST)
	public ModelAndView register(@Valid Account account, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("account/registerAccount");
		 
		if (!account.getPassword().equals(account.getRepeatedPassword()))
            result.addError(new FieldError("account", "repeatedPassword",
                    messageSource.getMessage("addUser.equalPassword", null, Locale.getDefault())));
		
		if (result.hasErrors()) {
			mav.addObject("account", account);
			return mav;
		}
		
		ArrayList<Role> roles = new ArrayList<Role>();
		roles.add(new Role("ROLE_USER"));
		account.setRoles(roles);
		
		try {
			accountDao.registerAccount(account);
		} 
		catch (UserExistsException e) {
			result.reject("user.exists");
			result.addError(new FieldError("account", "username", messageSource.getMessage("addUser.userExists", null, Locale.getDefault())));
			mav.addObject("account", account);
			return mav;
		}
		 
        mav.setViewName("redirect:/index");
        return mav;
	}
	
	@RequestMapping(value = "/findAccount", method = RequestMethod.GET)
	public ModelAndView findAccount(@RequestParam(value = "order") String order)
	{
		String[] orderDetails = order.split(":");
		List<Account> accounts = accountDao.getAccounts(SearchFilter.MAX_RESULTS, orderDetails[0], orderDetails[1]);
		
		ModelAndView mav = new ModelAndView("/account/findAccount");
		mav.addObject("accounts", accounts);
		mav.addObject("orderBy", orderDetails[0]);
		mav.addObject("orderType", orderDetails[1]);
		mav.addObject("searchFilter", new SearchFilter());
		return mav;
	}
	 
	@RequestMapping(value = "/findAccount", method = RequestMethod.POST)
	public ModelAndView findAccount(SearchFilter searchFilter, BindingResult result)
	{
		ModelAndView mav = new ModelAndView("/account/findAccount");
  
		try {
			List<Account> accounts = accountDao.findUsers(searchFilter.getName());
			mav.addObject("accounts", accounts);
		}
		catch (AccountNotFoundException e) {
	        mav.setViewName("redirect:/index");
	        return mav;
		}
		
		mav.addObject("searchFilter", searchFilter);
		return mav;	
	}
	
	@RequestMapping(value = "/deleteAccount", method = RequestMethod.GET)
	public ModelAndView deleteAccount(@RequestParam(value = "id") Integer id)
	{
		try {
			accountDao.delete(id);
		}
		catch (AccountNotFoundException e) {
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/findAccount?order=registerDate:DESC");
		return mav;
	}
	 
	@RequestMapping(value = "/detailsAccount", method = RequestMethod.GET)
	public ModelAndView detailsAccount(Principal principal, @RequestParam(value = "id") Integer id)
	{
		ModelAndView mav = new ModelAndView("/account/detailsAccount");
		
		try	{
			Account account = accountDao.find(id);
			mav.addObject("events", account.getEvents());
			mav.addObject("account", account);
			mav.addObject("searchFilter", new SearchFilter());
		}
		catch (AccountNotFoundException e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/detailsAccount", method = RequestMethod.POST)
	public ModelAndView detailsAccount(SearchFilter searchFilter, BindingResult result, @RequestParam(value = "username") String username)
	{
		Account account = null;
		List<Event> events = null;
		try {
			account = accountDao.findUsername(username);
			events = eventDao.findEvent(searchFilter.getName(), "'PUBLIC','EXTENDED','PRIVATE'", account.getUsername());
		}
		catch (AccountNotFoundException e) {
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("/account/detailsAccount");
		mav.addObject("account", account);
		mav.addObject("events", events);
		mav.addObject("searchFilter", searchFilter);
		return mav;	
	}
	
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