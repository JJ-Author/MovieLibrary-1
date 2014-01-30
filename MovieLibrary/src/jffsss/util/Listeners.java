package jffsss.util;

import java.util.ArrayList;
import java.util.Collection;

public class Listeners
{
	private Object _Source;
	private Collection<Listener> _Listeners;

	public Listeners(Object _Source)
	{
		this._Source = _Source;
		this._Listeners = new ArrayList<Listener>();
	}

	public synchronized void addListener(Listener _Listener)
	{
		this._Listeners.add(_Listener);
	}

	public synchronized void removeListener(Listener _Listener)
	{
		this._Listeners.remove(_Listener);
	}

	public synchronized void clearListeners()
	{
		this._Listeners.clear();
	}

	public synchronized int getListenersCount()
	{
		return this._Listeners.size();
	}

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