package pl.radek.ss.exception;

@SuppressWarnings("serial")
public class EventNotFoundException extends Exception
{
	public EventNotFoundException() { }
	public EventNotFoundException(String message)
	{ 
		super(message);
	}
}
