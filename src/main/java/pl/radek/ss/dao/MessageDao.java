package pl.radek.ss.dao;

import pl.radek.ss.domain.Message;

import java.util.List;

public interface MessageDao
{
	// -----------------------------------------------------------------------------------------------------
	List<Message> getMessages(Integer limit, Integer id, String orderBy, String orderType);
	// -----------------------------------------------------------------------------------------------------
} 