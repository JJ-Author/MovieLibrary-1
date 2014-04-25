package jffsss.movlib;

import java.io.Closeable;
import java.io.File;
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
import org.apache.lucene.document.IntField;
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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jffsss.util.Listeners;
import jffsss.util.Utils;

import jffsss.api.OpenSubtitlesHasher;

/**
 * InStoreFilesCollectionbeinhaltet eine Menge von InStoreFile-Objekte und stellt die Methoden zum Export und zur Manipulation von diesen
 * Objekten aus dem Lucene-Index bereit.
 */
public class InStoreFilesCollection implements Closeable
{
	private Directory _Directory;
	private IndexWriter _IndexWriter;
	private DirectoryReader _DirectoryReader;
	private Map<Object, InStoreFile> _InStoreFiles;

	/**
	 * Konstruiert ein InStoreFilesCollection-Objekt.
	 * 
	 * @param _Directory
	 *            das Verzeichnis f�r den Lucene-Index
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
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

	/**
	 * Schlie�t alle Streams.
	 * 
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public void close() throws IOException
	{
		this._IndexWriter.close();
		this._DirectoryReader.close();
		this._Directory.close();
	}

	private Listeners onUpdate = null;

	/**
	 * Gibt die Listener zur�ck.
	 * 
	 * @return die Listener
	 */
	public Listeners onUpdate()
	{
		if (this.onUpdate == null)
		{
			this.onUpdate = new Listeners(this);
		}
		return this.onUpdate;
	}

	/**
	 * Exportiert alle indexierten Filme aus dem Lucene-Index und speichert diese im JSON-Format in die gegebenen Datei.
	 * 
	 * @param _File
	 *            die Datei, wohin die Filme gespeicht werden
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
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
	 * 
	 * @param _File
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public void writeExhibitJsonp(File _File) throws IOException
	{
		/*
		 * BufferedWriter bw = new BufferedWriter(new FileWriter(_File)); Gson gson = new Gson();
		 * gson.toJson(this.getExhibitJson(), new JsonWriter(bw));
		 */

		String _json = new Gson().toJson(this.getExhibitJson());
		String _jsonp = "callback(" + _json + ");"; //convert json to jsonp for local use of exhibit
		PrintWriter _PrintWriter = new PrintWriter(_File, "UTF-8");
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
	 * 
	 * @return
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public JsonObject getExhibitJson() throws IOException
	{
		JsonObject _top = new JsonObject();
		JsonObject _properties = new JsonObject();
		JsonObject _prop;
		_prop = new JsonObject();
		_prop.addProperty("valueType", "number");
		_properties.add("MovieDuration", _prop);
		_prop = new JsonObject();
		_prop.addProperty("valueType", "number");
		_properties.add("MovieImdbRating", _prop);
		_prop = new JsonObject();
		_prop.addProperty("valueType", "number");
		_properties.add("MovieRating", _prop);
		_prop = new JsonObject();
		_prop.addProperty("valueType", "url");
		_properties.add("MoviePosterSource", _prop);

		_top.add("properties", _properties);
		_top.add("items", this.getMoviesAsJson(true));
		return _top;
	}

