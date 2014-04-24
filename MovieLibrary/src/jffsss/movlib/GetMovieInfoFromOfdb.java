package jffsss.movlib;


import java.util.Map;

import jffsss.ParseException;
import jffsss.api.OfdbApi;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetMovieInfoFromOfdb 
{
	//private String _ImdbId;

	public GetMovieInfoFromOfdb()
	{
		//this._ImdbId = _ImdbId;
	}

	
	public String getPosterURL(String _ImdbId) throws TaskExecutionException
	{
		String URL = "";
		try
		{
			OfdbApi _Api = new OfdbApi();
			String OfdbId = getOfdbIdByImdbId(_ImdbId);
			if (OfdbId!="")
			{
				DObject _Response = _Api.requestSearch2("http://ofdbgw.home-of-root.de/movie_json/",OfdbId);
				System.out.println("get Poster for OFDB-ID"+OfdbId+" "+_ImdbId+" "+_Response.toString());
				URL = parseResultProperty(_Response.asMap().get("Content"),"bild");
				System.out.println("received Poster for OFDB-ID"+OfdbId+" "+_ImdbId+" "+URL);
			}
			else
				System.out.println("did not found OFDB-ID for "+_ImdbId);
			return URL;
		}
		catch (Exception e)
		{
			
			throw new TaskExecutionException("getPosterURL From OFDB: " + e.getMessage());
		}
	}
	
	public String getOfdbIdByImdbId(String _ImdbId) throws TaskExecutionException
	{
		try
		{
			OfdbApi _Api = new OfdbApi();
			DObject _Response = _Api.requestSearch2("http://ofdbgw.home-of-root.de/imdb2ofdb_json/", _ImdbId);
			System.out.println("get OFDB-ID for "+_ImdbId+" : "+_Response.toString());
			return parseResultProperty(_Response.asMap().get("Content"),"ofdbid");
		}
		catch (Exception e)
		{
			throw new TaskExecutionException("getOfdbIdByImdbId: " +e.getClass() + e.getMessage());
		}
	}
	
	private static String parseResultProperty(DObject _Response,String _elementName) throws ParseException
	{
		String _propertyValue = "";
		if (_Response != null)
		{
			try
			{
				Map<String, DObject> _ResponseMapMovie = _Response.asMap().get("ofdbgw").asMap().get("resultat").asMap();
				try
				{
					//Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
					_propertyValue = _ResponseMapMovie.get(_elementName).asString();
				}
				catch (Exception e)
				{}
			}
			catch (Exception e)
			{
				throw new ParseException("Error during parsing OFDB element "+_elementName+" for Response "+_Response.toString());
			}
		}
		return _propertyValue;
	}
}