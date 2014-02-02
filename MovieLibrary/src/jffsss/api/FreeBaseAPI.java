package jffsss.api;

import java.lang.InterruptedException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jffsss.util.Utils;
import jffsss.util.d.D;
import jffsss.util.d.DObject;
import jffsss.util.RequestLimiter;

public class FreeBaseAPI
{
	static RequestLimiter request_limiter = new RequestLimiter(30);
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
		_Params.put("key", "AIzaSyBfL-V2dsatdGVKyOZn3iYPQ3tVUbx7Tcg");
		
		DObject _Response=null;
		for (int i=0; i<3; i++)
		{
			try
			{// try to keep requests in limit (30 requests per second)
				//Date d = new Date(System.currentTimeMillis()); 
				//SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss.SSS");
				long s_time = request_limiter.getWaitTime(); 
				//System.out.println("Thread "+Thread.currentThread().getId()+" got sleep time of "+s_time+" at \t\t"+ft.format(d));
				Thread.sleep(s_time); 
				//d = new Date(System.currentTimeMillis()); 
				//System.out.println("Thread "+Thread.currentThread().getId()+" woke up at "+ft.format(d));
				_Response = this.executeAPI("https://www.googleapis.com/freebase/v1/search", _Params);
				return _Response;
			}
			catch (InterruptedException e)
			{
				System.out.println("<<<<<<< Thread interrupted before end of sleep Freebase API>>>>>>>>>>>>>>>>");
			}
			catch (Exception e)
			{
				try
				{// try to keep requests in limit (10 requests per second)
					Thread.sleep(1200/*request_limiter.getWaitTime()*/); 
					System.out.println("made some extra timeout for freebase request:"+ e.getMessage());
				}
				catch (Exception e2)
				{}
			}
		}
		return _Response;
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