package jffsss.movlib.view;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.media.Image;

import jffsss.movlib.MovieInfo;
import jffsss.util.Utils;

public class MovieInfoView
{
	private MovieInfo _MovieInfo;
	private TablePane _Component;
	private ImageView _PosterImage;
	private TextArea _Text;

	public MovieInfoView()
	{
		this._MovieInfo = null;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (TablePane) _BXMLSerializer.readObject(MovieInfoView.class, "MovieInfoView.bxml");
			this._PosterImage = (ImageView) _BXMLSerializer.getNamespace().get("PosterImage");
			this._Text = (TextArea) _BXMLSerializer.getNamespace().get("Text");
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
					if (MovieInfoView.this._MovieInfo != null)
						if (Desktop.isDesktopSupported())
							try
							{
								Desktop.getDesktop().browse(new URI(MovieInfoView.this._MovieInfo.getImdbUrl()));
							}
							catch (Exception e)
							{}
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
			this._PosterImage.getComponentMouseButtonListeners().add(_Listener);
		}
	}

	public TablePane getComponent()
	{
		return this._Component;
	}

	public void setMovieInfo(MovieInfo _MovieInfo)
	{
		this._MovieInfo = _MovieInfo;
		if (_MovieInfo == null)
		{
			this._PosterImage.setImage((Image) null);
			this._Text.setText("");
		}
		else
		{
			try
			{
				this._PosterImage.setImage(new URL(_MovieInfo.getPosterSource()));
			}
			catch (Exception e)
			{}
			this._Text.setText(buildTextInfo(_MovieInfo));
		}
	}

	private static String buildTextInfo(MovieInfo _MovieInfo)
	{
		StringBuilder _StringBuilder = new StringBuilder();
		_StringBuilder.append(_MovieInfo.getTitle());
		_StringBuilder.append(" ");
		_StringBuilder.append("(" + _MovieInfo.getYear() + ")");
		{
			Double _IMDbRating = _MovieInfo.getImdbRating();
			if (_IMDbRating != null)
			{
				_StringBuilder.append(" - ");
				_StringBuilder.append(_IMDbRating);
			}
		}
		{
			List<String> _Genres = _MovieInfo.getGenres();
			if (_Genres != null)
			{
				_StringBuilder.append("\n");
				_StringBuilder.append(" - ");
				_StringBuilder.append(Utils.join(_Genres, " | "));
			}
		}
		{
			String _Plot = _MovieInfo.getPlot();
			if (_Plot != null)
			{
				_StringBuilder.append("\n");
				_StringBuilder.append(_Plot);
			}
		}
		_StringBuilder.append("\n");
		{
			List<String> _Directors = _MovieInfo.getDirectors();
			if (_Directors != null)
			{
				_StringBuilder.append("\n");
				_StringBuilder.append("Directors:");
				_StringBuilder.append(" ");
				_StringBuilder.append(Utils.join(_Directors, ", "));
			}
		}
		{
			List<String> _Writers = _MovieInfo.getWriters();
			if (_Writers != null)
			{
				_StringBuilder.append("\n");
				_StringBuilder.append("Writers:");
				_StringBuilder.append(" ");
				_StringBuilder.append(Utils.join(_Writers, ", "));
			}
		}
		{
			List<String> _Actors = _MovieInfo.getActors();
			if (_Actors != null)
			{
				_StringBuilder.append("\n");
				_StringBuilder.append("Actors:");
				_StringBuilder.append(" ");
				_StringBuilder.append(Utils.join(_Actors, ", "));
			}
		}
		return _StringBuilder.toString();
	}
}