package jffsss.movlib;

import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.OmdbApi;
import jffsss.api.FreeBaseApi;
import jffsss.util.Utils;
import jffsss.util.d.DObject;
import jffsss.movlib.GetMovieInfoFromOfdb;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

/**
 * GetMovieInfo ist eine Task-Klasse f�r das asynchrone Finden der Film-Informationen mit Hilfe von OMDb-Seite.
 */
public class GetMovieInfo extends Task<MovieInfo>
{
	private String _ImdbId;

	/**
	 * Konstruiert ein GetMovieInfo-Objekt.
	 * 
	 * @param _ImdbId
	 *            die IMDb-ID
	 */
	public GetMovieInfo(String _ImdbId)
	{
		this._ImdbId = _ImdbId;
	}

	@Override
	public MovieInfo execute() throws TaskExecutionException
	{
		try
		{
			OmdbApi _OmdbApi = new OmdbApi();
			DObject _OmdbResponse = _OmdbApi.requestMovieById(this._ImdbId);
			FreeBaseApi _FreeBaseApi = new FreeBaseApi();
			DObject _FreeBaseResponse = _FreeBaseApi.requestSearch2(true, "\"tt" + _ImdbId + "\"", "(all type:/film/film)", "(all)", 1, "de,en");
			return parseResponse(_OmdbResponse.asMap().get("Content"), _FreeBaseResponse.asMap().get("Content"), this._ImdbId);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e.getMessage());
		}
	}

	private MovieInfo parseResponse(DObject _OmdbResponse, DObject _FreeBaseResponse, String _ImdbId) throws ParseException
	{
		try
		{
			Map<String, DObject> _OmdbResponseMap = _OmdbResponse.asMap();
			Map<String, DObject> _FreeBaseResponseMap = _FreeBaseResponse.asMap();
			String _Title = _OmdbResponseMap.get("Title").asString();
			String _TitleDe;
			try
			{
				_TitleDe = _FreeBaseResponseMap.get("result").asList().get(0).asMap().get("name").asString();
			}
			catch (Exception e)
			{
				_TitleDe = null;
			}
			String _Year = _OmdbResponseMap.get("Year").asString();
			// Bindestrich im Jahr richtig parsen
			String _Plot;
			try
			{
				_Plot = _OmdbResponseMap.get("Plot").asString();
			}
			catch (Exception e)
			{
				_Plot = null;
			}
			List<String> _Genres;
			try
			{
				_Genres = Utils.split(_OmdbResponseMap.get("Genre").asString(), ", ");
			}
			catch (Exception e)
			{
				_Genres = null;
			}
			List<String> _Directors;
			try
			{
				_Directors = Utils.split(_OmdbResponseMap.get("Director").asString(), ", ");
			}
			catch (Exception e)
			{
				_Directors = null;
			}
			List<String> _Writers;
			try
			{
				_Writers = Utils.split(_OmdbResponseMap.get("Writer").asString(), ", ");
			}
			catch (Exception e)
			{
				_Writers = null;
			}
			List<String> _Actors;
			try
			{
				_Actors = Utils.split(_OmdbResponseMap.get("Actors").asString(), ", ");
			}
			catch (Exception e)
			{
				_Actors = null;
			}
			String _IMDbID = _OmdbResponseMap.get("imdbID").asString().substring(2);
			Double _IMDbRating;
			try
			{
				_IMDbRating = _OmdbResponseMap.get("imdbRating").parseAsDouble();
			}
			catch (Exception e)
			{
				_IMDbRating = null;
			}
			String _PosterSource;
			try
			{
				GetMovieInfoFromOfdb g = new GetMovieInfoFromOfdb();
				//_PosterSource = _OmdbResponseMap.get("Poster").asString();
				String url = g.getPosterURL("tt" + this._ImdbId);
				if (url == "" || url == "http://img.ofdb.de/film/na.gif") // if ofdb lookup failed (unknown imdb-id) or the placeholder picture
					;//url = _OmdbResponseMap.get("Poster").asString();
				_PosterSource = url;
			}
			catch (Exception e)
			{
				System.out.println("Fehler beim Suchen des deutschen Posters: " + e.getMessage());
				_PosterSource = null;
			}
			Double _Duration;
			try
			{
				_Duration = Double.valueOf(_OmdbResponseMap.get("Runtime").asString().replaceAll(" min", ""));
			}
			catch (Exception e)
			{
				System.out.println("parsing OMDB Duration failed");
				_Duration = null;
			}
			System.out.println("Movie: " + _Title + " Poster: " + _PosterSource);
			MovieInfo _MovieInfo = new MovieInfo(_Title, _TitleDe, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource, _Duration,0);
			return _MovieInfo;
		}
		catch (Exception e)
		{
			throw new ParseException("OMDbAPIParse" + _OmdbResponse + " - " + this._ImdbId);
		}
	}

	@SuppressWarnings("unused")
	private static String getElement(DObject _Response)
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
					{
						System.out.println("!!!!");
						e.printStackTrace();
					}
			}
			catch (Exception e)
			{
				throw new RuntimeException("FreeBaseParseMovieDetail");
			}
		return name;
	}
	
	/**
	 * Liefert das Poster eines Films auf Ofdb.
	 * @param ImdbId	Id des Films
	 * @return	URL des Posters
	 */
	public static String getMoviePoster(String ImdbId)
	{
		String _PosterSource;
		try
		{
			GetMovieInfoFromOfdb g = new GetMovieInfoFromOfdb();
			String url = g.getPosterURL("tt" + ImdbId);
			_PosterSource = url;
		}
		catch (Exception e)
		{
			System.out.println("Fehler beim Suchen des deutschen Posters: " + e.getMessage());
			_PosterSource = null;
		}
		
		return _PosterSource;
	}
}