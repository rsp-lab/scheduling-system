package pl.radek.ss.exception;

@SuppressWarnings("serial")
public class ParticipationNotFoundException extends Exception
{
	public ParticipationNotFoundException() { }
	public ParticipationNotFoundException(String message)
	{ 
		super(message);
	}
}
