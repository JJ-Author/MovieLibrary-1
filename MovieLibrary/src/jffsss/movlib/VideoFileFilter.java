package jffsss.movlib;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pivot.util.Filter;

/**
 * VideoFileFilter filtert die Video-Dateien.
 */
public class VideoFileFilter implements FileFilter, Filter<File>
{
	private static FileNameExtensionFilter _FileFilter;

	/**
	 * Konstruiert das VideoFileFilter-Objekt. Als Video-Dateien werden die Dateien erkannt, die ein der Endungen avi,
	 * mkv oder mp4 haben.
	 */
	public VideoFileFilter()
	{
		//this._FileFilter = new FileNameExtensionFilter("Video Files", "avi", "mkv", "mp4", "mpg", "mpeg", "mov", "divx", "xvid", "vob", "iso", "ts", "flv");
		
	}
	

	public static  void setFileExtensions(String extensions)
	{
		if (extensions==null || extensions.isEmpty())
			_FileFilter = new FileNameExtensionFilter("Video Files", "avi", "mkv", "mp4", "mpg", "mpeg", "mov", "divx", "xvid", "iso");
		else
			_FileFilter = new FileNameExtensionFilter("Video Files", extensions.split(","));
	}
	
	@Override
	public boolean accept(File _File)
	{
		return _FileFilter.accept(_File);
	}

	@Override
	public boolean include(File _File)
	{
		return !_FileFilter.accept(_File);
	}
}