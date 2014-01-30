package jffsss.util.d;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class D
{
	private D()
	{}

	public static DObject fromXmlRpc(Object _Object)
	{
		try
		{
			if (_Object == null)
			{
				return null;
			}
			else if (_Object instanceof Map)
			{
				Map<?, ?> _Map = (Map<?, ?>) _Object;
				Map<String, DObject> _Result = new HashMap<String, DObject>();
				for (Map.Entry<?, ?> _MapEntry : _Map.entrySet())
				{
					String _ResultKey = _MapEntry.getKey().toString();
					DObject _ResultValue = fromXmlRpc(_MapEntry.getValue());
					_Result.put(_ResultKey, _ResultValue);
				}
				return new DMap(_Result);
			}
			else if (_Object instanceof Object[])
			{
				Object[] _Array = (Object[]) _Object;
				if (_Array.length > 0)
				{
					List<DObject> _Result = new ArrayList<DObject>();
					for (Object _ArrayElement : _Array)
						_Result.add(fromXmlRpc(_ArrayElement));
					return new DList(_Result);
				}
			}
			else
			{
				return new DString(_Object.toString());
			}
		}
		catch (Exception e)
		{}
		throw new RuntimeException("Parse");
	}

	public static DObject fromJson(InputStream _InputStream)
	{
		return fromJson(new InputStreamReader(_InputStream));
	}

	public static DObject fromJson(Reader _Reader)
	{
		JsonParser _JsonParser = new JsonParser();
		JsonElement _JsonElement;
		try
		{
			_JsonElement = _JsonParser.parse(_Reader);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return fromJson(_JsonElement);
	}

	public static DObject fromJson(JsonElement _JsonElement)
	{
		try
		{
			if (_JsonElement == null)
			{
				return null;
			}
			else if (_JsonElement.isJsonNull())
			{
				return null;
			}
			else if (_JsonElement.isJsonPrimitive())
			{
				JsonPrimitive _JsonPrimitive = _JsonElement.getAsJsonPrimitive();
				return new DString(_JsonPrimitive.getAsString());
			}
			else if (_JsonElement.isJsonArray())
			{
				JsonArray _JsonArray = _JsonElement.getAsJsonArray();
				List<DObject> _Result = new ArrayList<DObject>();
				for (JsonElement _JsonArrayElement : _JsonArray)
					_Result.add(fromJson(_JsonArrayElement));
				return new DList(_Result);
			}
			else if (_JsonElement.isJsonObject())
			{
				JsonObject _JsonObject = _JsonElement.getAsJsonObject();
				Map<String, DObject> _Result = new HashMap<String, DObject>();
				for (Map.Entry<String, JsonElement> _JsonObjectEntry : _JsonObject.entrySet())
				{
					String _ResultKey = _JsonObjectEntry.getKey();
					DObject _ResultValue = fromJson(_JsonObjectEntry.getValue());
					_Result.put(_ResultKey, _ResultValue);
				}
				return new DMap(_Result);
			}
		}
		catch (Exception e)
		{}
		throw new RuntimeException("Parse");
	}
}