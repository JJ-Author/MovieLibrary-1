package jffsss.movlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;

import jffsss.util.Listeners;

import jffsss.movlib.GetMovieInfo;

/**
 * ToStoreFile beinhalten die Informationen des in der Datenbank zu speichernden Films.
 */
public class ToStoreFile
{
	private VideoFileInfo _VideoFileInfo;
	private Map<Object, ProbablyMovie> _ProbablyMovies;

	/**
	 * Konstruiert ein ToStoreFile-Objekt.
	 */
	public ToStoreFile()
	{
		this._VideoFileInfo = null;
		this._ProbablyMovies = new HashMap<Object, ProbablyMovie>();
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
	 * Versucht die Informationen zur Video-Datei asynchron zu extrahieren und danach die zur Video-Datei passende Filme
	 * im Web.
	 * 
	 * @param _FilePath
	 *            der Pfad zur Datei
	 */
	public void startRetrieving(String _FilePath)
	{
		Task<VideoFileInfo> _Task = new GetVideoFileInfo(_FilePath);
		TaskListener<VideoFileInfo> _TaskListener = new GetVideoFileInfoListener();
		_Task.execute(_TaskListener);
	}

	/**
	 * GetVideoFileInfoListener ist die Callback-Klasse zur startRetrieving-Methode. Beim Erfolg wird die Methode
	 * <CODE>startRetrievingProbablyMovies</CODE> aufgerufen.
	 */
	private class GetVideoFileInfoListener implements TaskListener<VideoFileInfo>
	{
		@Override
		public void taskExecuted(Task<VideoFileInfo> _Task)
		{
			VideoFileInfo _VideoFileInfo = _Task.getResult();
			ToStoreFile.this.setVideoFileInfo(_VideoFileInfo);
			ToStoreFile.this.startRetrievingProbablyMovies();
		}

		@Override
		public void executeFailed(Task<VideoFileInfo> _Task)
		{
			_Task.getFault().printStackTrace();
		}
	}

	/**
	 * Legt das VideoFileInfo-Objekt fest.
	 * 
	 * @param _VideoFileInfo
	 *            das VideoFileInfo-Objekt
	 */
	public void setVideoFileInfo(VideoFileInfo _VideoFileInfo)
	{
		this._VideoFileInfo = _VideoFileInfo;
		this.onUpdate().notifyListeners("SetVideoFileInfo", null);
	}

	/**
	 * Gibt das VideoFileInfo-Objekt zurück.
	 * 
	 * @return das VideoFileInfo-Objekt
	 */
	public VideoFileInfo getVideoFileInfo()
	{
		return this._VideoFileInfo;
	}

	/**
	 * Versucht die zur Video-Datei passende Filme im Web asynchron zu finden.
	 */
	public void startRetrievingProbablyMovies()
	{
		{
			Task<Map<String, Double>> _Task = new GetImdbIdsFromOpenSubtitles(this._VideoFileInfo);
			TaskListener<Map<String, Double>> _TaskListener = new GetImdbIdsListener(search_source.OpenSubtitles,search_mode.searchByFileName);
			_Task.execute(_TaskListener);
		}
		{
			Task<Map<String, Double>> _Task = new GetImdbIdsFromFreeBase(this._VideoFileInfo,search_mode.searchByFileName);
			TaskListener<Map<String, Double>> _TaskListener = new GetImdbIdsListener(search_source.Freebase,search_mode.searchByFileName);
			_Task.execute(_TaskListener);
		}
		{
			Task<Map<String, Double>> _Task = new GetImdbIdsFromGoogle(this._VideoFileInfo,search_mode.searchByFileName);
			TaskListener<Map<String, Double>> _TaskListener = new GetImdbIdsListener(search_source.Google,search_mode.searchByFileName);
			_Task.execute(_TaskListener);
		}
	}
	

	public void startRetrievingProbablyMoviesByDirectoryName()
	{
		{
			Task<Map<String, Double>> _Task = new GetImdbIdsFromFreeBase(this._VideoFileInfo,search_mode.searchByDirName);
			TaskListener<Map<String, Double>> _TaskListener = new GetImdbIdsListener(search_source.Freebase,search_mode.searchByDirName);
			_Task.execute(_TaskListener);
		}
		{
			Task<Map<String, Double>> _Task = new GetImdbIdsFromGoogle(this._VideoFileInfo,search_mode.searchByDirName);
			TaskListener<Map<String, Double>> _TaskListener = new GetImdbIdsListener(search_source.Google,search_mode.searchByDirName);
			_Task.execute(_TaskListener);
		}
	}

	/**
	 * Erstellt ein neues ProbablyMovie-Objekt, falls keins mit dieser IMDb-ID bereits existierte, fügt es in die Liste
	 * ein und führt dessen Methode <CODE>startRetrieving</CODE> aus.
	 * 
	 * @param _ImdbId
	 *            die IMDb-ID als Schlüssel
	 * @return neu erstelltes oder bereits vorhandenes ProbablyMovie-Objekt
	 */
	public ProbablyMovie addProbablyMovie(String _ImdbId, double _Probability)
	{
		ProbablyMovie _ProbablyMovieModel = this._ProbablyMovies.get(_ImdbId);
		if (_ProbablyMovieModel == null)
		{
			_ProbablyMovieModel = new ProbablyMovie(this._ProbablyMovies.values(), this);
			
			if(_Probability != -1.0)
			{
				/*_ProbablyMovieModel.incProbability(_Probability);
				String poster = GetMovieInfo.getMoviePoster(_ImdbId);
				System.out.println("###### Imdb ID: " + _ImdbId + "Movie Poster: " + poster);
				if(poster==null || poster.equals("") || poster.equals("http://img.ofdb.de/film/na.gif"))
				{
					_ProbablyMovieModel.decProbability(0.5);
				}*/
				
				String poster = GetMovieInfo.getMoviePoster(_ImdbId);
				if(poster==null || poster.equals("") || poster.equals("http://img.ofdb.de/film/na.gif"))
				{
					_ProbablyMovieModel.incProbability((2/3.0)*_Probability);
				}
				else
				{
					_ProbablyMovieModel.incProbability(_Probability);
				}
				
			}
			
			this._ProbablyMovies.put(_ImdbId, _ProbablyMovieModel);
			this.onUpdate().notifyListeners("DeleteProbablyMovies", null);
			
			Set<Object> added = new HashSet<Object>();
			added.addAll(this._ProbablyMovies.keySet());
			
			for(int i=0; i < this._ProbablyMovies.size(); i++)
			{
				double highestRating = Integer.MIN_VALUE;
				Object ImdbId = null;
								
				for (Map.Entry<Object, ProbablyMovie> movie : this._ProbablyMovies.entrySet())
				{
					if(added.contains(movie.getKey()) && (movie.getValue().getProbabilityCount() > highestRating && movie.getValue().getProbabilityCount() > -1))
					{
						highestRating = movie.getValue().getProbabilityCount();
						ImdbId = movie.getKey();
					}
				}
				
				if(highestRating != Integer.MIN_VALUE & ImdbId != null)
				{
					this.onUpdate().notifyListeners("AddProbablyMovie", this._ProbablyMovies.get(ImdbId));
					this._ProbablyMovies.get(ImdbId).startRetrieving(ImdbId.toString());
					added.remove(ImdbId);
				}
			}
		}
		return _ProbablyMovieModel;
	}

	/**
	 * Gibt das ProbablyMovie-Objekt zur gegebenen IMDb-ID aus der Liste zurück.
	 * 
	 * @param _ImdbId
	 *            die IMDb-ID als Schlüssel
	 * @return das ProbablyMovie-Objekt oder <CODE>null</CODE> falls kein Eintrag zur gegebenen IMDb-ID existiert
	 */
	public ProbablyMovie getProbablyMovie(String _ImdbId)
	{
		return this._ProbablyMovies.get(_ImdbId);
	}

	/**
	 * Gibt die Liste aller ProbablyMovie-Objekte zurück.
	 * 
	 * @return die Liste aller ProbablyMovie-Objekte
	 */
	public List<ProbablyMovie> getAllProbablyMovies()
	{
		return new ArrayList<ProbablyMovie>(this._ProbablyMovies.values());
	}

	public static enum search_mode   {searchByFileName,searchByDirName};
	public static enum search_source {OpenSubtitles,Google,Freebase};
	/**
	 * GetImdbIdsListener ist die Callback-Klasse zur startRetrievingProbablyMovies-Methode.
	 */
	private class GetImdbIdsListener implements TaskListener<Map<String, Double>>
	{

		private double _Weight;
		private search_mode _mode;
		private search_source _source;
		//private boolean _madeDirSearch = false;
		
		public GetImdbIdsListener(search_source source,search_mode mode)
		{
			this._source = source;
			switch (source) { //weights for the global percentage value calculation
			case OpenSubtitles:
				_Weight = 3.0;
				break;
			case Freebase:
				_Weight = 2.0;
				break;
			case Google:
				_Weight = 1.0;
				break;
			default:
				break;
			}
			this._mode = mode;
			
		}

		@Override
		public void taskExecuted(Task<Map<String, Double>> _Task)
		{
			Map<String, Double> _Results = _Task.getResult();
			if (this._source==search_source.Google && _Results.isEmpty() && this._mode == search_mode.searchByFileName)
			{
				ToStoreFile.this.startRetrievingProbablyMoviesByDirectoryName();	
			}
				
			double _MaxCount = 0;
			for (Map.Entry<String, Double> _Result : _Results.entrySet())
			{
				_MaxCount = Math.max(_MaxCount, _Result.getValue());
			}
			for (Map.Entry<String, Double> _Result : _Results.entrySet())
			{
				String _ImdbId = _Result.getKey();
				double _Factor = _MaxCount > 0 ? _Result.getValue() / _MaxCount : 0;
				ToStoreFile.this.addProbablyMovie(_ImdbId, _Factor * this._Weight); //Rating der Quelle * Gewicht
				//ProbablyMovie _ProbablyMovie = ToStoreFile.this.addProbablyMovie(_ImdbId, _Factor * this._Weight);
				//_ProbablyMovie.incProbability(_Factor * this._Weight);
			}
		}

		@Override
		public void executeFailed(Task<Map<String, Double>> _Task)
		{
			System.out.println("Failed:GetIMDbIDs: " + _Task.getFault().getMessage());
		}
	}
}
