package jffsss.movlib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.util.Listeners;

public class ToStoreFilesCollection
{
	private InStoreFilesCollection _InStoreFilesCollection;
	private Map<Object, ToStoreFile> _ToStoreFiles;

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

	public ToStoreFile addToStoreFile(String _FilePath)
	{
		ToStoreFile _ToStoreFile = this._ToStoreFiles.get(_FilePath);
		if (_ToStoreFile == null)
		{
			// check if not in InStoreFilesCollection
			try {
				if (this._InStoreFilesCollection.filePathInStore(_FilePath))
					return null;	
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			_ToStoreFile = new ToStoreFile();
			this._ToStoreFiles.put(_FilePath, _ToStoreFile);
			_ToStoreFile.startRetrieving(_FilePath);
			this.onUpdate().notifyListeners("AddToStoreFile", _ToStoreFile);
		}
		return _ToStoreFile;
	}

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

	public ToStoreFile removeToStoreFile(String _FilePath)
	{
		ToStoreFile _ToStoreFile = this._ToStoreFiles.remove(_FilePath);
		if (_ToStoreFile != null)
		{
			this.onUpdate().notifyListeners("RemoveToStoreFile", _ToStoreFile);
		}
		return _ToStoreFile;
	}

	public ToStoreFile getToStoreFile(String _FilePath)
	{
		return this._ToStoreFiles.get(_FilePath);
	}

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