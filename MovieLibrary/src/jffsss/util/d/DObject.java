package jffsss.util.d;

import java.util.List;
import java.util.Map;

public interface DObject
{
	public boolean isString();

	public String asString();

	public Boolean parseAsBoolean();
	
	public Boolean parseAsBoolean(Boolean _DefaultValue);

	public Integer parseAsInteger();
	
	public Integer parseAsInteger(Integer _DefaultValue);

	public Long parseAsLong();
	
	public Long parseAsLong(Long _DefaultValue);

	public Double parseAsDouble();
	
	public Double parseAsDouble(Double _DefaultValue);

	public boolean isList();

	public List<DObject> asList();

	public boolean isMap();

	public Map<String, DObject> asMap();
}