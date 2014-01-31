package jffsss.movlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.TaskAdapter;

import jffsss.util.Listeners;

public class ToStoreFile
{
	private VideoFileInfo _VideoFileInfo;
	private Map<Object, ProbablyMovie> _ProbablyMovies;

	public ToStoreFile()
	{
		this._VideoFileInfo = null;
		this._ProbablyMovies = new HashMap<Object, ProbablyMovie>();
	}

	private Listeners onUpdate = null;

	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
			this.onUpdate = new Listeners(this);
		return this.onUpdate;
	}

	public void startRetrieving(String _FilePath)
	{
		Task<VideoFileInfo> _Task = new GetVideoFileInfo(_FilePath);
		TaskListener<VideoFileInfo> _TaskListener = new GetVideoFileInfoListener();
		_Task.execute(new TaskAdapter<VideoFileInfo>(_TaskListener));

	}

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

	public void setVideoFileInfo(VideoFileInfo _VideoFileInfo)
	{
		this._VideoFileInfo = _VideoFileInfo;
		this.onUpdate().notifyListeners("SetVideoFileInfo", null);
	}

	public VideoFileInfo getVideoFileInfo()
	{
		return this._VideoFileInfo;
	}

	public void startRetrievingProbablyMovies()
	{
		{
			Task<Map<String, Double>> _Task = new GetIMDbIDsFromOpenSubtitles(this._VideoFileInfo);
			TaskListener<Map<String, Double>> _TaskListener = new GetIMDbIDsListener(3);
			_Task.execute(new TaskAdapter<Map<String, Double>>(_TaskListener));
		}
		{
			Task<Map<String, Double>> _Task = new GetIMDbIDsFromGoogle(this._VideoFileInfo);
			TaskListener<Map<String, Double>> _TaskListener = new GetIMDbIDsListener(1);
			_Task.execute(new TaskAdapter<Map<String, Double>>(_TaskListener));
		}
		{
			Task<Map<String, Double>> _Task = new GetIMDbIDsFromFreeBase(this._VideoFileInfo);
			TaskListener<Map<String, Double>> _TaskListener = new GetIMDbIDsListener(1);
			_Task.execute(new TaskAdapter<Map<String, Double>>(_TaskListener));
		}
	}

	public ProbablyMovie addProbablyMovie(String _IMDbID)
	{
		ProbablyMovie _ProbablyMovieModel = this._ProbablyMovies.get(_IMDbID);
		if (_ProbablyMovieModel == null)
		{
			_ProbablyMovieModel = new ProbablyMovie(this._ProbablyMovies.values());
			this._ProbablyMovies.put(_IMDbID, _ProbablyMovieModel);
			this.onUpdate().notifyListeners("AddProbablyMovie", _ProbablyMovieModel);
			_ProbablyMovieModel.startRetrieving(_IMDbID);
		}
		return _ProbablyMovieModel;
	}

	public ProbablyMovie getProbablyMovie(String _IMDbID)
	{
		return this._ProbablyMovies.get(_IMDbID);
	}

	public List<ProbablyMovie> getAllProbablyMovies()
	{
		return new ArrayList<ProbablyMovie>(this._ProbablyMovies.values());
	}

	private class GetIMDbIDsListener implements TaskListener<Map<String, Double>>
	{
		private double _AdditionalFactor;

		public GetIMDbIDsListener(double _AdditionalFactor)
		{
			this._AdditionalFactor = _AdditionalFactor;
		}

		@Override
		public void taskExecuted(Task<Map<String, Double>> _Task)
		{
			Map<String, Double> _Results = _Task.getResult();
			double _TotalCount = 0;
			for (Map.Entry<String, Double> _Result : _Results.entrySet())
				_TotalCount += _Result.getValue();
			for (Map.Entry<String, Double> _Result : _Results.entrySet())
			{
				String _IMDbID = _Result.getKey();
				double _Factor = _TotalCount > 0 ? _Result.getValue() / _TotalCount : 0;
				ProbablyMovie _ProbablyMovie = ToStoreFile.this.addProbablyMovie(_IMDbID);
				_ProbablyMovie.incProbability(_Factor + this._AdditionalFactor);
			}
		}

		@Override
		public void executeFailed(Task<Map<String, Double>> _Task)
		{
			System.out.println("Failed:GetIMDbIDs: " + _Task.getFault().getMessage());
		}
	}
}