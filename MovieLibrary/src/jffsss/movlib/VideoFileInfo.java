package jffsss.movlib;

import java.io.File;

import jffsss.api.OpenSubtitlesHasher;
import jffsss.util.FileNameCleaner;

public class VideoFileInfo
{
	private FileInfo _FileInfo;
	private String _OpenSubtitlesHash;
	private String _CleanedFileName;

	public VideoFileInfo(FileInfo _FileInfo, String _OpenSubtitlesHash, String _CleanedFileName)
	{
		this._FileInfo = _FileInfo;
		this._OpenSubtitlesHash = _OpenSubtitlesHash;
		this._CleanedFileName = _CleanedFileName;
	}

	public static VideoFileInfo getFromFile(String _FilePath)
	{
		FileInfo _FileInfo = FileInfo.getFromFile(_FilePath);
		String _OpenSubtitlesHash = OpenSubtitlesHasher.computeHash(new File(_FilePath));
		String _CleanedFileName = FileNameCleaner.getCleanedFileName(_FileInfo.getName());
		return new VideoFileInfo(_FileInfo, _OpenSubtitlesHash, _CleanedFileName);
	}

	public FileInfo getFileInfo()
	{
		return this._FileInfo;
	}

	public String getOpenSubtitlesHash()
	{
		return this._OpenSubtitlesHash;
	}

	public String getCleanedFileName()
	{
		return this._CleanedFileName;
	}
}