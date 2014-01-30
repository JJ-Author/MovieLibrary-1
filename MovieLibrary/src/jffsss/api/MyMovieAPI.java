package jffsss.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import jffsss.util.d.D;
import jffsss.util.d.DObject;

public class MyMovieAPI
{
	public MyMovieAPI()
	{}

	public DObject requestMoviesByID(List<String> _IMDbIDs) throws IOException
	{
		String _URL;
		{
			StringBuilder _StringBuilder = new StringBuilder();
			_StringBuilder.append("tt" + _IMDbIDs.get(0));
			for (int i = 1; i < _IMDbIDs.size(); i++)
				_StringBuilder.append("," + "tt" + _IMDbIDs.get(i));
			_URL = "http://mymovieapi.com/?ids=" + _StringBuilder.toString();
		}
		return this.executeAPI(_URL);
	}

	public DObject requestMovieByID(String _IMDbID) throws IOException
	{
		String _URL = "http://mymovieapi.com/?id=" + "tt" + _IMDbID;
		return this.executeAPI(_URL);
	}

	private DObject executeAPI(String _URL) throws IOException
	{
		HttpURLConnection _Connection = (HttpURLConnection) (new URL(_URL)).openConnection();
		try
		{
			_Connection.setDoOutput(false);
			_Connection.setDoInput(true);
			_Connection.setRequestMethod("GET");
			_Connection.setRequestProperty("User-Agent", "Mozilla/4.0");
			_Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			_Connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			InputStream _InputStream = _Connection.getInputStream();
			try
			{
				return D.fromJson(_InputStream);
			}
			finally
			{
				_InputStream.close();
			}
		}
		finally
		{
			_Connection.disconnect();
		}
	}
}