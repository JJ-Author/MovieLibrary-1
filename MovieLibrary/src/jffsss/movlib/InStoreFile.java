package jffsss.movlib;

public class InStoreFile
{
	private Integer _LuceneID;
	private FileInfo _FileInfo;
	private MovieInfo _MovieInfo;

	public InStoreFile(Integer _LuceneID, FileInfo _FileInfo, MovieInfo _MovieInfo)
	{
		this._LuceneID = _LuceneID;
		this._FileInfo = _FileInfo;
		this._MovieInfo = _MovieInfo;
	}

	public Integer getLuceneID()
	{
		return this._LuceneID;
	}

	public FileInfo getFileInfo()
	{
		return this._FileInfo;
	}

	public MovieInfo getMovieInfo()
	{
		return this._MovieInfo;
	}
}