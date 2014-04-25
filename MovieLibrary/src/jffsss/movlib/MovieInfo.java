package jffsss.movlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jffsss.util.Utils;

/**
 * MovieInfo beinhaltet die Basisinformationen eines beliebigen Films.
 */
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
	private Integer _Rating;
	private String _PosterSource;
	private Double _Duration;

	/**
	 * Konstruiert ein MovieInfo-Objekt.
	 * 
	 * @param _Title
	 *            der Titel
	 * @param _TitleDe
	 *            der deutsche Titel
	 * @param _Year
	 *            das Release-Jahr
	 * @param _Plot
	 *            die Kurzbeschreibung des Plots
	 * @param _Genres
	 *            die Genres
	 * @param _Directors
	 *            die Liste der Regisseure
	 * @param _Writers
	 *            die Liste der Drehbuchautoren
	 * @param _Actors
	 *            die Liste der Schauspieler
	 * @param _ImdbId
	 *            die IMDb-ID
	 * @param _ImdbRating
	 *            der Rating
	 * @param _PosterSource
	 *            die Quell-URL des Posters
	 * @param _Duration
	 *            die L�nge des Films in Minuten
	 */
	public MovieInfo(String _Title, String _TitleDe, String _Year, String _Plot, List<String> _Genres, List<String> _Directors, List<String> _Writers, List<String> _Actors, String _ImdbId, Double _ImdbRating, String _PosterSource, Double _Duration,Integer _Rating)
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
		this._Rating = _Rating;
		this._PosterSource = _PosterSource;
		this._Duration = _Duration;
	}

	/**
	 * Gibt den Titel zur�ck.
	 * 
	 * @return der Titel
	 */
	public String getTitle()
	{
		return this._Title;
	}

	/**
	 * Gibt den deutschen Titel zur�ck.
	 * 
	 * @return der deutsche Titel
	 */
	public String getTitleDe()
	{
		return this._TitleDe;
	}

	/**
	 * Gibt das Release-Jahr des Plots zur�ck.
	 * 
	 * @return das Release-Jahr
	 */
	public String getYear()
	{
		return this._Year;
	}

	/**
	 * Gibt die Kurzbeschreibung des Plots zur�ck.
	 * 
	 * @return die Kurzbeschreibung des Plots
	 */
	public String getPlot()
	{
		return this._Plot;
	}

	/**
	 * Gibt die Genres zur�ck.
	 * 
	 * @return die Genres
	 */
	public List<String> getGenres()
	{
		return (this._Genres == null) ? Collections.<String> emptyList() : Collections.unmodifiableList(this._Genres);
	}

	/**
	 * Gibt die Liste der Regisseure zur�ck.
	 * 
	 * @return die Liste der Regisseure
	 */
	public List<String> getDirectors()
	{
		return (this._Directors == null) ? Collections.<String> emptyList() : Collections.unmodifiableList(this._Directors);
	}

	/**
	 * Gibt die Liste der Drehbuchautoren zur�ck.
	 * 
	 * @return die Liste der Drehbuchautoren
	 */
	public List<String> getWriters()
	{
		return (this._Writers == null) ? Collections.<String> emptyList() : Collections.unmodifiableList(this._Writers);
	}

	/**
	 * Gibt die Liste der Schauspieler zur�ck.
	 * 
	 * @return die Liste der Schauspieler
	 */
	public List<String> getActors()
	{
		return (this._Actors == null) ? Collections.<String> emptyList() : Collections.unmodifiableList(this._Actors);
	}

	/**
	 * Gibt die IMDb-ID zur�ck.
	 * 
	 * @return die IMDb-ID
	 */
	public String getImdbId()
	{
		return this._ImdbId;
	}

	/**
	 * Gibt die URL zur Seite des Films auf IMDb zur�ck.
	 * 
	 * @return die URL zur Seite des Films auf IMDb
	 */
	public String getImdbUrl()
	{
		return (this._ImdbId == null) ? null : "http://www.imdb.com/title/tt" + this._ImdbId + "/";
	}

	/**
	 * Gibt das IMDBRating zur�ck.
	 * 
	 * @return der Rating
	 */
	public Double getImdbRating()
	{
		return this._ImdbRating;
	}
	
	/**
	 * Gibt das persönliche Rating zur�ck.
	 * 
	 * @return der Rating
	 */
	public Integer getRating()
	{
		return this._Rating;
	}

	/**
	 * Gibt die Quell-URL des Posters zur�ck.
	 * 
	 * @return die Quell-URL des Posters
	 */
	public String getPosterSource()
	{
		return this._PosterSource;
	}

	/**
	 * Gibt die L�nge in Minuten zur�ck.
	 * 
	 * @return die L�nge in Minuten
	 */
	public Double getDuration()
	{
		return this._Duration;
	}

	/**
	 * Extrahiert die IMDb-ID des Films aus der URL.
	 * 
	 * @return die extrahierte URL
	 */
	public static String extractImdbIdFromUrl(String _Url)
	{
		return Utils.findFirstPattern(_Url, "/tt([0-9]+)/");
	}
}