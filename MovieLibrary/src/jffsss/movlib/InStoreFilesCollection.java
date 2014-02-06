package jffsss.movlib;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

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
		List<Integer> _LuceneIds = getLuceneIdsFromSearch(_Query, _DirectoryReader);
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

	private DirectoryReader getDirectoryReader() throws IOException
	{
		DirectoryReader _DirectoryReader = DirectoryReader.openIfChanged(this._DirectoryReader);
		if (_DirectoryReader != null)
			this._DirectoryReader = _DirectoryReader;
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
			String _PosterSource = _Document.get("Movie:PosterSource");
			_MovieInfo = new MovieInfo(_Title, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource);
		}
		return new InStoreFile(_LuceneId, _FileInfo, _MovieInfo);
	}

	private static List<Integer> getLuceneIdsFromSearch(String _Query, DirectoryReader _DirectoryReader) throws IOException
	{
		List<Integer> _LuceneIds = new ArrayList<Integer>();
		if (!_Query.isEmpty())
		{
			Analyzer _Analyzer = new StandardAnalyzer(Version.LUCENE_46);
			IndexSearcher _IndexSearcher = new IndexSearcher(_DirectoryReader);
			Similarity _Similarity = new BM25Similarity();
			_IndexSearcher.setSimilarity(_Similarity);
			String[] _Fields = {"Movie:Title", "Movie:Year", "Movie:Plot", "Movie:Genres", "Movie:Directors", "Movie:Writers", "Movie:Actors"};
			Map<String, Float> _Boosts = new HashMap<String, Float>();
			_Boosts.put("Movie:Title", (float) 7.6);
			_Boosts.put("Movie:Year", (float) 4.2);
			_Boosts.put("Movie:Plot", (float) 0.8);
			_Boosts.put("Movie:Genres", (float) 2.3);
			_Boosts.put("Movie:Directors", (float) 4.1);
			_Boosts.put("Movie:Writers", (float) 1.7);
			_Boosts.put("Movie:Actors", (float) 3.8);
			MultiFieldQueryParser _Parser = new MultiFieldQueryParser(Version.LUCENE_46, _Fields, _Analyzer, _Boosts);
			try
			{
				ScoreDoc[] _ScoreDocs = _IndexSearcher.search(_Parser.parse(_Query), null, 100).scoreDocs;
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
}