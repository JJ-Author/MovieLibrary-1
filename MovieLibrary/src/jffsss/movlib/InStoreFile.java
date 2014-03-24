package jffsss.movlib;

/**
 * InStoreFile beinhalten die Informationen des bereits in der Datenbank gespeicherten Films.
 */
public class InStoreFile
{
	private Integer _LuceneId;
	private FileInfo _FileInfo;
	private MovieInfo _MovieInfo;

	/**
	 * Konstruiert ein InStoreFile-Objekt.
	 * @param _LuceneId
	 * @param _FileInfo
	 * @param _MovieInfo
	 */
	public InStoreFile(Integer _LuceneId, FileInfo _FileInfo, MovieInfo _MovieInfo)
	{
		this._LuceneId = _LuceneId;
		this._FileInfo = _FileInfo;
		this._MovieInfo = _MovieInfo;
	}

	public Integer getLuceneId()
	{
		return this._LuceneId;
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