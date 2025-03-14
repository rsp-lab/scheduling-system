package pl.com.pk.sutip.dao;

import java.util.List;

import pl.com.pk.sutip.domain.Account;
import pl.com.pk.sutip.exception.AccountNotFoundException;
import pl.com.pk.sutip.exception.UserExistsException;

public interface AccountDao extends org.springframework.security.core.userdetails.UserDetailsService
{	
	// -----------------------------------------------------------------------------------------------------
	public Account findUsername(String username) throws AccountNotFoundException;
	public List<Account> findUsers(String name) throws AccountNotFoundException;
	public void save(Account account);
	public Account find(Integer id) throws AccountNotFoundException;
	public void delete(Integer id) throws AccountNotFoundException;
	public void update(Account account);
	public void updateLastLogin(Account account);
	public void registerAccount(Account account) throws UserExistsException;
	public List<Account> getAccounts(Integer limit, String orderBy, String orderType);
	// -----------------------------------------------------------------------------------------------------
}
