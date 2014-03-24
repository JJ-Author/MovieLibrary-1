package jffsss.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.util.d.DList;
import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.PropertyValue;
import com.osbcp.cssparser.Rule;
import com.osbcp.cssparser.Selector;

/**
 * Wird nicht mehr benutzt.
 */
public class HideMyAssApi
{
	public HideMyAssApi()
	{}

	public DObject requestProxies(Integer _Page) throws IOException, ParseException
	{
		return this.executeAPI("http://www.hidemyass.com/proxy-list/" + _Page);
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

	private static DObject parseResponse(Document _Response) throws ParseException
	{
		List<DObject> _Result = new ArrayList<DObject>();
		for (Element _ResponseTag : _Response.select("#listtable tbody > tr"))
		{
			try
			{
				Map<String, DObject> _ResultMap = new HashMap<String, DObject>();
				{
					Element _ResponseTagTag = _ResponseTag.child(1).clone();
					for (Element _Element : _ResponseTagTag.getElementsByAttributeValue("style", "display:none"))
					{
						_Element.remove();
					}
					List<Rule> _Rules = CSSParser.parse(_ResponseTagTag.child(0).child(0).html().replace("}", ";}"));
					for (Rule _Rule : _Rules)
					{
						List<PropertyValue> propertyValues = _Rule.getPropertyValues();
						for (PropertyValue propertyValue : propertyValues)
						{
							if (propertyValue.getProperty().equalsIgnoreCase("display") && propertyValue.getValue().equalsIgnoreCase("none"))
							{
								List<Selector> _Selectors = _Rule.getSelectors();
								for (Selector _Selector : _Selectors)
								{
									for (Element _Element : _ResponseTagTag.getElementsByClass(_Selector.toString().substring(1)))
										_Element.remove();
								}
								break;
							}
						}
					}
					_ResultMap.put("Host", new DString(_ResponseTagTag.text()));
				}
				_ResultMap.put("Port", new DString(_ResponseTag.child(2).text()));
				_ResultMap.put("Type", new DString(_ResponseTag.child(6).text()));
				_Result.add(new DMap(_ResultMap));
			}
			catch (Exception e)
			{}
		}
		return new DList(_Result);
	}
}