	/**
	 * returns all movies in index with attributes as JsonArray
	 * 
	 * @param _exhibitMode
	 *            if true the property name for German title is label and filepath id(needed for exhibit json format)
	 * @return
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public JsonArray getMoviesAsJson(boolean _exhibitMode) throws IOException
	{
		JsonArray _JsonArray = new JsonArray();
		{
			for (int i = 0; i < this.getInStoreFilesCount(); i++)
			{
				JsonObject _JsonArrayObject = new JsonObject();
				InStoreFile _InStoreFile = createInStoreFile(i, this.getDirectoryReader());
				if (_exhibitMode) _JsonArrayObject.addProperty("id", _InStoreFile.getFileInfo().getPath());
				_JsonArrayObject.addProperty("FilePath", _InStoreFile.getFileInfo().getPath());
				_JsonArrayObject.addProperty("MovieTitle", _InStoreFile.getMovieInfo().getTitle());
				String titleKeyName = (_exhibitMode) ? "label" : "MovieTitleDE";
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
				_JsonArrayObject.addProperty("MovieRating", _InStoreFile.getMovieInfo().getRating());
				_JsonArrayObject.addProperty("MoviePosterSource", _InStoreFile.getMovieInfo().getPosterSource());
				_JsonArrayObject.addProperty("FileSize", _InStoreFile.getFileInfo().getSize() / (1024*1024));
				_JsonArray.add(_JsonArrayObject);
			}
		}
		return _JsonArray;
	}

	/**
	 * Erstellt ein neues InStoreFile-Objekt, falls keins mit dieser Lucene-ID bereits existierte und f�gt es in die
	 * Liste ein.
	 * 
	 * @param _LuceneId
	 *            die Lucene-ID als Schl�ssel
	 * @return neu erstelltes oder bereits vorhandenes InStoreFile-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public InStoreFile addInStoreFile(Integer _LuceneId) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		return this.addInStoreFile(_LuceneId, _DirectoryReader);
	}

	/**
	 * Erstellt ein neues InStoreFile-Objekt, falls keins mit dieser Lucene-ID bereits existierte und f�gt es in die
	 * Liste ein.
	 * 
	 * @param _LuceneId
	 *            die Lucene-ID als Schl�ssel
	 * @param _DirectoryReader
	 *            die Quelle des Lucene-Indexes
	 * @return neu erstelltes oder bereits vorhandenes InStoreFile-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
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

	/**
	 * Erstellt neue InStoreFile-Objekte, falls keine mit diesen Lucene-IDs bereits existierten und f�gt sie in die
	 * Liste ein.
	 * 
	 * @param _LuceneIds
	 *            die Lucene-IDs als Schl�ssel
	 * @return neu erstellte oder bereits vorhandene InStoreFile-Objekte
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public List<InStoreFile> addInStoreFiles(List<Integer> _LuceneIds) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		return this.addInStoreFiles(_LuceneIds, _DirectoryReader);
	}

	/**
	 * Erstellt neue InStoreFile-Objekte, falls keine mit diesen Lucene-IDs bereits existierten und f�gt sie in die
	 * Liste ein.
	 * 
	 * @param _LuceneIds
	 *            die Lucene-IDs als Schl�ssel
	 * @param _DirectoryReader
	 *            die Quelle des Lucene-Indexes
	 * @return neu erstellte oder bereits vorhandene InStoreFile-Objekte
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	private List<InStoreFile> addInStoreFiles(List<Integer> _LuceneIds, DirectoryReader _DirectoryReader) throws IOException
	{
		List<InStoreFile> _InStoreFiles = new ArrayList<InStoreFile>();
		for (Integer _LuceneId : _LuceneIds)
		{
			_InStoreFiles.add(this.addInStoreFile(_LuceneId, _DirectoryReader));
		}
		return _InStoreFiles;
	}

	/**
	 * Stellt eine Abfrage an den Lucene-Index, f�gt die gefundenen InStoreFile-Objekte in die Liste und gibt diese
	 * InStoreFile-Objekte zur�ck.
	 * 
	 * @param _Query
	 *            die Abfrage
	 * @return die gefundenen InStoreFile-Objekte
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public List<InStoreFile> addInStoreFilesFromSearch(String _Query) throws IOException
	{
		DirectoryReader _DirectoryReader = this.getDirectoryReader();
		List<Integer> _LuceneIds = getLuceneIdsFromSearch(_Query, _DirectoryReader);
		return this.addInStoreFiles(_LuceneIds, _DirectoryReader);
	}

	/**
	 * Entfernt das InStoreFile-Objekt zur gegebenen Lucene-ID aus der Liste.
	 * 
	 * @param _LuceneId
	 *            die Lucene-ID als Schl�ssel
	 * @return das ToStoreFile-Objekt oder <CODE>null</CODE> falls kein Eintrag zur gegebenen Lucene-ID existiert
	 */
	public InStoreFile removeInStoreFile(Integer _LuceneId)
	{
		InStoreFile _InStoreFile = this._InStoreFiles.remove(_LuceneId);
		if (_InStoreFile != null)
		{
			this.onUpdate().notifyListeners("RemoveInStoreFile", _InStoreFile);
		}
		return _InStoreFile;
	}

	/**
	 * Entfernt alle InStoreFile-Objekte aus der Liste.
	 */
	public void removeAllInStoreFiles()
	{
		this._InStoreFiles.clear();
		this.onUpdate().notifyListeners("RemoveAllInStoreFiles", null);
	}

	/**
	 * Gibt das InStoreFile-Objekt zur gegebenen Lucene-ID aus der Liste zur�ck.
	 * 
	 * @param _LuceneId
	 *            die Lucene-ID als Schl�ssel
	 * @return das InStoreFile-Objekt oder <CODE>null</CODE> falls kein Eintrag zur gegebenen Lucene-ID existiert
	 */
	public InStoreFile getInStoreFile(Integer _LuceneId)
	{
		return this._InStoreFiles.get(_LuceneId);
	}

