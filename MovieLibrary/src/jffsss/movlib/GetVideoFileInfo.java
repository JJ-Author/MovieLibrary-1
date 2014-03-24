package jffsss.movlib;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

/**
 * GetVideoFileInfo ist eine Task-Klasse für das asynchrone Erstellen der VideoFileInfo-Objekte aus den Dateien.
 */
public class GetVideoFileInfo extends Task<VideoFileInfo>
{
	private String _FilePath;

	/**
	 * Konstruiert ein GetVideoFileInfo-Objekt.
	 * 
	 * @param _FilePath
	 *            der Pfad zu der Datei
	 */
	public GetVideoFileInfo(String _FilePath)
	{
		this._FilePath = _FilePath;
	}

	@Override
	public VideoFileInfo execute() throws TaskExecutionException
	{
		try
		{
			return VideoFileInfo.getFromFile(this._FilePath);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e.getMessage());
		}
	}
}