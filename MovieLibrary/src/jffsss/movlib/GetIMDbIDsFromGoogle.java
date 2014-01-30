package jffsss.movlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.api.GoogleAPI;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetIMDbIDsFromGoogle extends Task<Map<String, Double>>
{
	private VideoFileInfo _VideoFileInfo;

	public GetIMDbIDsFromGoogle(VideoFileInfo _FilePath)
	{
		this._VideoFileInfo = _FilePath;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		try
		{
			GoogleAPI _API = new GoogleAPI();
			DObject _Response = _API.requestSearch(this._VideoFileInfo.getCleanedFileName() + " site:imdb.com", 0, 5);
			return parseResponse(_Response);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e);
		}
	}

	private static Map<String, Double> parseResponse(DObject _Response)
	{
		Map<String, Double> _ResultMap = new HashMap<String, Double>();
		if (_Response != null)
			try
			{
				List<DObject> _ResponseList = _Response.asList();
				for (DObject _ResponseListElement : _ResponseList)
					try
					{
						Map<String, DObject> _ResponseListMap = _ResponseListElement.asMap();
						String _IMDbID = MovieInfo.extractIMDbIDFromURL(_ResponseListMap.get("Link").asString());
						Double _Factor = 1.0;
						_ResultMap.put(_IMDbID, _Factor);
					}
					catch (Exception e)
					{}
			}
			catch (Exception e)
			{
				throw new RuntimeException("GoogleParse");
			}
		return _ResultMap;
	}
}