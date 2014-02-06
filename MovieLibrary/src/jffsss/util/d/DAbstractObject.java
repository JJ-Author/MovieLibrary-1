package jffsss.util.d;

import java.util.List;
import java.util.Map;

import jffsss.ParseException;

public class DAbstractObject implements DObject
{
	public DAbstractObject()
	{}

	@Override
	public boolean isString()
	{
		return false;
	}

	@Override
	public String asString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean parseAsBoolean() throws ParseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean parseAsBoolean(Boolean _DefaultValue)
	{
		try
		{
			return this.parseAsBoolean();
		}
		catch (ParseException e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Integer parseAsInteger() throws ParseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer parseAsInteger(Integer _DefaultValue)
	{
		try
		{
			return this.parseAsInteger();
		}
		catch (ParseException e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Long parseAsLong() throws ParseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Long parseAsLong(Long _DefaultValue)
	{
		try
		{
			return this.parseAsLong();
		}
		catch (ParseException e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Double parseAsDouble() throws ParseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Double parseAsDouble(Double _DefaultValue)
	{
		try
		{
			return this.parseAsDouble();
		}
		catch (ParseException e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public boolean isList()
	{
		return false;
	}

	@Override
	public List<DObject> asList()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMap()
	{
		return false;
	}

	@Override
	public Map<String, DObject> asMap()
	{
		throw new UnsupportedOperationException();
	}
}