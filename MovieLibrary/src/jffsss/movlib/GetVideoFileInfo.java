package jffsss.movlib;


import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetVideoFileInfo extends Task<VideoFileInfo>
{
	private String _FilePath;

	public GetVideoFileInfo(String _FilePath)
	{
		this._FilePath = _FilePath;
	}

	public VideoFileInfo execute() throws TaskExecutionException
	{
		try
		{
			return VideoFileInfo.getFromFile(this._FilePath);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e);
		}
	}
}