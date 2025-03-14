package pl.com.pk.sutip.exception;

@SuppressWarnings("serial")
public class ParticipationNotFoundException extends Exception
{
	public ParticipationNotFoundException() { }
	public ParticipationNotFoundException(String message)
	{ 
		super(message);
	}
}
