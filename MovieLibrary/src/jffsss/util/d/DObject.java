package jffsss.util.d;

import java.util.List;
import java.util.Map;

import jffsss.ParseException;

public interface DObject
{
	public boolean isString();

	public String asString();

	public Boolean parseAsBoolean() throws ParseException;

	public Boolean parseAsBoolean(Boolean _DefaultValue);

	public Integer parseAsInteger() throws ParseException;

	public Integer parseAsInteger(Integer _DefaultValue);

	public Long parseAsLong() throws ParseException;

	public Long parseAsLong(Long _DefaultValue);

	public Double parseAsDouble() throws ParseException;

	public Double parseAsDouble(Double _DefaultValue);

	public boolean isList();

	public List<DObject> asList();

	public boolean isMap();

	public Map<String, DObject> asMap();
}