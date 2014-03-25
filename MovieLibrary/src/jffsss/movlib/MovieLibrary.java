package jffsss.movlib;

import java.io.File;
import java.io.IOException;

/**
 * MovieLibrary beinhaltet zwei Objekte: InStoreFilesCollection und ToStoreFilesCollection.
 */
public class MovieLibrary
{
	InStoreFilesCollection _InStoreFilesCollection;
	ToStoreFilesCollection _ToStoreFilesCollection;

	/**
	 * Konstruiert ein MovieLibrary-Objekt.
	 * 
	 * @param _WorkingDirectory
	 *            das Verzeichnis, wo die f�r das Programm ben�tigten Dateien abgelegt werden
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public MovieLibrary(File _WorkingDirectory) throws IOException
	{
		this._InStoreFilesCollection = new InStoreFilesCollection(new File(_WorkingDirectory, "MovieIndex"));
		this._ToStoreFilesCollection = new ToStoreFilesCollection(this._InStoreFilesCollection);
	}

	/**
	 * Gibt das InStoreFilesCollection-Objekt zur�ck.
	 * 
	 * @return das InStoreFilesCollection-Objekt
	 */
	public InStoreFilesCollection getInStoreFilesCollection()
	{
		return this._InStoreFilesCollection;
	}

	/**
	 * Gibt das ToStoreFilesCollection-Objekt zur�ck.
	 * 
	 * @return das ToStoreFilesCollection-Objekt
	 */
	public ToStoreFilesCollection getToStoreFilesCollection()
	{
		return this._ToStoreFilesCollection;
	}
}