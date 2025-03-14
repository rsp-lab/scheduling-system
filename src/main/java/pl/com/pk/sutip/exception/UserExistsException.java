package pl.com.pk.sutip.exception;

@SuppressWarnings("serial")
public class UserExistsException extends Exception
{
	public UserExistsException() { }
	public UserExistsException(String message)
	{ 
		super(message);
	}
}
