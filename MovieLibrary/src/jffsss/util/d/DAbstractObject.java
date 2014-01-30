package jffsss.util.d;

import java.util.List;
import java.util.Map;

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
		throw new RuntimeException("Unsupported");
	}

	@Override
	public Boolean parseAsBoolean()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public Boolean parseAsBoolean(Boolean _DefaultValue)
	{
		try
		{
			return this.parseAsBoolean();
		}
		catch (Exception e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Integer parseAsInteger()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public Integer parseAsInteger(Integer _DefaultValue)
	{
		try
		{
			return this.parseAsInteger();
		}
		catch (Exception e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Long parseAsLong()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public Long parseAsLong(Long _DefaultValue)
	{
		try
		{
			return this.parseAsLong();
		}
		catch (Exception e)
		{
			return _DefaultValue;
		}
	}

	@Override
	public Double parseAsDouble()
	{
		throw new RuntimeException("Unsupported");
	}

	@Override
	public Double parseAsDouble(Double _DefaultValue)
	{
		try
		{
			return this.parseAsDouble();
		}
		catch (Exception e)
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
		throw new RuntimeException("Unsupported");
	}

	@Override
	public boolean isMap()
	{
		return false;
	}

	@Override
	public Map<String, DObject> asMap()
	{
		throw new RuntimeException("Unsupported");
	}
}