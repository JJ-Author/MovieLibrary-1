package jffsss.movlib.view;

import jffsss.movlib.MovieLibrary;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.TabPane;
import org.apache.pivot.wtk.Window;

public class MovieLibraryView
{
	private MovieLibrary _Model;
	private Window _Component;
	private TabPane _TabsContainer;
	private InStoreFilesCollectionView _InStoreFilesView;
	private ToStoreFilesCollectionView _ToStoreFilesView;

	public MovieLibraryView(MovieLibrary _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (Window) _BXMLSerializer.readObject(MovieLibraryView.class, "MovieLibraryView.bxml");
			this._TabsContainer = (TabPane) _BXMLSerializer.getNamespace().get("TabsContainer");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new NullPointerException();
		}
		{
			this._InStoreFilesView = new InStoreFilesCollectionView(this._Model.getInStoreFilesCollection());
			this._TabsContainer.getTabs().add(this._InStoreFilesView.getComponent());
			TabPane.setTabData(this._InStoreFilesView.getComponent(), "Search Movie Files");
		}
		{
			this._ToStoreFilesView = new ToStoreFilesCollectionView(this._Model.getToStoreFilesCollection());
			this._TabsContainer.getTabs().add(this._ToStoreFilesView.getComponent());
			TabPane.setTabData(this._ToStoreFilesView.getComponent(), "Import Movie Files");
		}
	}

	public Window getComponent()
	{
		return this._Component;
	}

	public MovieLibrary getModel()
	{
		return this._Model;
	}
}