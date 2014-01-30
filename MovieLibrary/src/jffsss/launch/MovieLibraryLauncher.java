package jffsss.launch;

import java.awt.Dimension;
import java.io.File;

import jffsss.movlib.MovieLibrary;
import jffsss.movlib.view.MovieLibraryView;

import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;

public class MovieLibraryLauncher implements Application
{
	public static void main(String[] _Args)
	{
		DesktopApplicationContext.main(MovieLibraryLauncher.class, _Args);
	}

	private MovieLibrary _Model;
	private MovieLibraryView _View;

	public MovieLibraryLauncher()
	{
		this._Model = null;
		this._View = null;
	}

	@Override
	public void startup(Display _Display, Map<String, String> _Properties) throws Exception
	{
		this._Model = new MovieLibrary(new File(_Properties.get("WorkingDirectory")));
		this._View = new MovieLibraryView(this._Model);

		this._View.getComponent().open(_Display);
		_Display.getHostWindow().setMinimumSize(new Dimension(640, 480));
	}

	@Override
	public boolean shutdown(boolean _Optional)
	{
		if (this._View != null)
			try
			{
				this._View.getComponent().close();
			}
			catch (Exception e)
			{}
		if (this._Model != null)
			try
			{
				this._Model.getInStoreFilesCollection().close();
			}
			catch (Exception e)
			{}
		return false;
	}

	@Override
	public void suspend()
	{}

	@Override
	public void resume()
	{}
}