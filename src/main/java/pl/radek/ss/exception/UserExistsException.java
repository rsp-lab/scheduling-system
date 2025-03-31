package pl.radek.ss.exception;

@SuppressWarnings("serial")
public class UserExistsException extends Exception
{
	public UserExistsException() { }
	public UserExistsException(String message)
	{ 
		super(message);
	}
}
