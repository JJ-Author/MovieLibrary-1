package jffsss.movlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jffsss.util.Utils;

public class MovieInfo
{
	private String _Title;
	private String _TitleDe;
	private String _Year;
	private String _Plot;
	private List<String> _Genres;
	private List<String> _Directors;
	private List<String> _Writers;
	private List<String> _Actors;
	private String _ImdbId;
	private Double _ImdbRating;
	private String _PosterSource;

	public MovieInfo(String _Title, String _TitleDe, String _Year, String _Plot, List<String> _Genres, List<String> _Directors, List<String> _Writers, List<String> _Actors, String _ImdbId, Double _ImdbRating, String _PosterSource)
	{
		this._Title = _Title;
		this._TitleDe = _TitleDe;
		this._Year = _Year;
		this._Plot = _Plot;
		this._Genres = (_Genres == null || _Genres.isEmpty()) ? null : new ArrayList<String>(_Genres);
		this._Directors = (_Directors == null || _Directors.isEmpty()) ? null : new ArrayList<String>(_Directors);
		this._Writers = (_Writers == null || _Writers.isEmpty()) ? null : new ArrayList<String>(_Writers);
		this._Actors = (_Actors == null || _Actors.isEmpty()) ? null : new ArrayList<String>(_Actors);
		this._ImdbId = _ImdbId;
		this._ImdbRating = _ImdbRating;
		this._PosterSource = _PosterSource;
	}

	public String getTitle()
	{
		return this._Title;
	}
	
	public String getTitleDe()
	{
		return this._TitleDe;
	}

	public String getYear()
	{
		return this._Year;
	}

	public String getPlot()
	{
		return this._Plot;
	}

	public List<String> getGenres()
	{
		return (this._Genres == null) ? null : Collections.unmodifiableList(this._Genres);
	}

	public List<String> getDirectors()
	{
		return (this._Directors == null) ? null : Collections.unmodifiableList(this._Directors);
	}

	public List<String> getWriters()
	{
		return (this._Writers == null) ? null : Collections.unmodifiableList(this._Writers);
	}

	public List<String> getActors()
	{
		return (this._Actors == null) ? null : Collections.unmodifiableList(this._Actors);
	}

	public String getImdbId()
	{
		return this._ImdbId;
	}

	public String getImdbUrl()
	{
		return (this._ImdbId == null) ? null : "http://www.imdb.com/title/tt" + this._ImdbId + "/";
	}

	public Double getImdbRating()
	{
		return this._ImdbRating;
	}

	public String getPosterSource()
	{
		return this._PosterSource;
	}

	public static String extractImdbIdFromUrl(String _Url)
	{
		return Utils.findFirstPattern(_Url, "/tt([0-9]+)/");
	}
}