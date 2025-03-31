package pl.radek.ss.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.radek.ss.domain.Message;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Transactional
@Repository
public class MessageDaoImpl implements MessageDao
{
	// -----------------------------------------------------------------------------------------------------
	@PersistenceContext
	EntityManager entityManager;
	// -----------------------------------------------------------------------------------------------------
	public List<Message> getMessages(Integer limit, Integer id, String orderBy, String orderType) {
		String queryDB = QueryDB.GET_MESSAGES;
		queryDB = queryDB.replace("#orderBy", orderBy);
		queryDB = queryDB.replace("#orderType", orderType);
		Query query = entityManager.createQuery(queryDB);
		query.setParameter("id", id);
		
		return query.setMaxResults(limit).getResultList();
	}
	// -----------------------------------------------------------------------------------------------------
}
