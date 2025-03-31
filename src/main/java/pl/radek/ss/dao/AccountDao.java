package pl.radek.ss.dao;

import pl.radek.ss.domain.Account;
import pl.radek.ss.exception.AccountNotFoundException;
import pl.radek.ss.exception.UserExistsException;

import java.util.List;

public interface AccountDao extends org.springframework.security.core.userdetails.UserDetailsService
{	
	// -----------------------------------------------------------------------------------------------------
	Account findUsername(String username) throws AccountNotFoundException;
	List<Account> findUsers(String name) throws AccountNotFoundException;
	void save(Account account);
	Account find(Integer id) throws AccountNotFoundException;
	void delete(Integer id) throws AccountNotFoundException;
	void update(Account account);
	void updateLastLogin(Account account);
	void registerAccount(Account account) throws UserExistsException;
	List<Account> getAccounts(Integer limit, String orderBy, String orderType);
	// -----------------------------------------------------------------------------------------------------
}
