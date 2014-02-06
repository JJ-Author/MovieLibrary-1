package jffsss.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jffsss.ParseException;
import jffsss.util.Utils;
import jffsss.util.d.DList;
import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

public class GoogleApi
{
	private Proxy _Proxy;

	public GoogleApi()
	{
		this(null);
	}

	public GoogleApi(Proxy _Proxy)
	{
		this._Proxy = _Proxy;
	}

	public DObject requestSearch(String _Query, Integer _Page, Integer _Count) throws IOException, ParseException
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("q", _Query);
		_Params.put("start", _Page);
		_Params.put("num", _Count);
		return this.executeAPI("https://www.google.com/search", _Params);
	}

	private DObject executeAPI(String _BaseUrl, Map<String, Object> _Params) throws IOException, ParseException
	{
		return this.executeAPI(Utils.buildURL(_BaseUrl, _Params));
	}

	private DObject executeAPI(String _Url) throws IOException, ParseException
	{
		HttpURLConnection _Connection;
		if (this._Proxy == null)
		{
			_Connection = (HttpURLConnection) (new URL(_Url)).openConnection();
		}
		else
		{
			_Connection = (HttpURLConnection) (new URL(_Url)).openConnection(this._Proxy);
		}
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
				return new HttpApiResponse(_StatusCode, parseResponse(Jsoup.parse(_Connection.getInputStream(), null, _Url)));
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

	private static DObject parseResponse(Document _Response)
	{
		List<DObject> _ResultList = new ArrayList<DObject>();
		for (Element _ResponseTag : _Response.select("p:has(a)"))
		{
			try
			{
				Map<String, DObject> _ResultListMap = new HashMap<String, DObject>();
				_ResultListMap.put("Title", new DString(_ResponseTag.text()));
				Element _ResponseTagTag = _ResponseTag.nextElementSibling().select("td.j > font").first();
				{
					String _Text = _ResponseTagTag.getElementsByAttributeValue("color", "green").first().text();
					if (_Text.contains(" "))
					{
						_Text = _Text.substring(0, _Text.indexOf(" "));
					}
					_ResultListMap.put("Link", new DString(_Text));
				}
				_ResponseTagTag.getElementsByTag("font").remove();
				_ResultListMap.put("Snippet", new DString(_ResponseTagTag.text()));
				_ResultList.add(new DMap(_ResultListMap));
			}
			catch (Exception e)
			{}
		}
		return new DList(_ResultList);
	}
}