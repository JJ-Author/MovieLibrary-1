package jffsss.util.d;

public class DString extends DAbstractObject
{
	private String _Value;

	public DString(String oValue)
	{
		this._Value = oValue;
	}

	@Override
	public boolean isString()
	{
		return true;
	}

	@Override
	public String asString()
	{
		return this._Value;
	}

	@Override
	public Boolean parseAsBoolean()
	{
		try
		{
			return Boolean.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse");
		}
	}

	@Override
	public Integer parseAsInteger()
	{
		try
		{
			return Integer.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse");
		}
	}

	@Override
	public Long parseAsLong()
	{
		try
		{
			return Long.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse");
		}
	}

	@Override
	public Double parseAsDouble()
	{
		try
		{
			return Double.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse");
		}
	}

	@Override
	public String toString()
	{
		return this._Value;
	}
}