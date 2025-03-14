package pl.com.pk.sutip.exception;

@SuppressWarnings("serial")
public class IntervalNotFoundException extends Exception
{
	public IntervalNotFoundException() { }
	public IntervalNotFoundException(String message)
	{ 
		super(message);
	}
}
