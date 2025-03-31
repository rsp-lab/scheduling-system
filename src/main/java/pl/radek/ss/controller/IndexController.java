package pl.radek.ss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pl.radek.ss.dao.AccountDao;
import pl.radek.ss.domain.Account;
import pl.radek.ss.exception.AccountNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class IndexController
{
	// -----------------------------------------------------------------------------------------------------
	private final AccountDao accountDao;
    
    public IndexController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    // -----------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, Principal principal) {
		try {
			Account account = accountDao.findUsername(principal.getName());
			accountDao.updateLastLogin(account);
			
			if (request.isUserInRole("ROLE_ADMIN"))
                return "/administrator/page";
			if (request.isUserInRole("ROLE_USER"))
                return "/user/page";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
        return "start";
	}
    
    @RequestMapping(value = "/index/auth", method = RequestMethod.POST)
    public ModelAndView authenticationFail() {
        ModelAndView mav = new ModelAndView("/");
        mav.setViewName("redirect:/?error=true");
        return mav;
    }
	// -----------------------------------------------------------------------------------------------------
}
