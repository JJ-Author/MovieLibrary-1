package jffsss.movlib;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pivot.util.Filter;

public class VideoFileFilter implements FileFilter, Filter<File>
{
	private FileNameExtensionFilter _FileFilter;

	public VideoFileFilter()
	{
		this._FileFilter = new FileNameExtensionFilter("Video Files", "avi", "mkv", "mp4");
	}

	@Override
	public boolean accept(File _File)
	{
		return this._FileFilter.accept(_File);
	}

	@Override
	public boolean include(File _File)
	{
		return !this._FileFilter.accept(_File);
	}
}