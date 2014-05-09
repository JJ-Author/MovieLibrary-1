package jffsss.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jffsss.ParseException;
import jffsss.util.Utils;
import jffsss.util.d.D;
import jffsss.util.d.DMap;

public class OmdbApi
{
	public OmdbApi()
	{}

	public DMap requestMovieById(String _ImdbId) throws IOException, ParseException
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("i", "tt" + _ImdbId);
		return this.executeApi("http://www.omdbapi.com/", _Params);
	}

	private DMap executeApi(String _BaseUrl, Map<String, Object> _Params) throws IOException, ParseException
	{
		return this.executeApi(Utils.buildURL(_BaseUrl, _Params));
	}

	private DMap executeApi(String _Url) throws IOException, ParseException
	{
		HttpURLConnection _Connection = (HttpURLConnection) (new URL(_Url)).openConnection();
		try
		{
			_Connection.setDoOutput(false);
			_Connection.setDoInput(true);
			_Connection.setRequestMethod("GET");
			_Connection.setRequestProperty("User-Agent", "Mozilla/4.0");
			_Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			_Connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			_Connection.setRequestProperty("Accept-Charset", "UTF-8");
			int _StatusCode = _Connection.getResponseCode();
			if (_StatusCode == 200)
			{
				return new HttpApiResponse(_StatusCode, D.fromJson(_Connection.getInputStream()));
			}
			else
			{
				return new HttpApiResponse(_StatusCode);
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
}