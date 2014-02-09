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

public class OfdbApi
{
	private static RequestLimiter _RequestLimiter = new RequestLimiter(10);

	public OfdbApi()
	{}

	public DObject requestSearch2(String _Base, String _Query) throws IOException, ParseException
	{
		int rcode = -1;
		for (int i = 0; i < 5; i++)
		{
			try
			{
				long _WaitTime = _RequestLimiter.getWaitTime();

				Thread.sleep(_WaitTime);
				DObject _Response = this.requestSearch(_Base, _Query);
				rcode = _Response.asMap().get("Content").asMap().get("ofdbgw").asMap().get("status").asMap().get("rcode").parseAsInteger();
				if (rcode != 0)
					continue;
				return _Response;
			}
			catch (InterruptedException e)
			{
				System.out.println("<<<<<<< Thread interrupted before end of sleep OFDB API>>>>>>>>>>>>>>>>");
			}
			catch (Exception e) {
				System.out.println("OFDB Lookup Problem rcode: "+rcode+" for "+_Base+_Query);
				e.printStackTrace();
			}
		}
		throw new IOException("OFDB Lookup Failed rcode: "+rcode+" for "+_Base+_Query);
	}

	public DObject requestSearch(String _Base, String _Query) throws IOException, ParseException
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("query", _Query);
		return this.executeAPI(_Base, _Params);
	}

	private DObject executeAPI(String _BaseUrl, Map<String, Object> _Params) throws IOException, ParseException
	{
		return this.executeAPI(Utils.buildURL2(_BaseUrl, _Params));
	}

	private DObject executeAPI(String _Url) throws IOException, ParseException
	{
		System.out.println(_Url);
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