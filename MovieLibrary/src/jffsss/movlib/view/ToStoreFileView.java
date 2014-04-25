package jffsss.movlib.view;

import java.util.HashMap;
import java.util.Map;

import jffsss.movlib.ProbablyMovie;
import jffsss.movlib.ToStoreFile;
import jffsss.util.Listener;
import jffsss.util.Listeners;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.FlowPane;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;

public class ToStoreFileView
{
	private ToStoreFile _Model;
	private Border _Component;
	private Label _FilePathText;
	private TextInput _AddMovieText;
	private PushButton _AddMovieButton;
	private PushButton _RemoveButton;
	private FlowPane _ProbablyMovieViewsContainer;
	private Map<ProbablyMovie, ProbablyMovieView> _ProbablyMovieViews;

	public ToStoreFileView(ToStoreFile _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (Border) _BXMLSerializer.readObject(ToStoreFileView.class, "ToStoreFileView.bxml");
			this._FilePathText = (Label) _BXMLSerializer.getNamespace().get("FilePathText");
			this._AddMovieText = (TextInput) _BXMLSerializer.getNamespace().get("AddMovieText");
			this._AddMovieButton = (PushButton) _BXMLSerializer.getNamespace().get("AddMovieButton");
			this._RemoveButton = (PushButton) _BXMLSerializer.getNamespace().get("RemoveButton");
			this._ProbablyMovieViewsContainer = (FlowPane) _BXMLSerializer.getNamespace().get("ProbablyMovieViewsContainer");
			this._ProbablyMovieViews = new HashMap<ProbablyMovie, ProbablyMovieView>();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new NullPointerException();
		}
		this._Model.onUpdate().addListener(this._onUpdateListener);
		{
			ButtonPressListener _Listener = new ButtonPressListener()
			{
				@Override
				public void buttonPressed(Button _Button)
				{
					String _IMDbID = ToStoreFileView.this._AddMovieText.getText();
					ToStoreFileView.this._AddMovieText.setText("");
					ToStoreFileView.this._Model.addProbablyMovie(_IMDbID, -1.0);
				}
			};
			this._AddMovieButton.getButtonPressListeners().add(_Listener);
		}
		{
			ButtonPressListener _Listener = new ButtonPressListener()
			{
				@Override
				public void buttonPressed(Button _Button)
				{
					ToStoreFileView.this.onAction.notifyListeners("Remove", null);
				}
			};
			this._RemoveButton.getButtonPressListeners().add(_Listener);
		}
	}

	private Listeners onAction = null;

	public Listeners onAction()
	{
		if (this.onAction == null)
		{
			this.onAction = new Listeners(this);
		}
		return this.onAction;
	}

	public Border getComponent()
	{
		return this._Component;
	}

	public ToStoreFile getModel()
	{
		return this._Model;
	}

	public void updateVideoFileInfo()
	{
		if (this._Model.getVideoFileInfo() == null)
		{
			this._FilePathText.setText("");
		}
		else
		{
			this._FilePathText.setText(this._Model.getVideoFileInfo().getFileInfo().getPath() + " || " + this._Model.getVideoFileInfo().getCleanedFileName());
		}
	}

	public void addProbablyMovieView(ProbablyMovie _ProbablyMovie)
	{
		ProbablyMovieView _ProbablyMovieView = this._ProbablyMovieViews.get(_ProbablyMovie);
		if (_ProbablyMovieView == null)
		{
			_ProbablyMovieView = new ProbablyMovieView(_ProbablyMovie);
			_ProbablyMovieView.onAction().addListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.add(_ProbablyMovieView.getComponent());
			this._ProbablyMovieViews.put(_ProbablyMovie, _ProbablyMovieView);
		}
	}

	public void removeProbablyMovieView(ProbablyMovie _ProbablyMovie)
	{
		ProbablyMovieView _ProbablyMovieView = this._ProbablyMovieViews.remove(_ProbablyMovie);
		if (_ProbablyMovieView != null)
		{
			_ProbablyMovieView.onAction().removeListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.remove(_ProbablyMovieView.getComponent());
		}
	}

	public void clearProbablyMovieViews()
	{
		for (ProbablyMovieView _ProbablyMovieView : this._ProbablyMovieViews.values())
		{
			_ProbablyMovieView.onAction().removeListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.remove(_ProbablyMovieView.getComponent());
		}
		this._ProbablyMovieViews.clear();
	}

	public void clean()
	{
		this._Model.onUpdate().removeListener(this._onUpdateListener);
	}

	private OnUpdateListener _onUpdateListener = new OnUpdateListener();

	private class OnUpdateListener implements Listener
	{
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source == ToStoreFileView.this._Model)
			{
				switch (_Command)
				{
					case "SetVideoFileInfo":
						ToStoreFileView.this.updateVideoFileInfo();
						break;
					case "AddProbablyMovie":
						if (_Arg instanceof ProbablyMovie)
							ToStoreFileView.this.addProbablyMovieView((ProbablyMovie) _Arg);
						break;
					case "DeleteProbablyMovies":
						ToStoreFileView.this.clearProbablyMovieViews();
						break;
				}
			}
		}
	}

	private OnActionListener _OnActionListener = new OnActionListener();

	private class OnActionListener implements Listener
	{
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source instanceof ProbablyMovieView)
			{
				ProbablyMovieView _ProbablyMovieView = (ProbablyMovieView) _Source;
				switch (_Command)
				{
					case "Confirm":
						ToStoreFileView.this.onAction().notifyListeners("ConfirmProbablyMovie", _ProbablyMovieView.getModel());
						break;
				}
			}
		}
	}
}