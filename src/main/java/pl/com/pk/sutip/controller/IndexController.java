package pl.com.pk.sutip.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.com.pk.sutip.dao.AccountDao;
import pl.com.pk.sutip.domain.Account;
import pl.com.pk.sutip.exception.AccountNotFoundException;

@Controller
public class IndexController
{
	// -----------------------------------------------------------------------------------------------------
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static Logger logger = Logger.getLogger(IndexController.class);
	// -----------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, Principal principal)
	{
		try
		{
			Account account = accountDao.findUsername(principal.getName());
			accountDao.updateLastLogin(account);
			
			if (request.isUserInRole("ROLE_ADMIN"))
			{
				logger.info("returning /administrator/page");
				return "/administrator/page";
			}
			if (request.isUserInRole("ROLE_USER"))
			{
				logger.info("returning /user/page");
				return "/user/page";
			}
		}
		catch (AccountNotFoundException e)
		{
			logger.info("throwing AccountNotFoundException");
			e.printStackTrace();
		}
		
        logger.info("returning /index/index");
        return "index";
	}
	// -----------------------------------------------------------------------------------------------------
}
