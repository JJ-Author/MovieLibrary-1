package jffsss.api;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jffsss.util.Utils;
import jffsss.util.d.D;
import jffsss.util.d.DObject;

public class OMDbAPI
{
	public OMDbAPI()
	{}

	public DObject requestMovieByID(String _IMDbID)
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("i", "tt" + _IMDbID);
		return this.executeAPI("http://www.omdbapi.com/", _Params);
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
			throw new RuntimeException("OMDbAPI: " + e.getMessage());
		}
	}
}