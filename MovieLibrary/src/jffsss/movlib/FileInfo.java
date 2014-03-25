package jffsss.movlib;

import java.io.File;
import java.io.IOException;

/**
 * FileInfo beinhaltet die Basisinformationen einer beliebigen Datei.
 */
public class FileInfo
{
	private String _Path;
	private Long _Size;

	/**
	 * Konstruiert ein FileInfo-Objekt.
	 * 
	 * @param _Path
	 *            der Pfad zur Datei
	 * @param _Size
	 *            die Größe der Datei
	 */
	public FileInfo(String _Path, Long _Size)
	{
		this._Path = _Path;
		this._Size = _Size;
	}

	/**
	 * Erstellt das FileInfo-Objekt direkt aus einer Datei.
	 * 
	 * @param _FilePath
	 *            der Pfad zur Datei
	 * @return das erstellte FileInfo-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public static FileInfo getFromFile(String _FilePath) throws IOException
	{
		try
		{
			File _File = new File(_FilePath);
			Long _Size = _File.length();
			return new FileInfo(_FilePath, _Size);
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Gibt den Pfad zu der Datei zurück.
	 * 
	 * @return der Pfad zur Datei
	 */
	public String getPath()
	{
		return this._Path;
	}

	/**
	 * Gibt den Namen der Datei zurück.
	 * 
	 * @return der Name der Datei
	 */
	public String getName()
	{
		return (this._Path == null) ? null : (new File(this._Path)).getName();
	}

	/**
	 * Gibt das Verzeichnis der Datei zurück.
	 * 
	 * @return das Verzeichnis der Datei
	 */
	public String getDirectory()
	{
		return (this._Path == null) ? null : (new File(this._Path)).getParent();
	}

	/**
	 * Gibt die Größe der Datei zurück.
	 * 
	 * @return die Größe der Datei
	 */
	public Long getSize()
	{
		return this._Size;
	}
}