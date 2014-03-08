package jffsss.movlib;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.ParseException;
import jffsss.api.GoogleApi;
import jffsss.api.HideMyAssProxyProvider;
import jffsss.api.ProxyProvider;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetImdbIdsFromGoogle extends Task<Map<String, Double>>
{
	private static ProxyProvider _ProxyProvider = new HideMyAssProxyProvider(5);
	private static Proxy _CurrentProxy = null;

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
			while (true)
			{
				synchronized (_ProxyProvider)
				{
					if (_CurrentProxy == null)
					{
						_CurrentProxy = _ProxyProvider.provideProxy();
						if (_CurrentProxy == null)
						{
							throw new Exception("Keine Proxies mehr");
						}
					}
				}
				GoogleApi _Api = new GoogleApi(_CurrentProxy);
				try
				{
					System.out.println(">> USING PROXY " + _CurrentProxy + " FOR GOOGLE SEARCH");
					DObject _Response = _Api.requestSearch(this._VideoFileInfo.getCleanedFileName() + " site:imdb.com", 0, 5);
					if (_Response.asMap().get("StatusCode").parseAsInteger() == 200)
					{
						System.out.println(">> PROXY " + _CurrentProxy + " SUCEED FOR " + this._VideoFileInfo.getCleanedFileName());
						return parseResponse(_Response.asMap().get("Content"));
					}
					else
					{
						throw new Exception();
					}
				}
				catch (Exception e)
				{
					System.out.println(">> PROXY " + _CurrentProxy + " FAILED");
					_CurrentProxy = null;
				}
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