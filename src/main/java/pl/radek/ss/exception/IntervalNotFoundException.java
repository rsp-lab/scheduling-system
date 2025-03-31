package pl.radek.ss.exception;

@SuppressWarnings("serial")
public class IntervalNotFoundException extends Exception
{
	public IntervalNotFoundException() { }
	public IntervalNotFoundException(String message)
	{ 
		super(message);
	}
}
