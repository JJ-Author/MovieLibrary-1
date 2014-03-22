package jffsss.util.d;

import jffsss.ParseException;

/**
 * Die String-Implementation der dynamischen Struktur.
 */
public class DString extends DAbstractObject
{
	private String _Value;

	/**
	 * Konstruiert einen String.
	 * 
	 * @param _Value
	 *            ein Vorgabe-Wert
	 */
	public DString(String _Value)
	{
		this._Value = _Value;
	}

	/**
	 * Konstruiert einen String aus dem Integer-Wert.
	 * 
	 * @param _Value
	 *            ein Vorgabe-Wert
	 */
	public DString(int _Value)
	{
		this(String.valueOf(_Value));
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
	public Boolean parseAsBoolean() throws ParseException
	{
		try
		{
			return Boolean.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new ParseException();
		}
	}

	@Override
	public Integer parseAsInteger() throws ParseException
	{
		try
		{
			return Integer.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new ParseException();
		}
	}

	@Override
	public Long parseAsLong() throws ParseException
	{
		try
		{
			return Long.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new ParseException();
		}
	}

	@Override
	public Double parseAsDouble() throws ParseException
	{
		try
		{
			return Double.valueOf(this._Value);
		}
		catch (Exception e)
		{
			throw new ParseException();
		}
	}

	@Override
	public String toString()
	{
		return this._Value;
	}
}