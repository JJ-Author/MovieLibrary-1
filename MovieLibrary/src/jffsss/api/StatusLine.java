package jffsss.api;

public enum StatusLine
{
	OK(200, "OK"),
	PARTIAL_CONTENT(206, "Partial Content"),
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	UNAUTHORIZED(401, "Unauthorized"),
	PAYMENT_REQUIRED(402, "Payment Required"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	PROXY_AUTHENTICATION_EQUIRED(407, "Proxy Authentication Required"),
	REQUEST_TIMEOUT(408, " Request Timeout"),
	CONFLICT(409, "Conflict"),
	GONE(410, "Gone"),
	LENGTH_REQUIRED(411, "Length Required"),
	PRECONDITION_FAILED(412, "Precondition Failed"),
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
	REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	SERVICE_UNAVAILABLE(503, "Service Unavailable");

	private Integer _StatusCode;
	private String _ReasonPhrase;

	StatusLine(Integer _StatusCode, String _ReasonPhrase)
	{
		this._StatusCode = _StatusCode;
		this._ReasonPhrase = _ReasonPhrase;
	}

	public boolean isSuccess()
	{
		switch (this)
		{
			case OK:
			case PARTIAL_CONTENT:
				return true;
			default:
				return false;
		}
	}

	public Integer getStatusCode()
	{
		return this._StatusCode;
	}

	public String getReasonPhrase()
	{
		return this._ReasonPhrase;
	}

	@Override
	public String toString()
	{
		return String.valueOf(this._StatusCode);
	}

	public static StatusLine fromStatusCode(String _StatusCode)
	{
		try
		{
			return fromStatusCode(Integer.valueOf(_StatusCode));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse");
		}
	}

	public static StatusLine fromStatusCode(Integer _StatusCode)
	{
		for (StatusLine _StatusLine : StatusLine.values())
			if (_StatusLine.getStatusCode().equals(_StatusCode))
				return _StatusLine;
		throw new RuntimeException("Parse");
	}
}