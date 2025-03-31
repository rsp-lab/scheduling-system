package pl.radek.ss.dtos;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("filter")
public class SearchFilter
{
	public SearchFilter() { }
	
	public static Integer MAX_RESULTS = 10;
	
	String name;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	Boolean user;
	public Boolean getUser()
	{
		return user;
	}
	public void setUser(Boolean user)
	{
		this.user = user;
	}
	public Boolean isUser()
	{
		return user;
	}
}
