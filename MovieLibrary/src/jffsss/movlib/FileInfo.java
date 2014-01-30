package jffsss.movlib;

import java.io.File;

public class FileInfo
{
	private String _Path;
	private Long _Size;

	public FileInfo(String _Path, Long _Size)
	{
		this._Path = _Path;
		this._Size = _Size;
	}

	public static FileInfo getFromFile(String _FilePath)
	{
		try
		{
			File _File = new File(_FilePath);
			Long _Size = _File.length();
			return new FileInfo(_FilePath, _Size);
		}
		catch (Exception e)
		{
			throw new RuntimeException("I/O", e);
		}
	}

	public String getPath()
	{
		return this._Path;
	}

	public String getName()
	{
		return (this._Path == null) ? null : (new File(this._Path)).getName();
	}

	public String getDirectory()
	{
		return (this._Path == null) ? null : (new File(this._Path)).getParent();
	}

	public Long getSize()
	{
		return this._Size;
	}
}