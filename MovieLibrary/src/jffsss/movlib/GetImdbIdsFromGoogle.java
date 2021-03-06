package jffsss.movlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.GoogleApi;
import jffsss.util.d.DObject;
import jffsss.movlib.ToStoreFile.search_mode;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

/**
 * GetImdbIdsFromGoogle ist eine Task-Klasse f�r das asynchrone Finden der IMDb-IDs der Filme mit Hilfe von
 * Google-Suchmaschine.
 */
public class GetImdbIdsFromGoogle extends Task<Map<String, Double>>
{
	private VideoFileInfo _VideoFileInfo;
	private search_mode _mode;

	/**
	 * Konstruiert ein GetImdbIdsFromGoogle-Objekt.
	 * 
	 * @param _VideoFileInfo
	 *            das VideoFileInfo-Objekt
	 */
	public GetImdbIdsFromGoogle(VideoFileInfo _VideoFileInfo, search_mode _mode)
	{
		this._VideoFileInfo = _VideoFileInfo;
		this._mode = _mode;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		try
		{
			String name;
			if(_mode==search_mode.searchByFileName)
				name = this._VideoFileInfo.getCleanedFileName();
			else
				name = this._VideoFileInfo.getCleanedDirName();
			GoogleApi _Api = new GoogleApi();
			DObject _Response = _Api.requestSearch(name + " site:imdb.com", 0, 5);
			if (_Response.asMap().get("StatusCode").parseAsInteger() == 200)
			{
				return parseResponse(_Response.asMap().get("Content"));
			}
			else
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			throw new TaskExecutionException("GetImdbIdsFromGoogle: " + e.getMessage());
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
				Double _Factor = 0.0;
				for (DObject _ResponseListElement : _ResponseList)
				{
					try
					{
						Map<String, DObject> _ResponseListMap = _ResponseListElement.asMap();
						String _ImdbId = MovieInfo.extractImdbIdFromUrl(_ResponseListMap.get("Link").asString());
						_Factor = (_Factor == 0) ? 1.0 : 0.5;
						if(_ResultMap.containsKey(_ImdbId))
						{
							_ResultMap.put(_ImdbId, _ResultMap.get(_ImdbId) + 0);
						}
						else
						{
							_ResultMap.put(_ImdbId, _Factor);
						}
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