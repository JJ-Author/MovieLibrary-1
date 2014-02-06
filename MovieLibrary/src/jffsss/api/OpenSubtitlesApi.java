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

public class OpenSubtitlesApi
{
	private XmlRpcClient _Client;

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

	public void requestLogOut(String _Token) throws IOException, ParseException
	{
		List<String> _Params = new ArrayList<String>();
		_Params.add(_Token);
		this.executeApi("LogOut", _Params);
	}

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