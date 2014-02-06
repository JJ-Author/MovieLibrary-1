package jffsss.api;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.util.d.DList;
import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HideMyAssApi
{
	public HideMyAssApi()
	{}

	public DObject requestProxies(Integer _Page)
	{
		return this.executeAPI("http://www.hidemyass.com/proxy-list/" + _Page);
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
		for (Element _ResponseTag : _Response.select("listtable tbody > tr"))
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