package jffsss.util.d;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * D ist eine Parser-Klasse, um dynamische Strukturen zu erstellen.
 */
public class D
{
	private D()
	{}

	/**
	 * Parst die dynamische Struktur aus einem XML-Objekt.
	 * 
	 * @param _Object
	 *            ein XML-Objekt
	 * @return die dynamische Struktur
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public static DObject fromXmlRpc(Object _Object) throws ParseException
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
					String _ResultKey = String.valueOf(_MapEntry.getKey());
					DObject _ResultValue = fromXmlRpc(_MapEntry.getValue());
					_Result.put(_ResultKey, _ResultValue);
				}
				return new DMap(_Result);
			}
			else if (_Object instanceof Object[])
			{
				Object[] _Array = (Object[]) _Object;
				List<DObject> _Result = new ArrayList<DObject>();
				if (_Array.length > 0)
				{
					for (Object _ArrayElement : _Array)
					{
						_Result.add(fromXmlRpc(_ArrayElement));
					}
				}
				return new DList(_Result);
			}
			else
			{
				return new DString(String.valueOf(_Object));
			}
		}
		catch (Exception e)
		{}
		throw new ParseException();
	}

	/**
	 * Parst die dynamische Struktur aus einem Eingabe-Stream.
	 * 
	 * @param _InputStream
	 *            ein Eingabe-Stream
	 * @return die dynamische Struktur
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public static DObject fromJson(InputStream _InputStream) throws IOException, ParseException
	{
		return fromJson(new InputStreamReader(_InputStream,"UTF-8"));
	}

	public static DObject fromJson(Reader _Reader) throws IOException, ParseException
	{
		JsonParser _JsonParser = new JsonParser();
		JsonElement _JsonElement;
		try
		{
			_JsonElement = _JsonParser.parse(_Reader);
		}
		catch (JsonIOException e)
		{
			throw new IOException(e.getMessage());
		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage());
		}
		return fromJson(_JsonElement);
	}

	/**
	 * Parst die dynamische Struktur aus einem JSON-Element.
	 * 
	 * @param _JsonElement
	 *            ein JSON-Element
	 * @return die dynamische Struktur
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public static DObject fromJson(JsonElement _JsonElement) throws ParseException
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
				{
					_Result.add(fromJson(_JsonArrayElement));
				}
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
		throw new ParseException();
	}
}