package jffsss.util;

public class FileNameCleaner
{
	private FileNameCleaner()
	{}

	public static String getCleanedFileName(String _FileName)
	{
		String _Result = _FileName;
		_Result = _Result.replaceAll("(?i)\\.[a-z2-4]{3}$", ""); // delete file suffix
		_Result = _Result.replaceAll("(?i)\\.ts$", "");
		_Result = _Result.replaceAll("(?i)\\.mpeg$", "");
		_Result = _Result.replace('_', ' '); // use only blanks instead of underscore
		_Result = _Result.replaceAll("5\\.1", "");
		_Result = _Result.replaceAll("7\\.1", "");
		//_Result = _Result.replaceAll("(?i)([^\\.])(\\.)([^ ])", "$1 $3"); // if dots used as whitespace replace them with
		_Result = _Result.replaceAll("\\.([^\\. ])", " $1"); // if dots used as whitespace replace them with
																	// whitespace

		// remove some keywords indicating quality or format of the movie file
		_Result = _Result.replaceAll("(?i)\\D720p?.*$", "");
		_Result = _Result.replaceAll("(?i)1080[pi]?", "");
		_Result = _Result.replaceAll("(?i)CD ?\\d\\d?", "");
		_Result = _Result.replaceAll("(?i)DVD ?\\d\\d?", "");
		_Result = _Result.replaceAll("(?i)part ?\\d\\d?", "");
		_Result = _Result.replaceAll("(?i)bdrip.*$", "");
		_Result = _Result.replaceAll("(?i)hdrip.*$", "");
		_Result = _Result.replaceAll("(?i)dvdrip", "");
		_Result = _Result.replaceAll("(?i)dubbed", "");
		_Result = _Result.replaceAll("(?i)[xh]?264.*$", "");
		_Result = _Result.replaceAll("(?i)divx", "");
		_Result = _Result.replaceAll("(?i)xvid", "");
		_Result = _Result.replaceAll("(?i)ac3.*$", "");
		_Result = _Result.replaceAll("(?i)dts", "");
		_Result = _Result.replaceAll("(?i)mp3", "");
		_Result = _Result.replaceAll("(?i)mp4", "");
		_Result = _Result.replaceAll("(?i)BluRay.*$", "");
		_Result = _Result.replaceAll("(?i)German", "");
		_Result = _Result.replaceAll("DL", "");
		_Result = _Result.replaceAll("(?i)RETAIL", "");
		_Result = _Result.replaceAll("(?i)UNCUT", "");
		_Result = _Result.replaceAll("(?i)EXTENDED ?(CUT)?", "");


		// remove anything between brackets
		_Result = _Result.replaceAll("(?i)\\(.*\\)", "");
		_Result = _Result.replaceAll("(?i)\\[.*\\]", "");
		
		
		_Result = _Result.replaceAll("  ", " "); //remove double whitespaces
		_Result = _Result.trim();
		_Result = _Result.replaceAll("\\.$", "");
		// Pattern pattern = Pattern.compile();
		// Matcher matcher = pattern.matcher(name);
		// result = matcher.replaceAll("");

		return _Result;
	}
}