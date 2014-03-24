package jffsss.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jffsss.ParseException;
import jffsss.util.d.D;
import jffsss.util.d.DObject;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * OpenSubtitlesApi ist eine Schnittstelle für die Anfragen an die OpenSubtitles-Seite.
 */
public class OpenSubtitlesApi
{
	private XmlRpcClient _Client;

	/**
	 * Konstruiert eine direkte OpenSubtitles-Schnittstelle.
	 */
	public OpenSubtitlesApi()
	{
		this._Client = new XmlRpcClient();
		XmlRpcClientConfigImpl _Config = new XmlRpcClientConfigImpl();
		try
		{
			_Config.setServerURL(new URL("http://api.opensubtitles.org/xml-rpc"));
		}
		catch (MalformedURLException e)
		{}
		this._Client.setConfig(_Config);
	}

	/**
	 * Sendet eine LogIn-Anfrage, um einen Session-Token zu erhalten. Der Token wird gebraucht, um andere Anfragen
	 * auszuführen.
	 * 
	 * @param _Username
	 *            ein Benutzername
	 * @param _Password
	 *            ein Passwort
	 * @return der Session-Token
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public String requestLogIn(String _Username, String _Password) throws IOException, ParseException
	{
		List<String> _Params = new ArrayList<String>();
		_Params.add(_Username);
		_Params.add(_Password);
		_Params.add("en");
		_Params.add("OS Test User Agent");
		DObject _Response = this.executeApi("LogIn", _Params);
		try
		{
			return _Response.asMap().get("token").asString();
		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage());
		}
	}

	/**
	 * Sendet eine LogOut-Anfrage, um die Session zu beendet.
	 * 
	 * @param _Token
	 *            ein Session-Token
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public void requestLogOut(String _Token) throws IOException, ParseException
	{
		List<String> _Params = new ArrayList<String>();
		_Params.add(_Token);
		this.executeApi("LogOut", _Params);
	}

	/**
	 * Sendet eine CheckMovieHash2-Anfrage, um die Informationen über die Filme anhand der Haschsummen der Videodateien.
	 * 
	 * @param _Token
	 *            ein Session-Token
	 * @param _Hashes
	 *            die Haschsummen der Videodateien
	 * @return das Response-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public DObject requestCheckMovieHash2(String _Token, List<String> _Hashes) throws IOException, ParseException
	{
		List<Object> _Params = new ArrayList<Object>();
		_Params.add(_Token);
		_Params.add(_Hashes);
		DObject _Response = executeApi("CheckMovieHash2", _Params);
		try
		{
			int _StatusCode = getStatusCode(_Response.asMap().get("status").asString());
			return new HttpApiResponse(_StatusCode, _Response.asMap().get("data"));
		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage());
		}
	}

	/**
	 * Sendet eine Post-Anfrage an die OpenSubtitles-Seite und erhält das Response-Objekt zurück.
	 * 
	 * @param _Method
	 *            der Name einer Request-Methode
	 * @param _Params
	 *            die Parameter der Request-Methode
	 * @return das Response-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	private DObject executeApi(String _Method, List<?> _Params) throws IOException, ParseException
	{
		try
		{
			return D.fromXmlRpc(this._Client.execute(_Method, _Params));
		}
		catch (XmlRpcException e)
		{
			throw new IOException();
		}
	}

	/**
	 * Extrahiert den Statuscode aus der Response-Status-Line.
	 * 
	 * @param _String
	 *            eine Response-Status-Line
	 * @return der extrahierte Statuscode
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	private static int getStatusCode(String _String) throws ParseException
	{
		try
		{
			return Integer.parseInt(_String.split(" ", 2)[0]);
		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage());
		}
	}
}