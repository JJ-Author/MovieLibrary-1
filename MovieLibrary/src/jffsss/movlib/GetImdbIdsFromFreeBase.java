package jffsss.movlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.FreeBaseApi;
import jffsss.util.d.DObject;
import jffsss.movlib.ToStoreFile.search_mode;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

/**
 * GetImdbIdsFromFreeBase ist eine Task-Klasse für das asynchrone Finden der IMDb-IDs der Filme mit Hilfe von
 * FreeBase-Seite.
 */
public class GetImdbIdsFromFreeBase extends Task<Map<String, Double>>
{
	private VideoFileInfo _VideoFileInfo;
	private search_mode _mode;

	/**
	 * Konstruiert ein GetImdbIdsFromFreeBase-Objekt.
	 * 
	 * @param _VideoFileInfo
	 *            das VideoFileInfo-Objekt
	 */
	public GetImdbIdsFromFreeBase(VideoFileInfo _VideoFileInfo,search_mode mode)
	{
		this._VideoFileInfo = _VideoFileInfo;
		this._mode = mode;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		try
		{
			FreeBaseApi _Api = new FreeBaseApi();
			String langs = "de,en";
			String _movieName;
			if (_mode==search_mode.searchByFileName)
				_movieName = this._VideoFileInfo.getCleanedFileName();
			else
				_movieName = this._VideoFileInfo.getCleanedDirName();
			DObject _Response = _Api.requestSearch2(true, null, "(all name{full}:\"" + _movieName + "\" type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
			if (hasNoHit(_Response.asMap().get("Content")))
			{
				_Response = _Api.requestSearch2(true, null, "(all name{phrase}:\"" + _movieName + "\" type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
				System.out.println(this._VideoFileInfo.getCleanedFileName() + ": NO HITS full");
			}
			if (hasNoHit(_Response.asMap().get("Content")))
			{
				_Response = _Api.requestSearch2(true, "\"" + _movieName + "\"", "(all type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
				System.out.println(this._VideoFileInfo.getCleanedFileName() + ": NO HITS phrase");
			}
			if (hasNoHit(_Response.asMap().get("Content")))
			{
				System.out.println(this._VideoFileInfo.getCleanedFileName() + ": NO HITS query");
			}
			return parseResponse(_Response.asMap().get("Content"));
		}
		catch (Exception e)
		{
			throw new TaskExecutionException("GetImdbIdsFromFreeBase:" + e.getMessage());
		}
	}

	private static boolean hasNoHit(DObject _Response) throws ParseException
	{
		if (_Response != null)
		{
			try
			{
				if (_Response.asMap().get("hits").parseAsInteger() != 0)
				{
					return false;
				}
			}
			catch (Exception e)
			{
				throw new ParseException();
			}
		}
		return true;
	}

	private static Map<String, Double> parseResponse(DObject _Response) throws ParseException
	{
		System.out.println(_Response);
		Map<String, Double> _ResultMap = new HashMap<String, Double>();
		if (_Response != null)
		{
			try
			{
				List<DObject> _ResponseMapList = _Response.asMap().get("result").asList();
				for (DObject _ResponseMapListElement : _ResponseMapList)
				{
					try
					{
						Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
						/* Bugfix 
						 * der einfache Befehl //String _ImdbId = _ResponseMapListMap.get("output").asMap().get("key:/authority/imdb/title/").asMap().get("/type/object/key").asList().get(0).asString().substring(24);
						 * funktioniert nicht da die ID manchmal so {key:/authority/imdb/title/ => {/type/object/id => [/authority/imdb/title/tt1483013]}}
						 * oder so {key:/authority/imdb/title/ => {/type/object/key => [/authority/imdb/title/tt0289879]}} zurückgegeben wird
						 */
						Map<String, DObject> _ImdbIdMap = _ResponseMapListMap.get("output").asMap().get("key:/authority/imdb/title/").asMap();
						List<DObject> _ImdbIdList = new ArrayList<DObject>(_ImdbIdMap.values());
						String _ImdbId = _ImdbIdList.get(0).asList().get(0).asString().substring(24);
						/* Bugfix ende
						 */
						Double _Factor = _ResponseMapListMap.get("score").parseAsDouble(1.0);
						_ResultMap.put(_ImdbId, _Factor);
					}
					catch (Exception e)
					{
						System.out.println("###### Freebase Result Parsing error (probably for the Imdb-ID of the Candidate with the name "+_ResponseMapListElement.asMap().get("name")+") and freebase key:"+_ResponseMapListElement.asMap().get("id")+" Exception Message: "+e.getMessage());
						e.printStackTrace();
					}
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