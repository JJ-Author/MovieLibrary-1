package jffsss.movlib.view;

import java.awt.Desktop;
import java.io.File;

import jffsss.movlib.FileInfo;
import jffsss.movlib.InStoreFile;
import jffsss.movlib.MovieInfo;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Mouse;

public class InStoreFileView
{
	private InStoreFile _Model;
	private Border _Component;
	private Label _FilePathText;
	private Border _MovieInfoViewContainer;
	private MovieInfoView _MovieInfoView;

	public InStoreFileView(InStoreFile _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (Border) _BXMLSerializer.readObject(InStoreFileView.class, "InStoreFileView.bxml");
			this._FilePathText = (Label) _BXMLSerializer.getNamespace().get("FilePathText");
			this._MovieInfoViewContainer = (Border) _BXMLSerializer.getNamespace().get("MovieInfoViewContainer");
			this._MovieInfoView = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new NullPointerException();
		}
		{
			ComponentMouseButtonListener _Listener = new ComponentMouseButtonListener()
			{
				@Override
				public boolean mouseClick(Component _Component, Mouse.Button _MouseButton, int _X, int _Y, int _Count)
				{
					if (InStoreFileView.this._Model.getFileInfo() != null)
					{
						if (Desktop.isDesktopSupported())
						{
							try
							{
								Desktop.getDesktop().open(new File(InStoreFileView.this._Model.getFileInfo().getPath()));
							}
							catch (Exception e)
							{}
						}
					}
					return true;
				}

				@Override
				public boolean mouseDown(Component _Component, Mouse.Button _MouseButton, int _X, int _Y)
				{
					return false;
				}

				@Override
				public boolean mouseUp(Component _Component, Mouse.Button _MouseButton, int _X, int _Y)
				{
					return false;
				}
			};
			this._FilePathText.getComponentMouseButtonListeners().add(_Listener);
		}
		this.updateFileInfo();
		this.updateMovieInfo();
	}

	public Border getComponent()
	{
		return this._Component;
	}

	public InStoreFile getModel()
	{
		return this._Model;
	}

	public void updateFileInfo()
	{
		FileInfo _FileInfo = this._Model.getFileInfo();
		if (_FileInfo != null)
		{
			this._FilePathText.setText(_FileInfo.getPath());
		}
	}

	public void updateMovieInfo()
	{
		MovieInfo _MovieInfo = this._Model.getMovieInfo();
		if (_MovieInfo != null)
		{
			this._MovieInfoView = new MovieInfoView();
			this._MovieInfoView.setMovieInfo(_MovieInfo);
			this._MovieInfoViewContainer.setContent(this._MovieInfoView.getComponent());
		}
	}
}