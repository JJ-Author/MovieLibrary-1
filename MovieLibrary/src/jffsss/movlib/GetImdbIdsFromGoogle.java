package jffsss.movlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.GoogleApi;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetImdbIdsFromGoogle extends Task<Map<String, Double>>
{
	private VideoFileInfo _VideoFileInfo;

	public GetImdbIdsFromGoogle(VideoFileInfo _FilePath)
	{
		this._VideoFileInfo = _FilePath;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		try
		{
			GoogleApi _Api = new GoogleApi();
			DObject _Response = _Api.requestSearch(this._VideoFileInfo.getCleanedFileName() + " site:imdb.com", 0, 5);
			return parseResponse(_Response.asMap().get("Content"));
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e.getMessage());
		}
	}

	private static Map<String, Double> parseResponse(DObject _Response) throws ParseException
	{
		Map<String, Double> _ResultMap = new HashMap<String, Double>();
		if (_Response != null)
		{
			try
			{
				List<DObject> _ResponseList = _Response.asList();
				for (DObject _ResponseListElement : _ResponseList)
				{
					try
					{
						Map<String, DObject> _ResponseListMap = _ResponseListElement.asMap();
						String _ImdbId = MovieInfo.extractImdbIdFromUrl(_ResponseListMap.get("Link").asString());
						Double _Factor = 1.0;
						_ResultMap.put(_ImdbId, _Factor);
					}
					catch (Exception e)
					{}
				}
			}
			catch (Exception e)
			{
				throw new ParseException();
			}
		}
		return _ResultMap;
	}
}