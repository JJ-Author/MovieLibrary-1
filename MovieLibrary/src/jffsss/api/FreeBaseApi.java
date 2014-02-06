package jffsss.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jffsss.ParseException;
import jffsss.util.Utils;
import jffsss.util.d.D;
import jffsss.util.d.DObject;
import jffsss.util.RequestLimiter;

public class FreeBaseApi
{
	private static RequestLimiter _RequestLimiter = new RequestLimiter(30);

	public FreeBaseApi()
	{}

	public DObject requestSearch2(Boolean _Ident, String _Query, String _Filter, String _Output, Integer _Limit, String _Lang) throws IOException, ParseException
	{
		for (int i = 0; i < 3; i++)
		{
			try
			{
				long _WaitTime = _RequestLimiter.getWaitTime();

				Thread.sleep(_WaitTime);
				DObject _Response = this.requestSearch(_Ident, _Query, _Filter, _Output, _Limit, _Lang);
				if (_Response.asMap().get("StatusCode").parseAsInteger() != 200)
					continue;
				return _Response;
			}
			catch (InterruptedException e)
			{
				System.out.println("<<<<<<< Thread interrupted before end of sleep Freebase API>>>>>>>>>>>>>>>>");
			}
		}
		throw new IOException("StatusCode != 200");
	}

	public DObject requestSearch(Boolean _Ident, String _Query, String _Filter, String _Output, Integer _Limit, String _Lang) throws IOException, ParseException
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("ident", _Ident);
		_Params.put("query", _Query);
		_Params.put("filter", _Filter);
		_Params.put("output", _Output);
		_Params.put("limit", _Limit);
		_Params.put("lang", _Lang);
		_Params.put("key", "AIzaSyBfL-V2dsatdGVKyOZn3iYPQ3tVUbx7Tcg");
		return this.executeAPI("https://www.googleapis.com/freebase/v1/search", _Params);
	}

	private DObject executeAPI(String _BaseUrl, Map<String, Object> _Params) throws IOException, ParseException
	{
		return this.executeAPI(Utils.buildURL(_BaseUrl, _Params));
	}

	private DObject executeAPI(String _Url) throws IOException, ParseException
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