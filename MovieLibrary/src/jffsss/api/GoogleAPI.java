package jffsss.api;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jffsss.util.RequestLimiter;
import jffsss.util.Utils;
import jffsss.util.d.DList;
import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

public class GoogleAPI
{
	static RequestLimiter request_limiter = new RequestLimiter(1);
	public GoogleAPI()
	{}

	public DObject requestSearch(String _Query, Integer _Page, Integer _Count)
	{
		Date d = new Date(System.currentTimeMillis()); 
		SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss.SSS");
		long s_time = request_limiter.getWaitTime(); 
		System.out.println("Google Thread "+Thread.currentThread().getId()+" got sleep time of "+s_time+" at \t\t"+ft.format(d));
		try {
			Thread.sleep(s_time);
		} catch (InterruptedException e) {
			System.out.println("<<<<<<< Thread interrupted before end of sleep Google API>>>>>>>>>>>>>>>>");
		} 
		d = new Date(System.currentTimeMillis()); 
		System.out.println("Google Thread "+Thread.currentThread().getId()+" woke up at "+ft.format(d));
		
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("q", _Query);
		_Params.put("start", _Page);
		_Params.put("num", _Count);
		return this.executeAPI("https://www.google.com/search", _Params);
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
					return parseResponse(Jsoup.parse(_InputStream, null, _URL));
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
			throw new RuntimeException("GoogleAPI: " + e.getMessage());
		}
	}

	private static DObject parseResponse(Document _Response)
	{
		List<DObject> _Result = new ArrayList<DObject>();
		for (Element _ResponseTag : _Response.select("p:has(a)"))
			try
			{
				Map<String, DObject> _ResultMap = new HashMap<String, DObject>();
				_ResultMap.put("Title", new DString(_ResponseTag.text()));
				Element _ResponseTagTag = _ResponseTag.nextElementSibling().select("td.j > font").first();
				{
					String _Text = _ResponseTagTag.getElementsByAttributeValue("color", "green").first().text();
					if (_Text.contains(" "))
						_Text = _Text.substring(0, _Text.indexOf(" "));
					_ResultMap.put("Link", new DString(_Text));
				}
				_ResponseTagTag.getElementsByTag("font").remove();
				_ResultMap.put("Snippet", new DString(_ResponseTagTag.text()));
				_Result.add(new DMap(_ResultMap));
			}
			catch (Exception e)
			{}
		return new DList(_Result);
	}
}