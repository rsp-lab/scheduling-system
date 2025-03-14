package pl.com.pk.sutip.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.com.pk.sutip.domain.Message;

@Transactional
@Repository
public class MessageDaoImpl implements MessageDao
{
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	EntityManager entityManager;
	// -----------------------------------------------------------------------------------------------------
	public List<Message> getMessages(Integer limit, Integer id, String orderBy, String orderType)
	{
		String queryDB = QueryDB.GET_MESSAGES;
		queryDB = queryDB.replaceAll("#orderBy", orderBy);
		queryDB = queryDB.replaceAll("#orderType", orderType);
		Query query = entityManager.createQuery(queryDB);
		query.setParameter("id", id);
		
		return query.setMaxResults(limit).getResultList();
	}
	// -----------------------------------------------------------------------------------------------------
}
