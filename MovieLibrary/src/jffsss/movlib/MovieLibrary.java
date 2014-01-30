package jffsss.movlib;

import java.io.File;
import java.io.IOException;

public class MovieLibrary
{
	InStoreFilesCollection _InStoreFilesCollection;
	ToStoreFilesCollection _ToStoreFilesCollection;

	public MovieLibrary(File _WorkingDirectory) throws IOException
	{
		this._InStoreFilesCollection = new InStoreFilesCollection(new File(_WorkingDirectory, "MovieIndex"));
		this._ToStoreFilesCollection = new ToStoreFilesCollection(this._InStoreFilesCollection);
	}

	public InStoreFilesCollection getInStoreFilesCollection()
	{
		return this._InStoreFilesCollection;
	}

	public ToStoreFilesCollection getToStoreFilesCollection()
	{
		return this._ToStoreFilesCollection;
	}
}