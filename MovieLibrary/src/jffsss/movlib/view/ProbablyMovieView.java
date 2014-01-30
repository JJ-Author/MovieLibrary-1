package jffsss.movlib.view;

import java.text.MessageFormat;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;

import jffsss.movlib.MovieInfo;
import jffsss.movlib.ProbablyMovie;
import jffsss.util.Listener;
import jffsss.util.Listeners;

public class ProbablyMovieView
{
	private ProbablyMovie _Model;
	private Border _Component;
	private Border _MovieInfoViewContainer;
	private MovieInfoView _MovieInfoView;
	private Label _ProbabilityText;
	private PushButton _ConfirmButton;

	public ProbablyMovieView(ProbablyMovie _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (Border) _BXMLSerializer.readObject(ProbablyMovieView.class, "ProbablyMovieView.bxml");
			this._MovieInfoViewContainer = (Border) _BXMLSerializer.getNamespace().get("MovieInfoViewContainer");
			this._MovieInfoView = null;
			this._ProbabilityText = (Label) _BXMLSerializer.getNamespace().get("ProbabilityText");
			this._ConfirmButton = (PushButton) _BXMLSerializer.getNamespace().get("ConfirmButton");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new NullPointerException();
		}
		this._Model.onUpdate().addListener(this._OnUpdateListener);
		{
			ButtonPressListener _Listener = new ButtonPressListener()
			{
				@Override
				public void buttonPressed(Button _Button)
				{
					ProbablyMovieView.this.onAction.notifyListeners("Confirm", null);
				}
			};
			this._ConfirmButton.getButtonPressListeners().add(_Listener);
		}
		this.updateMovieInfo();
		this.updateProbability();
	}

	private Listeners onAction = null;

	public Listeners onAction()
	{
		if (this.onAction == null)
			this.onAction = new Listeners(this);
		return this.onAction;
	}

	public Border getComponent()
	{
		return this._Component;
	}

	public ProbablyMovie getModel()
	{
		return this._Model;
	}

	private void updateMovieInfo()
	{
		MovieInfo _MovieInfo = this._Model.getMovieInfo();
		if (_MovieInfo == null)
		{
			this._ConfirmButton.setEnabled(false);
		}
		else
		{
			this._MovieInfoViewContainer.setContent(null);
			this._MovieInfoView = new MovieInfoView();
			this._MovieInfoView.setMovieInfo(_MovieInfo);
			this._MovieInfoViewContainer.setContent(this._MovieInfoView.getComponent());
			this._ConfirmButton.setEnabled(true);
		}
	}

	private void updateProbability()
	{
		double _TotalCount = this._Model.getProbabilityTotalCount();
		double _Probability = _TotalCount > 0 ? this._Model.getProbabilityCount() / _TotalCount : 0;
		if (_Probability > 0)
			this._ProbabilityText.setText(MessageFormat.format("{0,number,#.##%}", _Probability));
		else
			this._ProbabilityText.setText("");
	}

	public void clean()
	{
		this._Model.onUpdate().removeListener(this._OnUpdateListener);
	}

	private OnUpdateListener _OnUpdateListener = new OnUpdateListener();

	private class OnUpdateListener implements Listener
	{
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source == ProbablyMovieView.this._Model)
				switch (_Command)
				{
					case "SetMovieInfo":
						ProbablyMovieView.this.updateMovieInfo();
						break;
					case "SetProbability":
						ProbablyMovieView.this.updateProbability();
						break;
				}
		}
	}
}