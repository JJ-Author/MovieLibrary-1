package jffsss.movlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.OpenSubtitlesApi;
import jffsss.util.concurrent.AbstractBufferedExecutor;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetImdbIdsFromOpenSubtitles extends Task<Map<String, Double>>
{
	private static BufferedExecutor _BufferedExecutor = new BufferedExecutor();

	private VideoFileInfo _VideoFileInfo;

	public GetImdbIdsFromOpenSubtitles(VideoFileInfo _VideoFileInfo)
	{
		this._VideoFileInfo = _VideoFileInfo;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		return _BufferedExecutor.execute(this._VideoFileInfo.getOpenSubtitlesHash());
	}

	private static class BufferedExecutor extends AbstractBufferedExecutor<String, Map<String, Double>>
	{
		public BufferedExecutor()
		{
			super(1, 1000, 500);
		}

		@Override
		protected void execute()
		{
			List<String> _FileHashs = this.pollInputs(10);
			if (_FileHashs.isEmpty())
				return;
			try
			{
				OpenSubtitlesApi _Api = new OpenSubtitlesApi();
				String _Token = _Api.requestLogIn("", "");
				try
				{
					DObject _Response = _Api.requestCheckMovieHash2(_Token, _FileHashs);
					Map<String, Map<String, Double>> _Results = parseResponse(_Response.asMap().get("Content"));
					for (String _FileHash : _FileHashs)
					{
						Map<String, Double> _Result = _Results.get(_FileHash);
						if (_Result == null)
							_Result = new HashMap<String, Double>();
						this.setResult(_FileHash, _Result);
					}
				}
				finally
				{
					try
					{
						_Api.requestLogOut(_Token);
					}
					catch (Exception e)
					{}
				}
			}
			catch (Exception e)
			{
				this.setFault(e.getMessage());
			}
		}

		private static Map<String, Map<String, Double>> parseResponse(DObject _Response) throws ParseException
		{
			Map<String, Map<String, Double>> _ResultMap = new HashMap<String, Map<String, Double>>();
			if (_Response != null)
			{
				try
				{
					Map<String, DObject> _ResponseMap = _Response.asMap();
					for (Map.Entry<String, DObject> _ResponseMapEntry : _ResponseMap.entrySet())
					{
						try
						{
							String _ResultMapKey = _ResponseMapEntry.getKey();
							Map<String, Double> _ResultMapMap = new HashMap<String, Double>();
							List<DObject> _ResponseMapList = _ResponseMapEntry.getValue().asList();
							for (DObject _ResponseMapListElement : _ResponseMapList)
							{
								Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
								String _ImdbId = _ResponseMapListMap.get("MovieImdbID").asString();
								Double _Factor = _ResponseMapListMap.get("SeenCount").parseAsDouble(1.0);
								_ResultMapMap.put(_ImdbId, _Factor);
							}
							_ResultMap.put(_ResultMapKey, _ResultMapMap);
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
}