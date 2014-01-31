package jffsss.movlib;

import java.util.List;
import java.util.Map;

import jffsss.api.OMDbAPI;
import jffsss.api.FreeBaseAPI;
import jffsss.util.Utils;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetMovieInfo extends Task<MovieInfo>
{
	private String _IMDbID;

	public GetMovieInfo(String _IMDbID)
	{
		this._IMDbID = _IMDbID;
	}

	public MovieInfo execute() throws TaskExecutionException
	{
		try
		{
			OMDbAPI _API = new OMDbAPI();
			DObject _Response = _API.requestMovieByID(this._IMDbID);
			return parseResponse(_Response,this._IMDbID);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e);
		}
	}

	private MovieInfo parseResponse(DObject _Response, String IMDbID) throws Exception
	{
		DObject _fResponse;
		try
		{
			
			//lookup German name and description at freebase
			try 
			{
				FreeBaseAPI _fAPI = new FreeBaseAPI();
				String langs = "de,en";
				_fResponse = _fAPI.requestSearch(true, "\"tt"+IMDbID+"\"", "(all type:/film/film)", "(all)", 5, langs);
			}
			catch (Exception e)
			{
				throw new TaskExecutionException(e);
			}
			
			Map<String, DObject> _ResponseMap = _Response.asMap();
			String _fTitle = getMovieName(_fResponse); //get German movie title from freebase
			String _Title =  (_fTitle!="") ? _fTitle :_ResponseMap.get("Title").asString();
			//String _Title = _ResponseMap.get("Title").asString();
			String _Year = _ResponseMap.get("Year").asString();
			// Bindestrich im Jahr richtig parsen
			String _Plot;
			try
			{
				_Plot = _ResponseMap.get("Plot").asString();
			}
			catch (Exception e)
			{
				_Plot = null;
			}
			List<String> _Genres;
			try
			{
				_Genres = Utils.split(_ResponseMap.get("Genre").asString(), ", ");
			}
			catch (Exception e)
			{
				_Genres = null;
			}
			List<String> _Directors;
			try
			{
				_Directors = Utils.split(_ResponseMap.get("Director").asString(), ", ");
			}
			catch (Exception e)
			{
				_Directors = null;
			}
			List<String> _Writers;
			try
			{
				_Writers = Utils.split(_ResponseMap.get("Writer").asString(), ", ");
			}
			catch (Exception e)
			{
				_Writers = null;
			}
			List<String> _Actors;
			try
			{
				_Actors = Utils.split(_ResponseMap.get("Actors").asString(), ", ");
			}
			catch (Exception e)
			{
				_Actors = null;
			}
			String _IMDbID = _ResponseMap.get("imdbID").asString().substring(2);
			Double _IMDbRating;
			try
			{
				_IMDbRating = _ResponseMap.get("imdbRating").parseAsDouble();
			}
			catch (Exception e)
			{
				_IMDbRating = null;
			}
			String _PosterSource;
			try
			{
				_PosterSource = _ResponseMap.get("Poster").asString();
			}
			catch (Exception e)
			{
				_PosterSource = null;
			}
			MovieInfo _MovieInfo = new MovieInfo(_Title, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource);
			return _MovieInfo;
		}
		catch (Exception e)
		{
			throw new RuntimeException("OMDbAPIParse" + _Response + " - " +  this._IMDbID);
		}
	}
	
    public static String getMovieName(DObject _Response)
	{
    	String name = "";
		if (_Response != null)
			try
			{
				List<DObject> _ResponseMapList = _Response.asMap().get("result").asList();
				for (DObject _ResponseMapListElement : _ResponseMapList)
					try
					{
						Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
						name = _ResponseMapListMap.get("name").asString();
					}
					catch (Exception e)
					{System.out.println("!!!!"); e.printStackTrace();}
			}
			catch (Exception e)
			{
				throw new RuntimeException("FreeBaseParseMovieDetail");
			}
		return name;
	}
    
    public static String getElement(DObject _Response)
	{
    	String name = "";
		if (_Response != null)
			try
			{
				List<DObject> _ResponseMapList = _Response.asMap().get("result").asList();
				for (DObject _ResponseMapListElement : _ResponseMapList)
					try
					{
						Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
						name = _ResponseMapListMap.get("name").asString();
					}
					catch (Exception e)
					{System.out.println("!!!!"); e.printStackTrace();}
			}
			catch (Exception e)
			{
				throw new RuntimeException("FreeBaseParseMovieDetail");
			}
		return name;
	}
}