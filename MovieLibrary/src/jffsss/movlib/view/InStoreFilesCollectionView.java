package jffsss.movlib.view;

import java.util.HashMap;
import java.util.Map;

import jffsss.movlib.InStoreFile;
import jffsss.movlib.InStoreFilesCollection;
import jffsss.util.Listener;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.FlowPane;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TextInput;

public class InStoreFilesCollectionView
{
	private InStoreFilesCollection _Model;
	private TablePane _Component;
	private TextInput _SearchMoviesText;
	private PushButton _SearchMoviesButton;
	private FlowPane _InStoreFileViewsContainer;
	private Map<InStoreFile, InStoreFileView> _InStoreFileViews;

	public InStoreFilesCollectionView(InStoreFilesCollection _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (TablePane) _BXMLSerializer.readObject(InStoreFilesCollectionView.class, "InStoreFilesCollectionView.bxml");
			this._SearchMoviesText = (TextInput) _BXMLSerializer.getNamespace().get("SearchMoviesText");
			this._SearchMoviesButton = (PushButton) _BXMLSerializer.getNamespace().get("SearchMoviesButton");
			this._InStoreFileViewsContainer = (FlowPane) _BXMLSerializer.getNamespace().get("InStoreFileViewsContainer");
			this._InStoreFileViews = new HashMap<InStoreFile, InStoreFileView>();
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
					String _Query = InStoreFilesCollectionView.this._SearchMoviesText.getText();
					InStoreFilesCollectionView.this._Model.removeAllInStoreFiles();
					try
					{
						InStoreFilesCollectionView.this._Model.addInStoreFilesFromSearch(_Query);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			};
			this._SearchMoviesButton.getButtonPressListeners().add(_Listener);
		}
	}

	public TablePane getComponent()
	{
		return this._Component;
	}

	public InStoreFilesCollection getModel()
	{
		return this._Model;
	}

	public void addInStoreFileView(InStoreFile _InStoreFileModel)
	{
		InStoreFileView _InStoreFileView = this._InStoreFileViews.get(_InStoreFileModel);
		if (_InStoreFileView == null)
		{
			_InStoreFileView = new InStoreFileView(_InStoreFileModel);
			this._InStoreFileViewsContainer.add(_InStoreFileView.getComponent());
			this._InStoreFileViews.put(_InStoreFileModel, _InStoreFileView);
		}
	}

	public void removeInStoreFileView(InStoreFile _InStoreFileModel)
	{
		InStoreFileView _InStoreFileView = this._InStoreFileViews.remove(_InStoreFileModel);
		if (_InStoreFileView != null)
		{
			this._InStoreFileViewsContainer.remove(_InStoreFileView.getComponent());
		}
	}

	public void removeAllInStoreFileViews()
	{
		for (InStoreFileView _InStoreFileView : this._InStoreFileViews.values())
		{
			this._InStoreFileViewsContainer.remove(_InStoreFileView.getComponent());
		}
		this._InStoreFileViews.clear();
	}

	private OnUpdateListener _onUpdateListener = new OnUpdateListener();

	private class OnUpdateListener implements Listener
	{
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source == InStoreFilesCollectionView.this._Model)
			{
				switch (_Command)
				{
					case "AddInStoreFile":
						if (_Arg instanceof InStoreFile)
							InStoreFilesCollectionView.this.addInStoreFileView((InStoreFile) _Arg);
						break;
					case "RemoveInStoreFile":
						if (_Arg instanceof InStoreFile)
							InStoreFilesCollectionView.this.removeInStoreFileView((InStoreFile) _Arg);
						break;
					case "RemoveAllInStoreFiles":
						InStoreFilesCollectionView.this.removeAllInStoreFileViews();
						break;
				}
			}
		}
	}
}