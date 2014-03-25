package jffsss.movlib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.util.Listeners;

/**
 * ToStoreFilesCollection beinhaltet eine Menge von ToStoreFile-Objekte und stellt die Methoden zum Import von diesen
 * Objekten in den Lucene-Index bereit.
 */
public class ToStoreFilesCollection
{
	private InStoreFilesCollection _InStoreFilesCollection;
	private Map<Object, ToStoreFile> _ToStoreFiles;

	/**
	 * Konstruiert ein ToStoreFilesCollection-Objekt.
	 * 
	 * @param _InStoreFilesCollection
	 */
	public ToStoreFilesCollection(InStoreFilesCollection _InStoreFilesCollection)
	{
		this._InStoreFilesCollection = _InStoreFilesCollection;
		this._ToStoreFiles = new HashMap<Object, ToStoreFile>();
	}

	private Listeners onUpdate = null;

	/**
	 * Gibt die Listener zurück.
	 * 
	 * @return die Listener
	 */
	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
		{
			this.onUpdate = new Listeners(this);
		}
		return this.onUpdate;
	}

	/**
	 * Erstellt ein neues ToStoreFile-Objekt, falls keins mit diesem Dateipfad bereits existierte, fügt es in die Liste
	 * ein und führt dessen Methode <CODE>startRetrieving</CODE> aus.
	 * 
	 * @param _FilePath
	 *            der Dateipfad als Schlüssel
	 * @return neu erstelltes oder bereits vorhandenes ToStoreFile-Objekt
	 */
	public ToStoreFile addToStoreFile(String _FilePath)
	{
		ToStoreFile _ToStoreFile = this._ToStoreFiles.get(_FilePath);
		if (_ToStoreFile == null)
		{
			// check if not in InStoreFilesCollection
			try
			{
				if (this._InStoreFilesCollection.filePathInStore(_FilePath))
				{
					return null;
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			_ToStoreFile = new ToStoreFile();
			this._ToStoreFiles.put(_FilePath, _ToStoreFile);
			_ToStoreFile.startRetrieving(_FilePath);
			this.onUpdate().notifyListeners("AddToStoreFile", _ToStoreFile);
		}
		return _ToStoreFile;
	}

	/**
	 * Importiert alle Video-Dateien aus der gegebenen Liste. Für jede Datei aus der Liste und für jede Datei im Order
	 * aus der Liste wird die Methode <CODE>addToStoreFile</CODE> aufgerufen.
	 * 
	 * @param _Files
	 *            die Liste der Video-Dateien und der Ordner mit den Video-Dateien
	 * @return die Liste der erstellten ToStoreFile-Objekte
	 */
	public List<ToStoreFile> addToStoreFilesFromImport(List<File> _Files)
	{
		List<ToStoreFile> _ToStoreFiles = new ArrayList<ToStoreFile>();
		for (File _File : _Files)
		{
			if (_File.isFile())
			{
				_ToStoreFiles.add(this.addToStoreFile(_File.getPath()));
			}
			else if (_File.isDirectory())
			{
				_ToStoreFiles.addAll(this.addToStoreFilesFromImport(Arrays.asList(_File.listFiles(new VideoFileFilter()))));
			}
		}
		return _ToStoreFiles;
	}

	/**
	 * Entfernt das ToStoreFile-Objekt zum gegebenen Dateipfad aus der Liste.
	 * 
	 * @param _FilePath
	 *            der Dateipfad als Schlüssel
	 * @return das ToStoreFile-Objekt oder <CODE>null</CODE> falls kein Eintrag zum gegebenen Dateipfad existiert
	 */
	public ToStoreFile removeToStoreFile(String _FilePath)
	{
		ToStoreFile _ToStoreFile = this._ToStoreFiles.remove(_FilePath);
		if (_ToStoreFile != null)
		{
			this.onUpdate().notifyListeners("RemoveToStoreFile", _ToStoreFile);
		}
		return _ToStoreFile;
	}

	/**
	 * Gibt das ToStoreFile-Objekt zum gegebenen Dateipfad aus der Liste zurück.
	 * 
	 * @param _FilePath
	 *            der Dateipfad als Schlüssel
	 * @return das ToStoreFile-Objekt oder <CODE>null</CODE> falls kein Eintrag zum gegebenen Dateipfad existiert
	 */
	public ToStoreFile getToStoreFile(String _FilePath)
	{
		return this._ToStoreFiles.get(_FilePath);
	}

	/**
	 * Indexiert die Datei-Informationen zum gegebenen Dateipfad und die Film-Informationen zur gegebenen IMDb-ID in
	 * Lucene. Diese Informationen werden aus dem entsprechenden ToStoreFile- und ProbablyMovie-Objekten geholt.
	 * 
	 * @param _FilePath
	 *            der Dateipfad als Schlüssel
	 * @param _ImdbId
	 *            die IMDb-ID als Schlüssel
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public void indexFile(String _FilePath, String _ImdbId) throws IOException
	{
		ToStoreFile _ToStoreFile = this.getToStoreFile(_FilePath);
		if (_ToStoreFile != null)
		{
			FileInfo _FileInfo = _ToStoreFile.getVideoFileInfo().getFileInfo();
			if (_FileInfo != null)
			{
				ProbablyMovie _ProbablyMovie = _ToStoreFile.getProbablyMovie(_ImdbId);
				if (_ProbablyMovie != null)
				{
					MovieInfo _MovieInfo = _ProbablyMovie.getMovieInfo();
					if (_MovieInfo != null)
					{
						this._InStoreFilesCollection.indexFile(_FileInfo, _MovieInfo);
						// save HASH to OpenSubtitles
					}
				}
			}
		}
	}
}