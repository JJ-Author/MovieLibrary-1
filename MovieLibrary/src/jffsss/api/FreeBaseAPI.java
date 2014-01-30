package jffsss.api;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jffsss.util.Utils;
import jffsss.util.d.D;
import jffsss.util.d.DObject;

public class FreeBaseAPI
{
	public FreeBaseAPI()
	{}

	public DObject requestSearch(Boolean _Ident, String _Query, String _Filter, String _Output, Integer _Limit, String _Lang)
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("ident", _Ident);
		_Params.put("query", _Query);
		_Params.put("filter", _Filter);
		_Params.put("output", _Output);
		_Params.put("limit", _Limit);
		_Params.put("lang", _Lang);
		_Params.put("key", "189173206573");
		return this.executeAPI("https://www.googleapis.com/freebase/v1/search", _Params);
	}

	private DObject executeAPI(String _BaseURL, Map<String, Object> _Params)
	{
		return this.executeAPI(Utils.buildURL(_BaseURL, _Params));
	}

	private DObject executeAPI(String _URL)
	{
		try
		{
			HttpURLConnection _Connection = (HttpURLConnection) (new URL(_URL)).openConnection();
			try
			{
				_Connection.setDoOutput(false);
				_Connection.setDoInput(true);
				_Connection.setRequestMethod("GET");
				_Connection.setRequestProperty("User-Agent", "Mozilla/4.0");
				_Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				_Connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
				InputStream _InputStream = _Connection.getInputStream();
				try
				{
					return D.fromJson(_InputStream);
				}
				finally
				{
					try
					{
						_InputStream.close();
					}
					catch (Exception e)
					{}
				}
			}
			finally
			{
				try
				{
					_Connection.disconnect();
				}
				catch (Exception e)
				{}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("FreeBaseAPI: " + e.getMessage());
		}
	}
}