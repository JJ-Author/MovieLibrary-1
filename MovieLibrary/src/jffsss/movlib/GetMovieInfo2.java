package jffsss.movlib;

import java.util.List;
import java.util.Map;

import jffsss.api.OMDbAPI;
import jffsss.util.Utils;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetMovieInfo2 extends Task<MovieInfo>
{
	private String _IMDbID;

	public GetMovieInfo2(String _IMDbID)
	{
		this._IMDbID = _IMDbID;
	}

	public MovieInfo execute() throws TaskExecutionException
	{
		try
		{
			OMDbAPI _API = new OMDbAPI();
			DObject _Response = _API.requestMovieByID(this._IMDbID);
			return parseResponse(_Response);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e);
		}
	}

	private static MovieInfo parseResponse(DObject _Response) throws Exception
	{
		try
		{
			Map<String, DObject> _ResponseMap = _Response.asMap();
			String _Title = _ResponseMap.get("Title").asString();
			String _Year = _ResponseMap.get("Year").asString().replace('–', '-');
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
			throw new RuntimeException("OMDbAPIParse");
		}
	}
}