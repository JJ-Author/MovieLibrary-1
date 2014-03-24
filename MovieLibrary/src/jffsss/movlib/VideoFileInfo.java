package jffsss.movlib;

import java.io.File;
import java.io.IOException;

import jffsss.api.OpenSubtitlesHasher;
import jffsss.util.FileNameCleaner;

/**
 * VideoFileInfo beinhaltet die Basisinformationen einer beliebigen Video-Datei.
 */
public class VideoFileInfo
{
	private FileInfo _FileInfo;
	private String _OpenSubtitlesHash;
	private String _CleanedFileName;

	/**
	 * Konstruiert ein VideoFileInfo-Objekt.
	 * 
	 * @param _FileInfo
	 *            das FileInfo-Objekt
	 * @param _OpenSubtitlesHash
	 *            die Hashsumme für OpenSubtitles
	 * @param _CleanedFileName
	 *            der bereinigte Name der Datei
	 */
	public VideoFileInfo(FileInfo _FileInfo, String _OpenSubtitlesHash, String _CleanedFileName)
	{
		this._FileInfo = _FileInfo;
		this._OpenSubtitlesHash = _OpenSubtitlesHash;
		this._CleanedFileName = _CleanedFileName;
	}

	/**
	 * Erstellt das VideoFileInfo-Objekt direkt aus einer Video-Datei.
	 * 
	 * @param _FilePath
	 *            der Pfad zu der Datei
	 * @return das erstellte VideoFileInfo-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public static VideoFileInfo getFromFile(String _FilePath) throws IOException
	{
		FileInfo _FileInfo = FileInfo.getFromFile(_FilePath);
		String _OpenSubtitlesHash = OpenSubtitlesHasher.computeHash(new File(_FilePath));
		String _CleanedFileName = FileNameCleaner.getCleanedFileName(_FileInfo.getName());
		return new VideoFileInfo(_FileInfo, _OpenSubtitlesHash, _CleanedFileName);
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
	 * Gibt die Hashsumme für OpenSubtitles zurück.
	 * 
	 * @return die Hashsumme für OpenSubtitles
	 */
	public String getOpenSubtitlesHash()
	{
		return this._OpenSubtitlesHash;
	}

	/**
	 * Gibt den bereinigten Name der Datei zurück.
	 * 
	 * @return der bereinigte Name der Datei
	 */
	public String getCleanedFileName()
	{
		return this._CleanedFileName;
	}
}