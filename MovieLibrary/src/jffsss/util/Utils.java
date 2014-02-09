package jffsss.util;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
	private Utils()
	{}

	public static String findFirstPattern(String _String, String _Regex)
	{
		Pattern _Pattern = Pattern.compile(_Regex);
		Matcher _Matcher = _Pattern.matcher(_String);
		if (_Matcher.find())
			return _Matcher.group(1);
		throw new RuntimeException("NotFound");
	}

	public static String buildURL(String _BaseURL, Map<String, Object> _Params)
	{
		StringBuilder _StringBuilder = new StringBuilder();
		for (Map.Entry<String, Object> _Param : _Params.entrySet())
		{
			try
			{
				String _Value = URLEncoder.encode(_Param.getValue().toString(), "UTF-8");
				String _Key = _Param.getKey();
				if (_Value != null)
				{

					if (_StringBuilder.length() == 0)
						_StringBuilder.append("?");
					else
						_StringBuilder.append("&");
					_StringBuilder.append(_Key + "=" + _Value);
				}
			}
			catch (Exception e)
			{}
		}
		return _BaseURL + _StringBuilder.toString();
	}
	
	/**
	 * url encodes every value in params and simply appends to unencoded base url
	 * @param _BaseURL
	 * @param _Params
	 * @return
	 */
	public static String buildURL2(String _BaseURL, Map<String, Object> _Params)
	{
		StringBuilder _StringBuilder = new StringBuilder();
		for (Map.Entry<String, Object> _Param : _Params.entrySet())
		{
			try
			{
				String _Value = URLEncoder.encode(_Param.getValue().toString(), "UTF-8");
				if (_Value != null)
				{
					_StringBuilder.append(_Value);
				}
			}
			catch (Exception e)
			{}
		}
		return _BaseURL + _StringBuilder.toString();
	}

	public static String join(Collection<?> _Strings, String _Delimiter)
	{
		StringBuilder _StringBuilder = new StringBuilder();
		for (Object _String : _Strings)
		{
			if (_StringBuilder.length() > 0)
				_StringBuilder.append(_Delimiter);
			_StringBuilder.append(_String);
		}
		return _StringBuilder.toString();
	}

	public static List<String> split(String _String, String _Delimiter)
	{
		return Arrays.asList(_String.split(_Delimiter));
	}
}