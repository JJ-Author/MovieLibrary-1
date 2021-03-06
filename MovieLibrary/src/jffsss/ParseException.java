package jffsss;

/**
 * ParseException ist eine Exception, die dann aufritt, wenn das Parsen aufgrund des fehlerhaften Formats des zu
 * parsenden Inhalts gescheitert ist.
 */
public class ParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ParseException()
	{}

	public ParseException(String _Message)
	{
		super(_Message);
	}

	public ParseException(Throwable _Case)
	{
		super(_Case);
	}

	public ParseException(String _Message, Throwable _Case)
	{
		super(_Message, _Case);
	}
}