package jffsss.movlib;

import java.util.Collection;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.TaskAdapter;

import jffsss.util.Listeners;

public class ProbablyMovie
{
	private MovieInfo _MovieInfo;
	private double _ProbabilityCount;
	private Collection<ProbablyMovie> _ProbablyMovies;

	public ProbablyMovie(Collection<ProbablyMovie> _ProbablyMovies)
	{
		super();
		this._MovieInfo = null;
		this._ProbabilityCount = 0;
		this._ProbablyMovies = _ProbablyMovies;
	}

	private Listeners onUpdate = null;

	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
			this.onUpdate = new Listeners(this);
		return this.onUpdate;
	}

	public void startRetrieving(String _IMDbID)
	{
		Task<MovieInfo> _Task = new GetMovieInfo(_IMDbID);
		TaskListener<MovieInfo> _TaskListener = new GetMovieInfoListener();
		_Task.execute(new TaskAdapter<MovieInfo>(_TaskListener));
	}

	private class GetMovieInfoListener implements TaskListener<MovieInfo>
	{
		@Override
		public void taskExecuted(Task<MovieInfo> _Task)
		{
			MovieInfo _MovieInfo = _Task.getResult();
			ProbablyMovie.this.setMovieInfo(_MovieInfo);
		}

		@Override
		public void executeFailed(Task<MovieInfo> _Task)
		{
			System.out.println("Failed:GetMovieInfo: " + _Task.getFault().getMessage());
		}
	}

	public void setMovieInfo(MovieInfo _MovieInfo)
	{
		this._MovieInfo = _MovieInfo;
		this.onUpdate().notifyListeners("SetMovieInfo", null);
	}

	public MovieInfo getMovieInfo()
	{
		return this._MovieInfo;
	}

	public void incProbability(double _Count)
	{
		this._ProbabilityCount += _Count;
		if (this._ProbablyMovies != null)
			for (ProbablyMovie _ProbablyMovie : this._ProbablyMovies)
				_ProbablyMovie.onUpdate().notifyListeners("SetProbability", null);
	}

	public double getProbabilityCount()
	{
		return this._ProbabilityCount;
	}

	public double getProbabilityTotalCount()
	{
		double _TotalCount = 0;
		if (this._ProbablyMovies != null)
			for (ProbablyMovie _ProbablyMovie : this._ProbablyMovies)
				_TotalCount += _ProbablyMovie.getProbabilityCount();
		return _TotalCount;
	}
}