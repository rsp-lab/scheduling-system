package pl.com.pk.sutip.exception;

@SuppressWarnings("serial")
public class EventNotFoundException extends Exception
{
	public EventNotFoundException() { }
	public EventNotFoundException(String message)
	{ 
		super(message);
	}
}
