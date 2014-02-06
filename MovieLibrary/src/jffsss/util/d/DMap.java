package jffsss.util.d;

import java.util.HashMap;
import java.util.Map;

public class DMap extends DAbstractObject
{
	private Map<String, DObject> _Value;

	public DMap()
	{
		this._Value = new HashMap<String, DObject>();
	}

	public DMap(Map<String, DObject> _Value)
	{
		this();
		this._Value.putAll(_Value);
	}

	@Override
	public boolean isMap()
	{
		return true;
	}

	@Override
	public Map<String, DObject> asMap()
	{
		return this._Value;
	}

	@Override
	public String toString()
	{
		StringBuilder _StringBuilder = new StringBuilder();
		for (Map.Entry<String, DObject> _Entry : this._Value.entrySet())
		{
			if (_StringBuilder.length() > 0)
				_StringBuilder.append(", ");
			_StringBuilder.append(_Entry.getKey() + " => " + _Entry.getValue());
		}
		return "{" + _StringBuilder.toString() + "}";
	}
}