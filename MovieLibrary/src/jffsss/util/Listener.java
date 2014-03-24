package jffsss.util;

/**
 * Interface für den Listener aus dem Observer-Designpattern.
 */
public interface Listener
{
	/**
	 * Wird aufgerufen, wenn im beobachteten Objekt eine Änderung auftrat.
	 * 
	 * @param _Source
	 *            das beobachtete Objekt, in dem eine Änderung auftrat
	 * @param _Command
	 *            die benannte Änderung, die aufgetreten ist
	 * @param _Arg
	 *            ein optionales Argument
	 */
	public void on(Object _Source, String _Command, Object _Arg);
}