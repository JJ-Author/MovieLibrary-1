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

/**
 * GoogleApi ist eine Schnittstelle für die Anfragen an die Google-Suchmaschine.
 */
public class GoogleApi
{
	private Proxy _Proxy;

	/**
	 * Konstruiert eine direkte Google-Schnittstelle.
	 */
	public GoogleApi()
	{
		this(null);
	}

	/**
	 * Konstruiert eine Google-Schnittstelle, dessen Anfragen über eine Proxy laufen.
	 * 
	 * @param _Proxy
	 *            eine Proxy
	 */
	public GoogleApi(Proxy _Proxy)
	{
		this._Proxy = _Proxy;
	}

	/**
	 * Sendet eine Suchanfrage.
	 * @param _Query ein Text für die Anfrage
	 * @param _Page eine Nummer der Seite
	 * @param _Count eine Anzahl der angezeigten Ergebnisse pro Seite
	 * @return das Response-Objekt
	 * @throws IOException falls ein IO-Fehler auftrat
	 * @throws ParseException falls das Parsen gescheitert ist
	 */
	public DObject requestSearch(String _Query, Integer _Page, Integer _Count) throws IOException, ParseException
	{
		Map<String, Object> _Params = new HashMap<String, Object>();
		_Params.put("q", _Query);
		_Params.put("start", _Page);
		_Params.put("num", _Count);
		return this.executeAPI("https://www.google.com/search", _Params);
	}

	/**
	 * Erstellt eine URL für die Anfrage, sendet die Anfrage an die Google-Suchmaschine und erhält das Response-Objekt zurück.
	 * @param _BaseUrl eine Basis-URL
	 * @param _Params die URL-Parameter
	 * @return das Response-Objekt
	 * @throws IOException falls ein IO-Fehler auftrat
	 * @throws ParseException falls das Parsen gescheitert ist
	 */
	private DObject executeAPI(String _BaseUrl, Map<String, Object> _Params) throws IOException, ParseException
	{
		return this.executeAPI(Utils.buildURL(_BaseUrl, _Params));
	}

	/**
	 * Sendet eine Get-Anfrage an die Google-Suchmaschine und erhält das Response-Objekt zurück.
	 * @param _Url eine angefragte URL
	 * @return das Response-Objekt
	 * @throws IOException falls ein IO-Fehler auftrat
	 * @throws ParseException falls das Parsen gescheitert ist
	 */
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

	/**
	 * Parst den Inhalt der HTML-Seite, um das Response-Objekt zu erstellen.
	 * @param _Response eine HTML-Seite
	 * @return das Response-Objekt
	 */
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