package pl.radek.ss.exception;

@SuppressWarnings("serial")
public class AccountNotFoundException extends Exception
{
	public AccountNotFoundException() { }
	public AccountNotFoundException(String message)
	{ 
		super(message);
	}
}
