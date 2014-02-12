package jffsss.movlib.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.movlib.ProbablyMovie;
import jffsss.movlib.ToStoreFile;
import jffsss.movlib.ToStoreFilesCollection;
import jffsss.movlib.VideoFileFilter;
import jffsss.util.Listener;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.TablePane;

public class ToStoreFilesCollectionView
{
	private ToStoreFilesCollection _Model;
	private TablePane _Component;
	private PushButton _ImportFilesButton;
	private PushButton _ImportDirectoriesButton;
	private BoxPane _ToStoreFileViewsContainer;
	private Map<ToStoreFile, ToStoreFileView> _ToStoreFileViews;

	public ToStoreFilesCollectionView(ToStoreFilesCollection _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (TablePane) _BXMLSerializer.readObject(ToStoreFilesCollectionView.class, "ToStoreFilesCollectionView.bxml");
			this._ImportFilesButton = (PushButton) _BXMLSerializer.getNamespace().get("ImportFilesButton");
			this._ImportDirectoriesButton = (PushButton) _BXMLSerializer.getNamespace().get("ImportDirectoriesButton");
			this._ToStoreFileViewsContainer = (BoxPane) _BXMLSerializer.getNamespace().get("ToStoreFileViewsContainer");
			this._ToStoreFileViews = new HashMap<ToStoreFile, ToStoreFileView>();
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
					FileBrowserSheet _FileBrowserSheet = new FileBrowserSheet();
					if (_Button == ToStoreFilesCollectionView.this._ImportFilesButton)
					{
						_FileBrowserSheet.setMode(FileBrowserSheet.Mode.OPEN_MULTIPLE);
					}
					else
					{
						_FileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);
					}
					_FileBrowserSheet.setDisabledFileFilter(new VideoFileFilter());
					_FileBrowserSheet.getStyles().put("hideDisabledFiles", true);
					SheetCloseListener _Listener = new SheetCloseListener()
					{
						@Override
						public void sheetClosed(Sheet _Sheet)
						{
							if (_Sheet.getResult())
							{
								if (_Sheet instanceof FileBrowserSheet)
								{
									FileBrowserSheet _FileBrowserSheet = (FileBrowserSheet) _Sheet;
									Sequence<File> _SelectedFiles = _FileBrowserSheet.getSelectedFiles();
									List<File> _Files = new ArrayList<File>();
									for (int i = 0; i < _SelectedFiles.getLength(); i++)
									{
										_Files.add(_SelectedFiles.get(i));
									}
									ToStoreFilesCollectionView.this._Model.addToStoreFilesFromImport(_Files);
								}
							}
						}
					};
					_FileBrowserSheet.open(ToStoreFilesCollectionView.this._Component.getWindow(), _Listener);
				}
			};
			this._ImportFilesButton.getButtonPressListeners().add(_Listener);
			this._ImportDirectoriesButton.getButtonPressListeners().add(_Listener);
		}
	}

	public TablePane getComponent()
	{
		return this._Component;
	}

	public ToStoreFilesCollection getModel()
	{
		return this._Model;
	}

	public void addToStoreFileView(ToStoreFile _ToStoreFile)
	{
		ToStoreFileView _ToStoreFileView = this._ToStoreFileViews.get(_ToStoreFile);
		if (_ToStoreFileView == null)
		{
			_ToStoreFileView = new ToStoreFileView(_ToStoreFile);
			_ToStoreFileView.onAction().addListener(this._OnActionListener);
			this._ToStoreFileViewsContainer.add(_ToStoreFileView.getComponent());
			this._ToStoreFileViews.put(_ToStoreFile, _ToStoreFileView);
		}
	}

	public void removeToStoreFileView(ToStoreFile _ToStoreFile)
	{
		ToStoreFileView _ToStoreFileView = this._ToStoreFileViews.remove(_ToStoreFile);
		if (_ToStoreFileView != null)
		{
			_ToStoreFileView.onAction().removeListener(this._OnActionListener);
			this._ToStoreFileViewsContainer.remove(_ToStoreFileView.getComponent());
		}
	}

	public void removeAllToStoreFileViews()
	{
		for (ToStoreFileView _ToStoreFileView : this._ToStoreFileViews.values())
		{
			_ToStoreFileView.onAction().removeListener(this._OnActionListener);
			this._ToStoreFileViewsContainer.remove(_ToStoreFileView.getComponent());
		}
		this._ToStoreFileViews.clear();
	}

	private OnUpdateListener _onUpdateListener = new OnUpdateListener();

	private class OnUpdateListener implements Listener
	{
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source == ToStoreFilesCollectionView.this._Model)
			{
				switch (_Command)
				{
					case "AddToStoreFile":
						if (_Arg instanceof ToStoreFile)
							ToStoreFilesCollectionView.this.addToStoreFileView((ToStoreFile) _Arg);
						break;
					case "RemoveToStoreFile":
						if (_Arg instanceof ToStoreFile)
							ToStoreFilesCollectionView.this.removeToStoreFileView((ToStoreFile) _Arg);
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
			if (_Source instanceof ToStoreFileView)
			{
				ToStoreFile _ToStoreFileModel = ((ToStoreFileView) _Source).getModel();
				String _FilePath = _ToStoreFileModel.getVideoFileInfo().getFileInfo().getPath();
				switch (_Command)
				{
					case "ConfirmProbablyMovie":
					{
						if (_Arg instanceof ProbablyMovie)
						{
							ProbablyMovie _ProbablyMovieModel = (ProbablyMovie) _Arg;
							String _ImdbId = _ProbablyMovieModel.getMovieInfo().getImdbId();
							try
							{
								ToStoreFilesCollectionView.this._Model.indexFile(_FilePath, _ImdbId);
								ToStoreFilesCollectionView.this._Model.removeToStoreFile(_FilePath);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						break;
					}
					case "Remove":
					{
						ToStoreFilesCollectionView.this._Model.removeToStoreFile(_FilePath);
						break;
					}
				}
			}
		}
	}
}