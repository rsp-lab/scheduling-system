package pl.radek.ss.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.radek.ss.domain.Account;
import pl.radek.ss.exception.AccountNotFoundException;
import pl.radek.ss.exception.UserExistsException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public class AccountDaoImpl implements AccountDao
{	
	// -----------------------------------------------------------------------------------------------------
    @PersistenceContext
    private EntityManager entityManager;
    
	private final PasswordEncoder passwordEncoder;
    
    public AccountDaoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    // -----------------------------------------------------------------------------------------------------
	public Account findUsername(String username) throws AccountNotFoundException {
		try	{
			Query query = entityManager.createQuery(QueryDB.FIND_USERNAME);
			query.setParameter("username", username);
			return (Account) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new AccountNotFoundException();
		}
	}

	public void save(Account account)
	{
		entityManager.persist(account);
	}
	
	public List<Account> findUsers(String name) {
		String queryDB = QueryDB.FIND_USERS;
		queryDB = queryDB.replace("#name", "'%" + name.toUpperCase() + "%'");
		Query query = entityManager.createQuery(queryDB);
		return query.getResultList();
	}
	
	public void registerAccount(Account account) throws UserExistsException {
		try	{
			findUsername(account.getUsername());
			throw new UserExistsException();
		}
		catch (AccountNotFoundException une) {
			entityManager.persist(account);
			account.setPassword(passwordEncoder.encode(account.getPassword()));
		}
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		try	{
			return findUsername(username);
		} 
		catch (AccountNotFoundException e) {
			throw new UsernameNotFoundException(username);
		}
	}

	public List<Account> getAccounts(Integer limit, String orderBy, String orderType) {
		String queryDB = QueryDB.GET_ACCOUNTS;
		queryDB = queryDB.replace("#orderBy", orderBy);
		queryDB = queryDB.replace("#orderType", orderType);
		Query query = entityManager.createQuery(queryDB);
		return query.setMaxResults(limit).getResultList();
	}

	public Account find(Integer id) throws AccountNotFoundException {
		try	{
			return entityManager.find(Account.class, id);
		}
		catch(NoResultException nre) {
			throw new AccountNotFoundException();
		}
	}
	
	public void delete(Integer id) throws AccountNotFoundException {
		Account account = find(id);
		entityManager.remove(account);
	}

	public void update(Account account)
	{
		entityManager.merge(account);
	}

	public void updateLastLogin(Account account) {
		account.setLastLogin(new Date());
		update(account);
	}
	// -----------------------------------------------------------------------------------------------------
}
