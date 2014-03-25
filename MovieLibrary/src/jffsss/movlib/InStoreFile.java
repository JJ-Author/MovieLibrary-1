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
	 * 
	 * @param _LuceneId
	 *            die Dokumenten-ID innerhalb von Lucene
	 * @param _FileInfo
	 *            das FileInfo-Objekt
	 * @param _MovieInfo
	 *            das MovieInfo-Objekt
	 */
	public InStoreFile(Integer _LuceneId, FileInfo _FileInfo, MovieInfo _MovieInfo)
	{
		this._LuceneId = _LuceneId;
		this._FileInfo = _FileInfo;
		this._MovieInfo = _MovieInfo;
	}

	/**
	 * Gibt die Dokumenten-ID innerhalb von Lucene zurück.
	 * 
	 * @return die Dokumenten-ID innerhalb von Lucene
	 */
	public Integer getLuceneId()
	{
		return this._LuceneId;
	}

	/**
	 * Gibt das FileInfo-Objekt zurück.
	 * 
	 * @return das FileInfo-Objekt
	 */
	public FileInfo getFileInfo()
	{
		return this._FileInfo;
	}

	/**
	 * Gibt das MovieInfo-Objekt zurück.
	 * 
	 * @return das MovieInfo-Objekt
	 */
	public MovieInfo getMovieInfo()
	{
		return this._MovieInfo;
	}
}