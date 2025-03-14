package pl.com.pk.sutip.dao;

import java.util.List;

import pl.com.pk.sutip.domain.Message;

public interface MessageDao
{
	// -----------------------------------------------------------------------------------------------------
	public List<Message> getMessages(Integer limit, Integer id, String orderBy, String orderType);
	// -----------------------------------------------------------------------------------------------------
} 