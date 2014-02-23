package jffsss.movlib;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.MatchAllDocsQuery;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import jffsss.util.Listeners;
import jffsss.util.Utils;

public class InStoreFilesCollection implements Closeable
{
	private Directory _Directory;
	private IndexWriter _IndexWriter;
	private DirectoryReader _DirectoryReader;
	private Map<Object, InStoreFile> _InStoreFiles;

	public InStoreFilesCollection(File _Directory) throws IOException
	{
		_Directory.mkdir();
		this._Directory = FSDirectory.open(_Directory);
		Analyzer _Analyzer = new StandardAnalyzer(Version.LUCENE_46);
		IndexWriterConfig _IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, _Analyzer);
		this._IndexWriter = new IndexWriter(this._Directory, _IndexWriterConfig);
		this._IndexWriter.commit();
		this._DirectoryReader = DirectoryReader.open(InStoreFilesCollection.this._Directory);
		this._InStoreFiles = new HashMap<Object, InStoreFile>();
	}

	public void close() throws IOException
	{
		this._IndexWriter.close();
		this._DirectoryReader.close();
		this._Directory.close();
	}

	private Listeners onUpdate = null;

	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
		{
			this.onUpdate = new Listeners(this);
		}
		return this.onUpdate;
	}
	/**
	 * 
	 * @param _File
	 * @throws IOException
	 */
	public void exportAsJson(File _File) throws IOException
	{
		JsonArray _JsonArray = this.getMoviesAsJson(false);
		PrintWriter _PrintWriter = new PrintWriter(_File);
		try
		{
			_PrintWriter.println(_JsonArray.toString());
		}
		finally
		{
			try
			{
				_PrintWriter.close();
			}
			catch (Exception e)
			{}
		}
	}

	/**
	 * writes movie index in exhibit jsonp format to file for exhibit faceted browsing
	 * @param _File
	 * @throws IOException
	 */
	public void writeExhibitJsonp(File _File) throws IOException 
	{
		/*BufferedWriter bw = new BufferedWriter(new FileWriter(_File));
		Gson gson = new Gson();
		gson.toJson(this.getExhibitJson(), new JsonWriter(bw));*/
		
		String _json = new Gson().toJson(this.getExhibitJson()); 
		String _jsonp = "callback("+_json+");"; //convert json to jsonp for local use of exhibit
		PrintWriter _PrintWriter = new PrintWriter(_File,"UTF-8");
		try
		{
			_PrintWriter.print('\ufeff'); //write UTF-8 with BOM, umlaute are only correct in exhibit if they are 
										  //unicode escaped or in latin1, but BOM seems to help exhibit to understand utf8 without escaping
			_PrintWriter.println(_jsonp);
		}
		finally
		{
			try
			{
				_PrintWriter.close();
			}
			catch (Exception e)
			{}
		}
	}

	
	/**
	 * returns the movie index in exhibit json format as JsonObject
	 * @return
	 * @throws IOException
	 */
	public JsonObject getExhibitJson() throws IOException 
	{
		JsonObject _top = new JsonObject();
		JsonObject _properties = new JsonObject();
			JsonObject _prop;
			_prop = new JsonObject(); _prop.addProperty("valueType", "number");
				_properties.add("MovieDuration", _prop);
			_prop = new JsonObject(); _prop.addProperty("valueType", "number");
				_properties.add("MovieImdbRating", _prop);
			_prop = new JsonObject(); _prop.addProperty("valueType", "url");
				_properties.add("MoviePosterSource", _prop);
				
		_top.add("properties", _properties);
		_top.add("items", this.getMoviesAsJson(true));
		return _top;
	}
	
	/**
	 * returns all movies in index with attributes as JsonArray
	 * @param _exhibitMode if true the property name for German title is label and filepath id(needed for exhibit json format)
	 * @return 
	 * @throws IOException
	 */
	public JsonArray getMoviesAsJson(boolean _exhibitMode) throws IOException
	{
		JsonArray _JsonArray = new JsonArray();
		{
			for (int i = 0; i < this.getInStoreFilesCount(); i++)
			{
				JsonObject _JsonArrayObject = new JsonObject();
				InStoreFile _InStoreFile = createInStoreFile(i, this.getDirectoryReader());
				String FilepathName =  (_exhibitMode) ? "id" : "FilePath";
				_JsonArrayObject.addProperty(FilepathName, _InStoreFile.getFileInfo().getPath());
				_JsonArrayObject.addProperty("MovieTitle", _InStoreFile.getMovieInfo().getTitle());
				String titleKeyName =  (_exhibitMode) ? "label" : "MovieTitleDE";
				_JsonArrayObject.addProperty(titleKeyName, _InStoreFile.getMovieInfo().getTitleDe());
				_JsonArrayObject.addProperty("MovieDuration", _InStoreFile.getMovieInfo().getDuration());
				_JsonArrayObject.addProperty("MovieYear", _InStoreFile.getMovieInfo().getYear());
				_JsonArrayObject.addProperty("MoviePlot", _InStoreFile.getMovieInfo().getPlot());
				{
					JsonArray _JsonArrayObjectArray = new JsonArray();
					for (String _Genre : _InStoreFile.getMovieInfo().getGenres())
					{
						_JsonArrayObjectArray.add(new JsonPrimitive(_Genre));
					}
					_JsonArrayObject.add("MovieGenres", _JsonArrayObjectArray);
				}
				{
					JsonArray _JsonArrayObjectArray = new JsonArray();
					for (String _Director : _InStoreFile.getMovieInfo().getDirectors())
					{
						_JsonArrayObjectArray.add(new JsonPrimitive(_Director));
					}
					_JsonArrayObject.add("MovieDirectors", _JsonArrayObjectArray);
				}
				{
					JsonArray _JsonArrayObjectArray = new JsonArray();
					for (String _Writer : _InStoreFile.getMovieInfo().getWriters())
					{
						_JsonArrayObjectArray.add(new JsonPrimitive(_Writer));
					}
					_JsonArrayObject.add("MovieWriters", _JsonArrayObjectArray);
				}
				{
					JsonArray _JsonArrayObjectArray = new JsonArray();
					for (String _Actor : _InStoreFile.getMovieInfo().getActors())
					{
						_JsonArrayObjectArray.add(new JsonPrimitive(_Actor));
					}
					_JsonArrayObject.add("MovieActors", _JsonArrayObjectArray);
				}
				_JsonArrayObject.addProperty("MovieImdbId", _InStoreFile.getMovieInfo().getImdbId());
				_JsonArrayObject.addProperty("MovieImdbRating", _InStoreFile.getMovieInfo().getImdbRating());
				_JsonArrayObject.addProperty("MoviePosterSource", _InStoreFile.getMovieInfo().getPosterSource());
				_JsonArray.add(_JsonArrayObject);
			}
		}
		return _JsonArray;
	}

	public InStoreFile addInStoreFile(Integer _LuceneId) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		return this.addInStoreFile(_LuceneId, _DirectoryReader);
	}

	private InStoreFile addInStoreFile(Integer _LuceneId, DirectoryReader _DirectoryReader) throws IOException
	{
		InStoreFile _InStoreFile = this._InStoreFiles.get(_LuceneId);
		if (_InStoreFile == null)
		{
			_InStoreFile = createInStoreFile(_LuceneId, _DirectoryReader);
			this.onUpdate().notifyListeners("AddInStoreFile", _InStoreFile);
		}
		return _InStoreFile;
	}

	public List<InStoreFile> addInStoreFiles(List<Integer> _LuceneIds) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		return this.addInStoreFiles(_LuceneIds, _DirectoryReader);
	}

	private List<InStoreFile> addInStoreFiles(List<Integer> _LuceneIds, DirectoryReader _DirectoryReader) throws IOException
	{
		List<InStoreFile> _InStoreFiles = new ArrayList<InStoreFile>();
		for (Integer _LuceneId : _LuceneIds)
		{
			_InStoreFiles.add(this.addInStoreFile(_LuceneId, _DirectoryReader));
		}
		return _InStoreFiles;
	}

	public List<InStoreFile> addInStoreFilesFromSearch(String _Query) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		List<Integer> _LuceneIds;
			_LuceneIds = getLuceneIdsFromSearch(_Query, _DirectoryReader);
		return this.addInStoreFiles(_LuceneIds, _DirectoryReader);
	}

	public InStoreFile removeInStoreFile(Integer _LuceneId)
	{
		InStoreFile _InStoreFile = this._InStoreFiles.remove(_LuceneId);
		if (_InStoreFile != null)
		{
			this.onUpdate().notifyListeners("RemoveInStoreFile", _InStoreFile);
		}
		return _InStoreFile;
	}

	public void removeAllInStoreFiles()
	{
		this._InStoreFiles.clear();
		this.onUpdate().notifyListeners("RemoveAllInStoreFiles", null);
	}

	public InStoreFile getInStoreFile(Integer _LuceneId)
	{
		return this._InStoreFiles.get(_LuceneId);
	}
	
	public int getInStoreFilesCount() throws IOException
	{
		return this.getDirectoryReader().numDocs();
	}

	private DirectoryReader getDirectoryReader() throws IOException
	{
		DirectoryReader _DirectoryReader = DirectoryReader.openIfChanged(this._DirectoryReader);
		if (_DirectoryReader != null)
		{
			this._DirectoryReader = _DirectoryReader;
		}
		return this._DirectoryReader;
	}

	public void indexFile(FileInfo _FileInfo, MovieInfo _MovieInfo) throws IOException
	{
		Document _Document = new Document();
		{
			String _Name = _FileInfo.getName();
			if (_Name == null)
				throw new IOException("File:Name");
			_Document.add(new TextField("File:Name", _Name, Field.Store.YES));
		}
		{
			String _Directory = _FileInfo.getDirectory();
			if (_Directory == null)
				throw new IOException("File:Directory");
			_Document.add(new StoredField("File:Directory", _Directory));
		}
		{
			String _Path = _FileInfo.getPath();
			if (_Directory == null)
				throw new IOException("File:Path");
			_Document.add(new StringField("File:Path", _Path, Field.Store.YES)); //indexed but not tokenized/analyzed --> "as is" value
		}
		{
			Long _Size = _FileInfo.getSize();
			if (_Size == null)
				throw new IOException("File:Size");
			_Document.add(new StoredField("File:Size", _Size));
		}
		{
			String _Title = _MovieInfo.getTitle();
			if (_Title == null)
				throw new IOException("Movie:Title");
			_Document.add(new TextField("Movie:Title", _Title, Field.Store.YES));
		}
		{
			String _TitleDe = _MovieInfo.getTitleDe();
			if (_TitleDe == null)
				throw new IOException("Movie:Title:De");
			_Document.add(new TextField("Movie:Title:De", _TitleDe, Field.Store.YES));
		}
		{
			String _Year = _MovieInfo.getYear();
			if (_Year != null)
				_Document.add(new TextField("Movie:Year", _Year, Field.Store.YES));
		}
		{
			String _Plot = _MovieInfo.getPlot();
			if (_Plot != null)
				_Document.add(new TextField("Movie:Plot", _Plot, Field.Store.YES));
		}
		{
			List<String> _Genres = _MovieInfo.getGenres();
			if (_Genres != null)
				_Document.add(new TextField("Movie:Genres", Utils.join(_Genres, ", "), Field.Store.YES));
		}
		{
			List<String> _Directors = _MovieInfo.getDirectors();
			if (_Directors != null)
				_Document.add(new TextField("Movie:Directors", Utils.join(_Directors, ", "), Field.Store.YES));
		}
		{
			List<String> _Writers = _MovieInfo.getWriters();
			if (_Writers != null)
				_Document.add(new TextField("Movie:Writers", Utils.join(_Writers, ", "), Field.Store.YES));
		}
		{
			List<String> _Actors = _MovieInfo.getActors();
			if (_Actors != null)
				_Document.add(new TextField("Movie:Actors", Utils.join(_Actors, ", "), Field.Store.YES));
		}
		{
			String _ImdbId = _MovieInfo.getImdbId();
			if (_ImdbId == null)
				throw new IOException("Movie:ImdbId");
			_Document.add(new StoredField("Movie:ImdbId", _ImdbId));
			//_Document.add(new StringField("Movie:ImdbId", _ImdbId, Field.Store.YES));

		}
		{
			Double _ImdbRating = _MovieInfo.getImdbRating();
			if (_ImdbRating != null)
				_Document.add(new StoredField("Movie:ImdbRating", _ImdbRating));
		}
		{
			String _PosterSource = _MovieInfo.getPosterSource();
			if (_PosterSource != null)
				_Document.add(new StoredField("Movie:PosterSource", _PosterSource));
		}
		{
			Double _Duration = _MovieInfo.getDuration();
			if (_Duration != null)
				_Document.add(new StoredField("Movie:Duration", _Duration));
		}
		this._IndexWriter.addDocument(_Document);
		this._IndexWriter.commit();
	}

	private static InStoreFile createInStoreFile(Integer _LuceneId, DirectoryReader _DirectoryReader) throws IOException
	{
		Document _Document = _DirectoryReader.document(_LuceneId);
		FileInfo _FileInfo;
		{
			String _Name;
			{
				_Name = _Document.get("File:Name");
				if (_Name == null)
					throw new IOException("File:Name");
			}
			String _Directory;
			{
				_Directory = _Document.get("File:Directory");
				if (_Directory == null)
					throw new IOException("File:Directory");
			}
			Long _Size;
			{
				try
				{
					_Size = Long.valueOf(_Document.get("File:Size"));
				}
				catch (Exception e)
				{
					throw new IOException("File:Size");
				}
			}
			_FileInfo = new FileInfo(new File(_Directory, _Name).getPath(), _Size);
		}
		MovieInfo _MovieInfo;
		{
			String _Title;
			{
				_Title = _Document.get("Movie:Title");
				if (_Title == null)
					throw new IOException("Movie:Title");
			}
			String _TitleDe;
			{
				_TitleDe = _Document.get("Movie:Title:De");
				if (_TitleDe == null)
					throw new IOException("Movie:Title:De");
			}
			String _Year;
			{
				try
				{
					_Year = _Document.get("Movie:Year");
				}
				catch (Exception e)
				{
					_Year = null;
				}
			}
			String _Plot = _Document.get("Movie:Plot");
			List<String> _Genres;
			{
				try
				{
					_Genres = Utils.split(_Document.get("Movie:Genres"), ", ");
				}
				catch (Exception e)
				{
					_Genres = null;
				}
			}
			List<String> _Directors;
			{
				try
				{
					_Directors = Utils.split(_Document.get("Movie:Directors"), ", ");
				}
				catch (Exception e)
				{
					_Directors = null;
				}
			}
			List<String> _Writers;
			{
				try
				{
					_Writers = Utils.split(_Document.get("Movie:Writers"), ", ");
				}
				catch (Exception e)
				{
					_Writers = null;
				}
			}
			List<String> _Actors;
			{
				try
				{
					_Actors = Utils.split(_Document.get("Movie:Actors"), ", ");
				}
				catch (Exception e)
				{
					_Actors = null;
				}
			}
			String _IMDbID = _Document.get("Movie:ImdbId");
			Double _IMDbRating;
			{
				try
				{
					_IMDbRating = Double.valueOf(_Document.get("Movie:ImdbRating"));
				}
				catch (Exception e)
				{
					_IMDbRating = null;
				}
			}
			Double _Duration;
			{
				try
				{
					_Duration = Double.valueOf(_Document.get("Movie:Duration"));
				}
				catch (Exception e)
				{
					_Duration = null;
				}
			}
			String _PosterSource = _Document.get("Movie:PosterSource");
			_MovieInfo = new MovieInfo(_Title, _TitleDe, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource, _Duration);
		}
		return new InStoreFile(_LuceneId, _FileInfo, _MovieInfo);
	}

	private static List<Integer> getLuceneIdsFromSearch(String _Query, DirectoryReader _DirectoryReader) throws IOException
	{
		List<Integer> _LuceneIds = new ArrayList<Integer>();
		if (true)
		{
			Analyzer _Analyzer = new StandardAnalyzer(Version.LUCENE_46);
			IndexSearcher _IndexSearcher = new IndexSearcher(_DirectoryReader);
			Similarity _Similarity = new BM25Similarity();
			_IndexSearcher.setSimilarity(_Similarity);
			String[] _Fields = {"Movie:Title", "Movie:Title:De", "Movie:Year", "Movie:Plot", "Movie:Genres", "Movie:Directors", "Movie:Writers", "Movie:Actors"};
			Map<String, Float> _Boosts = new HashMap<String, Float>();
			_Boosts.put("Movie:Title", (float) 7.6);
			_Boosts.put("Movie:Title:De", (float) 7.6);
			_Boosts.put("Movie:Year", (float) 4.2);
			_Boosts.put("Movie:Plot", (float) 0.8);
			_Boosts.put("Movie:Genres", (float) 2.3);
			_Boosts.put("Movie:Directors", (float) 4.1);
			_Boosts.put("Movie:Writers", (float) 1.7);
			_Boosts.put("Movie:Actors", (float) 3.8);
			MultiFieldQueryParser _Parser = new MultiFieldQueryParser(Version.LUCENE_46, _Fields, _Analyzer, _Boosts);
			try
			{
				ScoreDoc[] _ScoreDocs;
				if (_Query.length()==0)
				{
					MatchAllDocsQuery m = new MatchAllDocsQuery();
					_ScoreDocs = _IndexSearcher.search(m, null, Integer.MAX_VALUE).scoreDocs;
				}
				else 
					_ScoreDocs = _IndexSearcher.search(_Parser.parse(_Query), null, 100).scoreDocs;
				for (int i = 0; i < _ScoreDocs.length; i++)
				{
					_LuceneIds.add(_ScoreDocs[i].doc);
				}
			}
			catch (ParseException e)
			{
				throw new IOException(e.getMessage());
			}
		}
		return _LuceneIds;
	}
	
	public boolean filePathInStore(String _FilePath) throws IOException 
	{
		Term indexTerm = new Term("File:Path", _FilePath);
		int docs = this.getDirectoryReader().docFreq(indexTerm);
		if (docs >0)
			return true;
		else
			return false;
	}
}


