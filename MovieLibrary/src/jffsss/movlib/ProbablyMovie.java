package jffsss.movlib;

import java.util.Collection;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;

import jffsss.util.Listeners;
import jffsss.util.LevenshteinDistance;

import jffsss.movlib.ToStoreFile;

/**
 * ProbablyMovie beinhalten die Informationen zu einem Film und eine Wahrscheinlichkeit, die sich aus einer Menge von
 * anderen ProbablyMovie-Objekten ergibt.
 */
public class ProbablyMovie
{
	private MovieInfo _MovieInfo;
	private double _ProbabilityCount;
	private double PosterPenalty;
	private double LevenshteinPenalty;
	private boolean _RetrievingStarted; 
	private Collection<ProbablyMovie> _ProbablyMovies;
	private ToStoreFile father;

	/**
	 * Konstruiert ein ProbablyMovie-Objekt.
	 * 
	 * @param _ProbablyMovies
	 *            eine Menge von anderen ProbablyMovie-Objekten und diesem Objekt, aus denen sich die Wahrscheinlichkeit
	 *            ergibt
	 */
	public ProbablyMovie(Collection<ProbablyMovie> _ProbablyMovies, ToStoreFile _father)
	{
		super();
		this._MovieInfo = null;
		this._ProbabilityCount = 0;
		this._ProbablyMovies = _ProbablyMovies;
		this.father = _father;
		this._RetrievingStarted=false;
		this.PosterPenalty = 1.0;
		this.LevenshteinPenalty = 1.0;
	}

	private Listeners onUpdate = null;

	/**
	 * Gibt die Listener zurück.
	 * 
	 * @return die Listener
	 */
	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
		{
			this.onUpdate = new Listeners(this);
		}
		return this.onUpdate;
	}

	/**
	 * Versucht die Film-Informationen aus dem Web asynchron abzurufen.
	 * 
	 * @param _ImdbId
	 *            die IMDb-ID des Films, dessen Informationen abgerufen werden
	 */
	public void startRetrieving(String _ImdbId)
	{
		if(this._RetrievingStarted == false)
		{
			this._RetrievingStarted = true;
			Task<MovieInfo> _Task = new GetMovieInfo(_ImdbId);
			TaskListener<MovieInfo> _TaskListener = new GetMovieInfoListener();
			_Task.execute(_TaskListener);
		}
	}

	/**
	 * GetMovieInfoListener ist die Callback-Klasse zur startRetrieving-Methode.
	 */
	private class GetMovieInfoListener implements TaskListener<MovieInfo>
	{
		@Override
		public void taskExecuted(Task<MovieInfo> _Task)
		{
			MovieInfo _MovieInfo = _Task.getResult();
			ProbablyMovie.this.setMovieInfo(_MovieInfo);
			ProbablyMovie.this.updateProbability();
		}

		@Override
		public void executeFailed(Task<MovieInfo> _Task)
		{
			System.out.println("Failed:GetMovieInfo: " + _Task.getFault().getMessage());
		}
	}

	/**
	 * Legt das MovieInfo-Objekt fest.
	 * 
	 * @param _MovieInfo
	 *            das MovieInfo-Objekt
	 */
	public void setMovieInfo(MovieInfo _MovieInfo)
	{
		this._MovieInfo = _MovieInfo;
		this.onUpdate().notifyListeners("SetMovieInfo", null);
	}

	/**
	 * Gibt das MovieInfo-Objekt zurück.
	 * 
	 * @return das MovieInfo-Objekt
	 */
	public MovieInfo getMovieInfo()
	{
		return this._MovieInfo;
	}
	
	/**
	 * Gibt die Poter Penalty zurück.
	 * 
	 * @return Poster Penalty
	 */
	public double getPosterPenalty()
	{
		return this.PosterPenalty;
	}
	
	/**
	 * Gibt die Levenshtein Penalty zurück.
	 * 
	 * @return Levenshtein Penalty
	 */
	public double getLevenshteinPenalty()
	{
		return this.LevenshteinPenalty;
	}

	/**
	 * Erhöht die Wahrscheinlichkeit des Films.
	 * 
	 * @param _Count
	 *            der Wert, um den der Zähler in der Wahrscheinlichkeitsberechnung, erhöht wird
	 */
	public void incProbability(double _Count)
	{
		this._ProbabilityCount += _Count;
		if (this._ProbablyMovies != null)
		{
			for (ProbablyMovie _ProbablyMovie : this._ProbablyMovies)
			{
				_ProbablyMovie.onUpdate().notifyListeners("SetProbability", null);
			}
		}
	}
	
	/**
	 * Überprüft das Filmposter und passt evtl die Wahrscheinlichkeit an.
	 */
	private void updateProbability()
	{
		if(this._MovieInfo.getPosterSource()==null || this._MovieInfo.getPosterSource().equals("") || this._MovieInfo.getPosterSource().equals("http://img.ofdb.de/film/na.gif")) 
		{
			//Reduziert Wsk wenn kein Poster für den Film gefunden wurde
			this.PosterPenalty = ToStoreFile.BadPosterPenalty;
			this._ProbabilityCount = this._ProbabilityCount * this.PosterPenalty;
		}
		
		//Multipliziert Wsk mit der Ähnlichkeit zum bereinigten Dateinamen
		if(this._MovieInfo.getTitleDe() == null)
		{
			this.LevenshteinPenalty = LevenshteinDistance.similarity(this._MovieInfo.getTitle(), this.father.getVideoFileInfo().getCleanedFileName());
		}
		else
		{
			this.LevenshteinPenalty = LevenshteinDistance.similarity(this._MovieInfo.getTitleDe(), this.father.getVideoFileInfo().getCleanedFileName());
		}
		System.out.println("Movie: " + this._MovieInfo.getTitle() + " Cleaned Filename: " + this.father.getVideoFileInfo().getCleanedFileName() + " Levenshtein Ähnlichkeit: " + this.LevenshteinPenalty);
		this._ProbabilityCount = this._ProbabilityCount * this.LevenshteinPenalty;
		
		if (this._ProbablyMovies != null)
		{
			for (ProbablyMovie _ProbablyMovie : this._ProbablyMovies)
			{
				_ProbablyMovie.onUpdate().notifyListeners("SetProbability", null);
			}
		}
		
		this.father.updateProbablyMovieView();
	}

	/**
	 * Gibt den Zähler zurück, der in der Wahrscheinlichkeitsberechnung benutzt wird. Der Nenner wird berechnet, indem
	 * die Zähler aller ProbablyMovie-Objekte, die beim Konstruktor festgelegt wurden, aufsummiert werden.
	 * 
	 * @return der Zähler, der in der Wahrscheinlichkeitsberechnung benutzt wird
	 */
	public double getProbabilityCount()
	{
		return this._ProbabilityCount;
	}

	/**
	 * Gibt den Nenner zurück, der in der Wahrscheinlichkeitsberechnung benutzt wird.
	 * 
	 * @return der Nenner, der in der Wahrscheinlichkeitsberechnung benutzt wird
	 */
	public double getProbabilityTotalCount()
	{
		double _TotalCount = 0;
		if (this._ProbablyMovies != null)
		{
			for (ProbablyMovie _ProbablyMovie : this._ProbablyMovies)
			{
				_TotalCount += _ProbablyMovie.getProbabilityCount();
			}
		}
		return _TotalCount;
	}
	
	
}