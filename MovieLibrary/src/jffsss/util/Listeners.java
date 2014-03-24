package jffsss.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Eine thread-sichere Container-Klasse für die Listener.
 */
public class Listeners
{
	private Object _Source;
	private Collection<Listener> _Listeners;

	/**
	 * Konstruiert ein Listeners-Objekt mit der gegebenen Quelle, die als das beobachtete Objekt fungiert.
	 * 
	 * @param _Source
	 *            die Quelle
	 */
	public Listeners(Object _Source)
	{
		this._Source = _Source;
		this._Listeners = new ArrayList<Listener>();
	}

	/**
	 * Fügt einen neuen Listener hinzu.
	 * 
	 * @param _Listener
	 *            der Listener, der eingefügt werden soll
	 */
	public synchronized void addListener(Listener _Listener)
	{
		this._Listeners.add(_Listener);
	}

	/**
	 * Entfernt einen Listener.
	 * 
	 * @param _Listener
	 *            der Listener, der entfernt werden soll
	 */
	public synchronized void removeListener(Listener _Listener)
	{
		this._Listeners.remove(_Listener);
	}

	/**
	 * Entfernt alle Listener.
	 */
	public synchronized void clearListeners()
	{
		this._Listeners.clear();
	}

	/**
	 * Gibt die Anzahl der eingefügten Listener zurück.
	 * 
	 * @return die Anzahl der eingefügten Listener
	 */
	public synchronized int getListenersCount()
	{
		return this._Listeners.size();
	}

	/**
	 * Benachrichtigt alle eingefügten Listener über die Änderung des Quellobjekts.
	 * 
	 * @param _Command
	 *            die benannte Änderung, die aufgetreten ist
	 * @param _Arg
	 *            ein optionales Argument
	 */
	public void notifyListeners(String _Command, Object _Arg)
	{
		Collection<Listener> _Listeners;
		synchronized (this)
		{
			_Listeners = new ArrayList<Listener>(this._Listeners);
		}
		for (Listener _Listener : _Listeners)
			_Listener.on(this._Source, _Command, _Arg);
	}
}