	/**
	 * Gibt die Gesamtanzahl der indexierten Filme zur�ck.
	 * 
	 * @return die Gesamtanzahl der indexierten Filme
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public int getInStoreFilesCount() throws IOException
	{
		return this.getDirectoryReader().numDocs();
	}

	/**
	 * Gibt das aktuelle DirectoryReader-Objekt zur�ck.
	 * 
	 * @return das aktuelle DirectoryReader-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	private DirectoryReader getDirectoryReader() throws IOException
	{
		DirectoryReader _DirectoryReader = DirectoryReader.openIfChanged(this._DirectoryReader);
		if (_DirectoryReader != null)
		{
			this._DirectoryReader = _DirectoryReader;
		}
		return this._DirectoryReader;
	}
	
	/**
	 * Erstellt ein Document f�r Lucene aus den Datei-Informationen und den Film-Informationen.
	 * 
	 * @param _FileInfo
	 *            die Datei-Informationen
	 * @param _MovieInfo
	 *            die Film-Informationen
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public Document createDocument(FileInfo _FileInfo, MovieInfo _MovieInfo) throws IOException
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
			String _OSHash = null;
			try
			{
				_OSHash = OpenSubtitlesHasher.computeHash(new File(_FileInfo.getPath()));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			if(_OSHash == null)
				throw new IOException("File:OSHash");
			_Document.add(new StringField("File:OSHash", _OSHash, Field.Store.YES));
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
				_Document.add(new TextField("Movie:Title:De", "##n/a##", Field.Store.YES));
			else
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
		return _Document;
	}
	
	/**
	 * Indexiert die Datei-Informationen und die Film-Informationen in Lucene.
	 * 
	 * @param _FileInfo
	 *            die Datei-Informationen
	 * @param _MovieInfo
	 *            die Film-Informationen
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public void indexFile(FileInfo _FileInfo, MovieInfo _MovieInfo) throws IOException
	{
		Document _Document = createDocument(_FileInfo, _MovieInfo);
		_Document.add(new IntField("Movie:Rating", 0, Field.Store.YES));
		this._IndexWriter.addDocument(_Document);
		this._IndexWriter.commit();
	}

	/**
	 * Erstellt ein neues InStoreFile-Objekt, dessen Informationen aus dem Lucene-Index geladen werden.
	 * 
	 * @param _LuceneId
	 *            die Lucene-ID als Schl�ssel
	 * @param _DirectoryReader
	 *            das Verzeichnis f�r den Lucene-Index
	 * @return ein neues InStoreFile-Objekt
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
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
			Integer _Rating;
			{
				try
				{
					_Rating = Integer.valueOf(_Document.get("Movie:Rating"));
				}
				catch (Exception e)
				{
					_Rating = null;
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
			_MovieInfo = new MovieInfo(_Title, _TitleDe, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource, _Duration,_Rating);
		}
		return new InStoreFile(_LuceneId, _FileInfo, _MovieInfo);
	}

	/**
	 * Stellt eine Abfrage an den Lucene-Index und gibt die gefundenen Lucene-IDs zur�ck.
	 * 
	 * @param _Query
	 *            die Abfrage
	 * @param _DirectoryReader
	 *            das Verzeichnis f�r den Lucene-Index
	 * @return die gefundenen Lucene-IDs
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	private static List<Integer> getLuceneIdsFromSearch(String _Query, DirectoryReader _DirectoryReader) throws IOException
	{
		List<Integer> _LuceneIds = new ArrayList<Integer>();
		if (true)
		{
			Analyzer _Analyzer = new StandardAnalyzer(Version.LUCENE_46);
			IndexSearcher _IndexSearcher = new IndexSearcher(_DirectoryReader);
			Similarity _Similarity = new BM25Similarity();
			_IndexSearcher.setSimilarity(_Similarity);
			String[] _Fields = {"Movie:Title", "Movie:Title:De", "Movie:Year", "Movie:Plot", "Movie:Genres", "Movie:Directors", "Movie:Writers", "Movie:Actors", "Movie:Rating"};
			Map<String, Float> _Boosts = new HashMap<String, Float>();
			_Boosts.put("Movie:Title", (float) 7.6);
			_Boosts.put("Movie:Title:De", (float) 7.6);
			_Boosts.put("Movie:Year", (float) 4.2);
			_Boosts.put("Movie:Plot", (float) 0.8);
			_Boosts.put("Movie:Genres", (float) 2.3);
			_Boosts.put("Movie:Directors", (float) 4.1);
			_Boosts.put("Movie:Writers", (float) 1.7);
			_Boosts.put("Movie:Actors", (float) 3.8);
			_Boosts.put("Movie:Rating", (float) 1.0);
			MultiFieldQueryParser _Parser = new MultiFieldQueryParser(Version.LUCENE_46, _Fields, _Analyzer, _Boosts);
			try
			{
				ScoreDoc[] _ScoreDocs;
				if (_Query.length() == 0)
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

	/**
	 * Pr�ft, ob ein Dateipfad in Lucene vorhanden ist.
	 * @param _FilePath Dateipfad
	 * @return True wenn Dateipfad gefunden, sonst false.
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public boolean filePathInStore(String _FilePath) throws IOException
	{
		Term indexTerm = new Term("File:Path", _FilePath);
		int docs = this.getDirectoryReader().docFreq(indexTerm);
		if (docs > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Pr�ft, ob ein Open Subtitles Hash in Lucene vorhanden ist.
	 * @param _OSHash Hash
	 * @return True wenn Hash gefunden, sonst false.
	 * @throws IOException
	 *             falls ein IO-Fehler auftrat
	 */
	public boolean osHashInStore(String _OSHash) throws IOException
	{
		int docs = this.getDirectoryReader().docFreq(new Term("File:OSHash", _OSHash));
		if (docs > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Aktualisiert die Dateiinformationen eines Films der bereits in Lucene ist.
	 * @param _FilePath	Pfad zur Filmdatei
	 * @throws IOException	falls ein IO-Fehler auftrat
	 */
	public void updateFileInformation(String _FilePath) throws IOException
	{
		//suche lucene id f�r die datei mit dem hash von _FilePath
		IndexSearcher searcher = new IndexSearcher(this.getDirectoryReader());
		TermQuery query = new TermQuery(new Term("File:OSHash", OpenSubtitlesHasher.computeHash(new File(_FilePath))));
		TopDocs topdocs = searcher.search(query, 1);
		
		if (topdocs.totalHits > 0)
		{
			Document _Document = this._DirectoryReader.document(topdocs.scoreDocs[0].doc);
			if(!_Document.get("File:Path").equals(_FilePath)) //wenn sich der pfad ge�ndert hat, aktualisiere lucene eintrag
			{
				FileInfo _FileInfo = FileInfo.getFromFile(_FilePath);
								
				{
					String _Name = _FileInfo.getName();
					_Document.removeField("File:Name");
					if (_Name == null)
						throw new IOException("File:Name");
					_Document.add(new TextField("File:Name", _Name, Field.Store.YES));
				}
				{
					String _Directory = _FileInfo.getDirectory();
					_Document.removeField("File:Directory");
					if (_Directory == null)
						throw new IOException("File:Directory");
					_Document.add(new StoredField("File:Directory", _Directory));
				}
				{
					String _Path = _FileInfo.getPath();
					_Document.removeField("File:Path");
					if (_Directory == null)
						throw new IOException("File:Path");
					_Document.add(new StringField("File:Path", _Path, Field.Store.YES)); //indexed but not tokenized/analyzed --> "as is" value
				}
				{
					Long _Size = _FileInfo.getSize();
					_Document.removeField("File:Size");
					if (_Size == null)
						throw new IOException("File:Size");
					_Document.add(new StoredField("File:Size", _Size));
				}
				
				this._IndexWriter.updateDocument(new Term("File:OSHash", OpenSubtitlesHasher.computeHash(new File(_FilePath))), _Document);
				this._IndexWriter.commit();				
			}
			
		}
	}

	/**
	 * �ndert das MovieLibraryRating f�r einen Film
	 * @param _newRating	Neue Bewertung
	 * @param _LuceneId		Id des Films dessen Bewertung ge�ndert wird
	 */
	public void updateMovieLibraryRating(int _NewRating, InStoreFile _InStoreFile)
	{
		try
		{
			Document _Document = createDocument(_InStoreFile.getFileInfo(), _InStoreFile.getMovieInfo());
			_Document.add(new IntField("Movie:Rating", _NewRating, Field.Store.YES));
			this._IndexWriter.updateDocument(new Term("File:OSHash", _InStoreFile.getFileInfo().getOSHash()), _Document);
			this._IndexWriter.commit();
		}
		catch(IOException ex)
		{	
			System.out.println("Error updating the Movie Library Rating");
			ex.printStackTrace();
		} 
	}
	
	/**
	 * Liest die Movie Library Rating aus Lucene aus.
	 * @param _LuceneId	Id des Films
	 * @return Movie Library Rating
	 */
	public int readMovieLibraryRating(Integer _LuceneId)
	{
		try
		{
			Document _Document = this._DirectoryReader.document(_LuceneId);
			return Integer.parseInt(_Document.get("Movie:Rating"));
		}
		catch(IOException ex)
		{
			System.out.println("Error reading the Movie Library Rating");
			ex.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * L�scht einen Eintrag in Lucene.
	 * @param _InStoreFile Eintrag der gel�scht wird.
	 */
	public void deleteInStoreFile(InStoreFile _InStoreFile)
	{
		try
		{
			this.removeInStoreFile(_InStoreFile.getLuceneId());
			this._IndexWriter.deleteDocuments(new Term("File:Path", _InStoreFile.getFileInfo().getPath()));
			this._IndexWriter.commit();
		}
		catch(IOException ex)
		{	
			System.out.println("Error deleting the Lucene Entry");
			ex.printStackTrace();
		} 
	}
	
}
